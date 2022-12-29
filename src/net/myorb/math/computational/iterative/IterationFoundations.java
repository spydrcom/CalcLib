
package net.myorb.math.computational.iterative;

import net.myorb.math.SpaceManager;

/**
 * descriptions of functions and their derivatives
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class IterationFoundations <T>
{

	/**
	 * adjust intermediate results to reduce overhead
	 * @param <T> data type being processed
	 */
	public interface PrecisionAdjustment <T>
	{
		/**
		 * apply adjustment to an intermediate result
		 * @param termValue the intermediate result to be adjusted
		 * @return the adjusted intermediate result value
		 */
		T adjust (T termValue);
	}

	/**
	 * adjust intermediate results to specified limit
	 * @param <T> data type being processed
	 */
	public interface PrecisionRestriction <T>
	{
		/**
		 * apply adjustment to an intermediate result
		 * @param termValue the intermediate result to be adjusted
		 * @param toLimit the most decimal places of precision
		 * @return the adjusted intermediate result value
		 */
		T adjust (T termValue, int toLimit);
	}

	/**
	 * identify summation termination conditions
	 * @param <T> data type being processed
	 */
	public interface ShortCircuit <T>
	{
		/**
		 * check intermediate result for loop ending conditions
		 * @param termValue the intermediate result to be evaluated
		 * @return TRUE when the summation should be completed
		 */
		boolean terminateSummation (T termValue);
	}


	/*
	 * Java Bean Get/Set methods for the properties
	 */

	public T getX () {
		return x;
	}

	public void setX (T x) {
		this.x = x;
	}

	public T getFunctionOfX () {
		return functionOfX;
	}

	public void setFunctionOfX (T functionOfX) {
		this.functionOfX = functionOfX;
	}

	public T getDerivativeAtX () {
		return derivativeAtX;
	}

	public void setDerivativeAtX (T derivativeAtX) {
		this.derivativeAtX = derivativeAtX;
	}

	public void setDelta (T delta)
	{
		if (precisionCheck != null)
		{ delta = precisionCheck.adjust (delta); }
		this.delta = delta;
	}

	public T getDelta () {
		return delta;
	}

	T x = null, functionOfX = null, derivativeAtX = null, delta = null;


	/*
	 * precision based loop control
	 */

	/**
	 * establish a precision check algorithm
	 * @param precisionCheck the object to use
	 */
	public void setPrecisionCheck
	(PrecisionAdjustment <T> precisionCheck) { this.precisionCheck = precisionCheck; }
	protected PrecisionAdjustment <T> precisionCheck = null;

	/**
	 * establish a Short Circuit algorithm
	 * @param shortCircuit the object to use
	 */
	public void setShortCircuit
	(ShortCircuit <T> shortCircuit) { this.shortCircuit = shortCircuit; }
	protected ShortCircuit <T> shortCircuit = null;

	/**
	 * check term value for Short Circuit conditions
	 * @param termValue the value of the intermediate value
	 * @param atIteration the count of iterations when this value is seen
	 * @param mgr the data type manager for the term value computed
	 * @throws ShortCircuitTermination for condition met
	 */
	public void testForShortCircuit
		(T termValue, int atIteration, SpaceManager <T> mgr)
	throws ShortCircuitTermination
	{
		if (shortCircuit != null && shortCircuit.terminateSummation (termValue))
		{
			String message = "term value was " + mgr.toDecimalString (termValue);
			throw new ShortCircuitTermination (message, atIteration);
		}
	}
	public static class ShortCircuitTermination extends Throwable
	{
		public ShortCircuitTermination () { super (MESSAGE); }
		public ShortCircuitTermination (String message, int iteration)
		{ super (MESSAGE + " : " + message + ", at iteration " + iteration); }
		private static final String MESSAGE = "Short circuit conditions met";
		private static final long serialVersionUID = 1670961473218425868L;
	}


	/**
	 * apply monitor restrictions
	 * @param restriction the implementation of the precision reduction mechanism
	 * @param mgr a space manager for the data type being used
	 * @param precision the limit of decimal places to use
	 */
	public void installPrecisionMonitor
		(
			IterationFoundations.PrecisionRestriction <T> restriction,
			SpaceManager <T> mgr, int precision
		)
	{
		this.installedMonitor = new PrecisionMonitor <T> (this, restriction, mgr, precision);
	}
	protected PrecisionMonitor <T> installedMonitor;


	/*
	 * display compilation methods
	 */

	/**
	 * @param buffer the buffer being compiled
	 * @param label the label that describes a field
	 * @param value the value of the field
	 */
	public void add (StringBuffer buffer, String label, T value)
	{
		if (value == null) return;
		buffer.append (label).append (toString (value)).append ("\n");
	}

	/**
	 * an optional method for override allowing addition of extra display content
	 * @param buffer the buffer being compiled
	 */
	public void add (StringBuffer buffer) {}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		StringBuffer buffer = new StringBuffer ();
		buffer.append ("X = ").append (toString (x)).append ("\n");
		add (buffer, "f(x) = ", functionOfX); add (buffer, "f'(x) = ", derivativeAtX);
		add (buffer, "delta = ", delta); add (buffer);
		return buffer.toString ();
	}

	/**
	 * specify the formatting of values
	 * @param x a value to be formatted
	 * @return the text for display of the value
	 */
	public String toString (T x) { return x.toString (); }


	/**
	 * show intermediate results
	 */
	public void trace ()
	{
		if (!tracing) return;
		System.out.println (this);
		System.out.println ();
	}
	public void enableTracing () { this.tracing = true; }
	boolean tracing = false;


}


/**
 * common algorithms for Precision Manipulation implementation
 * @param <T> data type used
 */
class PrecisionMonitor <T>
{

	PrecisionMonitor
		(
			IterationFoundations <T> iterator,
			IterationFoundations.PrecisionRestriction <T> restriction,
			SpaceManager <T> mgr,
			int maxPrecision
		)
	{
		this.mgr = mgr;
		this.restriction = restriction;

		this.establishPrecisionMonitor
		(maxPrecision, iterator);
	}
	IterationFoundations.PrecisionRestriction <T> restriction;
	SpaceManager <T> mgr;

	void establishPrecisionMonitor
	(int precision, IterationFoundations <T> iterator)
	{
		this.precision = precision;
		this.limit = mgr.pow (mgr.newScalar (10), -precision);

		iterator.setPrecisionCheck ( x -> adjustForPrecision (x) );
		iterator.setShortCircuit ( (x) -> isDone (x) );
	}

	boolean isDone (T x)
	{
		if (mgr.isZero (x)) return false;
		return mgr.lessThan (abs (x), limit);
	}
	T abs (T x)
	{
		if (mgr.isNegative (x))
		{ return mgr.negate (x); }
		else return x;
	}
	T limit;

	T adjustForPrecision (T x)
	{
		if (mgr.isZero (x)) return x;
		T adjusted = restriction.adjust (x, precision);
		return adjusted;
	}
	int precision;

}

