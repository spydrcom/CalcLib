
package net.myorb.math.linalg;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.decomposition.GenericQRD;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.data.abstractions.SimpleStreamIO;

/**
 * implementation of Solution Primitives using QRD
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class QRDSolution <T>
	implements SolutionPrimitives.Invertable <T>, SolutionPrimitives <T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#restore(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
	 */
	public Decomposition restore (SimpleStreamIO.TextSource from) { return QRD.restore (from); }

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#decompose(net.myorb.math.matrices.Matrix)
	 */
	public SolutionPrimitives.Decomposition decompose (Matrix <T> A) { return QRD.decompose (A); }

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
		GenericQRD <T>.QRDecomposition QR =
				( GenericQRD <T>.QRDecomposition ) D;
		return QRD.solve (QR, b);
	}

	public QRDSolution (ExpressionSpaceManager <T> mgr)
	{ this.QRD = new GenericQRD <T> (mgr); this.QRD.setSolutionClassPath (this.getClass ().getCanonicalName ()); }
	protected GenericQRD <T> QRD;

}
