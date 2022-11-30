
package net.myorb.math.computational;

import net.myorb.math.SpaceManager;

/**
 * provide description of matrices as types for algebraic transform
 * @author Michael Druckman
 */
public class GenericArithmetic
{

	/**
	 * compute base^exponent for integer exponent values
	 * - zero exponent returns 1 and negative exponent is 1/base^(-exponent)
	 * @param base the generic base value for the calculation
	 * @param exponent the exponent value for the calculation
	 * @param typeManager manager for data type
	 * @return the computed result
	 * @param <T> data type
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
