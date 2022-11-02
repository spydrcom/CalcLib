
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

/**
 * Bose-Einstein polylog integral formula
 * @author Michael Druckman
 */
public class BoseEinstein extends ComplexExponentComponents
{

	/**
	 * compute t^(s-1)
	 * @param s the real part of the exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public double mu (double s, double t)
	{ return Math.pow (t, s-1); }

	/**
	 * complex integral factor
	 * @param z the parameter to the Li function
	 * @param t the integration variable
	 * @return the complex factor
	 */
	public ComplexValue <Double> muz (ComplexValue <Double> z, double t)
	{
		ComplexValue <Double> negZ = ComplexSpaceCore.manager.negate (z);
		ComplexValue <Double> expT = ComplexSpaceCore.manager.C ( - Math.exp (t), 0.0 );
		ComplexValue <Double> invZexp = ComplexSpaceCore.manager.invert
				(ComplexSpaceCore.manager.add (expT, z));
		return ComplexSpaceCore.manager.multiply (negZ, invZexp);
	}

	/**
	 * evaluate integral in fully complex arithmetic
	 * @param s complex order of function
	 */
	public BoseEinstein (ComplexValue <Double> s)
	{
		super (s);
	}

	/**
	 * prepare evaluation using just real numbers
	 * @param alpha the real part of the order
	 * @param sigma the imag part of the order
	 */
	public BoseEinstein (double alpha, double sigma)
	{
		super (alpha, sigma);
	}

	/**
	 * allow as base class for static reference
	 */
	public BoseEinstein () {}

}
