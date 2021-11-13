
package net.myorb.math;

/**
 * 
 * provide a mechanism that will reduce term values by cancelation of common factors
 * 
 * @author Michael Druckman
 *
 * @param <T> the type of term values that are to be reduced
 * 
 */
public interface ReductionMechanism<T>
{
	/**
	 * reduce the parameter value object by common factor analysis
	 * @param value value to be reduced
	 */
	void reduce (T value);
}
