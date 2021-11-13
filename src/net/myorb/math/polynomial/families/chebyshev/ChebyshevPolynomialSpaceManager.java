
package net.myorb.math.polynomial.families.chebyshev;

import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.SpaceManager;

/**
 * override display of polynomials to show T functions as appropriate
 * for Chebyshev polynomials which do not show exponents of x nor the
 * inner coefficients that are generated as part of the series
 * @param <T> type on which operations are to be executed
 */
public class ChebyshevPolynomialSpaceManager<T> extends PolynomialSpaceManager<T>
{

	/**
	 * @param manager data type manager is required
	 */
	public ChebyshevPolynomialSpaceManager
		(SpaceManager<T> manager)
	{
		super (manager);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialSpaceManager#formatTerm(int, java.lang.Object, java.lang.StringBuffer)
	 */
	public void formatTerm (int termNo, T c, StringBuffer buffer)
	{
		// the constant for the term is displayed for c ~= 1
		if (!formatTermOperation (c, termNo, buffer)) buffer.append (" * ");

		// then the Chebyshev T function of (x) replaces the traditional x^n
		buffer.append ("T[").append (termNo).append ("](x)");
	}

}


