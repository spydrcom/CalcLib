
package net.myorb.math.polynomial.families;

import net.myorb.math.polynomial.families.gegenbauer.*;

import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.PolynomialFamily;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * support for Gegenbauer polynomial based algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class GegenbauerPolynomial<T> extends Polynomial<T>
			implements PolynomialFamily<T>
{

	public GegenbauerPolynomial
	(SpaceManager<T> manager, T lambda)
	{ super (manager); init (); this.lambda = lambda; }
	public GegenbauerPolynomial () { super (null); }
	protected T lambda;
	
	/* (non-Javadoc)
	* @see net.myorb.math.PolynomialFamily#init(net.myorb.math.SpaceManager)
	*/
	public void init (SpaceManager<T> manager)
	{ this.manager = manager; init (); }
	public void init ()
	{
		this.psm = new GegenbauerPolynomialSpaceManager<T>(manager);
	}
	protected PolynomialSpaceManager<T> psm;
	
	/**
	* use function inter-dependencies to generate series
	* @param upTo highest order of functions to be generated
	* @return the list of generated functions
	*/
	public PolynomialFamilyManager.PowerFunctionList<T> recurrence (int upTo)
	{
		// recurrence formula degenerates for lambda=0, accept definition as Chebyshev T
		if (manager.isZero (lambda)) return new ChebyshevPolynomial<T>(manager).getT (upTo);
		return new SimpleGegenbauerRecurrenceFormula<T> (psm, lambda).constructFuntions (upTo);
	}

	/* (non-Javadoc)
	* @see net.myorb.math.PolynomialFamily#getName()
	*/
	public String getName () { return "Gegenbauer"; }

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
	public String getIdentifier (String kind)
	{
		lambda =
			JacobiPolynomial.parseParameter (kind, manager);
		return "C";
	}

	/**
	* unit test
	* @param args not used
	*/
	public static void main (String[] args)
	{
		Polynomial.RealNumbers mgr = new Polynomial.RealNumbers ();
		GegenbauerPolynomial<Double> C = new GegenbauerPolynomial<Double> (mgr, 0.5);
		PolynomialFamilyManager.dump (C.recurrence (10), mgr);
	}

}

/**
 * using recurrence seed 1, 2 lambda X
 * @param <T> type on which operations are to be executed
 */
class SimpleGegenbauerRecurrenceFormula<T> extends GegenbauerRecurrenceFormula<T>
{

	//
	//	(n+1)C[lambda,n+1](x) - 2 (lambda+n) x C[lambda,n](x) + (2 lambda + n - 1) C[lambda,n-1](x) = 0
	//
	//	(n + 2 lambda) C[lambda,n](x) - 2 lambda C[lambda+1,n](x) + 2 lambda x C[lambda+1,n-1](x) = 0
	//
	//	(n+1) C[lambda,n+1](x) - 2 lambda x C[lambda+1,n](x) + 2 lambda C[lambda+1,n-1](x) = 0
	//

	public SimpleGegenbauerRecurrenceFormula
	(PolynomialSpaceManager<T> psm, T lambda)
	{ super (psm); setLambda (lambda); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.families.gegenbauer.GegenbauerRecurrenceFormula#seedRecurrence()
	 */
	public void seedRecurrence ()
	{
		add (ONE); add (multiply (times (lambdaValue, TWO), variable));
	}

	private static final long serialVersionUID = -214642895928363453L;
}

