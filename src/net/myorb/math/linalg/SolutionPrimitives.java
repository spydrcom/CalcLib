
package net.myorb.math.linalg;

import net.myorb.math.matrices.Matrix;

/**
 * abstract view of working with sets of linear equations
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public interface SolutionPrimitives <T>
{

	/*
	 * attempting to solve Ax = b
	 */

	/**
	 * a digestion of the mapping data
	 */
	public interface Decomposition {}

	/**
	 * a description of the sought result
	 */
	public interface RequestedResultVector {}

	/**
	 * a solution description for a requested result
	 */
	public interface SolutionVector {}

	/**
	 * digest a translation matrix
	 * @param A the mapping data
	 * @return the digested form
	 */
	public Decomposition decompose (Matrix <T> A);

	/**
	 * @param m the digested map to be used
	 * @param b the result vector being sought
	 * @return the solution giving the result
	 */
	public SolutionVector solve (Decomposition m, RequestedResultVector b);

}
