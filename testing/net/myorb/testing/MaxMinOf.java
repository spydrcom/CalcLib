
package net.myorb.testing;

import net.myorb.math.computational.MaxMin;
import net.myorb.math.realnumbers.RealFunctionWrapper;
import java.util.List;

public class MaxMinOf
{

	static double ImagPart = 5;
//	static double ImagPart = 14.134725;
	static MaxMin maxMin = new MaxMin ();

	static double F (double t)
//	{ return Math.sin ( ImagPart * Math.log (t) ) * Math.sqrt (t) / SQ ( Math.cosh (t) ); }
	{ return Math.cos ( ImagPart * Math.log (t) ) * Math.sqrt (t) / SQ ( Math.cosh (t) ); }
	static double SQ (double x) { return x*x; }

	public static void main (String[] args)
	{
		RealFunctionWrapper f =
			new RealFunctionWrapper
			(
				(x) -> F (x)
			);
		maxMin.setFunction (f.toCommonFunction ());
		List <Double> c = maxMin.find (0, 1, 1E-8);
		System.out.println (c);

		double loEnd = maxMin.integralOver (c);
		double hiEnd = maxMin.eval (1, 20);
		double full = loEnd+hiEnd;

		System.out.println ("0..1:  " + loEnd);
		System.out.println ("0..20:  " + full);
	}

}
