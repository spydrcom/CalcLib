
package net.myorb.math.linalg;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.Matrix;

/**
 * linear algebra solution for system of equations presented as a matrix
 * @author Michael Druckman
 */
public class SolutionApplication <T>
{


	/**
	 * solve system of equations with SolutionPrimitives Decomposition
	 * @param M matrix holding the equation parameters to be solved
	 * @param V the requested result vector for the solution
	 * @return the computed solution
	 */
	public Matrix <T> decompositionSolution (Matrix <T> M, Matrix <T> V)
	{
		VectorAccess <T> result = solve
		(
			primitives.decompose (M),
			V.getColAccess (1)
		);
		Matrix <T> computedSolution = new Matrix <> (result.size (), 1, manager);
		ops.setCol (1, computedSolution, result);
		return computedSolution;
	}
	@SuppressWarnings("unchecked") VectorAccess <T> solve
		(
			SolutionPrimitives.Decomposition D,
			VectorAccess <T> solution
		)
	{
		SolutionPrimitives.RequestedResultVector b =
			new SolutionPrimitives.Content <T> (solution, manager);
		SolutionPrimitives.SolutionVector V = primitives.solve (D, b);
		return (VectorAccess <T>) V;
	}
	protected MatrixOperations <T> ops;


	/**
	 * identify solution algorithm to be used
	 * @param primitives implementation of SolutionPrimitives
	 */
	public void setPrimitives
	(SolutionPrimitives <T> primitives) { this.primitives = primitives; }
	public SolutionPrimitives <T> getPrimitives () { return primitives; }
	protected SolutionPrimitives <T> primitives;


	// constructors based on processing environment


	public SolutionApplication (ExpressionSpaceManager <T> manager)
	{
		this.ops = new MatrixOperations <T> (this.manager = manager);
		// default is matrix inverse based on determinants
		this.primitives = ops;
	}
	protected ExpressionSpaceManager <T> manager;

	public SolutionApplication (Environment <T> environment)
	{ this (environment.getSpaceManager ()); }


}

