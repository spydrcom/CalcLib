
package net.myorb.math.computational;

import net.myorb.data.abstractions.Function;

/**
 * calculate function root with iterative adjustment specified by Newton's method.
 *  first order Newton-Raphson takes just 1st derivative, Newton 2nd order requires 2nd derivative.
 * @param <T> data type for calculations
 * @author Michael Druckman
 */
public class IterativeRootApproximation<T>
  extends IterativeProcessingSupportTabular<T>
{


	/**
	 * @param function the function describing a term
	 * @param derivative the derivative of the function
	 */
	public IterativeRootApproximation
	(Function<T> function, Function<T> derivative)
	{
		super (function, "Root Approximation Iterator");
		this.secondDerivative = null;
		this.derivative = derivative;
		header ();
	}


	/**
	 * @param function the function describing a term
	 * @param derivative the derivative of the function
	 * @param secondDerivative 2nd derivative for 2nd order formula
	 */
	public IterativeRootApproximation
	(Function<T> function, Function<T> derivative, Function<T> secondDerivative)
	{ this (function, derivative); this.secondDerivative = secondDerivative; }


	/**
	 * evaluation of function derivative at parameter
	 * @param x the parameter to the function to be calculated
	 * @return the value of the function at the specified parameter
	 */
	public T fPrime (T x)
	{ return derivative.eval (x); }
	protected Function<T> derivative;			// access to the function derivative


	/**
	 * evaluation of second derivative at parameter
	 * @param x the parameter to the function to be calculated
	 * @return the value of the function at the specified parameter
	 */
	public T fPrime2 (T x)
	{ return secondDerivative.eval (x); }
	protected Function<T> secondDerivative;		// access to the function second derivative


	/**
	 * apply adjustment for one iteration
	 * @param approximation current approximation of root
	 * @return x - f(x)/f'(x) { possibly adjusted for 2nd order }
	 */
	public T iterationAdjustment (T approximation)
	{
		T x = approximation;
		T fOfX = f (x), slope = fPrime (x);
		if (secondDerivative != null) slope =				// 2nd order adjustment
			iterationAdjustment2 (slope, fOfX, x);			// in presence of 2nd derivative
		T ratio = mgr.multiply (fOfX, mgr.invert (slope));
		return mgr.add (x, mostRecentDelta = mgr.negate (ratio));
	}


	/**
	 * second order adjustment
	 * @param firstDerivative evaluated first derivative
	 * @param fOfX function evaluated at approximated root
	 * @param x current approximation of root
	 * @return adjusted slope of function
	 */
	public T iterationAdjustment2 (T firstDerivative, T fOfX, T x)
	{
		// f'(x) - [ f''(x) * f(x) / (2 * f'(x)) ]
		T fPrimeOfX2 = mgr.multiply (mgr.newScalar (2), firstDerivative);
		T ratio = mgr.multiply (mgr.multiply (fPrime2 (x), fOfX), mgr.invert (fPrimeOfX2));
		return mgr.add (firstDerivative, mgr.negate (ratio));
	}


	/**
	 * run series of iterations
	 * @param initialApproximation the initial value of the approximation
	 * @param iterationCount the number of iterations to be run
	 */
	public void executeIterations (double initialApproximation, int iterationCount)
	{
		samples = 0;
		currentApproximation = mgr.convertFromDouble (initialApproximation);
		for (int remaining = iterationCount; remaining > 0; remaining--)
		{
			samples++;
			currentApproximation = iterationAdjustment (currentApproximation);
			showCurrentApproximation ();
			if (unchanged) break;
		}
		this.done ();
	}


}

