
package net.myorb.math.expressions.algorithms;

import net.myorb.math.ExtendedPowerLibrary;

import net.myorb.data.abstractions.SpaceConversion;

/**
 * extend JRE library use to generic types that can have real representation
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class ExtendedJrePowerLibrary<T> implements ExtendedPowerLibrary<T>
{

	public ExtendedJrePowerLibrary (SpaceConversion<T> conversion)
	{
		this.realLib = new JrePowerLibrary ();
		this.conversion = conversion;
	}
	SpaceConversion<T> conversion;
	JrePowerLibrary realLib;

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#ln(java.lang.Object)
	 */
	public T ln (T value) { return conversion.convertFromDouble (realLib.ln (conversion.convertToDouble (value))); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#exp(java.lang.Object)
	 */
	public T exp (T value) { return conversion.convertFromDouble (realLib.exp (conversion.convertToDouble (value))); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#pow(java.lang.Object, int)
	 */
	public T pow (T value, int exponent) { return conversion.convertFromDouble (realLib.pow (conversion.convertToDouble (value), exponent)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#power(java.lang.Object, java.lang.Object)
	 */
	public T power (T value, T exponent)
	{
		return conversion.convertFromDouble
			(
				realLib.power
				(
					conversion.convertToDouble (value),
					conversion.convertToDouble (exponent)
				)
			);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#sqrt(java.lang.Object)
	 */
	public T sqrt (T value) { return conversion.convertFromDouble (realLib.sqrt (conversion.convertToDouble (value))); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#factorial(java.lang.Object)
	 */
	public T factorial (T value) { return conversion.convertFromDouble (realLib.factorial (conversion.convertToDouble (value))); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#dFactorial(java.lang.Object)
	 */
	public T dFactorial (T value) { return conversion.convertFromDouble (realLib.dFactorial (conversion.convertToDouble (value))); }

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#nThRoot(java.lang.Object, int)
	 */
	public T nThRoot (T x, int root) { return conversion.convertFromDouble (realLib.nThRoot (conversion.convertToDouble (x), root)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#magnitude(java.lang.Object)
	 */
	public T magnitude (T x)
	{
		return conversion.convertFromDouble (Math.abs (conversion.convertToDouble (x)));
	}

}
