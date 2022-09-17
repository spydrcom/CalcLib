
package net.myorb.math.expressions.algorithms;

import net.myorb.math.specialfunctions.Gamma;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.TaylorPolynomials;

/**
 * basic exponentiation and root primitives
 *  using JRE Math package to give fastest and most accurate solution available
 * @author Michael Druckman
 */
public class JrePowerLibrary implements ExtendedPowerLibrary<Double>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#ln(java.lang.Object)
	 */
	public Double ln (Double value) { return Math.log (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#exp(java.lang.Object)
	 */
	public Double exp (Double value) { return Math.exp (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#nativeExp(java.lang.Object)
	 */
	public Double nativeExp (Double value) { return Math.exp (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#pow(java.lang.Object, int)
	 */
	public Double pow (Double value, int exponent) { return Math.pow (value, exponent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#power(java.lang.Object, java.lang.Object)
	 */
	public Double power (Double value, Double exponent) { return Math.pow (value, exponent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#sqrt(java.lang.Object)
	 */
	public Double sqrt (Double value) { return Math.sqrt (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#nThRoot(java.lang.Object, int)
	 */
	public Double nThRoot (Double x, int root) { return exp (ln (x) / root); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#factorial(java.lang.Object)
	 */
	public Double factorial (Double value) { return taylor.factorial (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#dFactorial(java.lang.Object)
	 */
	public Double dFactorial (Double value) { return taylor.dFactorial (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#GAMMA(java.lang.Object)
	 */
	public Double GAMMA (Double value)
	{
//		throw new RuntimeException ("No GAMMA support present in library");
		if (gamma == null) gamma = new Gamma ();
		return gamma.eval (value);
	}
	Gamma gamma = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#magnitude(java.lang.Object)
	 */
	public Double magnitude (Double x) { return Math.abs (x); }

	protected static ExpressionSpaceManager<Double> mgr =
		new net.myorb.math.expressions.managers.ExpressionFloatingFieldManager ();
	protected TaylorPolynomials<Double> taylor = new TaylorPolynomials<Double> (mgr);

}
