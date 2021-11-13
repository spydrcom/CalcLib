
package net.myorb.testing;

import net.myorb.math.*;
import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

public class TaylorTests
{

	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	static PolynomialSpaceManager<Double> pfm = new PolynomialSpaceManager<Double> (mgr);

	/**
	 * execute tests
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		sqrtTest ();
	}

	public static void sqrtTest ()
	{
		TaylorPolynomials<Double> taylor = new TaylorPolynomials<Double> (mgr);
		PowerFunction<Double> f = taylor.getSqrtSeries (20);
		System.out.println (pfm.toString (f));

		System.out.println ("sqrt(2)=" + taylor.sqrt (2.0));
		System.out.println ("sqrt(3)=" + taylor.sqrt (3.0));
		System.out.println ("sqrt(4)=" + taylor.sqrt (4.0));
		System.out.println ("sqrt(5)=" + taylor.sqrt (5.0));
		System.out.println ("sqrt(9)=" + taylor.sqrt (9.0));
		System.out.println ("sqrt(16)=" + taylor.sqrt (16.0));
		System.out.println ("sqrt(25)=" + taylor.sqrt (25.0));
		System.out.println ("sqrt(1M)=" + taylor.sqrt (1000000.0));

		PowerFunction<Double> exp = taylor.getExpSeries (20);
		System.out.println ("exp(x) = " + pfm.toString (exp));
		System.out.println ("exp(1) = " + exp.eval (1.0));

		PowerFunction<Double>
			cos = taylor.getCosSeries (10);
		System.out.println ("cos(x) = " + pfm.toString (cos));
		System.out.println ("cos(PI/4) = " + cos.eval (mgr.getPi()/4.0));

		PowerFunction<Double>
			sin = taylor.getSinSeries (10);
		System.out.println ("sin(x) = " + pfm.toString (sin));
		System.out.println ("sin(PI/4) = " + sin.eval (mgr.getPi()/4.0));

		PowerFunction<Double>
			ln = taylor.getLnSeries (20);
		System.out.println ("ln(x) = " + pfm.toString (ln));
		System.out.println ("exp (ln(5)) = " + exp.eval (taylor.ln (5.0)));
		System.out.println ("exp (ln(0.5)) = " + exp.eval (taylor.ln (0.5)));
		System.out.println ("ln(0.5) = " + taylor.ln (0.5));

		PowerFunction<Double>
			asin = taylor.getAsinSeries (20);
		System.out.println ("asin (x) = " + pfm.toString (asin));
		System.out.println ("6*asin(0.5) = " + asin.eval (0.5)*6);

	}
}
