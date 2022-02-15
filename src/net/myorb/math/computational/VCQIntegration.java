
package net.myorb.math.computational;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.computational.Parameterization;
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
		this.lud = new VC31LUD (configuration);
		this.configuration = configuration;
	}
	protected Parameterization configuration;
	protected VC31LUD lud;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#computeApproximation(java.lang.Object, java.lang.Object)
	 */
	public Double computeApproximation (Double lo, Double hi)
	{
		GeneratingFunctions.Coefficients<Double> regression =
				lud.spline (integrand1D, lo, hi);
		VC31LUD.vec.show (regression);
		return 0.0;
	}


}

