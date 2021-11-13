
package net.myorb.testing.vc31;

import net.myorb.math.vanche.Primitives;
import net.myorb.math.Function;

/**
 * a unit test for VanChe spline generator using Airy Ai function samples
 */
public class Airy extends Primitives
{

	static final double
	A269892 = -1.018792971647471,				// Ai max occurs at
	A269893 = +0.53565665601569986114486;		// Ai maximum value

	/**
	 * location of Ai max and max value are know to high precision.
	 *  check the spline for precision of the calculation of the max value
	 * @param spline the function to check
	 */
	public static void checkMax (Function <Double> spline)
	{
		double max = spline.eval (A269892), error = max - A269893;
		System.out.println ("error at max = " + error);
	}

	/**
	 * for items in the domain the spline should exactly match the sample
	 * @param spline the generated spline to be tested
	 */
	public static void verify (Function <Double> spline)
	{
		int n = 0;
		for (double x : DOMAIN)
		{
			double
				y = AIRY_SAMPLES[n++],
				e = y - spline.eval (x);
			System.out.println
			(
				"  x = " + x + 
				", y = " + y + 
				", error = " + e
			);
		}
	}

	/**
	 * Airy function values over domain [ -1.5 .. +1.5 ] with step of 0.1
	 */
	static final double[] AIRY_SAMPLES =
		{
			0.4642565777488693,  0.49170018106129076, 0.5122720060410308,  0.5261943748021201,  0.5338105104305021, 
			0.535560883292352,   0.5319599456109738,  0.5235739497057739,  0.51100039757501,    0.4948495254311495, 
			0.4757280916105394,  0.4542256138886673,  0.43090309528558074, 0.40628418744480127, 0.38084866812012136,
			0.355028053887817,   0.3292031299435379,  0.3037031542863818,  0.2788064819550048,  0.2547423542956762, 
			0.23169360648083331, 0.20980006166637935, 0.18916240039814997, 0.1698463174443647,  0.15188680364054413, 
			0.13529241631288128, 0.1200494273553975,  0.10612576226331226, 0.09347466577150236, 0.0820380498076103, 
			0.07174949700810496
		};
	public static void main (String... args)
	{
		Function <Double>
			spline = splineFor (AIRY_SAMPLES);
		verify (spline); checkMax (spline);
	}

}
