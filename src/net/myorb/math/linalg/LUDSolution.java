
package net.myorb.math.linalg;

import net.myorb.math.matrices.decomposition.*;
import net.myorb.math.matrices.Matrix;

import net.myorb.math.SpaceManager;

/**
 * implementation of Solution Primitives using LUD
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class LUDSolution <T>
	implements SolutionPrimitives.Determinable <T>, SolutionPrimitives.Invertable <T>,
		SolutionPrimitives <T>
{

	public LUDSolution (SpaceManager <T> mgr)
	{
		this.LUD = new GenericLUD <T> (mgr);
	}
	GenericLUD <T> LUD;

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#decompose(net.myorb.math.matrices.Matrix)
	 */
	public SolutionPrimitives.Decomposition decompose (Matrix <T> A)
	{
		return LUD.decompose (A);
	}

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
		GenericLUD.LUDecomposition <T> LU =
				( GenericLUD.LUDecomposition <T> ) D;
		return LUD.solve (LU, b);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Invertable#inv(net.myorb.math.matrices.Matrix)
	 */
	public Matrix <T> inv (Matrix <T> source)
	{
		return LUD.inv (source);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Determinable#det(net.myorb.math.matrices.Matrix)
	 */
	public T det (Matrix <T> source)
	{
		return LUD.det (source);
	}

}
