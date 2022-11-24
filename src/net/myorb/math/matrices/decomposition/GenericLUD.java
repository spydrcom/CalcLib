
package net.myorb.math.matrices.decomposition;

import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.matrices.*;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.data.abstractions.SimpleStreamIO;

/**
 * implementation of Dolittle LU decomposition on generic data types
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class GenericLUD <T> extends CommonLUD <T>
{


	public GenericLUD (ExpressionSpaceManager <T> mgr) { super (mgr); }


	/**
	 * representation of matrix Decomposition using this LUD algorithm set
	 */
	public class LUDecomposition extends DecompositionPrimitives
		implements SolutionPrimitives.Decomposition
	{

		public LUDecomposition
			(
				SimpleStreamIO.TextSource source
			)
		{
			load (source);
		}

		public LUDecomposition (Matrix <T> A)
		{
			this.copySourceMatrix (A);
			this.buildPermutationMatrixDescription ();
		}

		/**
		 * force maximum identifiable row to next position
		 * @param rowToPrep the next row of interest to evaluate
		 */
		public void prepNextRow (int rowToPrep)
		{
	        int maxRowNum = maxRow (rowToPrep, A);
	        if (maxRowNum != rowToPrep) pivot (maxRowNum, rowToPrep);
		}

		/**
		 * adjust the request vector to initialize the solution
		 * @param requested the requested vector for the solution
		 * @param solution the solution vector being built
		 */
		public void reduceInSolution
		(VectorAccess <T> requested, VectorAccess <T> solution)
		{
			for (int item = 1; item <= N; item++)
			{
				solution.set (item, requested.get (P [item]));
				reduceByDotProduct (item, solution, 1, item - 1);
			}
		}

	}


	/*
	 * decompose
	 */

	/**
	 * @param D the Decomposition to be processed
	 */
	public void decompose (LUDecomposition D)
	{
		int N = D.N;

		for (int i = 1; i <= N; i++)
		{
	        D.prepNextRow (i);

	        for (int j = i + 1; j <= N; j++)
	        {
	        	T factor = divideInto
	        		(
	        			D.A, j, i,
	        			D.getDiag (i),
	        			mgr
	        		);
	            for (int k = i + 1; k <= N; k++)
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
	public LUDecomposition decompose (Matrix <T> A)
	{
		LUDecomposition D;
		decompose (D = new LUDecomposition (A));
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
			LUDecomposition D, SolutionPrimitives.Content <T> b
		)
	{
		int N = D.N;
		SolutionPrimitives.Content <T> x =
			new SolutionPrimitives.Content <T> (b.size (), mgr);
		D.reduceInSolution (b, x);

		for (int i = N; i > 0; i--)
		{
			D.reduceByDotProduct (i, x, i + 1, N);
			divideInto (x, i, D.getDiag (i), mgr);
		}
		return x;
	}

	/**
	 * @param D the decomposed translation matrix description
	 * @param b the expected result vector
	 * @return the computed solution
	 */
	public SolutionPrimitives.SolutionVector solve
		(
			LUDecomposition D, SolutionPrimitives.RequestedResultVector b
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
	 * @param source the matrix to be inverted
	 * @return the computed inverse of the matrix
	 */
	public Matrix <T> inv (Matrix <T> source)
	{
		return decompose (source).inv ();
	}


	/*
	 * determinant
	 */

	/**
	 * @param source the matrix to be evaluated
	 * @return the determinant of the matrix
	 */
	public T det (Matrix <T> source)
	{
		return decompose (source).det ();
	}


	/*
	 * Decomposition transport
	 */

	/**
	 * restore a stored LUDecomposition
	 * @param source the location of the stored copy
	 * @return the LUDecomposition
	 */
	public LUDecomposition restore (SimpleStreamIO.TextSource source)
	{
		return new LUDecomposition (source);
	}


}

