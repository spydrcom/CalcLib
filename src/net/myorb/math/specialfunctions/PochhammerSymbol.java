
package net.myorb.math.specialfunctions;

/**
 * support for Pochhammer Symbol operations
 * @author Michael Druckman
 */
public class PochhammerSymbol extends Library
{

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

}
