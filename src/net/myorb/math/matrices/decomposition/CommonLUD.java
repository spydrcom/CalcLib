
package net.myorb.math.matrices.decomposition;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.Matrix;

/**
 * support for linear algebra algorithms specific to LU Decomposition
 * @param <T> data type used in Matrix objects
 * @author Michael Druckman
 */
public class CommonLUD <T> extends GenericSupport <T>
{


	public CommonLUD
	(ExpressionSpaceManager <T> mgr)
	{ super (mgr); }


	/**
	 * implementation of permutation matrix and others LUD primitives
	 */
	public class DecompositionPrimitives
	{


		/**
		 * @param A the matrix being decomposed
		 */
		public void copySourceMatrix (Matrix <T> A)
		{
			this.N = A.getEdgeCount ();
			this.A = new Matrix <T> (N, N, mgr);
			for (int i = 1; i <= N; i++) copyCells (A.getRow (i), i, this.A);
		}
		protected Matrix <T> A;
		protected int N;


		/*
		 * Unit permutation matrix management
		 */

		/**
		 * represent the effect of a pivot
		 * @param row1 row that has been found to need interchange
		 * @param row2 the row that will replace row1
		 */
		public void pivot (int row1, int row2)
		{
            int oldRow1 = P[row1]; P[row1] = P[row2]; P[row2] = oldRow1;
            interchange (A, row1, row2);
            pivotCount++;
		}

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
		 * tracking for pivot executions
		 */
		public void buildPermutationMatrixDescription ()
		{
			this.P = new int [N+1];
			for (int i = 0; i <= N; i++) P[i] = i;
			this.pivotCount = 0;
		}
		protected int P [], pivotCount;										// 


		/*
		 * determinant computation
		 */

		/**
		 * @return determinant of the decomposed permutations matrix
		 */
		public T permutationDet ()
		{
			return mgr.newScalar (detP ());
		}

		/**
		 * @return the determinant of the matrix
		 */
		public T det ()
		{
			T result =
				permutationDet ();
			for (int i = 1; i <= N; i++)
				result = mgr.multiply (result, getDiag (i));
			return result;
		}

		/**
		 * compute the determinant of the permutation matrix
		 * @return the determinant of the permutation matrix
		 */
		public int detP () { return pivotCount % 2 == 0 ? 1 : -1; }

		/**
		 * get cell from diagonal
		 * @param item identify (item,item)
		 * @return cell value
		 */
		public T getDiag (int item) { return A.get (item, item); }


		/*
		 * inverse algorithm
		 */

		/**
		 * @return the computed inverse of the matrix
		 */
		public Matrix <T> inv ()
		{
			Matrix <T> IA = new Matrix <T> (N, N, mgr);

			for (int j = 1; j <= N; j++)
			{
				VectorAccess <T> jTHcol = IA.getColAccess (j);

				for (int i = 1; i <= N; i++)
		        {
		            setPermutationCellFor (IA, i, j);
		            reduceByDotProduct (i, jTHcol, 1, i - 1);
		        }

		        for (int i = N; i > 0; i--)
		        {
		            reduceByDotProduct (i, jTHcol, i + 1, N);
		            divideInto (jTHcol, i, getDiag (i), mgr);
		        }
			}

		    return IA;
		}


		/*
		 * dot product reduction algorithm
		 */

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
	        T colVecProduct =
	        	dot (A.getRowAccess (row), colVec, start, end);
	        reduceBy (colVec, row, colVecProduct, mgr);
		}


		/*
		 * Decomposition transport
		 */

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			StringBuffer JSON = new StringBuffer ().append ("{"); addPathTo (JSON);
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


}
