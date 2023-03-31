
package net.myorb.math.linalg;

import net.myorb.math.matrices.Vector;
import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.SimultaneousEquations;
import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.Matrix;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.abstractions.SimpleStreamIO;

/**
 * an implementation of SolutionPrimitives using Gaussian Elimination
 * @author Michael Druckman
 */
public class GaussSolution <T> implements SolutionPrimitives <T>
{


	public GaussSolution (ExpressionSpaceManager <T> manager)
	{ this.ops = new MatrixOperations <T> (this.manager = manager); }
	protected ExpressionSpaceManager <T> manager;
	protected MatrixOperations <T> ops;

	public GaussSolution (Environment <T> environment)
	{ this (environment.getSpaceManager ()); }


	// implementation of SolutionPrimitives

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#decompose(net.myorb.math.matrices.Matrix)
	 */
	public SolutionPrimitives.Decomposition decompose (Matrix <T> A)
	{
		return new AugmentedMatrix <T> (A, ops, manager);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#solve(net.myorb.math.linalg.SolutionPrimitives.Decomposition, net.myorb.math.linalg.SolutionPrimitives.RequestedResultVector)
	 */
	public SolutionPrimitives.SolutionVector solve
		(
			Decomposition D, RequestedResultVector b
		)
	{
		@SuppressWarnings("unchecked") VectorAccess <T> S =
			( ( AugmentedMatrix <T> ) D ).solve ( ( Vector <T> ) b );
		return new SolutionPrimitives.Content <T> ( S, manager );
	}


	// Decomposition IO implementation

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#restore(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
	 */
	public SolutionPrimitives.Decomposition restore (SimpleStreamIO.TextSource from)
	{
		SolutionPrimitives.Decomposition D;
		( D = new AugmentedMatrix <T> (ops) ).load (from);
		return D;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives#restore(net.myorb.data.notations.json.JsonLowLevel.JsonValue)
	 */
	public SolutionPrimitives.Decomposition restore (JsonValue source)
	{ return new AugmentedMatrix <T> (source); }


}


/**
 * a wrapper for a matrix to use as a solution primitive
 */
class AugmentedMatrix <T> implements SolutionPrimitives.Decomposition
{

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#store(net.myorb.data.abstractions.SimpleStreamIO.TextSink)
	 */
	public void store (SimpleStreamIO.TextSink to) { ops.saveTo (this.M, to); }

	/* (non-Javadoc)
	 * @see net.myorb.math.linalg.SolutionPrimitives.Decomposition#load(net.myorb.data.abstractions.SimpleStreamIO.TextSource)
	 */
	public void load (SimpleStreamIO.TextSource from) { this.M = ops.readFrom (from); }

	/**
	 * solve system using Simultaneous Equations implementation of Gaussian Elimination
	 * @param column the requested solutions vector
	 * @return the solution to the system
	 */
	VectorAccess <T> solve (VectorAccess <T> column) { return simeq.applyGaussianElimination (M, column); }

	/**
	 * @param ops MatrixOperations support object
	 */
	AugmentedMatrix (MatrixOperations <T> ops) { this.ops = ops; }
	AugmentedMatrix (JsonValue source)  { this.M = ops.fromJson (source); }
	AugmentedMatrix (Matrix <T> M, MatrixOperations <T> ops, ExpressionSpaceManager <T> manager)
	{ this (ops); this.M = M; simeq = new SimultaneousEquations <T> (manager); }
	protected SimultaneousEquations <T> simeq;
	protected MatrixOperations <T> ops;
	protected Matrix <T> M;

}

