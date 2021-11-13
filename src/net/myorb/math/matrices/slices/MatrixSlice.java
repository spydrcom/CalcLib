
package net.myorb.math.matrices.slices;

import net.myorb.math.matrices.*;

/**
 * hold parameters for access object
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public abstract class MatrixSlice<T> implements VectorAccess<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#size()
	 */
	public int size () { return size; }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#resetSpan()
	 */
	public void resetSpan () { throw new RuntimeException ("N/A"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#nextSpan()
	 */
	public void nextSpan () { throw new RuntimeException ("N/A"); }

	/**
	 * initialize parameters for access object
	 * @param access the parent matrix access object
	 * @param index the stable index for the other parameter (row/col)
	 * @param size the size of the slice
	 */
	MatrixSlice (MatrixAccess<T> access, int index, int size)
	{ this.access = access; this.index = index; this.size = size; }

	MatrixAccess<T> access;
	int index, size;

}
