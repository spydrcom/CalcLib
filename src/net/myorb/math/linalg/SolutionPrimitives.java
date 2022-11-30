
package net.myorb.math.linalg;

import net.myorb.math.matrices.*;
import net.myorb.math.SpaceManager;

import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.abstractions.SimpleStreamIO;

/**
 * abstract view of working with sets of linear equations
 * @param <T> data type for operations
 * @author Michael Druckman
 */
public interface SolutionPrimitives <T>
{

	/*
	 * attempting to solve Ax = b
	 */

	/**
	 * a digestion of the mapping data
	 */
	public interface Decomposition
	{

		/**
		 * @param to a sink object where copy will save
		 */
		void store (SimpleStreamIO.TextSink to);

		/**
		 * @param from a source object where copy can be found
		 */
		void load (SimpleStreamIO.TextSource from);

	}

	/**
	 * @param from a source object where copy can be found
	 * @return a Decomposition restored from source
	 */
	public Decomposition restore (SimpleStreamIO.TextSource from);

	/**
	 * @param source a source object with JSON representation
	 * @return a Decomposition restored from source
	 */
	public Decomposition restore (JsonSemantics.JsonValue source);

	/**
	 * a description of the sought result
	 */
	public interface RequestedResultVector {}

	/**
	 * a solution description for a requested result
	 */
	public interface SolutionVector {}

	/**
	 * vector type to use with 
	 * @param <T> data type SolutionPrimitives
	 */
	public static class Content <T> extends Vector <T>
		implements RequestedResultVector, SolutionVector
	{
		public Content (int N, SpaceManager <T> manager) { super (N, manager); }

		public Content (int [] cells, SpaceManager <T> manager)
		{
			super (cells.length-1, manager);
			for (int i = 1; i < cells.length; i++)
			{ set (i, manager.newScalar (cells [i])); }
		}

		public Content (Vector <T> source)
		{
			super (source.size (), source.getSpaceManager ());
			for (int i = 1; i <= source.size (); i++)
			{ this.set (i, source.get (i)); }
		}

		public Content (VectorAccess <T> source, SpaceManager <T> manager)
		{
			super (source.size (), manager);
			for (int i = 1; i <= source.size (); i++)
			{ this.set (i, source.get (i)); }
		}
	}

	/**
	 * digest a translation matrix
	 * @param A the mapping data
	 * @return the digested form
	 */
	public Decomposition decompose (Matrix <T> A);

	/**
	 * @param D the digested map to be used
	 * @param b the result vector being sought
	 * @return the solution giving the result
	 */
	public SolutionVector solve (Decomposition D, RequestedResultVector b);

	/**
	 * for solutions that provide means to pseudo-inverse
	 * @param <T> data type
	 */
	public interface Invertable <T>
	{
		/**
		 * @param source matrix to evaluate
		 * @return computed pseudo-inverse
		 */
		Matrix <T> inv (Matrix <T> source);
	}

	/**
	 * for solutions that provide means to determinant evaluation
	 * @param <T> data type
	 */
	public interface Determinable <T>
	{
		/**
		 * @param source matrix to evaluate
		 * @return computed determinant
		 */
		T det (Matrix <T> source);
	}

	/**
	 * for solutions that provide means to parallel evaluations
	 * @param <T> data type
	 */
	public interface MatrixSolution <T>
	{
		/**
		 * @param D the digested map to be used
		 * @param source matrix holding column vectors to solve
		 * @return computed solution set
		 */
		Matrix <T> solve (Decomposition D, Matrix <T> source);
	}

}
