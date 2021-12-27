
package net.myorb.math.computational.integration;

import net.myorb.math.computational.CCQIntegration;

import java.util.Map;

/**
 * quadrature using Clenshaw-Curtis algorithm
 * @author Michael Druckman
 */
public class CCQuadrature implements Quadrature.Integral
{

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		integrand.setParameter (x);
		return ccq.computeApproximation (lo, hi);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#getErrorEstimate()
	 */
	public double getErrorEstimate ()
	{
		throw new RuntimeException ("Error estimate not available");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#getEvaluationCount()
	 */
	public int getEvaluationCount ()
	{
		throw new RuntimeException ("Evaluation count not available");
	}

	public CCQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters
		)
	{
		this.ccq = new CCQIntegration (integrand);
		this.integrand = integrand;
	}
	protected RealIntegrandFunctionBase integrand;
	protected CCQIntegration ccq;

}
