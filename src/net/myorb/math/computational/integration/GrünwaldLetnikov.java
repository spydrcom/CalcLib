
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.computational.Combinatorics;

/**
 * configuration object for Grünwald-Letnikov derivative equation
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class GrünwaldLetnikov <T> extends QuadratureCore <T>
{

	public GrünwaldLetnikov
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (null);					// summation in place of integral
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
		this.terms = (int) eval ("INFINITY");
		this.h = this.getDelta ();
		this.q = p;
	}
	protected double q;		// this algorithm identifies order with q
	protected int terms;	// count of terms to sum (approximation of INFINITY)
	protected double h;		// equation using LIM [h->0]


	T BC (T x, T y)
	{
		return combinatorics.gammaBinomialCoefficient (x, y);
	}
	void setCombinatorics ()
	{
		this.lib = environment.getLibrary ();
		this.manager = environment.getSpaceManager ();
		this.combinatorics = new Combinatorics <T> (manager, lib);
	}
	protected Combinatorics <T> combinatorics;
	protected ExtendedPowerLibrary <T> lib;
	protected SpaceManager <T> manager;


}

