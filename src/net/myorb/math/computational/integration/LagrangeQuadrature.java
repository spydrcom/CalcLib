
package net.myorb.math.computational.integration;

import net.myorb.math.computational.GaussQuadrature;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.DataSequence2D;

/**
 * configuration object for Gauss-Lagrange quadrature implementations
 * @author Michael Druckman
 */
public class LagrangeQuadrature extends CommonQuadrature
	implements Environment.AccessAcceptance<Double>
{

	public LagrangeQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (integrand, parameters);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment<Double> environment)
	{
		this.q = new GaussQuadrature<Double> (environment);
	}
	protected GaussQuadrature<Double> q;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		return q.evaluateIntegral (sequenceFor (lo, hi), lo, hi);
	}

	/**
	 * build an evenly spaced sample sequence
	 * @param lo the lo end of the range
	 * @param hi the hi for the range
	 * @return the sequence of pairs
	 */
	public DataSequence2D<Double> sequenceFor (double lo, double hi)
	{
		double delta = parameters.getValue ("delta").doubleValue ();
		DataSequence2D<Double> s = new DataSequence2D<Double>();
		return fill (s, lo, hi, delta);
	}
	public DataSequence2D<Double> fill
		(
			DataSequence2D<Double> s,
			double lo, double hi,
			double delta
		)
	{
		for (double x = lo; x < hi; x += delta)
		{ s.addSample (x, this.integrand.eval (x)); }
		return s;
	}

}
