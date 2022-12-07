
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.*;
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
 * Single Value decomposition implemented by Colt library
 * @author Michael Druckman
 */
public class ColtSVD  extends DecompositionSupport
	implements SolutionPrimitives <Double>, SolutionPrimitives.Invertable <Double>
{


	public ColtSVD ()
	{
		this (manager);
	}
	
	public ColtSVD (ExpressionSpaceManager <Double> mgr)
	{
		this.support = new GenericSupport <Double> (mgr);
		this.support.setSolutionClassPath (this.getClass ().getCanonicalName ());
		this.matOps = new MatrixOperations <Double> (mgr);
	}
	protected MatrixOperations <Double> matOps;
	protected GenericSupport <Double> support;


	/**
	 * representation of matrix Decomposition using this LUD algorithm set
	 */
	public class SVDecomposition implements SolutionPrimitives.Decomposition, ClMathBIF.FieldAccess
	{
	
		public SVDecomposition (SimpleStreamIO.TextSource source) { load (source); }
		public SVDecomposition (JsonSemantics.JsonValue source) { load (source); }
	
		public SVDecomposition (int rows, int cols)
		{
			this.cols = cols; this.rows = rows;
			this.V = MAT.encloseZeroBased (Linalg.getSvdV ());
			this.U = MAT.encloseZeroBased (Linalg.getSvdU ());
			this.S = MAT.encloseZeroBased (Linalg.getSvdS ());
			this.vm = new ValueManager <Double> ();
			this.norm2 = Linalg.getSvdNorm2 ();
			this.norm = Linalg.getSvdNorm ();
			this.rank = Linalg.getSvdRank ();
		}
		protected MAT S, V, U; protected int rows, cols;
		protected double norm, norm2; protected int rank;
		protected ValueManager <Double> vm;
	
		public SolutionPrimitives.Content <Double> solve (SolutionPrimitives.Content <Double> b)
		{
			throw new RuntimeException ("unimplemented solution mechanism");
		}

		/**
		 * @return pseudo inverse of S including transpose
		 */
		public Matrix <Double> pseudoSigma ()
		{
			return matOps.transpose (matOps.pseudoInv (S));
		}

		/**
		 * @return pseudo inverse computed from S, V, and U
		 */
		public Matrix <Double> pseudoInv ()
		{
			return matOps.product
			(		// M_p = V * S_p * U_t
				matOps.product (V, pseudoSigma ()),
				matOps.transpose (U)
			);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString () { return toJson ().toString (); }

		/**
		 * @return JSON representation of QRDecomposition
		 */
		public JsonValue toJson ()
		{
			JsonSemantics.JsonObject representation = new JsonSemantics.JsonObject ();
			support.addTo (representation, "Norm", norm); support.addTo (representation, "2Norm", norm); support.addTo (representation, "Rank", rank);
			support.addTo (representation, "V", V); support.addTo (representation, "U", U); support.addTo (representation, "S", S);
			representation.addMemberNamed ("Solution", new JsonSemantics.JsonString (support.solutionClassPath));
			return representation;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#store(net.myorb.data.abstractions.SimpleStreamIO.TextSink)
		 */
		public void store (SimpleStreamIO.TextSink to) { support.storeDecomposition (toJson (), to); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathBIF.FieldAccess#getFieldNamed(java.lang.String)
		 */
		public GenericValue getFieldNamed (String name)
		{
			switch (name.charAt (0))
			{
				case 'V': return vm.newMatrix (V); case 'U': return vm.newMatrix (U);
				case 'S': return vm.newMatrix (S); case 'I': return vm.newMatrix (pseudoInv ());
				case 'R': return vm.newDiscreteValue (manager.convertFromDouble ((double) rank));	// RANK
				case 'N': return vm.newDiscreteValue (manager.convertFromDouble (norm2));			// 2NORM
				case 'C': return vm.newDiscreteValue (manager.convertFromDouble (norm));			// COND
				default: throw new RuntimeException ("Field not recognized: " + name);
			}
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
			this.V = MAT.enclose (support.getMatrix ("V"));
			this.U = MAT.enclose (support.getMatrix ("U"));
			this.S = MAT.enclose (support.getMatrix ("S"));
			this.rank = support.getValue ("Rank").intValue ();
			this.norm = support.getValue ("Norm").doubleValue ();
			this.norm2 = support.getValue ("2Norm").doubleValue ();
			this.cols = V.columnCount ();
			this.rows = V.rowCount ();
		}
	
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#decompose(net.myorb.math.matrices.Matrix)
	 */
	public Decomposition decompose (Matrix <Double> A)
	{
		Linalg.prep (A.toRawCells ());
		return new SVDecomposition (A.rowCount (), A.columnCount ());
	}
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#solve(net.myorb.math.linalg.SolutionPrimitives.Decomposition, net.myorb.math.linalg.SolutionPrimitives.RequestedResultVector)
	 */
	public SolutionVector solve (Decomposition D, RequestedResultVector b)
	{
		return solveUsingColt ( (SVDecomposition) D, b);
	}
	public SolutionVector solveUsingColt (SVDecomposition D, RequestedResultVector b)
	{
		@SuppressWarnings("unchecked") SolutionPrimitives.Content <Double> B =
				( SolutionPrimitives.Content <Double> ) b;
		return (SolutionVector) D.solve (B);
	}


	/* (non-Javadoc)
	* @see net.myorb.math.linalg.SolutionPrimitives.Invertable#inv(net.myorb.math.matrices.Matrix)
	*/
	public Matrix <Double> inv (Matrix <Double> A)
	{
		Linalg.prep (A.toRawCells ());
		SVDecomposition D = new SVDecomposition (A.rowCount (), A.columnCount ());
		return D.pseudoInv ();
	}


	/*
	 * Decomposition transport
	 */
	
	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#restore(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
	 */
	public SVDecomposition restore (SimpleStreamIO.TextSource source)
	{
		return new SVDecomposition (source);
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#restore(net.myorb.data.notations.json.JsonLowLevel.JsonValue)
	 */
	public SVDecomposition restore (JsonSemantics.JsonValue source)
	{
		return new SVDecomposition (source);		
	}


}

