
package net.myorb.math.polynomial.algebra;


import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.computational.*;

/**
 * implementation of scalar arithmetic within the algebra of polynomial series
 * @author Michael Druckman
 */
public class Arithmetic extends ArithmeticFundamentals
{

	public interface LocalScalar extends Scalar
	{

		boolean isNotZero ();
		boolean isNotOne ();

	}

	public static <T> Conversions <T>
		getConverter ( ExpressionSpaceManager <T> manager )
	{
		return getConverter (manager, new LocalFactory <T> (manager));
	}

	public static void timesEquals (Scalar value, Scalar operand)
	{
		value.set (value.times (operand));
	}

	public static void plusEquals (Scalar value, Scalar operand)
	{
		value.set (value.plus (operand));
	}

	public static java.util.Comparator <Scalar>
	getComparator () {  return  (L, R)  ->  compare (L, R);  }
	public static int compare (Scalar left, Scalar right)
	{
		return left.toDouble ().compareTo (right.toDouble ());
	}

}

class LocalFactory <T> implements ArithmeticFundamentals.ScalarFactory <T>
{

	LocalFactory ( ExpressionSpaceManager <T> manager )
	{
		this.manager = manager;
	}

	public ArithmeticScalar <T> newValue (T value)
	{
		return new LocalScalarValue <T> (value, manager, this);
	}
	ExpressionSpaceManager <T> manager;
	
}

class LocalScalarValue <T> extends ArithmeticScalar <T> implements Arithmetic.LocalScalar
{

	LocalScalarValue (int value, ExpressionSpaceManager <T> manager, ArithmeticFundamentals.ScalarFactory <T> factory)
	{
		super (manager.newScalar (value), manager, factory);
	}

	LocalScalarValue (T value, ExpressionSpaceManager <T> manager, ArithmeticFundamentals.ScalarFactory <T> factory)
	{ super (value, manager, factory); }


	public boolean isNotZero () { return toDouble () != 0.0; }
	public boolean isNotOne () { return toDouble () != 1.0; }

}

