
package net.myorb.math.specialfunctions;

import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

/**
 * formulas for relationships between polylog functions
 * @author Michael Druckman
 */
public class PolylogRatioFormulas
{


	/**
	 * formulas are implemented intended for complex numbers 
	 */
	public static class CORE extends ComplexSpaceCore {}
	public static final ComplexValue<Double> ONE = CORE.manager.getOne ();


	/**
	 * common product function
	 * @param s function parameter
	 * @return 2^(1-s)
	 */
	public static ComplexValue<Double> mu (ComplexValue<Double> s)
	{
		return CORE.cplxLib.power
			(
				CORE.manager.newScalar (2),
				CORE.manager.add
				(
					ONE, CORE.manager.negate (s)
				)
			);
	}


	/**
	 * 1 - mu(s) is a common factor
	 * @param s function parameter
	 * @return 1 - mu(s)
	 */
	public static ComplexValue<Double> oneMinusMu (ComplexValue<Double> s)
	{
		return CORE.manager.add (ONE, CORE.manager.negate (mu (s)));
	}


	/**
	 * @param s function parameter
	 * @param zetaValue the zeta evaluation for s
	 * @return the value of eta for s
	 */
	public static ComplexValue<Double> etaFromZeta
		(ComplexValue<Double> s, ComplexValue<Double> zetaValue)
	{
		return CORE.manager.multiply (zetaValue, oneMinusMu (s));
	}


	/**
	 * @param s function parameter
	 * @param etaValue the eta evaluation for s
	 * @return the value of zeta for s
	 */
	public static ComplexValue<Double> zetaFromEta
		(ComplexValue<Double> s, ComplexValue<Double> etaValue)
	{
		return CORE.manager.multiply (etaValue, CORE.manager.invert (oneMinusMu (s)));
	}


	/**
	 * GAMMA function evaluation for s+1
	 * @param s function parameter
	 * @return GAMMA(s+1)
	 */
	public static ComplexValue<Double> gammaSplusOne (ComplexValue<Double> s)
	{
		return CORE.cplxLib.gamma (CORE.manager.add (ONE, s));				// uses Lanczos in cplxlib
	}


	/**
	 * @param s function parameter
	 * @return GAMMA(s+1) * mu(s)
	 */
	public static ComplexValue<Double> muGamma (ComplexValue<Double> s)
	{
		return CORE.manager.multiply (mu (s), gammaSplusOne (s));
	}


	/**
	 * @param s function parameter
	 * @param integralValue the Amdeberhan integral evaluated at s
	 * @return the value of eta at s
	 */
	public static ComplexValue<Double> etaFromAmdeberhan
		(ComplexValue<Double> s, ComplexValue<Double> integralValue)
	{
		return CORE.manager.multiply (integralValue, CORE.manager.invert (muGamma (s)));
	}


	/**
	 * ratio between Amdeberhan and zeta
	 * @param s complex function parameter
	 * @return (1-mu)*G(s+1)*mu
	 */
	public static ComplexValue<Double> mugsi (ComplexValue<Double> s)
	{
		ComplexValue<Double> mug = muGamma (s);
		ComplexValue<Double> oneMinusMu = oneMinusMu (s);
		ComplexValue<Double> mugs = CORE.manager.multiply (mug, oneMinusMu);
		return CORE.manager.invert (mugs);
	}


	/**
	 * @param integral the evaluation of the Amdeberhan integral
	 * @param s the parameter to the function
	 * @return the product giving zeta
	 */
	public static ComplexValue<Double> muProduct
	(ComplexValue<Double> integral, ComplexValue<Double> s)
	{
		return CORE.manager.multiply (integral, mugsi (s));
	}


}

