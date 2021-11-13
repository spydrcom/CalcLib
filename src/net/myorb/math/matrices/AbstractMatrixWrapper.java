
package net.myorb.math.matrices;

import net.myorb.math.matrices.slices.*;

/**
 * set default behaviors for implementations of matrix access
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public abstract class AbstractMatrixWrapper<T> implements MatrixAccess<T>
{

	public AbstractMatrixWrapper () {}

	/**
	 * set the row and column counts
	 * @param rowCount the number of elements in a row
	 * @param columnCount elements in a column
	 */
	public void setCounts (int rowCount, int columnCount)
	{
		this.rowCount = rowCount;
		this.columnCount = columnCount;
	}

	/**
	 * for square matrix a single size parameter sets row and column counts
	 * @param edgeCount the size of both rows and columns
	 */
	public void setSquareCounts (int edgeCount)
	{
		this.rowCount = edgeCount;
		this.columnCount = edgeCount;
	}

	/**
	 * constructor for square matrix types
	 * @param squareCount rows and columns are same value
	 */
	public AbstractMatrixWrapper (int squareCount)
	{
		this (squareCount, squareCount);
	}

	/**
	 * constructor for generic matrix
	 * @param rowCount count of rows in the matrix
	 * @param columnCount count of columns
	 */
	public AbstractMatrixWrapper (int rowCount, int columnCount)
	{
		this.rowCount = rowCount;
		this.columnCount = columnCount;
	}
	protected int rowCount, columnCount;

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#rowCount()
	 */
	public int rowCount () { return rowCount; }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#columnCount()
	 */
	public int columnCount () { return columnCount; }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getRowAccess(int)
	 */
	public VectorAccess<T> getRowAccess (int row)
	{
		return new RowSlice<T> (this, row, columnCount);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getColAccess(int)
	 */
	public VectorAccess<T> getColAccess (int col)
	{
		return new ColumnSlice<T> (this, col, rowCount);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#set(int, int, java.lang.Object)
	 */
	public void set (int row, int col, T value)
	{ throw new RuntimeException ("Access object does not support SET functionality"); }

}
