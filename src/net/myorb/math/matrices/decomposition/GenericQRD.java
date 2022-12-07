
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.*;
import net.myorb.math.linalg.SolutionPrimitives;

import net.myorb.math.expressions.algorithms.ClMathBIF;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonSemantics;

import net.myorb.data.abstractions.SimpleStreamIO;

/**
 * implementation of QR decomposition on generic data types
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class GenericQRD <T> extends GenericSupport <T>
{
	

	public GenericQRD (ExpressionSpaceManager <T> mgr) { super (mgr); }


	/**
	 * @param x parameter to SQRT
	 * @return computed SQRT
	 */
	public T sqrt (T x)
	{ return mgr.convertFromDouble (Math.sqrt (mgr.convertToDouble (x))); }
	//TODO: need to introduce library for generic T


	/**
	 * representation of matrix Decomposition using this QRD algorithm set
	 */
	public class QRDecomposition implements SolutionPrimitives.Decomposition, ClMathBIF.FieldAccess
	{

		public QRDecomposition (Matrix <T> A)
		{ this (A.getEdgeCount ()); this.A = copyOf (A); }
		public QRDecomposition (SimpleStreamIO.TextSource source) { load (source); }
		public QRDecomposition (JsonSemantics.JsonValue source) { load (source); }

		public QRDecomposition (int N)
		{
			this.vm = new ValueManager <T> ();
			this.C = new Vector <T> (N, mgr);
			this.D = new Vector <T> (N, mgr);
			this.N = N;
		}
		protected ValueManager <T> vm;

		/**
		 * get cell from diagonal
		 * @param item identify (item,item)
		 * @return cell value
		 */
		public T getDiag (int item) { return A.get (item, item); }

		/**
		 * compute tau from dot product
		 * @param access vector for dot product
		 * @param col the column of the Decomposition matrix
		 * @return computed value of tau
		 */
		public T getTau (VectorAccess <T> access, int col)
		{
			T product = dot (access, getCol (col), col, N);
			return mgr.multiply (product, mgr.invert (C.get (col)));
		}

		/**
		 * @param access vector being reduced
		 * @param starting the starting column of the Decomposition matrix
		 */
		public void reduceByTauProduct (VectorAccess <T> access, int starting)
		{
			reduceByTauProduct
			(
				getTau (access, starting), access, getCol (starting), starting
			);
		}

		/**
		 * @param tau the computed value of tau
		 * @param access the vector being reduced
		 * @param col the values of the Decomposition matrix column
		 * @param starting column of the Decomposition matrix
		 */
		public void reduceByTauProduct
		(T tau, VectorAccess <T> access, VectorAccess <T> col, int starting)
		{
			for (int i = starting; i <= N; i++)
			{
				reduceBy (access, i, mgr.multiply (tau, col.get (i)), mgr);
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			return toJson ().toString ();
		}

		/**
		 * @return JSON representation of QRDecomposition
		 */
		public JsonValue toJson ()
		{
			JsonSemantics.JsonObject representation = new JsonSemantics.JsonObject ();
			addTo (representation, "A", A); addTo (representation, "C", C); addTo (representation, "D", D);
			representation.addMemberNamed ("Solution", new JsonSemantics.JsonString (solutionClassPath));
			return representation;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#store(net.myorb.data.abstractions.SimpleStreamIO.TextSink)
		 */
		public void store (SimpleStreamIO.TextSink to) { storeDecomposition (toJson (), to); }

		/**
		 * transport representation (store) for use in CalcTools
		 * @return decomposition represented as matrix
		 */
		public Matrix <T> asMatrix ()
		{
			Matrix <T> M = new Matrix <T> (N+2, N, mgr);
			copyCells (C, N+1, M); copyCells (D, N+2, M);
			copyRows (N, A, M);
			return M;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#load(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
		 */
		public void load (SimpleStreamIO.TextSource from)
		{
			parseDecomposedMatrix (from); loadFromJSON ();
		}
		public void load (JsonSemantics.JsonValue source)
		{
			identifySource (source); loadFromJSON ();
		}
		public void loadFromJSON ()
		{
			this.A = getMatrix ("A");
			this.C = getVector ("C");
			this.D = getVector ("D");
			this.N = this.C.size ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathBIF.FieldAccess#getFieldNamed(java.lang.String)
		 */
		public GenericValue getFieldNamed (String name)
		{
			switch (name.charAt (0))
			{
				case 'A': return vm.newMatrix (A);
				case 'C': return vm.newDimensionedValue (C.getElementsList ());
				case 'D': return vm.newDimensionedValue (D.getElementsList ());
				default: throw new RuntimeException ("Field not recognized: " + name);
			}
		}

		public VectorAccess <T> getRow (int row) { return A.getRowAccess (row); }
		public VectorAccess <T> getCol (int col) { return A.getColAccess (col); }

		protected Vector <T> C, D;
		protected Matrix <T> A;
		protected int N;

	}


	/*
	 * decompose
	 */

	/**
	 * the primitive solution interface entry point for decompose
	 * @param A the matrix to be decomposed using QR algorithms
	 * @return the resulting Decomposition object
	 */
	public QRDecomposition decompose (Matrix <T> A)
	{
		QRDecomposition D;
		decompose (D = new QRDecomposition (A));
		return D;
	}

	/**
	 * perform the QRDecomposition on a new matrix
	 * @param QRD the Decomposition to be processed
	 */
	public void decompose (QRDecomposition QRD)
	{
		int N = QRD.N;

		for (int k = 1; k < N; k++)
		{
			int maxRowNum = maxRow (k, QRD.A);
			T scale = QRD.getRow (maxRowNum).get (k);

			T sum = mgr.getZero ();
			for (int i = k; i <= N; i++)
			{
				T Aik = divideInto (QRD.A, i, k, scale, mgr);
				sum = mgr.add (sum, mgr.pow (Aik, 2));
			}

			T sigma = arithmetic.SIGN ( sqrt (sum), QRD.getDiag (k) );
			QRD.D.set (k, mgr.negate (mgr.multiply (scale, sigma)));

			T Akk = addInto (QRD.A, k, k, sigma, mgr);
			QRD.C.set (k, mgr.multiply (sigma, Akk));

			for (int j = k + 1; j <= N; j++)
			{
				QRD.reduceByTauProduct (QRD.getCol (j), k);
			}
		}

		QRD.D.set (N, QRD.getDiag (N));
	}


	/*
	 * solution
	 */

	/**
	 * the primitive solution interface entry point for solve
	 * @param D the decomposed translation matrix description
	 * @param b the expected result vector
	 * @return the computed solution
	 */
	public SolutionPrimitives.SolutionVector solve
		(
			QRDecomposition D, SolutionPrimitives.RequestedResultVector b
		)
	{
		@SuppressWarnings("unchecked")
		SolutionPrimitives.Content <T> result =
				( SolutionPrimitives.Content <T> ) b;
		return solve (D, result);
	}

	/**
	 * full solution A x = b
	 * @param D the decomposed translation matrix description
	 * @param b the expected result vector
	 * @return the computed solution
	 */
	public SolutionPrimitives.SolutionVector solve
		(
			QRDecomposition D, SolutionPrimitives.Content <T> b
		)
	{
		SolutionPrimitives.Content <T> x =
				new SolutionPrimitives.Content <T> (b.size (), mgr);
		copyCells (b, x);

		for (int j = 1; j < D.N; j++)
		{ D.reduceByTauProduct (x, j); }
		rsolve (D, x);

		return x;
	}

	/**
	 * solution along the diagonal
	 * @param D the decomposed translation matrix description
	 * @param x the solution vector being built
	 */
	public void rsolve
		(
			QRDecomposition D, SolutionPrimitives.Content <T> x
		)
	{
		int N = D.N;
        divideInto (x, N, D.D.get (N), mgr);

		for (int i = N - 1; i > 0; i--)
		{
			T dp = dot (x, D.getRow (i), i+1, N);
			T dif = mgr.add (x.get (i), mgr.negate (dp));
			x.set (i, mgr.multiply (dif, mgr.invert (D.D.get (i))));
		}
	}


	/*
	 * Decomposition transport
	 */


	/**
	 * restore a text stored QRDecomposition
	 * @param source the location of the stored copy
	 * @return the QRDecomposition
	 */
	public QRDecomposition restore (SimpleStreamIO.TextSource source)
	{
		return new QRDecomposition (source);
	}

	/**
	 * restore a JSON stored QRDecomposition
	 * @param source the location of the stored copy
	 * @return the QRDecomposition
	 */
	public QRDecomposition restore (JsonSemantics.JsonValue source)
	{
		return new QRDecomposition (source);		
	}

	/**
	 * transport representation (restore) for use in CalcTools
	 * @param from matrix representation generated by asMatrix
	 * @return the QRDecomposition loaded
	 */
	public QRDecomposition load (Matrix <T> from)
	{
		QRDecomposition D =
			new QRDecomposition
				(from.columnCount ());
		D.A = new Matrix <T> (D.N, D.N, mgr);
		copyCells (from.getRow (D.N+1), D.C);
		copyCells (from.getRow (D.N+2), D.D);
		copyRows (D.N, from, D.A);
		return D;
	}

}

