
package net.myorb.math.polynomial.families.legendre;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.SpaceManager;

public class LegendrePolynomialSpaceManager<T> extends PolynomialSpaceManager<T>
{
	/**
	 * @param manager data type manager is required
	 */
	public LegendrePolynomialSpaceManager
		(SpaceManager<T> manager)
	{
		super (manager);
	}
}