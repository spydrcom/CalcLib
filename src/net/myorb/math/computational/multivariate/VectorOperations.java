
package net.myorb.math.computational.multivariate;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.ValueManager.RawValueList;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.matrices.Matrix;

/**
 * implementations of algorithms specific to Multivariate function derivatives
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class VectorOperations <T> extends Gradients <T>
{


	public VectorOperations (Environment <T> environment)
	{
		super (environment);
	}


	/**
	 * compute gradient for function at point
	 * @param context the meta-data holding the context
	 * @return the matrix of partial derivatives
	 */
	public ValueManager.GenericValue grad (OperationContext context)
	{
		return valueManager.newMatrix ( partialDerivativeComputations (context) );
	}


	/**
	 * compute divergence for function at point
	 * @param context the meta-data holding the context
	 * @return the scalar value computed as divergence
	 */
	public ValueManager.GenericValue div (OperationContext context)
	{
		T divergence =
			computeDivergence (partialDerivativeComputations (context));
		return valueManager.newDiscreteValue (divergence);
	}


	/**
	 * sum partial derivatives as evaluation of divergence
	 * @param M the matrix of partial derivatives computed
	 * @return the computed divergence
	 */
	T computeDivergence (Matrix <T> M)
	{
		int N = M.getEdgeCount ();
		T divergence = manager.getZero ();

		for (int row = 1; row <= N; row++)
		{
			for (int col = 1; col <= N; col++)
			{
				divergence = 
					manager.add
					(
						divergence, M.get (row, col)
					);
			}
		}

		return divergence;
	}


	/**
	 * compute curl for function at point
	 * @param context the meta-data holding the context
	 * @return the scalar value computed as divergence
	 */
	public ValueManager.GenericValue curl (OperationContext context)
	{
		int N;
		Matrix <T> M = partialDerivativeComputations (context);
		if ( (N = M.getEdgeCount ()) < 2 ) throw new RuntimeException ("Too few dimensions for curl");
		if ( N > 3 ) throw new RuntimeException ("Too many dimensions for curl");
		return valueManager.newDimensionedValue (computeCurl (M));
	}

	/**
	 * apply equations to compute curl
	 * @param M the matrix of partial derivatives computed
	 * @return the computed curl vector
	 */
	RawValueList<T> computeCurl (Matrix <T> M)
	{
		RawValueList <T> vector;
		set ( 2, 2, 1, M, vector = zeroVector () );
		// no contribution from 3rd dimension given 2x2 matrix

		if ( M.getEdgeCount () > 2 )
		{
			// 3rd dimension is present
			set ( 1, 1, 3, M, vector );
			set ( 0, 3, 2, M, vector );
		}

		return vector;
	}


	/**
	 * set one component of the curl vector
	 * @param into index of the orthogonal basis being set
	 * @param V index of the component of the function being processed
	 * @param d index of the orthogonal basis being referenced
	 * @param M the matrix of partial derivatives computed
	 * @param vector the computed curl vector
	 */
	void set (int into, int V, int d, Matrix <T> M, RawValueList <T> vector)
	{
		vector.set ( into, manager.add ( M.get (V, d), manager.negate ( M.get (d, V) ) ) );		
	}


	/**
	 * @return initialized vector of zero entries
	 */
	RawValueList <T> zeroVector ()
	{
		T Z = manager.getZero ();
		RawValueList <T> vector = new RawValueList <T> ();
		vector.add (Z); vector.add (Z); vector.add (Z);
		return vector;
	}


}

