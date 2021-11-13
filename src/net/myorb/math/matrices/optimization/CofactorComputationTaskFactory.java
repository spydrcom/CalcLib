
package net.myorb.math.matrices.optimization;

import net.myorb.math.matrices.*;

/**
 * provide access to factory for runnable cofactor computation tasks
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface CofactorComputationTaskFactory <T>
{

	/**
	 * @param m matrix minor access object
	 * @param column the column removed from the matrix
	 * @param v the vector access object to write the cofactors to
	 * @param pool the pool of tasks that are required to complete the vector
	 * @param ops the matrix operations object that provides cofactor computation
	 * @return the Runnable object that can compute the results
	 */
	public Runnable cofactorComputationTask
	(
			MinorAccess<T> m, int column, VectorAccess<T> v,
			MinorMatrixComputationPool<T> pool, MatrixOperationsOptimized<T> ops
	);

}
