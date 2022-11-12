
package net.myorb.math.primenumbers;

import net.myorb.math.computational.Combinatorics;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.List;

/**
 * support for Factorization data operations
 * @author Michael Druckman
 */
public class FactorizationPrimitives
{


	public FactorizationPrimitives
	(SpaceManager <Factorization> manager, ExtendedPowerLibrary <Factorization> lib)
	{
		this
		(
			(ExpressionFactorizedFieldManager) manager,
			new ValueManager <Factorization> (), lib
		);
	}

	public FactorizationPrimitives (Environment <Factorization> environment)
	{
		this
		(
			(ExpressionFactorizedFieldManager) environment.getSpaceManager (),
			environment.getValueManager (), null
		);
	}

	public FactorizationPrimitives
		(
			ExpressionFactorizedFieldManager manager,
			ValueManager <Factorization> valueManager,
			ExtendedPowerLibrary <Factorization> lib
		)
	{
		this.factoredMgr = manager; this.valueManager = valueManager;
		this.combo = new Combinatorics <Factorization> (factoredMgr, lib);
		this.setConstants (); this.setLib (lib);
	}
	protected ExpressionFactorizedFieldManager factoredMgr;
	protected ValueManager <Factorization> valueManager;
	protected Combinatorics <Factorization> combo;


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


	/**
	 * describe profile of BIG operators (binary)
	 */
	public interface BigOp
	{
		/**
		 * binary function profile
		 * @param left the parameter left of operator
		 * @param right the parameter right of operator
		 * @return the computed result
		 */
		BigInteger op (BigInteger left, BigInteger right);
	}


	/**
	 * describe profile of BIG operators (unary)
	 */
	public interface BigUnaryOp
	{
		/**
		 * binary function profile
		 * @param parameter the parameter to the operator
		 * @return the computed result
		 */
		BigInteger op (BigInteger parameter);
	}


	/**
	 * describe profile of BIG operators that return multiple values
	 */
	public interface BigArrayOp
	{
		/**
		 * binary function profile
		 * @param left the parameter left of operator
		 * @param right the parameter right of operator
		 * @return the computed results
		 */
		BigInteger [] op (BigInteger left, BigInteger right);
	}


	/*
	 * decompose objects
	 */


	/**
	 * Factorization reduced to integer
	 * @param value the Factorization to be reduced
	 * @return the reduced value
	 */
	public static BigInteger toInteger (Factorization value)
	{
		return (BigInteger) Factorization.toInteger (value);
	}

	/**
	 * pull values from a bundled parameter list
	 * @param parameterList the GenericValue holding parameters
	 * @return the parameters as an array
	 */
	public BigInteger [] extract (ValueManager.GenericValue parameterList)
	{
		return array
		(
			valueManager.getDimensionedValue (parameterList)
						.getValues ()
		);
	}

	/**
	 * array built from list of factored values
	 * @param values the factored value list being converted
	 * @return an array of BigInteger object reduced from the list
	 * @throws RuntimeException for any value found not to be integer
	 */
	public static BigInteger [] array
			(List <Factorization> values)
	throws RuntimeException
	{
		BigInteger [] integerValues =
				new BigInteger [values.size ()];
		for (int i = 0; i < integerValues.length; i++)
		{ integerValues [i] = toInteger (values.get (i)); }
		return integerValues;
	}


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
	 * apply binary formulas to generic parameter lists
	 */


	/**
	 * process a binary function call
	 * @param values the parameters to the operator
	 * @param formula the formula to apply
	 * @return the computed result
	 */
	public ValueManager.GenericValue process
	(ValueManager.GenericValue values, BigOp formula)
	{
		BigInteger [] p = extract (values);
		return bundle (formula.op (p [0], p [1]));
	}


	/**
	 * process a binary function call
	 * @param values the parameters to the operator
	 * @param formula the formula to apply
	 * @return the computed results
	 */
	public ValueManager.GenericValue processArray
	(ValueManager.GenericValue values, BigArrayOp formula)
	{
		BigInteger [] p = extract (values);
		return bundle (formula.op (p [0], p [1]));
	}


	/**
	 * process a binary operator evaluation
	 * @param left the left side parameter to the operator
	 * @param right the right side parameter to the operator
	 * @param formula the formula to apply
	 * @return the computed result
	 */
	public ValueManager.GenericValue process
		(
			ValueManager.GenericValue left,
			ValueManager.GenericValue right,
			BigOp formula
		)
	{
		BigInteger
			leftValue = toInteger (valueManager.toDiscrete (left)),
			rightValue = toInteger (valueManager.toDiscrete (right));
		return bundle (formula.op (leftValue, rightValue));
	}


	/**
	 * process a unary operator evaluation
	 * @param parameter the parameter to the operator
	 * @param formula the formula to apply
	 * @return the computed result
	 */
	public ValueManager.GenericValue processUnary
		(
			ValueManager.GenericValue parameter,
			BigUnaryOp formula
		)
	{
		BigInteger value =
			toInteger (valueManager.toDiscrete (parameter));
		return bundle (formula.op (value));
	}


	/**
	 * process a binary operator evaluation
	 * @param left the left side parameter to the operator
	 * @param right the right side parameter to the operator
	 * @param formula the formula to apply
	 * @return the computed array result
	 */
	public ValueManager.GenericValue processArray
		(
			ValueManager.GenericValue left,
			ValueManager.GenericValue right,
			BigArrayOp formula
		)
	{
		BigInteger
			leftValue = toInteger (valueManager.toDiscrete (left)),
			rightValue = toInteger (valueManager.toDiscrete (right));
		return bundle (formula.op (leftValue, rightValue));
	}


	/*
	 * Factorization processing as fractions
	 */


	/**
	 * normalize a Factorization to a fraction
	 * @param parameter value to normalize
	 * @return the normalized Distribution
	 */
	public Distribution norm (Factorization parameter)
	{
		Distribution d =
			Factorization.normalize
				(parameter, factoredMgr);
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
	(Factorization parameter, BigOp formula)
	{
		return process (norm (parameter), formula);
	}


	/**
	 * apply a formula to a fraction
	 * @param fraction the value as a normalized fraction
	 * @param formula the formula to be applied
	 * @return the computed value
	 */
	public static BigInteger process
	(Distribution fraction, BigOp formula)
	{
		BigInteger
			num = toInteger (fraction.getNumerator ()),
			den = toInteger (fraction.getDenominator ());
		return formula.op (num, den);
	}


	/*
	 * produce generic result packets
	 */


	/**
	 * package a result for return
	 * @param value the value for the result
	 * @return the GenericValue representation
	 */
	public ValueManager.GenericValue bundle (BigInteger value)
	{
		Factorization result = factoredMgr.bigScalar (value);
		return valueManager.newDiscreteValue (result);
	}

	/**
	 * package array result for return
	 * @param values the value for the result
	 * @return the GenericValue representation
	 */
	public ValueManager.GenericValue bundle (BigInteger [] values)
	{
		List <Factorization> computed = new ArrayList <> ();
		for (BigInteger v : values) computed.add (factoredMgr.bigScalar (v));
		return valueManager.newDimensionedValue (computed);
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

