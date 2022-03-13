
package net.myorb.math.computational;

import net.myorb.math.computational.integration.RealDomainIntegration;
import net.myorb.math.computational.integration.Configuration;

import net.myorb.math.Function;

/**
 * description of objects creating splines
 *  and the operations of spline objects once created
 * @author Michael Druckman
 */
public class Spline
{


	/**
	 * the functionality provided by generated splines
	 * @param <T> type on which operations are to be executed
	 */
	public interface Operations<T>
		extends Function<T>, RealDomainIntegration<T>
	{
		/**
		 * @return the computed integral of the full range
		 */
		T evalIntegral ();
	}


	/**
	 * objects that provide quadrature functionality in constructed spline models 
	 * @param <T> type on which operations are to be executed
	 */
	public interface Factory<T>
	{
		/**
		 * generate a spline for function
		 * @param f the function to be fit with the spline evaluation
		 * @param lo the lo end of spline domain range in function coordinates
		 * @param hi the hi end of spline domain range in function coordinates
		 * @param configuration the configuration of parameter management
		 * @return a spline providing interface functionality
		 */
		public Operations<T> generateSpline
		(
			Function<T> f, double lo, double hi,
			Parameterization configuration
		);
	}


	/**
	 * use factory parameter to construct object that exports a Spline Factory functionality
	 * @param parameters the configuration of parameter management
	 * @return the constructed Spline factory
	 * @param <T> data type for operations
	 */
	@SuppressWarnings("unchecked")
	public static <T> Spline.Factory<T> buildFactoryFrom (Configuration parameters)
	{
		try
		{
			String factoryName = parameters.getParameter ("factory");
			Object factory = Class.forName (factoryName).newInstance ();
			return (Spline.Factory<T>) factory;
		}
		catch (Exception e) { throw new RuntimeException ("Factory not available"); }
	}


}

