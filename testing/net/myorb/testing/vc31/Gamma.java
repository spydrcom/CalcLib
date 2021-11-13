
package net.myorb.testing.vc31;

import net.myorb.math.vanche.Primitives;
import net.myorb.math.Function;

public class Gamma extends Primitives
{

	static double
	A073005 = 2.678938534707747633,		// GAMMA(1/3)
	A068466 = 3.625609908221908311,		// GAMMA(1/4)
	A220086 = 6.548062940247824437;		// GAMMA(1/7)

	public static void specific (Function <Double> f, String title)
	{
		System.out.println (); System.out.println (title);
		System.out.println ("gamma(4/3) = " + (f.eval (4.0/3.0) - A073005/3)); // 0.892979511
		System.out.println ("gamma(5/4) = " + (f.eval (5.0/4.0) - A068466/4)); // 0.906402477
		System.out.println ("gamma(8/7) = " + (f.eval (8.0/7.0) - A220086/7)); // 0.935437562
	}

	public static void verify (Function <Double> spline)
	{
		for (double x = 1.0; x <= 2.05; x += 0.1)
		{
			System.out.println (x + " => " + (f.eval (x) - spline.eval (x)));
		}
		Gamma.spline = spline;
	}
	static Function <Double> spline;

	public static void main (String... args)
	{ verify (splineFor (f, 0.86, 2.5)); specific (spline, S); specific (f, G); }
	public static Function <Double> f = new net.myorb.math.specialfunctions.Gamma ();
	public static String S = "Spline", G = "Original Gamma";
}
