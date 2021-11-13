
package net.myorb.math.polynomial.families.legendre;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.families.gegenbauer.GegenbauerRecurrenceFormula;

/**
 * Recurrence Formula using relationship to Gegenbauer (lambda = 0.5)
 * @param <T> underlying data type
 * @author Michael Druckman
 */
public class LegendreGegenbauerRecurrenceFormula<T> extends GegenbauerRecurrenceFormula<T>
{

	//  (n+1) * C[n+1] - 2 * (lambda + n) * x * c[n] + (2 * lambda + n - 1) * C[n-1] = 0
	//	lambda = 0.5 for Legendre

	public LegendreGegenbauerRecurrenceFormula (PolynomialSpaceManager<T> psm)
	{ super (psm); setLambda (inverseOf (discrete (2))); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.families.gegenbauer.GegenbauerRecurrenceFormula#seedRecurrence()
	 */
	public void seedRecurrence () { useSimpleSeed (); }

	private static final long serialVersionUID = 1847523457137870456L;
}

