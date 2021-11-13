
package net.myorb.math.polynomial;

import net.myorb.math.Polynomial;
import net.myorb.math.SpaceManager;

import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * support for calculus on polynomials.
 *  polynomial object connected to a power function must have calculus support implemented.
 *  the support object makes available derivative and integral methods for calculation using the coefficients.
 *  the calculus implementation can support any set of algorithms appropriate to the polynomial type.
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class PolynomialCalculus<T> extends Polynomial<T>
{


	public PolynomialCalculus
		(SpaceManager<T> manager)
	{ super (manager); }


	/**
	 * given a Polynomial Power Function with specified descriptor
	 *  compute the derivative of the function at specified x and return value
	 * @param f the power function to be evaluated
	 * @param atX the value of x at which to evaluate 
	 * @return the value of the derivative at x
	 */
	public T evaluatePolynomialDerivative (Polynomial.PowerFunction<T> f, T atX)
	{
		return getSupportFor (f).evaluatePolynomialDerivative (f.getCoefficients (), atX);
	}


	/**
	 * given a Polynomial Power Function with specified descriptor
	 *  compute the integral of the function at specified x and return value
	 * @param f the function descriptor holding the coefficients
	 * @param atX the value of x at which to evaluate 
	 * @return the value of the integral at x
	 */
	public T evaluatePolynomialIntegral (Polynomial.PowerFunction<T> f, T atX)
	{
		return getSupportFor (f).evaluatePolynomialIntegral (f.getCoefficients (), atX);
	}


	/**
	 * given a Polynomial Power Function with specified descriptor
	 *  compute the integral of the function over the specified interval
	 * @param f the function descriptor holding the coefficients
	 * @param fromX the lo value of the interval at which to evaluate
	 * @param toX the hi value of the interval at which to evaluate 
	 * @return the value of the integral at x
	 */
	public T evaluatePolynomialIntegral (Polynomial.PowerFunction<T> f, T fromX, T toX)
	{
		return getSupportFor (f).evaluatePolynomialIntegral (f.getCoefficients (), fromX, toX);
	}


	/**
	 * given a Polynomial Power Function with specified descriptor
	 *  compute the integral of the function over the specified intervals.
	 *  one lo end for all intervals specified by multiple upper interval endpoints.
	 * @param f the function descriptor holding the coefficients
	 * @param fromX the lo value of the intervals at which to evaluate
	 * @param toX the hi values of the intervals at which to evaluate 
	 * @return the value of the integral at x
	 */
	public List<T> evaluatePolynomialIntegral (Polynomial.PowerFunction<T> f, T fromX, List<T> toX)
	{
		return getSupportFor (f).evaluatePolynomialIntegral (f.getCoefficients (), fromX, toX);
	}


	/**
	 * produce the power function that is the derivative of the source
	 * @param f source polynomial to use to find the derivative
	 * @return the derivative power function
	 */
	public Function<T> getFunctionDerivative (Function<T> f)
	{
		Polynomial.PowerFunction<T> poly = (Polynomial.PowerFunction<T>)f;
		return getSupportFor (poly).getFunctionDerivative (f);
	}


	/**
	 * produce the power function that is the anti-derivative of the source
	 * @param f source polynomial to use to find the anti-derivative
	 * @return the anti-derivative power function
	 */
	public Function<T> getFunctionIntegral (Function<T> f)
	{
		Polynomial.PowerFunction<T> poly = (Polynomial.PowerFunction<T>)f;
		return getSupportFor (poly).getFunctionIntegral (f);
	}


}
