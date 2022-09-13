
package net.myorb.math.computational.integration;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.data.abstractions.DerivativeApproximations;

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
	public double eval (double x, double a, double t)
	{
		return DerivativeApproximations.compute
		(
			(tau) -> eval (a, tau),
			transform.getDerivativeOrder (),
			t, transform.getDelta ()
		);
	}


	/**
	 * adjust evaluation for derivative rise/run
	 * @param a the low end of the integration interval
	 * @param tau the point to evaluate
	 * @return the computed value
	 */
	double eval (double a, double tau)
	{
		transform.evaluateAt (tau);
		return integral.eval (0, a, tau);
	}


}

