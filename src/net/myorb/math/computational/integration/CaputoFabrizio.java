
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.computational.Parameterization;

/**
 * configuration object for Caputo Fabrizio derivative equation
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
		this.integrand = integrand;
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
		this.integrand = new CauchyMultiIntegralTransform <T>
				(digest, options, environment);
	}

}

