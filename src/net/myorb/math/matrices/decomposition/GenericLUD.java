
package net.myorb.math.matrices.decomposition;

import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.matrices.Matrix;

import net.myorb.math.SpaceManager;

/**
 * implementation of Dolittle LU decomposition on generic data types
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class GenericLUD <T> extends GenericSupport
{


	public GenericLUD
	(SpaceManager <T> mgr) { this.mgr = mgr; }
	protected SpaceManager <T> mgr;


	/**
	 * representation of matrix Decomposition using this LUD algorithm set
	 */
	public static class LUDecomposition <T> implements SolutionPrimitives.Decomposition
	{

		LUDecomposition (Matrix <T> A)
		{
			this.N = A.getEdgeCount ();
			this.A = new Matrix <T> (N, N, A.getSpaceManager ());
			for (int i = 1; i <= N; i++) copyCells (A.getRow (i), i, this.A);
			this.P = new int [N+1]; for (int i = 0; i <= N; i++) P[i] = i;
			this.pivotCount = 0;
		}
		
		/**
		 * represent the effect of a pivot
		 * @param row1 row that has been found to need interchange
		 * @param row2 the row that will replace row1
		 */
		public void pivot (int row1, int row2)
		{
            int oldRow1 = P[row1]; P[row1] = P[row2]; P[row2] = oldRow1;	// pivot of P
            GenericLUD.interchange (A, row1, row2);							// pivoting rows of A
            pivotCount++;													// counting pivots starting from N
		}

		/**
		 * @param processed row being processed
		 * @param expected row expected for processed row
		 * @return 1 for expected row otherwise 0
		 */
		public int getPermutationCellFor (int processed, int expected)
		{
			return P[processed] == expected ? 1 : 0;
		}

		/**
		 * force maximum identifiable row to next position
		 * @param rowToPrep the next row of interest
		 */
		public void prepNextRow (int rowToPrep)
		{
	        int maxRowNum = maxRow (rowToPrep, A);
	        if (maxRowNum != rowToPrep) pivot (maxRowNum, rowToPrep);
		}

		/**
		 * @return the determinant of the permutation matrix
		 */
		public int detP ()
		{ return pivotCount % 2 == 0 ? 1 : -1; }
		protected int P [], pivotCount;										// Unit permutation matrix

		protected Matrix <T> A;
		protected int N;

	}


	/*
	 * decompose
	 */

	/**
	 * @param D the Decomposition to be processed
	 */
	public void decompose (LUDecomposition <T> D)
	{
		for (int i = 1; i <= D.N; i++)
		{
	        D.prepNextRow (i);

	        for (int j = i + 1; j <= D.N; j++)
	        {
	        	T factor = divideInto
	        		(
	        			D.A, j, i,
	        			D.A.get (i, i),
	        			mgr
	        		);
	            for (int k = i + 1; k <= D.N; k++)
	            {
	                T term = mgr.multiply
	                	(
	                		factor, D.A.get (i, k)
	                	);
	                reduceBy (D.A, j, k, term, mgr);
	            }
	        }
		}
	}

	/**
	 * @param A the matrix to be decomposed
	 * @return the resulting Decomposition object
	 */
	public LUDecomposition <T> decompose (Matrix <T> A)
	{
		LUDecomposition <T> D;
		decompose (D = new LUDecomposition <T> (A));
		return D;
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
			LUDecomposition <T> D, SolutionPrimitives.Content <T> b
		)
	{
		SolutionPrimitives.Content <T> x =
			new SolutionPrimitives.Content <T> (b.size (), mgr);
		for (int i = 1; i <= D.N; i++)
		{
			x.set (i, b.get (D.P [i]));
			dot (i, D.A, x, 1, i-1);
		}
		for (int i = D.N; i >= 1; i--)
		{
			dot (i, D.A, x, i+1, D.N);
			divideInto (x, i, D.A.get (i, i), mgr);
		}
		return x;
	}
	void dot (int row, Matrix <T> A, SolutionPrimitives.Content <T> x, int start, int end)
	{
        T sum = dot (A.getRowAccess (row), x, start, end, mgr);
		reduceBy (x, row, sum, mgr);
	}

	/**
	 * @param D the decomposed translation matrix description
	 * @param b the expected result vector
	 * @return the computed solution
	 */
	public SolutionPrimitives.SolutionVector solve
		(
			LUDecomposition <T> D, SolutionPrimitives.RequestedResultVector b
		)
	{
		@SuppressWarnings("unchecked")
		SolutionPrimitives.Content <T> result =
				(SolutionPrimitives.Content <T>) b;
		return solve (D, result);
	}


	/*
	 * inverse
	 */

	/**
	 * @param D the decomposed matrix description
	 * @return the computed inverse of the matrix
	 */
	public Matrix <T> inv (LUDecomposition <T> D)
	{
		Matrix <T> IA = new Matrix <T> (D.N, D.N, mgr);

		for (int j = 1; j <= D.N; j++)
		{
			for (int i = 1; i <= D.N; i++)
	        {
				int p = D.getPermutationCellFor (i, j);
	            IA.set (i, j, mgr.newScalar (p));
	            dot (i, j, D.A, IA, 1, i-1);
	        }

	        for (int i = D.N; i > 0; i--)
	        {
	            dot (i, j, D.A, IA, i+1, D.N);
	            divideInto (IA, i, j, D.A.get (i, i), mgr);
	        }
		}

	    return IA;
	}
	void dot (int row, int col, Matrix <T> l, Matrix <T> r, int start, int end)
	{
        T sum = dot (l.getRowAccess (row), r.getColAccess (col), start, end, mgr);
        reduceBy (r, row, col, sum, mgr);
	}

	/**
	 * @param source the matrix to be inverted
	 * @return the computed inverse of the matrix
	 */
	public Matrix <T> inv (Matrix <T> source)
	{
		return inv (decompose (source));
	}


	/*
	 * determinant
	 */

	/**
	 * @param D the decomposed matrix description
	 * @return determinant of the decomposed permutations matrix
	 */
	public T detP (LUDecomposition <T> D)
	{
		return mgr.newScalar (D.detP ());
	}

	/**
	 * @param D the decomposed matrix description
	 * @return the determinant of the matrix
	 */
	public T det (LUDecomposition <T> D)
	{
		T result = detP (D);
		for (int i = 1; i <= D.N; i++)
			result = mgr.multiply (result, D.A.get (i, i));
		return result;
	}

	/**
	 * @param source the matrix to be evaluated
	 * @return the determinant of the matrix
	 */
	public T det (Matrix <T> source)
	{
		return det (decompose (source));
	}


}

