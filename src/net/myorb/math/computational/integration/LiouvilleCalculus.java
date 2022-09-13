
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.computational.Parameterization;

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
		this.options = options;
	}
	protected CauchyMultiIntegralTransform <T> transform;
	protected Parameterization.Hash options;


	/* (non-Javadoc)
	* @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	*/
	public double eval (double x, double lo, double hi)
	{
		switch (transform.getDerivativeOrder ())
		{
			case 0: return this.eval (hi);
			case 1: return this.first (hi);
			case 2: return this.second (hi);
			default:
		}
		throw new RuntimeException ("Too many derivatives required");
	}


	/**
	 * compute first order derivative
	 * @param t parameter to derivative
	 * @return computed derivative
	 */
	double first (double t)
	{
		double delta = transform.getDelta ();
		double e1 = eval (t), e2 = eval (t + delta);
		double d = (e2 - e1) / delta;
		return d;
	}


	/**
	 * compute second order derivative
	 * @param t parameter to derivative
	 * @return computed derivative
	 */
	double second (double t)
	{
		double delta = transform.getDelta ();
		double e1 = eval (t - delta), e2 = eval (t);
		double e3 = e2, e4 = eval (t + delta);
		double d1 = (e2 - e1) / delta;
		double d2 = (e4 - e3) / delta;
		return (d2 - d1) / delta;
	}


	/**
	 * adjust evaluation for derivative rise/run
	 * @param t the point to evaluate
	 * @return the computed value
	 */
	double eval (double t)
	{
		transform.evaluateAt (t);
		return integral.eval (0, 0, t);
	}


}

