
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.TokenParser;
import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * do conversions of float for plot functions
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class ExpressionMacro<T> extends Subroutine<T> implements Function<T>
{

	/**
	 * for singleton parameter
	 * @param parameterName name of parameter
	 * @param functionTokens tokens that comprise macro
	 */
	public ExpressionMacro
		(
			String parameterName,
			TokenParser.TokenSequence functionTokens
		)
	{
		super (listOfNames (parameterName), functionTokens);
	}

	/**
	 * for multi-parameter case
	 * @param parameterNames names of parameters
	 * @param functionTokens tokens that comprise macro
	 */
	public ExpressionMacro
		(
			List<String> parameterNames,
			TokenParser.TokenSequence functionTokens
		)
	{
		super (parameterNames, functionTokens);
	}


	/**
	 * evaluate unary function, single discrete parameter, and discrete result
	 * @param parameter value of the parameter
	 * @return value of the result
	 */
	public ValueManager.GenericValue evaluate (ValueManager.GenericValue parameter)
	{ setParameterValue (parameter); run (); return topOfStack (); }


	/**
	 * evaluate an expression based on a discrete parameter.
	 * @param parameter the generic value of the parameter
	 * @return the list of value object computed
	 */
	public ValueManager.GenericValue evaluate (T parameter)
	{ return evaluate (valueManager.newDiscreteValue (parameter)); }


	/**
	 * evaluate an expression based on a real parameter.
	 *  this is special case processing to be used for X/Y plots
	 * @param parameter the real value of the parameter
	 * @return the list of values computed
	 */
	public List<T> evaluate (double parameter)
	{
		T t = spaceManager.convertFromDouble (parameter);
		return valueManager.toDiscreteValues (evaluate (t));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#f(java.lang.Object)
	 */
	public T eval (T x)
	{
		try
		{
			ValueManager.GenericValue result = evaluate (x);
			return valueManager.toDiscrete (result);
		}
		catch (Exception e)
		{
			if (supressingErrorMessages) return null;
			else throw new RuntimeException (e);
		}
	}


}

