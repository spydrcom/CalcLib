
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.computational.Combinatorics;

import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

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
public class JonquierePolylog
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
			expTmZ = mgr.add (mgr.C (Math.exp (t), 0.0), mgr.negate (z)),
			ztToSm1 = mgr.multiply (z, mgr.pow (mgr.C (t, 0.0), s-1));
		return mgr.multiply (ztToSm1, mgr.invert (expTmZ));
	}

	/**
	 * derivative of dilogarithm
	 * @param t parameter to function
	 * @return computed value
	 */
	public static ComplexValue <Double> Li2Prime (ComplexValue <Double> t)
	{
		if (mgr.isZero (t)) return mgr.getOne ();
		ComplexValue <Double> oneMz = oneMinusZ (t);
		if (mgr.isZero (oneMz)) return mgr.getZero ();
		if (mgr.isNegative (oneMz)) oneMz = mgr.negate (oneMz);
		ComplexValue <Double> lnt = ComplexSpaceCore.cplxLib.ln (oneMz);
		return mgr.multiply (lnt, mgr.invert (t));
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
			f, ComplexSpaceCore.manager
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
		return mgr.add (mgr.getOne (), mgr.negate (z));
	}

	/**
	 * @param z the parameter to the function
	 * @param power the exponent of the function
	 * @return ( 1 - z ) ^ power
	 */
	public static ComplexValue <Double> oneMinusZto (ComplexValue <Double> z, int power)
	{
		return ComplexSpaceCore.cplxLib.pow (oneMinusZ (z), power);
	}

	/**
	 * evaluation of a polynomial
	 * @param z the parameter to the polynomial
	 * @param c the coefficients of the polynomial
	 * @return the evaluation of the polynomial
	 */
	public static ComplexValue <Double> poly (ComplexValue <Double> z, int [] c)
	{
		ComplexValue <Double> sum = mgr.newScalar ( c [0] );
		for (int i = 1; i < c.length; i++)
		{
			ComplexValue <Double> scalar = mgr.newScalar ( c [i] );
			sum = mgr.add (mgr.multiply (z, sum), scalar);
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
		return mgr.multiply (polyValue, mgr.invert (oneMinusZto (z, power)));
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
		double zRe = Math.abs (z.Re ());
		if (mgr.isZero (z)) return mgr.getZero ();
		if ( zRe < 1 ) return Lipn  (2, terms, z, false);					// SIGMA [1..N] (z^k/k^2)
		if (z.Re () < -1) return continuation (2, z, terms);				// use negative continuation
		if ( zRe > 1 || z.Im () != 0 ) return continuation (z, terms);		// use positive continuation
		if (z.Re () < 0) return mgr.C ( - PI_SQ / 12, 0.0 );				// - PI^2 / 12
		else return mgr.C ( PI_SQ / 6, 0.0 );								//   PI^2 / 6
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
			PI_SQ_over3 = mgr.C (PI_SQ/3, 0.0),								// PI^2 / 3
			lnz = ComplexSpaceCore.cplxLib.ln (z),							// ln z
			termSum = mgr.C (0.5, 0.0), ipi = mgr.C (0.0, Math.PI);
		termSum = mgr.add (mgr.multiply (termSum, lnz), ipi);				// 1/2 * ln z + i * PI
		termSum = mgr.add
			(
				mgr.multiply (termSum, lnz),								// 1/2 * (ln z)^2 + i * PI * ln z 
				Lipn (2, terms, z, true)									// + SIGMA ...
			);
		return mgr.add (PI_SQ_over3, mgr.negate (termSum));
	}
	static final double PI_SQ = Math.pow (Math.PI, 2);


	/*
	 * Li for simple order 0 and 1
	 */

	/**
	 * @param z complex parameter to Li0
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li0 (ComplexValue <Double> z)
	{
		return mgr.multiply (z, mgr.invert (oneMinusZ (z)));				// z / (1-z)
	}

	/**
	 * @param z complex parameter to Li1
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li1 (ComplexValue <Double> z)
	{
		return mgr.negate (ComplexSpaceCore.cplxLib.ln (oneMinusZ (z)));	// -ln(1-z)
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
	public static ComplexValue <Double> Li3 (ComplexValue <Double> z, int terms)
	{
		return Lipn (3, terms, z, false);									// SIGMA [1..N] (z^k/k^3)
	}

	/**
	 * Taylor series for abs(Re(z)) LT 1
	 * @param z complex parameter to Li4
	 * @param terms number of terms in series
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li4 (ComplexValue <Double> z, int terms)
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
			mgr.multiply (poly (z, C110), poly (z, C1A1));
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
		ComplexValue <Double> continued = Lipn (s, terms, z, true);
		continued = s % 2 == 1 ? continued : mgr.negate (continued);
		continued = mgr.add (continued, continuationOffset (s, z));
		return continued;
	}
	public static ComplexValue <Double> continuationOffset
			(int s, ComplexValue <Double> z)
	{
		ComplexValue <Double>
			twoPiI = mgr.C (0.0, 2 * Math.PI),							// 2 * pi * i
			lnz = ComplexSpaceCore.cplxLib.ln (z),						// ln z
			lnz2piI = mgr.multiply (lnz, mgr.invert (twoPiI)),			// ln z / ( 2 * pi * i )
			BP = Combinatorics.BernoulliPolynomial (lnz2piI, s, mgr),	// bernpoly ( ... )
			sF = mgr.invert (mgr.C (Combinatorics.F (s), 0.0)),			// 1 / s !
			offset = mgr.multiply (mgr.pow (twoPiI, s), BP);			// (2 * pi * i) ^ s * bernpoly

		offset = mgr.negate (mgr.multiply (offset, sF));				// - (2 * pi * i) ^ s / s! * bernpoly
		if (z.Im () == 0.0) return mgr.C (offset.Re (), 0.0);			// Re (offset)

	/*
	    twopij = i * 2 * pi
	    a = -twopij**n/fac(n) * bernpoly (n, ln(z)/twopij)
	    if _is_real_type(z) and z < 0: a = _re(a)
	    if _im(z) < 0 or (_im(z) == 0 and _re(z) >= 1):
	        a -= twopij*ln(z)**(n-1)/fac(n-1)
	    return a
	 */

		ComplexValue <Double>
			lnZ2sM1 = mgr.pow (lnz, s-1),								// s * ( ln z )^(s - 1)
			lnZsF = mgr.multiply (lnZ2sM1, sF),							// ( ln z )^(s - 1) / (s - 1)!
			lnZsF2piI = mgr.multiply (lnZsF, twoPiI)					// 2 * pi * i * ( ln z )^(s - 1) / (s - 1)!
		;
		return mgr.add (offset, mgr.negate (lnZsF2piI));
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
		ComplexValue <Double> sum = mgr.getZero ();
		// Li(z) = SIGMA [1 <= k <= INFINITY] (   z^k  / k^(-n) )			// for |Re(z)| < 1
		// Li(z) = SIGMA [1 <= k <= INFINITY] ( z^(-k) * k^(-n) )			// for |Re(z)| > 1
		for (int k = 1; k <= terms; k++)
		{
			sum = mgr.add
				(
					sum,
					mgr.multiply
					(
						mgr.pow (mgr.newScalar (k), -n),
						mgr.pow (z, isContinuation ? -k : k)
					)
				);
		}
		return sum;
	}

	/**
	 * general negative case
	 * @param n the integer order
	 * @param z complex parameter to Li
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Linn (int n, ComplexValue <Double> z)
	{
		ComplexValue <Double>
			sgn = mgr.newScalar ((int) Math.pow (-1, n+1)), sum = mgr.getZero ();
		// (-1)^(n+1) * SUM [0 <= k <= n] ( k! S(n+1,k+1) (-1 / (1-z) )^(k+1) )
		for (int k = 0; k <= n; k++) sum = mgr.add (sum, term (z, n, k));
		return mgr.multiply (sgn, sum);
	}
	static ComplexValue <Double> term (ComplexValue <Double> z, int n, int k)
	{
		ComplexValue <Double> frac = mgr.invert (mgr.add (z, NONE));
		ComplexValue <Double> exp = ComplexSpaceCore.cplxLib.pow (frac, k+1);
		double SKF = Combinatorics.stirlingNumbers2HW (n+1, k+1) * Combinatorics.F (k);
		return mgr.multiply (mgr.C (SKF, 0.0), exp);
	}
	public static ExpressionComplexFieldManager mgr = ComplexSpaceCore.manager;
	public static ComplexValue <Double> NONE = mgr.newScalar (-1);


}

