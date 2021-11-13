
package net.myorb.math.specialfunctions;

import net.myorb.math.computational.ADSplineRealSegmentManager;

import net.myorb.math.Function;

/**
 * Anti-Derivative spline calculation of sinc function (Si)
 * @author Michael Druckman
 */
public class Si
{

	/**
	 * describe the Anti Derivative as segments covering portions of the domain
	 */
	public static class Segments extends ADSplineRealSegmentManager
	{

		public static final double[]
		UP_TO = new double[]{ 0.5, 1, 2, 3, 4, 5, 6, 7, 14, 28, 56 },
		AREA = new double[]
			{
				 0.4931073180430653,    0.4529756523241151,   0.6593299064355098,
				 0.24323955119677268,  -0.09044938905041502, -0.2082718940043784,
				-0.12524369366416724,   0.029909062971101352, 0.10161443582956377,
				 0.048534688212699274, -0.04900981008445089
			};

		public Segments () { super (UP_TO, AREA); }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.ADSplineRealSegmentManager#getFirstSegmentBase()
		 */
		public Double getFirstSegmentBase () { return 0.0; }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.ADSplineRealSegmentManager#eval(java.lang.Double)
		 */
		public Double eval (Double x)  { return sinc (x); }
		
	}

	/**
	 * @param x parameter value
	 * @return INTEGRAL [ 0 : x ] sin t / t dt
	 */
	public static double si (double x)
	{
		if (spline == null)
		{ spline = new Segments ().newSplineInstance (); }
		return spline.eval (x);
	}
	static Function<Double> spline = null;

	/**
	 * @param x parameter to function
	 * @return sin x / x
	 */
	public static double sinc (double x)
	{
		return Math.sin (x) / x;
	}

}
