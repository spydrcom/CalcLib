
package net.myorb.math.computational;

import net.myorb.math.MultiDimensional;
import net.myorb.math.Function;

/**
 * factory interface for integral engine objects
 * @param <T> the data type supported for the operation
 * @author Michael Druckman
 */
public interface MultiDimensionalIntegralEngineFactory<T>
{

	/**
	 * @param integrand the function to be integrated
	 * @return an instance of the integral engine
	 */
	MultiDimensionalIntegral<T> newMultiDimensionalIntegral
		(MultiDimensional.Function<T> integrand);

	/**
	 * @param integrand the function to be integrated
	 * @return an instance of the integral engine
	 */
	MultiDimensionalIntegral<T> newMultiDimensionalIntegral
		(Function<T> integrand);

}
