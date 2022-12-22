
package net.myorb.math.computational.iterative;

/**
 * descriptions of functions and their derivatives
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class IterationFoundations <T>
{

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

	public void setDelta (T delta) {
		this.delta = delta;
	}

	public T getDelta () {
		return delta;
	}

	T x = null, functionOfX = null, derivativeAtX = null, delta = null;


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


