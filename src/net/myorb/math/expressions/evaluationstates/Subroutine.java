
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.*;
import net.myorb.math.expressions.*;
import net.myorb.math.expressions.symbols.*;

import net.myorb.math.expressions.tree.Gardener;
import net.myorb.math.expressions.tree.Expression;
import net.myorb.math.expressions.commands.CommandSequence;

import net.myorb.math.computational.integration.RealDomainIntegration;
import net.myorb.math.computational.Spline.Operations;

import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.abstractions.ErrorHandling;

import java.util.ArrayList;
import java.util.List;

/**
 * a recursive descent mechanism allowing execution of streams of tokens
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Subroutine<T>
	implements MultiDimensional.Function<T>, RealDomainIntegration<T>
{


	/**
	 * set the defining properties of the subroutine
	 * @param parameterNames the names of parameters to the subroutine
	 * @param functionTokens the stream of tokens that define the subroutine behavior
	 */
	public Subroutine
		(
			List<String> parameterNames,
			TokenParser.TokenSequence functionTokens
		)
	{
		this.parameterNames = new ParameterList ();
		if (parameterNames != null) this.parameterNames.addAll (parameterNames);
		this.valueManager = new ValueManager<T>();
		this.functionTokens = functionTokens;
		newExpressionTree (functionTokens);
	}
	protected ValueManager<T> valueManager;


	/**
	 * @return the tokens of the function
	 */
	public TokenParser.TokenSequence getFunctionTokens () { return functionTokens; }
	protected TokenParser.TokenSequence functionTokens;


	/**
	 * set the space management object
	 * @param spaceManager the space management object to use in execution
	 */
	public void setSpaceManager (ExpressionSpaceManager<T> spaceManager)
	{
		this.spaceManager = spaceManager;
	}
	public ExpressionSpaceManager<T> getExpressionSpaceManager () { return spaceManager; }
	public SpaceManager<T> getSpaceManager () { return spaceManager; }
	protected ExpressionSpaceManager<T> spaceManager;


	/*
	 * expression processing
	 */


	/**
	 * @param tokens the expression tokens to parse into tree
	 */
	void newExpressionTree (List<TokenParser.TokenDescriptor> tokens)
	{
		if (!useExpressionTree) return;
		this.gardener = new Gardener<T> ();
		try { gardener.completeLexicalAnalysis (tokens); }
		catch (Exception e) { throw new RuntimeException ("Error in expression", e); }
		//catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * @param gardener the gardener holding the restored tree
	 */
	public void useExpressionTree (Gardener<T> gardener)
	{
		this.gardener = gardener;
		this.useExpressionTree = true;
	}
	protected Gardener<T> gardener = null;


	/**
	 * Save Expression Tree as JSON file
	 * @param functionName name to use as file name
	 * @throws Exception for any errors
	 */
	public void saveExpressionTree (String functionName) throws Exception
	{
		if (gardener == null)
		{ throw new RuntimeException ("Function not enabled as expression tree implementation"); }
		if (!semanticallyComplete) { gardener.completeSemanticAnalysis (symbols, spaceManager); }
		gardener.profiledTransplant (functionName, parameterNames, symbols.getDescription (functionName));
	}


	/**
	 * enable expression tree construction and use
	 */
	public void allowExpressionTree ()
	{
		useExpressionTree = true;
		newExpressionTree (functionTokens);
	}
	protected boolean useExpressionTree = false; // allow use of tree


	/*
	 * symbol table access
	 */


	/**
	 * copy the parent symbol table to create a child scope
	 * @param symbols the parent symbol table to be copied
	 */
	public void setSymbolTable (SymbolMap symbols)
	{
		this.symbols = new SymbolMap ();
		this.symbols.setParent (symbols);

		if (usingTokenSymbols)
		{
			lookupTokenSymbols (symbols, true);
		} else this.symbols.putAll (symbols);
	}
	protected boolean usingTokenSymbols = true;
	protected SymbolMap symbols;


	/**
	 * manage only pertinent symbols used by function
	 * @param symbols the complete set of symbols
	 */
	public void updateSymbolTable (SymbolMap symbols)
	{ lookupTokenSymbols (symbols, false); }


	/**
	 * scan the token stream for symbols
	 * @param symbols the full set of symbols to copy from
	 * @param recurse TRUE = allow recursion
	 */
	public void lookupTokenSymbols (SymbolMap symbols, boolean recurse)
	{
		if (symbols == null) return;
		checkItems (functionTokens, symbols, recurse);
		lookupTokenSymbols (symbols.getParent (), recurse);
	}
	void checkItems (List<TokenParser.TokenDescriptor> tokens, SymbolMap symbols, boolean recurse)
	{
		for (TokenParser.TokenDescriptor t : tokens)
		{
			if (TokenParser.isRecognizable (t.getTokenType ()))
			{ checkItem (t.getTokenImage (), symbols, recurse); }
		}
	}
	void checkItem (String name, SymbolMap parent, boolean recurse)
	{
		SymbolMap.Named n;
		if (symbols.containsKey (name)) return;

		// look for used tokens to copy in symbol table
		if ((n = parent.lookup (name)) == null) return;
		symbols.add (n);

		if (recurse)
		{
			Subroutine<T> s = cast (n);
			if (s != null) checkItems (s.functionTokens, parent, true);
			copyDerivatives (n, name, parent, recurse);
		}
	}
	void copyDerivatives (SymbolMap.Named n, String name, SymbolMap parent, boolean recurse)
	{
		checkItem (name + "'", parent, recurse); checkItem (name + "''", parent, recurse);
		// optimize cache for posted transform derivatives
	}


	/**
	 * cast Object to Subroutine when appropriate
	 * @param from source Object for the cast
	 * @return Object cast to Subroutine
	 * @param <T> data type
	 */
	public static <T> Subroutine<T> cast (Object from)
	{
		@SuppressWarnings ("unchecked") Subroutine<T>
		s = SimpleUtilities.verifyClass (from, Subroutine.class);
		return s;
	}


	/*
	 * evaluation of function
	 */


	/**
	 * construct an evaluation engine using local symbol table
	 * @return newly constructed engine
	 */
	public EvaluationEngine<T> constructEngine ()
	{
		engine = new EvaluationEngine<T> (symbols, spaceManager, null);
		if (supressingErrorMessages) engine.supressErrorMessages ();
		return engine;
	}
	protected EvaluationEngine<T> engine;


	/**
	 * perform semantic completion on expression
	 * @throws Exception for any errors
	 */
	public void enableExpression () throws Exception
	{
		if (gardener != null && expression == null)
		{
			gardener.completeSemanticAnalysis (symbols, spaceManager);
			expression = gardener.getExpression ();
			semanticallyComplete = true;
		}
	}
	protected boolean semanticallyComplete = false;


	/**
	 * @return computed value of expression
	 */
	public ValueManager.GenericValue processExpression ()
	{
		try { enableExpression (); return gardener.reap (expression, symbols); }
		catch (Exception e) { e.printStackTrace (); return null; }
	}
	public Expression<T> getExpression () { return expression; }
	protected Expression<T> expression = null;


	/**
	 * @param splineFunctions the exported functionality from the spline generator
	 */
	public void attachSpline (Operations<T> splineFunctions)
	{
		if ((this.splineFunctions = splineFunctions) == null) return;
		if (gardener != null) this.gardener.attachSpline (splineFunctions);
	}


	/**
	 * identify connection to a spline
	 * @return TRUE when splineFunctions has been set
	 */
	public boolean
		hasAttachedSpline () { return splineFunctions != null; }
	protected Operations<T> splineFunctions = null;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealDomainIntegration#evalIntegralOver(double, double)
	 */
	public T evalIntegralOver (double lo, double hi)
	{
		return splineFunctions.evalIntegralOver (lo, hi);
	}


	/**
	 * execute the token stream
	 */
	public void run ()
	{
		if (useExpressionTree)
			constructEngine ().pushValueStack (processExpression ());
		else constructEngine ().processWithCatch (new CommandSequence (functionTokens));
	}


	/**
	 * suppress errors from display
	 */
	public void
	supressErrorMessages ()
	{ supressingErrorMessages = true; }
	protected boolean supressingErrorMessages = false;


	/**
	 * get top entry of value stack
	 * @return value at top of stack
	 */
	public ValueManager.GenericValue topOfStack ()
	{
		return engine.popValueStack ();
	}


	/*
	 * parameter processing
	 */


	/**
	 * encapsulation of parameter name list processing
	 */
	public static class ParameterList extends ParameterListDescription
	{
		private static final long serialVersionUID = -6524886514740960418L;
	}


	/**
	 * get the count of parameters found in the definition
	 * @return the length of the parameter names list
	 */
	public int parameterCount () { return parameterNames.size (); }


	/**
	 * singleton parameter list generator
	 * @param name the name(s) of the parameter(s)
	 * @return the list of one name
	 */
	public static List<String> listOfNames (String... name)
	{
		List<String> list = new ArrayList<String> ();
		for (String n : name) list.add (n.trim ());
		return list;
	}


	/**
	 * get a list of the parameter names
	 * @return the list of names
	 */
	public ParameterList getParameterNames () { return parameterNames; }
	public void addToParameterNames (String name) { parameterNames.add (name); }
	public ParameterList getParameterNameList () { return parameterNames; }
	protected ParameterList parameterNames;


	/**
	 * set the named item
	 *  in the symbol table to specified value
	 * @param name the name of the item
	 * @param value the new value
	 */
	public void setParameter (String name, ValueManager.GenericValue value)
	{
		symbols.add (new AssignedVariableStorage (name, value));
	}


	/**
	 * for single parameter functions
	 * @param value the new value
	 */
	public void setParameterValue (ValueManager.GenericValue value)
	{
		setParameter (parameterNames.getSingletonParameterName (), value);
	}


	/**
	 * copy values of parameters into symbol table
	 * @param parameters the values popped from the expression stack (as dimensioned list)
	 */
	public void copyParameters (List<T> parameters)
	{
		for (int n = 0; n < parameters.size (); n++)
		{
			if (n == parameterNames.size ()) break;

			ValueManager.DiscreteValue<T> value =
				valueManager.newDiscreteValue (parameters.get (n));
			setParameter (parameterNames.get (n), value);
		}
	}


	/**
	 * copy values of parameters into symbol table
	 * @param parameters the values popped from the expression stack (as value list)
	 */
	public void copyParameters (ValueManager.ValueList parameters)
	{
		int n = 0;
		for (ValueManager.GenericValue p : parameters.getValues ())
		{
			if (n == parameterNames.size ()) break;
			setParameter (parameterNames.get (n++), p);
		}
	}


	/*
	 * implementation of subroutine as MultiDimensional.Function<T>
	 */


	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceDescription () { return spaceManager; }


	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#f(java.util.List)
	 */
	public T f (List<T> parameterValues)
	{
		try
		{
			copyParameters (parameterValues); run ();
			ValueManager.GenericValue tos = topOfStack ();
			return valueManager.toDiscrete (tos);
		}
		catch (Exception e)
		{
			if (supressingErrorMessages) return null;
			else throw new ErrorHandling.Terminator (e.getMessage (), e);
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#f(T[])
	 */
	@SafeVarargs
	public final T f (T... x)
	{
		List<T> list = new ArrayList<T> ();
		for (T t : x) list.add (t);
		return f (list);
	}


	/**
	 * @return Function object evaluated by subroutine
	 */
	public Function<T> toSimpleFunction ()
	{
		if (parameterNames.size () != 1)
		{
			throw new RuntimeException ("Simple function must have excatly 1 parameter");
		}

		if (splineFunctions != null) return splineFunctions;

		return new Function<T>()
		{
			public SpaceManager<T> getSpaceManager () { return spaceManager; }
			public SpaceManager<T> getSpaceDescription () { return spaceManager; }
			public T eval (T x) { return f (x); }
		};
	}


	/*
	 * formal profile specification formatting
	 */


	/**
	 * format subroutine profile
	 * @param functionName name of the function
	 * @param parameters names of the parameters
	 * @param withNotations translate symbols
	 * @return the profile text
	 */
	public static String formatFullProfile (String functionName, List<String> parameters, boolean withNotations)
	{
		StringBuffer buffer = new StringBuffer ();
		buffer.append (withNotations? ConventionalNotations.determineNotationFor (functionName): functionName);
		buffer.append (new ParameterListDescription (parameters).formatProfile (withNotations));
		return buffer.toString ();
	}

	public static String prettyFormatFullProfile
		(String functionName, List<String> parameters)
	{ return formatFullProfile (functionName, parameters, true); }
	public static String formatFullFormalProfile (String functionName, List<String> parameters)
	{ return formatFullProfile (functionName, parameters, false); }

	public String formatFullProfile (String functionName, boolean withNotations)
	{ return formatFullProfile (functionName, parameterNames, withNotations); }


	/**
	 * parse comment text to tokens
	 * @param text the text of the comment
	 * @return the token sequence parsed
	 */
	public static TokenParser.TokenSequence tokensFor (String text)
	{
		return TokenParser.parse (new StringBuffer (text));
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return TokenParser.toString (functionTokens); }
	public String toFormatted (boolean pretty) { return TokenParser.toFormatted (functionTokens, pretty); }
	public String toPrettyText () { return TokenParser.toPrettyText (functionTokens); }


	/**
	 * parse description to establish function display tokens
	 * @param description the text of the description
	 */
	public void setDescription (String description)
	{
		functionTokens = TokenParser.parse (new StringBuffer (description));
	}


}


