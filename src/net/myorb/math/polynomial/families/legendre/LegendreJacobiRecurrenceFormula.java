
package net.myorb.math.polynomial.families.legendre;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.families.jacobi.JacobiRecurrenceFormula;

/**
 * Recurrence Formula using relationship to Jacobi
 * @param <T> underlying data type
 * @author Michael Druckman
 */
public class LegendreJacobiRecurrenceFormula<T> extends JacobiRecurrenceFormula<T>
{

	/**
	 * using Jacobi (a=b=0)
	 * @param psm required space manager
	 */
	public LegendreJacobiRecurrenceFormula (PolynomialSpaceManager<T> psm)
	{ super (psm); setParameters (discrete (0),  discrete (0)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.families.jacobi.JacobiRecurrenceFormula#seedRecurrence()
	 */
	public void seedRecurrence () { useSimpleSeed (); }

	private static final long serialVersionUID = 2913214284094427975L;
}

