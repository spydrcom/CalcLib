
package net.myorb.math.matrices;

import net.myorb.math.matrices.cofactors.*;
import net.myorb.math.SpaceManager;

/**
 * minor matrix mapping algorithm
 *  used to improve the comatrix and cofactor expansion execution
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MatrixOperationsAccelerated<T> extends MatrixOperations<T>
{

	/**
	 * sections of code are provided to convert to RecursiveAction multi-processor functionality.
	 * current coding uses Thread with wait/notify to accomplish Fork/Join execution pattern.
	 * when updated to SE8.0 the THREADED section can be changed to
	 * hopefully allow parallel execution of tasks
	 */
	public static final boolean THREADED = true;

	/**
	 * threaded super class is chosen for threaded execution pattern selected
	 * @return computation pool, threaded or sequential on option
	 */
	public CofactorComputationPool<T> constructCofactorComputationPool ()
	{
		if (THREADED)
			//return new ThreadedCofactorComputationPool<T> (this);
			return new ExecutorCofactorComputationPool<T> (this);
		return new CofactorComputationPool<T> (this);
	}

	/**
	 * type manager is passed to super constructor
	 * @param manager the type manager for T
	 */
	public MatrixOperationsAccelerated
		(SpaceManager<T> manager)
	{
		super (manager);
	}

	/**
	 * accelerated version of cofactor using minor matrix mapping
	 * @param access the minor matrix access object which maps minor to parent matrix without copies
	 * @param row the number of the row being removed in this cofactor
	 * @param col the number of the column that was removed
	 * @return the cofactor (row=specified, column=1)
	 */
	public T cofactor (MinorAccess<T> access, int row, int col)
	{
		T result = det (access.getMinorAbsent (row));
		if ((row + col) % 2 == 0) return result;
		return neg (result);
	}

	/**
	 * compute matrix of cofactors
	 * @param m the matrix to use for computation
	 * @return computed result
	 */
	public Matrix<T> comatrix (MatrixAccess<T> m)
	{
		int rows = m.rowCount (), cols = m.columnCount ();
		Matrix<T> result = new Matrix<T> (rows, cols, manager);
		CofactorComputationPool<T> pool = constructCofactorComputationPool ();

		for (int c = 1; c <= cols; c++)
		{ pool.addTask (m.getMinor (c), c, result.getColAccess (c)); }
		pool.forkJoin ();

		return result;
	}

	/**
	 * construct a task that will compute the
	 * row cofactors associated with a minor matrix
	 * @param m the matrix to use for computation of cofactors
	 * @param column the column number removed from the minor matrix 
	 * @return a vector of the results
	 */
	public VectorAccess<T> computeCofactors (MatrixAccess<T> m, int column)
	{
		Vector<T> v = new Vector<T> (m.rowCount (), manager);
		CofactorComputationPool<T> pool = constructCofactorComputationPool ();
		pool.addTask (m.getMinor (column), column, v);
		pool.forkJoin ();
		return v;
	}

	/**
	 * Laplace algorithm for computation of determinant of square matrix.
	 *  this version improves efficiency by using a matrix wrapper that maps minor matrices using index mapping.
	 *  the cofactor removed column is 1 and the mapping object can map each of the rows as removed
	 * @param m the matrix to use for computation
	 * @return computed result
	 */
	public T cofactorExpansion (MatrixAccess<T> m)
	{
		return vectorOperations.dotProduct (computeCofactors (m, 1), m.getColAccess (1));
	}

}
