
package net.myorb.math.computational.integration;

import java.util.Map;

import net.myorb.math.computational.GLQuadrature;

/**
 * configuration object for Gauss-Laguerre quadrature implementations
 * @author Michael Druckman
 */
public class LaguerreQuadrature extends CommonQuadrature
{

	public LaguerreQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters
		)
	{
		super (integrand, parameters);
		configureLists ();
	}

	public void configureLists ()
	{
		int forOrder = Integer.parseInt (parameters.get ("order").toString ());
		double domainHi = Double.parseDouble (parameters.get ("hi").toString ());
		laguerreLists = GLQuadrature.computeWeights (forOrder, domainHi);
	}
	protected GLQuadrature.LaguerreLists laguerreLists;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		return GLQuadrature.approximateIntegral (integrand, laguerreLists);
	}

}
