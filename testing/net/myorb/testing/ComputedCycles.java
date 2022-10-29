
package net.myorb.testing;

import net.myorb.math.computational.integration.polylog.*;

import java.util.List;

public class ComputedCycles extends CyclicQuadrature
{


//	!!  d (t, k)  =  t *  ( exp (pi/k) - 1 )


	static double mu (double t)
	{ return Math.sqrt (t) / COSH2SQ (t); }
	static double COSH2SQ (double x) { return SQ ( Math.cosh (x) ); }
	static double SQ (double x) { return x*x; }

	static double REAL_PART (double t, double sigma)
	{ return Math.cos ( kLnT (sigma, t) ) * mu (t); }
	static double IMAG_PART (double t, double sigma)
	{ return Math.sin ( kLnT (sigma, t) ) * mu (t); }


/*
	-7.211501870593717E-7
	( 5.796075033467907E-8 + i * -7.211501870593717E-7 )
mul=2;sp=1e-10
	-7.109401801430604E-7
	( 8.725038216569891E-8 + i * -7.109401801430604E-7 )
mul=4
	-7.176635703487477E-7
	( 5.504424522781437E-8 + i * -7.176635703487477E-7 )
mul=4;sp=1e-12
	-7.240139701727988E-7
	( 5.3350348756808774E-8 + i * -7.240139701727988E-7 )
mul=4;sp=1e-14
	-7.842245927959512E-7
	( 6.143651428217045E-8 + i * -7.842245927959512E-7 )
mul=4;sp=1e-20
	( 6.142199589914688E-8 + i * -7.210180399530716E-7 )
*/


	static double compute (FunctionBody f, double sigma, String part)
	{
		List <Double> domain = computer.computeCycleSyncPoints (f, 1E-40, 1, 1, sigma);
		domain.add (10d); if (TRC) System.out.println (domain);

		double I = computer.integralOver (domain);
		if (TRC) System.out.println (I);
		System.out.println ();

		System.out.println (); System.out.print (part);
		System.out.print (" - N=" + domain.size ());
		System.out.print (" - E=" + computer.aggregateError);
		System.out.print (" - O=" + computer.evaluations);
		System.out.println (); System.out.println ();

		return I;
	}
	static ComputedCycles computer = new ComputedCycles ();


	public static void main (String[] args)
	{
		System.out.println ();
		double sigma = 14.1, Re, Im;
		if (TRC) System.out.println ("Re: "); Re = compute ((x) -> REAL_PART (x, sigma), sigma, "  RE: ");
		if (TRC) System.out.println ("Im: "); Im = compute ((x) -> IMAG_PART (x, sigma), sigma, "  IM: ");
		String sgn = " + "; if (Im < 0) { Im = - Im; sgn = " - "; }
		String v = "( " + Re + " " + sgn + "i * " + Im + " )";

		System.out.println ();
		System.out.println ("Full Integral");
		System.out.println (v);
		System.out.println ();
	}

}

