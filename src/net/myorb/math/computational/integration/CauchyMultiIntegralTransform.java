
package net.myorb.math.computational.integration;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.splines.GenericSplineQuad.AccessToTarget;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.tree.RangeNodeDigest;

/**
 * function description of expression in integral target
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class CauchyMultiIntegralTransform <T>
	extends IntegrandCore <T> implements AccessToTarget
{


	/*
	 * I[a,t] f(t) = 1/GAMMA(alpha) * INTEGRAL [a <= tau <= t] 
	 * 					( (t - tau)^(alpha - 1) * f(tau) * <*> tau ) 
	 * 		mu(t,tau) = (t-tau) ^ (alpha-1) / GAMMA(alpha)
	 * t > a
	 */

	/*
	 * I[t,b] f(t) = 1/GAMMA(alpha) * INTEGRAL [t <= tau <= b] 
	 * 					( (tau - t)^(alpha - 1) * f(tau) * <*> tau ) 
	 * 		mu(t,tau) = (tau-t) ^ (alpha-1) / GAMMA(alpha)
	 * t < b
	 */


	public CauchyMultiIntegralTransform
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
		evaluateAt
			(cvt.toDouble (digest.getHiBnd ()));
		this.processOrder ("D", "I");

		options.put ("alpha", alpha);
		options.put ("K", k);
	}
	protected double exponent, coef;


	/**
	 * @param t the value for the upper-bound
	 */
	public void evaluateAt (double t)
	{
		this.upperBound = t;
	}
	protected double upperBound;
	

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.IntegrandCore#parameterize(double)
	 */
	public void parameterize (double p)
	{
		this.alpha = p;
		while (alpha < 1) { alpha += 1; k++; }
		this.coef = 1 / GAMMA.eval (alpha);
		this.exponent = alpha - 1;
		this.p = p;
	}
	protected int k = 0;		// order of required derivatives	}	p + alpha = k
	protected double p = 1;		// requested order of result		}	and
	protected double alpha;		// order of the integral			}	alpha > 1 to avoid asymptote
	

	/**
	 * @return the required order of derivative
	 */
	public int getDerivativeOrder ()
	{
		return k;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.IntegrandCore#mu(double)
	 */
	public double mu (double t)
	{
		return coef * Math.pow (upperBound - t, exponent);
	}


}

