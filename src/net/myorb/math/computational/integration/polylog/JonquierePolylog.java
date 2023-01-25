
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.data.abstractions.FunctionWrapper;
import net.myorb.data.abstractions.Function;

/**
 * description of the polylog Li functions 
 * - integer orders -4 .. 1 use known polynomials
 * - general case for negative integer orders also available
 * - positive orders computed using Taylor series
 * - special case provided for Dilogarithm
 * @author Michael Druckman
 */
public class JonquierePolylog extends ComplexSpaceCore
{


// !! Li (s, z) = 1/GAMMA(s) * INTEGRAL [ 0 <= t <= INFINITY ] ( t^(s-1) / ( exp(t)/z - 1 ) * <*> t )
// exp(t)/z - 1 = (exp(t)-z) / z  ;  t^(s-1) / ( exp(t)/z - 1 ) = z * t^(s-1) / ( exp(t) - z )
// Li[s+1](z) = INTEGRAL [ 0 <= t <= Z ] ( Li[s](t) / t * <*> t )

	/**
	 * integrand of the Li Polylog functions
	 * @param s the order of the function being described
	 * @param z the parameter to the function
	 * @param t the integration variable
	 * @return the computed value
	 */
	public static ComplexValue <Double> Li
		(int s, ComplexValue <Double> z, Double t)
	{
		ComplexValue <Double>
			expTmZ = reduce (RE (Math.exp (t)), z),
			ztToSm1 = productOf (z, POW (RE (t), s-1));
		return ratioOf (ztToSm1, expTmZ);
	}

	/**
	 * derivative of dilogarithm
	 * @param t parameter to function
	 * @return computed value
	 */
	public static ComplexValue <Double> Li2Prime (ComplexValue <Double> t)
	{
		if (isZ (t)) return ONE;
		ComplexValue <Double> oneMz;
		if (isZ (oneMz = oneMinusZ (t))) return Z;
		if (manager.isNegative (oneMz)) oneMz = NEG (oneMz);
		return ratioOf (ln (oneMz), t);
	}

	/**
	 * @param f the body of the function
	 * @return a fully wrapped version of the function
	 */
	public static Function < ComplexValue <Double> > functionFor
	(FunctionWrapper.F < ComplexValue <Double> > f)
	{
		return new FunctionWrapper < ComplexValue <Double> >
		(
			f, manager
		);
	}


	/*
	 * identify function that fulfills request
	 */

	/**
	 * @param s the (negative) order for the Li function 
	 * @return a fully wrapped version of the function
	 */
	public static Function < ComplexValue <Double> > Li (int s)
	{
		switch (s)
		{

			case  0:	return functionFor ( (z) -> Li0 (z) );
			case -1:	return functionFor ( (z) -> Lin1 (z) );
			case -2:	return functionFor ( (z) -> Lin2 (z) );
			case -3:	return functionFor ( (z) -> Lin3 (z) );
			case -4:	return functionFor ( (z) -> Lin4 (z) );
			case  1:	return functionFor ( (z) -> Li1 (z) );

			default:
				if (s > 0)
				{
					throw new RuntimeException ("Function not available");
				}
				return functionFor ( (z) -> Linn (-s, z) );
		}
	}

	/**
	 * @param s the (positive) order for the Li function 
	 * @param terms number of terms in series evaluation
	 * @return a fully wrapped version of the function
	 */
	public static Function < ComplexValue <Double> > Li (int s, int terms)
	{
		switch (s)
		{
			//			Taylor series for abs(Re(z))<1
			case  4:	return functionFor ( (z) -> Li4 (z, terms) );
			case  3:	return functionFor ( (z) -> Li3 (z, terms) );
			case  2:	return functionFor ( (z) -> Li2 (z, terms) );

			//			simple formulations
			case  1:	return functionFor ( (z) -> Li1 (z) );
			case  0:	return functionFor ( (z) -> Li0 (z) );

			default:
				if (s < 0) return Li (s);
				// Taylor series for abs(Re(z))<1 attempt general formula
				return functionFor ( (z) -> Lipn (s, terms, z, true) );
		}
	}


	/*
	 * simple formula implementations for use in Li formulations
	 */

	/**
	 * @param z the parameter to the function
	 * @return 1 - z
	 */
	public static ComplexValue <Double> oneMinusZ (ComplexValue <Double> z)
	{
		return sumOf (ONE, NEG (z));
	}

	/**
	 * @param z the parameter to the function
	 * @param power the exponent of the function
	 * @return ( 1 - z ) ^ power
	 */
	public static ComplexValue <Double> oneMinusZto (ComplexValue <Double> z, int power)
	{
		return POW (oneMinusZ (z), power);
	}

	/**
	 * evaluation of a polynomial
	 * @param z the parameter to the polynomial
	 * @param c the coefficients of the polynomial
	 * @return the evaluation of the polynomial
	 */
	public static ComplexValue <Double> poly (ComplexValue <Double> z, int [] c)
	{
		ComplexValue <Double> sum = S ( c [0] );
		for (int i = 1; i < c.length; i++)
		{
			ComplexValue <Double> scalar = S ( c [i] );
			sum = sumOf (productOf (z, sum), scalar);
		}
		return sum;
	}

	/**
	 * @param z the parameter to the function
	 * @param polyValue the calculated value of the polynomial factor
	 * @param power the exponent of the function
	 * @return poly / (1 - z)^power
	 */
	public static ComplexValue <Double> polyOver
	(ComplexValue <Double> z, ComplexValue <Double> polyValue, int power)
	{
		return ratioOf (polyValue, oneMinusZto (z, power));
	}


	/*
	 * special case treatment for Dilogarithm Li2
	 */

	/**
	 * @param z complex parameter to Li2
	 * @param terms number of terms in series
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li2
			(ComplexValue <Double> z, int terms)
	{
		if (isZ (z)) return Z;
		double zRe = Math.abs (z.Re ());
		if ( zRe < 1 ) return Lipn  (2, terms, z, false);					// SIGMA [1..N] (z^k/k^2)
		if (z.Re () < -1) return continuation (2, z, terms);				// use negative continuation
		if ( zRe > 1 || z.Im () != 0 ) return continuation (z, terms);		// use positive continuation
		if (z.Re () < 0) return RE ( - PI_SQ / 12 );						// - PI^2 / 12
		else return RE ( PI_SQ / 6 );										//   PI^2 / 6
	}

	/**
	 * processing of parameters Re(z) GT 1
	 * @param z the value of the parameter found to be GT 1
	 * @param terms the number of terms in the series evaluation
	 * @return computed value
	 */
	public static ComplexValue <Double> continuation						// Re(z) > 1 || Im(z) != 0
		(ComplexValue <Double> z, int terms)
	{
		ComplexValue <Double>
			lnz = ln (z), termSum = Z;										// ln z
		termSum = sumOf
			(
				productOf (RE (0.5), lnz),									// 1/2 * ln z + i * PI
				I_PI
			);
		termSum = sumOf
			(
				productOf (termSum, lnz),									// 1/2 * (ln z)^2 + i * PI * ln z 
				Lipn (2, terms, z, true)									// + SIGMA ...
			);
		return reduce (RE (PI_SQ/3), termSum);								// PI^2 / 3 - SUMMATION ...
	}


	/*
	 * Li for simple order 0 and 1
	 */

	/**
	 * @param z complex parameter to Li0
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li0 (ComplexValue <Double> z)
	{
		return ratioOf (z, oneMinusZ (z));									// z / (1-z)
	}

	/**
	 * @param z complex parameter to Li1
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li1 (ComplexValue <Double> z)
	{
		return NEG (ln (oneMinusZ (z)));									// - ln (1-z)
	}

	/*
	 * Li for positive order s
	 */

	/**
	 * Taylor series for abs(Re(z)) LT 1
	 * @param z complex parameter to Li3
	 * @param terms number of terms in series
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li3
			(ComplexValue <Double> z, int terms)
	{
		return Lipn (3, terms, z, false);									// SIGMA [1..N] (z^k/k^3)
	}

	/**
	 * Taylor series for abs(Re(z)) LT 1
	 * @param z complex parameter to Li4
	 * @param terms number of terms in series
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li4
			(ComplexValue <Double> z, int terms)
	{
		return Lipn (4, terms, z, false);									// SIGMA [1..N] (z^k/k^4)
	}

	/*
	 * Li for negative order s
	 */

	/**
	 * @param z complex parameter to Li -1
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Lin1 (ComplexValue <Double> z)
	{
		return polyOver (z, z, 2);											// z / (1-z)^2
	}

	/**
	 * @param z complex parameter to Li -2
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Lin2 (ComplexValue <Double> z)
	{
		return polyOver (z, poly (z, C110), 3);								// z(z+1) / (1-z)^3
	}
	static final int [] C110 = new int [] {1, 1, 0};

	/**
	 * @param z complex parameter to Li -3
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Lin3 (ComplexValue <Double> z)
	{
		return polyOver (z, poly (z, C1410), 4);							// z(z^2+4z+1) / (1-z)^4
	}
	static final int [] C1410 = new int [] {1, 4, 1, 0};

	/**
	 * @param z complex parameter to Li -4
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Lin4 (ComplexValue <Double> z)
	{
		ComplexValue <Double> polyProduct =
			productOf (poly (z, C110), poly (z, C1A1));
		return polyOver (z, polyProduct, 5);								// z(z+1)(z^2+10z+1) / (1-z)^5
	}
	static final int [] C1A1 = new int [] {1, 10, 1};


	/*
	 * analytic continuation for polylog allowing convergence for Re(z) > 1
	 */

	/**
	 * @param s order of polylog
	 * @param z complex parameter to Li
	 * @param terms number of terms in series
	 * @return computed value
	 */
	public static  ComplexValue <Double>  continuation
		(int s, ComplexValue <Double> z, int terms)
	{
		/*
		 * translation from MPMATH Python source:
		 * 
		 * if abs(z) >= 1.4 and ctx.isint(s):
		 *		return (-1)**(s+1)*polylog_series(s, 1/z) + polylog_continuation(s, z)
		 */
		ComplexValue <Double> continued =
				negWhenOdd (Lipn (s, terms, z, true), s);
		continued = sumOf (continued, continuationOffset (s, z));
		return continued;
	}
	public static ComplexValue <Double> continuationOffset
			(int s, ComplexValue <Double> z)
	{
		ComplexValue <Double> lnz,
			twoPiI = productOf (TWO, I_PI),										// 2 * pi * i
			lnz2piI = ratioOf (lnz = ln (z), twoPiI),							// ln z / ( 2 * pi * i )
			offset = productOf (POW (twoPiI, s), bernpoly (lnz2piI, s)),		// (2 * pi * i) ^ s * bernpoly
			sF = oneOver (RE (F (s)));											// 1 / s !

		offset = NEG (productOf (offset, sF));									// - (2 * pi * i) ^ s * bernpoly(...) / s!
		if (z.Im () == 0.0) return RE (offset.Re ());							// Re (offset)

	/*
	    twopij = i * 2 * pi
	    a = -twopij**n/fac(n) * bernpoly (n, ln(z)/twopij)
	    if _is_real_type(z) and z < 0: a = _re(a)
	    if _im(z) < 0 or (_im(z) == 0 and _re(z) >= 1):
	        a -= twopij*ln(z)**(n-1)/fac(n-1)
	    return a
	 */

		ComplexValue <Double>
			lnZ2sM1 = POW (lnz, s-1),										// ( ln z )^(s - 1)
			lnZsF = productOf (lnZ2sM1, productOf (sF, S (s))),				// ( ln z )^(s - 1) / (s - 1)!
			lnZsF2piI = productOf (lnZsF, twoPiI)							// 2 * pi * i * ( ln z )^(s - 1) / (s - 1)!
		;
		return reduce (offset, lnZsF2piI);
	}


	/*
	 * distinct series implementations for positive order and negative order polylog computation
	 */

	/**
	 * general positive case
	 * @param n the integer order
	 * @param terms number of terms
	 * @param z complex parameter to Li
	 * @param isContinuation indicates z outside convergence when TRUE
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Lipn
		(int n, int terms, ComplexValue <Double> z, boolean isContinuation)
	{
		ComplexValue <Double> sum = Z;
		// Li(z) = SIGMA [1 <= k <= INFINITY] (   z^k  * k^(-n) )			// for |Re(z)| < 1
		// Li(z) = SIGMA [1 <= k <= INFINITY] ( z^(-k) * k^(-n) )			// for |Re(z)| > 1
		for (int k = 1; k <= terms; k++)
		{
			sum = sumOf
				(
					sum,
					productOf
					(
						POW (S (k), -n),
						POW (z, isContinuation ? -k : k)
					)
				);
		}
		return sum;
	}

	/**
	 * general case with complex order
	 * - negative order requires continuation
	 * @param order complex order of Li
	 * @param z complex parameter to Li
	 * @param terms number of terms
	 * @return computed value
	 */
	public static ComplexValue <Double> complexPolylog
		(ComplexValue <Double> order, ComplexValue <Double> z, ComplexValue <Double> terms)
	{
		Double orderRe = order.Re (), termCount = terms.Re ();
		if (order.Im () == 0.0 && orderRe == Math.floor (orderRe))
		{ return Lipn (orderRe.intValue (), termCount.intValue (), z, false); }

		ComplexValue <Double> sum = Z;

		for (int k = 1; k <= termCount; k++)
		{
			sum = sumOf
				(
					sum,
					productOf
					(
						toThe (S (k), NEG (order)),
						POW (z, k)
					)
				);
		}

		return sum;
	}

	/**
	 * general negative case
	 * - using  chosen  numbers
	 * @param n the integer order
	 * @param z complex parameter to Li
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Linn (int n, ComplexValue <Double> z)
	{ return useStirling ? LinnStirling (n, z) : LinnEuler (n, z); }
	public static void chooseStirling () { useStirling = true; }
	public static boolean useStirling = false;

	/**
	 * general negative case
	 * - using  Euler  numbers
	 * @param n the integer order
	 * @param z complex parameter to Li
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> LinnEuler (int n, ComplexValue <Double> z)
	{
		ComplexValue <Double> sum = Z;
		// Li[-n](z) = 1 / ( 1 - z ) ^ ( n + 1 ) * SIGMA [ 0 <= k < n ] ( E(n,k) * z ^ (n-k) )
		for (int k = 0; k < n; k++) sum = sumOf (sum, termEuler (z, n, k));
		return productOf (sum, POW (oneMinusZ (z), -(n+1)));
	}
	static ComplexValue <Double> termEuler (ComplexValue <Double> z, int n, int k)
	{ return productOf (RE (EN (n, k)), POW (z, n-k)); }

	/**
	 * general negative case
	 * - using Stirling numbers
	 * @param n the integer order
	 * @param z complex parameter to Li
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> LinnStirling (int n, ComplexValue <Double> z)
	{
		ComplexValue <Double> sum = Z;
		// (-1)^(n+1) * SUM [0 <= k <= n] ( k! S(n+1,k+1) (-1 / (1-z) )^(k+1) )
		// (-1)^(n+1) * SUM [0 <= k <= n] ( k! S(n+1,k+1) ( 1 / (z-1) )^(k+1) )
		// using algebra to eliminate -1 in fraction numerator (term negation)
		for (int k = 0; k <= n; k++) sum = sumOf (sum, termStirling (z, n, k));
		return negWhenOdd (sum, n);
	}
	static ComplexValue <Double> termStirling
			(ComplexValue <Double> z, int n, int k)
	{
		ComplexValue <Double>
			SNKF = RE (SN (n+1, k+1) * F (k)),								// S{n+1,k+1} * k!
			exp = POW (reduce (z, ONE), -(k+1));							// ---------------
		return productOf (SNKF, exp);										//  (z-1) ^ (k+1)
	}
	public static ComplexValue <Double>
		I_PI = IM (Math.PI), Z = S (0), ONE = S (1), TWO = S (2);
	public static final double PI_SQ = Math.pow (Math.PI, 2);


}

