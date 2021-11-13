
package net.myorb.math.polynomial.families.legendre;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.GeneralRecurrence;

import net.myorb.math.Polynomial;

/**
 * general recurrence formula for Legendre polynomials
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LegendreRecurrenceFormula<T> extends GeneralRecurrence<T>
{

	// (n+1) * P[n+1](x) - (2n+1)*x*P[n](x) + nP[n-1](x) = 0

	public LegendreRecurrenceFormula (PolynomialSpaceManager<T> psm)
	{
		super (psm);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#seedRecurrence()
	 */
	public void seedRecurrence () { useSimpleSeed (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfN(int)
	 */
	public Polynomial.PowerFunction<T> functionOfN (int n)
	{
		return multiply (con (2 * n + 1), variable);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNminus1(int)
	 */
	public Polynomial.PowerFunction<T> functionOfNminus1 (int n)
	{
		return con (-n);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNplus1(int)
	 */
	public Polynomial.PowerFunction<T> functionOfNplus1 (int n)
	{
		return con (n + 1);
	}

	private static final long serialVersionUID = -6063838626441802373L;
}

