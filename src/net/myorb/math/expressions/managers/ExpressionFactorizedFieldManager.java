
package net.myorb.math.expressions.managers;

import net.myorb.math.fractions.Representation;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.primenumbers.*;

import net.myorb.data.abstractions.ValueDisplayProperties;

import java.util.List;

/**
 * field manager for expression evaluation using prime factorizations
 * @author Michael Druckman
 */
public class ExpressionFactorizedFieldManager extends FactorizationFieldManager
	implements ExpressionSpaceManager<Factorization>
{

	static final double MAX_VALUE = 1.0E15;

	public ExpressionFactorizedFieldManager ()
	{
		super ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#setEvaluationControl(net.myorb.math.expressions.EvaluationControlI)
	 */
	public void setEvaluationControl
	(EvaluationControlI<Factorization> control) { this.control = control; }
	public EvaluationControlI<Factorization> getEvaluationControl () { return control; }
	protected EvaluationControlI<Factorization> control;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#evaluate(java.lang.String)
	 */
	public Factorization evaluate (String expression) { return parseValueToken (TokenParser.TokenType.NUM, expression); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#parseValueToken(net.myorb.math.expressions.TokenParser.TokenType, java.lang.String)
	 */
	public Factorization parseValueToken (TokenParser.TokenType type, String image)
	{
		if (type == TokenParser.TokenType.INT)
			return bigScalar (Long.parseLong (image));
		return convertFromDouble (Double.parseDouble (image));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#convertFromDouble(java.lang.Double)
	 */
	public Factorization convertFromDouble (Double value)
	{
		Double numerator = value;
		if (value == 0) return null;
		long fixedValue = numerator.longValue ();
		Factorization multiplier = newScalar (1), TEN = newScalar (10);
		while (numerator != fixedValue && numerator < MAX_VALUE)
		{
			numerator *= 10; fixedValue = numerator.longValue ();
			multiplier = multiplier.multiplyBy (TEN);
		}
		return bigScalar (fixedValue).divideBy (multiplier);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#providesSupportFor(java.lang.String)
	 */
	public boolean providesSupportFor (String requiredType)
	{
		return ValueCharacterization.valueOf (requiredType.toUpperCase ()) == ValueCharacterization.INTEGER;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#convertToDouble(java.lang.Object)
	 */
	public Double convertToDouble (Factorization value)
	{
		return toNumber (value).doubleValue ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#characterize(java.lang.Object)
	 */
	public ValueCharacterization characterize (Factorization value)
	{
		switch (scalingOf (value).getClassification ())
		{
			case BIG_FLOAT: case DECIMAL: case DOUBLE: case ENGINEERING:
				return ValueCharacterization.REAL;
	
			case LONG: case BIG_INT: case ZERO: default:
				return ValueCharacterization.INTEGER;
		}
	}
//	public ValueCharacterization characterize (Factorization value)
//	{
//		if (isZero (value))
//			return ValueCharacterization.INTEGER;
//		Distribution distribution = Distribution.normalizeCopy (value, this);
//		long denominator = distribution.getDenominator ().reduce ().longValue ();
//		if (denominator == 1) return ValueCharacterization.INTEGER;
//		return ValueCharacterization.REAL;
//	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#convertToInteger(java.lang.Object)
	 */
	public Integer convertToInteger (Factorization value)
	{
		ValueCharacterization characterization = characterize (value);
		if (characterization != ValueCharacterization.INTEGER) return null;
		return toNumber (value).intValue ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#getDataConversions()
	 */
	public DataConversions<Factorization> getDataConversions ()
	{
		return new DataConversions<Factorization> (this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#convertToStructure(java.util.List, java.util.List, java.util.List)
	 */
	public void convertToStructure (List<Factorization> sequence, List<Double> x, List<Double> y)
	{ throw new RuntimeException ("No structure in this data"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.Formatter#format(java.lang.Object)
	 */
	public String format (Factorization value)
	{
		return formatToMode (value, getDefaultDisplayMode ());
	}

	/**
	 * names of options for formatting value displays
	 */
	public enum DisplayModes {Decimal, Ratio, Factorization, FactoredRatio, Mixed}

	/**
	 * @return current selected default mode
	 */
	public DisplayModes getDefaultDisplayMode ()
	{
		return DisplayModes.valueOf (ValueDisplayProperties.DEFAULT_DISPLAY_MODE);
	}

	/**
	 * @param mode the display mode in question
	 * @return TRUE => representation requires factors
	 */
	boolean requiresFactors (DisplayModes mode)
	{
		switch (mode)
		{
			case Ratio: return false;
			case Decimal: return false;
			case Factorization: return true;
			case FactoredRatio: return true;
			case Mixed: return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#formatByMode(java.lang.Object, java.lang.String)
	 */
	public String formatToMode (Factorization value, String mode)
	{  return formatToMode (value, DisplayModes.valueOf (mode));  }
	public String formatToMode (Factorization value, DisplayModes mode)
	{
		Representation representation;
		if ((representation = toRepresentation (value)) == null) return "0";
		else if (representation.isTrivial () && !requiresFactors (mode))
		{
			return representation.toNumber (getSelectedPrecision ()).toString ();
		}

		switch (mode)
		{
			case Decimal: return toDecimalString (value);
			case Factorization: return toPrimeFactors (value);
			case FactoredRatio: return scalingOf (value).toFactoredRatio ();
			case Mixed: return toDecimalString (value) + " = " + toPrimeFactors (value);
			case Ratio: return scalingOf (value).toRatio ();
		}
		return null;
	}

	/**
	 * get full representation of value
	 * @param x the value to be analyzed
	 * @return the Representation for value
	 */
	public Representation toRepresentation (Factorization x) { return scalingOf (x).toRepresentation (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#getformattingModes()
	 */
	public String[] getformattingModes ()
	{
		int i = 0;
		DisplayModes[] modes =
			DisplayModes.values ();
		String[] names = new String[modes.length];
		for (DisplayModes mode : modes) names[i++] = mode.toString ();
		return names;
	}

}

