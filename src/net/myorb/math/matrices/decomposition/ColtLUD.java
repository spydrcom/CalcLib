
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.*;
import net.myorb.math.structures.loaders.DecomposedMatrix;
import net.myorb.math.linalg.SolutionPrimitives;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.algorithms.ClMathBIF;

import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
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
	protected GenericSupport <Double> support;
	protected Triangular <Double> tri;


	/**
	 * representation of matrix Decomposition using this LUD algorithm set
	 */
	public class LUDecomposition implements SolutionPrimitives.Decomposition, ClMathBIF.FieldAccess
	{

		public LUDecomposition (SimpleStreamIO.TextSource source) { load (source); }
		public LUDecomposition (JsonSemantics.JsonValue source) { load (source); }

		public LUDecomposition ()
		{
			this.detP = Linalg.getLudPdet ();
			this.L = MAT.encloseZeroBased (Linalg.getLudL ());
			this.U = MAT.encloseZeroBased (Linalg.getLudU ());
			this.P = Linalg.getLudIntP (); this.N = P.length;
			this.vm = new ValueManager <Double> ();
		}
		protected MAT L, U; protected double detP; protected int P [],  N; 
		protected ValueManager <Double> vm;

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString () { return toJson (null).toString (); }

		public Matrix <Double> solve (Matrix <Double> A)
		{ return MAT.encloseZeroBased (Linalg.getLudSolution (A.toRawCells ())); }
		public SolutionPrimitives.Content <Double> solve (SolutionPrimitives.Content <Double> b)
		{ return new SolutionPrimitives.Content <Double> (tri.luXb (L, U, b, VEC.enclose (P)), manager); }
		public double det () { return tri.det (U, L, detP); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.ValueManager.PortableValue#toJson(net.myorb.math.expressions.ExpressionSpaceManager)
		 */
		public JsonValue toJson (ExpressionSpaceManager <Double> manager)
		{
			JsonSemantics.JsonObject representation = new JsonSemantics.JsonObject ();
			support.addTo (representation, "L", L); support.addTo (representation, "U", U); support.addTo (representation, "P", P);
			representation.addMemberNamed ("Loader", new JsonSemantics.JsonString (DecomposedMatrix.class.getCanonicalName ()));
			representation.addMemberNamed ("Solution", new JsonSemantics.JsonString (support.solutionClassPath));
			return representation;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#store(net.myorb.data.abstractions.SimpleStreamIO.TextSink)
		 */
		public void store (SimpleStreamIO.TextSink to) { support.storeDecomposition (toJson (null), to); }

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

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathBIF.FieldAccess#getFieldNamed(java.lang.String)
		 */
		public GenericValue getFieldNamed (String name)
		{
			switch (name.charAt (0))
			{
				case 'D': return vm.newDiscreteValue (det ());
				case 'U': return vm.newMatrix (U); case 'L': return vm.newMatrix (L);
				case 'P': return vm.newDimensionedValue (VEC.enclose (P).getElementsList ());
				default: throw new RuntimeException ("Field not recognized: " + name);
			}
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

