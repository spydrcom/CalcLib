
package net.myorb.math.computational.integration;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.integration.Configuration;

import net.myorb.math.expressions.symbols.AbstractVectorReduction.Range;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.tree.RangeNodeDigest;

import java.util.Map;

/**
 * quadrature implementation as described by parameters
 * @author Michael Druckman
 */
public class Quadrature
{

	/**
	 * support exported from external sources
	 * @param <T> data type used
	 */
	public interface ExternalSupport <T>
	{

		public Integral getIntegralFor
		(
			Configuration.Methods m,
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		);

		public UsingTransform <T> getSupportFor
		(
			Configuration.Methods m,
			Configuration parameters
		);

	}

	public static void setExternalSupport (ExternalSupport <?> external)
	{
		externalSupport = external;
	}
	static ExternalSupport <?> externalSupport = null;

	/**
	 * the access to numerical integration of an integrand (function)
	 */
	public interface Integral extends IntegralMetadata
	{
		/**
		 * @param x the domain point at which to perform integration
		 * @param lo the low point of the domain from which to start the computation
		 * @param hi the high point of the domain at which to end the computation
		 * @return the resulting computation
		 */
		public double eval (double x, double lo, double hi);
	}

	/**
	 * apply a transform to the integrand for the integral
	 * @param <T> data type for operations
	 */
	public interface UsingTransform <T>
	{
		/**
		 * @param digest the digest from the declaration
		 * @param options parameters to the computations
		 * @return the integral object to use
		 */
		Quadrature.Integral constructIntegral
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		);
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

			case TSQ:		return new TSQuadrature (integrand, parameters);
			case CCQ:		return new CCQuadrature (integrand, parameters);
			case VCQ:		return new VCQuadrature (integrand, parameters);
			case ASQ:		return new ASQuadrature (integrand, parameters);
			case CPC:		return new CPQuadrature (integrand, parameters);

			case LIOUVILLE:	return new LiouvilleCalculus <Double> (integrand, parameters);
			case GAUSS:		return new GaussQuadrature (integrand, parameters).getIntegral ();

			case CTA:		return new TrapezoidalApproximation (integrand, parameters, false);
			case CTAA:		return new TrapezoidalApproximation (integrand, parameters, true);

			default:		return getIntegralFor (parameters.getMethod (), integrand, parameters);

		}
	}

	public <T> Integral getIntegralFor
	(Configuration.Methods m, RealIntegrandFunctionBase integrand, Configuration parameters)
	{
		if (externalSupport != null)
			return externalSupport.getIntegralFor (m, integrand, parameters);
		throw new RuntimeException ("No support for method");
	}

	/**
	 * given the quadrature configuration parameters
	 *  build an object that will provide numerical integration for a transformed integrand
	 * @param <T> the data type to be used for operations
	 * @return a newly constructed transform object
	 */
	public <T> UsingTransform <T> getTransform ()
	{
		switch (parameters.getMethod ())
		{
			case LIOUVILLE:	return new LiouvilleCalculus <T> (null, parameters);
			default: return getSupportFor (parameters.getMethod (), parameters);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> UsingTransform <T> getSupportFor
	(Configuration.Methods m, Configuration parameters)
	{
		if (externalSupport != null)
			return (UsingTransform <T>) externalSupport.getSupportFor (m, parameters);
		throw new RuntimeException ("No support for method");
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
