
package net.myorb.math.specialfunctions;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * Binomial Coefficient computed using GAMMA
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class Binomial <T>
{

	public interface SFLibrary <T>
	{
		T GAMMA (T z);
	}

	public Binomial
		(
			SpaceManager <T> manager,
			ExtendedPowerLibrary <T> lib,
			SFLibrary <T> sflib
		)
	{
		this.manager = (ExpressionSpaceManager<T>) manager;
		this.sflib = sflib;
		this.lib = lib;
	}
	ExpressionSpaceManager <T> manager;
	ExtendedPowerLibrary <T> lib;
	SFLibrary <T> sflib;

	/**
	 * binomial coefficients
	 * - general case for all data types
	 * @param x the upper number of the set
	 * @param y the lower number of the set
	 * @return GAMMA based computation
	 */
	public T gammaBinomialCoefficient (T x, T y)
	{
		T	one = manager.getOne (),
			ny	= manager.negate (y),
			x1	= manager.add (x, one),
			xy	= manager.add (x1, ny),
			y1	= manager.add (y, one);

		return manager.multiply
			(
				sflib.GAMMA (x1),
			//  ----------------
				manager.invert
				(
					manager.multiply
					(
						sflib.GAMMA (y1),
						sflib.GAMMA (xy)
					)
				)
			);
	}

	/**
	 * binomial coefficients
	 * - general case for all data types
	 * - lnGAMMA used to extend to larger values
	 * @param x the upper number of the set
	 * @param y the lower number of the set
	 * @return LnGAMMA based computation
	 */
	public T lnGammaBinomialCoefficient (T x, T y)
	{
		T	one = manager.getOne (),
			ny	= manager.negate (y),
			x1	= manager.add (x, one),
			xy	= manager.add (x1, ny),
			y1	= manager.add (y, one);

		return lib.exp (manager.add
			(
				lib.ln (sflib.GAMMA (x1)),
			//  ----------------
				manager.negate
				(
					manager.add
					(
						lib.ln (sflib.GAMMA (y1)),
						lib.ln (sflib.GAMMA (xy))
					)
				)
			));
	}

}
