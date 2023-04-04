
package net.myorb.math.specialfunctions;

import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.polynomial.families.LegendrePolynomial;

import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;
import net.myorb.math.Function;

import java.util.*;

/**
 * realization of Legendre Q functions
 * @author Michael Druckman
 */
public class Legendre
{


	/*

			general Legendre equation reads
			
			( 1 - x^2 ) * f''(x) - 2 * x * f'(x) + [ lambda*(lambda+1) - mu^2/(1-x^2) ] f(x) = 0
			
			where the numbers lambda and mu may be complex,
			and are called the degree and order of the relevant function, respectively.
			The polynomial solutions when lambda is an integer (denoted n), and mu = 0 are the Legendre polynomials Pn; 
			and when lambda is an integer (denoted n), and mu = m is also an integer with |m| < n are the associated 
			Legendre polynomials. All other cases of lambda and mu can be discussed as one.
			
	 */


	/*
	 * Q0 and Q1 functions (used by seed generator)
	 */

	public static double Q0 (double x)
	{
		return Math.log ((1 + x) / (1 - x)) / 2;
	}

	public static double Q1 (double x)
	{
		return x * Q0 (x) - 1;
	}


	/*
	 * implementation of recurrence algorithm
	 * 
	 * Q[n+1](x) = ( (2n+1)*x*Q[n](x) - nQ[n-1](x) ) / (n+1)
	 * 
	 */

	/**
	 * seed the recurrence algorithm
	 * @param x parameter to Q[l]
	 * @return [ Q0(x), Q1(x) ]
	 */
	public static List<Double> seed (double x)
	{
		double q0 = Q0 (x);
		List<Double> q = new ArrayList<Double>();
		q.add (q0); q.add (x * q0 - 1);
		return q;
	}

	/**
	 * add next Q[l] to list
	 * @param x parameter to Q[l]
	 * @param q list of [ Q[0](x), Q[1](x), ..., Q[n](x) ], Q[n+1](x) will be added
	 */
	public static void recurrence (double x, List<Double> q)
	{
		int n = q.size () - 1;
		double qnp1 = ((2*n + 1) * x * q.get (n) - n * q.get (n - 1)) / (n + 1);
		q.add (qnp1);
	}

	/**
	 * build list of Q
	 * @param x parameter to Q[l]
	 * @param upTo highest order of Q to be calculated
	 * @return list of [ Q[0](x), Q[1](x), ..., Q[upTo](x) ]
	 */
	public static List<Double> evaluateUsingRecurrence (double x, int upTo)
	{
		List<Double> q = seed (x);
		for (int n = 2; n <= upTo; n++)
			recurrence (x, q);
		return q;
	}


	/*
	 * implementation of Bonnet's formula
	 * 
	 * Q[l](x) = (2*l-1)/l * x * Q[l-1](x) - (l-1)/l * Q[l-2](x)
	 * 
	 */

	/**
	 * compute using Bonnet
	 * @param x parameter to Q[l]
	 * @param l order of the function being evaluated
	 * @return Q[l](x)
	 */
	public static Double evaluateUsingBonnetsFormula (Double x, int l)
	{
		return l > 1
		? ( (2*l-1) * x * evaluateUsingBonnetsFormula (x, l-1) -
			(l-1) * evaluateUsingBonnetsFormula (x, l-2) ) / l
		: l == 0 ? Q0 (x) : Q1 (x);
	}


	/*
	 * implementation of Neumann's formula
	 * 
	 * Q[l](x) = 1/2 * INTEGRAL [ -1 <= y <= 1 ] ( P[l](y) / (x - y) * dy )
	 * 
	 */

	/**
	 * compute using Neumann
	 * @param x parameter to Q[l]
	 * @param l order of the function being evaluated
	 * @return Q[l](x)
	 */
	public static Double evaluateUsingNeumannsFormula (Double x, int l)
	{
		return TanhSinhQuadratureAlgorithms.Integrate
		(getFunction (l, x), -1, 1, 1E-6, null);
	}

	/**
	 * get Legendre P function
	 * @param n order of P to get
	 * @return P[n] polynomial function
	 */
	public static Polynomial.PowerFunction<Double> getP (int n)
	{
		if (P == null || P.size() < n+1)
		{ P = new LegendrePolynomial<Double> (sm).recurrenceP (n); }
		return P.get (n);
	}
	static PolynomialFamilyManager.PowerFunctionList<Double> P;

	/**
	 * build integral function
	 * @param l order of function
	 * @param x parameter to Q[l]
	 * @return { P[l](y) / (x - y) } as a function
	 */
	static Function<Double> getFunction (int l, double x)
	{
		Polynomial.PowerFunction<Double> P = getP (l);

		return new Function<Double> ()
		{
			public Double eval (Double y) { return P.eval (y) / (x - y); }
			public SpaceManager<Double> getSpaceDescription () { return sm; }
			public SpaceManager<Double> getSpaceManager () { return sm; }
		};
	}
	static SpaceManager<Double> sm = new ExpressionFloatingFieldManager ();


	/*
	 * choose algorithm depending of segment of domain
	 * 
	 * [-1,1] interval can use recurrence algorithm for efficiency
	 * 
	 * [1,INFINITY] must use Neumann's formula to avoid ln (-x)
	 * 
	 */

	/**
	 * @param x the parameter to Q[l]
	 * @param l the order of Q to be used
	 * @return Q[l](x)
	 */
	public static double evaluate (double x, int l)
	{
		if (x >= 1)
			return evaluateUsingNeumannsFormula (x, l);
		return evaluateUsingRecurrence (x, l).get (l);
	}


	/*
	 * Q function implementation entry points
	 */

	public static double Q2 (double x)
	{
//		return (3 * x * Q1 (x) - Q0 (x)) / 2;
		return evaluate (x, 2);
	}

	public static double Q3 (double x)
	{
//		return (5 * x * Q2 (x) - 2 * Q1 (x)) / 3;
		return evaluate (x, 3);
	}

	public static double Q4 (double x)
	{
//		return (7 * x * Q3 (x) - 3 * Q2 (x)) / 4;
		return evaluate (x, 4);
	}

	public static double Q5 (double x)
	{
//		return (9 * x * Q4 (x) - 4 * Q3 (x)) / 5;
		return evaluate (x, 4);
	}


}

