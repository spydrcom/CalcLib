
package net.myorb.math.matrices;

import net.myorb.math.SpaceManager;
import net.myorb.math.matrices.optimization.CofactorComputationTaskFactory;
import net.myorb.math.matrices.optimization.MinorMatrixComputationTask;
import net.myorb.math.ListOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * representation of matrix of generic cells
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Matrix<T> extends ListOperations<T> implements MatrixAccess<T>
{

	/**
	 * construct a new matrix
	 * @param rows the count of rows in the the matrix
	 * @param columns the count of columns in the the matrix
	 * @param manager the manager for the type being manipulated
	 */
	public Matrix (int rows, int columns, SpaceManager<T> manager)
	{
		super (manager);
		allocate (rows, columns);
	}

	/**
	 * construct a new matrix with initial value
	 * @param rows the count of rows in the the matrix
	 * @param columns the count of columns in the the matrix
	 * @param init the initial value to copy into each cell of matrix
	 * @param manager the manager for the type being manipulated
	 */
	public Matrix (int rows, int columns, T init, SpaceManager<T> manager)
	{
		super (manager);
		this.rows = rows; this.cols = columns;
		save (init, rows*columns);
	}

	/**
	 * construct a new matrix with initial values
	 * @param rows the count of rows in the the matrix
	 * @param columns the count of columns in the the matrix
	 * @param cells the initial value of all cells of the matrix
	 * @param manager the manager for the type being manipulated
	 */
	public Matrix (int rows, int columns, List<T> cells, SpaceManager<T> manager)
	{
		super (manager);
		save (rows, columns, cells);
	}

	/**
	 * allocate and zero fill
	 * @param rows the count of rows in the the matrix
	 * @param columns the count of columns in the the matrix
	 */
	public void allocate (int rows, int columns)
	{
		int n = rows * columns;
		ArrayList<T> a = new ArrayList<T> (n);
		fillAppendingWith (a, discrete (0), n);
		save (rows, columns, a);
	}

	/**
	 * save dimensions and initialize
	 * @param rows the count of rows in the the matrix
	 * @param columns the count of columns in the the matrix
	 * @param cells the initial value of all cells of the matrix
	 */
	public void save (int rows, int columns, List<T> cells)
	{
		int n = rows*columns;
		if (cells.size () == 1)
		{ this.save (cells.get (0), n); }
		else this.save (cells, n);
		this.cols = columns;
		this.rows = rows;
	}
	protected int rows, cols;

	/**
	 * verify size and initialize
	 * @param cells the initial value of all cells of the matrix
	 * @param size the expected size (rows*cols)
	 */
	public void save (List <T> cells, int size)
	{
		int initSize = cells.size ();
		if (initSize != size)
		{
			if (initSize == 1)
			{ save (cells.get (0), size); }
			else throw new RuntimeException (INIT_ERROR);
		}
		this.cells = cells;
		this.size = size;
	}
	public static final String INIT_ERROR = "Matrix initialization error";

	/**
	 * copy initial value to the matrix cells
	 * @param init the initial value to copy into each cell of matrix
	 * @param size the expected size (rows*cols)
	 */
	public void save (T init, int size)
	{
		cells = new ArrayList <T> ();
		for (int i=1; i<=size; i++) cells.add (init);
	}
	protected List<T> cells;
	protected int size;

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#rowCount()
	 */
	public int rowCount () { return rows; }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#columnCount()
	 */
	public int columnCount () { return cols; }

	/**
	 * verify matrix is square and get edge size
	 * @return size of matrix edge for square matrix objects
	 * @throws RuntimeException if matrix not square
	 */
	public int getEdgeCount () throws RuntimeException
	{
		if (rows != cols)
		{ throw new RuntimeException ("Matrix not square: " + rows + "x" + cols); }
		return cols;
	}

	/**
	 * get access to the cells of the matrix
	 * @return a List object providing access to the matrix cells
	 */
	public List<T> getCellList () { return cells; }

	/**
	 * compute linear cell number for specified row and column
	 * @param row the row number containing the cell
	 * @param col the column number
	 * @return the array index
	 */
	public int cellNumber (int row, int col)
	{
		if (row > rows) throw new RuntimeException ("ROW " + row + "," + col);
		if (col > cols) throw new RuntimeException ("COL " + row + "," + col);
		return (row - 1) * cols + col - 1;
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#set(int, int, java.lang.Object)
	 */
	public void set (int row, int col, T value)
	{
		cells.set (cellNumber (row, col), value);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#get(int, int)
	 */
	public T get (int row, int col)
	{
		return cells.get (cellNumber (row, col));
	}

	/**
	 * get row vector of specified row
	 * @param row number of the row
	 * @return row vector
	 */
	public Vector<T> getRow (int row)
	{
		Vector<T> v = new Vector<T> (cols, manager);
		for (int c=1; c<=cols; c++) v.set (c, this.get (row, c)); // copy row elements to vector
		return v;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getRowAccess(int)
	 */
	public VectorAccess<T> getRowAccess (int row)
	{
		return new SpanAccess<T> (cells, cellNumber (row, 1), cols); // index span is assumed to be 1
	}

	/**
	 * get column vector of specified column
	 * @param col number of the column
	 * @return column vector
	 */
	public Vector<T> getCol (int col)
	{
		Vector<T> v = new Vector<T> (rows, manager);
		for (int r=1; r<=rows; r++) v.set (r, this.get (r, col)); // copy column elements to vector
		return v;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getColAccess(int)
	 */
	public VectorAccess<T> getColAccess (int col)
	{
		return new SpanAccess<T> (cells, cols, cellNumber (1, col), 1, rows); // index span is 'cols' stepping over a row per index unit
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getMinor(int)
	 */
	public MinorAccess<T> getMinor (int column)
	{
		return new MinorMatrixWrapper<T> (this, column);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getMinor(int, int)
	 */
	public MatrixAccess<T> getMinor (int row, int column)
	{
		return new MinorMatrixWrapper<T> (this, row, column);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getOptimizedAccess(net.myorb.math.matrices.FullMatrixWrapper)
	 */
	public MinorMatrixComputationTask<T> getOptimizedAccess (FullMatrixWrapper<T> wrapperFactory)
	{
		return wrapperFactory.getOptimizedAccess (this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MinorAccess#getCofactorComputationTaskFactory()
	 */
	public CofactorComputationTaskFactory<T> getCofactorComputationTaskFactory ()
	{
		raiseException ("Optimized access not implemented");
		return null;
	}

	/**
	 * convert to double array.
	 *  useful for interface to external methods
	 * @return double array with values
	 */
	public double[][] toRawCells ()
	{
		double[][] cells = new double[rows][cols];
		for (int r = 0; r < rows; r++)
		{
			for (int c = 0; c < cols; c++)
			{
				cells[r][c] = manager.toNumber (get (r+1, c+1)).doubleValue ();
			}
		}
		return cells;
	}

}
