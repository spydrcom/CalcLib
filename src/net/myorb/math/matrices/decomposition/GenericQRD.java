
package net.myorb.math.matrices.decomposition;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.matrices.*;

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
	public class QRDecomposition implements SolutionPrimitives.Decomposition
	{

		public QRDecomposition (SimpleStreamIO.TextSource source) { load (source); }
		public QRDecomposition (JsonSemantics.JsonValue source) { load (source); }

		public QRDecomposition (Matrix <T> A)
		{
			this.A = copyOf (A); this.N = A.getEdgeCount ();
			this.C = new Vector <T> (N, mgr); this.D = new Vector <T> (N, mgr);
		}

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
			StringBuffer JSON = new StringBuffer ().append ("{"); addPathTo (JSON);
			addTo (JSON, "A", A).append (","); addTo (JSON, "C", C).append (",");
			addTo (JSON, "D", D).append ("\n}");
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

		public VectorAccess <T> getRow (int row) { return A.getRowAccess (row); }
		public VectorAccess <T> getCol (int col) { return A.getColAccess (col); }

		//protected ExpressionSpaceManager <T> mgr;
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
				T AIK = divideInto (QRD.A, i, k, scale, mgr);
				sum = mgr.add (sum, mgr.multiply (AIK, AIK));
			}

			T sigma = arithmetic.SIGN ( sqrt (sum), QRD.getDiag (k) );
			QRD.D.set (k, mgr.negate (mgr.multiply (scale, sigma)));

			T AKK = addInto (QRD.A, k, k, sigma, mgr);
			QRD.C.set (k, mgr.multiply (sigma, AKK));

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


}

