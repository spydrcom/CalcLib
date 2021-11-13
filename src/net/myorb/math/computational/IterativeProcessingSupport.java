
package net.myorb.math.computational;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.data.abstractions.Function;
//import net.myorb.math.Function;

import java.io.PrintStream;

/**
 * follow series iterations looking for convergence pattern
 * @param <T> type of data being computed
 * @author Michael Druckman
 */
public class IterativeProcessingSupport<T>
{


	/**
	 * keep track of time spent in calculation
	 */
	protected long start = getCurrent ();


	/**
	 * number of pieces the range has been broken into
	 */
	protected int samples;


	/**
	 * evaluation of function at parameter
	 * @param x the parameter to the function bounded by lo:hi range
	 * @return the value of the function at the specified parameter
	 */
	public T f (T x)
	{ return function.eval (x); }
	protected Function<T> function;		// access to the function of given type


	/**
	 * get the calculation result
	 * @return the last iteration result
	 */
	public T getResult ()
	{ return currentApproximation; }
	protected T currentApproximation = null;
	protected T mostRecentDelta = null;


	/**
	 * get the estimated time required for calculations
	 * @return count of milli-seconds since calculations started
	 */
	public long getDuration () { return getCurrent () - start; }
	public static long getCurrent () { return System.currentTimeMillis (); }
	public String timeStamp () { return Long.toString (getDuration ()); }


	/**
	 * display a header to the print stream
	 */
	public void header ()
	{
		if (out == null) return;
		out.println ("Count \tChange \tResult \tms");
		out.println ("===== \t====== \t====== \tms");
	}
	protected PrintStream out;								// the output of the results


	/**
	 * format display of current value of approximation
	 */
	public void showCurrentApproximation ()
	{
		T change = mostRecentDelta;
		unchanged = mgr.isZero (change);
		double count = samples; String scale = "";
		if (count >= M) { count /= M; scale = "M"; }
		else if (count >= K) { count /= K; scale = "K"; }
		if (change == null) change = mgr.add (currentApproximation, lastSeen);
		lastSeen = mgr.negate (currentApproximation);
		mostRecentDelta = null;

		showCurrentApproximation
		(
			Integer.toString ((int) count) + scale,
			mgr.format (currentApproximation), mgr.format (change)
		);
	}
	protected int K = 1024, M = K * K;
	protected boolean unchanged;
	protected T lastSeen;


	/**
	 * default display is print stream
	 * @param count number of iterations
	 * @param approx the approximation
	 * @param change delta from last 
	 */
	public void showCurrentApproximation
	(String count, String approx, String change)
	{
		if (out == null) return;
		out.print (count); out.print ("\t");
		out.println (change); out.print ("\t");
		out.println (approx); out.print ("\t");
		out.print (getDuration ());
	}


	/**
	 * indicate processing termination
	 */
	public void done ()
	{
		if (out == null) return;
		out.println ("===");
	}


	/**
	 * @param function the function being evaluated
	 * @param out the print stream for output
	 */
	public IterativeProcessingSupport
	(Function<T> function, PrintStream out)
	{
		this.function = function;
		this.mgr = (ExpressionSpaceManager<T>) function.getSpaceDescription ();
		this.lastSeen = mgr.getZero ();
		this.out = out;
	}
	protected ExpressionSpaceManager<T> mgr;							// description of the data type

}

