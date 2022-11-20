
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.Triangular;
import net.myorb.math.matrices.Matrix;
import net.myorb.math.SpaceManager;

/**
 * Doolittle LU Decomposition algorithm implementation
 * @param <T> data type used in Matrix objects
 * @author Michael Druckman
 */
public class Doolittle<T>
{

	/**
	 * @param mgr data type manager
	 */
	public Doolittle (SpaceManager<T> mgr)
	{
		ZERO = mgr.getZero ();
		ONE = mgr.getOne ();
		this.mgr = mgr;
	}
	SpaceManager<T> mgr;
	T ZERO, ONE;

	/**
	 * @param A the matrix to be decomposed
	 * @param U the upper triangular matrix of the decomposition
	 * @param L the lower triangular matrix of the decomposition
	 */
	public void decompose (Matrix<T> A, Matrix<T> U, Matrix<T> L)
	{
		int n = A.columnCount ();			// matrix must be square
		for (int j = 1; j <= n; j++)		// start with L set to ZERO
			for (int i = 1; i <= n; i++)
				L.set (i, j, ZERO);
	    for (int j = 1; j <= n; j++)
	    {
	        for (int i = 1; i <= n; i++)
	        {
	            if (i <= j)
	            {
	            	U.set (i, j, A.get (i, j));
	                for (int k = 1; k <= i - 1; k++)
	                {
	                	U.set
	                	(
	                		i, j,
	                		mgr.add
	                		(
	                			U.get (i, j),
	                			mgr.negate
	                			(
	                				mgr.multiply (L.get (i, k), U.get (k, j))
	                			)
	                		)
	                	);
	                }
	                L.set (i, j, i==j ? ONE : ZERO);	// diagonal is ONE, upper is ZERO
	            }
	            else
	            {
	                L.set (i, j, A.get (i, j));
	                for (int k = 1; k <= j - 1; k++)
	                {
	                	L.set
	                	(
	                		i, j, 
	                		mgr.add
	                		(
	                			L.get (i, j),
	                			mgr.negate
	                			(
	                				mgr.multiply (L.get (i, k), U.get (k, j))
	                			)
	                		)
	                	);
	                }
	                L.set (i, j, mgr.multiply (L.get (i, j), mgr.invert (U.get (j, j))));
	                U.set (i, j, ZERO);
	            }
	        }
	    }
	}


	/**
	 * decompose matrix to LU
	 * - and use triangles to compute determinant
	 * @param A the matrix to decompose
	 * @return determinant of A
	 */
	public T det (Matrix <T> A)
	{
		int n = A.getEdgeCount ();

		Matrix<T>
			U = new Matrix<T> (n, n, mgr),
			L = new Matrix<T> (n, n, mgr);
		decompose (A, U, L);

		// no permutation matrix in this decomposition
		return new Triangular <T> (mgr).det (U, L);
	}


}

