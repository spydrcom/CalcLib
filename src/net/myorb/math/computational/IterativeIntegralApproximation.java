
package net.myorb.math.computational;

import net.myorb.math.expressions.TypedRangeDescription;
import net.myorb.data.abstractions.Function;
//import net.myorb.math.Function;

/**
 * calculate function integral with iterative increase in precision.
 *  Use Trapezoid rule with a double of sample count for each iteration
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class IterativeIntegralApproximation<T>
		extends IterativeProcessingSupportTabular<T>
{


	/**
	 * basic parameters of the integral
	 * @param typedRange the description of the collected range data
	 * @param function the function being integrated
	 * @param silent TRUE : no output
	 */
	public IterativeIntegralApproximation
		(
			TypedRangeDescription.TypedRangeProperties<T> typedRange,
			Function<T> function, boolean silent
		)
	{
		this (function, typedRange.getTypedLo (), typedRange.getTypedHi (), silent);
	}
	public IterativeIntegralApproximation
		(
			Function<T> function, T lo, T hi, boolean silent
		)
	{
		super (function, silent? null: "Integration Iterator");
		this.ZERO = mgr.getZero (); this.HALF = mgr.invert (mgr.newScalar (2));
		initializeRange (lo, hi); initializeSum ();
		header ();
	}
	private T ZERO, HALF; 		// constants
	

	/**
	 * initialize parameters related to range
	 * @param lo the lo bound of the integration range
	 * @param hi the hi bound of the integration range
	 */
	public void initializeRange (T lo, T hi)
	{
		this.xAxisDelta = mgr.add (this.hi = hi, mgr.negate (this.lo = lo));
	}
	protected T lo, hi;			// lo and hi of specified range of integration


	/**
	 * initial sum includes HALF * (f (lo) + f (hi)) + f (lo + halfTick);
	 * where tick is (hi - lo)
	 */
	public void initializeSum ()
	{
		T halfPoint = setTickToFullRange ();									// halfPoint = lo + xAxisDelta/2
		this.priorTickSum = mgr.multiply (HALF, mgr.add (f (lo), f (hi)));		// Trapezoid rule:  f(lo) + f(hi)  // multiplied by HALF to avoid 2*f(x)
		this.priorTickSum = mgr.add (priorTickSum, f (halfPoint));				//  2 * f(mid) added to f(lo)+f(hi) gives initial sum (3 points, N = 2)
		this.samples = 2;														// <>X/2 * ( f(x[0]) + 2f(x[1]) + 2f(x[2]) + ... + 2f(x[N-1]) + f(x[N]) )
	}
	protected T priorTickSum;	// sum of processed ticks


	/**
	 * calculate center spots between previous ticks
	 * @param tick the distance of the space to be divided
	 * @return lo + halfTick
	 */
	public T setTick (T tick)
	{
		// lo + tick / 2 is the first tick of a new iteration;
		//  the previous tick having been twice the value in size
		return mgr.add (this.lo, this.halfTick = mgr.multiply (this.tick = tick, HALF));
	}
	public T setTickToFullRange () { return setTick (xAxisDelta); }
	public T setTickToHalf () { return setTick (halfTick); }
	public T nextTick (T x) { return mgr.add (x, tick); }
	private T tick, halfTick;	// current tick values


	/**
	 * @return (hi - lo) / (2*N)
	 */
	public T halfDeltaX ()
	{
		T N = mgr.newScalar (samples);							
		return mgr.multiply (xAxisDelta, mgr.invert (N));						// HALF already applied to each term
	}
	private T xAxisDelta;		// distance along X-axis


	/**
	 * approximation = sum * deltaX / 2
	 * deltaX = (hi - lo) / N
	 */
	public void setCurrentApproximation ()
	{
		T area = mgr.multiply (priorTickSum, halfDeltaX ());
		currentApproximation = mgr.multiply (area, areaMultiplier);
		showCurrentApproximation ();
	}
	private T areaMultiplier;


	/**
	 * execute a block of iterations
	 * @param iterations the count of iterations to be executed
	 * @param areaMultiplier a constant multiplier for the displayed area
	 */
	public void execute (int iterations, double areaMultiplier)
	{
		this.areaMultiplier = mgr.convertFromDouble (areaMultiplier);
		for (int remaining = iterations; remaining > 0; remaining--)
		{ executeIteration (); setCurrentApproximation (); }
		this.done ();
	}


	/**
	 * run iteration on tick half of previous
	 */
	public void executeIteration ()
	{
		T halfTickSum = executeIteration (setTickToHalf ());					// compute sum of half tick values
		priorTickSum = mgr.add (priorTickSum, halfTickSum);						// sum of f(ticks) + f(halfTicks)
		samples *= 2;															// twice the sample count now
	}


	/**
	 * execute a single iteration doubling the sample size
	 * @param x first tick value of new iteration (with new halfTick value)
	 * @return sum of iteration of half tick points
	 */
	public T executeIteration (T x)
	{
		T halfTickSum = ZERO;													// prepare to sum the half point ticks
		for (int n = samples; n > 0; n--)										// evaluate same number of pieces at new 1/2 tick point
		{
			halfTickSum = mgr.add (halfTickSum, f (x));							// sum function evaluations made at the new 1/2 tick point
			x = nextTick (x);													// step between 1/2 tick points is twice the size (tick value)
		}
		return halfTickSum;
	}


}


