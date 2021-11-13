
package net.myorb.math.computational;

import net.myorb.math.computational.DerivativeApproximation;

import net.myorb.data.abstractions.Function;

/**
 * approximate derivatives for generic functions using rise/run concepts
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public interface DerivativeApproximationEngine<T>
{

	/**
	 * @param f the function to differentiate
	 * @param delta the offset from the data point
	 * @return the functions that approximate the derivatives
	 */
	DerivativeApproximation.Functions<T> approximateDerivativesFor
	(
		Function<T> f, T delta
	);

}
