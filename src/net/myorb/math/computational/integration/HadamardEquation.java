
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.computational.Parameterization;

/**
 * configuration object for Hadamard fractional integral equation
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class HadamardEquation <T> extends QuadratureCore <T>
{

	public HadamardEquation
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (parameters.getParameter ("quad"));
		this.setIntegrand (integrand);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.QuadratureCore#constructIntegrand(net.myorb.math.expressions.tree.RangeNodeDigest, net.myorb.math.computational.Parameterization.Hash)
	 */
	public void constructIntegrand
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		)
	{
		this.setIntegrand
		(
			new HadamardIntegrand <T>
			(digest, options, environment)
		);
	}

}


/**
 * integrand object for Hadamard integral algorithm
 * @param <T> data type being processed
 */
class HadamardIntegrand <T>
	extends IntegrandCore <T>
{

	/*
	 * I[a,t] f(t) = 1/GAMMA(alpha) * INTEGRAL [a <= tau <= t] 
	 * 					( (log(t/tau))^(alpha-1) * f(tau) / tau * <*> tau ) 
	 * 		mu(t,tau) = (log(t/tau))^(alpha-1) / tau
	 * t > a
	 */

	public HadamardIntegrand
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options,
			Environment <T> environment
		)
	{
		super (digest, options, environment);
		this.prepareMu (digest, options);
	}

	/**
	 * compute the values needed for the mu computation
	 * @param digest the digest of the integrand consumer
	 * @param options the options specified on the consumer
	 */
	void prepareMu
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		)
	{
		this.t = cvt.toDouble (digest.getHiBnd ());
		this.processOrder (null, "H");
		options.put ("alpha", alpha);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.IntegrandCore#parameterize(double)
	 */
	public void parameterize (double p)
	{ this.alpha = p; this.exponent = this.alpha - 1; this.G = GAMMA.eval (p); }
	protected double exponent, G;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.IntegrandCore#mu(double)
	 */
	public double mu (double tau)
	{ return Math.pow (Math.log (t / tau), exponent) / (G * tau); }
	double alpha, t;			// order, upper-bound

}

