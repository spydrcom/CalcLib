
package net.myorb.math.computational;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.computational.Combinatorics;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;
import net.myorb.data.abstractions.FunctionWrapper;
import net.myorb.data.abstractions.Function;

/**
 * polylog Li functions for integer orders -4 .. 1
 * @author Michael Druckman
 */
public class PolylogFamily
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

			case  1:	return functionFor ( (z) -> Li1 (z) );
			case  0:	return functionFor ( (z) -> Li0 (z) );
			case -1:	return functionFor ( (z) -> Lin1 (z) );
			case -2:	return functionFor ( (z) -> Lin2 (z) );
			case -3:	return functionFor ( (z) -> Lin3 (z) );
			case -4:	return functionFor ( (z) -> Lin4 (z) );

			default:
				if (s > 0)
				{
					throw new RuntimeException ("Function not available");
				}
				return functionFor ( (z) -> Linn (-s, z) );
		}
	}

	/**
	 * @param z complex parameter to Li1
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li1 (ComplexValue <Double> z)
	{
		// -ln(1-z)
		return null;
	}

	/**
	 * @param z complex parameter to Li0
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Li0 (ComplexValue <Double> z)
	{
		// z / (1-z)
		return null;
	}

	/**
	 * @param z complex parameter to Li -1
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Lin1 (ComplexValue <Double> z)
	{
		// z / (1-z)^2
		return null;
	}

	/**
	 * @param z complex parameter to Li -2
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Lin2 (ComplexValue <Double> z)
	{
		// z(z+1) / (1-z)^3
		return null;
	}

	/**
	 * @param z complex parameter to Li -3
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Lin3 (ComplexValue <Double> z)
	{
		// z(z^2+4z+1) / (1-z)^4
		return null;
	}

	/**
	 * @param z complex parameter to Li -4
	 * @return complex result of function evaluation
	 */
	public static ComplexValue <Double> Lin4 (ComplexValue <Double> z)
	{
		// z(z+1)(z^2+10z+1) / (1-z)^5
		return null;
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
		double SKF = Combinatorics.stirlingNumbers (n+1, k+1) * F (k);
		return mgr.multiply (mgr.C (SKF, 0.0), exp);
	}
	static double F (double n)
	{
		double res = n;
		if (n < 2) return 1;
		for (int f = (int) n - 1; f > 1; f--) res *= f;
		return res;
	}
	public static ExpressionComplexFieldManager mgr = ComplexSpaceCore.manager;
	public static ComplexValue <Double> NONE = mgr.newScalar (-1);

}
