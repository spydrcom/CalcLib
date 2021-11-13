
package net.myorb.math.fractions;

import java.math.*;

/**
 * manage the representation of fractions of arbitrarily large integers
 * @author Michael Druckman
 */
public class Representation
{


	/**
	 * the type of the value that dictates the representation used
	 */
	public enum Classification { ZERO, LONG, BIG_INT, DECIMAL, ENGINEERING, DOUBLE, BIG_FLOAT }


	/**
	 * @param valueScale the number of digits in the resulting value
	 * @param isInteger TRUE = the value is known to have mantissa zero
	 * @return the classification for the value
	 */
	public static Classification getClassificationFor (int valueScale, boolean isInteger)
	{
		if (isInteger)
		{
			if (valueScale > LONG_MAX_SCALE)
				return Representation.Classification.BIG_INT;
			return Representation.Classification.LONG;
		}
		else if (valueScale < TOO_SMALL_FOR_ENG)
		{
			if (ALWAYS_USE_SCIENTIFIC || Math.abs (valueScale) > DOUBLE_MAX_EXPONENT)
				return Representation.Classification.BIG_FLOAT;
			return Representation.Classification.DOUBLE;
		}
		else if (valueScale < BIG_ENOUGH_FOR_DECIMAL)
		{
			return Representation.Classification.ENGINEERING;
		}
		else return Representation.Classification.DECIMAL;
	}

	public static final int LONG_MAX_SCALE = 15, DOUBLE_MAX_EXPONENT = Double.MAX_EXPONENT;
	public static final int TOO_SMALL_FOR_ENG = -8, BIG_ENOUGH_FOR_DECIMAL = -4;
	static boolean ALWAYS_USE_SCIENTIFIC = true;


	public Representation
		(
			BigInteger numerator,
			BigInteger denominator,
			Classification classification,
			int scaleOfValue
		)
	{
		this.numerator = new BigDecimal (numerator);
		this.denominator = new BigDecimal (denominator);
		this.scaleOfValue = scaleOfValue;
		this.classOf = classification;
	}
	BigDecimal numerator, denominator;
	Classification classOf;
	int scaleOfValue;


	/**
	 * @return numerator divided by denominator as a double float value
	 */
	public double computeDoubleQuotient ()
	{ return numerator.doubleValue () / denominator.doubleValue (); }


	/**
	 * @param precision the number of digits after the decimal point
	 * @param roundingMode the rounding mode value as defined by the BigDecimal class
	 * @return numerator divided by denominator as a big decimal value
	 */
	public BigDecimal computeDecimalQuotient (int precision, int roundingMode)
	{ return numerator.divide (denominator, precision, roundingMode); }


	/**
	 * @param precision the number of digits after the decimal point
	 * @return an implementation of the Abstract Number class that represents the value
	 */
	public Number toNumber (int precision)
	{
		switch (classOf)
		{
			case ZERO:			return 0;
			case BIG_INT:		return computeDecimalQuotient (precision, BigDecimal.ROUND_UNNECESSARY);
			case LONG:			return computeDecimalQuotient (precision, BigDecimal.ROUND_UNNECESSARY).longValue ();
			case ENGINEERING:	return new Engineering (computeDecimalQuotient (precision, BigDecimal.ROUND_UP));
			case DECIMAL:		return computeDecimalQuotient (precision, BigDecimal.ROUND_UP);
			case BIG_FLOAT:		return new Scientific (this, precision);
			case DOUBLE:		return computeDoubleQuotient ();
		}
		return null;
	}

	/**
	 * @return TRUE = trivial to represent
	 */
	public boolean isTrivial ()
	{
		switch (classOf)
		{
			case ZERO:			return true;
			case BIG_INT:		return true;
			case LONG:			return true;
			case ENGINEERING:	return false;
			case DECIMAL:		return false;
			case BIG_FLOAT:		return false;
			case DOUBLE:		return false;
		}
		return true;
	}

}


/**
 * a wrapper for a big decimal value that restricts display to Engineering mode
 */
class Engineering extends Number
{

	Engineering (BigDecimal value)
	{
		this.value = value;
	}
	BigDecimal value;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return value.toEngineeringString (); }

	public double doubleValue() { return value.doubleValue(); }
	public float floatValue() { return value.floatValue(); }
	public long longValue() { return value.longValue(); }
	public int intValue() { return value.intValue(); }

	private static final long serialVersionUID = 1L;
}


/**
 * an implementation of the Abstract Number class 
 * that represents the values too large or small for other containers
 */
class Scientific extends Number
{


	Scientific (Representation representation, int precision)
	{
		this.representation = representation;
		this.precision = precision;
	}
	Representation representation;
	int precision;


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return toScientificString (representation.numerator, representation.denominator, representation.scaleOfValue);
	}


	/**
	 * force scientific notation without rounding
	 * @param n the numerator of the value to display
	 * @param d the denominator of the value to display
	 * @return the formatted text
	 */
	public String toScientificString (BigDecimal n, BigDecimal d, int scale)
	{
		int exponent = scale;
		BigDecimal qot = n.multiply (BigDecimal.TEN.pow (-exponent)).divide (d, precision, BigDecimal.ROUND_UP);
		return ltOne (qot)? toNormalizedScientificString (qot, exponent): toScientificString (qot, exponent);
	}

	public String toScientificString (BigDecimal digits, int exponent)
	{ return (isOne (digits)? "1": digits.toString ()) + "E" + exponent; }

	public String toNormalizedScientificString (BigDecimal digits, int exponent)			// change from 0.Ddd to D.dd
	{ return toScientificString (digits.multiply (BigDecimal.TEN), exponent - 1); }

	public int ctOne (BigDecimal value)
	{ return value.compareTo (BigDecimal.ONE); }

	public boolean isOne (BigDecimal value) { return ctOne (value) == 0; }
	public boolean ltOne (BigDecimal value) { return ctOne (value) < 0; }


	/**
	 * by definition all attempts to reduce the contained value are erroneous
	 */
	void valueOutOfRange ()
	{ throw new RuntimeException ("Value Out Of Range"); }

	public double doubleValue () { valueOutOfRange (); return 0; }
	public float floatValue () { valueOutOfRange (); return 0; }
	public long longValue () { valueOutOfRange (); return 0; }
	public int intValue () { valueOutOfRange (); return 0; }


	private static final long serialVersionUID = 1L;
}

