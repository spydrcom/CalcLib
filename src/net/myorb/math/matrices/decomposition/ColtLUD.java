
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.*;
import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.abstractions.SimpleStreamIO;

import cern.colt.library.Linalg;

/**
 * Dolittle LU decomposition implemented by Colt library
 * @author Michael Druckman
 */
public class ColtLUD extends DecompositionSupport
	implements SolutionPrimitives <Double>
{


	public ColtLUD ()
	{
		this (manager);
	}

	public ColtLUD (ExpressionSpaceManager <Double> mgr)
	{
		this.support = new GenericSupport <Double> (mgr);
		this.support.setSolutionClassPath (this.getClass ().getCanonicalName ());
		this.tri = new Triangular <Double> (mgr);
	}
	GenericSupport <Double> support;
	Triangular <Double> tri;


	/**
	 * representation of matrix Decomposition using this LUD algorithm set
	 */
	public class LUDecomposition implements SolutionPrimitives.Decomposition
	{

		public LUDecomposition (SimpleStreamIO.TextSource source) { load (source); }
		public LUDecomposition (JsonSemantics.JsonValue source) { load (source); }

		public LUDecomposition ()
		{
			this.detP = Linalg.getLudPdet ();
			this.L = MAT.encloseZeroBased (Linalg.getLudL ());
			this.U = MAT.encloseZeroBased (Linalg.getLudU ());
			this.P = Linalg.getLudIntP (); this.N = P.length;
		}
		MAT L, U; double detP; int P [],  N; 

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			StringBuffer JSON = new StringBuffer ().append ("{");
			support.addPathTo (JSON); support.addTo (JSON, "L", L).append (",");
			return support.addTo (JSON, "U", U).append (",\n  \"P\" : ")
				.append (GenericSupport.toList (P, 0))
				.append ("\n}")
			.toString ();
		}

		public Matrix <Double> solve (Matrix <Double> A)
		{ return MAT.encloseZeroBased (Linalg.getLudSolution (A.toRawCells ())); }
		public SolutionPrimitives.Content <Double> solve (SolutionPrimitives.Content <Double> b)
		{ return new SolutionPrimitives.Content <Double> (tri.luXb (L, U, b, VEC.enclose (P)), manager); }
		public double det () { return tri.det (U, L, detP); }
		
		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#store(net.myorb.data.abstractions.SimpleStreamIO.TextSink)
		 */
		public void store (SimpleStreamIO.TextSink to)
		{
			support.storeDecomposition (toString (), to);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#load(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
		 */
		public void load (SimpleStreamIO.TextSource from)
		{
			support.parseDecomposedMatrix (from); loadFromJSON ();
		}
		public void load (JsonSemantics.JsonValue source)
		{
			support.identifySource (source); loadFromJSON ();
		}
		public void loadFromJSON ()
		{
			this.L = MAT.enclose (support.getMatrix ("L"));
			this.U = MAT.enclose (support.getMatrix ("U"));
			this.P = getPivot ();
			this.N = P.length;
		}

		/**
		 * @return pivot vector read from JSON
		 */
		public int [] getPivot ()
		{
			int i = 0;
			Number [] numbers =
					support.getIndex ("P");
			int [] pivot = new int [numbers.length];
			for (Number n : numbers) pivot [i++] = n.intValue ();
			return pivot;
		}

		/**
		 * @param to vector to receive copy
		 */
		public void copyPivot (Vector <Double> to)
		{
			for (int i=1; i<=P.length; i++) to.set (i, (double) P[i-1]);
		}

	}


	/*
	 * decompose
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#decompose(net.myorb.math.matrices.Matrix)
	 */
	public Decomposition decompose (Matrix <Double> A)
	{
		Linalg.prep (A.toRawCells ());
		return new LUDecomposition ();
	}


	/*
	 * solution
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#solve(net.myorb.math.linalg.SolutionPrimitives.Decomposition, net.myorb.math.linalg.SolutionPrimitives.RequestedResultVector)
	 */
	public SolutionVector solve (Decomposition D, RequestedResultVector b)
	{
		return solveUsingColt ( (LUDecomposition) D, b);
	}
	public SolutionVector solveUsingColt (LUDecomposition D, RequestedResultVector b)
	{
		@SuppressWarnings("unchecked") SolutionPrimitives.Content <Double> B =
				( SolutionPrimitives.Content <Double> ) b;
		return (SolutionVector) D.solve (B);
	}
	public Matrix <Double> solveUsingColt
	(LUDecomposition D, Matrix <Double> b)
	{ return D.solve (b); }


	/*
	 * determinant
	 */

	public Double det (Matrix <Double> A)
	{
		Linalg.prep (A.toRawCells ());
		return new LUDecomposition ().det ();
	}


	/*
	 * Decomposition transport
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#restore(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
	 */
	public LUDecomposition restore (SimpleStreamIO.TextSource source)
	{
		return new LUDecomposition (source);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#restore(net.myorb.data.notations.json.JsonLowLevel.JsonValue)
	 */
	public LUDecomposition restore (JsonSemantics.JsonValue source)
	{
		return new LUDecomposition (source);		
	}


	/*
	 * functionality provided CalcTools
	 */

	/**
	 * LUD function in ColtTools
	 * @param A source matrix being decomposed
	 * @param L the lower triangular
	 * @param U the upper triangular
	 * @param P the pivot vector
	 */
	@SuppressWarnings("unchecked") public void decompose
		(
			Matrix <?> A,
			Matrix <?> L, Matrix <?> U,
			Vector <?> P
		)
	{
		LUDecomposition D =
			(LUDecomposition) decompose ((Matrix <Double>) A);
		CellSequencePrimitives.copyRows (D.N, D.L, (Matrix <Double>) L);
		CellSequencePrimitives.copyRows (D.N, D.U, (Matrix <Double>) U);
		D.copyPivot ((Vector <Double>) P);
	}


}

