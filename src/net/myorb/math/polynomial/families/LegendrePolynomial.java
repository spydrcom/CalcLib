
package net.myorb.math.polynomial.families;

import net.myorb.math.polynomial.*;
import net.myorb.math.polynomial.families.legendre.*;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

import java.util.List;

/**
 * support for Legendre polynomial based algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LegendrePolynomial<T> extends Polynomial<T>
			implements PolynomialFamily<T>
{

	public LegendrePolynomial
	(SpaceManager<T> manager) { super (manager); init (); }
	public LegendrePolynomial () { super (null); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#init(net.myorb.math.SpaceManager)
	 */
	public void init (SpaceManager<T> manager)
	{ this.manager = manager; init (); }
	public void init ()
	{
		this.psm = new LegendrePolynomialSpaceManager<T>(manager);
		this.representX ();
	}
	protected PolynomialSpaceManager<T> psm;
	
	/**
	 * x is represented as (0, 1)
	 */
	void representX ()
	{
		this.X = psm.getPolynomialFunction
		(newCoefficients (manager.getZero (), manager.getOne ()));
	}
	protected Polynomial.PowerFunction<T> X;

	/**
	 * @param functions the list being built
	 */
	public void seedRecurrence
	(List<Polynomial.PowerFunction<T>> functions)
	{
		functions.add (psm.getOne ()); functions.add (X);
	}

	/**
	 * use function inter-dependencies to generate series P
	 * @param upTo highest order of functions to be generated
	 * @return the list of generated functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> recurrenceP (int upTo)
	{
		PolynomialFamilyManager.PowerFunctionList<T>  P =
				new PolynomialFamilyManager.PowerFunctionList<T> ();
		// (n+1) * P[n+1](x) - (2n+1)*x*P[n](x) + nP[n-1](x) = 0
		seedRecurrence (P);

		for (int n = 1; n < upTo; n++)
		{
			P.add
			(
				psm.times
				(
						manager.invert (manager.newScalar (n + 1)),
						psm.add
						(
							psm.times (manager.newScalar (-n), P.get (n - 1)),
							psm.times (manager.newScalar (2*n+1), psm.multiply (X, P.get (n)))
						)
				)
			);
		}
	
		return P;
	}

	/**
	 * use function inter-dependencies to generate series Q
	 * @param upTo highest order of functions to be generated
	 * @return the list of generated functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> recurrenceQ (int upTo)
	{
		PolynomialFamilyManager.PowerFunctionList<T>  result =
				new PolynomialFamilyManager.PowerFunctionList<T> ();
		// Q[l](x) = 1/2 * INTEGRAL [ -1 <= y <= 1 ] ( P[l](y) / (x - y) * dy )
		// Q[n+1](x) = ( (2n+1)*x*Q[n](x) - nQ[n-1](x) ) / (n+1)
		// Q0 = 1/2 ln ( (1+x)/(1-x) ), Q1 = xQ0 - 1
		//TODO: HELP!

		Polynomial.PowerFunction<T>
			Qnp1, Qn = X, Qnm1 = psm.getOne ();
		result.add (Qnm1);						// Q0 = 1/2 ln ( (1+x)/(1-x) )
		result.add (Qn);						// Q1 = xQ0 - 1
		int n = 1;

		for (int i = 2; i <= upTo; i++)
		{
			Qnp1 =
				psm.add
				(
					psm.times (manager.newScalar (-n), Qnm1),
					psm.times (manager.newScalar (2*n+1), psm.multiply (X, Qn))
				);
			Qnp1 = psm.times (manager.invert (manager.newScalar (n+1)), Qnp1);
					
			result.add (Qnp1);
			Qnm1 = Qn; Qn = Qnp1;
			n++;
		}
	
		return result;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getName()
	 */
	public String getName () { return "Legendre"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getFunctions(java.lang.String, int)
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> getPolynomialFunctions (String identifier, int upTo)
	{
		if (identifier.toUpperCase ().startsWith ("C"))
			return new LegendreGegenbauerRecurrenceFormula<T>(psm).constructFuntions (upTo);
		else if (identifier.toUpperCase ().startsWith ("J"))
			return new LegendreJacobiRecurrenceFormula<T>(psm).constructFuntions (upTo);
		return identifier.startsWith ("P")? recurrenceP (upTo): recurrenceQ (upTo);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getIdentifier(java.lang.String)
	 */
	public String getIdentifier (String kind)
	{
		if (kind.toUpperCase ().startsWith ("C")) return "C";
		else if (kind.toUpperCase ().startsWith ("J")) return "J";
		return kind.toUpperCase ().startsWith ("F")? "P": "Q";
	}

	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		Polynomial.RealNumbers mgr = new Polynomial.RealNumbers ();
		LegendrePolynomialSpaceManager<Double> psm = new LegendrePolynomialSpaceManager<Double> (mgr);
		LegendrePolynomial<Double> L = new LegendrePolynomial<Double> (mgr);

		PolynomialFamilyManager.dump (L.recurrenceP (10), mgr);
		//PolynomialFamilyManager.dump (L.recurrenceQ (10), mgr);

		PolynomialFamilyManager.dump (usingSimpleRecurrence (psm), 10, mgr);
		PolynomialFamilyManager.dump (usingGegenbauer (psm), 10, mgr);
		PolynomialFamilyManager.dump (usingJacobi (psm), 10, mgr);
	}

	public static <T> GeneralRecurrence<T>
		usingJacobi (PolynomialSpaceManager<T> psm)
	{ return new LegendreJacobiRecurrenceFormula<T>(psm); }

	public static <T> GeneralRecurrence<T>
		usingGegenbauer (PolynomialSpaceManager<T> psm)
	{ return new LegendreGegenbauerRecurrenceFormula<T>(psm); }

	public static <T> GeneralRecurrence<T>
		usingSimpleRecurrence (PolynomialSpaceManager<T> psm)
	{ return new LegendreRecurrenceFormula<T>(psm); }

}

