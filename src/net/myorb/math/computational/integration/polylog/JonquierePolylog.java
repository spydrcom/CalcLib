
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.computational.Combinatorics;

import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

import net.myorb.data.abstractions.FunctionWrapper;
import net.myorb.data.abstractions.Function;

/**
 * polylog Li functions for integer orders -4 .. 1
 * - general case for negative integer orders also available
 * @author Michael Druckman
 */
public class JonquierePolylog
{

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

	/**
	 * @param s the order for the Li function
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

	/**
	 * @param z complex parameter to Li1
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li1 (ComplexValue <Double> z)
	{
		return mgr.negate (ComplexSpaceCore.cplxLib.ln (oneMinusZ (z)));	// -ln(1-z)
	}

	/**
	 * @param z complex parameter to Li0
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li0 (ComplexValue <Double> z)
	{
		return mgr.multiply (z, mgr.invert (oneMinusZ (z)));				// z / (1-z)
	}

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
