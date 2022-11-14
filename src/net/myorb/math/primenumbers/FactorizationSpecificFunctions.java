
package net.myorb.math.primenumbers;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;

import net.myorb.math.computational.Combinatorics;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

import java.math.BigInteger;

/**
 * implementation of functions specific to prime numbers
 * @author Michael Druckman
 */
public class FactorizationSpecificFunctions extends FactorizationPrimitives
{


	public FactorizationSpecificFunctions
	(SpaceManager <Factorization> manager, ExtendedPowerLibrary <Factorization> lib)
	{
		this
		(
			(ExpressionFactorizedFieldManager) manager,
			new ValueManager <Factorization> (), lib
		);
	}

	public FactorizationSpecificFunctions (Environment <Factorization> environment)
	{
		this
		(
			(ExpressionFactorizedFieldManager) environment.getSpaceManager (),
			environment.getValueManager (), null
		);
	}

	public FactorizationSpecificFunctions
		(
			ExpressionFactorizedFieldManager manager,
			ValueManager <Factorization> valueManager,
			ExtendedPowerLibrary <Factorization> lib
		)
	{
		super (manager, valueManager);
		this.combo = new Combinatorics <Factorization> (factoredMgr, lib);
		this.core = Factorization.getImplementation ();
		this.setConstants (); this.setLib (lib);
	}
	protected Combinatorics <Factorization> combo;
	protected Factorization.Underlying core;


	/**
	 * @param lib a library to be used for power functions
	 */
	public void setLib
	(ExtendedPowerLibrary <Factorization> lib) { this.lib = lib; }
	protected ExtendedPowerLibrary <Factorization> lib;


	/**
	 * collect commonly used constants
	 */
	public void setConstants ()
	{
		this.NEGONE = factoredMgr.newScalar (-1);
		this.TWO = factoredMgr.newScalar (2);
		this.ONE = factoredMgr.getOne ();
	}
	protected Factorization ONE, NEGONE, TWO;


	/*
	 * BigInteger low-level primitives
	 */


	/**
	 * @param value parameter to check
	 * @return TRUE for zero value
	 */
	public boolean isZero (BigInteger value)
	{
		return value.compareTo (BigInteger.ZERO) == 0;
	}


	/**
	 * @param x the number to be tested
	 * @return TRUE when floor of parameter matches parameter
	 */
	public boolean isInt (Factorization x)
	{
		return isZero (rem (x));
	}


	/*
	 * Factorization operations producing BigInteger
	 */


	/**
	 * @param x the number to be evaluated
	 * @return the parameter truncated at the decimal point
	 */
	public BigInteger characteristic (Factorization x)
	{
		return process ( x, (a, b) -> a.divide (b) );
	}


	/**
	 * @param x the number to be evaluated
	 * @return the remainder after the fraction is computed
	 */
	public BigInteger rem (Factorization x)
	{
		return process ( x, (a, b) -> a.remainder (b) );
	}


	/**
	 * @param x the numerator of the fraction
	 * @param y the divisor of the division operation
	 * @return remainder from x divided by y
	 */
	public BigInteger rem (Factorization x, Factorization y)
	{
		Factorization value =
			factoredMgr.multiply (x, y.pow (-1));
		return process ( value, (a, b) -> a.remainder (b) );
	}


	/**
	 * primorial function (product of prime less than parameter)
	 * @param parameter parameter to function
	 * @return computed value
	 */
	public BigInteger primorial (BigInteger parameter)
	{
		int N = parameter.intValue ();
		BigInteger product = BigInteger.ONE;
		for (BigInteger factor : core.getPrimesUpTo (N))
			product = product.multiply (factor);
		return product;
	}


	/*
	 * Factorization formulas directly tied to factors CORE functionality 
	 */


	/**
	 * PI function
	 * @return operation formula 
	 */
	public BigUnaryOp getPiOp ()
	{
		return (x) -> core.piFunction (x.intValue ());
	}


	/**
	 * Pn function (Nth prime)
	 * @return operation formula 
	 */
	public BigUnaryOp getNthPrimeOp ()
	{
		return (x) -> core.getNthPrime (x.intValue ());
	}


	/*
	 * Factorization unary and binary operations 
	 */


	/**
	 * @param x the parameter to GAMMA
	 * @return the computed value GAMMA for parameter x
	 * @throws RuntimeException for real number use that has no implementation
	 */
	public Factorization GAMMA (Factorization x) throws RuntimeException
	{
		if (isInt (x))
		{
			return combo.factorial (factoredMgr.add (x, NEGONE));
		}
		if (lib == null)
		{
			throw new RuntimeException ("GAMMA for Real numbers not available");
		}
		return lib.GAMMA (x);
	}


	/**
	 * power function
	 * @param x the value to be raised
	 * @param exponent an integer to use as exponent
	 * @return x^exponent
	 */
	public Factorization pow (Factorization x, Factorization exponent)
	{
		if (exponent == null) return ONE;
		if (x == null) return factoredMgr.getZero ();
		return x.pow (toInteger (exponent).intValue ());
	}


	/**
	 * @param x the number to be tested
	 * @return 1 for even and -1 for odd
	 */
	public Factorization alt (Factorization x)
	{
		return isZero (rem (x, TWO)) ? ONE : NEGONE;
	}


}

