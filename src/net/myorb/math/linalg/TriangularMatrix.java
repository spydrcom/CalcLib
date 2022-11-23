
package net.myorb.math.linalg;

import net.myorb.math.matrices.*;
import net.myorb.math.matrices.decomposition.Doolittle;

import net.myorb.data.abstractions.SimpleStreamIO.TextSource;
import net.myorb.data.abstractions.SimpleStreamIO;

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
	public static class Decomposition <T> implements SolutionPrimitives.Decomposition
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

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#store(net.myorb.data.abstractions.SimpleStreamIO.TextSink)
		 */
		public void store (SimpleStreamIO.TextSink to)
		{
			throw new RuntimeException ("not implemented");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#load(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
		 */
		public void load (SimpleStreamIO.TextSource from)
		{
			throw new RuntimeException ("not implemented");
		}

		public String toString ()
		{
			print ("L", L); print ("U", U);
			return "see system OUT";
		}
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

	public static <T> void print (String name, Matrix<T> m)
	{
		System.out.println ();
		System.out.println (name);
		for (int r=1;r<=m.rowCount();r++)
		{
			for (int c=1;c<=m.columnCount();c++)
				System.out.print (m.get (r, c) + "\t");
			System.out.println ();
		}
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

	/**
	 * @return a Linear Algebra Solution Primitives object based on Doolittle
	 */
	public static <T> SolutionPrimitives <T> getSolutionPrimitives ()
	{
		return new TriangularPrimitives <T> ();
	}

}


/**
 * Solution Primitives object based on Doolittle LUD
 * @param <T> data type of operations
 */
class TriangularPrimitives <T>
	implements SolutionPrimitives <T>, SolutionPrimitives.Determinable <T>, SolutionPrimitives.Invertable <T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#decompose(net.myorb.math.matrices.Matrix)
	 */
	@Override
	public SolutionPrimitives.Decomposition
		decompose (Matrix <T> A)
	{
		return TriangularMatrix.decompose (A);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#solve(net.myorb.math.linalg.SolutionPrimitives.Decomposition, net.myorb.math.linalg.SolutionPrimitives.RequestedResultVector)
	 */
	@Override @SuppressWarnings("unchecked")
	public SolutionPrimitives.SolutionVector solve
		(
			SolutionPrimitives.Decomposition d,
			SolutionPrimitives.RequestedResultVector b
		)
	{
		SolutionPrimitives.Content <T> points = (SolutionPrimitives.Content <T>) b;
		TriangularMatrix.Decomposition <T> decomposition = (TriangularMatrix.Decomposition <T>) d;
		Vector <T> solution = TriangularMatrix.solve (points, decomposition);
		return new SolutionPrimitives.Content <T> (solution);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Determinable#det(net.myorb.math.matrices.Matrix)
	 */
	@Override public T det (Matrix <T> source)
	{
		TriangularMatrix.Decomposition <T> decomposition = TriangularMatrix.decompose (source);
		return new Triangular <T> (source.getSpaceManager ()).det (decomposition.U, decomposition.L);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Invertable#inv(net.myorb.math.matrices.Matrix)
	 */
	@Override public Matrix <T> inv (Matrix <T> source)
	{
		return new InversionSolution <T> (this).inv (source);
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#restore(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
	 */
	public SolutionPrimitives.Decomposition restore (TextSource from)
	{
		throw new RuntimeException ("unimplemented");
	}

}
