
package net.myorb.math;

/**
 * a grouped set of operations that provide the definitive structure of a mathematical field
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface FieldStructure<T>
{

	/**
	 * add two values (+)
	 * @param left first term
	 * @param right second term
	 * @return sum of terms
	 */
	T addition (T left, T right);

	/**
	 * multiply two values (*)
	 * @param left first factor
	 * @param right second factor
	 * @return product of factors
	 */
	T multiplication (T left, T right);

	/**
	 * compute that which adds with value producing additive identity
	 * @param value the quantity searching for an inverse
	 * @return the inverse of the value
	 */
	T additiveInverse (T value);

	/**
	 * compute that which multiplies with value producing multiplicative identity
	 * @param value the quantity searching for an inverse
	 * @return the inverse of the value
	 */
	T multiplicativeInverse (T value);

	/**
	 * the value for which X + value = X for all X in space
	 * @return the identity value
	 */
	T additiveIdentity ();

	/**
	 * the value for which X * value = X for all X in space
	 * @return the identity value
	 */
	T multiplicativeIdentity ();

}
