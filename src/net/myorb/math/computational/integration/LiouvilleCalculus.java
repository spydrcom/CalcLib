
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.computational.Parameterization;

/**
 * configuration object for Liouville Calculus algorithms
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
		this.integrand = integrand;
	}

	/**
	 * set the local integrand object
	 * @param digest the digest that holds the declaration
	 * @param options the parameters for the integrand
	 */
	public void constructIntegrand
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		)
	{
		this.integrand = new CauchyMultiIntegralTransform <T>
				(digest, options, environment);
	}

}

