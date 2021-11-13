
package net.myorb.math.computational;

import net.myorb.math.complexnumbers.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.matrices.*;

/**
 * implementation of discrete Fourier transform
 * as documented at https://en.wikipedia.org/wiki/DFT_matrix
 * @author Michael Druckman
 */
public class DFT extends ComplexLibrary<Double>
{

	/**
	 * data type is Matrix(ComplexValue(Double))
	 */
	public DFT ()
	{
		super (new DoubleFloatingFieldManager (), null);
	}

	/**
	 * the Vandermonde matrix 
	 * @param N the matrix size (edge of square)
	 * @return a Vandermonde matrix size N
	 */
	public Matrix<ComplexValue<Double>> getVandermondeMatrix (int N)
	{
		ComplexValue<Double> cn = C (N);
		ComplexFieldManager<Double> cfm = new ComplexFieldManager<Double> (manager);
		Matrix<ComplexValue<Double>> result = new Matrix<ComplexValue<Double>> (N, N, cfm);
		ComplexValue<Double> omega = cis (neg (piTimes (2, N))), normalization = pow (sqrt (cn), -1);
		
		for (int i = 1; i <= N; i++)
		{
			for (int j = 1; j <= N; j++)
			{
				int ij = (i - 1) * (j - 1);
				result.set (i, j, cfm.multiply (pow (omega, ij), normalization));
			}
		}
		return result;
	}


}
