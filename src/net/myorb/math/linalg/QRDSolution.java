
package net.myorb.math.linalg;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.decomposition.*;
import net.myorb.math.expressions.ExpressionSpaceManager;

/**
 * implementation of Solution Primitives using QRD
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class QRDSolution <T> extends GenericQRD <T>
	implements SolutionPrimitives.Invertable <T>, SolutionPrimitives <T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Invertable#inv(net.myorb.math.matrices.Matrix)
	 */
	public Matrix <T> inv (Matrix <T> source) { return new InversionSolution <T> (this).inv (source); }

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
		QRDecomposition QR = ( QRDecomposition ) D;
		return solve (QR, b);
	}

	public QRDSolution (ExpressionSpaceManager <T> mgr)
	{ super (mgr); setSolutionClassPath (); }

}
