
package net.myorb.math;

import net.myorb.data.abstractions.ManagedSpace;

import java.util.List;

/**
 * description for multi-variable functions
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class MultiDimensional<T>
{

	/**
	 * a wrapper for passing procedures as parameters
	 * @param <T> type of component values on which operations are to be executed
	 * @author Michael Druckman
	 */
	public interface Function<T> extends ManagedSpace<T>
	{
		/**
		 * call the function given a parameter per variable
		 * @param x the ordered set of the parameters to the function
		 * @return the resulting value computed by the function
		 */
		@SuppressWarnings("unchecked")
		T f (T... x);

		/**
		 * call the function given a list of parameters per variable
		 * @param dataPoint the data point as a list of a parameter per variable
		 * @return the value of the function at the point
		 */
		T f (List<T> dataPoint);
	}

	/**
	 * describe a range of values in a domain of type T
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface Domain<T>
	{
		T getLo ();
		T getHi ();
	}
	
	/**
	 * describe a delta value in a domain of type T
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface Dx<T>
	{
		T getDelta ();
	}

	/**
	 * build a domain object
	 * @param lo the lo value
	 * @param hi the hi value
	 * @return the domain
	 */
	public Domain<T> buildDomain (T lo, T hi)
	{
		return new ParameterStorage<T> (lo, hi);
	}

	/**
	 * build a Dx object
	 * @param delta the delta value
	 * @return the Dx object
	 */
	public Dx<T> buildDx (T delta)
	{
		return new ParameterStorage<T> (delta);
	}

}


/**
 * getter/setter for parameters of MultiDimensional interfaces
 * @param <T> type of component values on which operations are to be executed
 */
class ParameterStorage<T> implements MultiDimensional.Domain<T>, MultiDimensional.Dx<T>
{
	T v1, v2;
	ParameterStorage (T delta) { v1 = delta; }
	ParameterStorage (T lo, T hi) { v1 = lo; v2 = hi; }
	public T getDelta () { return v1; }
	public T getLo () { return v1; }
	public T getHi () { return v2; }
}


