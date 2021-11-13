
package net.myorb.math.specialfunctions;

import net.myorb.math.Function;
import net.myorb.math.SpaceManager;
import net.myorb.math.ExtendedPowerLibrary;

/**
 * calculation of Incomplete Lower Gamma function.
 *  Incomplete Upper Gamma function is computed from Lower and GAMMA.
 * @author Michael Druckman
 */
public class GenericIncompleteGamma<T>
{

	/**
	 * count of terms for polynomial series
	 */
	public static final int TERMS = 200, DIGITS = 50;
	public static boolean TRACE = false;

	/**
	 * @param gammaFunction access to full GAMMA function
	 * @param lib primitive math library
	 */
	public GenericIncompleteGamma
		(
			Function<T> gammaFunction, ExtendedPowerLibrary<T> lib
		)
	{
		this.gammaFunction = gammaFunction;
		this.mgr = gammaFunction.getSpaceManager ();
		this.tolerance = lib.pow (mgr.newScalar (10), -DIGITS);
		this.lib = lib;
	}
	protected ExtendedPowerLibrary<T> lib;
	protected Function<T> gammaFunction;
	protected SpaceManager<T> mgr;
	protected T tolerance;


	/**
	 * the limiting function
	 * @param s the parameter to GAMMA
	 * @param z the intermediate point between upper and lower
	 * @return the function result
	 */
	public T gammaStar (T s, T z)
	{
		T ps = powerSeries (s, z);
		T expnz = lib.exp (mgr.negate (z));
		T result = mgr.multiply (expnz, ps);
		if (TRACE) System.out.println (" exp (-" + z + ") = " + expnz + " ps = " + ps + " result = " + result);
		return result;
	}

	/**
	 * @param s the parameter to GAMMA
	 * @param z the intermediate point between upper and lower
	 * @return the function result
	 */
	public T powerSeries (T s, T z)
	{
		// sum [0 <= k <= INF] ( z^k / GAMMA (s+k+1) )

		if (mgr.isZero (lib.magnitude (z))) return mgr.getZero ();
		if (TRACE) System.out.println ("s = " + s + " z = " + z);

		T ONE = mgr.getOne ();
		// for k=0 the value of the term is 1 / GAMMA (s+1)
		T term = mgr.invert (gammaFunction.eval (mgr.add (s, ONE))); 
		T sum = term, sPlus = s;

		for (int k = 1; k < TERMS; k++)		// start with k=0, term(0) was initial sum value
		{
			sPlus = mgr.add (sPlus, ONE);
			// use GAMMA identity to reduce full function calls g(x+1) = x*g(x)
			term = mgr.multiply (term, mgr.multiply (z, mgr.invert (sPlus)));

			T mag = lib.magnitude (term);
			// check for term contribution approaching zero
			if (mgr.lessThan (mag, tolerance)) return sum;

			sum = mgr.add (sum, term);						// sum of terms
			
			if (TRACE && k%20==0)
			{
				System.out.print ("Splus = " + sPlus + " term = " + term);
				System.out.println (" sum = " + sum);
			}
		}

		return sum;
	}

	/**
	 * Regularized lower
	 * @param a the parameter to GAMMA
	 * @param x the intermediate point between upper and lower [0..x]
	 * @return the function result [0..1] (portion of full GAMMA)
	 */
	public T gammaincr (T a, T x)
	{
		return mgr.multiply (lib.power (x, a), gammaStar (a, x));
	}
	
	/* gammaincr + gammainccr = 1 */

	/**
	 * Regularized upper
	 * @param a the parameter to GAMMA
	 * @param x the intermediate point between upper and lower [x..INF]
	 * @return the function result [0..1] (portion of full GAMMA)
	 */
	public T gammainccr (T a, T x)
	{
		return mgr.add (mgr.negate (gammaincr (a, x)), mgr.getOne ());
	}

	/**
	 * lower incomplete gamma
	 * @param s the parameter to GAMMA
	 * @param x the intermediate point between upper and lower [0..x]
	 * @return the function result
	 */
	public T lower (T s, T x)
	{
		return mgr.multiply (gammaFunction.eval (s), gammaincr (s, x));
	}
	public T gammainc (T a, T x)
	{
		return lower (a, x);
	}

	/**
	 * upper incomplete gamma
	 * @param s the parameter to GAMMA
	 * @param x the intermediate point between upper and lower [x..INF]
	 * @return the function result
	 */
	public T upper (T s, T x)
	{
		return mgr.multiply (gammaFunction.eval (s), gammainccr (s, x));
	}
	public T gammaincc (T a, T x)
	{
		return upper (a, x);
	}

}
