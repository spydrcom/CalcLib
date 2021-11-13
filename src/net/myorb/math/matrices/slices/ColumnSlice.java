
package net.myorb.math.matrices.slices;

import net.myorb.math.matrices.MatrixAccess;

/**
 * provide vector access to one column of a matrix
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ColumnSlice<T> extends MatrixSlice<T>
{

	/**
	 * initialize configuration of access object
	 * @param access the parent matrix access object
	 * @param c the column number to be treated as a vector
	 * @param s the size of the vector
	 */
	public ColumnSlice (MatrixAccess<T> access, int c, int s) { super (access, c, s); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#set(int, java.lang.Object)
	 */
	public void set (int row, T value) { access.set (row, index, value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#get(int)
	 */
	public T get (int row) { return access.get (row, index); }

}