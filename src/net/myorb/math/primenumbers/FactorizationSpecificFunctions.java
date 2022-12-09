
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
	 * Factorization formulas directly tied to Combinatorics
	 */


	/**
	 * Derangements Count (subfactorial)
	 * @return operation implementation object
	 */
	public UnaryFactoredOp derangementsCount ()
	{
		return (x) -> combo.derangementsCount (x);
	}


	/**
	 * Stirling Numbers ($$ first kind)
	 * @return operation implementation object
	 */
	public BinaryFactoredOp stirling1 ()
	{
		return (l, r) -> combo.stirlingNumbers1 (l, r);
	}


	/**
	 * Stirling Numbers ($$$ second kind)
	 * @return operation implementation object
	 */
	public BinaryFactoredOp stirling2 ()
	{
		return (l, r) -> combo.stirlingNumbers2 (l, r);
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

		BigInteger expInt = (BigInteger) Factorization.toInteger (exponent);

		if (isInt (x) && !factoredMgr.isNegative (exponent))
			return factoredMgr.bigScalar
			(
				pow
				(
					(BigInteger) Factorization.toInteger (x),
					expInt
				)
			);
		return x.pow (expInt.intValue ());
	}


	/**
	 * @param x the number to be tested
	 * @return 1 for even and -1 for odd
	 */
	public Factorization alt (Factorization x)
	{
		return isZero (rem (x, TWO)) ? ONE : NEGONE;
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


	/*
	 * functions operating on BigInteger
	 */


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


	/**
	 * integer power function
	 * @param x the value to be raised
	 * @param exponent an integer to use as exponent
	 * @return x^exponent
	 */
	public BigInteger pow (BigInteger x, BigInteger exponent)
	{
		return x.pow (exponent.intValue ());
	}


	/**
	 * floor of a fraction
	 * @param num the numerator
	 * @param den the denominator
	 * @return the computed floor
	 */
	public BigInteger floor (BigInteger num, BigInteger den)
	{
		return num.divideAndRemainder (den) [0];
	}


	/**
	 * ceiling of a fraction
	 * @param num the numerator
	 * @param den the denominator
	 * @return the computed ceiling
	 */
	public BigInteger ceil (BigInteger num, BigInteger den)
	{
		BigInteger [] frac = num.divideAndRemainder (den);
		if (frac [1].compareTo (BigInteger.ZERO) == 0) return frac [0];
		return frac [0].add (BigInteger.ONE);
	}


	/**
	 * round a fraction
	 * @param num the numerator
	 * @param den the denominator
	 * @return the rounded result
	 */
	public BigInteger round (BigInteger num, BigInteger den)
	{
		BigInteger [] frac = num.divideAndRemainder (den);
		if (frac [1].compareTo (BigInteger.ZERO) == 0) return frac [0];
		BigInteger adjusted = frac [1].multiply (BigInteger.valueOf (2));
		if (adjusted.compareTo (den) >= 0) return frac [0].add (BigInteger.ONE);
		return frac [0];
	}


	/*
	 * low-level primitives
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


}

