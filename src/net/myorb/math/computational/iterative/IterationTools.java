
package net.myorb.math.computational.iterative;

import net.myorb.math.computational.Combinatorics;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.SpaceManager;

/**
 * computation algorithms for use as components of converging series
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class IterationTools <T> implements Environment.AccessAcceptance <T>
{


	public IterationTools
	(Environment <T> environment) { setEnvironment (environment); }
	public IterationTools (SpaceManager <T> manager)
	{ setManager (manager); }


	/**
	 * for applications that use AccessAcceptance
	 * - this must be followed with a call to setEnvironment
	 */
	public IterationTools () {}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <T> environment)
	{
		setManager (environment.getSpaceManager ());
	}


	/**
	 * connect with data type management
	 * @param manager the appropriate data type manager
	 */
	public void setManager (SpaceManager <T> manager)
	{
		this.manager = manager;
		this.combo = new Combinatorics <T> (manager, null);
		this.ONE = manager.getOne ();
		this.Z = manager.getZero ();
	}
	protected SpaceManager <T> manager;
	public Combinatorics <T> combo;
	public T ONE, Z;


	/**
	 * import arithmetic
	 * @param N value of scalar
	 * @return the scalar wrapped by the manager
	 */
	public T S (int N) { return manager.newScalar (N); }
	public T oneOver (T x) { return manager.invert (x); }
	public T sumOf (T x, T y) { return manager.add (x, y); }
	public T productOf (T x, T y) { return manager.multiply (x, y); }
	public T POW (T x, int y) { return manager.pow (x, y); }


	/**
	 * factorial of an integer
	 * @param N the parameter to factorial
	 * @return the factorial value wrapped as a scalar
	 */
	public T F (int N)
	{
		if (N < 2) return ONE;
		T product = S(N); for (int n = N-1; n > 1; n--)
		{ product = productOf (product, S (n)); }
		return product;
	}


	/**
	 * short-circuit around factorial
	 * @param P a value check for zero otherwise a factor
	 * @param pow the power of n to use as a factor
	 * @param n the parameter to use with factorial
	 * @return product of derivative and factorial
	 */
	public T primePow (T P, int pow, int n)
	{
		if (manager.isZero (P)) return P;
		return powTimes (productOf (P, F (n)), pow, n);
	}
	T powTimes (T PFn, int pow, int n)
	{
		if (pow == 0) return PFn;
		return productOf (PFn, POW (S (n), pow));
	}


	/*
	 * cyclic derivatives
	 */

	public static final int []
		SIN_PRIME = new int [] {0, 1, 0, -1},
		COS_PRIME = new int [] {1, 0, -1, 0};
	public T sinPrime (int n) { return S ( SIN_PRIME [n % 4] ); }	// alternating sign of odd powers
	public T cosPrime (int n) { return S ( COS_PRIME [n % 4] ); }	// alternating sign of even powers
	public T coshPrime (int n) { return S ( (n%2==1)? 0: 1 ); }		// even powers
	public T sinhPrime (int n) { return S ( (n%2==0)? 0: 1 ); }		// odd powers


	// access to derivatives

	/**
	 * define the algorithm which gives the Nth derivative
	 * @param <T> the data type manager
	 */
	public interface DerivativeComputer <T> { T nTHderivative (int n); }


	// exponentials

	public DerivativeComputer <T> getExpDerivativeComputer () { return (n) -> ONE; }


	// the trigonometric functions

	public DerivativeComputer <T> getSinDerivativeComputer () { return (n) -> sinPrime (n); }
	public DerivativeComputer <T> getCosDerivativeComputer () { return (n) -> cosPrime (n); }

	public DerivativeComputer <T> getTanDerivativeComputer () { return (n) -> tanPrime (n); }
	public DerivativeComputer <T> getSecDerivativeComputer () { return (n) -> secPrime (n); }

	public DerivativeComputer <T> getSinhDerivativeComputer () { return (n) -> sinhPrime (n); }
	public DerivativeComputer <T> getCoshDerivativeComputer () { return (n) -> coshPrime (n); }

	public DerivativeComputer <T>  getAtanDerivativeComputer  () { return (n) -> primePow ( sinPrime  (n), 0, n-1 ); }
	public DerivativeComputer <T> getArtanhDerivativeComputer () { return (n) -> primePow ( sinhPrime (n), 0, n-1 ); }


	T tanPrime (int n)
	{
		T sin = sinPrime (n);
		if (manager.isZero (sin)) return Z;
		T four = manager.newScalar (4), fourN = manager.pow (four, (n+1)/2);
		T B2n = combo.firstKindBernoulli (n+1), twoN = manager.invert (manager.newScalar (n+1));
		T oneMinus4n = manager.add (ONE, manager.negate (fourN)), product = manager.multiply (fourN, oneMinus4n);
		return manager.negate (manager.multiply (manager.multiply (B2n, manager.multiply (product, twoN)), sin));
	}

	T secPrime (int n)
	{
		T cos = cosPrime (n);
		if (manager.isZero (cos)) return Z;
		T sec = combo.E2nDoubleSum (n);
		return manager.multiply (cos, sec);
	}


	// The polylogarithms

	public DerivativeComputer <T> getLi2DerivativeComputer () { return (n) -> n==0? Z: primePow (ONE, -2, n); }
	public DerivativeComputer <T> getLi3DerivativeComputer () { return (n) -> n==0? Z: primePow (ONE, -3, n); }


	// The Legendre chi functions 

	public DerivativeComputer <T> getChi2DerivativeComputer () { return (n) -> primePow (sinhPrime (n), -2, n); }
	public DerivativeComputer <T> getChi3DerivativeComputer () { return (n) -> primePow (sinhPrime (n), -3, n); }


	// inverse tangent integrals

	public DerivativeComputer <T> getTi2DerivativeComputer () { return (n) -> primePow (sinPrime (n), -2, n); }
	public DerivativeComputer <T> getTi3DerivativeComputer () { return (n) -> primePow (sinPrime (n), -3, n); }


	// elliptic integrals 

	public DerivativeComputer <T> getKDerivativeComputer () { return (n) -> (n%2==1)? Z: getK (n); }	// * pi/2
	public DerivativeComputer <T> getEDerivativeComputer () { return (n) -> (n%2==1)? Z: getE (n); }


	T getE (int n)
	{
		return productOf ( getK (n), oneOver (S ( 1 - n )) );
	}

	T getK (int n)
	{
		T ratio =
			productOf
			(
				POW ( F(n), 3 ),
				POW ( F(n/2), -4 )
			);
		return productOf (ratio, POW ( S(16), -n/2 ));
	}


}

