
package net.myorb.math.computational.integration;

import net.myorb.math.computational.VCQIntegration;

/**
 * quadrature using VanChe algorithm (Vandermonde-Chebychev)
 * @author Michael Druckman
 */
public class VCQuadrature extends CommonQuadrature
	implements Quadrature.Integral
{

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		integrand.setParameter (x);
		return vcq.computeApproximation (lo, hi);
	}

	public VCQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (integrand, parameters);
		this.vcq = new VCQIntegration (integrand, parameters);
	}
	protected VCQIntegration vcq;


}
