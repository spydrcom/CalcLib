
package net.myorb.testing.anal;

import net.myorb.math.computational.AntiDerivativeSplineMultiDimensional;
import net.myorb.math.computational.ADSplineReal2DSegmentManager;

import java.util.List;

/**
 * unit test for Double Integral of 2 dimensional Function.
 *  taken from "Calculus Concepts and Contexts" by James Stewart.
 *  Section 12.1, Double Integrals Over Rectangles, page 843.
 * @author Michael Druckman
 */
public class TwoDimSpline extends ADSplineReal2DSegmentManager
{


	/*
	 * as specified in source text:
	 * f(x) = 16 - x^2 - 2 * y^2, exact value should be 48
	 * 41.5 with 4 segs, 44.875 with 8, 46.46875 with 16
	 * 0 <= x <= 2 & 0 <= y <= 2
	 */


	/*
	 * precision driven by selection of DELTA value
	 */

	static final double
	LOWER_PRECISION_DELTA = 1E-4,								// for test demonstrating precision VS time
	HIGHER_PRECISION_DELTA = 1E-6,								// for test demonstrating precision VS time (longer version)
	PLOT_DELTA = 1E-2;											// for contour plot, see script sourceADSplineTwoD.txt

	/*
	 * descriptions of segments
	 */

	static final double
	X_HI[] = new double[]{0.5, 1, 1.5, 2, 2.5},
	Y_HI[] = new double[]{0.5, 1, 1.5, 2, 2.5};

	static final double
	LO[] = new double[]{0, 0},
	HI[][] = new double[][]{X_HI, Y_HI};


	/**
	 * pass segment descriptions to super-class(es)
	 */
	//public TwoDimSpline () { super (LO, HI, LOWER_PRECISION_DELTA); }
	//public TwoDimSpline () { super (LO, HI, HIGHER_PRECISION_DELTA); }
	public TwoDimSpline () { super (LO, HI); useSpecificPrecision (4); }

	/**
	 * change to plot delta
	 * @param precision relative value for precision (1-10)
	 */
	public void useSpecificPrecision (int precision)
	{
		integral.setRequestedPrecision (precision);
	}

	/**
	 * change to plot delta
	 */
	public void useReducedPrecision ()
	{
		integral.setDeltas (usingSquareRegions (PLOT_DELTA));
	}


	/*
	 * segment table generation and use
	 */

	/**
	 * integral by segment
	 * @param segX the x segment index 
	 * @param segY the y segment index
	 * @return INTEGRAL value for (segX, segY) region
	 */
	public Double getContributionFrom (int segX, int segY)
	{
		if (segX < 0 || segY < 0) return 0.0;
		double xLo = segX==0? 0: X_HI[segX-1], yLo = segY==0? 0: Y_HI[segY-1];
		return computeIntegralApproximation (xLo, X_HI[segX], yLo, Y_HI[segY]);
	}

	/**
	 * segment contribution matrix value calculations
	 */
	public void computeContributions ()
	{
		for (int x = 0; x < X_HI.length; x++)
		{
			for (int y = 0; y < Y_HI.length; y++)
			{
				System.out.print (x); System.out.print ("\t");
				System.out.print (y); System.out.print ("\t");
				System.out.print (getContributionFrom (x, y));
				System.out.println ();
			}
		}
	}

	/*
	 * output from computeContributions ()
	 * 
		0	0	3.9375000006256093
		0	1	3.6875000006254326
		0	2	3.1875000006250507
		0	3	2.437500000625239
		0	4	1.4375000006237892
		1	0	3.8125000006246728
		1	1	3.5625000006253575
		1	2	3.062500000624939
		1	3	2.3125000006250045
		1	4	1.312500000623899
		2	0	3.5625000006253305
		2	1	3.312500000625378
		2	2	2.8125000006241625
		2	3	2.0625000006248735
		2	4	1.062500000623977
		3	0	3.1875000006245067
		3	1	2.9375000006260996
		3	2	2.437500000625324
		3	3	1.6875000006249528
		3	4	0.6875000006238066
		4	0	2.687500000623861
		4	1	2.437500000624964
		4	2	1.9375000006245837
		4	3	1.1875000006242884
		4	4	0.1875000006232426
	 */

	static final double
	X0[] = new double[]{3.9375, 3.6875, 3.1875, 2.4375, 1.4375},
	X1[] = new double[]{3.8125, 3.5625, 3.0625, 2.3125, 1.3125},
	X2[] = new double[]{3.5625, 3.3125, 2.8125, 2.0625, 1.0625},
	X3[] = new double[]{3.1875, 2.9375, 2.4375, 1.6875, 0.6875},
	X4[] = new double[]{2.6875, 2.4375, 1.9375, 1.1875, 0.1875};
	static final double SEGMENT_CONTRIBUTIONS[][] = new double[][]{X0, X1, X2, X3, X4};

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ADSplineRealDimensionalSegmentManager#getContributionFrom(int[])
	 */
	public Double getContributionFrom (List<Integer> segmentAt)
	{
		int
			segX = segmentAt.get (0),
			segY = segmentAt.get (1);
		if (segX < 0 || segY < 0) return 0.0;
		return SEGMENT_CONTRIBUTIONS[segX][segY];
	}


	/*
	 * calculation of function values
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ADSplineReal2DSegmentManager#integrand(double, double)
	 */
	public double integrand (double x, double y)
	{
		return 16 - x*x - 2 * y*y;
	}

	/**
	 * @param x parameter value
	 * @param y parameter value
	 * @return computed result
	 */
	public static double twod (double x, double y)
	{
		if (spline == null)
		{
			TwoDimSpline descriptor = new TwoDimSpline ();
			spline = descriptor.newSplineInstance ();
			//descriptor.useSpecificPrecision (1);
			descriptor.useReducedPrecision ();
		}
		// INTEGRALD [ 0 <= u <= x <> DELTA ] [ 0 <= v <= y <> DELTA ] ( ( 16 - u^2 - 2 * v^2 ) * dv * du )
		return spline.f (x, y);
	}
	static AntiDerivativeSplineMultiDimensional<Double> spline = null;


	/*
	 * entry point and output formatting
	 */

//	public static void main (String[] args)
//	{
//		System.out.print (new TwoDimSpline ().computeIntegralApproximation (0, 2.1, 0, 2));
//	}

	/**
	 * entry point for unit test
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		System.out.println ();
		long stamp = System.currentTimeMillis ();
		test ("Unit test as specified in text", 2.0, 2.0);
		test ("Boundary test cases - small additional", 2.1, 2.2);
		test ("Boundary test case - NO segments", 0.4, 1.6);
		test ("ALL segments + both extras", 2.7, 2.8);
		test ("Single segment + extra", 0.8, 1.6);
		test ("Multi segment + extra", 1.1, 1.6);
		System.out.print (duration (stamp));
		System.out.println ("ms");
		System.out.println ();
	}
	static void testComputeContributions ()
	{
		new TwoDimSpline ().computeContributions ();
	}
	static void test (String title, double lo, double hi)
	{
		double clc;
		System.out.println (title);
		long stamp = System.currentTimeMillis ();
		System.out.print ("Full calculation of double integral: ");
		System.out.print (clc = new TwoDimSpline ().computeIntegralApproximation (0, lo, 0, hi));
		splinePart (clc, done (stamp), lo, hi);
	}
	static void splinePart (double clc, long t, double lo, double hi)
	{
		double spl;
		long stamp = System.currentTimeMillis ();
		System.out.print ("Spline calculation of double integral: ");
		System.out.print (spl = new TwoDimSpline ().newSplineInstance ().f (lo, hi));
		dif (spl, clc); done (stamp, percent (duration (stamp), t));
	}
	static void dif (double s, double c)
	{
		System.out.println ();
		System.out.print ("dif = ");
		System.out.print (Math.abs (s - c));
	}
	static long done (long started) { return done (started, "", false); }
	static long done (long started, String percent) { return done (started, percent, true); }
	static long done (long started, String percent, boolean terminate)
	{
		long d;
		System.out.print (", duration = ");
		System.out.print (d = duration (started)); System.out.print (percent);
		if (terminate) { System.out.println (); System.out.println ("==="); }
		System.out.println ();
		return d;
	}
	static String percent (long portion, long of)
	{
		int value = (int) (100 * (float) portion / (float) of);
		return " (" + value + "%)";
	}
	static long duration (long since)
	{
		return System.currentTimeMillis () - since;
	}

}
