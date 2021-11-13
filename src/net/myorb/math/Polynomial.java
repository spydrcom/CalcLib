
package net.myorb.math;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.PolynomialFunctionWrapper;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

/**
 * a description of polynomial equations
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class Polynomial<T> extends MathSupport<T>
{

	public static class RealNumbers
		extends ExpressionFloatingFieldManager {}
	public static class RealPolynomial extends Polynomial<Double>
	{ public RealPolynomial () { super (new RealNumbers ()); } }

	/**
	 * a polynomial function is described with a set of coefficients
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface PowerFunction<T> extends Function<T>
	{
		/**
		 * access a Polynomial Space Management object for this data type
		 * @return the access to a Polynomial Space Management object
		 */
		PolynomialSpaceManager<T> getPolynomialSpaceManager ();

		/**
		 * get access to the coefficients object defining this function
		 * @return the coefficients object
		 */
		Coefficients<T> getCoefficients ();

		/**
		 * get access to the Polynomial object able to compute the sum of the terms
		 * @return access to the Polynomial object
		 */
		Polynomial<T> getPolynomial ();
		
		/**
		 * get the degree of the polynomial
		 * @return the degree of the polynomial
		 */
		int getDegree ();
	}


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public Polynomial
		(SpaceManager<T> manager)
	{ super (manager); }


	/**
	 * provide general descriptor of f(X) = c0 + c1*X + c2*x^2 + ...
	 * @param coefficient a series of coefficients in the pattern c0, c1, c2, ...
	 * @return the new polynomial function descriptor
	 */
	@SuppressWarnings("unchecked")
	public Polynomial.PowerFunction<T> functionOfX (T... coefficient)
	{ return getPolynomialFunction (newCoefficients (coefficient)); }


	/**
	 * provide general descriptor of f(X) = c1*X + c0
	 * @param coefficientOfX the coefficient of X in the function description
	 * @param constantCoefficient the coefficient representing the constant term
	 * @return the new polynomial function descriptor
	 */
	public Polynomial.PowerFunction<T> linearFunctionOfX (T coefficientOfX, T constantCoefficient)
	{ return getPolynomialFunction (newCoefficients (constantCoefficient, coefficientOfX)); }

	/**
	 * provide general descriptor of f(X) = c0 + c1*X + c2*x^2 + ...
	 * @param coefficient a series of coefficients in the pattern c0, c1, c2, ...
	 * @return the new polynomial function descriptor
	 */
	public Polynomial.PowerFunction<T> functionOfX (int... coefficient)
	{
		return getPolynomialFunction (coefficients (coefficient));
	}


	/**
	 * compute the value of a polynomial at specified X value
	 * @param coefficients the list of coefficients of the polynomial
	 * @param x the value of X to use for the evaluation
	 * @return the computed result
	 */
	public T evaluatePolynomial (Coefficients<T> coefficients, T x)
	{ return evaluatePolynomialV (coefficients, forValue (x)).getUnderlying (); }
	public Value<T> evaluatePolynomialV (Coefficients<T> coefficients, Value<T> x)
	{ return ordinary (coefficients, x); }


	/**
	 * a set of coefficients uniquely identify a function
	 * @param coefficients the Coefficients object holding the ordered list of values of type T
	 * @return the function implementation based on the Coefficients object
	 */
	public PowerFunction<T> getPolynomialFunction (Coefficients<T> coefficients)
	{ return new PolynomialFunctionWrapper <T> (this, coefficients, manager); }


	/**
	 * a manager that will operate on polynomials as a field
	 * @return the space manager for the polynomial field
	 */
	public PolynomialSpaceManager<T> getPolynomialSpaceManager ()
	{ return new PolynomialSpaceManager<T> (manager); }


}

