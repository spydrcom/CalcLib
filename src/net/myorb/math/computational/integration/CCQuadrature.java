
package net.myorb.math.computational.integration;

import net.myorb.math.computational.CCQIntegration;

/**
 * quadrature using Clenshaw-Curtis algorithm
 * @author Michael Druckman
 */
public class CCQuadrature extends CommonQuadrature
		implements Quadrature.Integral
{

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		integrand.setParameter (x);
		return ccq.computeApproximation (lo, hi);
	}

	public CCQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (integrand, parameters);
		this.ccq = new CCQIntegration (integrand);
	}
	protected CCQIntegration ccq;

}
