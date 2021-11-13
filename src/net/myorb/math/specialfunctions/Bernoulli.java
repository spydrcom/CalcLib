
package net.myorb.math.specialfunctions;

import net.myorb.utilities.Clib;

/**
 * Bernoulli numbers and polynomials
 * @author Michael Druckman
 */
public class Bernoulli
{


	/**
	 * Bernoulli numbers of first kind
	 * @param n the index of the number
	 * @return the value
	 */
	public static double number (int n)
	{
		double a[] = new double[n+1];
		for (int m=0; m<=n; m++)
		{
			a[m] = 1.0 / (m+1);
			for (int j=m; j>=1; j--)
			{
				a[j-1] = j * (a[j-1] - a[j]);
			}
		}
		return n==1? -a[0]: a[0];
	}

	/**
	 * allow for index to be specified as real number
	 * @param n the index of the number
	 * @return the value
	 */
	public static double numberN (double n)
	{
		return number ((int) n);
	}


	/**
	 * polynomial evaluation 
	 *  for values greater than 2
	 * @param n the order of the polynomial
	 * @param z the parameter to the function
	 * @return the polynomial value
	 */
	public static double gt2 (int n, double z)
	{
		double sum = 1.0, t = 1.0, r = 1 / z;

		for (int k = 1; k <= n; k++)
		{
			t = t * r * (n - k + 1) / k;

			if (k <= 2 || k % 2 == 0)
			{
				sum += t * number (k);
			}
		}

		return sum;
	}


	/**
	 * polynomial evaluation 
	 *  for values less than/equal 2
	 * @param n the order of the polynomial
	 * @param z the parameter to the function
	 * @return the polynomial value
	 */
	static double le2 (int n, double z)
	{
		double t = 1.0;
		double sum = number (n);

		for (int k = 1; k <= n; k++)
		{
	          int m = n - k;
	          t = t * z * (n - k + 1) / k;

	          if (m <= 2 || m % 2 == 0)
	          {
	        	  sum += t * number (m);
	          }
		}

		return sum;
	}


	/**
	 * evaluate a Bernoulli polynomial
	 * @param n the order of the polynomial
	 * @param z the parameter to the function
	 * @return the polynomial value
	 */
	public static double evalPoly (int n, double z)
	{
   	    if (n < 0)
   	    	throw new RuntimeException ("ValueError: Bernoulli polynomials only defined for n >= 0");
   	    if (z == 0 || (z == 1 && n > 1)) return number (n);
   	    if (z == 0.5)
   	    {
   	    	return (Clib.ldexp (1, 1 - n) - 1) * number (n);
   	    }

   	    if (n <= 3)
   	    {
   	        if (n == 0) return 1;
   	        if (n == 1) return z - 0.5;
   	        if (n == 2) return (6*z*(z-1)+1)/6;
   	        if (n == 3) return z*(z*(z-1.5)+0.5);
   	    }

   	    if (Clib.isnan (z)) return z;
   	    if (Clib.isinf (z)) return Clib.pow (z, n);
   	    if (Clib.abs (z) > 2) return gt2 (n, z);
   	    else return le2 (n, z);
	}


	/**
	 * allow order to be 
	 *  specified as real number
	 * @param n the order of the polynomial
	 * @param z the parameter to the function
	 * @return the polynomial value
	 */
	public static double evalPolyN (double n, double z)
	{
		return evalPoly ((int) n, z);
	}


}

