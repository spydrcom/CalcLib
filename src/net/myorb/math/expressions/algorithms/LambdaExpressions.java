
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


	public void setEnvironment (Environment<T> environment)
	{
		this.environment = environment;
		this.valueManager = environment.getValueManager ();
	}
	ValueManager<T> valueManager;
	Environment<T> environment;


	/**
	 * @param parameters
	 * @param funcBody
	 * @return
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

		ValueManager.Executable<T> proParm =
			valueManager.newProcedureParameter (defnition);
		ValueManager.IndirectAccess access =
			valueManager.newPointer (proParm);
		proParm.setName (name);
		return access;

//		ValueManager.GenericValue
//			value = python (parameters, funcBody);
//		return value;
	}

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
	 * @param parameters the parameter profile of the symbol
	 * @param funcBody the token that make the body of the function symbol
	 * @return a generic value holding the text of the display
	 */
	public ValueManager.GenericValue python (String parameters, String funcBody)
	{
		String declare = "lambda " + parameters + " : " + funcBody;
		ValueManager.GenericValue value = valueManager.newCapturedValue (declare);
		//System.out.println (declare);
		return value;
	}


}
