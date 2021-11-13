
package net.myorb.testing;

import net.myorb.math.*;
import net.myorb.math.computational.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import java.util.List;

public class AsinSeries
{

	static double
	rad2 = 1.41421356237309504880168872,
	rad3 = 1.732050807568877293527446341505,
	rad6 = rad2 * rad3, r6m2 = rad6 - rad2, r6p2 = rad6 + rad2,
	 PI  = 3.14159265358979323;

	static double
	sinx[]	 = new double[]{r6m2/4, 0.5000, rad2/2, rad3/2},
	cosx[]	 = new double[]{r6p2/4, rad3/2, rad2/2, 0.5000},
	angles[] = new double[]{15.000, 30.000, 45.000, 60.000};


	protected static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	protected static OptimizedMathLibrary<Double> lib = new OptimizedMathLibrary<Double> (mgr);
	protected static HighSpeedMathLibrary mlib = new HighSpeedMathLibrary ();

	protected static PolynomialRoots<Double> roots = new PolynomialRoots<Double> (mgr, mlib);
	protected static TaylorPolynomials<Double> taylor = new TaylorPolynomials<Double> (mgr);


	/**
	 * taken from sin(a+b) = sin(a)cos(b) + cos(a)sin(b)
	 * @param sinAlphaPlusBeta
	 * @param sinBeta
	 * @param cosBeta
	 * @return
	 */
	static List<Double> sinAlpha
	(double sinAlphaPlusBeta, double sinBeta, double cosBeta)
	{
		double
		a = 1.0,
		b = - 2 * sinAlphaPlusBeta * cosBeta,
		c = sinAlphaPlusBeta*sinAlphaPlusBeta - sinBeta*sinBeta;
		return roots.quadratic (a, b, c);
	}

	static public Double asin (Double x)
	{
		if (x == 0) return 0.0;
		double angleDegrees = 0, angleRadians = 0, sinI;
		for (int i = angles.length - 1; i >= 0; i--)
		{
			if (x > (sinI = sinx[i]))
			{
				x = sinAlpha (x, sinI, cosx[i]).get (0);
				angleDegrees += angles[i];
			}
		}
		angleRadians = taylor.asin (x);
		return angleRadians + angleDegrees*PI/180;
	}

	public static void main(String[] args)
	{
		System.out.println (sinAlpha (rad2/2, (rad6-rad2)/4, (rad6+rad2)/4));
		double inc = PI/50, sum = 0, count = 0;

		for (double x = PI/2-inc; x >= 0; x -= inc)
		{
			double s = lib.sin (x), a = asin (s), e = lib.abs (a - x);
			System.out.println ("x=" + x + " s=" + s + " a=" + a + " e=" + e);
			sum += e; count++;
		}
		System.out.println (sum/count);
	}

}
