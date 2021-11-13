
package net.myorb.math.expressions;

import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * decision points in processing dimensioned data types
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class DimensionedValueProcessing<T> extends BuiltInArrayFunctions<T>
{

	public DimensionedValueProcessing (Environment<T> environment)
	{
		super (environment);
	}

	/**
	 * addition operator processing
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return computed result
	 */
	public ValueManager.GenericValue dimensionedAdd
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (valueManager.isArray (left))
			return valueManager.newDimensionedValue (vectorAdd (valueManager.toArray (left), valueManager.toArray (right)));
		return valueManager.newMatrix (abstractions.getMatrixLibrary ().add (valueManager.toMatrix (left), valueManager.toMatrix (right)));
	}

	/**
	 * subtraction operator processing
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return computed result
	 */
	public ValueManager.GenericValue dimensionedSubtract
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (valueManager.isArray (left))
			return valueManager.newDimensionedValue (vectorAdd (valueManager.toArray (left), vectorNegate (right)));
		return valueManager.newMatrix (abstractions.getMatrixLibrary ().add (valueManager.toMatrix (left), valueManager.toMatrix (right)));
	}

	/**
	 * multiplication operator processing
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return computed result
	 */
	public ValueManager.GenericValue dimensionedMultiply
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (valueManager.isMatrix (left) || valueManager.isMatrix (right))
			return valueManager.newMatrix (abstractions.getMatrixLibrary ().multiply (left, right));
		return vectorMultiply (left, right);
	}

	/**
	 * exponentiation operator processing
	 * @param left left side of operator
	 * @param right right side of operator
	 * @return computed result
	 */
	public ValueManager.GenericValue dimensionedPow
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		if (!valueManager.isMatrix (left))
			throw new RuntimeException ("Invalid exponentiation operation");
		return valueManager.newMatrix (abstractions.getMatrixLibrary ().toThe
		(valueManager.toMatrix (left), valueManager.toInt (right, spaceManager)));
	}

}


