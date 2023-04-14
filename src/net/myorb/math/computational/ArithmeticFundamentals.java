
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

	public interface Scalar extends Comparable <Scalar>
	{

		Double toDouble ();

		void set (Scalar newValue);

		boolean isEqualTo (Double value);
		boolean isNot (Double value);
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
		ExpressionSpaceManager <T> getManager ();

		Scalar fromInt (Integer D);
		Scalar fromDouble (Double D);
		Scalar fromText (String V);

		Scalar getOne ();
		Scalar getNegOne ();
		Scalar getZero ();
	}
	public static <T> Conversions <T> getConverter
		( ExpressionSpaceManager <T> manager )
	{ return new Converter <> (manager); }

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

	Core (ArithmeticFundamentals.Conversions <T> conversions)
	{ this (conversions.getManager ()); this.conversions = conversions; }
	protected ArithmeticFundamentals.Conversions <T> conversions;

	Core (ExpressionSpaceManager <T> manager)
	{ this.manager = manager; this.ZERO = manager.getZero (); }
	public ExpressionSpaceManager <T> getManager () { return manager; }
	protected ExpressionSpaceManager <T> manager;
	protected T ZERO;

	@SuppressWarnings("unchecked")
	T getValue (ArithmeticFundamentals.Scalar S)
	{ return ( (ArithmeticScalar <T>) S ).value; }

	ArithmeticScalar <T> newValue (T value)
	{ return new ArithmeticScalar <> (value, conversions); }

}

/**
 * implementation of Conversions interface
 * @param <T> data type used in Arithmetic operations
 */
class Converter <T> extends Core <T> implements ArithmeticFundamentals.Conversions <T>
{

	Converter (ExpressionSpaceManager <T> manager) { super (manager); this.conversions = this; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Conversions#toScalar(java.lang.Object)
	 */
	public ArithmeticFundamentals.Scalar toScalar (T value) { return newValue (value); }
	public T convertedFrom (ArithmeticFundamentals.Scalar value) { return getValue (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Conversions#fromInt(java.lang.Integer)
	 */
	public ArithmeticFundamentals.Scalar fromInt (Integer I) { return newValue (manager.newScalar (I)); }
	public ArithmeticFundamentals.Scalar fromDouble (Double D) { return newValue (manager.convertFromDouble (D)); }
	public ArithmeticFundamentals.Scalar fromText (String expression) { return toScalar (manager.evaluate (expression)); }

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare (Scalar left, Scalar right)
	{
		if ( manager.lessThan ( getValue (left), getValue (right) ) ) return -1;
		else if ( getValue (left).equals ( getValue (right) ) ) return 0;
		else return 1;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceConversion#convertFromDouble(java.lang.Double)
	 */
	public ArithmeticFundamentals.Scalar convertFromDouble (Double value) { return newValue (manager.convertFromDouble (value)); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceConversion#convertToDouble(java.lang.Object)
	 */
	public Double convertToDouble (ArithmeticFundamentals.Scalar value) { return manager.convertToDouble (getValue (value)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Conversions#getZero()
	 */
	public ArithmeticFundamentals.Scalar getZero () { return toScalar (ZERO); }
	public ArithmeticFundamentals.Scalar getNegOne () { return fromInt (-1); }
	public ArithmeticFundamentals.Scalar getOne () { return fromInt (1); }

}

