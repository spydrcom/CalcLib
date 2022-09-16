
package net.myorb.math;

/**
 * basic exponentiation and root primitives
 * @param <T>  type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class PowerPrimitives<T> extends Tolerances<T>
	implements ExtendedPowerLibrary<T>
{


	/**
	 * type manager required matching generic type
	 * @param manager space manager for component type
	 */
	public PowerPrimitives (SpaceManager<T> manager)
	{
		super (manager); setToleranceDefaultParameters ();
		taylor = new TaylorPolynomials<T>(manager);
	}
	TaylorPolynomials<T> taylor;


	/* (non-Javadoc)
	 * @see net.myorb.math.Tolerances#setToleranceDefaultParameters()
	 */
	public void setToleranceDefaultParameters ()
	{
		this.TEN = forValue (10);
		this.epsilon = pow (TEN, -14);
		this.tolerance = pow (TEN, -10);
		this.inflectionOffset = pow (TEN, -1);
		this.maxIterations = 50;
	}


	/**
	 * compute x^n for larger exponents
	 * @param x base of the exponentiation
	 * @param n exponent for the computation
	 * @return computed result
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
	public Value<T> pow (Value<T> x, int exponent)
	{
		return forValue (pow (x.getUnderlying (), exponent));
	}


	/**
	 * compute x^n for smaller exponent
	 * @param x base of the exponentiation
	 * @param exponent the exponent for the computation
	 * @return computed result
	 */
	public T toThe (T x, int exponent)
	{
		if (exponent < 0) return manager.invert (toThe (x, -exponent));
		if (exponent == 0) return manager.getOne ();
		if (exponent == 1) return x;

		T product = x;
		for (int i = exponent; i > 1; i--)
			product = manager.multiply (product, x);
		return product;
	}
	public Value<T> toThe (Value<T> x, int exponent)
	{
		return forValue (toThe (x.getUnderlying (), exponent));
	}


	/**
	 * find small enough approximation
	 * @param x the value seeking the root value
	 * @param root the integer root number
	 * @return computed approximation
	 */
	public T approximatedRoot (T x, int root)
	{
		T half = manager.invert (manager.newScalar (2)),
			approx = manager.invert (manager.newScalar (2));
		while (true)
		{
			T y = toThe (approx, root);
			if (!manager.lessThan (x, y)) break;
			approx = manager.multiply (approx, half);
		}
		return approx;
	}


	/**
	 * compute root using Newton-Raphson method
	 * @param value the value seeking the root calculation
	 * @param root the integer root number
	 * @return calculated root
	 */
	public T newtonRaphsonRootLoop (T value, int root)
	{
		T	negValue = neg (value),
			tRoot = manager.newScalar (root),
			x = approximatedRoot (value, root);
		T y, yPrime, xn, xnp1, toleranceCheck;
		T yOverPrime, negXn, diff;
		int dRoot = root - 1;

		for (int i = maxIterations; i > 0; i--)							// more iterations needed for very small numbers
		{
			xn = x;
			y = manager.add (toThe (x, root), negValue);				// value of the function at the approximated root
			yPrime = manager.multiply (toThe (x, dRoot), tRoot);		// value of the derivative at the approximated root
			yOverPrime = manager.negate (manager.multiply
				(y, manager.invert (yPrime)));
			xnp1 = manager.add (xn, yOverPrime);						// compute x(n+1), next iteration of approximation
			negXn = manager.negate (xn);
			x = xnp1;

			diff = manager.add (xnp1, negXn);
			toleranceCheck = manager.multiply
				(abs (diff), manager.invert (abs (xnp1)));
			if (withinTolerance (toleranceCheck)) return x;
		}

		throw new RuntimeException ("Convergence failure");
	}


	/**
	 * generic driver for root calculation
	 * @param x the value seeking the root calculation
	 * @param root the integer root number
	 * @return calculated root
	 */
	public T nThRoot (T x, int root)
	{
		T ONE = manager.getOne ();
		if (manager.lessThan (ONE, x))
			return manager.multiply (x, rootInverted (x, root));
		return newtonRaphsonRootLoop (x, root);
	}
	public Value<T> nThRoot (Value<T> x, int root)
	{
		return forValue (nThRoot (x.getUnderlying (), root));
	}


	/**
	 * n ROOT x = x * (n ROOT (1/x)^(n-1))
	 * @param x the value seeking the root calculation
	 * @param root the integer root number
	 * @return calculated root
	 */
	public T rootInverted (T x, int root)
	{
		T inverted =
			newtonRaphsonRootLoop (manager.invert (x), root);
		return toThe (inverted, root - 1);
	}


	/**
	 * use generic loop to compute SQRT
	 * @param x the value seeking the root calculation
	 * @return calculated root
	 */
	public T sqrt (T x)
	{
		if (manager.isNegative (x))
			throw new RuntimeException ("SQRT of negative parameter in real domain");
		if (manager.isZero (x)) return manager.getZero ();
		return nThRoot (x, 2);
	}
	public Value<T> sqrt (Value<T> x)
	{
		return forValue (sqrt (x.getUnderlying ()));
	}


	/**
	 * use generic loop to compute cube root
	 * @param x the value seeking the root calculation
	 * @return calculated root
	 */
	public T cubeRoot (T x)
	{
		return nThRoot (x, 3);
	}
	public Value<T> cubeRoot (Value<T> x)
	{
		return forValue (cubeRoot (x.getUnderlying ()));
	}


	/**
	 * compute factorial of a value
	 * @param n the parameter to the factorial function
	 * @return the computed factorial
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
	public Value<T> factorial (Value<T> x)
	{
		return forValue (factorial (x.getUnderlying ()));
	}


	/**
	 * compute parity factorial of a value
	 * @param n the parameter to the factorial function
	 * @return the computed factorial
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
	public Value<T> dFactorial (Value<T> x)
	{
		return forValue (dFactorial (x.getUnderlying ()));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#GAMMA(java.lang.Object)
	 */
	public T GAMMA (T value)
	{
		throw new RuntimeException ("No GAMMA support present in library");
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#exp(java.lang.Object)
	 */
	public T exp (T value)
	{
		return taylor.exp (value);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#ln(java.lang.Object)
	 */
	public T ln (T value)
	{
		return taylor.ln (value);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#power(java.lang.Object, java.lang.Object)
	 */
	public T power(T x, T y)
	{
		return exp (manager.multiply (ln (x), y));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#magnitude(java.lang.Object)
	 */
	public T magnitude (T x)
	{
		throw new RuntimeException ("Unimplemented feature: magnitude");
	}


}

