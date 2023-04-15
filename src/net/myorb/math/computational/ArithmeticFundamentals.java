
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

	/**
	 * describe the atomic value
	 */
	public interface Scalar extends Comparable <Scalar>
	{

		/**
		 * @return value as integer
		 */
		int intValue ();

		/**
		 * @return value as Double
		 */
		Double toDouble ();

		/**
		 * @return value treated as Java Number
		 */
		Number toNumber ();

		/**
		 * update this scalar to value taken from another
		 * @param newValue value to copy
		 */
		void set (Scalar newValue);

		/**
		 * compare to a Double
		 * @param value a Double value
		 * @return TRUE when found equal
		 */
		boolean isEqualTo (Double value);

		/**
		 * compare to a Double
		 * @param value a Double value
		 * @return TRUE when found NOT equal
		 */
		boolean isNot (Double value);

		/**
		 * compare to another Scalar
		 * @param with another Scalar value
		 * @return TRUE when found equal
		 */
		boolean EQ (Scalar with);

		/**
		 * @return TRUE when greater than zero
		 */
		boolean isPositive ();

		/**
		 * @return TRUE when less than zero
		 */
		boolean isNegative ();

		/**
		 * sum of this with addend
		 * @param addend the value to use as offset
		 * @return sum of the two values
		 */
		Scalar plus (Scalar addend);

		/**
		 * product of this with factor
		 * @param factor the value to use as scalar
		 * @return product of the two values
		 */
		Scalar times (Scalar factor);

		/**
		 * exponentiation of this with integer
		 * @param exponent the value to use as exponent
		 * @return this raised to exponent
		 */
		Scalar pow (int exponent);

		/**
		 * negate this value
		 * @return negative value of this
		 */
		Scalar negated ();

	}

	/**
	 * provide conversion between generic and scalar
	 * @param <T> the data type
	 */
	public interface Conversions <T>
		extends SpaceConversion <Scalar>, java.util.Comparator <Scalar>
	{

		 /**
		 * convert to scalar
		 * @param value a generic value
		 * @return the equivalent scalar representation
		 */
		Scalar toScalar (T value);

		/**
		 * translate representation to Number
		 * - this will use the algorithm supplied in the type manager
		 * @param value a Scalar representation of a value
		 * @return a machine implemented type
		 */
		Number toNumber (Scalar value);

		/**
		 * convert to generic
		 * @param value scalar representation of the value
		 * @return the equivalent generic representation
		 */
		T convertedFrom (Scalar value);

		/**
		 * @return the manager for the data type used
		 */
		ExpressionSpaceManager <T> getManager ();

		/**
		 * @param I an integer value
		 * @return the equivalent scalar representation
		 */
		Scalar fromInt (Integer I);

		/**
		 * @param D an integer value
		 * @return the equivalent scalar representation
		 */
		Scalar fromDouble (Double D);

		/**
		 * evaluate an equation
		 *  and give the scalar representation of the value
		 * @param text the text of a symbolic equation
		 * @return the computed scalar representation
		 */
		Scalar fromText (String text);

		/**
		 * @return scalar representation of 1
		 */
		Scalar getOne ();

		/**
		 * @return scalar representation of -1
		 */
		Scalar getNegOne ();

		/**
		 * @return scalar representation of 0
		 */
		Scalar getZero ();
	}

	/**
	 * get a Converter based on Expression Space Manager for type
	 * @param manager Expression Space Manager for the data type
	 * @return a Converter for the specified type
	 * @param <T> the data type
	 */
	public static <T> Conversions <T> getConverter
		( ExpressionSpaceManager <T> manager )
	{ return new Converter <> (manager); }

	/**
	 * scale value with operand
	 * @param value the value being updated
	 * @param operand the scaling value to multiply
	 */
	public static void timesEquals (Scalar value, Scalar operand)
	{
		value.set (value.times (operand));
	}

	/**
	 * increment value with operand
	 * @param value the value being updated
	 * @param operand the offset value to add
	 */
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

	/**
	 * retrieve generic value from Scalar
	 * @param S the Scalar representation
	 * @return the generic value
	 */
	@SuppressWarnings("unchecked")
	T getValue (ArithmeticFundamentals.Scalar S)
	{ return ( (ArithmeticScalar <T>) S ).value; }

	/**
	 * wrap generic value as scalar
	 * @param value the generic value
	 * @return Scalar representation
	 */
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
		else if ( left.toDouble ().doubleValue () == right.toDouble ().doubleValue () ) return 0;
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
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Conversions#toNumber(net.myorb.math.computational.ArithmeticFundamentals.Scalar)
	 */
	public Number toNumber (ArithmeticFundamentals.Scalar value) { return manager.toNumber (getValue (value)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Conversions#getZero()
	 */
	public ArithmeticFundamentals.Scalar getZero () { return toScalar (ZERO); }
	public ArithmeticFundamentals.Scalar getNegOne () { return fromInt (-1); }
	public ArithmeticFundamentals.Scalar getOne () { return fromInt (1); }

}

