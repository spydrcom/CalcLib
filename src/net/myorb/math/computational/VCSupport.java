
package net.myorb.math.computational;

import net.myorb.math.matrices.Vector;
import net.myorb.math.matrices.Triangular;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.matrices.VectorOperations;
import net.myorb.math.matrices.MatrixOperations;

import net.myorb.data.abstractions.DataSequence;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.expressions.DataIO;

/**
 * LUD support for VanChe based algorithms (Vandermonde-Chebychev)
 * @author Michael Druckman
 */
public class VCSupport
{


	/**
	 * the CHEBYSHEV POINTS as defined in approximation theory
	 */


	/**
	 * compute the Chebyshev points for a given order
	 * @param points an array to be filled with points for order N
	 * @param N the order of the polynomial to be used for approximations
	 */
	public static void fillPointsArray (double [] points, int N)
	{
		double d = Math.PI / N;
		for (int i = 0; i <= N; i++)
		{ points [i] = - Math.cos ( i * d ); }
	}


	/**
	 * establish the list of Chebyshev points given the order
	 * @param N the order of the polynomial
	 * @return the list of points for N
	 */
	public double [] computePoints (int N)
	{
		double [] points =
			new double [ N + 1 ];
		fillPointsArray (points, N);
		if (useSymmetric) forceSymmetry (points);
		return points;
	}
	static boolean useSymmetric = true;


	/**
	 * establish the list of symmetric Chebyshev points given the order
	 * - the list is forced symmetric around 0 by copy of the mirrored negative value
	 * - N should be even making array odd length so the central value is 0
	 * @param N the order of the polynomial
	 * @return the list of points for N
	 */
	public double [] computeSymmetricPoints (int N)
	{
		double [] symmetricPoints =
			new double [ N + 1 ];
		fillPointsArray (symmetricPoints, N);
		forceSymmetry (symmetricPoints);
		return symmetricPoints;
	}


	/**
	 * @param points an array of points needing adjustment
	 */
	public static void forceSymmetry (double [] points)
	{
		int lo, hi = points.length-1;
		for (lo = 0; lo < hi; lo++, hi--)
		{ points [hi] = - points [lo]; }
		if (lo == hi) points [lo] = 0;
	}


	/**
	 * package the solution vector as polynomial coefficients
	 * @param solution the solution vector
	 * @return the coefficients list
	 */
	public static GeneratingFunctions.Coefficients <Double>
			bundle (Vector <Double> solution)
	{
		GeneratingFunctions.Coefficients<Double> c =
				new GeneratingFunctions.Coefficients <Double> ();
		solution.addToList (c);
		return c;
	}


	/**
	 * construct matrix operations objects
	 */
	public static void loadOps ()
	{
		if (mgr != null) return;
		mgr = new ExpressionFloatingFieldManager ();
		ops = new MatrixOperations <Double> (mgr);
		vec = new VectorOperations <Double> (mgr);
		tri = ops.getTriangularOperations ();
		dio = new DataIO <Double> (mgr);
	}
	public static ExpressionFloatingFieldManager mgr = null;
	public static MatrixOperations <Double> ops;
	public static VectorOperations <Double> vec;
	public static Triangular <Double> tri;
	public static DataIO <Double> dio;


	/*
	 * spline domains given points
	 */


	/**
	 * @param lo the lo end of the domain
	 * @param hi the hi end of the domain
	 * @param mul the multipliers for the points
	 * @return the sequence of points in the domain
	 */
	public static DataSequence <Double> compute (double lo, double hi, double [] mul)
	{
		DataSequence <Double> domain = new DataSequence <> ();
		double range = hi - lo, halfRange = range / 2, mid = lo + halfRange;
		for (int i = 0; i < mul.length; i++) { domain.add ( mid + halfRange * mul [i] ); }
		return domain;
	}


	/**
	 * apply points to the range of the domain
	 * @param lo the low end of the span of the domain
	 * @param hi the high end of the span of the domain
	 * @param points the points computed for the order
	 * @return a data sequence holding domain values
	 */
	public static DataSequence <Double>
		getSplineDomainFor (double lo, double hi, double [] points)
	{ return compute (lo, hi, points); }


	/**
	 * select points between the Chebyshev points
	 * - these are the test points for the regression
	 * - these are added to the points used to build the model
	 * @param points the points computed for the order
	 * @return the list of multipliers
	 */
	public static double [] getCombMultipliers (double [] points)
	{
		double last = points[0];
		double [] comb = new double [ points.length - 1 ];
		for (int i = 1; i < points.length; i++)
		{
			double next = points [i];
			comb [ i - 1 ] = (last + next) / 2;
			last = next;
		}
		return comb;
	}


	/**
	 * compute comb points for the specified range
	 * @param lo the low end of the span of the domain
	 * @param hi the high end of the span of the domain
	 * @param points the points computed for the order
	 * @return a data sequence holding domain values
	 */
	public static DataSequence <Double>
		getCombDomainFor (double lo, double hi, double [] points)
	{ return compute (lo, hi, getCombMultipliers (points)); }


	/**
	 * parameter N is order to use with default 22
	 * @param configuration parameter map to use in session
	 * @return the order value for the session
	 */
	public static int getOrder (Parameterization configuration)
	{
		return configuration.getValue ("N", 22).intValue ();
	}


}

