
package net.myorb.math.computational.integration;

import net.myorb.math.computational.Parameterization;

import java.util.Map;

/**
 * processing for parameters that
 *  establish integration implementation
 * @author Michael Druckman
 */
public class Configuration extends Parameterization
{

	public Configuration (Map<String, Object> hash)
	{
		super (hash);
	}

	/**
	 * identification of series evaluation approaches
	 */
	public enum Approaches
	{
		STRAIGHT,		// calculate every term for every function call
		POLYNOMIAL,		// construct a polynomial with all coefficients calculated once
		SECTIONED		// calculate the coefficients for each section and treat as 2 series
	}

	/**
	 * @return the method of calculation configured for the function
	 */
	public Approaches identifyApproach ()
	{
		String method;
		if ((method = getMethood ()) != null)
		{
			try
			{ return Approaches.valueOf (method); }
			catch (Exception e) {}
		}
		return Approaches.SECTIONED;
	}

	/**
	 * identification of quadrature algorithms
	 */
	public enum Methods
	{
		TSQ,	// Tanh-Sinh Quadrature
		CCQ,	// Clenshaw-Curtis Quadrature
		ASQ,	// Adaptive Spline Quadrature
		VCQ,	// Vandermonde-Chebychev Quadrature (VanChe)
		CTA,	// Common Trapezoidal Approximation, no adjustment
		CTAA,	// Common Trapezoidal Approximation, using adjustment
		GAUSS,	// Gauss algorithms including Laguerre and Lagrange
		UNKNOWN
	}

	/**
	 * identify method of computation
	 * @return the method identified in the configuration
	 */
	public Methods getMethod ()
	{
		try
		{
			String methodName = getMethood ();
			if (methodName == null) return Methods.CCQ;
			return Methods.valueOf (methodName);
		}
		catch (Exception e)
		{
			return Methods.UNKNOWN;
		}
	}

	/**
	 * get a value of precision to be used either as specified or defaulted
	 * @return the precision value to be used
	 */
	public double getPrecision ()
	{
		Number p = getValue ("precision");
		double precision = DEFAULT_PRECISION;
		if (p != null) precision = p.doubleValue ();
		return precision;
	}
	static final double DEFAULT_PRECISION = 1E-4;

}
