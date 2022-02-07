
package net.myorb.math.computational.integration;

/**
 * configuration object for Gauss quadrature implementations
 * @author Michael Druckman
 */
public class GaussQuadrature
{

	public enum GaussTypes {LAGRANGE, LAGUERRE}

	/**
	 * @param parameters Configuration parameters for the algorithm
	 * @return the sub-type of the Gauss Quadrature being implemented
	 */
	public static GaussTypes getType (Configuration parameters)
	{
		return GaussTypes.valueOf (parameters.getType ());
	}

	public GaussQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		switch (getType (parameters))
		{
			case LAGRANGE: integral = new LagrangeQuadrature (integrand, parameters); break;
			case LAGUERRE: integral = new LaguerreQuadrature (integrand, parameters); break;
		}
	}

	public Quadrature.Integral getIntegral () { return integral; }
	protected Quadrature.Integral integral;

}
