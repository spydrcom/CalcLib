
package net.myorb.math.computational.integration;

import net.myorb.math.computational.TrapezoidIntegration;

import java.util.Map;

/**
 * integration by Trapezoidal Approximation
 * @author Michael Druckman
 */
public class TrapezoidalApproximation implements Quadrature.Integral
{

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		this.integrand.setParameter (x);
		return approximation.eval (lo, hi, delta);
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

	public TrapezoidalApproximation
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters,
			boolean adjusted
		)
	{
		this.approximation = new TrapezoidIntegration<Double> (integrand, adjusted);
		this.delta = Double.parseDouble (parameters.get ("delta").toString ());
		this.integrand = integrand;
	}
	protected TrapezoidIntegration<Double> approximation;
	protected RealIntegrandFunctionBase integrand;
	protected double delta;

}
