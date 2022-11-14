
package net.myorb.math.specialfunctions;

import net.myorb.math.primenumbers.FactorizationSpecificFunctions;
import net.myorb.math.primenumbers.Factorization;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * support for Pochhammer Symbol operations
 * @author Michael Druckman
 */
public class PochhammerSymbol extends Library
{

	public PochhammerSymbol () {}

	/**
	 * @param a real number parameter
	 * @param n the integer range of the raising factorial
	 * @return gamma(a+n) / gamma(a)
	 */
	public static double eval (double a, int n)
	{
		if (n == 0) return 1;

		if (n < 0)
		{
			return alternatingSign (-n) / eval (1-a, -n);
		}

		if (isInteger (a) && a < 0)
		{
			if (n > -a)
				return 0;
			else
			{
				double ratio =
					factorial (-a).doubleValue () /
					factorial (-a - n).doubleValue ();
				return alternatingSign (n) * ratio;
			}
		}

		return gamma (a + n) / gamma (a);
	}


	public PochhammerSymbol
	(SpaceManager <Factorization> manager, ExtendedPowerLibrary <Factorization> lib)
	{
		this.factoredPochhammer = new PochhammerHighPrecision (manager, lib);
	}
	PochhammerHighPrecision factoredPochhammer;

	/**
	 * Pochhammer operation specific to Factorization data
	 * - number of injective functions a set of size n to a set of size x.
	 * @param x the size of the master set being drawn from
	 * @param n distinct elements drawn from size x
	 * @return number of injective functions
	 */
	public Factorization eval (Factorization x, Factorization n)
	{
		return factoredPochhammer.eval (x, n);
	}

}


/**
 * extending FactorizationPrimitives to provide the evaluation
 */
class PochhammerHighPrecision extends FactorizationSpecificFunctions
{

	public PochhammerHighPrecision
	(SpaceManager <Factorization> manager, ExtendedPowerLibrary <Factorization> lib)
	{
		super (manager, lib);
	}

	public Factorization eval (Factorization a, Factorization n)
	{
		if (factoredMgr.isZero (n)) return ONE;

		if (factoredMgr.isNegative (n))
		{
			Factorization negN = factoredMgr.negate (n),
				negA1 = factoredMgr.add (factoredMgr.negate (a), ONE),
				evalNeg = factoredMgr.invert (eval (negA1, negN));
			return factoredMgr.multiply (alt (negN), evalNeg);
		}

		if (isInt (a) && factoredMgr.isNegative (a))
		{
			Factorization negA = factoredMgr.negate (a);

			if (factoredMgr.lessThan (negA, n))
			{ return factoredMgr.getZero (); }

			Factorization negAmN =
				factoredMgr.add (negA, factoredMgr.negate (n));
			Factorization negAf = combo.factorial (negA);
			Factorization negAmNf = combo.factorial (negAmN);
			Factorization negAmNfI = factoredMgr.invert (negAmNf);
			Factorization ratio = factoredMgr.multiply (negAf, negAmNfI);

			return factoredMgr.multiply (alt (n), ratio);
		}

		Factorization gammaAi = factoredMgr.invert (GAMMA (a));
		Factorization gammaAN = GAMMA (factoredMgr.add (a, n));
		return factoredMgr.multiply (gammaAN, gammaAi);
	}

}

