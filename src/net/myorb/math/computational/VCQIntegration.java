
package net.myorb.math.computational;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.computational.Parameterization;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;
import net.myorb.math.Function;

/**
 * integral objects based on VanChe Quadrature (Vandermonde-Chebychev)
 * @author Michael Druckman
 */
public class VCQIntegration
	extends MultiDimensionalRealIntegralSupport
	implements MultiDimensionalIntegral<Double>
{


	public VCQIntegration
		(
			Function<Double> integrand,
			Parameterization configuration
		)
	{
		super (integrand);
		this.configuration = configuration;
		this.lud = new VC31LUD (configuration);
		this.calculus = new ChebyshevPolynomialCalculus<Double>(VC31LUD.mgr);
	}
	protected ChebyshevPolynomialCalculus<Double> calculus;
	protected Parameterization configuration;
	protected VC31LUD lud;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#computeApproximation(java.lang.Object, java.lang.Object)
	 */
	public Double computeApproximation (Double lo, Double hi)
	{
		GeneratingFunctions.Coefficients<Double> regression = lud.spline (integrand1D, lo, hi);
		return computeApproximation (regression, lo, hi);
	}


	public Double computeApproximation
		(
			GeneratingFunctions.Coefficients<Double> regression, 
			Double lo, Double hi
		)
	{
		double slope = (hi-lo) / VC31LUD.SPLINE_RANGE;

		return slope * calculus.evaluatePolynomialIntegral
			(
				regression,
				VC31LUD.SPLINE_LO,
				VC31LUD.SPLINE_HI
			);
	}


}

