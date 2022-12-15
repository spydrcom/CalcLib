
package net.myorb.math.computational.iterative;

import net.myorb.math.SpaceManager;

/**
 * computation algorithms for use as components of converging series
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class IterationTools <T>
{


	public IterationTools
	(SpaceManager <T> manager)
	{ this.manager = manager; this.ONE = manager.getOne (); this.Z = manager.getZero (); }
	protected SpaceManager <T> manager;
	protected T ONE, Z;


	public T S (int N) { return manager.newScalar (N); }
	public T productOf (T x, T y) { return manager.multiply (x, y); }
	public T POW (T x, int y) { return manager.pow (x, y); }

	public T F (int N)
	{
		T product = ONE; for (int n = N; n > 1; n--)
		{ product = productOf (product, S (n)); }
		return product;
	}

	public T factorialProductOf (T scalar, int n)
	{
		if (manager.isZero (scalar)) return scalar;
		return manager.multiply (scalar, F(n));
	}

	public T factorialProductOf (T prime, T scalar, int n)
	{
		if (manager.isZero (prime)) return scalar;
		return manager.multiply (manager.multiply (scalar, F(n)), prime);
	}


	public static final int []
		SIN_PRIME = new int [] {0, 1, 0, -1},
		COS_PRIME = new int [] {1, 0, -1, 0};
	public T sinPrime (int n) { return S ( SIN_PRIME [n % 4] ); }
	public T cosPrime (int n) { return S ( COS_PRIME [n % 4] ); }
	public T sinhPrime (int n) { return S ( (n%2==0)? 0: 1 ); }
	public T coshPrime (int n) { return S ( (n%2==1)? 0: 1 ); }


	// the trigonometric functions

	public interface DerivativeComputer <T> { T nTHderivative (int k); }

	public DerivativeComputer <T> getExpDerivativeComputer () { return (n) -> ONE; }

	public DerivativeComputer <T> getSinDerivativeComputer () { return (n) -> sinPrime (n); }
	public DerivativeComputer <T> getCosDerivativeComputer () { return (n) -> cosPrime (n); }

	public DerivativeComputer <T> getSinhDerivativeComputer () { return (n) -> sinhPrime (n); }
	public DerivativeComputer <T> getCoshDerivativeComputer () { return (n) -> coshPrime (n); }

	public DerivativeComputer <T>
			getAtanDerivativeComputer ()
	{ return (n) -> factorialProductOf ( sinPrime (n), n-1 ); }

	public DerivativeComputer <T>
			getArtanhDerivativeComputer ()
	{ return (n) -> factorialProductOf ( sinhPrime (n), n-1 ); }

	// The polylogarithms

	public DerivativeComputer <T> getLi2DerivativeComputer ()
	{ return (n) -> n==0? Z: factorialProductOf (POW (S (n), -2), n); }

	public DerivativeComputer <T> getLi3DerivativeComputer ()
	{ return (n) -> n==0? Z: factorialProductOf (POW (S (n), -3), n); }

	// The Legendre chi functions 

	public DerivativeComputer <T> getChi2DerivativeComputer ()
	{ return (n) -> factorialProductOf (sinhPrime (n), POW (S (n), -2), n); }

	public DerivativeComputer <T> getChi3DerivativeComputer ()
	{ return (n) -> factorialProductOf (sinhPrime (n), POW (S (n), -3), n); }

	// inverse tangent integrals

	public DerivativeComputer <T> getTi2DerivativeComputer ()
	{ return (n) -> factorialProductOf (sinPrime (n), POW (S (n), -2), n); }

	public DerivativeComputer <T> getTi3DerivativeComputer ()
	{ return (n) -> factorialProductOf (sinPrime (n), POW (S (n), -3), n); }

	// elliptic integrals 

	public DerivativeComputer <T> getKDerivativeComputer ()
	{ return (n) -> (n%2==1)? Z: getK (n); }

	public DerivativeComputer <T> getEDerivativeComputer ()
	{ return (n) -> (n%2==1)? Z: getE (n); }

	T getK (int n) { return null; }
	T getE (int n) { return null; }


}

