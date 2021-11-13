
package net.myorb.math.polynomial.families.gegenbauer;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.GeneralRecurrence;

import net.myorb.math.Polynomial;

/**
 * general recurrence formula for Gegenbauer given lambda
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class GegenbauerRecurrenceFormula<T> extends GeneralRecurrence<T>
{

	//
	//  (n+1) * C[n+1] - 2 * (lambda + n) * x * c[n] + (2 * lambda + n - 1) * C[n-1] = 0
	//

	public GegenbauerRecurrenceFormula (PolynomialSpaceManager<T> psm) { super (psm); }
	public GegenbauerRecurrenceFormula (PolynomialSpaceManager<T> psm, T lambda)
	{ super (psm); setLambda (lambda); }
	
	/**
	 * @param lambda the lambda value identifies the sub-family
	 */
	public void setLambda (T lambda)
	{ this.lambdaValue = lambda; }
	protected T lambdaValue;

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#seedRecurrence()
	 */
	public void seedRecurrence () {} // must be overridden to add seed functions

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfN(int)
	 */
	public Polynomial.PowerFunction<T> functionOfN (int n)
	{
		T constant = productOf
		(discrete (2), sumOf (lambdaValue, discrete (n)));
		return times (constant, variable);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNminus1(int)
	 */
	public Polynomial.PowerFunction<T> functionOfNminus1 (int n)
	{
		T nm1 = discrete (n - 1);
		T lambda2 = productOf (discrete (2), lambdaValue);
		return con (negOf (sumOf (lambda2, nm1)));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNplus1(int)
	 */
	public Polynomial.PowerFunction<T> functionOfNplus1 (int n)
	{
		return con (n + 1);
	}

	private static final long serialVersionUID = -8838927435329254774L;
}

