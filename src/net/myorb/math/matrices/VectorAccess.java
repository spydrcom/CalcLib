
package net.myorb.math.matrices;

/**
 * provide access to spans treated as vectors
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface VectorAccess<T>
{

	/**
	 * get the count of elements
	 * @return count of elements
	 */
	public int size ();

	/**
	 * get the value of a element
	 * @param index the index number
	 * @return the element value
	 */
	public T get (int index);

	/**
	 * move access point to next span
	 */
	public void nextSpan ();

	/**
	 * reset starting point
	 */
	public void resetSpan ();

	/**
	 * update an element
	 * @param index the index of the element to change
	 * @param value the new value for the element
	 */
	public void set (int index, T value);

	/**
	 * set all elements to value
	 * @param value the value to use
	 */
	public void fill (T value);

}
