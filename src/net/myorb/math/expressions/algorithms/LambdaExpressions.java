
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

import java.util.ArrayList;
import java.util.List;

/**
 * symbol processing for Lambda Expressions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LambdaExpressions <T> implements Environment.AccessAcceptance <T>
{

	
	/**
	 * use default array name lambda
	 */
	public LambdaExpressions ()
	{
		this.PREFIX = OperatorNomenclature.LAMBDA_FUNCTION_NAME_PREFIX;
	}

	/**
	 * @param name non-default name for the array of lambda declarations
	 */
	public LambdaExpressions (String name)
	{
		this.PREFIX = name + "#";
		this.identifier = name;
	}
	protected String identifier = "lambda";
	public final String PREFIX;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <T> environment)
	{
		this.environment = environment;
		this.valueManager = environment.getValueManager ();
		this.allocateLambdaList ();								// array symbol is posted at point environment is provided
	}
	protected ValueManager <T> valueManager;
	protected Environment <T> environment;


	/*
	 * this is the original coding for reference to the declaration method
	 * 
		//	lambda.processDeclaration
		//			(parameters, "(" + funcBody + ")");
		//		this must align properly with the operator precedence
		//		the parenthesis in the first version offset a 9 precedence on ->
		//		absent the parenthesis seems aligned with a 7 precedence, this may yet be shown in error
	 * 
	 */


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
			public String getName () { return identifier; }
			public String toString () { return lambdaList.toString (); }
			public SymbolType getSymbolType () { return SymbolType.IDENTIFIER; }
			public GenericValue getValue () { return lambdaList; }
			public void rename (String to) {}
		};
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

