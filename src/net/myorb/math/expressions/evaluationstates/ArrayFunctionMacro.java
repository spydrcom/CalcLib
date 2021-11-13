
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.*;

import java.util.List;

/**
 * a macro form that uses functions based on arrays to evaluate transforms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ArrayFunctionMacro<T> extends ExpressionMacro<T>
{


	/**
	 * data required to do array function evaluation
	 * @param y the list of values of the array that make the curve sequence
	 * @param descriptor the array descriptor that provides function metadata
	 * @param arrayFunction an array function object that provides evaluation
	 * @param valueManager a manager for the value type
	 */
	public ArrayFunctionMacro
		(
			List<T> y,
			Arrays.Descriptor<T> descriptor,
			ArrayFunction<T> arrayFunction,
			ValueManager<T> valueManager
		)
	{
		super (listOfNames ("x"), null);
		this.arrayFunction = arrayFunction;
		this.valueManager = valueManager;
		this.descriptor = descriptor;
		this.y = y;
	}
	Arrays.Descriptor<T> descriptor;
	ArrayFunction<T> arrayFunction;
	ValueManager<T> valueManager;
	List<T> y;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.ExpressionMacro#evaluate(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public ValueManager.GenericValue evaluate (ValueManager.GenericValue parameter)
	{
		//setParameterValue (parameter);
		T x = valueManager.toDiscrete (parameter);
		return arrayFunction.arrayFunctionEval (x, y, descriptor);
	}


}
