
package net.myorb.math.specialfunctions;

import net.myorb.utilities.Clib;

/**
 * Euler numbers and polynomials
 * @author Michael Druckman
 */
public class Euler
{


	/**
	 * Euler constant (epsilon, natural log base)
	 */
	public static final double CONSTANT = 2.718281828459;


	/**
	 * evaluate a Euler polynomial
	 * @param n the order of the polynomial
	 * @param z the parameter to the function
	 * @return the polynomial value
	 */
	public static double evalPoly (int n, double z)
	{
		if (n < 0)
		{
		    throw new RuntimeException ("ValueError: Euler polynomials only defined for n >= 0");
		}

		if (n <= 2)
		{
		    if (n == 0) return 1;
		    if (n == 1) return z - 0.5;
		    if (n == 2) return z * (z - 1);
		}

		if (Clib.isinf (z)) return Clib.pow (z, n);
		if (Clib.isnan (z)) return z;

		int m = n + 1;

		if (z == 0)
			return - ldexpNumber (m);
		if (z == 1) return ldexpNumber (m);
		if (z == 0.5 && n%2 != 0) return 0;

		return terms (z, n);
	}

	/**
	 * Bernoulli multiplied by factor of 2
	 * @param m the index of the Bernoulli number
	 * @return the calculated value
	 */
	static double ldexpNumber (int m)
	{
		return 2 * (Clib.ldexp (1, m) - 1) * Bernoulli.number (m) / m;
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


	/**
	 * summation of terms for polynomial evaluation
	 * @param z the parameter to the function
	 * @param n the order of the polynomial
	 * @return the calculated value
	 */
	public static double terms (double z, int n)
	{
		double sum = 0.0, t = 1,
			w = Clib.ldexp (1, n+2);
        int k = 0;

        while (true)
        {
            int v = n - k + 1;
            if ( ! (v > 2 && v % 2 != 0) )
            { sum += (2-w) * Bernoulli.number (v) * t; }
            if ((k += 1) > n) break;
            t = t * z * (n-k+2) / k;
            w *= 0.5;
        }

        return sum;
	}


	/**
	 * Euler numbers
	 * @param n the index of the number
	 * @return the value
	 */
	public static double number (int n)
	{
		if (n % 2 != 0) return 0.0;
		return Clib.ldexp (evalPoly (n, 0.5), n);
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


}

