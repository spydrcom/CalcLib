
package net.myorb.math.matrices.transforms;

import net.myorb.math.*;
import net.myorb.math.matrices.*;

/**
 * implementation of algorithms supporting the Householder reflection transform
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Householder<T> extends Arithmetic<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public Householder
	(SpaceManager<T> manager)
	{
		super (manager);
		setLibrary (new OptimizedMathLibrary<T> (manager));
		matrices = new SimultaneousEquations<T> (manager);
		vecs = new VectorOperations<T> (manager);
	}
	protected SimultaneousEquations<T> matrices;
	protected VectorOperations<T> vecs;


	/**
	 * compute the alpha
	 *  that will be used to create the transform matrix
	 * @param a the matrix being transformed this iteration
	 * @param k the column number manipulated
	 * @return the computed alpha value
	 */
	public Value<T> computeAlpha (Matrix<T> a, int k)
	{
		int n = a.rowCount ();
		Value<T> sum = forValue (0), ax;
		Value<T> aKp1k = forValue (a.get (k+1, k));
		for (int j = k+1; j <= n; j++) { ax = forValue (a.get (j, k)); sum = sum.plus (ax.squared ()); }
		Value<T> alpha = sgn (aKp1k).negate ().times (sqrt (sum));
		return alpha;
	}


	/**
	 * compute the rho
	 *  that will be used to create the transform matrix
	 * @param alpha the alpha value computed for this matrix
	 * @param a the matrix being transformed this iteration
	 * @param k the column number manipulated
	 * @return the computed rho value
	 */
	public Value<T> computeRho (Value<T> alpha, Matrix<T> a, int k)
	{
		Value<T> aKp1k = forValue (a.get (k+1, k));
		Value<T> rho = sqrt (alpha.squared ().minus (aKp1k.times (alpha)).over (forValue (2)));
		return rho;
	}


	/**
	 * compute the vector
	 *  that will be used to create the transform matrix
	 * @param alpha the alpha value computed for this matrix
	 * @param rho the rho value computed for this matrix this iteration
	 * @param a the matrix being transformed this iteration
	 * @param k the column number manipulated
	 * @return the computed vector
	 */
	public Vector<T> computeVector (Value<T> alpha, Value<T> rho, Matrix<T> a, int k)
	{
		int n = a.rowCount ();
		Value<T> twoRhoInv = rho.times (forValue (2)).inverted ();
		Value<T> aKp1k = forValue (a.get (k+1, k));
		Vector<T> v = new Vector<T> (n, manager);

		Value<T> vk = aKp1k.minus (alpha).times (twoRhoInv);
		v.set (k+1, vk.getUnderlying ());

		T twoRhoInvT = twoRhoInv.getUnderlying ();
		for (int j = k+2; j <= n; j++)
		{
			v.set (j, X (twoRhoInvT, a.get (j, k)));
		}
		return v;
	}


	/**
	 * compute Kth iteration of P
	 * @param a source matrix to be transformed in this iteration
	 * @param k column number for this iteration being manipulated
	 * @return the P matrix for this iteration
	 */
	public Matrix<T> computeHouseholderPmatrix (Matrix<T> a, int k)
	{
		int n = a.rowCount ();
		Value<T> alpha = computeAlpha (a, k);
		Value<T> rho = computeRho (alpha, a, k);
		Vector<T> v = computeVector (alpha, rho, a, k);
		Matrix<T> vsq2 = matrices.times (discrete (-2), vecs.dyadicProduct (v, v));
		Matrix<T> p = matrices.sum (matrices.identity (n), vsq2);
		return p;
	}


	/**
	 * compute a Householder PAP transform of specified matrix
	 * @param a source matrix to be transformed in this iteration
	 * @param k column number for this iteration being manipulated
	 * @return the transform result
	 */
	public Matrix<T> computeHouseholderPAP (Matrix<T> a, int k)
	{
		Matrix<T> p = computeHouseholderPmatrix (a, k);
		Matrix<T> pap = matrices.product (matrices.product (p, a), p);
		return pap;
	}


	/**
	 * transform all columns of specified matrix
	 * @param a the matrix being transformed
	 * @return the transformed matrix
	 */
	public Matrix<T> computeHouseholderPAPnth (Matrix<T> a)
	{
		Matrix<T> an = a;
		int n = a.rowCount ();
		for (int i = 1; i < n-1; i++)
		{ an = computeHouseholderPAP (an, i); }
		return an;
	}


}

