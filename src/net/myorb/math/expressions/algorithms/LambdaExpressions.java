
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.symbols.DefinedFunction;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.expressions.TokenParser;

import net.myorb.math.expressions.ValueManager;

import java.util.List;

/**
 * symbol processing for Lambda Expressions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LambdaExpressions<T> implements Environment.AccessAcceptance<T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment<T> environment)
	{
		this.environment = environment;
		this.valueManager = environment.getValueManager ();
	}
	protected ValueManager<T> valueManager;
	protected Environment<T> environment;


	/**
	 * process a lambda function definition operator
	 * @param parameters the parameter text captured in the operator parse
	 * @param funcBody the tokens of the function body captured in the operator parse
	 * @return a generic value the holds a procedure parameter reference
	 */
	public ValueManager.GenericValue processDeclaration 
		(String parameters, String funcBody)
	{
		String name;
		DefinedFunction<T> defnition =
			DefinedFunction.defineUserFunction
			(
				name = nextName (),
				parameterNamesFrom (parameters),
				functionTokensFrom (funcBody),
				environment.getSpaceManager (),
				environment.getSymbolMap ()
			);
		environment.processDefinedFunction (defnition);

		LambdaMetadata metadata =
			new LambdaMetadata (parameters, funcBody);
		ValueManager.Executable<T> proParm =
			valueManager.newProcedureParameter (defnition);
		ValueManager.IndirectAccess access =
			valueManager.newPointer (proParm);
		proParm.setMetadata (metadata);
		proParm.setName (name);

		return access;
	}


	/*
	 * parse the required structures for User Function definition
	 */

	public TokenParser.TokenSequence functionTokensFrom (String source)
	{
		return TokenParser.parse (new StringBuffer (source));
	}

	public static List<String> parameterNamesFrom (String parameters)
	{
		return Subroutine.listOfNames (parameters.split (","));
	}

	public static String nextName ()
	{
		int thisIndex = nextLambdaIndex++;
		return "lambda#" + thisIndex;
	}
	static int nextLambdaIndex = 0;


	/**
	 * format a display using Python syntax
	 * @param metadata the metadata block for a lambda procedure parameter
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

