
package net.myorb.math.polynomial.families;

import net.myorb.math.polynomial.PolynomialFamily;
import net.myorb.math.polynomial.GeneralRecurrence;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.PolynomialFamilyManager;

import net.myorb.math.specialfunctions.Library;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

import java.util.List;

/**
 * support for Hermite polynomial based algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class HermitePolynomial<T> extends Polynomial<T>
			implements PolynomialFamily<T>
{

	public HermitePolynomial
	(SpaceManager<T> manager) { super (manager); init (); }
	public HermitePolynomial () { super (null); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#init(net.myorb.math.SpaceManager)
	 */
	public void init (SpaceManager<T> manager)
	{ this.manager = manager; init (); }
	public void init ()
	{
		this.psm = new HermitePolynomialSpaceManager<T>(manager);
		this.representX ();
	}
	protected PolynomialSpaceManager<T> psm;

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
	 * @param functions the list being built
	 */
	public void seedRecurrence
	(List<Polynomial.PowerFunction<T>> functions)
	{ functions.add (psm.getOne ()); functions.add (TWO_X); }

	/**
	 * use function inter-dependencies to generate series
	 * @param upTo highest order of functions to be generated
	 * @return the list of generated functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> recurrence (int upTo)
	{
		PolynomialFamilyManager.PowerFunctionList<T>  H =
			new PolynomialFamilyManager.PowerFunctionList<T> ();
		// H[n+1](x) = 2*x*H[n](x) - 2*n*H[n-1](x)
		seedRecurrence (H);

		for (int n = 1; n < upTo; n++)
		{
			H.add
			(
				psm.add
				(
					psm.times (manager.newScalar (-2*n), H.get (n-1)),
					psm.multiply (TWO_X, H.get (n))
				)
			);
		}

		return H;
	}

	/**
	 * calculate each coefficient independently
	 * @param upTo highest order of functions to be generated
	 * @return the list of generated functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> bruteForce (int upTo)
	{
		PolynomialFamilyManager.PowerFunctionList<T>  result =
					new PolynomialFamilyManager.PowerFunctionList<T> ();
		// Hn(x) = SIGMA [ 0 <= k <= n/2 ] ( (-1)^k * n! / ( k! * (n - 2*k)! ) * (2*x)^(n - 2*k) )
		Polynomial.PowerFunction<T> Hn;
		seedRecurrence (result);

		for (int n = 2; n <= upTo; n++)
		{
			Hn = psm.getZero ();

			for (int k = 0; k <= n / 2; k++)
			{
				int nm2k = n - 2 * k;
				Hn = psm.addTermFor (coef (n, k, nm2k), TWO_X, nm2k, Hn);
			}

			result.add (Hn);
		}

		return result;
	}
	T coef (int n, int k, int nm2k)
	{
		T kf = factorial (k), df = factorial (nm2k),
			num = manager.multiply (sgn (k), factorial (n)),
			den = manager.invert (manager.multiply (kf, df));
		return manager.multiply (num, den);
	}
	T factorial (int n) { return Library.factorialT (n, manager); }
	T sgn (int n) { return Library.signT (n, manager); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getName()
	 */
	public String getName () { return "Hermite"; }

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
	public String getIdentifier (String kind) { return "H"; }

	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		Polynomial.RealNumbers mgr = new Polynomial.RealNumbers ();

		HermitePolynomialSpaceManager<Double> psm = new HermitePolynomialSpaceManager<Double>(mgr);
		HermitePolynomial<Double> H = new HermitePolynomial<Double> (mgr);

		PolynomialFamilyManager.dump (H.recurrence (10), mgr);
		PolynomialFamilyManager.dump (H.bruteForce (10), mgr);

		PolynomialFamilyManager.dump
		(
			new HermiteRecurrenceFormula<Double> (psm), 10, mgr
		);
	}

}

class HermiteRecurrenceFormula<T> extends GeneralRecurrence<T>
{
	// H[n+1](x) = 2*x*H[n](x) - 2*n*H[n-1](x)

	public HermiteRecurrenceFormula (PolynomialSpaceManager<T> psm)
	{
		super (psm);
	}
	public void seedRecurrence ()
	{
		add (ONE); add (multiply (TWO, variable));
	}
	public Polynomial.PowerFunction<T> functionOfN (int n)
	{
		return multiply (TWO, variable);
	}
	public Polynomial.PowerFunction<T> functionOfNminus1 (int n)
	{
		return con (-2 * n);
	}
	public Polynomial.PowerFunction<T> functionOfNplus1 (int n)
	{
		return ONE;
	}
	private static final long serialVersionUID = 1L;
}

class HermitePolynomialSpaceManager<T> extends PolynomialSpaceManager<T>
{

	/**
	 * @param manager data type manager is required
	 */
	public HermitePolynomialSpaceManager
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
//		// then the Hermite H function of (x) replaces the traditional x^n
//		buffer.append ("H[").append (termNo).append ("](x)");
//	}

}
