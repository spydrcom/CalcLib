
package net.myorb.math.computational;

import net.myorb.math.*;
import net.myorb.math.complexnumbers.FunctionAnalyzer;
import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;

import java.math.BigInteger;
import java.util.List;

/**
 * algorithms for computation of roots of polynomial equations
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class PolynomialRoots<T> extends OrdinaryPolynomialCalculus<T>
	implements FunctionAnalyzer<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 * @param lib an implementation of the power library
	 */
	public PolynomialRoots
		(SpaceManager<T> manager, PowerLibrary<T> lib)
	{
		super (manager); setToleranceDefaults (lib);
		this.functionRoots = new FunctionRoots<T> (manager, lib);
	}
	protected FunctionRoots<T> functionRoots;


	/**
	 * compute solutions for ax^2 + bx + c = 0
	 * @param a coefficient of x^2 in the equation
	 * @param b coefficient of x
	 * @param c constant c
	 * @return roots list
	 */
	@SuppressWarnings("unchecked")
	public List<T> quadratic (T a, T b, T c)
	{
		Value<T> A = forValue (a), B = forValue (b), C = forValue (c);
		Value<T> twoA = forValue (2).times (A), negativeB = negative (B);
		Value<T> D = B.squared ().minus (productSeries (forValue (4), A, C));		// known as the "Discriminant" value
		List<T> roots = newList ();
		
		if (D.isZero ())
		{
			addToList (roots, negativeB.over (twoA));								// Discriminant is zero so single simple root returned
		}
		else
		{
			Value<T> sqrtD = null;													// sqrt (-real domain) will raise exception for illegal operation
			try { sqrtD = sqrt (D); }												// negative Discriminant will cause complex roots here
			catch (Exception e) { badDiscriminant (D, e); return roots; }			// non-complex domains will see no roots

			Value<T> r1 = negativeB.minus (sqrtD).over (twoA);
			Value<T> r2 = negativeB.plus (sqrtD).over (twoA);

			if (r1.isLessThan (r2))
			{ addToList (roots, r1, r2); }
			else { addToList (roots, r2, r1); }
		}

		return roots;
	}


	/**
	 * alternate representation is c(0) + c(1)*x + c(2)*x^2
	 * @param coefficients the coefficients of the terms of the polynomial
	 * @return the computed list of roots
	 */
	public List<T> quadratic (Coefficients<T> coefficients)
	{
		if (coefficients.size () != 3) throw new RuntimeException ("Quadratic equation requires 3 coefficients");
		return quadratic (coefficients.get (2), coefficients.get (1), coefficients.get (0));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.FunctionAnalyzer#analyze(net.myorb.math.Polynomial.PowerFunction)
	 */
	public List<T> analyze (Polynomial.PowerFunction<T> polynomial)
	{
		return quadratic (polynomial.getCoefficients ());
	}


	/**
	 * find function root using Laguerre Method
	 * @param polynomial the polynomial function description
	 * @param starting starting value to use as approximate root of function
	 * @return the value of the root found
	 */
	public T laguerreMethod
		(
			PowerFunction<T> polynomial, T starting
		)
	{
		PowerFunction<T>
			polynomialD1 = getFunctionDerivative (polynomial),
			polynomialD2 = getFunctionDerivative (polynomialD1);
		Value<T> x = forValue (starting), n = forValue (polynomial.getDegree ()), nm1 = n.minus (forValue (1));
		T xT = starting; Value<T> p, pPrime, pPrime2, G, G2, H, sqrt, a, a1, a2;
		for (int i = getMaxIterations (); i > 0; i--)
		{
			if ((p = forValue (polynomial.eval (xT))).isZero ()) return xT;	// compute polynomial value, return if root found

			 pPrime  = forValue (polynomialD1.eval (xT));					// value of first derivative
			pPrime2  = forValue (polynomialD2.eval (xT));					// value of second derivative
			      G  = pPrime.over (p); G2 = G.squared ();					// value of G per Laguerre
			      H  = G2.minus (pPrime2.over (p));							// value of H per Laguerre
			   sqrt  = sqrt (nm1.times (n.times (H).minus (G2)));

			a1 = n.over (G.plus (sqrt)); a2 = n.over (G.minus (sqrt));
			a = abs (a1).isLessThan (abs (a2))? a1: a2;

			if (withinTolerance (a)) return xT;								// computed value of "a" is Laguerre offset from Xn
			xT = (x = x.minus (a)).getUnderlying ();						// x(n+1) = x(n) - a; xT is x in underlying representation
		}
		return raiseException ("Failed to converge");
	}


	/**
	 * find function root using Laguerre Method
	 * @param coefficients the list of coefficients of the polynomial
	 * @param starting starting value to use as approximate root of function
	 * @return the value of the root found
	 */
	public T laguerreMethod (Coefficients<T> coefficients, T starting)
	{ return laguerreMethod (getPolynomialFunction (coefficients), starting); }


	/**
	 * use Newton's method to determine root
	 * @param polynomial the function being evaluated for roots
	 * @param x the value of X to use for the evaluation, best approximation of root
	 * @return the value of the root found
	 */
	public T newtonRaphsonMethod (PowerFunction<T> polynomial, T x)
	{
		return functionRoots.newtonRaphsonMethod
		(
			polynomial, getFunctionDerivative (polynomial), x
		);
	}


	/**
	 * compute roots using Newton's method with defaults
	 * @param coefficients the list of coefficients of the polynomial
	 * @param x the value of X to use for the evaluation, best approximation of root
	 * @return value of the root found
	 */
	public T newtonRaphsonMethod (Coefficients<T> coefficients, T x)
	{
		return newtonRaphsonMethod
		(
			getPolynomialFunction (coefficients), x
		);
	}


	/**
	 * do fast SQRT computation using Newton/Raphson
	 * @param x the value to find SQRT of
	 * @return computed SQRT value
	 */
	@SuppressWarnings("unchecked")
	public T fastSqrt (T x)
	{
		Coefficients<T> coef = new Coefficients<T> ();
		addToList (coef, neg (x), discrete (0), discrete (1));
		BigInteger value = BigInteger.valueOf (manager.toNumber (x).longValue ());
		T approximatedRoot = powerOf (discrete (2), value.bitLength () / 2);
		return newtonRaphsonMethod (coef, approximatedRoot);
	}


	/**
	 * find all function roots relative to derivative function zeros
	 * @param derivativeZeros the list of function zeros found for the derivative
	 * @param f the function object being evaluated
	 * @param dNumber number of derivatives deep
	 * @return zeros found for function
	 */
	@SuppressWarnings("unchecked")
	public List<T> locateFunctionRoots
	(List<T> derivativeZeros, Polynomial.PowerFunction<T> f, int dNumber)
	{
		List<T> roots = newList ();
		if (derivativeZeros.size() == 0)
		{
			throw new RuntimeException ("No function zeroes found");
		}
		T nthZero = derivativeZeros.get (0);
		Coefficients<T> coefficients = f.getCoefficients ();
		T nthApproximation = subtract (nthZero, offsetFromZeroAt (nthZero));
		T nthRoot = findRootLocatedNear (nthApproximation, coefficients, dNumber);
		T nextZero = nthZero, nextRoot = null;
		addToRoots (nthRoot, roots);

		for (int i = 1; i < derivativeZeros.size (); i++)
		{
			nextZero = derivativeZeros.get (i);
			nextRoot = findRootLocatedBetween (nthZero, nextZero, f, dNumber);
			addToRoots (nextRoot, roots);
			nthZero = nextZero;
		}

		nthApproximation = sumOf (nthZero, offsetFromZeroAt (nthZero));
		nthRoot = findRootLocatedNear (nthApproximation, coefficients, dNumber);
		addToRoots (nthRoot, roots); displayDerivativeRoots (roots, f, dNumber);
		return roots;
	}


	/**
	 * evaluate derivative polynomial and find zeroes
	 * @param coefficients the coefficients that define the function
	 * @return a list of zeroes found for the derivative
	 */
	public List<T> computeDerivativeZeroes (Coefficients<T> coefficients)
	{ return evaluateEquation (computeDerivativeCoefficients (coefficients)); }

	/**
	 * evaluate derivative polynomial and find zeroes...
	 *  also include zeroes of other derivatives to increase possible bisection range count
	 * @param coefficients the coefficients that define the function
	 * @param dNumber number of derivatives deep
	 * @return zeroes found for the derivative
	 */
	public List<T> computeAllDerivativeZeroes (Coefficients<T> coefficients, int dNumber)
	{ return evaluateEquation (computeDerivativeCoefficients (coefficients), true, dNumber + 1); }


	/**
	 * add root to list avoiding duplicates.
	 *  list is maintained in increasing order.
	 *  order is necessary so that lo LT hi in sequence
	 * @param root the root value to be added to list
	 * @param roots the list of roots being accumulated
	 */
	public void addToRoots (T root, List<T> roots)
	{
		if (root != null)
		{
			int count;
			if ((count = roots.size ()) > 0)
			{
				T lastRoot = roots.get (count - 1);
				if (isLessThan (lastRoot, root))
				{
					if (isDuplicate (lastRoot, root)) return;
				}
				else
				{
					for (int i = 0; i < count; i++)
					{
						lastRoot = roots.get (i);
						if (isDuplicate (lastRoot, root)) return;
						if (this.isLessThan (root, lastRoot))
						{ roots.add (i, root); return; }
					}
				}
			}
			roots.add (root);
		}
	}

	/**
	 * add a list of roots into another observing duplicate rules
	 * @param additions the additions to be made to the roots list
	 * @param roots the current list of roots
	 */
	void addToRoots (List<T> additions, List<T> roots)
	{
		for (T addition : additions) addToRoots (addition, roots);
	}

	/**
	 * compare two roots and ignore addition if within tolerance of duplicate
	 * @param root the new root value to check against root list
	 * @param possibleMatch item from list to be compared
	 * @return TRUE => is duplicate
	 */
	boolean isDuplicate (T root, T possibleMatch)
	{
		T difference = subtract (possibleMatch, root);
		if (withinTolerance (difference))
		{
			display ("*** duplicate root at " + root, null);
			return true;
		}
		return false;
	}


	/**
	 * use newton method to find roots beyond outside prime zeroes
	 * @param approximation a value near an outer prime zero for use as approximate root
	 * @param usingCoefficients the coefficient list defining the equation
	 * @param dNumber number of derivatives deep
	 * @return root if found
	 */
	public T findRootLocatedNear (T approximation, Coefficients<T> usingCoefficients, int dNumber)
	{
		T result; String dMsg = dNumber!=0? " [f'"+dNumber+"]": "";
		try { result = newtonRaphsonMethod (usingCoefficients, approximation); }
		catch (Exception e) { display ("*** no root near " + approximation + dMsg, e); return null; }
		display ("+++ root found near " + approximation + " @ " + result + dMsg, null);
		return result;
	}


	/**
	 * use bisection to find root between prime zeroes
	 * @param nthZero the lower prime zero of the range
	 * @param nextZero the higher prime zero
	 * @param inFunction being evaluated
	 * @param dNumber count derivatives
	 * @return root if found
	 */
	public T findRootLocatedBetween (T nthZero, T nextZero, Function<T> inFunction, int dNumber)
	{
		T result; String dMsg = dNumber!=0? " [f'"+dNumber+"]": "";
		try { result = functionRoots.bisectionMethod (inFunction, nthZero, nextZero); }
		catch (Exception e) { display ("*** no root between " + nthZero + " & " + nextZero + dMsg, e); return null; }
		display ("+++ root found between " + nthZero + " & " + nextZero + " @ " + result + dMsg, null);
		return result;
	}


	/**
	 * display exception associated with root location
	 * @param context a text message to establish context
	 * @param e the exception that was caught
	 */
	public void display (String context, Exception e)
	{
		if (!TRACE) return;
		System.out.println (context);
		if (e != null) System.out.println ("*** " + e.getMessage ());
	}
	public void displayDerivativeRoots
	(List<T> derivativeZeros, Polynomial.PowerFunction<T> f, int dNumber)
	{
		if (!TRACE) return;
		if (dNumber == 0) return;
		f.getPolynomialSpaceManager ().show (f);
		System.out.println ("derivative " + dNumber + " " + derivativeZeros);
	}
	static final boolean TRACE = false;


	/**
	 * error displayed from quadratic equation.
	 *  for complex roots generated in REAL domain
	 * @param D value of the discriminant
	 * @param e the exception seen
	 */
	public void badDiscriminant (Value<T> D, Exception e)
	{
		display ("*** Discriminant out of range [" + D + "]", e);
	}


	/**
	 * for polynomials of degree GT 2
	 * @param coefficients the list of coefficients that define the equation
	 * @param includeDerivatives the derivatives found zeroes should be in list
	 * @param dNumber number of derivatives deep
	 * @return the list of roots found
	 */
	public List<T> highOrderPolynomialRoots
		(Coefficients<T> coefficients, boolean includeDerivatives, int dNumber)
	{
		List<T> derivativeZeros =
			computeAllDerivativeZeroes (coefficients, dNumber);
		if (derivativeZeros.size () == 0) return derivativeZeros;
		Polynomial.PowerFunction<T> f = getPolynomialFunction (coefficients);
		List<T> functionZeroes = locateFunctionRoots (derivativeZeros, f, dNumber);
		if (includeDerivatives) addToRoots (derivativeZeros, functionZeroes);
		return functionZeroes;
	}


	/**
	 * 1 degree polynomial describes a line which may have one root
	 * @param coefficients the list of coefficients that define the equation
	 * @return the list of roots found
	 */
	public List<T> singleOrderPolynomialRoots (Coefficients<T> coefficients)
	{
		List<T> roots;
		T c0 = coefficients.get (0),
				c1 = coefficients.get (1);
		if (isZro (c1)) raiseException (ONE_DEGREE);
		(roots = newList ()).add (divide (neg (c0), c1));
		return roots;
	}
	public static final String ONE_DEGREE = "Coefficient of X is zero in polynomial of 1 degree";


	/**
	 * evaluate polynomial for methods of computing roots
	 * @param coefficients the list of coefficients that define the equation
	 * @param includeDerivatives the derivatives found zeroes should be in list
	 * @param dNumber number of derivatives deep
	 * @return the list of roots found
	 */
	public List<T> evaluateEquation (Coefficients<T> coefficients, boolean includeDerivatives, int dNumber)
	{
		switch (coefficients.size ())
		{
			case  3: return quadratic (coefficients);
			case  2: return singleOrderPolynomialRoots (coefficients);
			case  1: raiseException ("Function is constant, no roots found");
			case  0: raiseException ("Polynomial not defined, no coefficients specified");
			default: return highOrderPolynomialRoots (coefficients, includeDerivatives, dNumber);
		}
	}


	/**
	 * evaluate polynomial for methods of computing roots
	 * @param coefficients the list of coefficients that define the equation
	 * @return the list of roots found
	 */
	public List<T> evaluateEquation (Coefficients<T> coefficients)
	{
		return evaluateEquation (coefficients, false, 0);
	}


	/**
	 * evaluate polynomial for methods of computing roots
	 * @param polynomial the polynomial represented as a power function
	 * @return the list of roots found
	 */
	public List<T> evaluateEquation (PowerFunction<T> polynomial)
	{
		return evaluateEquation (polynomial.getCoefficients ());
	}


}

