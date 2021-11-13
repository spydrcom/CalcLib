
package net.myorb.math.complexnumbers;

import net.myorb.data.abstractions.SpaceConversion;
import net.myorb.math.expressions.algorithms.ExtendedJrePowerLibrary;

/**
 * library of operations use in complex arithmetic
 * implemented using JRE built-in functions for speed and accuracy
 * @author Michael Druckman
 */
public class JreComplexSupportLibrary
	extends ExtendedJrePowerLibrary<Double>
	implements ComplexSupportLibrary<Double>
{

	public JreComplexSupportLibrary (SpaceConversion<Double> conversion)
	{
		super (conversion);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.ComplexSupportLibrary#sin(java.lang.Object)
	 */
	public Double sin (Double t)
	{
		return Math.sin (t);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.ComplexSupportLibrary#cos(java.lang.Object)
	 */
	public Double cos (Double t)
	{
		return Math.cos (t);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.ComplexSupportLibrary#sinh(java.lang.Object)
	 */
	public Double sinh (Double t)
	{
		return Math.sinh (t);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.ComplexSupportLibrary#cosh(java.lang.Object)
	 */
	public Double cosh (Double t)
	{
		return Math.cosh (t);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.ComplexSupportLibrary#atan(java.lang.Object, java.lang.Object)
	 */
	public Double atan (Double x, Double y)
	{
		return Math.atan2 (x, y);
	}

}
