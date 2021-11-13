
package net.myorb.math.matrices.optimization;

import net.myorb.math.matrices.MinorAccess;

/**
 * node object allocates a list that can be used as a temporary result vector.
 * this extended version of the minor matrix access interface provides access to the vector space.
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface OptimizedMinorAccess<T> extends MinorAccess<T>
{
	/**
	 * get access to result vector
	 * @param initial the value to use to initialize vector
	 * @param vectorLength the length of the vector
	 * @return the node object holding the vector
	 */
	MinorMatrixComputationTask<T> getVectorAccess (T initial, int vectorLength);
}
