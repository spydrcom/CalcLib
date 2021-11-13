
package net.myorb.math.computational;

import net.myorb.math.expressions.TypedRangeDescription;

import net.myorb.math.Function;

/**
 * follow iterations of a series to see the pattern of convergence
 * @param <T> type of data being computed
 * @author Michael Druckman
 */
public class IterativeSeriesConvergence<T>
  extends IterativeProcessingSupportTabular<T>
{


	/**
	 * @param typedRange the description of the collected range data
	 * @param function the function describing a term
	 */
	public IterativeSeriesConvergence
	(TypedRangeDescription.TypedRangeProperties<T> typedRange, Function<T> function)
	{
		super (function, "Series Convergence Iterator");
		this.lo = typedRange.getTypedLo (); this.hi = typedRange.getTypedHi ();
		this.increment = typedRange.getTypedIncrement ();
		this.iteration = lo;
		header ();
	}
	protected T lo, hi, increment, iteration;


	/**
	 * run iterations over range
	 */
	public void executeIterations ()
	{
		samples = 0;
		currentApproximation = mgr.getZero ();

		while (!mgr.lessThan (hi, iteration))
		{
			mostRecentDelta = function.eval (iteration); samples++;
			currentApproximation = mgr.add (currentApproximation, mostRecentDelta);
			iteration = mgr.add (iteration, increment);
			showCurrentApproximation ();
		}

		this.done ();
	}


}

