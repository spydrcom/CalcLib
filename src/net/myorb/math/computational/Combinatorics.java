
package net.myorb.math.computational;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.specialfunctions.PochhammerSymbol;
import net.myorb.math.specialfunctions.Gamma;
import net.myorb.math.*;

import java.util.ArrayList;

/**
 * implementation of functions related to Combinatorics
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Combinatorics<T>  extends Tolerances<T>
{


	public static final double
	gamma = 0.57721566490153286060651209008240243104215933593992;


	/**
	 * power library required for implementations
	 * @param manager the manager for the component type
	 * @param lib a power library for the type
	 */
	public Combinatorics
		(SpaceManager<T> manager, PowerLibrary<T> lib)
	{
		super (manager);
		this.expressionManager = (ExpressionSpaceManager<T>)manager;
		this.ZERO = manager.getZero ();
		this.TWO = manager.newScalar (2);
		this.NEGONE = manager.newScalar (-1);
		this.HALF = manager.invert (TWO);
		this.ONE = manager.getOne ();
		this.lib = lib;
	}
	ExpressionSpaceManager<T> expressionManager;
	T ZERO, ONE, NEGONE, TWO, HALF;
	PowerLibrary<T> lib;


	/**
	 * implement x^y
	 * @param x base of the power
	 * @param y exponent of the power
	 * @return x^y
	 */
	public T toThe (T x, T y)
	{
		return lib.exp (manager.multiply (lib.ln (x), y));
	}


	/*
	 * factorial
	 */


	/**
	 * factorial ratio counting up from x to x+m-1
	 * @param x the starting number of the set of factors
	 * @param m the number of factors included
	 * @return product of x .. x+m-1
	 */
	public T raisingFactorial (T x, T m)
	{
		T result = ONE, xPlus = x,
			mm1 = manager.add (m, NEGONE),
			last = manager.add (x, mm1);
		while (manager.lessThan (xPlus, last))
		{
			result = manager.multiply (result, xPlus);
			xPlus = manager.add (xPlus, ONE);
		}
		return result;
	}


	/**
	 * factorial ratio counting down from x to x-m+1
	 * @param x the starting number of the set of factors
	 * @param m the number of factors included
	 * @return product of x-m+1 .. x
	 */
	public T fallingFactorial (T x, T m)
	{
		T result = ONE, xMinus = x,
			last = manager.add (x, manager.negate (m));
		while (manager.lessThan (last, xMinus))
		{
			result = manager.multiply (result, xMinus);
			xMinus = manager.add (xMinus, NEGONE);
		}
		return result;
	}


	/**
	 * product of 1 .. x
	 * @param x the number of factors
	 * @return 1*2*3*4* ... *x
	 */
	public T factorial (T x)
	{
		return raisingFactorial (ONE, manager.add (x, ONE));
	}


	/*
	 * Pochhammer
	 */


	/**
	 * evaluation of Pochhammer symbol
	 * @param a the starting number of the set of factors
	 * @param n the number of factors included
	 * @return gamma(a+n) / gamma(a)
	 */
	public T raisingPochhammer (T a, T n)
	{
		int factors = manager.toNumber (n).intValue ();
		double starting = manager.toNumber (a).doubleValue ();
		return expressionManager.convertFromDouble (PochhammerSymbol.eval (starting, factors));
	}


	/*
	 * binomial coefficients
	 */


	/**
	 * binomial coefficients
	 * @param n the upper number of the set
	 * @param k the lower number of the set
	 * @return n! / ( k! * (n - k)! )
	 */
	public T binomialCoefficient (T n, T k)
	{
		if (manager.isZero (k)) return ONE;
		if (manager.isNegative (k) || manager.isZero (n)) return ZERO;

		T nmk = manager.add (n, manager.negate (k));
		if (manager.isZero (nmk)) return ONE;

		T n2 = manager.multiply
			(n, manager.invert (TWO));
		if (manager.lessThan (n2, k)) k = nmk;

		return manager.multiply
			(
				fallingFactorial (n, k),
				manager.invert (factorial (k))
			);
	}


	public double binomialCoefficient (int n, int k)
	{
		if (k < 0 || k > n) return 0;
		if (k == 0 || k == n) return 1;

		double c = 1;
		int nmk = n - k;
		int hi = k<nmk? k: nmk;

		for (int i = 0; i < hi; i++)
		{
			float nm1 = n - 1, ip1 = i + 1;
			c *= nm1 * ip1;
		}

		return c;
	}


	/*
	 * Bernoulli
	 */


	/**
	 * a term of the Bernoulli numbers computation
	 * @param n first or second indicator (n=0 for first, n=1 second)
	 * @param m the ordinal of the number in the sequence
	 * @param k the outer sum index of the formula
	 * @param v the inner sum index of the formula
	 * @return the value of the term
	 */
	public T bernoulliTerm (int n, int m, T k, int v)
	{
		T c = binomialCoefficient
			(k, manager.newScalar (v));
		if (manager.isZero (c)) return ZERO;
		T p = lib.pow (manager.newScalar (v + n), m);
		return manager.multiply (c, p);
	}
	public T bernoulliInnerSum (int n, int m, int k)
	{
		T sum = ZERO, term, kt = manager.newScalar (k),
			kPlus1Inv = manager.invert (manager.add (kt, ONE));
		for (int v = 0; v <= k; v++)
		{
			term = bernoulliTerm (n, m, kt, v);
			if (!manager.isZero (term))
			{
				term = manager.multiply (term, kPlus1Inv);
				sum = manager.add (sum, term);
			}
			kPlus1Inv = manager.negate (kPlus1Inv);
		}
		return sum;
	}

	/**
	 * Bernoulli number 
	 * @param n indicator n=0 for first, n=1 second
	 * @param m the ordinal of the number in the sequence
	 * @return the number of the sequence
	 */
	public T B (int n, int m)
	{
		T sum = ZERO;
		for (int k = 0; k <= m; k++)
		{
			T term = bernoulliInnerSum (n, m, k);
			sum = manager.add (sum, term);
		}
		return sum;
	}

	/**
	 * first Bernoulli number sequence
	 * @param m the ordinal of the number in the sequence
	 * @return the number of the sequence
	 */
	public T B (int m)
	{
		T value = B (0, m);
		return m==1? manager.negate(value): value;
	}

	/**
	 * second Bernoulli number sequence
	 * @param m the ordinal of the number in the sequence
	 * @return the number of the sequence
	 */
	public T B2 (int m)
	{
		return B (1, m);
	}


	/**
	 * optimized version of Bernoulli algorithm
	 * @param n the ordinal of the number in the sequence
	 * @return B(n) [second kind]
	 */
	public T optimizedBernoulli (int n)
	{
		ArrayList<T> a = new ArrayList<T>();

		for (int m=0; m<=n; m++)
		{
			a.add (manager.invert (manager.newScalar (m + 1)));

			for (int j=m; j>=1; j--)
			{
				T dif = manager.add (a.get (j - 1), manager.negate (a.get (j)));
				a.set (j - 1, manager.multiply (dif, manager.newScalar (j)));
			}
		}

		return a.get (0);
	}
	public T firstKindBernoulli (int n)
	{ T Bn = optimizedBernoulli (n); return n==1? manager.negate (Bn): Bn; }
	public T secondKindBernoulli (int n) { return optimizedBernoulli (n); }


	/*
	 * Bernoulli optimized pseudocode
	 * taken from Wikipedia.org pages found at
	 * https://en.wikipedia.org/wiki/Bernoulli_number
	 * 
	  for m from 0 by 1 to n do
	    A[m] = 1/(m+1)
	    for j from m by -1 to 1 do
	      A[j-1] = j×(A[j-1] - A[j])
	  return A[0] (which is Bn)	
	 * 
	 */


	/**
	 * fast Bernoulli approximation (first kind)
	 * @param n index of the Bernoulli number
	 * @return B(n)
	 */
	public static double bernoulli (int n)
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


	/*
	 * GAMMA
	 */


	/**
	 * the first two factors of gamma
	 * @param t the parameter to the gamma function
	 * @return ( t * (t+1 )^(-1)
	 */
	public T gammaInit (T t)
	{
		T tPlusOne = manager.add (t, ONE);
		return manager.invert (manager.multiply (t, tPlusOne));
	}

	/**
	 * the Nth factor of the gamma product
	 * @param t the parameter to the gamma function
	 * @param n the next number of the factor in the series (n GT 1)
	 * @return ( n / (t+n) ) * ( n / (n-1) )^t
	 */
	public T gammaNthFactor (T t, int n)
	{
		T nScalar = manager.newScalar (n), tPlusN = manager.add (t, nScalar);
		T nOverNminus1 = manager.multiply (nScalar, manager.invert (manager.newScalar (n - 1)));
		T nOverTplusN = manager.multiply (nScalar, manager.invert (tPlusN));
		return manager.multiply (nOverTplusN, toThe (nOverNminus1, t));
	}

	/**
	 * multiple the first N factors of the gamma product series
	 * @param t the parameter to the gamma function
	 * @param n number of factors to be included
	 * @return product of factors
	 */
	public T gamma (T t, int n)
	{
		T result = gammaInit (t);
		for (int i = 2; i <= n; i++)
		{ result = manager.multiply (result, gammaNthFactor (t, i)); }
		return result;
	}

	/**
	 * compute gamma(t) to coded number of factors
	 * @param t the parameter to the gamma function
	 * @return gamma(t) to coded number of factors
	 */
	public T gamma (T t)
	{
		if (USE_SPLINE)
			return expressionManager.convertFromDouble
				(gammaSpline.eval (expressionManager.convertToDouble (t)));
		return gamma (t, GAMMA_TERMS);
	}
	static final boolean USE_SPLINE = true;
	static final int GAMMA_TERMS = 8000;

	static
	{ if (USE_SPLINE) gammaSpline = new Gamma (); }
	static Gamma gammaSpline;

	/**
	 * for z with large real component
	 * @param z the parameter to the gamma function (approximation for large z)
	 * @return approximate log(gamma(z))
	 */
	public T logGamma (T z)
	{
		//  ln (Gamma(z)) approx (z - 1/2) ln(z) - z + ln(2*pi)/2
		T tMhalfLnT = manager.multiply (lib.ln (z), manager.add (z, manager.negate (HALF)));
		T lnPiMinusT = manager.add (halfLn2Pi (), manager.negate (z));
		return manager.add (tMhalfLnT, lnPiMinusT);
	}
	T halfLn2Pi ()
	{
		if (HALF_LN_2PI == null)
		{
			HALF_LN_2PI = manager.multiply
			(lib.ln (manager.multiply (TWO, manager.getPi ())), HALF);
		}
		return HALF_LN_2PI;
	}
	T HALF_LN_2PI = null; // 0.91893853320467274178


	/*
	 * zeta
	 */


	/**
	 * compute the Nth term of the zeta function
	 * @param s the parameter to the zeta function
	 * @param n the next number of the term in the series (n GT 0)
	 * @return value of the Nth term
	 */
	public T zetaNthTerm (T s, T n)
	{
		return manager.invert (toThe (n, s));
	}

	/**
	 * compute zeta(s)
	 *  for specified number of terms
	 * @param s the parameter to the zeta function
	 * @param n number of terms to be computed
	 * @return zeta(s) for n terms
	 */
	public T zeta (T s, int n)
	{
		T result = ZERO;
		for (int i = 1; i <= n; i++)
		{ result = manager.add (result, zetaNthTerm (s, manager.newScalar (i))); }
		return result;
	}

	/**
	 * compute zeta(s) to coded number of terms
	 * @param s the parameter to the zeta function
	 * @return zeta(s) to coded number of terms
	 */
	public T zeta (T s)
	{
		if (manager.isNegative (s))
		{
			int nPlus1 = 1 - manager.toNumber (s).intValue ();

			return manager.multiply
				(
					manager.negate (optimizedBernoulli (nPlus1)),
					manager.invert (manager.newScalar (nPlus1))
				);
		}
		return zeta (s, ZETA_TERMS);
	}
	static final int ZETA_TERMS = 500;


	/*
	 * Harmonic
	 */


	/**
	 * compute harmonic number for specified count
	 * @param n the number of terms to be included
	 * @return the sum of the terms
	 */
	public T H (int n)
	{
		T result = ZERO;
		for (int i = 1; i <= n; i++)
		{
			result = manager.add (result, manager.invert (manager.newScalar (i)));
		}
		return result;
	}

	/**
	 * compute the Kth term of the H function
	 * @param x the parameter to the H function
	 * @param k the next number of the term in the series
	 * @return 1 / (k^2 + kx)
	 */
	public T hSeriesNthTerm (T x, T k)
	{
		T kPlusX = manager.add (x, k);
		T kSqPlusXk = manager.multiply (kPlusX, k);
		return manager.invert (kSqPlusXk);
	}

	/**
	 * compute H(x)
	 *  for specified number of terms
	 * @param x the parameter to the H function
	 * @param n number of terms to be computed
	 * @return H(x) for n terms
	 */
	public T H (T x, int n)
	{
		T sum = ZERO;
		for (int k = 1; k <= n; k++)
		{ sum = manager.add (sum, hSeriesNthTerm (x, manager.newScalar (k))); }
		return manager.multiply	(x, sum);
	}

	/**
	 * compute the harmonic number
	 *  for a real or complex value with coded term count
	 * @param x the parameter to the H function
	 * @return H(x) for coded number of terms
	 */
	public T H (T x)
	{
		return H (x, H_TERMS);
	}
	static final int H_TERMS = 10000;


}
