
package net.myorb.math.linalg;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.decomposition.Crouts;
import net.myorb.math.expressions.ExpressionSpaceManager;

/**
 * implementation of Solution Primitives using Crouts LUD
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class CroutsSolution <T> extends Crouts <T>
	implements SolutionPrimitives.Determinable <T>, SolutionPrimitives.Invertable <T>,
		SolutionPrimitives <T>
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
		CroutsDecomposition LU = ( CroutsDecomposition ) D;
		return solve (LU, b);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.decomposition.Crouts#inv(net.myorb.math.matrices.Matrix)
	 */
	public Matrix <T> inv (Matrix <T> source) { return new InversionSolution <T> (this).inv (source); }

	public CroutsSolution (ExpressionSpaceManager <T> mgr)
	{ super (mgr); setSolutionClassPath (); }

}
