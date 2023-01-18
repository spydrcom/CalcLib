
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
		(SpaceManager <T> manager, ExtendedPowerLibrary <T> lib)
	{
		super (manager);
		this.expressionManager =
			(ExpressionSpaceManager <T>) manager;
		this.ZERO = manager.getZero ();
		this.TWO = manager.newScalar (2);
		this.THREE = manager.newScalar (3);
		this.NEGONE = manager.newScalar (-1);
		this.NEGTWO = manager.newScalar (-2);
		this.FOUR = manager.newScalar (4);
		this.HALF = manager.invert (TWO);
		this.ONE = manager.getOne ();
		this.lib = lib;
	}
	protected ExpressionSpaceManager <T> expressionManager;
	protected T ZERO, ONE, NEGONE, TWO, NEGTWO, THREE, FOUR, HALF;


	/**
	 * base to integer exponent
	 * - exponents in real domain will be truncated
	 * - so true formula is x^floor(n)
	 * @param x base of computation
	 * @param n exponent value
	 * @return x^n
	 */
	public T pow (T x, T n)
	{
		T remaining = n, result = ONE;
		while (manager.lessThan (ZERO, remaining))
		{
			result = manager.multiply (result, x);
			remaining = manager.add (remaining, NEGONE);
		}
		return result;
	}


	/**
	 * implement x^y
	 * - using logarithms
	 * @param x base of the power
	 * @param y exponent of the power
	 * @return exp(ln(x)*y) using lib operations
	 */
	public T toThe (T x, T y)
	{
		return lib.exp (manager.multiply (lib.ln (x), y));
	}


	/*
	 * 
	 * factorial algorithms
	 * 
	 * NOTE:
	 *		managed data types need to remain managed...
	 *		case in point Q! and Q3 in EulerComputer class (below)
	 *		raisingFactorial called for Q (quarter) real values
	 *		this does fit the factorial definition
	 *		integer versions allow optimization
	 *
	 */


	/**
	 * factorial ratio counting up from x to x+m-1
	 * @param x the starting number of the set of factors
	 * @param m the number of factors included
	 * @return product of x .. x+m-1
	 */
	public T raisingFactorial (int x, int m)
	{
		T result = ONE; int endingAt = x + m;
		for (int xPlus = x; xPlus < endingAt; xPlus++)
		{
			result =
				manager.multiply
				(
					manager.newScalar (xPlus),
					result
				);
		}
		return result;
	}
	public T raisingFactorial (T x, T m)
	{
		T result = ONE, xPlus = x,
			endingAt = manager.add (x, m);
		while (manager.lessThan (xPlus, endingAt))
		{
			result =
				manager.multiply
				(
					xPlus,
					result
				);
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
	public T fallingFactorial (int x, int m)
	{
		T result = ONE; int endingAt = x - m;
		for (int xMinus = x; endingAt < xMinus; xMinus--)
		{
			result =
				manager.multiply
				(
					manager.newScalar (xMinus),
					result
				);
		}
		return result;
	}
	public T fallingFactorial (T x, T m)
	{
		T result = ONE, xMinus = x,
			endingAt = manager.add (x, manager.negate (m));
		while (manager.lessThan (endingAt, xMinus))
		{
			result =
				manager.multiply
				(
					xMinus,
					result
				);
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
		return raisingFactorial (ONE, x);
	}
	public T factorial (int x)
	{
		return raisingFactorial (1, x);
	}


	/**
	 * simple static integer factorial
	 * - double returned to extend range of result
	 * @param n the value to use as parameter
	 * @return n!
	 */
	public static double F (double n)
	{
		double res = n;
		if (n < 2) return 1;
		for (int f = (int) n - 1; f > 1; f--) res *= f;
		return res;
	}


	/*
	 * Derangements Counts
	 */


	/**
	 * derangement number (subfactorial !n)
	 * @param n the value to use as parameter
	 * @return floor ( 0.5 + n! / e )
	 */
	public static double subfactorial (double n)
	{
		if (n == 0) return 1;
		return Math.floor ( 0.5 + F (n) / Math.E );
	}


	/**
	 * generic version of subfactorial
	 * @param n number of derangements to consider
	 * @return computed count
	 */
	public T derangementsCount (T n)
	{
		if (manager.lessThan (n, ONE)) return ONE;
		if (manager.lessThan (n, TWO)) return ZERO;

		// !n = (n-1) ( ! (n - 1) + ! (n - 2) ) for n > 1
		T nm1 = manager.add (n, NEGONE), nm2 = manager.add (n, NEGTWO);
		T nm1d = derangementsCount (nm1), nm2d = derangementsCount (nm2);
		return manager.multiply (nm1, manager.add (nm1d, nm2d));
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
	 * binomial coefficients (Pascal Numbers)
	 */


	/**
	 * binomial coefficients
	 * @param n the upper number of the set
	 * @param k the lower number of the set
	 * @return n! / ( k! * (n - k)! )
	 */
	public T binomialCoefficient (T n, T k)
	{
		T nmk = manager.add (n, manager.negate (k));

		if (manager.isNegative (k) || manager.isNegative (nmk)) return ZERO;
		if (manager.isZero (k) || manager.isZero (nmk)) return ONE;

		T kf = ONE, nf = ONE, i = ONE;
		while ( ! manager.lessThan (n, i))
		{
			if ( manager.lessThan (nmk, i) ) nf = manager.multiply (nf, i);
			if ( ! manager.lessThan (k, i) ) kf = manager.multiply (kf, i);
			i = manager.add (i, ONE);
		}

		return manager.multiply (nf, manager.invert (kf));
	}


	/**
	 * binomial coefficients
	 * - specifically for integer operands
	 * - factorials computed in managed type
	 * @param n the upper number of the set
	 * @param k the lower number of the set
	 * @return n! / ( k! * (n - k)! )
	 */
	public T binomialCoefficient (int n, int k)
	{
		if ( k < 0 || k > n ) return ZERO;
		if (k == 0 || k == n) return ONE;

		T kf = ONE, nf = ONE; int nmk = n - k;

		for (int i = 1; i <= n; i++)
		{
			T iT = manager.newScalar (i);
			if (i > nmk) nf = manager.multiply (nf, iT);
			if (i <= k) kf = manager.multiply (kf, iT);
		}

		return manager.multiply (nf, manager.invert (kf));
	}


	/**
	 * binomial coefficients
	 * - specifically for integer operands
	 * - using hardware double float so overflow possible
	 * @param n the upper number of the set
	 * @param k the lower number of the set
	 * @return n! / ( k! * (n - k)! )
	 */
	public static double binomialCoefficientHW (int n, int k)
	{
		if ( k < 0 || k > n ) return 0;
		if (k == 0 || k == n) return 1;

		double kf = 1, nf = 1, nmk = n - k;

		for (int i = 1; i <= n; i++)
		{
			if (i > nmk) nf *= i;
			if (i <= k) kf *= i;
		}

		return nf / kf;
	}


	/*
	 * Lobb, Catalan, Stirling, Euler, ...
	 * - series named for famous mathematicians
	 * - most based on Pascal's triangle
	 */


	/**
	 * compute Lobb numbers
	 * @param m the upper number of the set
	 * @param n the lower number of the set
	 * @return the computed Lobb number
	 */
	public static double lobbNumbers (int m, int n)
	{
		int n2 = 2 * n, mn = m + n;

		double difference =
			binomialCoefficientHW (n2, mn) -
			binomialCoefficientHW (n2, mn+1);
		return difference;
	}

	public T lobbNumbers (T m, T n)
	{
		T
		n2 = manager.multiply (TWO, n),
		mn = manager.add (m, n), mn1 = manager.add (mn, ONE),
		mnbc = binomialCoefficient (n2, mn), mn1bc = binomialCoefficient (n2, mn1);
		return manager.add (mnbc, manager.negate (mn1bc));
	}


	/**
	 * compute Catalan numbers
	 * @param n the index of the series
	 * @return the Nth Catalan number
	 */
	public static double catalanNumbers (int n)
	{
		return binomialCoefficientHW (2*n, n) / (n + 1);
	}

	public T catalanNumbers (T n)
	{
		T n1 = manager.add (n, ONE),
		n2 = manager.multiply (TWO, n), bc = binomialCoefficient (n2, n);
		return manager.multiply (bc, manager.invert (n1));
	}


	/**
	 * sequence of Bell numbers
	 * @param n the index into the sequence
	 * @return Nth Bell number
	 */
	public static double bellNumbers (int n)
	{
		double sum = 0;
		// !! Bn(n) = SIGMA [ 0 <= k <= n ] ( n $$$ k )
		for (int k = 0; k <= n; k++)
		{
			sum += stirlingNumbers2HW (n, k);
		}
		return sum;
	}


	/**
	 * compute entries of the triangle
	 * @param i row number to identify entry
	 * @param j column number to identify entry
	 * @return computed entry
	 */
	public static double bellTriangle (int i, int j)
	{
		if (j > i) return 0; else if (j == 0)
		{ if (i == 0) return 1; return bellTriangle (i-1, i-1); }
		else return bellTriangle (i, j-1) + bellTriangle (i-1, j-1);
	}


	/*
	 * Stirling Numbers first and second kind
	 */


	/**
	 * Stirling Numbers first kind { n / k }
	 * @param n the upper number of the set
	 * @param k the lower number of the set
	 * @return S ( n, k )
	 */
	public static double stirlingNumbers1HW (int n, int k)
	{
		//  s (n + 1, k) = n * s (n, k) + s (n, k - 1)
		if (n == 0 && k == 0) return 1; if (n == 0 || k == 0) return 0;
		return (n-1) * stirlingNumbers1HW (n-1, k) + stirlingNumbers1HW (n-1, k-1);
	}

	public T stirlingNumbers1 (int n, int k)
	{
		return stirlingNumbers1
			(
				manager.newScalar (n),
				manager.newScalar (k)
			);
	}

	public T stirlingNumbers1 (T n, T k)
	{
		if (manager.isZero (n) && manager.isZero (k)) return ONE;
		if (manager.isZero (n) || manager.isZero (k)) return ZERO;
		T n1 = manager.add (n, NEGONE), k1 = manager.add (k, NEGONE);
		T sn1k = manager.multiply (n1, stirlingNumbers1 (n1, k));
		T sn1k1 = stirlingNumbers1 (n1, k1);
		return manager.add (sn1k, sn1k1);
	}


	/**
	 * Stirling Numbers second kind { n / k }
	 * - this uses hardware float so overflow danger
	 * @param n the upper number of the set
	 * @param k the lower number of the set
	 * @return S ( n, k )
	 */
	public static double stirlingNumbers2HW (int n, int k)
	{
		if (k > n) return 0;
		double F = 1.0, S = -1;

		// 1/k! * SUM [i=0:k] (-1)^i * BC(k/i) * (k - i)^n
		double number = Math.pow (k, n);

		for (int i = 1; i <= k; i++, S = -S)
		{
			number += S * Math.pow (k - i, n) *
				binomialCoefficientHW (k, i);
			F *= i; // compiled factorial
		}

		return number / F;
	}

	public T stirlingNumbers2 (int n, int k)
	{
		if (n < k) return ZERO;
		return new StirlingComputer (manager, n, k)
		.computeNumber ();
	}

	class StirlingComputer extends CommonSummation <T>
	{

		StirlingComputer (SpaceManager <T> manager, int N, int K)
		{ super (manager); this.K = K; this.N = N; }
		protected int N, K;

		public T factor1 (int n, int i)
		{
			T kmi = manager.newScalar (K - i);
			return alternating (manager.pow (kmi, N), i);
		}

		public T factor2 (int n, int i)
		{
			F = manager.multiply (F, manager.newScalar (i));
			return binomialCoefficient (K, i);
		}
		protected T F = ONE;

		T computeNumber ()
		{
			return manager.multiply
			(
				manager.add
				(									// (
					computeSum (K),					//   SIGMA 1:K

					manager.pow						//		+
					(
						manager.newScalar (K), N	//	  K ^ N
					)								// )
				),									//		/

				manager.invert (F)					//		i!
			);
		}

	}

	public T stirlingNumbers2 (T n, T k)
	{
		return stirlingNumbers2
			(
				manager.toNumber (n).intValue (),
				manager.toNumber (k).intValue ()
			);
	}


	/*
	 * 
	 * HGF:  2F1 ( 1 - n, -n ; 2 ; k )
	 * 
	 * 	k = 1 => Narayana Numbers
	 * 	k = 2 => Shroder Numbers
	 * 
	 */


	/**
	 * compute Narayana numbers
	 * @param n the upper number of the set
	 * @param k the lower number of the set
	 * @return the computed Narayana number
	 */
	public static double narayanaNumbers (int n, int k)
	{
		double product =
			binomialCoefficientHW (n, k) *
			binomialCoefficientHW (n, k-1);
		return product / n;
	}


	/**
	 * compute Shroder numbers
	 * @param n the index of the series
	 * @return the computed Shroder number
	 */
	public static double shroderNumbers (int n)
	{
		double sum = 0;
		for (int i = 1; i <= n; i++)
		{ sum += narayanaNumbers (n, i) * Math.pow (2, i-1); }
		return sum;
	}


	/*
	 * Euler numbers, coefficients, and polynomials
	 */


	/**
	 * common manager for summation of terms found in
	 *		Stirling algorithms for Euler numbers
	 */
	abstract class EulerComputer extends CommonSummation <T>
	{

		EulerComputer (SpaceManager <T> manager)
		{
			super (manager);
			Q1 = manager.invert (FOUR);					// 1/4 and 3/4 values having mantissa (see note above)
			Q3 = manager.multiply (THREE, Q1);			// 	  NOTE: raising factorial forced to be managed
		}

		public T factor1 (int n, int k) { return stirlingFactor (n, k); }
		public T factor2 (int n, int k) { return computeRaisingFactorial (k); }
		abstract T computeRaisingFactorial (int index);

		/**
		 * Stirling second kind evaluations for Euler numbers
		 * @param n the index of the Euler number being computed
		 * @param l the loop index being evaluated in the algorithm
		 * @return the Stirling number needed in the computation
		 */
		T stirlingFactor (int n, int l)
		{
			T factor = manager.multiply
				(
					manager.invert (manager.newScalar (l + 1)),
					stirlingNumbers2 (n, l)
				);
			return alternating (factor, l);
		}

		protected T Q1,  Q3;

	}


	/**
	 * Euler number
	 * @param n index in the series
	 * @return the computed number
	 */
	public T En (int n)
	{
		return n > 0
			? new EnComputer (manager)
				.computeNumber (n)
			: ONE;
	}
	class EnComputer extends EulerComputer
	{
		T computeRaisingFactorial (int l)
		{
			T lT = manager.newScalar (l),
				RF1 = raisingFactorial (Q1, lT),
				RF3 = raisingFactorial (Q3, lT);
			T RF = manager.multiply (THREE, RF1);
			return manager.add (manager.negate (RF3), RF);
		}
		T computeNumber (int n)
		{
			T multiplier = manager.pow (TWO, 2*n-1);
			return manager.multiply (multiplier, computeSum (n));
		}
		EnComputer (SpaceManager <T> manager) { super (manager); }
	}


	/**
	 * Euler number
	 * @param twoN index in the series
	 * @return the computed number
	 */
	public T E2n (int twoN)
	{
		return twoN > 0
			? new E2nComputer (manager)
				.computeNumber (twoN)
			: ONE;
	}
	class E2nComputer extends EulerComputer
	{
		T computeRaisingFactorial (int l)
		{
			return raisingFactorial (Q3, manager.newScalar (l));
		}
		T computeNumber (int twoN)
		{
			T multiplier = manager.pow (manager.negate (FOUR), twoN);
			return manager.negate (manager.multiply (multiplier, computeSum (twoN)));
		}
		E2nComputer (SpaceManager <T> manager) { super (manager); }
	}


	/**
	 * double sum computation of Euler 2n
	 * - this algorithm is less likely to overflow
	 * - even tests using hardware float did not show the problem
	 * @param twoN index into the series of numbers
	 * @return the number given the index
	 */
	public T E2nDoubleSum (int twoN)
	{
		return twoN > 0
			? new EulerSummation (manager, twoN)
				.computeSum (twoN)
			: ONE;
	}
	class EulerSummation extends CommonSummation <T>
	{

		EulerSummation (SpaceManager <T> manager, int twoN)
		{ super (manager); this.inner = new InnerSummation (manager, twoN); }

		public T factor2 (int twoN, int k) { return inner.computeSum (0, 2*k); }					//		SIGMA 0:2k
		public T factor1 (int twoN, int k) { return manager.pow (NEGTWO, -k); }						//	    (-2) ^ (-k)
		protected InnerSummation inner;

		class InnerSummation extends CommonSummation <T>
		{
			public T factor2 (int twoK, int l)														//		(-1) ^ l  *
			{ return alternating (manager.pow (manager.newScalar ( twoK/2 - l ), twoN ), l ); }		//	  ( k - l ) ^ 2n
			public T factor1 (int twoK, int l) { return binomialCoefficient (twoK, l); }			//		2*k ## l

			InnerSummation (SpaceManager <T> manager, int twoN)
			{ super (manager); this.twoN = twoN; }
			protected int twoN;
		}

	}


	/**
	 * Euler Numbers A ( n , m )
	 * @param n the upper number of the set
	 * @param m the lower number of the set
	 * @return E ( n, m )
	 */
	public static double eulerNumbers (int n, int m)
	{
		if (m > n) return 0;

		double S = 1;
		// SUM [k=0:m+1] (-1)^k * BC(n+1/k) * (m + 1 - k)^n
		double number = 0;

		for (int k = 0; k <= m+1; k++, S = -S)
		{
			number += S * Math.pow (m + 1 - k, n) *
				binomialCoefficientHW (n+1, k);
		}

		return number;
	}


	/**
	 * compute Euler polynomial coefficients of specified order
	 * @param n the order of the polynomial
	 * @return the array of coefficients
	 */
	public static double [] eulerCoefficients (int n)
	{
		double [] c = new double [n+1];
		for (int m = 0; m <= n; m++) c [m] = eulerNumbers (n, m);
		return c;
	}


	/**
	 * evaluate Euler polynomial of specified order
	 * @param n the order of the polynomial
	 * @param t the polynomial variable
	 * @return the computed value
	 */
	public T eulerPolynomial (int n, T t)
	{
		T sum = manager.getZero (), P = manager.getOne ();
		// An (t) = SUM [m=0:n] ( A(n,m) * t^m )
		for (int m = 0; m <= n; m++)
		{
			T c = manager.newScalar
				( (int) eulerNumbers (n, m) );
			T term = manager.multiply (P, c);
			sum = manager.add (sum, term);
			P = manager.multiply (P, t);
		}
		return sum;
	}


	/**
	 * Euler Numbers {{ n / m }}
	 * @param n the upper number of the set
	 * @param m the lower number of the set
	 * @return {{ n, m }}
	 */
	public static double eulerNumbersSecondOrder (int n, int m)
	{
		if (n == 0) return 0; else if (m == 0) return 1;
		double En1m1 = eulerNumbersSecondOrder (n - 1, m - 1);
		double En1m = eulerNumbersSecondOrder (n - 1, m);
		return (2*n - m - 1) * En1m1 + (m + 1) * En1m;
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


	/*
	 * Bernoulli optimized pseudocode
	 * taken from Wikipedia.org pages found at
	 * https://en.wikipedia.org/wiki/Bernoulli_number
	 * 
	  for m from 0 by 1 to n do
	    A[m] = 1/(m+1)
	    for j from m by -1 to 1 do
	      A[j-1] = j�(A[j-1] - A[j])
	  return A[0] (which is Bn)	
	 * 
	 */


	/**
	 * optimized version of Bernoulli algorithm
	 * - generic translation of pseudocode above
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


	/**
	 * fast Bernoulli approximation (first kind)
	 * - translation of pseudocode above using double
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

	/**
	 * polynomial built with Bernoulli number coefficients
	 * @param x the variable value raised to powers in series
	 * @param n the order of the polynomial
	 * @param m the data type manager
	 * @return the computed value
	 */
	public static <T> T BernoulliPolynomial
		(T x, int n, ExpressionSpaceManager <T> m)
	{
		T sum = m.getZero (), scalar, term;

		for (int k = 0; k <= n+1; k++)
		{
			scalar = m.convertFromDouble
				(
					bernoulli (k) *
					binomialCoefficientHW (n, k)
				);
			term = m.multiply (scalar, m.pow (x, n-k));
			sum = m.add (sum, term);
		}

		return sum;
	}
	public T BernoulliPolynomial (T x, int n)
	{
		return BernoulliPolynomial (x, n, expressionManager);
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
