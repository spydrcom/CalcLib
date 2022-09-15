
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.data.abstractions.DerivativeApproximations;
import net.myorb.math.computational.Parameterization;

/**
 * configuration object for Caputo Fabrizio derivative equation
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class CaputoFabrizio <T> extends QuadratureCore <T>
{

	public CaputoFabrizio
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
			this.transform = new CaputoFabrizioIntegrand <T>
					(digest, options, environment)
		);
	}
	protected CaputoFabrizioIntegrand <T> transform;

}


/**
 * integrand object for Caputo-Fabrizio derivative algorithm
 * @param <T> data type being processed
 */
class CaputoFabrizioIntegrand <T>
	extends IntegrandCore <T>
{

	/*
	 * D[a,t] f(t) = 1/(1-alpha) * INTEGRAL [a <= tau <= t] 
	 * 					( f'(tau) * exp ( -alpha * (t-tau) / (1-alpha) ) * <*> tau ) 
	 * 		mu(t,tau) = exp ( -alpha * (t-tau) / (1-alpha) ), constant = -alpha / (1-alpha)
	 * a < 0, 0 < alpha < 1
	 */

	public CaputoFabrizioIntegrand
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options,
			Environment <T> environment
		)
	{
		super (digest, options, environment);
		this.prepareMu (digest, options);
		this.delta = this.getDelta ();
	}
	protected double delta;

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
		this.processOrder (null, "C");
		this.checkAlpha ();
	}

	/**
	 * verify alpha range
	 */
	void checkAlpha ()
	{
		if (alpha > 0 && alpha < 1)
		{
			options.put ("alpha", alpha);
			return;
		}
		throw new RuntimeException ("Caputo alpha must be > 0 and < 1");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.IntegrandCore#parameterize(double)
	 */
	public void parameterize (double p)
	{ this.alpha = p; this.factor = 1 / (1 - p); this.coef = - p * factor; }
	protected double coef, factor;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.IntegrandCore#mu(double)
	 */
	public double mu (double tau)
	{ return factor * Math.exp (coef * (t - tau)); }
	protected double alpha, t;								// order, upper-bound

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealIntegrandFunctionBase#eval(java.lang.Double)
	 */
	public Double eval (Double t)
	{
		// first order derivative applied to the target function
		return mu (t) * DerivativeApproximations.first
		(
			(tau) -> this.evaluateTarget (tau),
			t, this.delta
		);
	}

}

