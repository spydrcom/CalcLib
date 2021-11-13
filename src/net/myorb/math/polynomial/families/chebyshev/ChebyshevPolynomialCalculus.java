
package net.myorb.math.polynomial.families.chebyshev;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.polynomial.PolynomialCalculusSupport;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

import net.myorb.data.abstractions.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * compute derivatives and anti-derivatives for Chebyshev polynomials
 * @param <T> the types of coefficients in the polynomial terms
 * @author Michael Druckman
 */
public class ChebyshevPolynomialCalculus<T> extends ChebyshevPolynomial<T>
	implements PolynomialCalculusSupport<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public ChebyshevPolynomialCalculus (SpaceManager<T> manager)
	{
		super (manager);
	}


	/**
	 * get the power function
	 *  that is the derivative of the Nth T polynomial
	 * @param n the index of the T function to be integrated
	 * @param Un the first N functions Un
	 * @return the integral as function
	 */
	public Polynomial.PowerFunction<T> getDerivativeT
	(int n, List<Polynomial.PowerFunction<T>> Un)
	{
		if (n == 0) return psm.getZero ();
		else if (n == 1) return psm.getOne ();
		return psm.times (manager.newScalar (n), Un.get (n-1));
	}


	/**
	 * get the top N derivatives of Tn
	 * @param upTo the number of Tn derivative polynomials to be computed
	 * @return a list of the top N derivative polynomials of Tn
	 */
	public List<Polynomial.PowerFunction<T>> getDerivativeT (int upTo)
	{
		List<Polynomial.PowerFunction<T>>
			TderivativeN = new ArrayList<Polynomial.PowerFunction<T>> ();
		List<Polynomial.PowerFunction<T>> Us = getU (upTo);
		for (int i = 0; i <= upTo; i++)
		{
			TderivativeN.add (getDerivativeT (i, Us));
		}
		return TderivativeN;
	}


	/**
	 * given a Chebyshev polynomial with specified coefficients
	 *  compute the derivative of the function at specified x and return value
	 * @param a the coefficients of the Chebyshev polynomial being evaluated
	 * @param derivatives a list of the top derivative forms of T
	 * @param atX the value of x at which to evaluate 
	 * @return the value of the derivative at x
	 */
	public T evaluatePolynomialDerivative
	(Coefficients<T> a, List<Polynomial.PowerFunction<T>> derivatives, T atX)
	{
		T sum = manager.getZero (), term;
		for (int i = 0; i < a.size (); i++)
		{
			term = manager.multiply
				(a.get (i), derivatives.get (i).eval (atX));
			sum = manager.add (sum, term);
		}
		return sum;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialDerivative(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object)
	 */
	public T evaluatePolynomialDerivative (Coefficients<T> a, T atX)
	{ return evaluatePolynomialDerivative (a, getDerivativeT (a.size ()), atX); }


	/**
	 * get the power function
	 *  that is the integral of the Nth U polynomial
	 * @param n the index of the U function to be integrated
	 * @param Tn the first N functions Tn to use as source
	 * @return the anti-derivative as function
	 */
	public Polynomial.PowerFunction<T> getIntegralU
	(int n, List<Polynomial.PowerFunction<T>> Tn)
	{
		if (n == 0) return X;
		else if (n == 1) return psm.multiply (X, X);
		T oOnP1 = manager.invert (manager.newScalar (n + 1));
		return psm.times (oOnP1, Tn.get (n + 1));
	}


	/**
	 * get the top N integrals of Un
	 * @param upTo the number of Tn derivative polynomials to be computed
	 * @return a list of the top N derivative polynomials of Un
	 */
	public List<Polynomial.PowerFunction<T>> getIntegralU (int upTo)
	{
		List<Polynomial.PowerFunction<T>>
			UintegralN = new ArrayList<Polynomial.PowerFunction<T>> ();
		List<Polynomial.PowerFunction<T>> Ts = getT (upTo + 1);
		for (int i = 0; i <= upTo; i++)
		{
			UintegralN.add (getIntegralU (i, Ts));
		}
		return UintegralN;
	}


	/**
	 * given a Chebyshev polynomial with specified coefficients
	 *  compute the integral of the function at specified x and return value
	 * @param a the coefficients of the Chebyshev polynomial being evaluated
	 * @param integrals a list of the top integral forms of T
	 * @param atX the value of x at which to evaluate 
	 * @return the value of the integral at x
	 */
	public T evaluatePolynomialIntegralU
	(Coefficients<T> a, List<Polynomial.PowerFunction<T>> integrals, T atX)
	{
		T sum = manager.getZero (), term;
		for (int i = 0; i < a.size (); i++)
		{
			term = manager.multiply
				(a.get (i), integrals.get (i).eval (atX));
			sum = manager.add (sum, term);
		}
		return sum;
	}


	/**
	 * given a Chebyshev polynomial with specified coefficients
	 *  compute the integral of the function at specified x and return value
	 * @param a the coefficients of the Chebyshev polynomial
	 * @param atX the value of x at which to evaluate 
	 * @return the value of the integral at x
	 */
	public T evaluatePolynomialIntegralU (Coefficients<T> a, T atX)
	{ return evaluatePolynomialIntegralU (a, getIntegralU (a.size ()), atX); }


	/**
	 * get the power function
	 *  that is the integral of the Nth T polynomial
	 * @param n the index of the T function to be integrated
	 * @param Tn the first N functions Tn
	 * @return the integral as function
	 */
	public Polynomial.PowerFunction<T> getIntegralT
	(int n, List<Polynomial.PowerFunction<T>> Tn)
	{
		if (n == 0) return X;
		else if (n == 1)
		{
			T half = manager.invert (manager.newScalar (2));
			Polynomial.PowerFunction<T> xSq = psm.multiply (X, X);
			return psm.times (half, xSq);
		}

		int nm1 = n - 1;
		T nt = manager.newScalar (n),							// n
			nm1t = manager.newScalar (nm1),						// n - 1
			nsm1t = manager.newScalar (nm1 * (n + 1)),			// n^2 - 1
			oonsm1t = manager.invert (nsm1t),					// 1 / (n^2 - 1)
			nonsm1t = manager.multiply (nt, oonsm1t),			// n / (n^2 - 1)
			oonm1 = manager.invert (nm1t);						// 1 / (n - 1)
		Polynomial.PowerFunction<T>
		p1 = psm.times (nonsm1t, Tn.get (n+1)),					// T[n+1](x) * n /(n^2-1)
		p2 = psm.times (oonm1, psm.multiply (X, Tn.get (n)));	// T[n](x) * x / (n-1)
		return psm.add (p1, psm.negate (p2));					// p1 - p2
	}


	/**
	 * get the top N integrals of Tn
	 * @param upTo the number of Tn polynomials to be integrated
	 * @return a list of the top N Tn anti-derivative polynomials
	 */
	public List<Polynomial.PowerFunction<T>> getIntegralT (int upTo)
	{
		List<Polynomial.PowerFunction<T>>
			TintegratedN = new ArrayList<Polynomial.PowerFunction<T>> ();
		List<Polynomial.PowerFunction<T>> Ts = getT (upTo + 1);
		for (int i = 0; i <= upTo; i++)
		{
			TintegratedN.add (getIntegralT (i, Ts));
		}
		return TintegratedN;
	}


	/**
	 * given a Chebyshev polynomial with specified coefficients
	 *  compute the integral of the function at specified x and return value
	 * @param a the coefficients of the Chebyshev polynomial being integrated
	 * @param integrals a list of the top integral forms of T
	 * @param atX the value of x at which to evaluate 
	 * @return the value of the integral at x
	 */
	public T evaluatePolynomialIntegral
	(Coefficients<T> a, List<Polynomial.PowerFunction<T>> integrals, T atX)
	{
		T sum = manager.getZero (), term;
		for (int i = 0; i < a.size (); i++)
		{
			term = manager.multiply
				(a.get (i), integrals.get (i).eval (atX));
			sum = manager.add (sum, term);
		}
		return sum;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialIntegral(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object)
	 */
	public T evaluatePolynomialIntegral (Coefficients<T> a, T atX)
	{ return evaluatePolynomialIntegral (a, getIntegralT (a.size ()), atX); }


	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialIntegral(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object, java.lang.Object)
	 */
	public T evaluatePolynomialIntegral (Coefficients<T> a, T fromX, T toX)
	{
		List<Polynomial.PowerFunction<T>> integrals = getIntegralT (a.size ());
		T lo = evaluatePolynomialIntegral (a, integrals, fromX);
		T hi = evaluatePolynomialIntegral (a, integrals, toX);
		return manager.add (hi, manager.negate (lo));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialIntegral(net.myorb.math.GeneratingFunctions.Coefficients, java.lang.Object, java.util.List)
	 */
	public List<T> evaluatePolynomialIntegral (Coefficients<T> a, T fromX, List<T> toX)
	{
		List<Polynomial.PowerFunction<T>> integrals = getIntegralT (a.size ());
		T nlo = manager.negate (evaluatePolynomialIntegral (a, integrals, fromX));
		List<T> results = new ArrayList<T>();

		for (T t : toX)
		{
			T hi = evaluatePolynomialIntegral (a, integrals, t);
			results.add (manager.add (hi, nlo));
		}
		return results;
	}


	/**
	 * compute U[n] as series of T[0..n]
	 * @param n degree of polynomial to represent
	 * @return Coefficients of T[0..n]
	 */
	public Coefficients<T> secondKindAsFirstKindSeries (int n)
	{
		Coefficients<T> result = new Coefficients <T> ();
		if (n % 2 == 0) result.add (manager.newScalar (1));
		// U[n](x) = 2 SIGMA [1 <= i <= n <> 2] (T[i](x)), {n odd}
		// U[n](x) = 2 SIGMA [0 <= i <= n <> 2] (T[i](x)) - 1, {n even}
		int i; T ZERO = manager.getZero (), TWO = manager.newScalar (2);
		for (i = n; i > 0; i -= 2) { result.add (ZERO); result.add (TWO); }
		return result;
	}


	/**
	 * compute the derivative of a T[i] series
	 * @param function the Chebyshev polynomial series being differentiated
	 * @return the Chebyshev polynomial that is the derivative of the parameter function
	 */
	public PowerFunction<T> getFirstKindDerivative (PowerFunction<T> function)
	{
		Coefficients<T> c = function.getCoefficients ();
		return getPolynomialFunction (getFirstKindDerivative (c));
	}
	public Coefficients<T> getFirstKindDerivative (Coefficients<T> c)
	{
		Coefficients<T> sum = new Coefficients <T> (), term;
		for (int n = 1; n < c.size(); n++)
		{
			term = secondKindAsFirstKindSeries (n - 1);			// U[n](x) = 2 SIGMA [n%2 <= i <= n <> 2] (T[i](x))
			psm.multiplyInto (manager.newScalar (n), term);		// dT[n](x)/dx = n U[n-1](x)
			psm.multiplyInto (c.get (n), term);
			sum = psm.add (sum, term);
		}
		return sum;
	}

	
	/**
	 * compute Anti-Derivative of T[n] as a series of T
	 * @param n degree of polynomial to represent
	 * @return Coefficients of T[0..n+1]
	 */
	public Coefficients<T> getFirstKindAntiDerivative (int n)
	{
		int inm1 = n - 1, inp1 = n + 1;
		Coefficients<T> Tn = newCoefficients ();
		T ZERO = manager.getZero (); fillListUpTo (Tn, ZERO, inp1);
		// INTEGRAL T[n}(x) dx = [T[n+1](x)/(n+1) - T[n-1](x)/(n-1)] / 2
		T np1 = manager.newScalar (2 * inp1), nm1 = manager.newScalar (-2 * inm1);
		if (n > 1) Tn.set (inm1, manager.invert (nm1));
		Tn.set (inp1, manager.invert (np1));
		return Tn;
	}


	/**
	 * compute Anti-Derivative of a T[i] series
	 * @param function the Chebyshev polynomial series being integrated
	 * @return the Chebyshev polynomial that is the Anti-Derivative of the parameter function
	 */
	public PowerFunction<T> getFirstKindAntiDerivative (PowerFunction<T> function)
	{
		Coefficients<T> c = function.getCoefficients ();
		Coefficients<T> sum = newCoefficients (), term;
		for (int n = 0; n < c.size(); n++)
		{
			// INTEGRAL T[n](x) dx = [T[n+1](x)/(n+1) - T[n-1](x)/(n-1)] / 2
			term = getFirstKindAntiDerivative (n);
			psm.multiplyInto (c.get (n), term);
			sum = psm.add (sum, term);
		}
		return getPolynomialFunction (sum);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculusSupport#getPolynomialFunctionDerivative(net.myorb.math.Polynomial.PowerFunction)
	 */
	public Function<T> getFunctionDerivative (Function<T> function)
	{ return getFirstKindDerivative ((PowerFunction<T>)function); }


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculusSupport#getPolynomialFunctionIntegral(net.myorb.math.Polynomial.PowerFunction)
	 */
	public Function<T> getFunctionIntegral (Function<T> function)
	{ return getFirstKindAntiDerivative ((PowerFunction<T>)function); }


	/**
	 * reduce Checbyshev to ordinary polynomial functions
	 * @return a calculus object that returns ordinary polynomials
	 */
	public ChebyshevPolynomialCalculus<T> useReducedForm ()
	{
		return new ReducedChebyshevPolynomialCalculus<T> (manager);
	}


}


/**
 * perform calculus on Chebyshev series returning ordinary polynomial functions
 * @param <T> the types of coefficients in the polynomial terms
 * @author Michael Druckman
 */
class ReducedChebyshevPolynomialCalculus<T> extends ChebyshevPolynomialCalculus<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculusSupport#getPolynomialFunctionDerivative(net.myorb.math.Polynomial.PowerFunction)
	 */
	public Function<T> getFunctionDerivative (Function<T> function)
	{
		PowerFunction<T> sum = psm.getZero (), term;
		PowerFunction<T> poly = (PowerFunction<T>)function;
		GeneratingFunctions.Coefficients<T> coefficients = poly.getCoefficients ();
		List<Polynomial.PowerFunction<T>> derivatives = getDerivativeT (coefficients.size ());
		for (int i = 0; i < coefficients.size (); i++)
		{
			term = psm.times
				(coefficients.get (i), derivatives.get (i));
			sum = psm.add (sum, term);
		}
		// no longer a Chebyshev polynomial but rather an ordinary polynomial with
		// the coefficient patterns coming from origination as a Chebyshev polynomial
		return new Polynomial<T>(manager).getPolynomialFunction (sum.getCoefficients ());
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialCalculusSupport#getPolynomialFunctionIntegral(net.myorb.math.Polynomial.PowerFunction)
	 */
	public Function<T> getFunctionIntegral (Function<T> function)
	{
		PowerFunction<T> sum = psm.getZero (), term;
		PowerFunction<T> poly = (PowerFunction<T>)function;
		GeneratingFunctions.Coefficients<T> coefficients = poly.getCoefficients ();
		List<Polynomial.PowerFunction<T>> integrals = getIntegralT (coefficients.size ());
		for (int i = 0; i < coefficients.size (); i++)
		{
			term = psm.times
				(coefficients.get (i), integrals.get (i));
			sum = psm.add (sum, term);
		}
		// no longer a Chebyshev polynomial but rather an ordinary polynomial with
		// the coefficient patterns coming from origination as a Chebyshev polynomial
		return new Polynomial<T>(manager).getPolynomialFunction (sum.getCoefficients ());
	}


	public ReducedChebyshevPolynomialCalculus (SpaceManager<T> manager) { super (manager); }


}

