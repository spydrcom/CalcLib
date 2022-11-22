
package net.myorb.math.linalg;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.decomposition.GenericQRD;

import net.myorb.math.expressions.ExpressionSpaceManager;

/**
 * implementation of Solution Primitives using QRD
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class QRDSolution <T>
	implements SolutionPrimitives.Invertable <T>, SolutionPrimitives <T>
{

	public QRDSolution (ExpressionSpaceManager <T> mgr)
	{
		this.QRD = new GenericQRD <T> (mgr);
	}
	GenericQRD <T> QRD;

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#decompose(net.myorb.math.matrices.Matrix)
	 */
	public SolutionPrimitives.Decomposition decompose (Matrix <T> A)
	{
		return QRD.decompose (A);
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
		GenericQRD.QRDecomposition <T> QR =
				( GenericQRD.QRDecomposition <T> ) D;
		return QRD.solve (QR, b);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Invertable#inv(net.myorb.math.matrices.Matrix)
	 */
	public Matrix <T> inv (Matrix <T> source)
	{
		return new InversionSolution <T> (this).inv (source);
	}

}
