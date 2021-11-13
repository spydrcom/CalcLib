
package net.myorb.math.matrices.slices;

import net.myorb.math.matrices.*;

/**
 * provide vector access to one row of a matrix
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class RowSlice<T> extends MatrixSlice<T>
{

	/**
	 * initialize configuration of access object
	 * @param access the parent matrix access object
	 * @param r the row number to be treated as a vector
	 * @param s the size of the vector
	 */
	public RowSlice (MatrixAccess<T> access, int r, int s) { super (access, r, s); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#set(int, java.lang.Object)
	 */
	public void set (int col, T value) { access.set (index, col, value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#get(int)
	 */
	public T get (int col) { return access.get (index, col); }

}
