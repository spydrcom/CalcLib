
package net.myorb.math.complexnumbers;

import net.myorb.math.ExtendedPowerLibrary;

/**
 * extend JRE library use to generic types that can have real representation
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public interface ComplexSupportLibrary<T>
	extends ExtendedPowerLibrary<T>
{

	/**
	 * @param t parameter to function
	 * @return function result
	 */
	T sin (T t);

	/**
	 * @param t parameter to function
	 * @return function result
	 */
	T cos (T t);

	/**
	 * @param t parameter to function
	 * @return function result
	 */
	T sinh (T t);

	/**
	 * @param t parameter to function
	 * @return function result
	 */
	T cosh (T t);

	/**
	 * @param x axis parameter to function
	 * @param y axis parameter to function
	 * @return computed result
	 */
	T atan (T x, T y);

}
