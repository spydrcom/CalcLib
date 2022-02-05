
package net.myorb.math.computational.integration;

import java.util.Map;

/**
 * common base for Integral object
 * @author Michael Druckman
 */
public class CommonQuadrature implements Quadrature.Integral
{

	public CommonQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters
		)
	{
		this.parameters = parameters;
		this.integrand = integrand;
	}
	protected RealIntegrandFunctionBase integrand;
	protected Map<String,Object> parameters;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		throw new RuntimeException ("Algorithm not implemented");
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

}
