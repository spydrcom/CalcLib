
package net.myorb.testing;

import net.myorb.math.OptimizedMathLibrary;
import net.myorb.math.Polynomial;
import net.myorb.math.computational.*;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

public class FunctionDump
{

	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	static PolynomialSpaceManager<Double> psm = new PolynomialSpaceManager<Double> (mgr);
	static OptimizedMathLibrary<Double> lib = new OptimizedMathLibrary<Double> (mgr);
	static PolynomialRoots<Double> proots = new PolynomialRoots<Double> (mgr, lib);
	static FunctionRoots<Double> roots = new FunctionRoots<Double> (mgr, lib);

	/**
	 * execute tests
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		PowerFunction<Double> pf;
		PowerFunction<Double> df;

//		pf = psm.getPolynomialFunction
//			(psm.newCoefficients (5.0, 1.0));
//		pf = multiply (pf, psm.newCoefficients (-4.0, 1.0));
//		pf = multiply (pf, psm.newCoefficients (-1.0, 5.0));
//		pf = multiply (pf, psm.newCoefficients (-7.0, 1.0));
//		pf = multiply (pf, psm.newCoefficients (-12.0, 1.0));
//		pf = multiply (pf, psm.newCoefficients (-3.0, 2.0));

//		pf = psm.getPolynomialFunction
//			(psm.newCoefficients (2.0, 4.0, -9.0, 4.0, 6.0));
		pf = psm.getPolynomialFunction
			(psm.newCoefficients (-1.0, 1.0, -1.0));
		df = psm.getFunctionDerivative (pf);

		dump (pf, df, 0, 2, 0.01);
		//dump (pf, df, -5.5, 12.5, 0.1);
		//dump (pf, df, -2.0, 2.0, 0.1);

//		System.out.println (psm.toString (pf));
//		double lo=-0.6937129433613966, hi=0.36037961002806324;
//		System.out.println ("bs computed=" + roots.bisectionMethod (pf, lo, hi));
//		//System.out.println ("bs computed=" + roots.bisectionMethod (pf, -0.5, 0.3));
//		System.out.println ("eval=" + proots.evaluateEquation (pf));
	}

	static PowerFunction<Double> multiply
	(PowerFunction<Double> p, Polynomial.Coefficients<Double> c)
	{ return psm.multiply (p, psm.getPolynomialFunction (c)); }

	static void dump
		(
			PowerFunction<Double> polynomial, PowerFunction<Double> d,
			double from, double to, double increment
		)
	{
		double x = from;
		while (x < to)
		{
			System.out.println
				(x + "\t" + polynomial.eval (x) + "\t" + d.eval (x));
			x += increment;
		}
	}

	static void dumpRotated
	(
		PowerFunction<Double> polynomial, PowerFunction<Double> d,
		double from, double to, double increment, double rotated
	)
{
	double x = from;
	while (x < to)
	{
		System.out.println
			(x + "\t" + polynomial.eval (x) + "\t" + d.eval (x));
		x += increment;
	}
}
}
