
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.SymbolMap.SymbolType;
import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.TokenParser;

import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * symbol processing for Lambda Expressions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LambdaExpressions <T> implements Environment.AccessAcceptance <T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <T> environment)
	{
		this.environment = environment;
		this.valueManager = environment.getValueManager ();
		this.allocateLambdaList ();
	}
	protected ValueManager <T> valueManager;
	protected Environment <T> environment;


	/**
	 * process a lambda function definition operator
	 * @param parameters the parameter text captured in the operator parse
	 * @param funcBody the tokens of the function body captured in the operator parse
	 * @return a generic value the holds a procedure parameter reference
	 */
	public ValueManager.GenericValue processDeclaration 
		(String parameters, String funcBody)
	{
		DefinedFunction<T> definition =
			DefinedFunction.defineUserFunction
			(
				nextName (),
				parameterNamesFrom (parameters),
				functionTokensFrom (funcBody),
				environment.getSpaceManager (),
				environment.getSymbolMap ()
			);
		environment.processDefinedFunction (definition);
		functionList.add (definition);
		return getAccessToProc
		(
			getProcParm
			(
				definition,
				new LambdaMetadata
				(
					parameters,
					funcBody
				)
			)
		);
	}


	/**
	 * build a pointer to a procedure parameter
	 * @param proParm the executable describing a function
	 * @return indirect access to the procedure parameter
	 */
	public ValueManager.IndirectAccess getAccessToProc
	(ValueManager.Executable <T> proParm)
	{
		ValueManager.IndirectAccess
			accessToProc = valueManager.newPointer (proParm);
		addToArray (accessToProc);
		return accessToProc;
	}


	/**
	 * build a procedure parameter description
	 * @param definition a function definition record
	 * @param metadata a meta-data object for the procedure
	 * @return the procedure parameter description
	 */
	public ValueManager.Executable <T> getProcParm
	(DefinedFunction <T> definition, LambdaMetadata metadata)
	{
		ValueManager.Executable <T> procParm =
			valueManager.newProcedureParameter (definition);
		procParm.setName (definition.getName ());
		procParm.setMetadata (metadata);
		return procParm;
	}


	/*
	 * the lambda array posted to the symbol table
	 */


	/**
	 * maintain a list of lambda functions
	 * @param value a new lambda resulting from expression execution
	 */
	public void addToArray
	(ValueManager.GenericValue value) { lambdas.add (value); }
	protected ValueManager.GenericValueList lambdas = null;
	protected ValueManager.ValueList lambdaList = null;


	/**
	 * build and post the lambda array
	 */
	public void allocateLambdaList ()
	{
		if (lambdas != null) return;
		this.lambdas = new ValueManager.GenericValueList ();
		this.lambdaList = environment.getValueManager ().newValueList (lambdas);
		this.functionList = new ArrayList< DefinedFunction<T> > ();
		this.environment.getSymbolMap ().add (post ());
	}
	protected List< DefinedFunction <T> > functionList;


	/**
	 * post the lambda array
	 * @return a symbol table posting for the lambda array
	 */
	public SymbolMap.VariableLookup post ()
	{
		return new SymbolMap.VariableLookup ()
		{
			public String getName () { return "lambda"; }
			public String toString () { return lambdaList.toString (); }
			public SymbolType getSymbolType () { return SymbolType.IDENTIFIER; }
			public GenericValue getValue () { return lambdaList; }
			public void rename (String to) {}
		};
	}


	/*
	 * functions collected for PLOTL processing
	 */


	/**
	 * build a list of the lambda functions
	 *  that have the profile of unary operators
	 * @return list of indexes of functions matching the profile
	 */
	public List<Integer> unaryFunctionProfiles ()
	{
		List<Integer> functions = new ArrayList<Integer>();
		for (int i = 0; i < functionList.size (); i++)
		{
			DefinedFunction <T> f = functionList.get (i);
			if (f.parameterCount () == 1)						// functions with profiles
			{ functions.add (i); }								// matching unary operators
		}
		return functions;
	}


	/**
	 * get the lambda functions that will be in a multi-unary-plot
	 * @return a list of lambda UDFs that have unary function profiles
	 */
	public List < Function <T> > getSimpleFunctionList ()
	{
		List < Function <T> > f =
			new ArrayList < Function <T> > ();
		for (int id : unaryFunctionProfiles ())
		{ f.add (functionList.get (id).toSimpleFunction ()); }
		return f;
	}


	/*
	 * parse the required structures for User Function definition
	 */

	/**
	 * parse the tokens of the function body
	 * @param source a line of source from the command stream
	 * @return the token sequence from the source
	 */
	public static TokenParser.TokenSequence functionTokensFrom (String source)
	{ return TokenParser.parse (new StringBuffer (source)); }

	/**
	 * take the text of a formal parameter list and parse with convention
	 * @param parameters a formal parameter list
	 * @return the ListOfName representation
	 */
	public static List<String> parameterNamesFrom (String parameters)
	{ return Subroutine.listOfNames (parameters.split (",")); }

	/**
	 * lambda generated function names use indexing syntax
	 * - the convention is lambda # index allowing maintenance of the lambda array
	 * @return a generated name conforming to convention
	 */
	public String nextName () { return PREFIX + allocateNextIndex (); }
	public String allocateNextIndex () { return Integer.toString (nextLambdaIndex++); }
	public static final String PREFIX = OperatorNomenclature.LAMBDA_FUNCTION_NAME_PREFIX;
	protected int nextLambdaIndex = 0;


	/*
	 * display options for functions
	 */

	/**
	 * format a display using Python syntax
	 * @param metadata the meta-data block for a lambda procedure parameter
	 * @return a generic value holding the text of the display
	 */
	public ValueManager.GenericValue toPythonSyntax (LambdaMetadata metadata)
	{
		return valueManager.newCapturedValue (metadata.toString ()); 
	}


}


/**
 * retain the original captured text of the declaration
 */
class LambdaMetadata implements ValueManager.Metadata
{


	LambdaMetadata
	(String parameters, String funcBody)
	{
		this.parameters = parameters;
		this.funcBody = funcBody;
	}
	protected String parameters, funcBody;


	/**
	 * format a display using Python syntax
	 * @param parameters the parameter profile of the symbol
	 * @param funcBody the token that make the body of the function symbol
	 * @return the text of the display
	 */
	public static String getPythonSyntaxFor (String parameters, String funcBody)
	{
		return "lambda " + parameters + " : " + funcBody;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return getPythonSyntaxFor (parameters, funcBody);
	}


}

