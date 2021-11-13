
package net.myorb.math;

/**
 * library of exponentiation algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ExponentiationLib<T> extends Tolerances<T> implements ExtendedPowerLibrary<T>
{

	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public ExponentiationLib (SpaceManager<T> manager)
	{ super (manager); setToleranceDefaults (this); }


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
	 * compute SQRT of value normalized to ideal range
	 * @param v value normalized as 0 LE x LE 1
	 * @return result of computation
	 */
	public Value<T> sqrtInRange (Value<T> v)
	{
		boolean r = reductionMechanism != null;
		boolean d = isSelected (DUMP_ITERATIVE_TERM_VALUES);
		Value<T> ONE = oneValue (), TWO = forValue (2), FOUR = forValue (4);
		Value<T> x =  v.minus (ONE), sum = ONE, xPower = ONE, n = ONE;
		SignManager manager = new SignManager (false);
		Value<T> coef = ONE, twonm1 = ONE;
		Value<T> fourn = FOUR, termValue;

		for (int i=getTermCount(SQRT); i>0; i--)
		{
			xPower = product (xPower, x);
			coef = coef.times (TWO).times (twonm1).over (n);
			termValue = coef.times (xPower).over (twonm1.times (fourn));
			if (manager.signToggle ()) { termValue = termValue.negate (); }
			reduceAndDump (termValue, sum = sum.plus (termValue), r, d);
			fourn = product (fourn, FOUR); twonm1 = twonm1.plus (TWO);
			n = sum (n, ONE);
		}

		return sum;
	}

	/**
	 * use taylor series approximation of sqrt
	 * @param v parameter for sqrt computation
	 * @return computed result
	 */
	public Value<T> taylorSqrt (Value<T> v)
	{
		if (forValue (1).isLessThan (v))
		{ return sqrtInRange (v.inverted ()).times (v); }
		else return sqrtInRange (v);
	}


	/**
	 * use newton method approximation of sqrt
	 * @param value parameter for sqrt computation
	 * @return computed result
	 */
	public Value<T> newtonSqrt (Value<T> value)
	{
		Value<T> two = forValue (2), y, yPrime, xn, xnp1;
		Value<T> x = bitLengthApproximatedSqrt (value);				// approximated root

		for (int i = getTermCount (SQRT); i > 0; i--)
		{
			y = x.squared ().minus (value);							// value of the function at the approximated root
			yPrime = x.times (two);									// value of the derivative at the approximated root

			xn = x;
			xnp1 = xn.minus (y.over (yPrime));						// compute x(n+1), next iteration of approximation
			x = xnp1;

			Value<T> toleranceCheck =
				abs (xnp1.minus (xn)).over (abs (xnp1));
			if (withinTolerance (toleranceCheck)) return x;
		}

		raiseException ("Failure to converge");
		return null;
	}


	/**
	 * compute value half bit-width in size
	 * @param x the value seeking SQRT approximation
	 * @return computed approximation
	 */
	public Value<T> bitLengthApproximatedSqrt (Value<T> x)
	{
		Value<T> two = forValue (2);
		java.math.BigInteger bigint = java.math.BigInteger.valueOf
			(manager.toNumber (x.getUnderlying ()).longValue ());
		return two.pow (bigint.bitLength () / 2);					// 1/2 the bit size
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#sqrt(java.lang.Object)
	 */
	public T sqrt (T x)
	{
		Value<T> v = forValue (x);
		if (v.isZero ()) return discrete (0);
		if (v.isNegative ())
			raiseException ("SQRT of negative number");
		return newtonSqrt (v).getUnderlying ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#exp(java.lang.Object)
	 */
	public T exp (T x)
	{
		Value<T> v = forValue (x);
		boolean r = reductionMechanism != null;
		boolean d = isSelected (DUMP_ITERATIVE_TERM_VALUES);
		Value<T> xPower = v, termValue, factorial = oneValue ();
		Value<T> accumulation = v.plus (oneValue ());
		int termNumber = 1;

		for (int i=getTermCount(EXP); i>0; i--)
		{
			xPower = xPower.times (v);
			termNumber = factorialExtended (termNumber, 1, factorial);
			//  exp (x) = 1 + x + x^2/2! + x^3/3! + x^4/4! + ...    all real x
			accumulation = accumulation.plus (termValue = xPower.over (factorial));
			reduceAndDump (termValue, accumulation, r, d);
		}

		return accumulation.getUnderlying();
	}


	/**
	 * compute natural logarithm of parameter value.
	 *  normalize domain to 0.33 LT x LT 1 by powers of multiples of E.
	 *  more cycles spent on normalizing to domain but result
	 *  precision is higher on faster convergence of series
	 * @param x the value to use as base of computation
	 * @return result of computation
	 */
	public T lnNormalized (T x)
	{		
		T v = x;
		if (manager.isZero(x) || manager.isNegative(x))
		{ raiseException ("Invalid parameter for Ln"); }

		T ONE = manager.newScalar (1); T E = exp (ONE); int power = 0;
		T THIRD = manager.invert (manager.newScalar (3)); T eInverted = manager.invert (E);

		while (manager.lessThan (ONE, v)) { v = manager.multiply (v, eInverted); power++; }
		while (manager.lessThan (v, THIRD)) { v = manager.multiply (v, E); power--; }
		
		return lnInRange (forValue (v)).plus (forValue (power)).getUnderlying ();
	}

	/**
	 * compute natural logarithm of parameter value.
	 *  faster normalization of domain 0 LT x LT 1 by using 1/x inversion and negation of result.
	 *  less precision seen for values GT 3 due to slower convergence of series
	 * @param x the value to use as base of computation
	 * @return result of computation
	 */
	public T lnInvertAndNegate (T x)
	{
		Value<T> v = forValue (x);
		if (v.isZero () || v.isNegative ())
		{ raiseException ("Invalid parameter for Ln"); }

		if (manager.lessThan (manager.newScalar (1), x))
			return lnInRange (v.inverted ()).negate ().getUnderlying ();
		else return lnInRange (v).getUnderlying ();
	}

	/**
	 * general natural logarithm calculation.
	 *  domain reduced for x GT 1 by negation of ln(1/x)
	 * @param x the value to use as base of computation
	 * @return result of computation
	 */
	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#ln(java.lang.Object)
	 */
	public T ln (T x)
	{
		Value<T> v = forValue (x);
		if (v.isZero () || v.isNegative ())
		{
			raiseException ("Invalid parameter for Ln");
		}
		else if (manager.lessThan (discrete (1), x))
		{
			return neg (lnLessThan1 (v.inverted ().getUnderlying ()));
		}
		return lnLessThan1 (x);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#power(java.lang.Object, java.lang.Object)
	 */
	public T power (T x, T y)
	{
		return exp (manager.multiply (ln (x), y));
	}

	/**
	 * natural logarithm calculation for x LT 1.
	 *  domain reduced further to x GT 1/3 to improve convergence speed
	 * @param x the value to use as base of computation x LT 1
	 * @return result of computation
	 */
	public T lnLessThan1 (T x)
	{
		T v = x; int power = 0;
		T E = exp (discrete (1)), THIRD = inverted (discrete (3));
		while (manager.lessThan (v, THIRD)) { v = X (v, E); power--; }
		return lnInRange (forValue (v)).plus (forValue (power)).getUnderlying ();
	}

	/**
	 * compute natural log constrained by -1 LT x LE 1
	 * @param v the value constrained to acceptable range
	 * @return natural log of parameter value
	 */
	public Value<T> lnInRange (Value<T> v)
	{
		int termno = 1;
		Value<T> x = v.minus (oneValue ());
		boolean d = isSelected (DUMP_ITERATIVE_TERM_VALUES);
		SignManager manager = new SignManager (true);
		boolean r = reductionMechanism != null;
		Value<T> sum = x, xpow = x;
		Value<T> termValue;
		
		for (int i=getTermCount(LOG); i>0; i--)
		{
			//  ln (x+1) = x - x^2/2 + x^3/3 - x^4/4 + ...  -1 < x <= 1
			manager.toggleSign (termValue = (xpow = xpow.times (x)).over (forValue (++termno)));
			reduceAndDump (termValue, sum = sum.plus (termValue), r, d);
		}

		return sum;
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

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#nThRoot(java.lang.Object, int)
	 */
	public T nThRoot (T x, int root)
	{
		T divisor = manager.invert (manager.newScalar (root));
		return exp (manager.multiply (ln (x), divisor));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#magnitude(java.lang.Object)
	 */
	public T magnitude (T x)
	{
		throw new RuntimeException ("Unimplemented feature: magnitude");
	}

}
