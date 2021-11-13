
package net.myorb.math.matrices;

import net.myorb.math.matrices.optimization.MinorMatrixComputationTask;

/**
 * principle methods used to represent access to matrix objects
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface MatrixAccess<T>
{

	/**
	 * get the count of rows
	 * @return count of rows
	 */
	public int rowCount ();

	/**
	 * get the count of columns
	 * @return count of columns
	 */
	public int columnCount ();

	/**
	 * set the value of a cell
	 * @param row the row number
	 * @param col the column number
	 * @param value the new cell value
	 */
	public void set (int row, int col, T value);

	/**
	 * get the value of a cell
	 * @param row the row number
	 * @param col the column number
	 * @return the cell value
	 */
	public T get (int row, int col);

	/**
	 * provide vector access to specified row
	 * @param row the row number for which access is to be provided
	 * @return a vector access object
	 */
	public VectorAccess<T> getRowAccess (int row);

	/**
	 * provide vector access to specified column
	 * @param col the column number for which access is to be provided
	 * @return a vector access object
	 */
	public VectorAccess<T> getColAccess (int col);

	/**
	 * build access to a set of minor matrix objects subordinate to this parent.
	 * @param row the row number to be eliminated
	 * @param column the column number chosen
	 * @return access to the minor matrix
	 */
	public MatrixAccess<T> getMinor (int row, int column);

	/**
	 * build access to a set of minor matrix objects subordinate to this parent.
	 * @param column the column number to be eliminated
	 * @return access to the minor matrix set of rows
	 */
	public MinorAccess<T> getMinor (int column);

	/**
	 * wrap matrix representation in an optimized object
	 * @param wrapperFactory a factory that is able to construct matrix wrappers
	 * @return optimized object representation
	 */
	public MinorMatrixComputationTask<T> getOptimizedAccess (FullMatrixWrapper<T> wrapperFactory);

}
