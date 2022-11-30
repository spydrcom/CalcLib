
package net.myorb.math.linalg;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.matrices.decomposition.GenericCholesky;

/**
 * implementation of Solution Primitives using Cholesky
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class CholeskySolution <T> extends GenericCholesky <T>
	implements SolutionPrimitives.Invertable <T>, SolutionPrimitives <T>, SolutionPrimitives.MatrixSolution <T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#solve(net.myorb.math.linalg.SolutionPrimitives.Decomposition, net.myorb.math.linalg.SolutionPrimitives.RequestedResultVector)
	 */
	public SolutionPrimitives.SolutionVector solve
		(
			SolutionPrimitives.Decomposition D,
			SolutionPrimitives.RequestedResultVector b
		)
	{
		@SuppressWarnings("unchecked")
		CholeskyDecomposition CHD = ( CholeskyDecomposition ) D;
		return solve (CHD, b);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.MatrixSolution#solve(net.myorb.math.linalg.SolutionPrimitives.Decomposition, net.myorb.math.matrices.Matrix)
	 */
	public Matrix<T> solve (SolutionPrimitives.Decomposition D, Matrix<T> source)
	{
		@SuppressWarnings("unchecked")
			CholeskyDecomposition CHD = ( CholeskyDecomposition ) D;
		return CHD.solve (source);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Invertable#inv(net.myorb.math.matrices.Matrix)
	 */
	public Matrix <T> inv (Matrix <T> source) { return new InversionSolution <T> (this).inv (source); }

	public CholeskySolution (ExpressionSpaceManager <T> mgr)
	{ super (mgr); setSolutionClassPath (); }

}
