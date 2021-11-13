
package net.myorb.math.polynomial.families.bessel;

import net.myorb.math.polynomial.GeneralRecurrence;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.specialfunctions.bessel.OrdinaryFirstKind;

import net.myorb.math.Polynomial;

/**
 * general recurrence formula for Bessel Jn functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class BesselRecurrenceFormula<T> extends GeneralRecurrence<T>
{

	//
	//  (2 * p) / x * J#p = J#(p-1) + J#(p+1)
	//
	//		x J     =  2 p J   -  x J
	//		    p+1			 p		  p-1
	//

	public BesselRecurrenceFormula (PolynomialSpaceManager<T> psm, int terms)
	{ super (psm); this.terms = terms; }
	protected int terms;

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#seedRecurrence()
	 */
	public void seedRecurrence ()
	{
		//add (BesselFunctions.getJ (0, terms, psm)); add (BesselFunctions.getJ (1, terms, psm));
		add (OrdinaryFirstKind.getJ (0, terms, psm)); add (OrdinaryFirstKind.getJ (1, terms, psm));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfN(int)
	 */
	public Polynomial.PowerFunction<T> functionOfN (int n)
	{
		return con (2 * n);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNminus1(int)
	 */
	public Polynomial.PowerFunction<T> functionOfNminus1 (int n)
	{
		return neg (variable);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.GeneralRecurrence#functionOfNplus1(int)
	 */
	public Polynomial.PowerFunction<T> functionOfNplus1 (int n)
	{
		return variable;
	}

	private static final long serialVersionUID = -5003518341210678613L;
}

