
package net.myorb.math;

/**
 * methods for management of tolerances
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class Tolerances<T> extends ListOperations<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public Tolerances
	(SpaceManager<T> manager)
	{ super (manager); }


	/**
	 * set Tolerance Parameters to Default values
	 * @param lib an implementation of the power library
	 */
	public void setToleranceDefaults (PowerLibrary<T> lib)
	{ setLibrary (lib); setToleranceDefaultParameters (); }


	/**
	 * set Tolerance Parameters to Default values
	 */
	public void setToleranceDefaultParameters ()
	{
		this.TEN = forValue (10);
		this.epsilon = TEN.pow (-14);
		this.tolerance = TEN.pow (-8);
		this.inflectionOffset = TEN.pow (-1);
		this.maxIterations = 50;
	}
	protected Value<T> epsilon, tolerance, inflectionOffset;
	protected int maxIterations;
	protected Value<T> TEN;


	// get a value of a parameter
	public T getEpsilon () { return epsilon.getUnderlying (); }
	public T getTolerance () { return tolerance.getUnderlying (); }
	public int getMaxIterations () { return maxIterations; }


	/**
	 * use a parameterized portion of a value as an offset to avoid zero derivatives
	 * @param value the value of the derivative zero root to be offset as a root approximation
	 * @return the proportional value relative to the parameter
	 */
	public T offsetFromZeroAt (T value) { return inflectionOffset.times (abs (forValue (value))).getUnderlying (); }

	// set parameters based on scale
	public void setToleranceScale (int scale) { tolerance = TEN.pow (-scale); }
	public void setEpsilonScale (int scale) { epsilon = TEN.pow (-scale); }


	/**
	 * set values to parameters used to determine approximations of roots
	 * @param epsilon the smallest allowed value of the derivative to avoid division by zero
	 * @param tolerance the necessary proximity to the root to consider search successful
	 * @param inflectionOffset use x axis offset from derivative zero as approximation of root
	 * @param maxIterations the maximum number of iterations allowed in determination
	 */
	public void setRootDeterminationParameters
		(T epsilon, T tolerance, T inflectionOffset, int maxIterations)
	{
		this.epsilon = forValue (epsilon);
		this.tolerance = forValue (tolerance);
		this.inflectionOffset = forValue (inflectionOffset);
		this.maxIterations = maxIterations;
	}


	/**
	 * determine range of a value relative to a threshold
	 * @param value the value being check for proximity to threshold
	 * @param threshold the value of the threshold to consider "nearness"
	 * @return TRUE = value is within range relative to threshold
	 */
	public boolean withinRange (Value<T> value, Value<T> threshold)
	{
		if (value == null) return true;
		return abs (value).isLessThan (threshold);
	}
	public boolean withinTolerance (Value<T> value) { return withinRange (value, tolerance); }
	public boolean withinTolerance (T value) { return withinTolerance (forValue (value)); }


}
