
package net.myorb.math.matrices;

import net.myorb.math.matrices.optimization.MinorMatrixComputationTask;

/**
 * wrap a master matrix to start tree of optimized cofactor processing
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface FullMatrixWrapper<T>
{
	/**
	 * wrap a matrix object
	 * @param m the matrix object to be wrapped
	 * @return wrapped matrix object
	 */
	MinorMatrixComputationTask<T> getOptimizedAccess (Matrix<T> m);
}
