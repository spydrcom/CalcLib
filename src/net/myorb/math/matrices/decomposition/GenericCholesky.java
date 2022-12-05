
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.*;
import net.myorb.math.linalg.SolutionPrimitives;

import net.myorb.math.expressions.algorithms.ClMathBIF;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager;

import net.myorb.data.abstractions.SimpleStreamIO.TextSource;
import net.myorb.data.abstractions.SimpleStreamIO.TextSink;

import net.myorb.data.notations.json.JsonSemantics;

/**
 * implementation of Banachiewicz Cholesky decomposition on generic data types
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public class GenericCholesky <T> extends GenericSupport <T>
{


	public GenericCholesky (ExpressionSpaceManager <T> mgr)
	{ super (mgr); this.matOP = new MatrixOperations <T> (mgr); }
	protected MatrixOperations <T> matOP;


	/**
	 * representation of matrix Decomposition using this Cholesky algorithm set
	 */
	public class CholeskyDecomposition
		implements SolutionPrimitives.Decomposition, ClMathBIF.FieldAccess
	{

		public CholeskyDecomposition (TextSource source) { load (source); }
		public CholeskyDecomposition (JsonSemantics.JsonValue source) { load (source); }

		public CholeskyDecomposition (Matrix <T> A)
		{
			this.vm = new ValueManager <T> ();
			this.N = A.getEdgeCount ();
			this.C = new Matrix <T> (N, N, mgr);
		}
		protected ValueManager <T> vm;
		protected int N;

		/**
		 * copy diag of C
		 */
		public void setS ()
		{
			this.S = new Matrix <T> (N, N, mgr);
			matOP.copyDiag (0, C, S);
		}
		protected Matrix <T> S;

		/**
		 * @return C * inv(S)
		 */
		public Matrix <T> getL ()
		{
			return matOP.product (this.C, matOP.pseudoInv (this.S));
		}

		public Matrix <T> getS () { return this.S; }
		public Matrix <T> getD () { return matOP.SQ (this.S); }
		public Matrix <T> getC () { return this.C; }
		protected Matrix <T> C;

		/**
		 * @param B vector to solve
		 * @return solution
		 */
		public SolutionPrimitives.SolutionVector solve
			(
				SolutionPrimitives.Content <T> B
			)
		{
			Matrix <T> b; int n = B.size ();
			matOP.setCol (1, b = new Matrix <T> (n, 1, mgr), B);
			return new SolutionPrimitives.Content <T>
			(solve (b).getColAccess (1), mgr);
		}

		/**
		 * full matrix of solutions
		 * @param B requested results in columns
		 * @return solutions in columns
		 */
		public Matrix <T> solve
			(
				Matrix <T> B
			)
		{
			int n = C.rowCount (),
				nx = B.columnCount ();
			Matrix <T> X = matOP.copy (B);

			for (int c = 1; c <= nx; c++)
			{

				// Solve L*Y = B;
				for (int i = 1; i <= n; i++)
				{
					T dp = dot
						(
							C.getRowAccess (i),
							X.getColAccess (c),
							1, i-1
						);
					T dif = mgr.add (B.get (i, c), mgr.negate (dp));
					X.set(i, c, mgr.multiply (dif, mgr.invert (C.get (i, i))));
				}

				// Solve L'*X = Y;
				for (int i = n; i > 0; i--)
				{
					T dp = dot
						(
							C.getColAccess (i),
							X.getColAccess (c),
							i+1, n
						);
					T dif = mgr.add (X.get(i, c), mgr.negate (dp));
					X.set (i, c, mgr.multiply (dif, mgr.invert (C.get (i, i))));
				}

			}

			return X;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			StringBuffer JSON = new StringBuffer ().append ("{"); addPathTo (JSON);
			addTo (JSON, "C", C).append (","); addTo (JSON, "L", getL ()).append (",");
			return addTo (JSON, "D", getD ()).append ("\n}").toString ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.matrices.decomposition.CommonLUD.DecompositionPrimitives#store(net.myorb.data.abstractions.SimpleStreamIO.TextSink)
		 */
		public void store (TextSink to) { storeDecomposition (toString (), to); }

		/* (non-Javadoc)
		 * @see net.myorb.math.matrices.decomposition.CommonLUD.DecompositionPrimitives#load(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
		 */
		public void load (TextSource from) { parseDecomposedMatrix (from); loadFromJSON (); }

		/**
		 * @param source a parsed JSON value
		 */
		public void load (JsonSemantics.JsonValue source)
		{
			identifySource (source); loadFromJSON ();
		}

		/**
		 * C matrix is read and S is extracted
		 */
		public void loadFromJSON ()
		{
			this.C = getMatrix ("C");
			this.N = C.getEdgeCount ();
			this.setS ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathBIF.FieldAccess#getFieldNamed(java.lang.String)
		 */
		public GenericValue getFieldNamed (String name)
		{
			Matrix <T> m;
			switch (name.charAt (0))
			{
				case 'C': m = getC (); break; case 'S': m = getS (); break;
				case 'D': m = getD (); break; case 'L': m = getL (); break;
				default: throw new RuntimeException ("Field not recognized: " + name);
			}
			return vm.newMatrix (m);
		}

	}


	/*
	 * decompose
	 */

	/**
	 * the primitive solution interface entry point for decompose
	 * @param A the matrix to be decomposed using Cholesky algorithms
	 * @return the resulting Decomposition object
	 */
	public CholeskyDecomposition decompose (Matrix <T> A)
	{
		CholeskyDecomposition D;
		decompose (D = new CholeskyDecomposition (A), A);
		return D;
	}

	/**
	 * perform the Cholesky on a new matrix
	 * - A must be a symmetric and positive definite matrix
	 * @param D the Decomposition to be processed
	 * @param A the source matrix
	 */
	public void decompose (CholeskyDecomposition D, Matrix <T> A)
	{
		/*
			Constructs and returns a
			new Cholesky decomposition object
			for a symmetric and positive definite matrix; 
			The decomposed matrices can be retrieved via
			instance methods of the returned 
			decomposition object.
		 */

		for (int i = 1; i <= D.N; i++)
		{
		    for (int j = 1; j <= i; j++)
		    {

		    	T dp = j == 1 ?
		    		mgr.getZero () : mgr.negate (rowDotProduct (D.C, i, j));
		    	T Aij = A.get (i, j), dif = mgr.add (Aij, dp);

		    	Aij = i == j ? SQRT (dif) :
		    		mgr.multiply (dif, mgr.pow (D.C.get (j, j), -1));
		    	D.C.set (i, j, Aij);

		    }
		}
		D.setS ();
	}
	T rowDotProduct (Matrix <T> C, int i, int j)
	{
		return dot (C.getRowAccess (i), C.getRowAccess (j), 1, j);
	}
	T SQRT (T x)
	{				//TODO: change to library of T power functions
		return mgr.convertFromDouble (Math.sqrt (mgr.convertToDouble (x)));
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
			CholeskyDecomposition D, SolutionPrimitives.RequestedResultVector b
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
			CholeskyDecomposition D, SolutionPrimitives.Content <T> b
		)
	{
		return D.solve (b);
	}

	/**
	 * full matrix of solutions
	 * @param D the decomposed translation matrix description
	 * @param B requested results in columns
	 * @return solutions in columns
	 */
	public Matrix <T> solve
		(
			CholeskyDecomposition D, 
			Matrix <T> B
		)
	{
		return D.solve (B);
	}

	/**
	 * apply update to solution vector
	 * @param D the modified Cholesky Decomposition
	 * @param x the vector to modify
	 */
	public void update (CholeskyDecomposition D, Vector <T> x)
	{
	    int n = x.size ();

	    for (int k = 0; k < n; k++)
	    {
	    	T Ckk = D.C.get(k, k), invCkk = mgr.pow (D.C.get(k, k), -1);;
	    	T r = SQRT (mgr.add (mgr.pow (Ckk, 2), mgr.pow (x.get (k), 2)));
	    	T c = mgr.multiply (r, invCkk), s = mgr.multiply (x.get (k), invCkk);
	    	T cinv = mgr.pow (c, -1);

	    	if (k < n)
	    	{
	    	    for (int kk = k+1; kk < n; kk++)
	    	    {
	    	    	T Xkk = x.get (kk);
	    	    	T Ckkk = D.C.get (kk, k);

	    	    	D.C.set
	    	    	(
	    	    		kk, k,
	    	    		mgr.multiply
	    	    		(
	    	    			cinv,
	    	    			mgr.add
	    	    			(
	    	    				Ckkk, mgr.multiply (s, Xkk)
	    	    			)
	    	    		)
	    	    	);

	    	    	x.set
	    	    	(
	    	    		kk,
	    	    		mgr.add
	    	    		(
	    	    			mgr.multiply (c, Xkk),
	    	    			mgr.negate (mgr.multiply (s, Ckkk))
	    	    		)
	    	    	);
	    	    }
	    	}
	    }
	}


	/*
	 * Decomposition transport
	 */

	/**
	 * restore a text stored CholeskyDecomposition
	 * @param source the location of the stored copy
	 * @return the CholeskyDecomposition
	 */
	public CholeskyDecomposition restore (TextSource source)
	{
		return new CholeskyDecomposition (source);
	}

	/**
	 * restore a JSON stored CholeskyDecomposition
	 * @param source the location of the stored copy
	 * @return the CholeskyDecomposition
	 */
	public CholeskyDecomposition restore (JsonSemantics.JsonValue source)
	{
		return new CholeskyDecomposition (source);		
	}


}

