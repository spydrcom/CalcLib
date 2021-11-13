
package net.myorb.math;

/**
 * coding of Taylor polynomials for commonly used functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class TaylorPolynomials<T> extends Polynomial<T> implements PowerLibrary<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public TaylorPolynomials (SpaceManager<T> manager)
	{
		super (manager);
		setLibrary (this);
		setToleranceDefaults (this);
		computeEulersNumber (25);
	}


	/**
	 * compute the value of 'e'
	 *  also known as Eulers Number and Napiers Constant
	 *  actually attributed as discovery of Jacob Bernoulli
	 *  (while studying the mathematics of compound interest)
	 * @return the value of 'e'
	 */
	public T getEulersNumber () { return e; }
	public T computeEulersNumber (int terms) { return e = getExpSeries (terms).eval (discrete (1)); }
	T e; // keep a copy around for logarithm and exponentiation methods


	/**
	 * an interface for passing function coefficient calculations
	 * @param <T> type on which operations are to be executed
	 */
	public interface CoefficientFunction<T>
	{
		/**
		 * compute the nth coefficient of the function
		 * @param n the number of the term to be calculated
		 * @return the coefficient for the specified term
		 */
		T nthCoefficient (int n);
	}


	/**
	 * generate coefficients object
	 * @param f the function that can compute each term coefficient
	 * @param numberOfTerms the number of terms to include in the series
	 * @return the computed coefficients
	 */
	public Coefficients<T> computeCoefficients (CoefficientFunction<T> f, int numberOfTerms)
	{
		Coefficients<T> coefficients = new Coefficients<T> ();
		for (int n = 0; n < numberOfTerms; n++) coefficients.add (f.nthCoefficient (n));
		return coefficients;
	}


	/**
	 * compute coefficients for
	 *  ordinary polynomial and wrap into a function object
	 * @param f the function that can compute each term coefficient
	 * @param numberOfTerms the number of terms to include in the series
	 * @return the function object that contains the computed coefficients
	 */
	public PowerFunction<T> getPolynomialFor (CoefficientFunction<T> f, int numberOfTerms)
	{
		Coefficients<T> c =
			computeCoefficients (f, numberOfTerms);
		return getPolynomialFunction (c);
	}


	/**
	 * compute coefficients for
	 *  exponential polynomial and wrap into a function object
	 * @param f the function that can compute each term coefficient
	 * @param numberOfTerms the number of terms to include in the series
	 * @return the function object that contains the computed coefficients
	 */
	public PowerFunction<T> getExponentialFor (CoefficientFunction<T> f, int numberOfTerms)
	{
		Coefficients<T> c = computeCoefficients (f, numberOfTerms);
		return new ExponentialPolynomial<T> (manager, this).getPolynomialFunction (c);
	}


	/**
	 * calculate nth coefficient of Taylor SQRT series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	@SuppressWarnings("unchecked")
	public T sqrtCoefficient (int n)
	{
		Value<T> N = forValue (n);
		Value<T> numerator = factorial (2 * n);
		Value<T> one = forValue (1), two = forValue (2), four = forValue (4);
		Value<T> denominator = one.minus (two.times (N)).timesProductOf (factorial (n).squared (), four.pow (n));
		return powerSign (n).times (numerator).over (denominator).getUnderlying ();
	}
	public PowerFunction<T> getSqrtSeries (int numberOfTerms)
	{
		if (sqrtSeries != null) return sqrtSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return sqrtCoefficient (n); } };
		return sqrtSeries = getPolynomialFor (f, numberOfTerms);
	}
	PowerFunction<T> sqrtSeries = null;


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#sqrt(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public T sqrt (T value)
	{
		T one = discrete (1), xMinus1;
		if (isZro (value)) return discrete (0);
		if (isNeg (value)) raiseException ("SQRT domain error");
		if (isZro (xMinus1 = sumOf (value, neg (one)))) return one;
		if (isLessThan (one, value)) return X (value, sqrt (inverted (value)));
		return getSqrtSeries (80).eval (xMinus1);
	}


	/**
	 * calculate nth coefficient of Taylor COS series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	public T cosCoefficient (int n)
	{
		if (n%2 == 1) return discrete (0);
		return discrete ((n/2)%2 == 1? -1: 1);
	}
	public PowerFunction<T> getCosSeries (int numberOfTerms)
	{
		if (cosSeries != null) return cosSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return cosCoefficient (n); } };
		return cosSeries = getExponentialFor (f, numberOfTerms);
	}
	PowerFunction<T> cosSeries = null;

	/**
	 * reduce domain to 0 LE X LT PI
	 * @param value the value of computation parameter X
	 * @return cos(X)
	 */
	public T cos (T value)
	{
		if (isNeg (value))return cos (neg (value));
		if (isLessThan (PI (), value = reduceAngle (value))) return sin (piOver2minus (value));
		if (isLessThan (piOver (2), value)) return neg (sin (subtract (value, piOver (2))));
		return restrictRange (getCosSeries (70).eval (value));
	}


	/**
	 * calculate nth coefficient of Taylor COSH series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	public T coshCoefficient (int n)
	{
		if (n%2 == 1) return discrete (0);
		return discrete (1);
	}
	public PowerFunction<T> getCoshSeries (int numberOfTerms)
	{
		if (coshSeries != null) return coshSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return coshCoefficient (n); } };
		return coshSeries = getExponentialFor (f, numberOfTerms);
	}
	public T cosh (T value) { return getCoshSeries (10).eval (value); }
	PowerFunction<T> coshSeries = null;


	/**
	 * calculate nth coefficient of Taylor SIN series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	public T sinCoefficient (int n)
	{
		if (n%2 == 0) return discrete (0);
		return discrete ((n/2)%2 == 1? -1: 1);
	}
	public PowerFunction<T> getSinSeries (int numberOfTerms)
	{
		if (sinSeries != null) return sinSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return sinCoefficient (n); } };
		return sinSeries = getExponentialFor (f, numberOfTerms);
	}
	PowerFunction<T> sinSeries = null;

	/**
	 * reduce domain to 0 LE X LT PI
	 * @param value the value of computation parameter X
	 * @return sin(X)
	 */
	public T sin (T value)
	{
		if (isNeg (value)) return neg (sin (neg (value)));
		if (isLessThan (PI (), value = reduceAngle (value))) return cos (piOver2minus (value));
		if (isLessThan (piOver (2), value = reduceAngle (value))) return cos (subtract (value, piOver (2)));
		return restrictRange (getSinSeries (70).eval (value));
	}
	T restrictRange (T value)
	{
		return isLessThan (abs (value), discrete (1))? value: sgn (value);
	}


	/**
	 * calculate nth coefficient of Taylor SINH series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	public T sinhCoefficient (int n)
	{
		if (n%2 == 0) return manager.getZero ();
		return discrete (1);
	}
	public PowerFunction<T> getSinhSeries (int numberOfTerms)
	{
		if (sinhSeries != null) return sinhSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return sinhCoefficient (n); } };
		return sinhSeries = getExponentialFor (f, numberOfTerms);
	}
	public T sinh (T value) { return getSinhSeries (10).eval (value); }
	PowerFunction<T> sinhSeries = null;


	/**
	 * calculate nth coefficient of Taylor ASIN series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	public T asinCoefficient (int n)
	{
		if (n%2 == 0) return discrete (0); n = (n - 1) / 2;
		// asin(x) = [(2n)! * X^(2n+1)] / [2^(2n) * (n!)^2 * (2n+1)]  n >= 0 && abs(x) < 1
		Value<T> one = forValue (1), two = forValue (2), twoNplus1 = two.times (forValue (n)).plus (one);
		return factorial (2 * n).over (two.pow (2 * n).times (factorial (n).squared ().times (twoNplus1))).getUnderlying ();
	}
	public PowerFunction<T> getAsinSeries (int numberOfTerms)
	{
		if (asinSeries != null) return asinSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return asinCoefficient (n); } };
		return asinSeries = getPolynomialFor (f, numberOfTerms);
	}
	public T asin (T value) { return getAsinSeries (70).eval (value); }
	PowerFunction<T> asinSeries = null;


	/**
	 * calculate nth coefficient of Taylor ATAN series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	public T atanCoefficient (int n)
	{
		if (n%2 == 0) return discrete (0);
		// atan(x) = x - x^3/3 + x^5/5 - x^7/7 + ...     -1 <= x <= 1
		return powerSign (n/2).times (forValue (n).inverted ()).getUnderlying ();
	}
	public PowerFunction<T> getAtanSeries (int numberOfTerms)
	{
		if (atanSeries != null) return atanSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return atanCoefficient (n); } };
		return atanSeries = getPolynomialFor (f, numberOfTerms);
	}
	public T atan (T value) { return getAtanSeries (20).eval (value); }
	PowerFunction<T> atanSeries = null;


	/**
	 * calculate nth coefficient of Taylor ARTANH series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	public T artanhCoefficient (int n)
	{
		if (n%2 == 0) return discrete (0);
		// artanh(x) = x + x^3/3 + x^5/5 + x^7/7 + ...     x > 0
		return forValue (n).inverted ().getUnderlying ();
	}
	public PowerFunction<T> getArtanhSeries (int numberOfTerms)
	{
		if (artanhSeries != null) return artanhSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return artanhCoefficient (n); } };
		return artanhSeries = getPolynomialFor (f, numberOfTerms);
	}
	public T artanh (T value) { return getArtanhSeries (20).eval (value); }
	PowerFunction<T> artanhSeries = null;


	/**
	 * calculate nth coefficient of Taylor EXP series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	public T expCoefficient (int n)
	{
		return discrete (1);
	}
	public PowerFunction<T> getExpSeries (int numberOfTerms)
	{
		if (expSeries != null) return expSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return expCoefficient (n); } };
		return expSeries = getExponentialFor (f, numberOfTerms);
	}
	PowerFunction<T> expSeries = null;


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#exp(java.lang.Object)
	 */
	public T exp (T value)
	{
		int characteristic =
			manager.toNumber (value).intValue ();
		T mantissa = subtract (value, discrete (characteristic)) ;
		return X (getExpSeries (25).eval  (mantissa), pow (e, characteristic));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#nativeExp(java.lang.Object)
	 */
	public T nativeExp (T value)
	{
		throw new RuntimeException ("Native access not available");
	}


	/**
	 * calculate nth coefficient of Taylor LN series
	 * @param n the number of the term
	 * @return calculated coefficient
	 */
	public T lnCoefficient (int n)
	{
		if (n == 0) return discrete (0);
		//  ln (x+1) = x - x^2/2 + x^3/3 - x^4/4 + ...  -1 < x <= 1
		return powerSign (n-1).over (forValue (n)).getUnderlying ();
	}
	public PowerFunction<T> getLnSeries (int numberOfTerms)
	{
		if (lnSeries != null) return lnSeries;
		CoefficientFunction<T> f = new CoefficientFunction<T> ()
		{ public T nthCoefficient (int n) { return lnCoefficient (n); } };
		return lnSeries = getPolynomialFor (f, numberOfTerms);
	}
	PowerFunction<T> lnSeries = null;


	/**
	 * natural logarithm calculation for x LT 1.
	 *  domain reduced further to x GT THRESHOLD to improve convergence speed
	 * @param x the value to use as parameter of computation (x LT 1) in arithmetic object wrapper
	 * @param threshold the low bound of the domain reduction to use in preparation for power series evaluation
	 * @param base the base value of the logarithm to be applied, characteristic counted in domain reduction, mantissa converted
	 * @param baseConversion the conversion divisor from natural log (ln(base)) according to log[base b](x) = log[base k](x) / log[base k](b) using k=e
	 * @return log with specified base
	 */
	@SuppressWarnings("unchecked")
	public T logUsingBase (Value<T> x, Value<T> threshold, Value<T> base, Value<T> baseConversion)
	{
		int power = 0;
		T cvtFactor = baseConversion.inverted ().getUnderlying ();
		while (x.isLessThan (threshold)) { x = x.times (base); power--; }
		// compute natural log constrained by -1 < x <= 1, ln (x + 1) = ...
		T seriesEvaluation = getLnSeries (50).eval (x.minus (forValue (1)).getUnderlying ());
		return sumOf (X (seriesEvaluation, cvtFactor), discrete (power));
	}
	public T lnLessThan1 (Value<T> x) { return logUsingBase (x, forValue (2).inverted (), forValue (e), forValue (1)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#ln(java.lang.Object)
	 */
	public T ln (T value) 
	{
		Value<T> v = forValue (value);
		if (v.isZero () || v.isNegative ()) { raiseException ("Invalid parameter for Ln"); }
		else if (forValue (1).isLessThan (v)) { return neg (lnLessThan1 (v.inverted ())); }
		return lnLessThan1 (v);
	}

	/**
	 * use identity log[base b](x) = log[base k](x) / log[base k](b).
	 *  the Taylor series computes natural logarithm, so this implies k=e
	 * @param value the value for which to compute the logarithm
	 * @param base the base of the logarithm to be computed
	 * @return computed logarithm with specified base
	 */
	public T log (T value, T base) { return X (ln (value), inverted (ln (base))); }


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#pow(java.lang.Object, int)
	 */
	public T pow (T x, int n)
	{
		if (n < 0) return inverted (pow (x, -n));
		else if (n == 0) return discrete (1);
		else if (n == 1) return x;
		
		Value<T> v = forValue (x);
		Value<T> square = v.squared ();
		Value<T> result = square;

		while ((n -= 2) >= 2)
		{
			result = result.times (square);
		}

		if (n == 1)
			return result.times (v).getUnderlying ();
		else return result.getUnderlying ();
	}


	/**
	 * compute n!
	 * @param n the function parameter
	 * @return the computed result
	 */
	public Value<T> factorial (int n)
	{
		Value<T> result = forValue (1);
		for (int N = 2; N <= n; N++)
		{ result = result.times (forValue (N)); }
		return result;
	}
	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#factorial(java.lang.Object)
	 */
	public T factorial (T n)
	{
		T N = n, ONE = discrete (1);
		T result = discrete (1);
		while (isLessThan (ONE, N))
		{
			result = X (N, result);
			N = subtract (N, ONE);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#dFactorial(java.lang.Object)
	 */
	public T dFactorial (T n)
	{
		T N = n;
		T result = discrete (1);
		T ONE = discrete (1), TWO = discrete (2);
		while (isLessThan (ONE, N))
		{
			result = X (N, result);
			N = subtract (N, TWO);
		}
		return result;
	}


	/**
	 * determine value of (-1)^n
	 * @param n the exponent value
	 * @return the value of (-1)^n
	 */
	public Value<T> powerSign (int n)
	{
		return forValue (n%2==1? -1: 1);
	}


}


/**
 * use exponential generating function as appropriate
 *  in place of ordinary polynomial generating function
 * @param <T> type on which operations are to be executed
 */
class ExponentialPolynomial<T> extends Polynomial<T>
{

	public ExponentialPolynomial
	(SpaceManager<T> manager, TaylorPolynomials<T> parent)
	{ super (manager); this.setLibrary(parent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.Polynomial#evaluatePolynomialV(net.myorb.math.GeneratingFunctions.Coefficients, net.myorb.math.Arithmetic.Value)
	 */
	public Value<T> evaluatePolynomialV
	(Coefficients<T> coefficients, Value<T> x)
	{ return exponential (coefficients, x); }

}


