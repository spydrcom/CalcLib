
package net.myorb.math;

/**
 * interface to a library for power functions
 * @param <T>  type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public interface PowerLibrary<T>
{

	/**
	 * compute natural log
	 * @param value parameter for computation
	 * @return computed result
	 */
	T ln (T value);

	/**
	 * compute e^x
	 * @param value parameter for computation
	 * @return computed result
	 */
	T exp (T value);

	/**
	 * compute value raised to integral exponent
	 * @param value the value to use as base of computation
	 * @param exponent the integral exponent value
	 * @return result of computation
	 */
	T pow (T value, int exponent);

	/**
	 * compute sqrt(x)
	 * @param value parameter for computation
	 * @return computed result
	 */
	T sqrt (T value);


}
