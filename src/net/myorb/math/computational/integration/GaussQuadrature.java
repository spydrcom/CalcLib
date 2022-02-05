
package net.myorb.math.computational.integration;

import java.util.Map;

/**
 * configuration object for Gauss quadrature implementations
 * @author Michael Druckman
 */
public class GaussQuadrature
{

	public enum Type {LAGRANGE, LAGUERRE}

	public GaussQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters
		)
	{
		switch (Type.valueOf (parameters.get ("type").toString ().toUpperCase ()))
		{
			case LAGRANGE: integral = new LagrangeQuadrature (integrand, parameters); break;
			case LAGUERRE: integral = new LaguerreQuadrature (integrand, parameters); break;
		}
	}

	public Quadrature.Integral getIntegral () { return integral; }
	protected Quadrature.Integral integral;

}
