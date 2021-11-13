
package net.myorb.testing;

import net.myorb.math.Function;
import net.myorb.math.SpaceManager;

public class IntegrationRefined
{

	public static void main(String[] args)
	{
		double lo = 0, hi = 1, areaMul = 4;

		Function<Double> f = new RefinementIterator.Integrand()
		{ public Double eval (Double x) { return Math.sqrt (1 - x*x); } };

		new RefinementIterator (lo, hi, f).execute (30, areaMul);
	}

}

class RefinementIterator
{

	public static class Integrand implements Function<Double>
	{
		public Double eval (Double x) { return 0.0; }
		public SpaceManager<Double> getSpaceDescription() { return null; }
		public SpaceManager<Double> getSpaceManager() { return null; }
	}

	public RefinementIterator (double lo, double hi, Function<Double> function)
	{
		this.function = function;
		this.lo = lo; this.hi = hi;
		this.mid = (hi - lo) / 2;
		this.sum = f (lo) + f (lo + mid);
		this.mul = this.mid;
		header ();
	}
	Function<Double> function;
	double lo, hi, mid;
	double sum, mul;

	long start = System.currentTimeMillis();
	double pieces = 1;

	public double f(double x) { return function.eval (x); }

	public void execute (int iterations, double areaMultiplier)
	{
		for (int m = 1; m <= 30; m++)
		{
			execute ();
			if (isSignificant ())
			{ System.out.println (dump () * areaMultiplier); }
		}
	}

	public void execute ()
	{
		pieces *= 2;
		double halfMid = mid / 2, x = lo + halfMid;

		for (long i = 1; i <= pieces; i++)
		{
			sum = sum + f(x);
			x += mid;
		}

		mid = halfMid;
	}

	public boolean isSignificant () { return pieces > 1E6; }

	public double dump ()
	{
		double area = sum * mul / pieces;
		long time = System.currentTimeMillis() - start;
		System.out.print (time + "\t" + pieces + "\t");
		return area;
	}

	public void header ()
	{
		System.out.println ("ms \tCount \t\tResult");
		System.out.println ("== \t===== \t\t======");
	}

}

