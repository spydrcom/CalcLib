
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.AbstractVectorReduction.Range;

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
	 * @param parameters the parameter hash that contains configuration for the algorithm
	 */
	public Quadrature
		(
			Map<String,Object> parameters
		)
	{
		this.parameters = new Configuration (parameters);
	}
	protected Configuration parameters;

	/**
	 * given the quadrature configuration parameters
	 *  build an object that will provide numerical integration for the integrand
	 * @param integrand the function to be the subject of the numerical integration
	 * @return a newly constructed Integral object
	 */
	public Integral getIntegral (RealIntegrandFunctionBase integrand)
	{
		switch (parameters.getMethod ())
		{
			case TSQ:	return new TSQuadrature (integrand, parameters);
			case CCQ:	return new CCQuadrature (integrand, parameters);
			case VCQ:	return new VCQuadrature (integrand, parameters);
			case ASQ:	return new ASQuadrature (integrand, parameters);
			case CPC:	return new CPQuadrature (integrand, parameters);
			case GAUSS:	return new GaussQuadrature (integrand, parameters).getIntegral ();
			case CTA:	return new TrapezoidalApproximation (integrand, parameters, false);
			case CTAA:	return new TrapezoidalApproximation (integrand, parameters, true);
			default: throw new RuntimeException ("Integration method not recognized");
		}
	}

	/**
	 * format a special case portion of a render
	 * @param range the range descriptor that introduced the integral
	 * @param using the node formatting support object supplied for the render
	 * @return mark-up for section specific to an algorithm
	 */
	public String specialCaseRenderSection (Range range, NodeFormatting using)
	{
		switch (parameters.getMethod ())
		{
			case GAUSS:
				if (GaussQuadrature.getType (parameters) == GaussQuadrature.GaussTypes.LAGUERRE)
				{
					return LaguerreQuadrature.specialCaseRenderSection (range, using);
				}
			default: return "";
		}
	}

}
