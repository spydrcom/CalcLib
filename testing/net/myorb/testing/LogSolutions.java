
package net.myorb.testing;

import net.myorb.math.TaylorPolynomials;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

public class LogSolutions
{


	protected DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	protected TaylorPolynomials<Double> taylor = new TaylorPolynomials<Double> (mgr);


	/*
	 * methods for computation of logarithms
	 */

	public static double
	LE10 = 2.30258509299404568401799145,		// for using 10.0 as base
	LE11 = 0.09531017980432486004395212,		// for using 1.1 as base
	LE20 = 0.693147180559945309417232121;		// for using 2.0 as base

	/*
	 * based on identity equation:  ln(x) = 2 * artanh ((x - 1) / (x + 1))
	 */

	public Double artanh (Double x) { return taylor.getArtanhSeries (50).eval (x); }
	public Double lnUsingArtanh (Double x) { return artanh ((x - 1) / (x + 1)) * 2; }
	public Double lnUsingTaylor (Double x) { return taylor.getLnSeries (50).eval (x - 1); }
	public Double naturalLog (Double x) { return lnUsingTaylor (x); }

	public Double naturalLogUsingReducedDomain
	(Double x, double threshold, double multiplier, double baseConversion)
	{
		int power = 0; while (x < threshold) { x = x * multiplier; power--; }
		return naturalLog (x) + power*baseConversion;
	}

	public Double naturalLogUsingSimpleReducedDomain (Double x)
	{
		//return naturalLogUsingReducedDomain (x, THRESHOLD20, MULTIPLIER20, BASE_CONVERSION20);
		return naturalLogUsingReducedDomain (x, THRESHOLD_E, MULTIPLIER_E, BASE_CONVERSION_E);
	}
	double THRESHOLD11 = 0.95, MULTIPLIER11 = 1.1,  BASE_CONVERSION11 = LE11;		// base 1.1
	double THRESHOLD20 = 0.65, MULTIPLIER20 = 2.0,  BASE_CONVERSION20 = LE20;		// base 2.0
	double THRESHOLD10 = 0.10, MULTIPLIER10 = 10.0, BASE_CONVERSION10 = LE10;		// base 10.0
	double THRESHOLD_E = 0.50, MULTIPLIER_E = e,	BASE_CONVERSION_E = 1;			// base e
	/*
	 * high error = 4.763865556591128E-15   avg error = 3.999714297089001E-16   [ time of test = 13407ms ]  *** using 2.0
	 * high error = 1.324587222680397E-15   avg error = 2.016700424453692E-16   [ time of test = 13125ms ]  *** using e
	 */

	double
	THRESHOLDS[]	= new double[]{THRESHOLD11,			THRESHOLD20,		THRESHOLD10},
	MULTIPLIERS[]	= new double[]{MULTIPLIER11,		MULTIPLIER20,		MULTIPLIER10},
	CONVERSIONS[]	= new double[]{BASE_CONVERSION11,	BASE_CONVERSION20,	BASE_CONVERSION10};

	public Double naturalLogUsingMultiplyReducedDomain (Double x)
	{
		double result = 0.0;
		for (int i = THRESHOLDS.length-1; i >= 0; i--)
		{
			int power = 0;
			double t = THRESHOLDS[i], m = MULTIPLIERS[i];
			while (x < t) { x = x * m; power--; }
			result += power * CONVERSIONS[i];
			
		}
		result += naturalLog (x);
		return result;
	}
	/*
	 * high error = 2.341644443287663E-15   avg error = 3.3957841384826E-16   [ time of test = 13235ms ]	
	 */

	public Double naturalLogUsingReducedDomain (Double x)
	{
		return naturalLogUsingSimpleReducedDomain (x);
		//return naturalLogUsingMultiplyReducedDomain (x);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#ln(java.lang.Object)
	 */
	public Double ln (Double value) 
	{
		if (value <= 0)
		{ throw new RuntimeException ("Invalid parameter for Ln"); }
		else if (value > 1) return - naturalLogUsingReducedDomain (1 / value);
		else return naturalLogUsingReducedDomain (value);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#exp(java.lang.Object)
	 */
	public Double exp (Double value)
	{
		double characteristic = value.intValue (), mantissa = value - characteristic;
		return taylor.getExpSeries (20).eval  (mantissa) * pow (e, (int)characteristic);
	}
	public static final double e = 2.7182818284590452353602874713527;


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#pow(java.lang.Object, int)
	 */
	public Double pow (Double x, int n)
	{
		if (n < 0) return 1 / (pow (x, -n));
		else if (n == 0) return 1.0;
		else if (n == 1) return x;
		
		Double v = x;
		Double square = v * v;
		Double result = square;

		while ((n -= 2) >= 2)
		{
			result *= square;
		}

		if (n == 1)
			return result * v;
		else return result;
	}


}

