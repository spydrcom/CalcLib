
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.*;
import net.myorb.math.expressions.symbols.*;
import net.myorb.math.computational.LinearCoordinateChange;

import net.myorb.data.abstractions.Function;
import net.myorb.math.*;

import java.util.ArrayList;
import java.util.List;

/**
 * support for processing declarations in token streams
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class DeclarationSupport<T> extends CommonSupport<T>
{


	public static class TokenStream extends TokenParser.TokenSequence
	{
		public TokenStream () {}
		public TokenStream (List<TokenParser.TokenDescriptor> tokens) { this.addAll (tokens); }
		public static TokenStream parse (StringBuffer source) { return new TokenStream (TokenParser.parse (source)); }
		public TokenStream between (int lo, int hi) { return new TokenStream (subList (lo, hi)); }
		private static final long serialVersionUID = -2352481307290037011L;
	}


	/**
	 * decalaration processing as required by the environment
	 * @param environment the environment object driving the processing
	 */
	public DeclarationSupport (Environment<T> environment)
	{
		super (environment.getSpaceManager ());
		this.environment = environment;
	}
	protected Environment<T> environment;


	/**
	 * verify an identifier is found in the token stream
	 * @param token the token descriptor being checked
	 * @return the text of the identifier
	 */
	public String processName (TokenParser.TokenDescriptor token)
	{
		TokenParser.TokenType type = token.getTokenType ();
		if (type != TokenParser.TokenType.IDN) throw new RuntimeException ("Identifier expected");
		return token.getTokenImage ();
	}
	public String processFunctionDefinitionName (List<TokenParser.TokenDescriptor> tokens)
	{
		String name = processName (tokens.get (1)), next;
		if ((next = tokens.get (2).getTokenImage ()).startsWith ("'"))
		{
			name = name + next;
			tokens.remove (2);
		}
		return name;
	}

	/**
	 * verify an expected token is found in the token stream
	 * @param expected the text of the token expected in the stream
	 * @param token the descriptor of the token being checked
	 */
	public void expect (String expected, TokenParser.TokenDescriptor token)
	{
		TokenParser.TokenType type = token.getTokenType ();
		if (!token.isIdentifiedAs (expected) || type != TokenParser.TokenType.OPR)
		{ throw new RuntimeException ("Delimiter '" + expected + "' expected, '" + token.getTokenImage () + "' found"); }
	}


	/**
	 * parse a symbol list
	 *  from the token source
	 * @param tokens the source tokens
	 * @param symbols the list to compile
	 * @param starting starting point in the token list
	 * @return the ending position
	 */
	public int getSymbolList (List<TokenParser.TokenDescriptor> tokens, List<String> symbols, int starting)
	{
		int n = starting;
		TokenParser.TokenDescriptor token;
		expect (OperatorNomenclature.START_OF_GROUP_DELIMITER, tokens.get (n));
		
		while (true)
		{
			token = tokens.get (++n);
			if (token.getTokenType () != TokenParser.TokenType.IDN) break;
			symbols.add (token.getTokenImage ()); token = tokens.get (++n);
			if (!token.isIdentifiedAs (OperatorNomenclature.GROUP_CONTINUATION_DELIMITER)) break;
		}

		expect (OperatorNomenclature.END_OF_GROUP_DELIMITER, token);
		return n;
	}


	/**
	 * parse the parameter list from the tokens
	 * @param tokens the list of tokens in the definition
	 * @param parameterNames the list to compile
	 * @return the ending position
	 */
	public int getParameterList (List<TokenParser.TokenDescriptor> tokens, List<String> parameterNames)
	{
		int ending = getSymbolList (tokens, parameterNames, 2);
		expect (OperatorNomenclature.ASSIGNMENT_DELIMITER, tokens.get (++ending));
		return ending;
	}


	/**
	 * expect singleton parameter name
	 * @param parameterNames the list of parameter names in transform declaration
	 * @return the name found to be singleton in list
	 * @throws RuntimeException if not singleton
	 */
	public String getParameter (List<String> parameterNames) throws RuntimeException
	{
		if (parameterNames.size () != 1)
		{ throw new RuntimeException ("Single parameter function only"); }
		return parameterNames.get (0);
	}


	/**
	 * expect singleton from transform declaration
	 * @param tokens the list of tokens in transform declaration
	 * @return the text of the singleton token found
	 * @throws RuntimeException if not singleton
	 */
	public String getToken (List<TokenParser.TokenDescriptor> tokens) throws RuntimeException
	{
		if (tokens.size () != 1)
		{ throw new RuntimeException ("Single token for transform only"); }
		return tokens.get (0).getTokenImage ();
	}


	/**
	 * find transform in declared functions list
	 * @param functionName the name to use as identification
	 * @return the transform object found by name in symbol table
	 * @throws RuntimeException if transform not found
	 */
	@SuppressWarnings("unchecked")
	public Function<T> getFunction (String functionName) throws RuntimeException
	{
		SymbolMap.Named item =
			environment.lookup (functionName);
		if (item == null || !(item instanceof DefinedTransform))
		{ throw new RuntimeException ("Symbol does not represent a transform"); }
		return ((DefinedTransform<T>)item).getTransform ();
	}


	/**
	 * build a token list description of the transform being declared
	 * @param definition a text description that describes the transforms declaration
	 * @param definitionTokens the tokens to display in the function list
	 */
	public void describeDefinition
	(String definition, TokenParser.TokenSequence definitionTokens)
	{
		TokenParser.TokenSequence parsed =
			TokenParser.parse (new StringBuffer (definition));
		definitionTokens.addAll (parsed);
	}


	/**
	 * use transform tokens to calculate an array of coefficients
	 * @param transformTokens the tokens describing the body of the transform
	 * @return an array of coefficients to be used in building transform
	 * @throws RuntimeException for failure to find array
	 */
	@SuppressWarnings("rawtypes")
	public GeneratingFunctions.Coefficients<T> getCoefficients
		(TokenParser.TokenSequence transformTokens) throws RuntimeException
	{
		ValueStack vs = environment.getValueStack ();
		ValueManager<T> vm = environment.getValueManager ();
		environment.getControl ().run (transformTokens, true); reorder (vs, vm);
		ValueManager.GenericValue contents = environment.getValueStack ().pop ();
		if (!vm.isArray (contents)) throw new RuntimeException ("Expression does not represent an array");
		GeneratingFunctions.Coefficients<T> c = new GeneratingFunctions.Coefficients<T> ();
		c.addAll (vm.toArray (contents));
		return c;
	}


	/**
	 * reverse order of items in parameter list
	 * @param vs the stack object holding the values
	 * @param vm a value manager object
	 */
	@SuppressWarnings("rawtypes")
	public void reorder (ValueStack vs, ValueManager<T> vm)
	{
		if (!vs.isEmpty ())		// do nothing if stack empty
		{
			if (vm.isParameterList (vs.peek ()))	// check for parameter list
			{
				List<ValueManager.GenericValue>
				values = ((ValueManager.ValueList)vs.pop ()).getValues ();					// pop parameter list off stack
				for (int i = values.size () - 1; i >= 0; i--) vs.push (values.get (i)); 	// push objects in opposite order
			}
		}
	}


	/**
	 * process the coefficients of a series being declared
	 * @param parameterNames list of parameter names in transform declaration
	 * @param operator the operator that defines the behavior of the series being declared
	 * @param transformTokens the tokens describing the body of the transform
	 * @param definitionTokens the tokens to display in the function list
	 * @return the coefficients that determine the series
	 */
	public GeneratingFunctions.Coefficients<T> getSeriesCoefficients
		(
			List<String> parameterNames, String operator,
			TokenParser.TokenSequence transformTokens,
			TokenParser.TokenSequence definitionTokens
		)
	{
		String parameter = getParameter (parameterNames);
		GeneratingFunctions.Coefficients<T> c = getCoefficients (transformTokens);
		describeDefinition (c + " " + operator + " " + parameter, definitionTokens);
		return c;
	}


	/**
	 * parse a series of tokens seeking embedded values
	 * @param tokens the stream of tokens being parsed
	 * @return the list of values found
	 */
	public List<T> getListOfValues (List<TokenParser.TokenDescriptor> tokens)
	{
		List<T> values = new ArrayList<T>();
		ListSupport.ItemProcessor<Double> p = ListSupport.getTypedListValueProcessor
				(values, environment.getSpaceManager ());
		ListSupport.processValueList (tokens, p);
		return values;
	}


	/**
	 * set domain constraints on symbol
	 * @param symbol the symbol to be constrained
	 * @param bounds list of 2 values to be used as (lo, hi) bounds
	 */
	public void setDomainConstraintsFromNumericList (SymbolMap.Named symbol, List<T> bounds)
	{
		AbstractFunction<T> f = AbstractFunction.cast (symbol);
		f.setDomainConstraints (bounds.get (0), bounds.get (1), environment);
	}


	/**
	 * evaluate expressions to get bounds
	 * @param tokens the tokens of the constraint declaration
	 * @param symbol the symbol from the map to be constrained
	 * @throws RuntimeException for ill-formed constraint
	 */
	public void setDomainConstraintsFromNumericTokens
	(List<TokenParser.TokenDescriptor> tokens, SymbolMap.Named symbol) throws RuntimeException
	{
		List<T> listOfValues;
		if ((listOfValues = getListOfValues (tokens)).size() != 2)
			throw new RuntimeException ("Specify (lo, hi) constraints");
		setDomainConstraintsFromNumericList (symbol, listOfValues);
	}


	/**
	 * find a variable in the symbol map
	 * @param symbolName name to use for symbol map lookup
	 * @return the symbol as variable object found in the symbol map 
	 * @throws RuntimeException specified symbol does not refer to variable
	 */
	public SymbolMap.VariableLookup lookupVariable (String symbolName) throws RuntimeException
	{
		SymbolMap.Named symbol = environment.getSymbolMap ().lookup (symbolName);
		if (!(symbol instanceof SymbolMap.VariableLookup)) throw new RuntimeException ("Symbol is not a variable");
		return (SymbolMap.VariableLookup)symbol;
	}


	/**
	 * check expression for symbolic multiplier
	 * @param tokens the tokens of the constraint declaration
	 * @param symbol the symbol to be constrained
	 * @return TRUE = multiplier found
	 */
	public boolean constrainedUsingSymbolicMultiplier
	(TokenParser.TokenSequence tokens, SymbolMap.Named symbol)
	{
		int tokenCount = tokens.size ();
		if (!matchesPattern (tokens, tokenCount)) return false;
		SymbolMap.VariableLookup multiplier = getMultiplier (tokens.get (tokenCount - 1));

		if (multiplier != null)
		{
			List<T> bounds = getBounds (tokens.between (0, tokenCount - 2));
			setSymbolicDomainConstraints (symbol, bounds, multiplier);
			return true;
		}
		return false;
	}

	/**
	 * assign symbolic constraints to symbol
	 * @param symbol the symbol being constrained
	 * @param bounds the bounds values subject to scaling
	 * @param multiplier the scaling multiplier variable
	 */
	void setSymbolicDomainConstraints
	(SymbolMap.Named symbol, List<T> bounds, SymbolMap.VariableLookup multiplier)
	{
		T lo = bounds.get (0), hi = bounds.get (1);
		AbstractFunction<T> f = AbstractFunction.cast (symbol);
		f.setSymbolicDomainConstraints (lo, hi, multiplier, environment);
	}

	/**
	 * parse token list to verify pattern is present
	 * @param tokens the tokens that should show the pattern
	 * @param tokenCount the numbr of tokens in the list
	 * @return TRUE => pattern is present
	 */
	boolean matchesPattern (List<TokenParser.TokenDescriptor> tokens, int tokenCount)
	{
		if (!tokens.get (tokenCount - 3).getTokenImage ().equals (")")) return false;
		if (!tokens.get (tokenCount - 2).getTokenImage ().equals ("*")) return false;
		return true;		// seeking pattern:  (lo, hi) * multiplier
	}

	/**
	 * verify that a symbolic reference is present
	 *  and that it refers to a variable in the symbol table
	 * @param idToken the token that should be found to be a variable
	 * @return NULL => not found, otherwise symbol table record found
	 */
	SymbolMap.VariableLookup getMultiplier (TokenParser.TokenDescriptor idToken)
	{
		if (idToken.getTokenType () != TokenParser.TokenType.IDN) { return null; }			// multiplier must be symbol
		else return lookupVariable (idToken.getTokenImage ());								// symbol must be variable
	}


	/**
	 * parse tokens as expression returning array
	 * @param tokens the tokens of the constraint declaration
	 * @return (lo, hi) array of bounds as parsed by coefficients parser
	 * @throws RuntimeException for ill-formed constraint
	 */
	List<T> getBounds (TokenParser.TokenSequence tokens) throws RuntimeException
	{
		List<T> bounds;
		if ((bounds = getCoefficients (tokens)).size() != 2)								// use the coefficients parser to get values
		{ throw new RuntimeException ("Bounds must be 2 element array (lo, hi)"); }			// array found must be 2 elements in length
		return bounds;
	}


	/**
	 * evaluate expressions to get bounds
	 * @param tokens the tokens of the constraint declaration
	 * @param symbol the symbol to be constrained
	 */
	public void setDomainConstraintsFromExpression
	(TokenParser.TokenSequence tokens, SymbolMap.Named symbol)
	{
		if (!constrainedUsingSymbolicMultiplier (tokens, symbol))
		{ setDomainConstraintsFromNumericList (symbol, getBounds (tokens)); }
	}


	/**
	 * lookup next token as symbol, verify as function
	 * @param functionName name to use for symbol map lookup
	 * @return the symbol as AbstractFunction cast from symbol map record
	 * @throws RuntimeException for non-function symbol
	 */
	public AbstractFunction<T> lookupFunction (String functionName) throws RuntimeException
	{
		AbstractFunction<T> f = AbstractFunction.cast (environment.getSymbolMap ().lookup (functionName));
		if (f == null) throw new RuntimeException ("Symbol is not a function");
		return f;
	}


	/**
	 * parse token stream for function definition
	 * @param tokens the token stream being parsed
	 * @return an analysis object holding declaration
	 */
	public Analysis<T> analyzeFunction (List<TokenParser.TokenDescriptor> tokens)
	{
		AbstractFunction<T>
			symbol = lookupFunction (tokens.remove (0).getTokenImage ());
		FunctionAnalysis<T> analysis = new FunctionAnalysis<T>
			(tokens.remove (0).getTokenImage (), this);
		analysis.setSymbol (symbol);
		return analysis;
	}

	/**
	 * determine type (function or transform) connected to AbstractFunction
	 * @param analysis the analysis object built by analyzeFunction
	 * @return the function found in the abstract
	 * @throws RuntimeException for bad type
	 */
	@SuppressWarnings("unchecked")
	public Function<T> getFunction (Analysis<T> analysis) throws RuntimeException
	{
		Function<T> f = null;
		SymbolMap.Named symbol = ((FunctionAnalysis<T>)analysis).symbol;
		if (symbol instanceof DefinedTransform) f = ((DefinedTransform<T>)symbol).getTransform ();
		else if (symbol instanceof DefinedFunction) f = ((DefinedFunction<T>)symbol).toSimpleFunction ();
		else if (symbol instanceof ImportedFunction) f = ((ImportedFunction<T>)symbol).toSimpleFunction ();
		else throw new RuntimeException ("Symbol is not a recognized function type: " + symbol.getClass ().getCanonicalName ());
		return f;
	}

	/**
	 * gather data on function declatations
	 * @param <T> type on which operations are to be executed
	 */
	public interface Analysis<T>
	{
		String getFunctionSymbol ();
		AbstractFunction<T> getAbstractFunction ();
		AbstractFunction.DomainConstraints<T> getDomainConstraints ();
		AbstractFunction<T> defineTransform (Function<T> f, StringBuffer buffer);
		boolean isStandard ();
	}

	/**
	 * update function to specific domain constraints
	 * @param tokens the tokens from the command
	 */
	public void setDomainConstraints (TokenParser.TokenSequence tokens)
	{
		AbstractFunction<T> symbol = lookupFunction (tokens.remove (0).getTokenImage ());
		setDomainConstraintsFromExpression (tokens, symbol);
	}

	/**
	 * build transform of function constrained to [-1,1] domain
	 * @param tokens the tokens from the command
	 */
	public void standardizeDomainConstraints (List<TokenParser.TokenDescriptor> tokens)
	{
		T ONE = expressionManager.getOne ();
		Analysis<T> analysis = analyzeFunction (tokens);
		if (analysis.isStandard ()) throw new RuntimeException ("Symbol is already constrained to [-1,1]");
		StringBuffer definition = new StringBuffer ().append (analysis.getFunctionSymbol ());
		String symbol = tokens.size () > 0 ? tokens.remove (0).getTokenImage () : null;
		Function<T> adjusted = buildAdjustedFunction (analysis, symbol, definition);
		analysis.defineTransform (adjusted, definition).setDomainConstraints
		(expressionManager.negate (ONE), ONE, environment);
	}

	/**
	 * apply Linear Coordinate Change
	 * @param analysis the analysis of the function
	 * @param symbol a symbol to hold the domain transform
	 * @param definition text buffer collecting definition
	 * @return the adjusted function
	 */
	public Function<T> buildAdjustedFunction (Analysis<T> analysis, String symbol, StringBuffer definition)
	{
		// Linear Coordinate Change resulting in function with adjusted domain

		AbstractFunction.DomainConstraints<T>
			constraints = analysis.getDomainConstraints ();
		LinearCoordinateChange<T> lcc = new LinearCoordinateChange<T>
			(constraints.getLo (), constraints.getHi (), getFunction (analysis), expressionManager);
		definition.append ("(").append (lcc).append (")");

		// optional assignment of domain transform to symbol

		if (symbol != null)
		{
			environment.setSymbol
			(
				symbol,
				environment.getValueManager ().newCoefficientList
				(
					lcc.describeLine ().getCoefficients ()
				)
			);
		}

		return lcc.functionWithAdjustedDomain ();
	}

	/**
	 * lookup the symbol table entry for pi
	 * @return the variable lookup record for pi in the symbol map
	 */
	public SymbolMap.VariableLookup getPi ()
	{
		SymbolMap.Named symbol = environment.getSymbolMap ().lookup ("pi");
		return (SymbolMap.VariableLookup)symbol;
	}

}


/**
 * storage for analysis of functions found in declaration
 * @param <T> type on which operations are to be executed
 */
class FunctionAnalysis<T> implements DeclarationSupport.Analysis<T>
{

	public FunctionAnalysis (String newFunctionSymbol, DeclarationSupport<T> decls)
	{
		this.decls = decls;
		this.newFunctionSymbol = newFunctionSymbol;
		this.sm = decls.environment.getSpaceManager ();
		this.ONE = sm.getOne ();
	}
	protected DeclarationSupport<T> decls;
	protected ExpressionSpaceManager<T> sm;
	protected String newFunctionSymbol;
	protected T ONE;

	/**
	 * capture the referenced function symbol.
	 *  verify that the function has constraints posted for domain.
	 * @param symbol the symbol-table entry for the function symbol
	 * @throws RuntimeException for no constraints on function
	 */
	public void setSymbol (AbstractFunction<T> symbol) throws RuntimeException
	{
		this.c = ((this.symbol = symbol)).getDomainConstraints ();
		if (c == null) throw new RuntimeException ("Function has no published domain constraints");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.DeclarationSupport.Analysis#isStandard()
	 */
	public boolean isStandard ()
	{
		T lo = c.getLo(), hi = c.getHi();
		T lo1 = sm.add (lo, sm.newScalar(1)), hi1 = sm.add (hi, sm.newScalar(-1));
		return sm.isZero(lo1) && sm.isZero(hi1);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.DeclarationSupport.Analysis#getDomainConstraints()
	 */
	public AbstractFunction.DomainConstraints<T> getDomainConstraints () { return c; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.DeclarationSupport.Analysis#getFunctionSymbol()
	 */
	public String getFunctionSymbol () { return symbol.getName (); }
	AbstractFunction.DomainConstraints<T> c;
	AbstractFunction<T> symbol;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.DeclarationSupport.Analysis#defineTransform(net.myorb.math.Function, java.lang.StringBuffer)
	 */
	public AbstractFunction<T> defineTransform (Function<T> f, StringBuffer buffer)
	{
		DeclarationSupport.TokenStream
		functionTokens = DeclarationSupport.TokenStream.parse (buffer);
		abstraction = new FunctionDefinition<T> (decls.environment).defineTransform
			(newFunctionSymbol, X, functionTokens, f);
		return abstraction;
	}
	public static final List<String> X = Subroutine.listOfNames (new String[]{"x"});

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.DeclarationSupport.Analysis#getAbstractFunction()
	 */
	public AbstractFunction<T> getAbstractFunction () { return abstraction; }
	AbstractFunction<T> abstraction;

}

