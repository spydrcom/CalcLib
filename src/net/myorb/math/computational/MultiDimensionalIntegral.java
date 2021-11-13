
package net.myorb.math.computational;

import java.util.List;

/**
 * common entry points for objects that implement integral approximation
 * @param <T> the data type supported for the operation
 * @author Michael Druckman
 */
public interface MultiDimensionalIntegral<T>
{

	/**
	 * @param deltas the DELTA for each dimension
	 */
	void setDeltas (List<T> deltas);

	/**
	 * @param level a relative value of precision
	 */
	void setRequestedPrecision (Number level);

	/**
	 * N-dimensional
	 * @param lo the LO of the interval for each dimension
	 * @param hi the HI of the interval for each dimension
	 * @return the computed approximation
	 */
	T computeApproximation (List<T> lo, List<T> hi);

	/**
	 * simple 1-dimensional
	 * @param lo the LO of the interval
	 * @param hi the HI of the interval
	 * @return the computed result
	 */
	T computeApproximation (T lo, T hi);

}
