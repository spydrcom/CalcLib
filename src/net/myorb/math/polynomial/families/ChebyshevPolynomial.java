
package net.myorb.math.polynomial.families;

import net.myorb.math.polynomial.families.chebyshev.*;

import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.PolynomialFamily;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

import java.util.List;

/**
 * support for Chebyshev polynomial based algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ChebyshevPolynomial<T> extends Polynomial<T>
			implements PolynomialFamily<T>
{


	public static final int
	FIRST_KIND = 1, SECOND_KIND = 2; 								// FIRST_KIND => T, SECOND_KIND => U
	public static final int Tn = FIRST_KIND, Un = SECOND_KIND;


	/**
	 * use polynomial arithmetic to generate coefficient patterns
	 * @param manager the type manager
	 */
	public ChebyshevPolynomial
	(SpaceManager<T> manager) { super (manager); init (); }
	public ChebyshevPolynomial () { super (null); }


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#init(net.myorb.math.SpaceManager)
	 */
	public void init (SpaceManager<T> manager)
	{ this.manager = manager; init (); }
	public void init ()
	{
		this.psm = new ChebyshevPolynomialSpaceManager<T>(manager);
		this.representX ();
	}
	protected ChebyshevPolynomialSpaceManager<T> psm;


	/**
	 * x is represented as (0, 1)
	 * and 2x is also kept in memory as (0, 2)
	 */
	void representX ()
	{
		this.X = psm.getPolynomialFunction
			(newCoefficients (manager.getZero (), manager.getOne ()));
		this.TWO_X = psm.times (manager.newScalar (2), X);
	}
	protected Polynomial.PowerFunction<T> X, TWO_X;


	/**
	 * the Chebyshev functions of specified kind
	 * @param kind first (T) = 1 or second (U) = 2
	 * @param upTo the number of indexed functions
	 * @return a list of the functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> recurrence (int kind, int upTo)
	{
		PolynomialFamilyManager.PowerFunctionList<T>  result =
			new PolynomialFamilyManager.PowerFunctionList<T> ();
		Polynomial.PowerFunction<T> pn, pnm2 = psm.newScalar (-1),
		pnm1 = seedRecurrence (kind, result);

		for (int n = 2; n <= upTo; n++)
		{
			pn = psm.add
				(psm.multiply (TWO_X, pnm1), pnm2);				// 2 * x * P[n - 1] - P[n - 2]
			pnm2 = psm.negate (pnm1);							// P[n - 2] = - P[n - 1]
			result.add (pn);									// P[n] added to list
			pnm1 = pn;											// P[n - 1] = P[n]
		}

		return result;
	}


	/**
	 * mutual recurrence algorithm allows
	 *  both T and U polynomial lists to be built
	 *  Efficiently in parallel reducing overhead
	 * @param upTo the number of indexed functions
	 * @param Tn the list built of Tn polynomials
	 * @param Un the list built of Un polynomials
	 */
	public void mutualRecurrence
		(
			int upTo,
			PolynomialFamilyManager.PowerFunctionList<T> Tn,
			PolynomialFamilyManager.PowerFunctionList<T> Un
		)
	{
		int n = 0;
		Polynomial.PowerFunction<T>
		xSqM1 = psm.add (psm.multiply (X, X), psm.newScalar (-1)),	// X^2 - 1 used to compute tNp1 & uNp1
		uNp1  = seedRecurrence (SECOND_KIND, Un),					// U[1] = 2x = (0, 2) { for kind = 2 (U) }
		tNp1  = seedRecurrence (FIRST_KIND, Tn);					// T[1] =  x = (0, 1) { for kind = 1 (T) }
		Polynomial.PowerFunction<T> tN, uNm1,						// convention used: tN = T[N], uNm1 = U[N-1], etc.
		uN = Un.get (n);											// U[0] = 1 (is used to compute T[2])

		while (n < upTo)
		{
			n++;													// N increments to next ordinal
			uNm1 = uN;												// U[n - 1] is set to old U[n]
			tN = tNp1;												// T[n] is set to old T[n + 1]
			uN = uNp1;												// U[n] is set to old U[n + 1]

			tNp1 = psm.multiply (xSqM1, uNm1);
			tNp1 = psm.add (tNp1, psm.multiply (X, tN));			// T[n + 1] = (x^2 - 1) U[n - 1] + x T[n]

			uNp1 = psm.multiply (X, uN);							// U[n + 1] = x U[n] + T[n + 1]
			uNp1 = psm.add (uNp1, tNp1);							//          = x(U[n] + T[n]) + (x^2 - 1) U[n - 1]
			
			Tn.add (tNp1);
			Un.add (uNp1);
		}
	}


	/**
	 * the basis for recurrence is first two polynomials.
	 *  the series of the two kinds have one small difference in seed polynomials.
	 *  exception is thrown for illegal KIND index, only 1 and 2 are legal.
	 * @param kind first (T) = 1 or second (U) = 2 series types
	 * @param result the seed list for the series given kind
	 * @return T1 = x or T1 = 2x (for first/second)
	 */
	public Polynomial.PowerFunction<T> seedRecurrence
		(
			int kind, List<Polynomial.PowerFunction<T>>  result
		)
	{
		result.add (psm.getOne ());								// P[0] =  1 = (1)
		Polynomial.PowerFunction<T> p;
		if (kind == Tn) result.add (p = X);						// P[1] =  x = (0, 1) { for kind = 1 (T) }
		else if (kind == Un) result.add (p = TWO_X);			// P[1] = 2x = (0, 2) { for kind = 2 (U) }
		else
		{
			throw new RuntimeException
			(
				"Illegal kind specified for Chebyshev recurrence algorithm"
			);
		}
		return p;
	}


	/**
	 * the the Chebyshev T (first kind) functions
	 * @param upTo the number of indexed functions
	 * @return a list of the functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> getT (int upTo)
	{
		return recurrence (Tn, upTo);
	}


	/**
	 * the Chebyshev U (second kind) functions
	 * @param upTo the number of indexed functions
	 * @return a list of the functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> getU (int upTo)
	{
		return recurrence (Un, upTo);
	}


	/**
	 * Clenshaw special case for Chebyshev
	 * @param a coefficients of the polynomial
	 * @param x value of parameter to polynomial
	 * @return result of the evaluation
	 */
	public Value<T> evaluatePolynomialV
	(Coefficients<T> a, Value<T> x)
	{
		int i;
		if ((i = a.size () - 1) < 2)
		{ throw new RuntimeException (MINIMUM_COEFFICIENTS); }
		Value<T> TWOX = forValue (2).times (x), bi = forValue (0);
		Value<T> bip1 = forValue (a.get (i)), bip2 = bi;			// b[i], b[i+1], b[i+2], etc.

		while (--i > 0)
		{
			bi =
				forValue (a.get (i)).minus (bip2)					// b(i) = a(i) + 2*x*b(i+1) - b(i+2)
				.plus (TWOX.times (bip1));
			bip2 = bip1; bip1 = bi;									// b(i+2) = b(i+1) ; b(i+1) = b(i)
		};															// i = 0 on exit

		Value<T> Pn =
			forValue (a.get (i)).minus (bip2)						// P(n) = a(0) + x*b(1) - b(2)
				.plus (x.times (bip1));
		return Pn;
	}
	static final String MINIMUM_COEFFICIENTS = "Clenshaw algorithm coded for minimum of 3 coefficients";


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getName()
	 */
	public String getName () { return "Chebyshev"; }


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getFunctions(java.lang.String, int)
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> getPolynomialFunctions (String identifier, int upTo)
	{
		return identifier.startsWith ("T")? getT (upTo): getU (upTo);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getIdentifier(java.lang.String)
	 */
	public String getIdentifier (String kind)
	{
		return kind.toUpperCase ().startsWith ("F")? "T": "U";
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#getPolynomialSpaceManager()
	 */
	public PolynomialSpaceManager<T> getPolynomialSpaceManager () { return psm; }


	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		Polynomial.RealNumbers mgr = new Polynomial.RealNumbers ();
		ChebyshevPolynomial<Double> C = new ChebyshevPolynomial<Double> (mgr);

		PolynomialFamilyManager.dump (C.getPolynomialFunctions ("T", 10), mgr);
		PolynomialFamilyManager.dump (C.getPolynomialFunctions ("U", 10), mgr);

		ChebyshevPolynomialSpaceManager<Double> psm = C.psm;
		PolynomialFamilyManager.dump (new ChebyshevRecurrenceFormula<Double> (psm, Tn), 10, mgr);
		PolynomialFamilyManager.dump (new ChebyshevRecurrenceFormula<Double> (psm, Un), 10, mgr);
	}


}

