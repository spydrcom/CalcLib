
package net.myorb.math.computational.integration;

import java.util.Map;

/**
 * quadrature implementation as described by parameters
 * @author Michael Druckman
 */
public class Quadrature
{

	/**
	 * the access to numerical integration of an integrand (function)
	 */
	public interface Integral
	{
		/**
		 * @param x the domain point at which to perform integration
		 * @param lo the low point of the domain from which to start the computation
		 * @param hi the high point of the domain at which to end the computation
		 * @return the resulting computation
		 */
		public double eval (double x, double lo, double hi);

		/**
		 * @return an estimation of the error in the analysis
		 */
		public double getErrorEstimate ();

		/**
		 * @return the count of function evaluations used
		 */
		public int getEvaluationCount ();
	}

	/**
	 * @param integrand the function to be the subject of the numerical integration
	 * @param parameters the parameter hash that contains configuration for the algorithm
	 */
	public Quadrature
		(
			RealIntegrandFunctionBase integrand,
			Map<String,Object> parameters
		)
	{
		this.integrand = integrand;
		this.parameters = parameters;
		this.method = Configuration.getMethod (parameters);
	}
	protected Map<String,Object> parameters;

	/**
	 * given the quadrature configuration parameters
	 *  build an object that will provide numerical integration for the integrand
	 * @return a newly constructed Integral object
	 */
	public Integral getIntegral ()
	{
		switch (method)
		{
			case TSQ:	return new TSQuadrature (integrand, parameters);
			case CCQ:	return new CCQuadrature (integrand, parameters);
			case GAUSS:	return new GaussQuadrature (integrand, parameters).getIntegral ();
			case CTA:	return new TrapezoidalApproximation (integrand, parameters, false);
			case CTAA:	return new TrapezoidalApproximation (integrand, parameters, true);
			default: throw new RuntimeException ("Integration method not recognized");
		}
	}
	protected RealIntegrandFunctionBase integrand;
	protected Configuration.Methods method;

}
