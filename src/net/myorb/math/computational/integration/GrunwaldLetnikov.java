
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.specialfunctions.Binomial;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * configuration object for Grünwald-Letnikov derivative equation
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class GrunwaldLetnikov <T> extends QuadratureCore <T>
	implements Quadrature.Integral
{


	public GrunwaldLetnikov
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (null);					// summation in place of integral
		this.setIntegrand (integrand);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.QuadratureCore#constructIntegral(net.myorb.math.expressions.tree.RangeNodeDigest, net.myorb.math.computational.Parameterization.Hash)
	 */
	public Quadrature.Integral constructIntegral
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		)
	{
		constructIntegrand (digest, options);
		return this;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.QuadratureCore#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		return transform.eval (x, lo, hi);
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
			this.transform = new GrünwaldLetnikovOperator <T>
					(digest, options, environment)
		);
	}
	protected GrünwaldLetnikovOperator <T> transform;


}


/**
 * integrand object for Grünwald-Letnikov derivative algorithm
 * @param <T> data type being processed
 */
class GrünwaldLetnikovOperator <T>
	extends IntegrandCore <T>
{

	/*
	 * D^q f(x) = [h->0] 1/h^q * SIGMA [ 0 <= m <= INFINITY ] ( (-1)^m * BC[q m] * f (x - m*h) )
	 */

	public GrünwaldLetnikovOperator
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options,
			Environment <T> environment
		)
	{
		super (digest, options, environment);
		this.processOrder (null, "G");
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.IntegrandCore#parameterize(double)
	 */
	public void parameterize (double p)
	{
		this.setCombinatorics ();
		this.h = this.getDelta ();
		this.terms = (Number) cvt.toDouble (digest.getHiBnd ());
		this.q = cvt.fromDouble (p);
		this.p = p;
	}
	protected T q;			// this algorithm identifies order with q
	protected Number terms;	// count of terms to sum (approximation of INFINITY)
	protected double h;		// equation using LIM [h->0]
	protected double p;


	void dump ()
	{
		System.out.println ("order: " + q);
		System.out.println ("terms: " + terms);
		System.out.println ("h: " + h);
	}


	T BC (T x, T y)
	{ return Binomial.gammaBinomialCoefficient (x, y); }
	T BC (T q, double m) { return BC (q, cvt.fromDouble (m)); }

	void setCombinatorics ()
	{
		this.lib = environment.getLibrary ();
		this.manager = environment.getSpaceManager ();
//		this.Binomial = new Binomial <T> (manager, lib);
	}
	protected Binomial <T> Binomial;
	protected ExtendedPowerLibrary <T> lib;
	protected SpaceManager <T> manager;


	public double eval (double x, double lo, double hi)
	{
		double sum = 0.0, sign = 1;
		x = cvt.toDouble (digest.getDelta ());
		for (int m = 0; m < terms.intValue (); m++)
		{
			sum += sign * cvt.toDouble (BC (q, m)) *
				this.evaluateTarget (x - m * h);
			sign = - sign;
		}
		return sum / Math.pow (h, p);
	}


}

