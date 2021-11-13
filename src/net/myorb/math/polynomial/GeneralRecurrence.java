
package net.myorb.math.polynomial;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * general three part recurrence formula helper
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public abstract class GeneralRecurrence<T>
		extends PolynomialFamilyManager.PowerFunctionList<T>
{

	/*
	 * works on form:
	 * 
	 * 		f[n+1](x) c[n+1](x) = f[n](x) c[n](x) + f[n-1](x) c[n-1](x)
	 * 
	 */

	/**
	 * @param psm polynomial space manager for the data type
	 */
	public GeneralRecurrence
	(PolynomialSpaceManager<T> psm)
	{
		this.sm = (this.psm = psm).getSpaceDescription ();
		this.ZERO = con (0); this.ONE = con (1); this.TWO = con (2);
		this.variable = psm.newVariable ();
	}
	protected Polynomial.PowerFunction<T> ZERO, ONE, TWO;
	protected Polynomial.PowerFunction<T> variable;
	protected PolynomialSpaceManager<T> psm;
	protected SpaceManager<T> sm;


	/**
	 * seed the list of functions starting with f[0] and f[1]
	 */
	public abstract void seedRecurrence ();

	/**
	 * recurrence seed is set to 1, x
	 */
	public void useSimpleSeed ()
	{
		add (ONE); add (variable);
	}

	/**
	 * @param n the order of the polynomial
	 * @return the function of order n
	 */
	public abstract Polynomial.PowerFunction<T> functionOfN (int n);

	/**
	 * @param n the order of the polynomial
	 * @return the function of order n-1
	 */
	public abstract Polynomial.PowerFunction<T> functionOfNminus1 (int n);

	/**
	 * @param n the order of the polynomial
	 * @return the function of order n+1
	 */
	public abstract Polynomial.PowerFunction<T> functionOfNplus1 (int n);


	/**
	 * @param n the order of the polynomial
	 * @return c[n+1]
	 */
	public Polynomial.PowerFunction<T> functionAfter (int n)
	{
		Polynomial.PowerFunction<T> sum =
			add
			(
				multiply (functionOfN (n), get (n)),
				multiply (functionOfNminus1 (n), get (n-1))
			);
		Polynomial.PowerFunction<T> rem = ZERO;
		Polynomial.PowerFunction<T> q = psm.divide (sum, functionOfNplus1 (n), rem);

		//if (!psm.isZero (rem)) throw new RuntimeException ("Non-zero remainder on N+1 function division");
		if (!psm.isZero (rem)) System.out.println ("*** Recurrence generation " + n + " had non-zero remainder: " + rem.getCoefficients ().get (0));
		return q;
	}


	/**
	 * @param upTo the highest order to construct
	 * @return the list of functions
	 */
	public GeneralRecurrence<T> constructFuntions (int upTo)
	{
		seedRecurrence ();

		for (int n = 1; n < upTo; n++)
		{
			add (functionAfter (n));
		}

		return this;
	}

	/*
	 * shortcuts for scalar arithmetic
	 */

	protected T discrete (int value) { return sm.newScalar (value); }
	protected T productOf (T x, T y) { return sm.multiply (x, y); }
	protected T negOf (T value) { return sm.negate (value); }
	protected T sumOf (T x, T y) { return sm.add (x, y); }
	protected T inverseOf (T x) { return sm.invert (x); }

	/*
	 * shortcuts for polynomial arithmetic
	 */

	/**
	 * a constant function
	 * @param value (int) the value of the function
	 * @return function with given value
	 */
	protected Polynomial.PowerFunction<T> con (int value)
	{ return psm.constantFunction (discrete (value)); }

	/**
	 * a constant function
	 * @param value (T) the value of the function
	 * @return function with given value
	 */
	protected Polynomial.PowerFunction<T> con (T value)
	{ return psm.constantFunction (value); }

	protected Polynomial.PowerFunction<T> neg
	(Polynomial.PowerFunction<T> f)
	{ return psm.negate (f); }

	protected Polynomial.PowerFunction<T> add
	(Polynomial.PowerFunction<T> x, Polynomial.PowerFunction<T> y)
	{ return psm.add (x, y); }

	protected Polynomial.PowerFunction<T> multiply
	(Polynomial.PowerFunction<T> x, Polynomial.PowerFunction<T> y)
	{ return psm.multiply (x, y); }

	protected Polynomial.PowerFunction<T> times
	(T c, Polynomial.PowerFunction<T> y)
	{ return psm.times (c, y); }

	private static final long serialVersionUID = 1L;
}

