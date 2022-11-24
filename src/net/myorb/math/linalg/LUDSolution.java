
package net.myorb.math.linalg;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.matrices.decomposition.*;
import net.myorb.math.matrices.Matrix;

/**
 * implementation of Solution Primitives using LUD
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class LUDSolution <T>
	implements SolutionPrimitives.Determinable <T>, SolutionPrimitives.Invertable <T>,
		SolutionPrimitives <T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#restore(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
	 */
	public Decomposition restore (SimpleStreamIO.TextSource from) { return LUD.restore (from); }

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#decompose(net.myorb.math.matrices.Matrix)
	 */
	public SolutionPrimitives.Decomposition decompose (Matrix <T> A) { return LUD.decompose (A); }

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Invertable#inv(net.myorb.math.matrices.Matrix)
	 */
	public Matrix <T> inv (Matrix <T> source) { return LUD.inv (source); }

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Determinable#det(net.myorb.math.matrices.Matrix)
	 */
	public T det (Matrix <T> source) { return LUD.det (source); }

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
		GenericLUD <T>.LUDecomposition LU =
				( GenericLUD <T>.LUDecomposition ) D;
		return LUD.solve (LU, b);
	}

	public LUDSolution (ExpressionSpaceManager <T> mgr)
	{ this.LUD = new GenericLUD <T> (mgr); this.LUD.setSolutionClassPath (this.getClass ().getCanonicalName ()); }
	protected GenericLUD <T> LUD;

}
