
package net.myorb.math.polynomial;

import net.myorb.math.TransformCalculus;
import net.myorb.math.GeneratingFunctions.Coefficients;

import java.util.List;

/**
 * describe support layer for implementation of calculus operations
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface PolynomialCalculusSupport<T> extends TransformCalculus<T>
{


	/**
	 * given a polynomial with specified coefficients
	 *  compute the derivative of the function at specified x and return value
	 * @param a the coefficients of the polynomial
	 * @param atX the value of x at which to evaluate 
	 * @return the value of the derivative at x
	 */
	public T evaluatePolynomialDerivative (Coefficients<T> a, T atX);


	/**
	 * given a polynomial with specified coefficients
	 *  compute the integral of the function at specified x and return value
	 * @param a the coefficients of the polynomial
	 * @param atX the value of x at which to evaluate 
	 * @return the value of the integral at x
	 */
	public T evaluatePolynomialIntegral (Coefficients<T> a, T atX);


	/**
	 * given a polynomial with specified coefficients
	 *  compute the integral of the function over the specified interval
	 * @param a the coefficients of the polynomial being integrated
	 * @param fromX the lo value of the interval at which to evaluate
	 * @param toX the hi value of the interval at which to evaluate 
	 * @return the value of the integral at x
	 */
	public T evaluatePolynomialIntegral (Coefficients<T> a, T fromX, T toX);


	/**
	 * given a polynomial with specified coefficients
	 *  compute the integral of the function over the specified intervals.
	 *  one lo end for all intervals specified by multiple upper interval endpoints.
	 * @param a the coefficients of the polynomial being integrated
	 * @param fromX the lo value of the intervals at which to evaluate
	 * @param toX the hi values of the intervals at which to evaluate 
	 * @return the value of the integral at x
	 */
	public List<T> evaluatePolynomialIntegral (Coefficients<T> a, T fromX, List<T> toX);


}

