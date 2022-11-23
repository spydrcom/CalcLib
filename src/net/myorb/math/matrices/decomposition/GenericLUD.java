
package net.myorb.math.matrices.decomposition;

import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.matrices.*;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.math.SpaceManager;

/**
 * implementation of Dolittle LU decomposition on generic data types
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class GenericLUD <T> extends GenericSupport <T>
{


	public GenericLUD (ExpressionSpaceManager <T> mgr) { super (mgr); }


	/**
	 * representation of matrix Decomposition using this LUD algorithm set
	 */
	public class LUDecomposition implements SolutionPrimitives.Decomposition
	{

		public LUDecomposition
			(
				SimpleStreamIO.TextSource source,
				ExpressionSpaceManager <T> mgr
			)
		{
			this.mgr = mgr; load (source);
		}

		public LUDecomposition (Matrix <T> A)
		{
			this.copySourceMatrix (A, A.getSpaceManager ());
			this.builtPermutationMatrixDescription ();
		}
		protected ExpressionSpaceManager <T> mgr;
		

		/**
		 * @param A the matrix being decomposed
		 * @param mgr the data type manager
		 */
		public void copySourceMatrix (Matrix <T> A, SpaceManager <T> mgr)
		{
			this.N = A.getEdgeCount ();
			this.A = new Matrix <T> (N, N, mgr);
			for (int i = 1; i <= N; i++) copyCells (A.getRow (i), i, this.A);
			this.mgr = (ExpressionSpaceManager <T>) mgr;
		}
		protected Matrix <T> A;
		protected int N;

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
		 * get cell from diagonal
		 * @param item identify (item,item)
		 * @return cell value
		 */
		public T getDiag (int item) { return A.get (item, item); }

		/**
		 * @param processed row being processed
		 * @param expected row expected for processed row
		 * @return 1 for expected row otherwise 0
		 */
		public T getPermutationCellFor (int processed, int expected)
		{
			return mgr.newScalar (P[processed] == expected ? 1 : 0);
		}

		/**
		 * identify appropriate permutation entry
		 * @param A the matrix being built currently working on specified cell
		 * @param processing the row currently of interest
		 * @param expecting the column that should match
		 */
		public void setPermutationCellFor
		(Matrix <T> A, int processing, int expecting)
		{
            A.set
            (
            	processing, expecting,
            	getPermutationCellFor (processing, expecting)
            );
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
		 * compute dot product of segment
		 * - then use to reduce identified cell
		 * @param row the row being reduction processed
		 * @param colVec access to dot product column vector
		 * @param start the starting point of the vector segment
		 * @param end the ending point of the vector segment
		 */
		public void reduceByDotProduct
		(int row, VectorAccess <T> colVec, int start, int end)
		{
	        T sum = GenericSupport.dot
	        	(A.getRowAccess (row), colVec, start, end, mgr);
	        reduceBy (colVec, row, sum, mgr);
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

		/**
		 * @return the determinant of the permutation matrix
		 */
		public int detP () { return pivotCount % 2 == 0 ? 1 : -1; }

		/**
		 * tracking for pivot executions
		 */
		public void builtPermutationMatrixDescription ()
		{
			this.P = new int [N+1];
			for (int i = 0; i <= N; i++) P[i] = i;
			this.pivotCount = 0;
		}
		protected int P [], pivotCount;										// Unit permutation matrix

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			StringBuffer JSON = new StringBuffer ().append ("{");
			addTo (JSON, "A", A).append (",\n  \"P\" : ").append (toList (P)).append (",")
				.append ("\n  \"pivots\" : ").append (pivotCount).append ("\n}");
			return JSON.toString ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#store(net.myorb.data.abstractions.SimpleStreamIO.TextSink)
		 */
		public void store (SimpleStreamIO.TextSink to)
		{
			storeDecomposition (toString (), to);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#load(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
		 */
		public void load (SimpleStreamIO.TextSource from)
		{
			parseDecomposedMatrix (from);
			this.P = toArray (getIndex ("P"));
			this.pivotCount = getValue ("pivots").intValue ();
			this.A = getMatrix ("A");
			this.N = P.length - 1;
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
	 * @param D the decomposed matrix description
	 * @return the computed inverse of the matrix
	 */
	public Matrix <T> inv (LUDecomposition D)
	{
		int N = D.N;
		Matrix <T> IA = new Matrix <T> (N, N, mgr);

		for (int j = 1; j <= N; j++)
		{
			VectorAccess <T> jTHcol = IA.getColAccess (j);

			for (int i = 1; i <= N; i++)
	        {
	            D.setPermutationCellFor (IA, i, j);
	            D.reduceByDotProduct (i, jTHcol, 1, i - 1);
	        }

	        for (int i = N; i > 0; i--)
	        {
	            D.reduceByDotProduct (i, jTHcol, i + 1, N);
	            divideInto (jTHcol, i, D.getDiag (i), mgr);
	        }
		}

	    return IA;
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
	public T detP (LUDecomposition D)
	{
		return mgr.newScalar (D.detP ());
	}

	/**
	 * @param D the decomposed matrix description
	 * @return the determinant of the matrix
	 */
	public T det (LUDecomposition D)
	{
		T result = detP (D);
		for (int i = 1; i <= D.N; i++)
			result = mgr.multiply (result, D.getDiag (i));
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
		return new LUDecomposition (source, mgr);
	}


}

