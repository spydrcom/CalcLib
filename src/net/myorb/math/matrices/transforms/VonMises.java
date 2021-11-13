
package net.myorb.math.matrices.transforms;

import net.myorb.math.*;
import net.myorb.math.matrices.*;

/**
 * the power iteration is an eigenvalue algorithm: given a matrix A, 
 * the algorithm will produce a number lambda (the eigenvalue) and a nonzero vector v (the eigenvector), 
 * such that Av = (lambda)v. The algorithm is also known as the Von Mises iteration
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class VonMises<T> extends Tolerances<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public VonMises
	(SpaceManager<T> manager)
	{
		super (manager);
		setLibrary (new PowerPrimitives<T> (manager));
		vecs = new VectorOperations<T> (manager);
		vecs.setToleranceDefaults (this.lib);
		setToleranceDefaults (this.lib);
		vecs.setLibrary (this.lib);
	}
	protected VectorOperations<T> vecs;


	/* (non-Javadoc)
	 * @see net.myorb.math.Tolerances#setToleranceScale(int)
	 */
	public void setToleranceScale (int scale)
	{
		vecs.setToleranceScale (scale);
	}


	/**
	 * set maximum allowed iteration count
	 * @param maxIterations the most iterations allowed in one call
	 */
	public void setIterationMaximum (int maxIterations)
	{
		this.maxIterations = maxIterations;
	}


	/**
	 * compute both eigenvalue and eigenvector for an iteration
	 * @param a the problem set described in a matrix of elements
	 * @param b an appoximation of the eigenvector being sought
	 * @param updated a vector object to be updated
	 * @return the computed eigenvalue
	 */
	public T computePowerIteration (Matrix<T> a, Vector<T> b, Vector<T> updated)
	{
		Vector<T> v = new Vector<T> (b.size (), manager);
		for (int r = 1; r <= a.rowCount(); r++)
		{
			v.set (r, vecs.conjDotProduct (a.getRowAccess (r), b));				// conj (a[r,...]) DOT  b
		}
		T magnitude = lib.sqrt (vecs.conjDotProduct (v, v));					// conj (v) DOT  v
		v.scale (inverted (magnitude), updated);
		return magnitude;
	}


	/**
	 * run one power iteration
	 * @param a the problem set described in a matrix of elements
	 * @param b an appoximation of the eigenvector being sought
	 * @return the improved appoximation of the eigenvector
	 */
	public Vector<T> computePowerIteration (Matrix<T> a, Vector<T> b)
	{
		Vector<T> v = new Vector<T> (manager);
		computePowerIteration (a, b, v);
		return v;
	}


	/**
	 * run multiple iterations looking for convergence
	 * @param a the problem set described in a matrix of elements
	 * @param b an appoximation of the eigenvector being sought
	 * @return the improved appoximation of the eigenvector
	 */
	public Vector<T> executePowerIterations (Matrix<T> a, Vector<T> b)
	{
		Vector<T> bN = b, bNp1 = null;
		for (int i = maxIterations; i > 0; i--)
		{
			bNp1 = computePowerIteration (a, bN);
			if (vecs.isWithinTolerance (bN, bNp1)) break;
			//vecs.show (bNp1);
			bN = bNp1;
		}
		return bNp1;
	}


}

