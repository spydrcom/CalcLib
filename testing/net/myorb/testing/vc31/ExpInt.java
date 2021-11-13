
package net.myorb.testing.vc31;

import net.myorb.math.vanche.Primitives;
import net.myorb.math.Function;

public class ExpInt extends Primitives
{

	public static double
	A099285 = -0.219383934395520273677163775460121649031047293406908207577978613;

	public static void specific (Function <Double> f, String title)
	{
		System.out.println (); System.out.println (title);
		System.out.println ("Ei(-1) = " + (f.eval (-1.0) - A099285));
	}

	public static void verify (Function <Double> spline)
	{
		for (double x = -3.1; x <= -0.05; x += 0.1)
		{
			System.out.println (x + " => " + (f.eval (x) - spline.eval (x)));
		}
		ExpInt.spline = spline;
	}
	static Function <Double> spline;

	public static void main (String... args)
	{ verify (splineFor (f, -3.1, 1)); specific (spline, S); specific (f, E); }
	public static Function <Double> f = net.myorb.math.specialfunctions.ExponentialIntegral.getEiSpline ();
	public static String S = "Spline", E = "Original Ei";

}
