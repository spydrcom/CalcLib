
package net.myorb.testing;

import net.myorb.math.*;
import net.myorb.math.TaylorPolynomials;
import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

public class TrigTests extends OptimizedMathLibrary<Double>
{


	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	static TaylorPolynomials<Double> taylor = new TaylorPolynomials<Double> (mgr);


	TrigTests () { super (mgr); }


	double
	rad2 = 1.4142135623730950488016887242097,
	rad3 = 1.732050807568877293527446341505,
	PI = 3.14159265358979323;


	HighSpeedMathLibrary lib = new HighSpeedMathLibrary ();
	public Double atanHook (Double x) { return lib.atan (x); }


	public void atanInversionTests ()
	{
		for (double x = 0; x < 1; x += 1/RUNS)
		{
			double a = arctangent (x);
			double t = tan (a);
			double e = abs (t - x);
			System.out.println ("x=" + x + "   a=" + a + "   t=" + t + "   e=" + e);
			sum += e; count++;
		}
		System.out.println ("avg=" + sum/count);
	}
	double RUNS = 360, sum = 0, count = 0;

	public void atanCircleTests ()
	{
		double inc = PI/RUNS;
		for (double x = -PI+inc; x < PI; x += inc)
		{
			double s=sin(x), c=cos(x);
			double arc = atan (s, c);
			double e = abs (arc - x);
			System.out.println ("x=" + x + "   s=" + s + "   c=" + c + "   a=" + arc + "   e=" + e);
			sum += e; count++;
		}
		System.out.println ("avg=" + sum/count);
	}


	public void atan45Tests ()
	{
		double x = PI/4;
		double s=sin(x), c=cos(x), t = s/c;
		double arc = atan (s, c);
		double e = abs (arc - x);
		System.out.println ("s=" + s + "\rc=" + c + "\rt=" + t);		
		System.out.println ("x=" + x + "   a=" + arc + "   e=" + e);		
	}

	public void atanTests ()
	{
		System.out.println (2-rad3);
		System.out.println ("atan(2-rad3)*180/PI="+arctangent (2-rad3)*180/PI);
		System.out.println (rad2-1);
		System.out.println ("atan(rad2-1)*180/PI="+arctangent (rad2-1)*180/PI);
		System.out.println (rad3/3);
		System.out.println ("atan(rad3/3)*180/PI="+arctangent (rad3/3)*180/PI);
		System.out.println ("---");

		System.out.println ("atan(s3/3)*180/PI="+arctangent (rad3/3)*180/PI);
		System.out.println ("atan(s3/3)*180/PI="+taylor.atan (rad3/3)*180/PI);
		System.out.println ("atan(1)*180/PI="+arctangent (1.0)*180/PI);
		System.out.println ("atan(1)*180/PI="+taylor.atan (1.0)*180/PI);
		System.out.println ("atan(rad3)*180/PI="+lib.atan (rad3,PI/4, 1)*180/PI);
	}


	/**
	 * execute tests
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		//new TrigTests ().arcTests ();
		//new TrigTests ().atanTests ();
		//new TrigTests ().atanInversionTests ();
		new TrigTests ().atanCircleTests ();
		//new TrigTests ().atan45Tests ();
	}


	double s2 = sqrt (2.0);
	double s3 = sqrt (3.0);

	public void arcTests ()
	{
		System.out.println ("sqrt(2) = " + s2);
		System.out.println ("sqrt(3) = " + s3);
		System.out.println ("12*asin((s2*s3-s2)/4...) = " + asin (0.2588190452035207623488988376)*12);
		System.out.println ("8*asin((.../2...) = " + asin (0.38268343236508977172845998)*8);
		System.out.println ("12*asin((s2*s3-s2)/4) = " + asin ((s2*s3-s2)/4)*12);
		System.out.println ("6*asin(0.5) = " + asin (0.5)*6);
		System.out.println ("4*asin(s2/2) = " + asin (s2/2)*4);
		System.out.println ("3*asin(s3/2) = " + asin (s3/2)*3);
		System.out.println ("3*asin(s3/2...) = " + asin (0.8660254037844386467637)*3);
		System.out.println ("12/5*asin((s2*s3+s2)/4) = " + asin ((s2*s3+s2)/4)*12/5);
		System.out.println ("2*asin(1) = " + asin (1.0)*2);
		System.out.println ("4*arctangent(1.0) = " + arctangent (1.0)*4);
		System.out.println ("4*atan(1.0) = " + atan (1.0)*4);
	}

	public static void sqrtTest ()
	{
		System.out.println ("sqrt(2)=" + taylor.sqrt (2.0));
		System.out.println ("sqrt(3)=" + taylor.sqrt (3.0));
		System.out.println ("sqrt(4)=" + taylor.sqrt (4.0));
		System.out.println ("sqrt(5)=" + taylor.sqrt (5.0));
		System.out.println ("sqrt(9)=" + taylor.sqrt (9.0));
		System.out.println ("sqrt(16)=" + taylor.sqrt (16.0));
		System.out.println ("sqrt(25)=" + taylor.sqrt (25.0));
		System.out.println ("sqrt(1M)=" + taylor.sqrt (1000000.0));

		PowerFunction<Double> exp = taylor.getExpSeries (20);
		System.out.println ("exp(1) = " + exp.eval (1.0));

		PowerFunction<Double>
			cos = taylor.getCosSeries (10);
		System.out.println ("cos(PI/4) = " + cos.eval (mgr.getPi()/4.0));

		PowerFunction<Double>
			sin = taylor.getSinSeries (10);
		System.out.println ("sin(PI/4) = " + sin.eval (mgr.getPi()/4.0));

		System.out.println ("exp (ln(5)) = " + exp.eval (taylor.ln (5.0)));
		System.out.println ("exp (ln(0.5)) = " + exp.eval (taylor.ln (0.5)));
		System.out.println ("ln(0.5) = " + taylor.ln (0.5));

		PowerFunction<Double>
			asin = taylor.getAsinSeries (20);
		System.out.println ("6*asin(0.5) = " + asin.eval (0.5)*6);

	}

}
