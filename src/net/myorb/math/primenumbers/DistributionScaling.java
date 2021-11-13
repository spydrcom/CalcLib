
package net.myorb.math.primenumbers;

import net.myorb.math.fractions.Representation;
import net.myorb.math.SpaceManager;

import java.math.BigInteger;

/**
 * manage the sizes of fraction components
 * @author Michael Druckman
 */
public class DistributionScaling
{


	public static final int DEFAULT_DIGITS = 16;


	/**
	 * value is ZERO
	 */
	protected DistributionScaling () {}

	/**
	 * @param distribution container for numerator and denominator
	 * @param precision the precision to be applied to resulting values
	 */
	protected DistributionScaling (Distribution distribution, int precision)
	{ this (distribution.getNumerator (), distribution.getDenominator (), precision); this.distribution = distribution; }

	protected DistributionScaling (Factorization numerator, Factorization denominator, int precision)
	{ this (numerator.reduce (), denominator.reduce (), precision); }


	protected DistributionScaling
	(BigInteger numerator, BigInteger denominator, int precision)
	{
		this.numerator = numerator;
		this.denominator = denominator;
		this.numeratorScale = scaleOf (numerator);
		this.denominatorScale = scaleOf (denominator);
		this.valueScale = this.numeratorScale - this.denominatorScale;
		this.classification = classify ();
		this.precision = precision;
	}
	BigInteger numerator = null, denominator = null;
	Distribution distribution;


	public int getValueScale() { return valueScale; }
	public int getNumeratorScale() { return numeratorScale; }
	public int getDenominatorScale() { return denominatorScale; }
	int numeratorScale, denominatorScale, valueScale, precision;


	/**
	 * @return the classification of the value
	 */
	public Representation.Classification getClassification () { return classification; }
	Representation.Classification classification = Representation.Classification.ZERO;


	/**
	 * @param value an arbitrarily large integer value
	 * @return the count of digits in the value
	 */
	public static int scaleOf (BigInteger value)
	{
		return value.abs ().toString ().length ();
	}


	/**
	 * @param source the value treated as a prime factored fraction
	 * @param precision the precision to be passed to BigDecimal
	 * @param manager the type manager for the value
	 * @return the constructed scaling object
	 */
	public static DistributionScaling newInstance
	(Factorization source, int precision, SpaceManager<Factorization> manager)
	{
		if (source == null) return new DistributionScaling ();
		Distribution d = Distribution.normalizeCopy (source, manager);
		return new DistributionScaling (d, precision);
	}


	/**
	 * @return TRUE = value represents as an integer
	 */
	public boolean hasZeroMantissa () { return denominator.equals (BigInteger.ONE); }


	/**
	 * @return the classification for the value
	 */
	public Representation.Classification classify ()
	{
		if (numerator == null) return Representation.Classification.ZERO;
		else return Representation.getClassificationFor (valueScale, hasZeroMantissa ());
	}


	/**
	 * @return decimal representation of arbitrarily large value
	 */
	public Representation toRepresentation ()
	{
		if (classification == Representation.Classification.ZERO) return null;
		return new Representation (numerator, denominator, classification, valueScale);
	}


	/**
	 * @return a Number Object holding the value
	 */
	public Number toNumber ()
	{
		switch (classification)
		{
			case DOUBLE:
			case DECIMAL:
			case BIG_FLOAT:
			case ENGINEERING:	return toRepresentation ().toNumber (precision);
			case LONG:			return numerator.divide (denominator).longValue ();
			case BIG_INT:		return numerator.divide (denominator);
			case ZERO:			return 0;
		}
		return null;
	}


	/**
	 * @return a description of the value in internal format
	 */
	public String toInternalString ()
	{
		return toRatio () + " = " + toFactoredRatio ();
	}
	public String toRatio ()
	{
		return numerator.toString () + " / " + denominator.toString ();
	}
	public String toFactoredRatio ()
	{
		return distribution.toString ();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return toNumber ().toString ();
	}


}

