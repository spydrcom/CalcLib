
package net.myorb.math.matrices;

import net.myorb.math.matrices.optimization.CofactorComputationTaskFactory;

/**
 * modeling efficient minor matrix access assumes use of column 1 as the removed column for cofactor computation.
 * all rows are made available assuming at the access point a row will need to be removed resulting in a square minor matrix.
 * a single access object will control all minor matrices with each being accessed by identifying the row to remove.
 * @param <T> the component type of the matrices being described
 * @author Michael Druckman
 */
public interface MinorAccess<T>
{
	/**
	 * implementers provide control over minor matrices modeled on top of the parent matrix
	 * @param row the row to remove to give the square minor matrix of interest
	 * @return a matrix access object for the minor matrix
	 */
	MatrixAccess<T> getMinorAbsent (int row);

	/**
	 * optimized version for cofactor computation
	 * @return task factory implementation
	 */
	CofactorComputationTaskFactory<T> getCofactorComputationTaskFactory ();
}
