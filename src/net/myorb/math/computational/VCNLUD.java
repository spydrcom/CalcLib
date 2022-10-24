
package net.myorb.math.computational;

import net.myorb.math.linalg.Solution;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.Vector;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.Function;

import java.io.File;

/**
 * Lower-Upper Decomposition for VanCheNodes algorithms (Vandermonde-Chebychev Nodes)
 * @author Michael Druckman
 */
public class VCNLUD extends VCSupport implements Solution
{


	public VCNLUD
		(
			int N,
			Parameterization configuration
		)
	{
		this.configuration = configuration;
		this.points = this.computePoints (N);
		this.loadVC (N);
	}
	protected Parameterization configuration;
	protected double points [];
	
	
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
	
		double [] domain = new double [points.length];
	
		for (int i = 0; i < domain.length; i++)
		{
			domain [i] = zeroPoint + halfRange * points[i];
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
	public void loadVC (int N)
	{
		loadOps ();
		String name = "VCN" + N;
		L = dio.read (new File ("data/" + name + "L.TDF"));
		P = dio.read (new File ("data/" + name + "P.TDF")).getCol (1);
		U = dio.read (new File ("data/" + name + "U.TDF"));
	}
	protected Matrix <Double> L = null, U = null;
	protected Vector <Double> P = null;


}

