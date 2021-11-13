
package net.myorb.math.matrices.optimization;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.MatrixAccess;
//import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.FullMatrixWrapper;
import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.MinorAccess;

import net.myorb.math.SpaceManager;

/**
 * optimized overlay of matrix operations object.
 *  cofactor expansion tree is constructed of optimized (memory allocation reduced) node objects
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MatrixOperationsOptimized <T> extends MatrixOperations<T>
	implements FullMatrixWrapper<T>, MinorMatrixNodeFactory<T>
{


	/**
	 * type manager is passed to super constructor
	 * @param manager the type manager for T
	 */
	public MatrixOperationsOptimized
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
	public Matrix<T> comatrix (MinorMatrixComputationTask<T> m)
	{
		//m.dump (); dump ("comatrix", m);
		MinorMatrixComputationPool<T> pool = m;
		int rows = m.rowCount (), columns = m.columnCount ();
		Matrix<T> result = new Matrix<T> (rows, columns, manager);

		for (int column = 1; column <= columns; column++)
		{
//			MinorAccess<T> minorAccess = m.getMinor (column);
			OptimizedMinorAccess<T> minorAccess = m.getMinor (this, column);
			CofactorComputationTaskFactory<T> factory = minorAccess.getCofactorComputationTaskFactory ();
			pool.addTask (minorAccess, column, result.getColAccess (column), factory, this);
		}

		pool.forkJoin (this);
		// tasks (m & children) can be marked for reuse
		releaseTask (m);
		return result;
	}

	/**
	 * construct a task that will compute the
	 * row cofactors associated with a minor matrix
	 * @param m the matrix to use for computation of cofactors
	 * @param column the column number removed from the minor matrix 
	 * @return a vector of the results
	 */
	public MinorMatrixComputationTask<T> computeCofactors (MinorMatrixComputationTask<T> m, int column)
	{
		//m.dump (); dump ("compute COF", m);
		MinorMatrixComputationPool<T> pool = m;
		OptimizedMinorAccess<T> minorAccess = m.getMinor (column);
//		OptimizedMinorAccess<T> minorAccess = m.getMinor (this, column);
		MinorMatrixComputationTask<T> resultBufferVector = minorAccess.getVectorAccess (discrete (0), m.rowCount ());
		CofactorComputationTaskFactory<T> factory = minorAccess.getCofactorComputationTaskFactory ();
		pool.addTask (minorAccess, column, resultBufferVector, factory, this); pool.forkJoin (this);
		//System.out.println ("COF vector" + vectorOperations.toString (resultBufferVector));
		return resultBufferVector;
	}

	/**
	 * Laplace algorithm for computation of determinant of square matrix.
	 *  this version improves efficiency by using a matrix wrapper that maps minor matrices using index mapping.
	 *  the cofactor removed column is 1 and the mapping object can map each of the rows as removed
	 * @param m the matrix to use for computation
	 * @return computed result
	 */
	public T cofactorExpansion (MinorMatrixComputationTask<T> m)
	{
		//m.dump (); dump ("coef EXP", m);
		//VectorAccess<T> firstColumn = m.getColAccess (1);
		//System.out.println ("COL V " + vectorOperations.toString (firstColumn));
		MinorMatrixComputationTask<T> cofactorVector = computeCofactors (m, 1);
		//System.out.println ("COL vector" + vectorOperations.toString (m.getColAccess (1)));
		T cofactor = vectorOperations.dotProduct (cofactorVector, m.getColAccess (1));
		releaseTask (m); // tasks (m & cofactor) can be marked for reuse
		return cofactor;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixOperations#cofactorExpansion(net.myorb.math.matrices.MatrixAccess)
	 */
	public T cofactorExpansion (MatrixAccess<T> m) { return cofactorExpansion (m.getOptimizedAccess (this)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixOperations#comatrix(net.myorb.math.matrices.MatrixAccess)
	 */
	public Matrix<T> comatrix (MatrixAccess<T> m) { return comatrix (m.getOptimizedAccess (this)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.FullMatrixWrapper#getOptimizedAccess(net.myorb.math.matrices.Matrix)
	 */
	public MinorMatrixComputationTask<T> getOptimizedAccess (Matrix<T> m)
	{
		MinorMatrixComputationTask<T> task = allocateTask ();
		task.wrapMasterMatrix (m);
		return task;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.optimization.MinorMatrixNodeFactory#buildMinorMatrixNode(net.myorb.math.matrices.optimization.MinorMatrixWrapper, int)
	 */
	public MinorMatrixWrapper<T> buildMinorMatrixNode (MinorMatrixWrapper<T> m, int column)
	{
		MinorMatrixWrapper<T> wrapper = allocateTask ();
		wrapper.wrapMinorMatrix (m); wrapper.eliminateColumn (column);
		return wrapper;
	}

	/**
	 * free cofactor computation task objects
	 * @param task the task object to be released
	 */
	public void releaseTask (MinorMatrixComputationTask<T> task)
	{
		if (!MANAGE_NODES) return;

		synchronized (this)
		{
			task.link = this.link;
			this.link = task;
		}
		System.out.println ("release " + (++relsd) + " " + task.toString());
	}

	/**
	 * allocate a cofactor computation task object.
	 *  take from free list if available
	 * @return a task object
	 */
	public MinorMatrixComputationTask<T> allocateTask ()
	{
		//String flag = " NEW ";
		MinorMatrixComputationTask<T> allocated = null;

		if (link != null)
		{
			synchronized (this)
			{
				allocated = this.link;
				this.link = allocated.link;
				allocated.link = null;
				//flag = " USED ";
			}
		} else allocated = new MinorMatrixComputationTask<T> ();
		//System.out.println ("================ alloc " + (++allocd) + flag + allocated.toString());
		return allocated;
	}

	static final boolean MANAGE_NODES = false;
	public MinorMatrixComputationTask<T> link = null;
	int allocd = 0, relsd = 0;

}
