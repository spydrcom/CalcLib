
package net.myorb.testing.factors;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.primenumbers.Factorization;

/**
 * import test suite that computes SQRT values to be used as constants
 * @author Michael Druckman
 */
public class ParameterManager extends IterativeAlternativeAlgorithmTests
{

	ExpressionFactorizedFieldManager mgr = FactorizationCore.mgr;

	public ParameterManager ()
	{
		this.prepareConstants ();
		this.computeSqrt ();
		this.computePi ();
	}

	/**
	 * parameters to function calls
	 */
	void prepareConstants ()
	{
		this.TWO	= IT.S (2);						// two
		this.Q		= IT.oneOver (IT.S (4));		// Quarter
		this.H		= IT.oneOver (TWO);				// Half
	}
	protected Factorization TWO, Q, H;

	/**
	 * @return data type manager
	 */
	public ExpressionFactorizedFieldManager getSpaceManager ()
	{
		return mgr;
	}

	/**
	 * @return pi / 2
	 */
	public Factorization getHalfPi ()
	{
		if (hpi != null) return hpi;
		return hpi = IT.productOf (H, pi);
	}
	Factorization hpi = null;

	/**
	 * @return sqrt(3) / 2
	 */
	public Factorization getHalfSqrt3 ()
	{
		if (hs3 != null) return hs3;
		return hs3 = IT.productOf (H, sqrt_3);
	}
	Factorization hs3 = null;

	/**
	 * @return phi - 1
	 */
	public Factorization getPhiMinus1 ()
	{
		if (pm1 != null) return pm1;
		return pm1 = IT.reduce (getPhi (), 1);
	}
	Factorization pm1 = null;

	/**
	 * @return computed value of phi
	 */
	public Factorization getPhi ()
	{
		if (phiCopy != null) return phiCopy;
		computePhi (); return phiCopy = phi;
	}
	Factorization phiCopy = null;

	/**
	 * @return pi / 12
	 */
	public Factorization PiOver12 ()
	{
		if (p12 != null) return p12;
		return p12 = FactorizationCore.getPiOver (12, MAX_PRECISION);
	}
	Factorization p12 = null;

	/**
	 * compute TAN(PI/12)
	 * @return 2 - SQRT(3)
	 */
	public Factorization tanPi12 ()
	{
		if (tp12 != null) return tp12;
		return tp12 = IT.reduce (TWO, sqrt_3);
	}
	Factorization tp12 = null;

	/**
	 * compute TAN(PI/6)
	 * @return 1 / SQRT(3)
	 */
	public Factorization tanPi6 ()
	{
		if (tp6 != null) return tp6;
		return tp6 = IT.oneOver (sqrt_3);
	}
	Factorization tp6 = null;

}
