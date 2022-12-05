
package net.myorb.math.matrices.decomposition;

import net.myorb.math.linalg.*;
import net.myorb.math.matrices.*;

import net.myorb.math.expressions.algorithms.ClMathBIF;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager;

import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.abstractions.SimpleStreamIO;

/**
 * Crouts LU Decomposition algorithm implementation
 * @param <T> data type used in Matrix objects
 * @author Michael Druckman
 */
public class Crouts <T> extends CommonLUD <T>
{


	public Crouts (ExpressionSpaceManager <T> mgr) { super (mgr); }


	/**
	 * representation of matrix Decomposition using this LUD algorithm set
	 */
	public class CroutsDecomposition extends DecompositionPrimitives
			implements SolutionPrimitives.Decomposition, ClMathBIF.FieldAccess
	{

		public CroutsDecomposition (SimpleStreamIO.TextSource source) { load (source); }
		public CroutsDecomposition (JsonSemantics.JsonValue source) { load (source); }

		public CroutsDecomposition (Matrix <T> A)
		{
			this.copySourceMatrix (A);
			this.buildPermutationMatrixDescription ();
			this.vm = new ValueManager <T> ();
			this.scale = scaling (A);
		}
		protected ValueManager <T> vm;
		protected Vector <T> scale;

		/**
		 * force biggest row to pivot point
		 * @param pivotPoint the current row being processed
		 * @param maxRow largest seen so far
		 * @return largest now
		 */
		public int nextPivot (int pivotPoint, int maxRow)
		{
			T biggest = mgr.getZero (), reduction, temp;

			for (int i = pivotPoint; i <= N; i++)
			{
				reduction = arithmetic.abs
					(reduceByProduct (i, pivotPoint, pivotPoint, A));
				temp = mgr.multiply (scale.get (i), reduction);
				if (mgr.lessThan (biggest, temp))
				{ biggest = temp; maxRow = i; }
			}

			if (pivotPoint != maxRow)
			{
				interchange (A, maxRow, pivotPoint);
				scale.set (maxRow, scale.get (pivotPoint));
				pivotCount++;
			}

			P [pivotPoint] = maxRow;
			return maxRow;
		}

		/**
		 * @param position current being evaluated
		 * @param in the solution vector being calculated
		 * @return next value at position
		 */
		public T adjust (int position, Vector <T> in)
		{
			int pivotAtPosition = P[position];
			T valueAtPivot = in.get (pivotAtPosition);
			in.set (pivotAtPosition, in.get (position));
			return valueAtPivot;
		}

		/**
		 * @param x solution vector
		 * @param row row of decomposed matrix
		 * @param col column for dot product
		 * @param from running summation
		 * @return next summation value
		 */
		public T reduceSumBy
		(Vector <T> x, int row, int col, T from)
		{
			return reduce
			(
				from,
				mgr.multiply (A.get (row, col),
				x.get (col)), mgr
			);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathBIF.FieldAccess#getFieldNamed(java.lang.String)
		 */
		public GenericValue getFieldNamed (String name)
		{
			switch (name.charAt (0))
			{
				case 'A': return vm.newMatrix (A);
				case 'D': return vm.newDiscreteValue (det ());
				case 'P': return vm.newDimensionedValue (Vector.toVector (P, mgr).getElementsList ());
				default: throw new RuntimeException ("Field not recognized: " + name);
			}
		}

	}


	/*
	 * decompose
	 */

	/**
	 * @param D the Decomposition to be processed
	 */
	public void decompose (CroutsDecomposition D)
	{
		int maxRow = 1;
		for (int j = 1; j <= D.N; j++)
		{

			for (int i = 1; i < j; i++)
			{
				reduceByProduct (i, j, i, D.A);
			}

			D.nextPivot (j, maxRow);

			if (j != D.N)
			{
				T ajj = D.getDiag (j);
				for (int i = j + 1; i <= D.N; i++)
				{ divideInto (D.A, i, j, ajj, mgr); }
			}

		}
	}

	/**
	 * @param A the matrix to be decomposed
	 * @return the resulting Decomposition object
	 */
	public CroutsDecomposition decomposeUsingCrouts (Matrix <T> A)
	{
		CroutsDecomposition D;
		decompose (D = new CroutsDecomposition (A));
		return D;
	}
	public SolutionPrimitives.Decomposition decompose (Matrix <T> A)
	{ return decomposeUsingCrouts (A); }


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
			CroutsDecomposition D, SolutionPrimitives.Content <T> b
		)
	{
		int N = D.N;
		SolutionPrimitives.Content <T> x =
			new SolutionPrimitives.Content <T> (b.size (), mgr);
		for (int i = 1; i <= N; i++) x.set (i, b.get (i));

		T sum; int startingIndex = 0;

		for (int i = 1; i <= N; i++)
		{
			sum = D.adjust (i, x);

			if (startingIndex != 0)
			{
				for (int j = startingIndex; j <= i - 1; j++)
					sum = D.reduceSumBy (x, i, j, sum);
			} else startingIndex = mgr.isZero (sum) ? 0 : i;

			x.set (i, sum);
		}
	
		for (int i = N; i >= 1; i--)
		{
			sum = x.get (i);
			for (int j = i + 1; j <= N; j++)
				sum = D.reduceSumBy (x, i, j, sum);
			x.set (i, sum); divideInto (x, i, D.getDiag (i), mgr);
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
			CroutsDecomposition D, SolutionPrimitives.RequestedResultVector b
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
		return decomposeUsingCrouts (source).inv ();
	}


	/*
	 * determinant
	 */

	/**
	 * compute determinant
	 * @param source the matrix to be evaluated
	 * @return the determinant of the matrix
	 */
	public T det (Matrix <T> source)
	{
		return decomposeUsingCrouts (source).det ();
	}


	/*
	 * Decomposition transport
	 */

	/**
	 * restore a stored CroutsDecomposition
	 * @param source the location of the stored copy
	 * @return the CroutsDecomposition
	 */
	public CroutsDecomposition restore (SimpleStreamIO.TextSource source)
	{
		return new CroutsDecomposition (source);
	}

	/**
	 * restore a JSON stored QRDecomposition
	 * @param source the location of the stored copy
	 * @return the LUDecomposition
	 */
	public CroutsDecomposition restore (JsonSemantics.JsonValue source)
	{
		return new CroutsDecomposition (source);		
	}


}

