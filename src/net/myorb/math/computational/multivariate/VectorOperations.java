
package net.myorb.math.computational.multivariate;

import net.myorb.math.expressions.evaluationstates.Environment;
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


	public static final // indices used for 3D curl
	/*
	 *	 	   matrix of partial derivatives
	 * 		   -----------------------------
	 * 
	 *					 dx		 dy		 dz
	 *					===		===		===
	 *			Fx		1,1		1,2		1,3
	 *			Fy		2,1		2,2		2,3
	 *			Fz		3,1		3,2		3,3
	 */
	int Fx = 1, dx = 1, Fy = 2, dy = 2, Fz = 3, dz = 3;


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
		T divergence = manager.getZero ();

		for (int row = 1; row <= M.rowCount (); row++)
		{
			for (int col = 1; col <= M.columnCount (); col++)
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
	ValueList computeCurl (Matrix <T> M)
	{
		ValueList vector = zeroVector ();

		set ( 2, Fy, dx, M, vector );	// dFy/dx - dFx/dy
		// no contribution from 3rd dimension given 2x2 matrix

		if ( M.getEdgeCount () > 2 )
		{
			// 3rd dimension is present
			set ( 1, Fx, dz, M, vector );	// dFx/dz - dFz/dx
			set ( 0, Fz, dy, M, vector );	// dFz/dy - dFy/dz
		}

		return vector;
	}


	/**
	 * set one component of the curl vector
	 * @param into index of the orthogonal basis being set
	 * @param F index of the component of the function being processed
	 * @param d index of the orthogonal basis being referenced
	 * @param M the matrix of partial derivatives computed
	 * @param vector the computed curl vector
	 */
	void set (int into, int F, int d, Matrix <T> M, ValueList vector)
	{
		vector.set ( into, manager.add ( M.get (F, d), manager.negate ( M.get (d, F) ) ) );		
	}


	/**
	 * @return initialized vector of zero entries
	 */
	ValueList zeroVector ()
	{
		T Z = manager.getZero ();
		ValueList vector = new ValueList ();
		vector.add (Z); vector.add (Z); vector.add (Z);
		return vector;
	}


}

