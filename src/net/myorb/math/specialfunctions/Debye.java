
package net.myorb.math.specialfunctions;

import net.myorb.math.computational.ADSplineRealSegmentManager;
import net.myorb.math.Function;

/**
 * general support for Debye Functions using Anti-Derivative splines
 * @author Michael Druckman
 */
public class Debye
{

	/**
	 * describe the Anti Derivative as segments covering portions of the domain
	 */
	public static class Segments extends ADSplineRealSegmentManager
	{

		public static final double[] UP_TO = new double[]{0.1, 1, 10};

		public Segments (double[] area, int N)
		{ super (UP_TO, area); this.N = N; }
		protected int N;

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.ADSplineRealSegmentManager#getFirstSegmentBase()
		 */
		public Double getFirstSegmentBase () { return 0.0; }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.ADSplineRealSegmentManager#eval(java.lang.Double)
		 */
		public Double eval (Double x)  { return Math.pow (x, N) / (Math.exp (x) - 1); }
		
	}


	/**
	 * spline for D1
	 */
	public static class D1Segments extends Segments
	{
		public static final double[]
		AREA = new double[]{0.09752777, 0.67997685911, 0.86693};
		D1Segments () { super (AREA, 1); }
	}

	public static double D1 (double x)
	{
		if (splineD1 == null)
		{ splineD1 = new D1Segments ().newSplineInstance (); }
		return splineD1.eval (x);
	}
	static Function<Double> splineD1 = null;


	/**
	 * spline for D2
	 */
	public static class D2Segments extends Segments
	{
		public static final double[]
		AREA = new double[]{0.004835416435, 0.3491038213, 2.0446356631921163};
		D2Segments () { super (AREA, 2); }
	}

	public static double D2 (double x)
	{
		if (splineD2 == null)
		{ splineD2 = new D2Segments ().newSplineInstance (); }
		return splineD2.eval (x);
	}
	static Function<Double> splineD2 = null;


	/**
	 * spline for D3
	 */
	public static class D3Segments extends Segments
	{
		public static final double[]
		AREA = new double[]{3.2099998E-4, 0.224484188, 6.2071167};
		D3Segments () { super (AREA, 3); }
	}

	public static double D3 (double x)
	{
		if (splineD3 == null)
		{ splineD3 = new D3Segments ().newSplineInstance (); }
		return splineD3.eval (x);
	}
	static Function<Double> splineD3 = null;


	/**
	 * spline for D4
	 */
	public static class D4Segments extends Segments
	{
		public static final double[]
		AREA = new double[]{2.401388715E-5, 0.1636945, 24.02047};
		D4Segments () { super (AREA, 4); }
	}

	public static double D4 (double x)
	{
		if (splineD4 == null)
		{ splineD4 = new D4Segments ().newSplineInstance (); }
		return splineD4.eval (x);
	}
	static Function<Double> splineD4 = null;


	/**
	 * spline for D5
	 */
	public static class D5Segments extends Segments
	{
		public static final double[]
		AREA = new double[]{1.91785698E-6, 0.1284181, 113.902297};
		D5Segments () { super (AREA, 5); }
	}

	public static double D5 (double x)
	{
		if (splineD5 == null)
		{ splineD5 = new D5Segments ().newSplineInstance (); }
		return splineD5.eval (x);
	}
	static Function<Double> splineD5 = null;


}


