
package net.myorb.math.computational;

import net.myorb.math.computational.TanhSinhQuadratureTables;
import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;

import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * identify maximum and minimum points for a function over an interval
 * @author Michael Druckman
 */
public class MaxMin
{


	/**
	 * @param lo the low value of the interval
	 * @param hi the high value of the interval to test
	 * @param delta the difference between test points
	 * @return the list of max and min points found
	 */
	public List <Double> find (double lo, double hi, double delta)
	{
		List <Double> found = new ArrayList <Double> ();
		double cur = lo, prev = f.eval (cur), next = f.eval (cur+=delta);
//		double cur = lo, prev = f.eval (cur+=delta), next = f.eval (cur+=delta);	// useful for zero asymptotes
		boolean positiveDerivative = next > prev;
		double lastFound = Double.NaN;

		while (cur < hi)
		{
			prev = next; next = f.eval (cur+=delta);

			if (positiveDerivative)
			{
				if (next < prev)
				{
					positiveDerivative = false;
					System.out.println ("x="+cur+" \t f(x)="+next);
					found.add (lastFound = cur);
				}
			}
			else
			{
				if (next > prev)
				{
					positiveDerivative = true;
					System.out.println ("x="+cur+" \t f(x)="+next);
					found.add (lastFound = cur);
				}
			}
		}

		if (lastFound < hi) found.add (hi);

		return found;
	}


	/**
	 * @param lo the low end of an integration range
	 * @param hi the high end of an integration range
	 * @return the calculated integral
	 */
	public double eval (double lo, double hi)
	{
		double result =
			TanhSinhQuadratureAlgorithms.Integrate
				(f, lo, hi, targetAbsoluteError, stats);
		System.out.println (lo + ".." + hi);
		System.out.println ("\t : " + stats);
		System.out.println ("\t I=" + result);
		return result;
	}
	protected TanhSinhQuadratureTables.ErrorEvaluation stats =
		new TanhSinhQuadratureTables.ErrorEvaluation ();
	protected double targetAbsoluteError = 1E-4;


	/**
	 * @param maxMin the list of max and min domain points
	 * @return the calculated integral
	 */
	public double integralOver (List <Double> maxMin)
	{
		double h, result = 0, l = maxMin.get (0);
		for (int i = 1; i < maxMin.size (); i++)
		{
			result += eval (l, h = maxMin.get (i));
			System.out.println ("\t AGG=" + result);
			System.out.println ();
			l = h;
		}
		return result;
	}


	/**
	 * @param f the function to be tested
	 */
	public void setFunction (Function <Double> f) { this.f = f; }
	Function <Double> f;


}

