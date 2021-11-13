
package net.myorb.math.matrices;

import net.myorb.math.*;

/**
 * linear equation solutions for triangular matrices
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class Triangular<T>
{


	public Triangular (SpaceManager<T> mgr)
	{
		this.mgr = mgr;
	}
	SpaceManager<T> mgr;


	/**
	 * permute solution vector to proper order
	 * @param v the solution vector subject to pivot
	 * @param p the pivot vector coming from LU decomposition process
	 * @return the appropriate permutation of solution vector
	 */
	public Vector<T> pivot (Vector<T> v, Vector<T> p)
	{
		Vector<T> reordered = new Vector<T>(v.size (), mgr);
		for (int i = 1; i <= v.size(); i++)
		{
			int n = mgr.toNumber (p.get (i)).intValue ();
			reordered.set (i, v.get (n + 1));
		}
		return reordered;
	}


	/**
	 * solve LUx = b
	 * @param L lower triangular matrix
	 * @param U upper triangular matrix
	 * @param b vector of values forming right side of equation
	 * @return vector of x values of solution
	 */
	public Vector<T> luXb (Matrix<T> L, Matrix<T> U, Vector<T> b)
	{
		int n = b.size (), k;
		// Ax = b -> LUx = b, SO y is defined to be Ux
		Vector<T> x = new Vector<T>(n, mgr), y = new Vector<T>(n, mgr);

		// Forward solve Ly = b
		for (int i = 0; i < n; i++)
		{
			k = copy (i, b, y);
			for (int j = 0; j < i; j++)
				setResultElement (y, L, k, j + 1);
			setResultElement (y, L, k);
		}

		// Backward solve Ux = y
		for (int i = n - 1; i >= 0; i--)
		{
			k = copy (i, y, x);
			for (int j = k; j < n; j++)
				setResultElement (x, U, k, j + 1);
			setResultElement (x, U, k);
		}

		return x;
	}


	/**
	 * set an element of the result
	 * @param v the vector of results being collected
	 * @param t the triangular matrix being used for solution
	 * @param i the row number of the element
	 * @param j the column number
	 */
	public void setResultElement (Vector<T> v, Matrix<T> t, int i, int j)
	{
		T tx = mgr.multiply (t.get (i, j), v.get (j));
		v.set (i, mgr.add (v.get (i), mgr.negate (tx)));
	}


	/**
	 * allow for additional pivot step
	 * @param L lower triangular matrix
	 * @param U upper triangular matrix
	 * @param b vector of values forming right side of equation
	 * @param p an additional pivot vector for solution permutation
	 * @return vector of x values of solution
	 */
	public Vector<T> luXb
	(Matrix<T> L, Matrix<T> U, Vector<T> b, Vector<T> p)
	{
		if (p != null) b = pivot (b, p);
		return luXb (L, U, b);
	}


	/**
	 * copy element from solution vector to result vector
	 * @param i the 0 based index of the element to copy
	 * @param from the solution vector to copy from
	 * @param to the destination result vector
	 * @return the 1 based index
	 */
	public int copy (int i, Vector<T> from, Vector<T> to)
	{ int k = i + 1; to.set (k, from.get (k)); return k; }


	/**
	 * long form using lXb and uXb
	 * @param L lower triangular matrix
	 * @param U upper triangular matrix
	 * @param b vector of values forming right side of equation
	 * @return vector of X values of solution
	 */
	public Vector<T> luCompounded (Matrix<T> L, Matrix<T> U, Vector<T> b)
	{
		Vector<T> y = lXb (L, b);
		return uXb (U, y);
	}


	/**
	 * solve uX = b
	 * @param u upper triangular matrix
	 * @param b vector of values forming right side of equation
	 * @return vector of X values of solution
	 */
	public Vector<T> uXb (Matrix<T> u, Vector<T> b)
	{
		int s = b.size ();
		Vector<T> x = new Vector<T>(s, mgr);
		for (int i = s; i >= 1; i--)
		{
			T sum = mgr.getZero ();
			for (int j = s; j > i; j--) sum = add (sum, i, j, u, x);
			computeResult (sum, i, u, b, x);
		}
		return x;
	}


	/**
	 * solve lX = b
	 * @param l lower triangular matrix
	 * @param b vector of values forming right side of equation
	 * @return vector of X values of solution
	 */
	public Vector<T> lXb (Matrix<T> l, Vector<T> b)
	{
		int s = b.size ();
		Vector<T> x = new Vector<T>(s, mgr);
		for (int i = 1; i <= s; i++)
		{
			T sum = mgr.getZero ();
			for (int j = 1; j < i; j++) sum = add (sum, i, j, l, x);
			computeResult (sum, i, l, b, x);
		}
		return x;
	}


	/**
	 * build term of the summation
	 * @param accum the accumulation of terms
	 * @param i the row number of the matrix
	 * @param j the column number of the matrix
	 * @param t the matrix containing the triangle
	 * @param x the vector of X result values
	 * @return the num accumulation value
	 */
	public T add (T accum, int i, int j, Matrix<T> t, Vector<T> x)
	{ return mgr.add (accum, mgr.multiply (t.get (i, j), x.get (j))); }


	/**
	 * use previous row sums to compute next x
	 * @param sum the sum of previous x values with coefficients
	 * @param i the current row number
	 * @param t the triangular matrix
	 * @param b the solution vector
	 * @param v the result vector
	 */
	public void computeResult
	(T sum, int i, Matrix<T> t, Vector<T> b, Vector<T> v)
	{ setResultElement (v, mgr.add (b.get (i), mgr.negate (sum)), t, i); }


	/**
	 * divide diagonal element into result
	 * @param v the result vector being computed
	 * @param accumulated the sum of the previous terms
	 * @param t the triangular matrix used for the solution
	 * @param i the index of the element being set
	 */
	public void setResultElement (Vector<T> v, T accumulated, Matrix<T> t, int i)
	{ v.set (i, mgr.multiply (accumulated, mgr.invert (t.get (i, i)))); }


	/**
	 * alter iTH element of result vector
	 * @param v result vector to be modified
	 * @param t the triangular matrix used for solution
	 * @param i the number of the element being computed
	 */
	public void setResultElement (Vector<T> v, Matrix<T> t, int i)
	{ setResultElement (v, v.get (i), t, i); }


}

