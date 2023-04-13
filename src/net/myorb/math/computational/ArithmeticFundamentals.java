
package net.myorb.math.computational;

import net.myorb.math.computational.ArithmeticFundamentals.Scalar;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.data.abstractions.SpaceConversion;

/**
 * implementation of scalar arithmetic within the algebra of polynomial series
 * @author Michael Druckman
 */
public class ArithmeticFundamentals
{

	public interface ScalarFactory <T>
	{
		ArithmeticScalar <T> newValue (T value);
	}

	public interface Scalar extends Comparable <Scalar>
	{

		Double toDouble ();

		void set (Scalar newValue);

		boolean EQ (Scalar with);

		boolean isPositive ();
		boolean isNegative ();

		Scalar plus (Scalar addend);
		Scalar times (Scalar factor);
		Scalar pow (int exponent);
		Scalar negated ();

	}

	public interface Conversions <T>
		extends SpaceConversion <Scalar>, java.util.Comparator <Scalar>
	{
		Scalar toScalar (T value);
		T convertedFrom (Scalar value);

		Scalar fromInt (Integer D);
		Scalar fromDouble (Double D);
		Scalar fromText (String V);

		Scalar getOne ();
		Scalar getNegOne ();
		Scalar getZero ();
	}
	public static <T> Conversions <T>
		getConverter ( ExpressionSpaceManager <T> manager ) { return new Converter <> (manager); }
	public static <T> Conversions <T> getConverter ( ExpressionSpaceManager <T> manager, ScalarFactory <T> factory )
	{ return new Converter <> (manager, factory); }

	public static void timesEquals (Scalar value, Scalar operand)
	{
		value.set (value.times (operand));
	}

	public static void plusEquals (Scalar value, Scalar operand)
	{
		value.set (value.plus (operand));
	}

}

/**
 * functionality implemented needed for conversions and scalars
 * @param <T> data type used in Arithmetic operations
 */
abstract class Core <T>
{
	Core (ExpressionSpaceManager <T> manager) { this (manager, null); }

	Core (ExpressionSpaceManager <T> manager, ArithmeticFundamentals.ScalarFactory <T> factory)
	{ this.manager = manager; this.useFactory (factory == null ? new DefaultFactory <> (manager) : factory); }

	@SuppressWarnings("unchecked") T getValue (ArithmeticFundamentals.Scalar S) { return ( (ArithmeticScalar <T>) S ).value; }
	ArithmeticScalar <T> newValue (T value) { return factory.newValue (value); }
	protected ExpressionSpaceManager <T> manager;

	public void useFactory (ArithmeticFundamentals.ScalarFactory <T> factory)
	{ this.factory = factory; this.ZERO = factory.newValue (manager.getZero ()); }
	protected ArithmeticFundamentals.ScalarFactory <T> factory;
	protected ArithmeticFundamentals.Scalar ZERO;
}

/**
 * default implementation of ScalarFactory
 * - this will establish no algorithm for object comparisons
 * - ArithmeticFundamentals.Scalar should be extended with comparisons implemented
 * - this would be the new target for a ScalarFactory implementation
 * @param <T> data type used in Arithmetic operations
 */
class DefaultFactory <T> implements ArithmeticFundamentals.ScalarFactory <T>
{
	DefaultFactory (ExpressionSpaceManager <T> manager) { this.manager = manager; }
	public ArithmeticScalar <T> newValue (T value) { return new ArithmeticScalar <T> (value, manager); }
	ExpressionSpaceManager <T> manager;
}

/**
 * implementation of Conversions interface
 * @param <T> data type used in Arithmetic operations
 */
class Converter <T> extends Core <T> implements ArithmeticFundamentals.Conversions <T>
{

	Converter (ExpressionSpaceManager <T> manager) { super (manager); }
	Converter (ExpressionSpaceManager <T> manager, ArithmeticFundamentals.ScalarFactory <T> factory) { super (manager, factory); }

	public ArithmeticFundamentals.Scalar toScalar (T value) { return newValue (value); }
	public T convertedFrom (ArithmeticFundamentals.Scalar value) { return getValue (value); }

	public ArithmeticFundamentals.Scalar fromInt (Integer I) { return newValue (manager.newScalar (I)); }
	public ArithmeticFundamentals.Scalar fromDouble (Double D) { return newValue (manager.convertFromDouble (D)); }
	public ArithmeticFundamentals.Scalar fromText (String expression) { return toScalar (manager.evaluate (expression)); }

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare (Scalar left, Scalar right) { return left.compareTo (right); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceConversion#convertFromDouble(java.lang.Double)
	 */
	public ArithmeticFundamentals.Scalar convertFromDouble (Double value) { return newValue (manager.convertFromDouble (value)); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceConversion#convertToDouble(java.lang.Object)
	 */
	public Double convertToDouble (ArithmeticFundamentals.Scalar value) { return manager.convertToDouble (getValue (value)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.complex.algebra.Arithmetic.Conversions#getOne()
	 */
	public ArithmeticFundamentals.Scalar getOne () { return fromInt (1); }
	public ArithmeticFundamentals.Scalar getNegOne () { return fromInt (-1); }
	public ArithmeticFundamentals.Scalar getZero () { return ZERO; }

}

