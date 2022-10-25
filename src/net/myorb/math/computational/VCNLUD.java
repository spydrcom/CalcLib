
package net.myorb.math.computational;

import net.myorb.math.linalg.Solution;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.Vector;

import net.myorb.data.abstractions.DataSequence;

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
			Parameterization configuration
		)
	{
		this (getOrder (configuration), configuration);
	}

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


	/**
	 * @return the points for the configured order
	 */
	public double [] getChebyshevPoints ()
	{
		return this.points;
	}
	protected double points [];


	/**
	 * scale domain to CHEBYSHEV_POINTS
	 * @param lo the lo end of the spline range
	 * @param hi the hi end of the spline range
	 * @return the domain scaled to nodes
	 */
	public DataSequence <Double>
		getSplineDomainFor (double lo, double hi)
	{
		return getSplineDomainFor (lo, hi, points);
	}


	/**
	 * identify values between Chebyshev points
	 * @param lo the lo end of the spline range
	 * @param hi the hi end of the spline range
	 * @return points to use for comb tests
	 */
	public DataSequence <Double>
		getCombDomainFor (double lo, double hi)
	{
		return getCombDomainFor (lo, hi, points);
	}


	/**
	 * construct solution vector
	 * - LUxb solution computed for f
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
		DataSequence <Double> domain =
				getSplineDomainFor (lo, hi, points);
		Vector <Double> b = new Vector <Double> (domain.size (), mgr);
		int i = 1; for (double x : domain) { b.set (i++, f.eval (x)); }
		return solve (b);
	}
	
	
	/**
	 * LUxb algorithm
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
		System.out.println ("loading " + name);
		L = dio.read (new File ("data/" + name + "L.TDF"));
		P = dio.read (new File ("data/" + name + "P.TDF")).getCol (1);
		U = dio.read (new File ("data/" + name + "U.TDF"));
	}
	protected Matrix <Double> L = null, U = null;
	protected Vector <Double> P = null;


}

