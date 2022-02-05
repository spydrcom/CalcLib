
package net.myorb.math.computational.integration;

import java.util.Map;

/**
 * processing for parameters that
 *  establish integration implementation
 * @author Michael Druckman
 */
public class Configuration
{

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
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @return the method of calculation configured for the function
	 */
	public static Approaches identifyApproach (Map<String,Object> parameters)
	{
		Object specified;
		if ((specified = parameters.get ("method")) != null)
		{
			try
			{
				String name = specified.toString ();
				return Approaches.valueOf (name.toUpperCase ());
			}
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
		CTA,	// Common Trapezoidal Approximation, no adjustment
		CTAA,	// Common Trapezoidal Approximation, using adjustment
		GAUSS,	// Gauss algorithms including Laguerre and Lagrange
		UNKNOWN
	}

	/**
	 * identify method of computation
	 * @param parameters the configuration parameters being used
	 * @return the method identified in the configuration
	 */
	public static Methods getMethod (Map<String,Object> parameters)
	{
		try
		{
			Object methodName =
					parameters.get ("method");
			if (methodName == null) return Methods.CCQ;
			return Methods.valueOf (methodName.toString ());
		}
		catch (Exception e)
		{
			return Methods.UNKNOWN;
		}
	}

	/**
	 * get a value of precision to be used either as specified or defaulted
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @return the precision value to be used
	 */
	public static double getPrecision (Map<String,Object> parameters)
	{
		double precision = DEFAULT_PRECISION;
		Object p = parameters.get ("precision");
		if (p != null) precision = Double.parseDouble (p.toString ());
		return precision;
	}
	static final double DEFAULT_PRECISION = 1E-4;

}
