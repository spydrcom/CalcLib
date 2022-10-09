
package net.myorb.testing;

import net.myorb.math.computational.MaxMin;
import net.myorb.math.realnumbers.RealFunctionWrapper;
import java.util.List;

public class MaxMinOf
{

	static MaxMin maxMin = new MaxMin ();

	static double REAL_PART (double t, double sigma)
	{ return Math.cos ( sigma * Math.log (t) ) * Math.sqrt (t) / COSH2SQ (t); }
	static double IMAG_PART (double t, double sigma)
	{ return Math.sin ( sigma * Math.log (t) ) * Math.sqrt (t) / COSH2SQ (t); }
	static double COSH2SQ (double x) { return SQ ( Math.cosh (x) ); }
	static double SQ (double x) { return x*x; }

	static void compute (RealFunctionWrapper f)
	{
		maxMin.setFunction (f.toCommonFunction ());
		List <Double> c = maxMin.find (0, 1, 1E-8);
		System.out.println (c);

		double loEnd = maxMin.integralOver (c);
		double hiEnd = maxMin.eval (1, 20);
		double full = loEnd+hiEnd;

		System.out.println ("0..1:  " + loEnd);
		System.out.println ("0..20:  " + full);
	}

	public static void main (String[] args)
	{
		double sigma = 14.1347;
		System.out.println ("Re: "); compute (new RealFunctionWrapper ((x) -> REAL_PART (x, sigma)));
		System.out.println ("Im: "); compute (new RealFunctionWrapper ((x) -> IMAG_PART (x, sigma)));
	}

}
