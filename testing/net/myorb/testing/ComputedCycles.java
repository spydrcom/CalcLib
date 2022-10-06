
package net.myorb.testing;

import net.myorb.math.computational.MaxMin;
import net.myorb.math.realnumbers.RealFunctionWrapper;

import java.util.ArrayList;
import java.util.List;

public class ComputedCycles
{

//	!!  d (t, k)  =  t *  ( exp (pi/k) - 1 )

//	static double ImagPart = 5;
//	static double ImagPart = 14.134725;
	static MaxMin maxMin = new MaxMin ();

	static double REAL_PART (double t, double sigma)
	{ return Math.cos ( sigma * Math.log (t) ) * Math.sqrt (t) / COSH2SQ (t); }
	static double IMAG_PART (double t, double sigma)
	{ return Math.sin ( sigma * Math.log (t) ) * Math.sqrt (t) / COSH2SQ (t); }
	static double COSH2SQ (double x) { return SQ ( Math.cosh (x) ); }
	static double SQ (double x) { return x*x; }

	static double step (double t, double k) { return t * ( Math.exp (Math.PI / k) - 1 ); }

	static double compute (RealFunctionWrapper f, double sigma)
	{
		maxMin.setFunction (f.toCommonFunction ());
		List <Double> domain = new ArrayList <Double> ();
		
		double startingAt = 1E-10;
		
		for (double x = startingAt; x <= 1; x += step (x, sigma))
		{
			domain.add (x);
		}

		domain.add (10d);
		System.out.println (domain);
		double I = maxMin.integralOver (domain);
		System.out.println (I);
		return I;
	}

	public static void main (String[] args)
	{
		double sigma = 12, Re, Im;
		System.out.println ("Re: "); Re = compute (new RealFunctionWrapper ((x) -> REAL_PART (x, sigma)), sigma);
		System.out.println ("Im: "); Im = compute (new RealFunctionWrapper ((x) -> IMAG_PART (x, sigma)), sigma);
		System.out.println ("( " + Re + " + i * " + Im + " )");
	}

}
