
package net.myorb.math.polynomial.families;

import net.myorb.math.polynomial.PolynomialFamily;
import net.myorb.math.polynomial.GeneralRecurrence;

import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * support for Laguerre polynomial based algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LaguerrePolynomial<T> extends Polynomial<T>
			implements PolynomialFamily<T>
{

	public LaguerrePolynomial
	(SpaceManager<T> manager) { super (manager); init (); }
	public LaguerrePolynomial () { super (null); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#init(net.myorb.math.SpaceManager)
	 */
	public void init (SpaceManager<T> manager)
	{ this.manager = manager; init (); }
	public void init ()
	{
		this.psm = new LaguerrePolynomialSpaceManager<T>(manager);
	}
	protected PolynomialSpaceManager<T> psm;
	
	/**
	 * use function inter-dependencies to generate series L
	 * @param upTo highest order of functions to be generated
	 * @return the list of generated functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> recurrence (int upTo)
	{
		return new LaguerreRecurrenceFormula<T> (psm).constructFuntions (upTo);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getName()
	 */
	public String getName () { return "Laguerre"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getFunctions(java.lang.String, int)
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> getPolynomialFunctions (String identifier, int upTo)
	{
		return recurrence (upTo);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getIdentifier(java.lang.String)
	 */
	public String getIdentifier (String kind) { return "L"; }

	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		Polynomial.RealNumbers mgr = new Polynomial.RealNumbers ();
		LaguerrePolynomial<Double> L = new LaguerrePolynomial<Double> (mgr);
		PolynomialFamilyManager.dump (L.recurrence (10), mgr);
	}

}

class LaguerreRecurrenceFormula<T> extends GeneralRecurrence<T>
{
	// (n+1) * L[n+1](x) = ( (2n+1)*L[n](x) - x*L[n](x) - nL[n-1](x) )

	public LaguerreRecurrenceFormula (PolynomialSpaceManager<T> psm)
	{
		super (psm);
	}
	public void seedRecurrence ()
	{
		add (ONE); add (add (ONE, neg (variable)));
	}
	public Polynomial.PowerFunction<T> functionOfN (int n)
	{
		return add (con (2*n + 1), neg (variable));
	}
	public Polynomial.PowerFunction<T> functionOfNminus1 (int n)
	{
		return con (-n);
	}
	public Polynomial.PowerFunction<T> functionOfNplus1 (int n)
	{
		return con (n + 1);
	}
	private static final long serialVersionUID = 1L;
}

class LaguerrePolynomialSpaceManager<T> extends PolynomialSpaceManager<T>
{

	/**
	 * @param manager data type manager is required
	 */
	public LaguerrePolynomialSpaceManager
		(SpaceManager<T> manager)
	{
		super (manager);
	}

//	/* (non-Javadoc)
//	 * @see net.myorb.math.PolynomialSpaceManager#formatTerm(int, java.lang.Object, java.lang.StringBuffer)
//	 */
//	public void formatTerm (int termNo, T c, StringBuffer buffer)
//	{
//		// the constant for the term is displayed for c ~= 1
//		if (!formatTermOperation (c, termNo, buffer)) buffer.append (" * ");
//
//		// then the Laguerre L function of (x) replaces the traditional x^n
//		buffer.append ("L[").append (termNo).append ("](x)");
//	}

}
