
package net.myorb.math.polynomial.families.jacobi;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.GeneralRecurrence;

import net.myorb.math.Polynomial;

/**
 * general recurrence formula for Jacobi give parameters 'a' and 'b'
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class JacobiRecurrenceFormula<T> extends GeneralRecurrence<T>
{


	// 2 * (n + 1) * (a + b + n + 1) * (a + b + 2 * n) * P[a,b,n+1] =
	//		( a + b + 2*n + 1 ) * (a^2 - b^2 + x * (a + b + 2 * n + 2) * (a + b + 2 * n) ) * P[a,b,n]
	//  - 2 * (a + n) * (b + n) * (a + b + 2*n + 2) * P[a,b,n-1]

	public JacobiRecurrenceFormula (PolynomialSpaceManager<T> psm) { super (psm); }


	public void setParameters (T a, T b)
	{ this.a = a; this.b = b; }
	protected T a, b;


	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#seedRecurrence()
	 */
	public void seedRecurrence () {} // must be overridden to add seed functions

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfN(int)
	 */
	public Polynomial.PowerFunction<T> functionOfN (int n)
	{
		T aPlusB = sumOf (a, b), aMinusB = sumOf (a, negOf (b));
		T aSqMinusBSq = productOf (aPlusB, aMinusB), sumAB2n = sumOf (aPlusB, discrete (2 * n));
		T xCoef = productOf (sumOf (sumAB2n, discrete (2)), sumAB2n);

		Polynomial.PowerFunction<T> xTerm = times (xCoef, variable);
		Polynomial.PowerFunction<T> line = add (xTerm, con (aSqMinusBSq));

		return multiply (con (sumOf (sumAB2n, discrete (1))), line);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNminus1(int)
	 */
	public Polynomial.PowerFunction<T> functionOfNminus1 (int n)
	{
		T N = discrete (n), product = discrete (-2);
		T aPlusN = sumOf (a, N); product = productOf (product, aPlusN);
		T bPlusN = sumOf (b, N); product = productOf (product, bPlusN);
		T sumAB2n = sumOf (sumOf (aPlusN, bPlusN), discrete (2));
		return con (productOf (product, sumAB2n));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNplus1(int)
	 */
	public Polynomial.PowerFunction<T> functionOfNplus1 (int n)
	{
		T constant = discrete (2 * (n + 1));
		T aPlusB = sumOf (a, b), nPlus1 = discrete (n + 1);
		T sumABnp1 = sumOf (aPlusB, nPlus1), sumAB2n = sumOf (aPlusB, discrete (2 * n));
		return con (productOf (productOf (constant, sumABnp1), sumAB2n));
	}

	private static final long serialVersionUID = -2524425103850043378L;
}

