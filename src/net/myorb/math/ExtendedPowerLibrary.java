
package net.myorb.math;

/**
 * secondary power functions
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public interface ExtendedPowerLibrary<T>
	extends PowerLibrary<T>
{

	/**
	 * distance from origin to value
	 * @param x the value to determine
	 * @return computed magnitude
	 */
	T magnitude (T x);

	/**
	 * compute x^y for complex base and exponent
	 * @param x the base value for the computation
	 * @param y the exponent value for the computation
	 * @return computation result object
	 */
	T power (T x, T y);

	/**
	 * compute root of parameter
	 * @param x the parameter to the root function
	 * @param root thwe root to be computed
	 * @return the result of calculation
	 */
	T nThRoot (T x, int root);

	/**
	 * compute factorial of parameter
	 * @param value the base of the calculation
	 * @return result of computation
	 */
	T factorial (T value);

	/**
	 * compute parity factorial of parameter
	 * @param value the base of the calculation
	 * @return result of computation
	 */
	T dFactorial (T value);

}
