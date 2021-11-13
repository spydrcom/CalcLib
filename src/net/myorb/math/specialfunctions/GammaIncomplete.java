
package net.myorb.math.specialfunctions;

import net.myorb.math.expressions.algorithms.JrePowerLibrary;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.Function;

/**
 * calculation of Incomplete Lower Gamma function.
 *  Incomplete Upper Gamma function is computed from Lower and GAMMA.
 *  Re-factored to use GenericIncompleteGamma to avoid redundant maintenance
 *  with the Complex version of the implementation 07/28/2021
 * @author Michael Druckman
 */
public class GammaIncomplete extends GenericIncompleteGamma<Double>
{

	/**
	 * generic requires GAMMA implementation and a power library
	 * @param gammaFunction a GAMMA function implementation
	 * @param lib an implementation of the power library
	 */
	public GammaIncomplete
		(
			Function<Double> gammaFunction,
			ExtendedPowerLibrary<Double> lib
		)
	{
		super (gammaFunction, lib);
	}

	/**
	 * default to use of JRE for power library requirements
	 * @param gammaFunction a GAMMA function implementation
	 */
	public GammaIncomplete
	(Function<Double> gammaFunction)
	{
		super (gammaFunction, new JrePowerLibrary ());
	}

//	/**
//	 * count of terms for polynomial series
//	 */
//	public static final int TERMS = 25;
//
//	/**
//	 * @param gammaFunction access to full GAMMA function
//	 */
//	public GammaIncomplete (Gamma gammaFunction)
//	{
//		this.gammaFunction = gammaFunction;
//	}
//	Gamma gammaFunction;
//
//	/**
//	 * the limiting function
//	 * @param s the parameter to GAMMA
//	 * @param z the intermediate point between upper and lower
//	 * @return the function result
//	 */
//	public Double gammaStar (Double s, Double z)
//	{
//		return Math.exp (-z) * powerSeries (s, z);
//	}
//
//	/**
//	 * @param s the parameter to GAMMA
//	 * @param z the intermediate point between upper and lower
//	 * @return the function result
//	 */
//	public Double powerSeries (Double s, Double z)
//	{
//		double sum = 0;
//		// sum [0 <= k <= INF] ( z^k / GAMMA (s+k+1) )
//		for (int k = 0; k < TERMS; k++) sum += Math.pow (z, k) / gammaFunction.eval (s+k+1);
//		return sum;
//	}
//
//	/**
//	 * @param s the parameter to GAMMA
//	 * @param x the intermediate point between upper and lower
//	 * @return the function result
//	 */
//	public Double lower (Double s, Double x)
//	{
//		return Math.pow (x, s) * gammaFunction.eval (s) * gammaStar (s, x);
//	}
//
//	/**
//	 * @param s the parameter to GAMMA
//	 * @param x the intermediate point between upper and lower
//	 * @return the function result
//	 */
//	public double upper (double s, double x)
//	{
//		return gammaFunction.eval (s) - lower (s, x);
//	}
//	public double gammainc (double a, double x)
//	{ return 1 - gammaincc (a, x); }
//	public double gammaincc (double a, double x)
//	{ return upper (a, x); }

}
