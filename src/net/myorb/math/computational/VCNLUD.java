
package net.myorb.math.computational;

import net.myorb.math.linalg.Solution;

import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.Vector;

import net.myorb.data.abstractions.DataSequence;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.Function;

import java.util.HashMap;

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
		this.loadVC (N);
		this.points = this.configuredSolution.computedPoints;
		this.configuration = configuration;
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
		return bundle (configuredSolution.solve (b));
	}


	/**
	 * load VanChe matrix data
	 * - the Chebyshev points for order N also made available
	 * - the configuredSolution property is the output of this load
	 * - load may be a misnomer since the SolutionData is a cache
	 * - solution data is only loaded once per use of N value
	 * @param N order of polynomial being built
	 */
	public void loadVC (int N)
	{
		loadOps ();

		// solution data
		String name = "VCN" + N;
		this.configuredSolution = loaded.get (name);
		if (this.configuredSolution != null) return;

		//		load and cache solution data
		//  (forced to single load by hash cache)

		loaded.put
		(
			name,
			this.configuredSolution = new SolutionData
			( name, this.computePoints (N) )
		);
	}
	protected SolutionData configuredSolution = null;


	/**
	 * force single load of each solution set required
	 */
	private static class SolutionData
	{

		SolutionData (String name, double computedPoints [])
		{		
			// System.out.println ("loading " + name);
			L = dio.read (new File ("data/" + name + "L.TDF"));
			P = dio.read (new File ("data/" + name + "P.TDF")).getCol (1);
			U = dio.read (new File ("data/" + name + "U.TDF"));

			// points correlate with N as does the LUD
			this.computedPoints = computedPoints;
		}
		public double computedPoints [];

		/**
		 * @param b the vector to solve for
		 * @return the coefficients calculated
		 */
		public Vector <Double> solve (Vector <Double> b)
		{
			return tri.luXb (L, U, b, P);
		}
		public Matrix <Double> L = null, U = null;
		public Vector <Double> P = null;

	}

	/**
	 * a hash of solutions mapped from the VCN name assigned
	 */
	static HashMap < String, SolutionData > loaded = new HashMap <> ();


}

