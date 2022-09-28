
package net.myorb.math.computational;

import net.myorb.math.GeneratingFunctions;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.splines.ChebyshevNodes;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.Vector;

import net.myorb.math.linalg.Solution;

import net.myorb.math.Function;

import java.io.File;

/**
 * Lower-Upper Decomposition for VanCheNodes-22 algorithm (Vandermonde-Chebychev Nodes)
 * @author Michael Druckman
 */
public class VCN22LUD extends VCSupport implements Solution
{


	public static final double
		SPLINE_LO = ChebyshevNodes.SPLINE_LO, SPLINE_HI = ChebyshevNodes.SPLINE_HI;
	public static final double SPLINE_RANGE = ChebyshevNodes.SPLINE_RANGE;


	/**
	 * the CHEBYSHEV POINTS as defined in approximation theory
	 */
	public static final double POINTS [] = ChebyshevNodes.CHEBYSHEV_POINTS;


	public VCN22LUD
		(
			Parameterization configuration
		)
	{
		this.configuration = configuration;
		if (L == null) loadVC ();
	}
	protected Parameterization configuration;


	/**
	 * scale domain to CHEBYSHEV_POINTS
	 * @param lo the lo end of the spline range
	 * @param hi the hi end of the spline range
	 * @return the domain scaled to nodes
	 */
	double [] chebyshevDomain (Double lo, Double hi)
	{
		double range = hi - lo;
		double halfRange = range / 2;
		double zeroPoint = lo + halfRange;

		double [] domain = new double [POINTS.length];

		for (int i = 0; i < domain.length; i++)
		{
			domain [i] = zeroPoint + halfRange * POINTS[i];
		}

		return domain;
	}


	/**
	 * @param f function for regression
	 * @param lo the lo end of the spline range
	 * @param hi the hi end of the spline range
	 * @return Chebyshev-T coefficients for the spline
	 */
	public GeneratingFunctions.Coefficients <Double> spline
		(
			Function <Double> f, Double lo, Double hi
		)
	{
		double[] domain = chebyshevDomain (lo, hi);
		Vector <Double> b = new Vector <Double> (domain.length, mgr);

		int i = 1;
		for (double x : domain)
		{ b.set (i++, f.eval (x)); }

		return solve (b);
	}


	/**
	 * @param b the vector to solve for
	 * @return the solution vector computed
	 */
	public GeneratingFunctions.Coefficients<Double> solve (Vector<Double> b)
	{
		return bundle (tri.luXb (L, U, b, P));
	}


	/**
	 * load VanChe matrix data
	 */
	public static void loadVC ()
	{
		loadOps ();
		L = dio.read (new File ("data/VCN22L.TDF"));
		P = dio.read (new File ("data/VCN22P.TDF")).getCol (1);
		U = dio.read (new File ("data/VCN22U.TDF"));
	}
	public static Matrix <Double> L = null, U = null;
	public static Vector <Double> P = null;


}

