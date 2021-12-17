
package net.myorb.math.specialfunctions;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.SpaceManager;

/**
 * static entry points for special functions
 * @author Michael Druckman
 */
public class Library
{


	/**
	 * describe a term for a summation
	 * @param <T> data type used
	 */
	public interface Term<T>
	{
		/**
		 * @param k the order of the term
		 * @param z the value of the variable in the term
		 * @return the calculated term value
		 */
		T eval (int k, T z);
	}


	/**
	 * @param lo the starting value
	 * @param hi the largest to be evaluated
	 * @param z the function parameter value
	 * @param t a Term object for the summation
	 * @param sm a manager for the type
	 * @return the sum of the terms
	 * @param <T> data type used
	 */
	public static <T> T summation
	(int lo, int hi, T z, Term<T> t, SpaceManager<T> sm)
	{
		T sum = sm.getZero ();
		for (int k = lo; k <= hi; k++)
		{ sum = sm.add (sum, t.eval (k, z)); }
		return sum;
	}


	/**
	 * @param from polynomial manager identifying function type
	 * @return the expression manager for the type
	 * @param <T> data type manager
	 */
	public static <T> ExpressionSpaceManager<T> getExpressionManager (PolynomialSpaceManager<T> from)
	{
		return toExpressionManager (from.getSpaceDescription ());
	}

	/**
	 * @param from primitive manager identifying function type
	 * @return the expression manager for the type
	 * @param <T> data type manager
	 */
	public static <T> ExpressionSpaceManager<T> toExpressionManager (SpaceManager<T> from)
	{
		return (ExpressionSpaceManager<T>) from;
	}

	/**
	 * @param n an arbitrary number object
	 * @return TRUE for integer type values
	 */
	public static boolean isInteger (Number n)
	{
		return n.doubleValue () == n.intValue ();
	}

	/**
	 * even is positive, odd is negative
	 * @param n an integer
	 * @return (-1)^n
	 */
	public static int alternatingSign (int n)
	{
		return n % 2 == 1 ? -1 : 1;
	}

	/**
	 * even is positive, odd is negative
	 * @param n integer value for sign basis
	 * @param sm the type manager for the result type
	 * @return 1 for even, -1 for odd
	 * @param <T> data type manager
	 */
	public static <T> T signT (int n, SpaceManager<T> sm)
	{
		return sm.newScalar (alternatingSign (n));
	}
	public static <T> T signT (T forValue, int basedOn, SpaceManager<T> sm)
	{
		return alternatingSign (basedOn) < 0 ? sm.negate (forValue) : forValue;
	}

	/**
	 * integer factorial computation
	 * @param n an integer wrapped as number
	 * @return n!
	 */
	public static Number factorial (Number n)
	{
		double result = 1;
		for (int i = n.intValue (); i > 1; i--) result *= i;
		return result;
	}

	public static double factorial (int x)
	{
		double result = 1, next = x;
		for (int i=2; i<=x; i++) { result *= next; next -= 1; }
		return result;
	}

	/**
	 * compute integer factorial
	 * @param n the integer to use a parameter to factorial function
	 * @param sm the type manager for the result type
	 * @return integer factorial of n
	 * @param <T> data type manager
	 */
	public static <T> T factorialT (int n, SpaceManager<T> sm)
	{
		return toExpressionManager (sm).convertFromDouble (factorial (n));
	}

	/**
	 * compute real value ^ power
	 * @param value the base for the computation
	 * @param power the exponent for the computation
	 * @param esm the type manager for the result type
	 * @return the exponentiation of the value
	 * @param <T> data type manager
	 */
	public static <T> T realPower
		(
			T value, T power,
			ExpressionSpaceManager<T> esm
		)
	{
		double result = 0, exponent = 0,
		base = esm.convertToDouble (value);
		if (base == 0) return esm.getZero ();
		exponent = esm.convertToDouble (power);
		result = Math.exp (exponent * Math.log (base));
		return esm.convertFromDouble (result);
	}

	/**
	 * error function
	 * @param x parameter to function
	 * @return result of function evaluation
	 */
	public static double erf (double x)
	{
		if (errorFunction == null)
			errorFunction = new Erf ();
		return errorFunction.eval (x);
	}
	static Erf errorFunction = null;

	/**
	 * gamma function
	 * @param x parameter to function
	 * @return result of function evaluation
	 */
	public static double gamma (double x)
	{
		if (gammaFunction == null)
			gammaFunction = new Gamma ();
		try { return gammaFunction.eval (x); }
		catch (Exception e) { return Double.POSITIVE_INFINITY; }
	}
	static Gamma gammaFunction = null;

	public static double gammaIncompleteLower (double s, double x)
	{
		if (gammaFunction == null)
			gammaFunction = new Gamma ();
		if (gammaIncomplete == null)
			gammaIncomplete = new GammaIncomplete (gammaFunction);
		return gammaIncomplete.lower (s, x);
	}
	static GammaIncomplete gammaIncomplete = null;

	public static double gammaIncompleteUpper (double s, double x)
	{
		return gamma (s) - gammaIncompleteLower (s, x);
	}

	/**
	 * gamma function of real value
	 * @param value the real value to use a parameter to gamma function
	 * @param sm the type manager for the result type
	 * @return the gamma computation for value
	 * @param <T> data type manager
	 */
	public static <T> T gammaT (double value, SpaceManager<T> sm)
	{
		return toExpressionManager (sm).convertFromDouble (gamma (value));
	}

	/**
	 * gamma function derivative
	 * @param x parameter to function
	 * @return result of function evaluation
	 */
	public static double gammaPrime (double x)
	{
		return (gamma (x + dx) - gamma (x)) / dx;
	}
	static final double dx = 0.0000001;
	//static final double dx = 0.0001;

	/**
	 * digamma function
	 * @param x parameter to function
	 * @return result of function evaluation
	 */
	public static double digamma (double x)
	{
		return gammaPrime (x) / gamma (x);
	}

	/**
	 * ei (Exponential Integral)
	 * @param x parameter to function
	 * @return result of function evaluation
	 */
	public static double ei (double x)
	{
		return ExponentialIntegral.Ei (x);
	}

	/**
	 * e1 (Exponential Integral)
	 * @param x parameter to function
	 * @return result of function evaluation
	 */
	public static double e1 (double x)
	{
		return ExponentialIntegral.E1 (x);
	}

	/**
	 * li (Log Integral)
	 * @param x parameter to function
	 * @return result of function evaluation
	 */
	public static double li (double x)
	{
		return ExponentialIntegral.li (x);
	}

	/**
	 * Hyper Geometric Function
	 * @param a standard function constant
	 * @param b standard function constant
	 * @param c standard function constant
	 * @param x standard function parameter
	 * @return function evaluated at x
	 */
	public static double F (double a, double b, double c, double x)
	{
		return HyperGeometricFunction.F (a, b, c, x);
	}
	public static double M (double a, double c, double x)
	{
		return HyperGeometricFunction.M (a, c, x);
	}
	public static double U (double a, double c, double x)
	{
		return HyperGeometricFunction.U (a, c, x);
	}

}
