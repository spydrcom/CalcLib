
package net.myorb.math.linalg;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.matrices.decomposition.*;

/**
 * implementation of Solution Primitives using LUD
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class LUDSolution <T> extends GenericLUD <T>
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
		LUDecomposition LU = ( LUDecomposition ) D;
		return solve (LU, b);
	}

	public LUDSolution (ExpressionSpaceManager <T> mgr)
	{ super (mgr); setSolutionClassPath (); }

}
