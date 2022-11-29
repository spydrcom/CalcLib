
package net.myorb.math.computational;

import net.myorb.math.SpaceManager;

public class GenericArithmetic
{

	/**
	 * @param base
	 * @param exponent
	 * @param typeManager
	 * @return
	 */
	public static <T> T pow
		(
			T base, int exponent,
			SpaceManager <T> typeManager
		)
	{
		T result = base;
		
		if (exponent == 0)
		{
			result = typeManager.getOne ();
		}
		else if (exponent < 0)
		{
			result = typeManager.invert (pow (base, -exponent, typeManager));
		}
		else
		{
			for (int remaining = exponent-1; remaining > 0; remaining--)
			{
				result = typeManager.multiply (result, base);
			}
		}

		return result;
	}

}
