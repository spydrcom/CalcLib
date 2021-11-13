
package net.myorb.math.matrices.optimization;

import net.myorb.math.matrices.*;

import java.util.Arrays;
import java.util.List;

/**
 * a wrapper for matrix class objects allowing minor matrix access modeled on top of full matrix.
 * this allows reduced object creation and no copying of content into minor matrix objects
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MinorMatrixWrapper<T> extends AbstractOptimizedWrapper<T> implements OptimizedMinorAccess<T>
{


	/**
	 * provide space allocations allowing for a square matrix with a maximum edge size
	 */
	public static final int MAX_EDGE_SIZE = 20;


	/**
	 * for recursive minor matrix descriptors
	 * @param parent the parent minor matrix wrapper
	 */
	public void wrapMinorMatrix (MinorMatrixWrapper<T> parent)
	{
		this.setSquareCounts (parent.size - 2);
		this.allocateColumnManagementObjects ();
		this.copyParent (parent);
	}
	protected List<T> cells;


	/**
	 * copy parent parameters into THIS wrapper
	 * @param parent the parent minor matrix wrapper
	 */
	public void copyParent (MinorMatrixWrapper<T> parent)
	{
		this.majorIndicies = parent.minorIndicies;
		System.arraycopy (parent.eliminatedColumns, 0, this.eliminatedColumns, 0, parent.eliminatedColumns.length);
		System.arraycopy (parent.columnOffset, 0, this.columnOffset, 0, parent.columnOffset.length);
		this.cells = parent.cells;
	}


	/**
	 * initialize column elimination management
	 */
	private void allocateColumnManagementObjects ()
	{
		this.size = super.rowCount + 1;									// row count reflects minor matrix
		Arrays.fill (this.eliminatedColumns, false);					//  minor is one dimension smaller
	}
	protected boolean eliminatedColumns[] = new boolean[MAX_EDGE_SIZE];


	/**
	 * the top level wrapper for the full size matrix
	 * @param m the matrix object to be wrapped
	 */
	public void wrapMasterMatrix (Matrix<T> m)
	{
		this.cells = m.getCellList ();
		this.setSquareCounts (m.rowCount ());
		this.allocateColumnManagementObjects ();

		for (int i = 0; i < size; i++)
		{
			this.majorIndicies[i] = m.cellNumber (i + 1, 1);		// point to first column of row and accept column offset will adjust
			this.columnOffset[i] = i;								// with none eliminated, the map is transparent
		}
		this.minorIndicies = this.majorIndicies;
	}
	protected int
		columnOffset[] = new int[MAX_EDGE_SIZE],
		majorIndicies[] = new int[MAX_EDGE_SIZE],
		minorIndicies[] = new int[MAX_EDGE_SIZE];
	protected int size;


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
	public MinorMatrixWrapper<T> getMinor (int row, int column)
	{
		notImplemented ();
		return null;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getMinor(int)
	 */
	public OptimizedMinorAccess<T> getMinor (int column)
	{
		MinorMatrixWrapper<T> wrapper =
			new MinorMatrixComputationTask<T> ();
		wrapper.wrapMinorMatrix (this);
		wrapper.eliminateColumn (column);
		return wrapper;
	}

	/**
	 * use node factory to construct minor matrix wrapper from this parent
	 * @param factory the factory object to be used 
	 * @param column the column to be removed
	 * @return minor matrix access
	 */
	public OptimizedMinorAccess<T> getMinor (MinorMatrixNodeFactory<T> factory, int column)
	{
		return factory.buildMinorMatrixNode (this, column);
	}


}

