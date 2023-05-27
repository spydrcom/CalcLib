
package net.myorb.math.computational;

import net.myorb.math.computational.sampling.Calculus;
import net.myorb.math.computational.sampling.SegmentAnalysis.SampleFormatter;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.GeneratingFunctions.Coefficients;
import net.myorb.math.Function;

import java.text.NumberFormat;

/**
 * Taylor polynomial evaluations for functions
 * @param <T> data type used in Arithmetic operations
 * @author Michael Druckman
 */
public class TaylorSeriesEvaluations <T> extends Calculus <T>
{


	/*
	 * f(x) = SIGMA [ 0 <= n <= INFINITY ] ( f'(n)(a)/n! * (x - a)^n ) 
	 * where f'(N)(a) is Nth derivative of f evaluated at a
	 */


	/**
	 * build a Taylor series for a function
	 * @param f the function for series being evaluated
	 * @param a the point a where the Taylor series focus is
	 * @param order the ultimate order of the polynomial being built
	 * @param dx the run value to use for the derivative approximations
	 * @param proximity the linear distance on each side of point a
	 * @return the polynomial Coefficients computed for the series
	 */
	public Coefficients <T> compute
		(
			Function <T> f, T a, int order,
			T dx, T proximity
		)
	{
		this.derivatives = new Derivatives ();
		Coefficients <T> C = new Coefficients <> ();
		evaluate (f, a, order, dx, proximity, derivatives);
		compute (a, derivatives, C);
		return C;
	}
	public Derivatives derivatives;


	/**
	 * compute Taylor series Coefficients
	 * @param a the point a where the Taylor series focus is
	 * @param derivatives the list collecting derivative samples
	 * @param C the list collecting computed Coefficients
	 */
	public void compute
		(
			T a, Derivatives derivatives,
			Coefficients <T> C
		)
	{
		T invF = manager.newScalar (1);
	
		C.add (derivatives.get (0).computeYfor (a));
		C.add (derivatives.get (1).computeYfor (a));
	
		for (int n = 2; n < derivatives.size (); n++)
		{
			invF = manager.multiply
				(invF, manager.invert (manager.newScalar (n)));
			T ddxAtA = derivatives.get (n).computeYfor (a);
			C.add (manager.multiply (ddxAtA, invF));
		}
	}


	public TaylorSeriesEvaluations
	(ExpressionSpaceManager <T> manager)
	{ super ( new Formatter <T> (manager), manager ); }


}

/**
 * format numbers as decimal restricting digit count
 * @param <T> data type being used
 */
class Formatter <T> implements SampleFormatter <T>
{

	Formatter (ExpressionSpaceManager <T> manager)
	{
		this.F = NumberFormat.getNumberInstance ();
		this.F.setMaximumFractionDigits (3);
		this.F.setMinimumFractionDigits (1);
		this.manager = manager;
	}
	ExpressionSpaceManager <T> manager;
	NumberFormat F;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.sampling.SegmentAnalysis.SampleFormatter#format(java.lang.Object)
	 */
	public String format (T sample)
	{
		if ( ! AS_SCIENTIFIC ) return F.format (sample) + "\t";
		double value = manager.convertToDouble (sample);
		return align (format (value));
	}
	boolean AS_SCIENTIFIC = true;

	/**
	 * force column alignment
	 * @param display the value to display
	 * @return the formatted value with constant width
	 */
	public String align (String display)
	{ return display + space.substring (display.length ()); }
	String space = "             ";

	/**
	 * format value using scientific format
	 * @param sample the value to display in format
	 * @return mantissa and exponent formatted
	 */
	public String format (double sample)
	{
		if (sample < 0) return "-" + format (-sample);
		int log10 = (int) Math.floor ( Math.log10 (sample) );
		Double mantissa = sample / Math.pow (10, log10);
		return adjust (mantissa, log10);
	}

	/**
	 * adjust exponent to conform display
	 * @param M the mantissa of the value to format
	 * @param E the exponent of the value to format
	 * @return properly conformed digit sequence
	 */
	public String adjust (Double M, int E)
	{
		double characteristic = Math.ceil (M);
		if (characteristic >= 10) { M /= 10; E += 1; }
		return scientific (F.format (M), E);
	}

	/**
	 * format exponent as required
	 * @param digits the mantissa of the value to format
	 * @param E the possibly zero exponent of the value to format
	 * @return image with non-zero exponent appended
	 */
	public String scientific (String digits, int E)
	{
		if (E != 0) digits += "E" + E;
		return digits;
	}

}

