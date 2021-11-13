
package net.myorb.math.matrices;

import net.myorb.math.matrices.optimization.CofactorComputationTaskFactory;
import net.myorb.math.matrices.optimization.MinorMatrixComputationTask;

import java.util.Arrays;
import java.util.List;

/**
 * a wrapper for matrix class objects allowing minor matrix access modeled on top of full matrix.
 * this allows reduced object creation and no copying of content into minor matrix objects
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MinorMatrixWrapper<T> extends AbstractMatrixWrapper<T> implements MinorAccess<T>
{


	/**
	 * a constructor for recursive minor matrix descriptors
	 * @param parent the parent minor matrix wrapper
	 */
	public MinorMatrixWrapper (MinorMatrixWrapper<T> parent)
	{
		super (parent.size - 2);
		this.allocateMajorAndMinorIndicies ();
		this.majorIndicies = parent.minorIndicies;

		this.columnOffset = new int[parent.columnOffset.length];
		this.eliminatedColumns = new boolean[parent.eliminatedColumns.length];
		System.arraycopy (parent.eliminatedColumns, 0, this.eliminatedColumns, 0, parent.eliminatedColumns.length);
		System.arraycopy (parent.columnOffset, 0, this.columnOffset, 0, parent.columnOffset.length);

		this.cells = parent.cells;
	}
	protected int majorIndicies[];
	protected List<T> cells;


	/**
	 * coordinate sets of major and minor indicies
	 */
	private void allocateMajorAndMinorIndicies ()
	{
		this.size = super.rowCount + 1;									// row count reflects minor matrix
		this.minorIndicies = new int[this.size - 1];					//  minor is one dimension smaller
		this.allocateColumnManagementObjects ();
	}
	protected int minorIndicies[], size;


	/**
	 * initialize column elimination management
	 */
	private void allocateColumnManagementObjects ()
	{
		this.eliminatedColumns = new boolean[size];
		Arrays.fill (this.eliminatedColumns, false);
	}
	protected boolean eliminatedColumns[];


	/**
	 * the top level wrapper for the full size matrix
	 * @param m the matrix object to be wrapped as a minor with column eliminated
	 * @param columnToEliminate column number to be eliminated
	 */
	public MinorMatrixWrapper (Matrix<T> m, int columnToEliminate)
	{
		super (m.rowCount () - 1);
		this.allocateMajorAndMinorIndicies ();
		this.majorIndicies = new int[this.size];
		this.columnOffset = new int[this.size];

		for (int i = 0; i < size; i++)
		{
			this.majorIndicies[i] = m.cellNumber (i + 1, 1);		// point to first column of row and accept column offset will adjust
			this.columnOffset[i] = i;								// with none eliminated, the map is transparent
		}

		this.eliminateColumn (columnToEliminate);
		this.cells = m.cells;
	}
	protected int columnOffset[];


	/**
	 * the top level wrapper for the full size matrix
	 * @param m the matrix object to be wrapped as a minor with eliminated row/column
	 * @param rowToEliminate this row number will be kept in major indicies list, eliminated in minor indices
	 * @param columnToEliminate column number to be eliminated
	 */
	public MinorMatrixWrapper (Matrix<T> m, int rowToEliminate, int columnToEliminate)
	{
		this (m, columnToEliminate);
		eliminateRow (rowToEliminate);
	}


	/**
	 * the top level wrapper for the full size matrix.
	 *  column 1 is assumed to be intended eliminated to
	 *  accomodate cofactor expansion accelerated algorithm
	 * @param m the matrix object to be wrapped
	 */
	public MinorMatrixWrapper (Matrix<T> m)
	{
		this (m, 1);
	}


	/**
	 * mark column as eliminated and compute offset list
	 * @param column the column to be eliminated
	 */
	public void eliminateColumn (int column)
	{
		int c = 0, colOffset = 0;
		eliminatedColumns[columnOffset[column-1]] = true;
		while (c < columnCount)
		{
			if (!eliminatedColumns[colOffset])
			{ this.columnOffset[c++] = colOffset; }
			colOffset++;
		}
	}


	/**
	 * mark row as absent in minor representation
	 * @param row the number of row to be eliminated
	 */
	public void eliminateRow (int row)
	{
		int remove = row - 1;
		for (int major = 0, minor = 0; major < size; major++)
		{
			if (major == remove) continue;
			minorIndicies[minor++] = majorIndicies[major];
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MinorAccess#getMinorAbsent(int)
	 */
	public MatrixAccess<T> getMinorAbsent (int row)
	{
		eliminateRow (row);
		return this;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#get(int, int)
	 */
	public T get (int row, int col)
	{
		return cells.get (minorIndicies[row - 1] + columnOffset[col - 1]);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getMinor(int, int)
	 */
	public MatrixAccess<T> getMinor (int row, int column)
	{
		MinorMatrixWrapper<T> wrapper =
			new MinorMatrixWrapper<T> (this);
		wrapper.eliminateColumn (column);
		wrapper.eliminateRow (row);
		return wrapper;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getMinor(int)
	 */
	public MinorAccess<T> getMinor (int column)
	{
		MinorMatrixWrapper<T> wrapper =
			new MinorMatrixWrapper<T> (this);
		wrapper.eliminateColumn (column);
		return wrapper;
	}


	public MinorMatrixComputationTask<T> getOptimizedAccess (FullMatrixWrapper<T> wrapperFactory)
	{
		throw new RuntimeException ("Optimized access not implemented");
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MinorAccess#getCofactorComputationTaskFactory()
	 */
	public CofactorComputationTaskFactory<T> getCofactorComputationTaskFactory ()
	{
		throw new RuntimeException ("Optimized access not implemented");
	}


}

