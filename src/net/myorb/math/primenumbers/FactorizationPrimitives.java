
package net.myorb.math.primenumbers;

import net.myorb.math.computational.Combinatorics;
import net.myorb.math.expressions.algorithms.PrimeFormulas;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

import java.math.BigInteger;

/**
 * support for Factorization data operations
 * @author Michael Druckman
 */
public class FactorizationPrimitives
{


	public FactorizationPrimitives
	(SpaceManager <Factorization> manager, ExtendedPowerLibrary <Factorization> lib)
	{
		this.combo = new Combinatorics <Factorization> (manager, lib);
		this.lib = lib; this.manager = manager;
		this.NEGONE = manager.newScalar (-1);
		this.TWO = manager.newScalar (2);
		this.ONE = manager.getOne ();
	}
	protected ExtendedPowerLibrary <Factorization> lib;
	protected SpaceManager <Factorization> manager;
	protected Combinatorics <Factorization> combo;
	protected Factorization ONE, NEGONE, TWO;


	/**
	 * @param value parameter to check
	 * @return TRUE for zero value
	 */
	public boolean isZero (BigInteger value)
	{
		return value.compareTo (BigInteger.ZERO) == 0;
	}


	/**
	 * normalize a Factorization to a fraction
	 * @param parameter value to normalize
	 * @return the normalized Distribution
	 */
	public Distribution norm (Factorization parameter)
	{
		Distribution d = Factorization.normalize
			(parameter, manager);
		Distribution.normalize (d);
		return d;
	}


	/**
	 * apply algorithm to fraction
	 * @param parameter the value being processed
	 * @param formula the operation to apply to the value
	 * @return the computed result
	 */
	public BigInteger process
	(Factorization parameter, PrimeFormulas.BigOp formula)
	{
		return process (norm (parameter), formula);
	}


	/**
	 * apply a formula to a fraction
	 * @param fraction the value as a normalized fraction
	 * @param formula the formula to be applied
	 * @return the computed value
	 */
	public BigInteger process
	(Distribution fraction, PrimeFormulas.BigOp formula)
	{
		BigInteger
			num = fraction.getNumerator ().reduce (),
			den = fraction.getDenominator ().reduce ();
		return formula.op (num, den);
	}


	/**
	 * @param x the number to be evaluated
	 * @return the parameter truncated at the decimal point
	 */
	public BigInteger characteristic (Factorization x)
	{
		return process (x, (a,b) -> a.divide (b));
	}


	/**
	 * @param x the number to be evaluated
	 * @return the remainder after the fraction is computed
	 */
	public BigInteger rem (Factorization x)
	{
		return process (x, (a,b) -> a.remainder (b));
	}


	/**
	 * @param x the numerator of the fraction
	 * @param y the divisor of the division operation
	 * @return remainder from x divided by y
	 */
	public BigInteger rem (Factorization x, Factorization y)
	{
		Factorization value = manager.multiply (x, manager.invert (y));
		return process (value, (a,b) -> a.remainder (b));
	}


	/**
	 * @param x the number to be tested
	 * @return TRUE when floor of parameter matches parameter
	 */
	public boolean isInt (Factorization x)
	{
		return isZero (rem (x));
	}


	/**
	 * @param x the parameter to GAMMA
	 * @return the computed value GAMMA for parameter x
	 * @throws RuntimeException for real number use that has no implementation
	 */
	public Factorization GAMMA (Factorization x) throws RuntimeException
	{
		if (isInt (x))
		{
			return combo.factorial (manager.add (x, NEGONE));
		}
		if (lib == null)
		{
			throw new RuntimeException ("GAMMA for Real numbers not available");
		}
		return lib.GAMMA (x);
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

