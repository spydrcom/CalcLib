
package net.myorb.testing.linalg;

import net.myorb.math.computational.VC31LUD;
import net.myorb.math.computational.Parameterization;
import net.myorb.math.realnumbers.RealFunctionWrapper;
import net.myorb.math.Function;

public class VC31 extends VC31LUD
{

	VC31 ()
	{
		super (new Parameterization ());
	}

	static double f (double x) { return 2*Math.pow(x,3) - 3*Math.pow(x,2); }

	public static void main (String... args)
	{
		Function <Double> f = new RealFunctionWrapper ( (x) -> f(x) ).toCommonFunction ();

		System.out.println (new VC31 ().spline (f, -1.5d, 1.5d));
	}

}
