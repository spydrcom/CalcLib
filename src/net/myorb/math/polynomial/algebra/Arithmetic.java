
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.data.abstractions.SpaceConversion;

/**
 * implementation of scalar arithmetic within the algebra of polynomial series
 * @author Michael Druckman
 */
public class Arithmetic
{

	public interface Scalar
	{

		void set (Scalar newValue);

		Double toDouble ();

		Scalar plus (Scalar addend);
		Scalar times (Scalar factor);
		Scalar pow (Scalar exponent);
		Scalar negated ();

		boolean EQ (Scalar with);

		boolean isPositive ();
		boolean isNegative ();

		boolean isNotZero ();
		boolean isNotOne ();

	}

	public interface Conversions <T> extends SpaceConversion <Scalar>
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
		getConverter ( ExpressionSpaceManager <T> manager )
	{ return new Converter <> (manager); }

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

abstract class Core <T>
{
	Core (ExpressionSpaceManager <T> manager) { this.manager = manager; }
	ScalarValue <T> newValue (T value) { return new ScalarValue <T> (value, manager); }
	@SuppressWarnings("unchecked") T getValue (Arithmetic.Scalar S) { return ( (ScalarValue <T>) S ).value; }
	ExpressionSpaceManager <T> manager;
}

class Converter <T> extends Core <T> implements Arithmetic.Conversions <T>
{

	Converter (ExpressionSpaceManager <T> manager) { super (manager); }

	public Arithmetic.Scalar toScalar (T value) { return newValue (value); }
	public T convertedFrom (Arithmetic.Scalar value) { return getValue (value); }

	public Arithmetic.Scalar fromInt (Integer I) { return newValue (manager.newScalar (I)); }
	public Arithmetic.Scalar fromDouble (Double D) { return newValue (manager.convertFromDouble (D)); }
	public Arithmetic.Scalar fromText (String expression)
	{
		T E = manager.evaluate (expression);
		return toScalar (E);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceConversion#convertFromDouble(java.lang.Double)
	 */
	public Arithmetic.Scalar convertFromDouble (Double value) { return newValue (manager.convertFromDouble (value)); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceConversion#convertToDouble(java.lang.Object)
	 */
	public Double convertToDouble (Arithmetic.Scalar value) { return manager.convertToDouble (getValue (value)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.complex.algebra.Arithmetic.Conversions#getOne()
	 */
	public Arithmetic.Scalar getOne () { return fromInt (1); }
	public Arithmetic.Scalar getNegOne () { return fromInt (-1); }
	public Arithmetic.Scalar getZero () { return fromInt (0); }

}

class ScalarValue <T> extends Core <T> implements Arithmetic.Scalar
{

	ScalarValue (int value, ExpressionSpaceManager <T> manager)
	{
		super (manager); this.value = manager.newScalar (value);
	}

	ScalarValue (T value, ExpressionSpaceManager <T> manager)
	{ super (manager); this.value = value; }
	protected T value;

	public String toString () { return manager.format (value); }

	public Double toDouble () { return manager.convertToDouble (value); }

	public Arithmetic.Scalar plus (Arithmetic.Scalar addend) { return newValue (manager.add (value, getValue (addend))); }
	public Arithmetic.Scalar times (Arithmetic.Scalar factor) { return newValue (manager.multiply (value, getValue (factor))); }
	public Arithmetic.Scalar negated () { return newValue (manager.negate (value)); }

	public Arithmetic.Scalar pow (Arithmetic.Scalar exponent)
	{
		Double exp = manager.convertToDouble (getValue (exponent));
		return newValue (manager.pow (value, exp.intValue ()));
	}

	public boolean EQ (Arithmetic.Scalar with)
	{
		return this.toDouble () == with.toDouble ();
	}

	public void set (Arithmetic.Scalar newValue)
	{
		this.value = getValue (newValue);
	}

	public boolean isPositive () { return toDouble () > 0.0; }
	public boolean isNegative () { return toDouble () < 0.0; }
	public boolean isNotZero () { return toDouble () != 0.0; }
	public boolean isNotOne () { return toDouble () != 1.0; }

}

