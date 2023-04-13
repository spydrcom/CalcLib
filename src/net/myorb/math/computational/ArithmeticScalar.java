
package net.myorb.math.computational;

import net.myorb.math.computational.ArithmeticFundamentals.Scalar;
import net.myorb.math.expressions.ExpressionSpaceManager;

/**
 * implementation of the Scalar object generic type
 * @param <T> data type used in Arithmetic operations
 * @author Michael Druckman
 */
public class ArithmeticScalar <T> extends Core <T> implements ArithmeticFundamentals.Scalar
{

	public ArithmeticScalar (int value, ExpressionSpaceManager <T> manager)
	{ this (manager.newScalar (value), manager); }

	public ArithmeticScalar (T value, ExpressionSpaceManager <T> manager)
	{ super (manager); this.value = value; }
	protected T value;

	public ArithmeticScalar
		(
			T value, ExpressionSpaceManager <T> manager,
			ArithmeticFundamentals.ScalarFactory <T> factory
		)
	{ super (manager, factory); this.value = value; }


	// value assignment

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Scalar#set(net.myorb.math.computational.ArithmeticFundamentals.Scalar)
	 */
	public void set (ArithmeticFundamentals.Scalar newValue) { this.value = getValue (newValue); }


	// scalar operations

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Scalar#pow(int)
	 */
	public ArithmeticFundamentals.Scalar pow (int exponent) { return newValue ( manager.pow (value, exponent) ); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Scalar#plus(net.myorb.math.computational.ArithmeticFundamentals.Scalar)
	 */
	public ArithmeticFundamentals.Scalar plus (ArithmeticFundamentals.Scalar addend) { return newValue (manager.add (value, getValue (addend))); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Scalar#times(net.myorb.math.computational.ArithmeticFundamentals.Scalar)
	 */
	public ArithmeticFundamentals.Scalar times (ArithmeticFundamentals.Scalar factor) { return newValue (manager.multiply (value, getValue (factor))); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Scalar#negated()
	 */
	public ArithmeticFundamentals.Scalar negated () { return newValue (manager.negate (value)); }


	// value comparisons

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int
		compareTo (Scalar value)
	{ throw new RuntimeException ("Comparison not implemented"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Scalar#EQ(net.myorb.math.computational.ArithmeticFundamentals.Scalar)
	 */
	public boolean EQ (ArithmeticFundamentals.Scalar with) { return this.compareTo (with) == 0; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Scalar#isPositive()
	 */
	public boolean isPositive () { return this.compareTo (ZERO) > 0; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Scalar#isNegative()
	 */
	public boolean isNegative () { return this.compareTo (ZERO) < 0; }


	// value conversion

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ArithmeticFundamentals.Scalar#toDouble()
	 */
	public Double toDouble () { return manager.convertToDouble (value); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return manager.format (value); }


}

