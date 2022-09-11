
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.computational.Parameterization;
import net.myorb.data.abstractions.Function;

/**
 * configuration object for Liouville Calculus algorithms
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class LiouvilleCalculus <T> extends QuadratureCore <T>
{

	public LiouvilleCalculus
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
				transform = new CauchyMultiIntegralTransform <T>
						(digest, options, environment)
		);
	}
	protected CauchyMultiIntegralTransform <T> transform;

	/* (non-Javadoc)
	* @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	*/
	public double eval (double x, double lo, double hi)
	{
		int order = transform.getDerivativeOrder ();
		if (order == 0) return this.integral.eval (x, lo, hi);
		Function <Double> I = this.getIntegralFunction (lo);
		Function <Double> f = transform.getDerivativeFor (I, order);
		return f.eval (hi);
	}

}

