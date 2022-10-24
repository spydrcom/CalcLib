
package net.myorb.math.linalg;

import net.myorb.math.matrices.Triangular;
import net.myorb.math.matrices.decomposition.Doolittle;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.Vector;

import net.myorb.math.SpaceManager;

/**
 * linear algebra solutions using triangular matrix decomposition
 * @author Michael Druckman
 */
public class TriangularMatrix
{

	/**
	 * solve luXb as interpolation of points
	 * @param U upper triangular matrix of LU solution
	 * @param L lower triangular matrix of LU solution
	 * @param points vector of points to be interpolated
	 * @return vector holding solution
	 * @param <T> data type
	 */
	public static <T> Vector<T> solve (Matrix<T> U, Matrix<T> L, Vector<T> points)
	{
		return new Triangular <T> (points.getSpaceDescription ()).luXb (L, U, points);
	}

	/**
	 * decompose matrix to LU
	 * @param A matrix to be decomposed
	 * @param U upper triangular matrix of LU solution
	 * @param L lower triangular matrix of LU solution
	 * @param <T> data type
	 */
	public static <T> void decompose
	(Matrix<T> A, Matrix<T> U, Matrix<T> L)
	{
		new Doolittle <T> (A.getSpaceDescription ()).decompose (A, U, L);
	}

	/**
	 * LU decomposition has 2 matrices for the intermediate result
	 * @param <T> data type
	 */
	public static class Decomposition <T>
	{
		/**
		 * L and U are initialized as square Matrix objects with type T
		 * @param size the number of columns and rows in the decomposition matrix objects
		 * @param mgr the data type manager
		 */
		public Decomposition (int size, SpaceManager<T> mgr)
		{
			L = new Matrix<T> (size, size, mgr);
			U = new Matrix<T> (size, size, mgr);
		}
		public Matrix<T> getL () { return L; }
		public Matrix<T> getU () { return U; }
		protected Matrix <T> L, U;
	}

	/**
	 * LU decomposition with a LUD result object
	 * @param A matrix to be decomposed
	 * @return LUD object with L / U
	 * @param <T> data type
	 */
	public static <T> Decomposition <T> decompose (Matrix<T> A)
	{
		int n = A.getEdgeCount ();
		Decomposition <T> lud = new Decomposition <T> (n, A.getSpaceDescription ());
		decompose (A, lud.U, lud.L);
		return lud;
	}

	/**
	 * LU solution using LUD object
	 * @param points vector of points to be interpolated
	 * @param using a decomposition object holding L and U
	 * @return the solution vector
	 * @param <T> data type
	 */
	public static <T> Vector<T> solve (Vector<T> points, Decomposition <T> using)
	{
		return solve (using.U, using.L, points);
	}

}
