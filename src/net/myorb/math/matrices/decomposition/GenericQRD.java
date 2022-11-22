
package net.myorb.math.matrices.decomposition;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.linalg.SolutionPrimitives;

import net.myorb.math.matrices.Vector;
import net.myorb.math.matrices.Matrix;

/**
 * implementation of QR decomposition on generic data types
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class GenericQRD <T> extends GenericSupport
{
	

	public GenericQRD
	(ExpressionSpaceManager <T> mgr) { this.mgr = mgr; }
	protected ExpressionSpaceManager <T> mgr;


	/**
	 * @param x parameter to SQRT
	 * @return computed SQRT
	 */
	T sqrt (T x) { return mgr.convertFromDouble (Math.sqrt (mgr.convertToDouble (x))); }
	//TODO: need to introduce library for generic T


	/**
	 * representation of matrix Decomposition using this QRD algorithm set
	 */
	public static class QRDecomposition <T> implements SolutionPrimitives.Decomposition
	{
		QRDecomposition (Matrix <T> A)
		{
			this.A = copyOf (A);
			this.N = A.getEdgeCount ();
			ExpressionSpaceManager <T> mgr =
				(ExpressionSpaceManager <T>) A.getSpaceManager ();
			this.C = new Vector <T> (N, mgr);
			this.D = new Vector <T> (N, mgr);
		}
		Vector <T> C, D;
		Matrix <T> A;
		int N;
	}


	/*
	 * decompose
	 */

	/**
	 * @param A the matrix to be decomposed
	 * @return the resulting Decomposition object
	 */
	public QRDecomposition <T> decompose (Matrix <T> A)
	{
		QRDecomposition <T> D;
		decompose (D = new QRDecomposition <T> (A));
		return D;
	}

	/**
	 * @param QRD the Decomposition to be processed
	 */
	public void decompose (QRDecomposition <T> QRD)
	{
		for (int k = 1; k < QRD.N; k++)
		{
			int maxRowNum = maxRow (k, QRD.A);
			T scale = QRD.A.get (maxRowNum, k);

			T sum = mgr.getZero ();
			for (int i = k; i <= QRD.N; i++)
			{
				T AIK = divideInto (QRD.A, i, k, scale, mgr);
				sum = mgr.add (sum, mgr.multiply (AIK, AIK));
			}

			T sigma = SIGN ( sqrt (sum), QRD.A.get (k, k), mgr );
			QRD.D.set (k, mgr.negate (mgr.multiply (scale, sigma)));

			T AKK = addInto (QRD.A, k, k, sigma, mgr);
			QRD.C.set (k, mgr.multiply (sigma, AKK));

			for (int j = k + 1; j <= QRD.N; j++)
			{
				T dp = dot (QRD.A.getColAccess (k), QRD.A.getColAccess (j), k, QRD.N, mgr);
				T tau = mgr.multiply (dp, mgr.invert (QRD.C.get (k)));

				for (int i = k; i <= QRD.N; i++)
				{
					reduceBy (QRD.A, i, j, mgr.multiply (tau, QRD.A.get (i, k)), mgr);
				}
			}
		}

		QRD.D.set (QRD.N, QRD.A.get (QRD.N, QRD.N));
	}


	/*
	 * solution
	 */

	/**
	 * @param D the decomposed translation matrix description
	 * @param b the expected result vector
	 * @return the computed solution
	 */
	public SolutionPrimitives.SolutionVector solve
		(
			QRDecomposition <T> D, SolutionPrimitives.RequestedResultVector b
		)
	{
		@SuppressWarnings("unchecked")
		SolutionPrimitives.Content <T> result =
				( SolutionPrimitives.Content <T> ) b;
		return solve (D, result);
	}

	/**
	 * @param D the decomposed translation matrix description
	 * @param b the expected result vector
	 * @return the computed solution
	 */
	public SolutionPrimitives.SolutionVector solve
		(
			QRDecomposition <T> D, SolutionPrimitives.Content <T> b
		)
	{
		SolutionPrimitives.Content <T> x =
				new SolutionPrimitives.Content <T> (b.size (), mgr);
		copyCells (b, x);

		for (int j = 1; j < D.N; j++)
		{
			T dp = dot (x, D.A.getColAccess (j), j, D.N, mgr);
			T tau = mgr.multiply (dp, mgr.invert (D.C.get (j)));

			for (int i = j; i <= D.N; i++)
			{
				reduceBy (x, i, mgr.multiply (tau, D.A.get (i, j)), mgr);
			}
		}

		rsolve (D, x);

		return x;
	}

	public void rsolve
		(
			QRDecomposition <T> D, SolutionPrimitives.Content <T> x
		)
	{
        divideInto (x, D.N, D.D.get (D.N), mgr);

		for (int i = D.N - 1; i >= 1; i--)
		{
			T dp = dot (x, D.A.getRowAccess (i), i+1, D.N, mgr);
			T dif = mgr.add (x.get (i), mgr.negate (dp));
			x.set (i, mgr.multiply (dif, mgr.invert (D.D.get (i))));
		}
	}


}

