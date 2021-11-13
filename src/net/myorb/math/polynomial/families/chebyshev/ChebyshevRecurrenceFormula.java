
package net.myorb.math.polynomial.families.chebyshev;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.GeneralRecurrence;
import net.myorb.math.Polynomial.PowerFunction;

/**
 * general recurrence formula for Chebychev given kind
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ChebyshevRecurrenceFormula<T> extends GeneralRecurrence<T>
{

	//
	//  T[n+1] - 2 * x * T[n] + T[n-1] = 0
	//  U[n+1] - 2 * x * U[n] + U[n-1] = 0
	//

	public ChebyshevRecurrenceFormula (PolynomialSpaceManager<T> psm, int kind)
	{ super (psm); this.kind = kind; }
	protected int kind;

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#seedRecurrence()
	 */
	public void seedRecurrence ()
	{
		add (ONE); add (kind==ChebyshevPolynomial.Tn ? variable : multiply (TWO, variable));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfN(int)
	 */
	public PowerFunction<T> functionOfN (int n)
	{
		return multiply (TWO, variable);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNminus1(int)
	 */
	public PowerFunction<T> functionOfNminus1 (int n)
	{
		return con (-1);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNplus1(int)
	 */
	public PowerFunction<T> functionOfNplus1 (int n)
	{
		return ONE;
	}


	private static final long serialVersionUID = -47367075266889175L;
}

