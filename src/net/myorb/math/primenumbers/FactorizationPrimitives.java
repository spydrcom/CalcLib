
package net.myorb.math.primenumbers;

import net.myorb.math.expressions.managers.ExpressionFactorizedFieldManager;
import net.myorb.math.expressions.ValueManager;

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
		(
			ExpressionFactorizedFieldManager manager,
			ValueManager <Factorization> valueManager
		)
	{
		this.factoredMgr = manager;
		this.valueManager = valueManager;
	}
	protected ExpressionFactorizedFieldManager factoredMgr;
	protected ValueManager <Factorization> valueManager;


	/*
	 * description of operations on BigInteger
	 */


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
		 * unary function profile
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
	 * description of operations on Factorization
	 */


	/**
	 * describe profile of Factored operators (binary)
	 */
	public interface BinaryFactoredOp
	{
		/**
		 * binary function profile
		 * @param left the parameter left of operator
		 * @param right the parameter right of operator
		 * @return the computed result
		 */
		Factorization op (Factorization left, Factorization right);
	}


	/**
	 * describe profile of Factored operators (unary)
	 */
	public interface UnaryFactoredOp
	{
		/**
		 * unary function profile
		 * @param parameter the parameter to the operator
		 * @return the computed result
		 */
		Factorization op (Factorization parameter);
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
	 * apply formulas to generic parameter lists
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
	 * process a binary function evaluation
	 * @param parameters the parameters to the operator
	 * @param formula the formula to apply
	 * @return the computed result
	 */
	public ValueManager.GenericValue processFactoredBinary
		(
			ValueManager.GenericValue parameters,
			BinaryFactoredOp formula
		)
	{
		List <Factorization> p =
			valueManager.getDimensionedValue (parameters).getValues ();
		return bundle (formula.op (p.get (0), p.get (1)));
	}


	/**
	 * process a binary operator evaluation
	 * @param left the left side parameter to the operator
	 * @param right the right side parameter to the operator
	 * @param formula the formula to apply
	 * @return the computed result
	 */
	public ValueManager.GenericValue processFactoredBinary
		(
			ValueManager.GenericValue left,
			ValueManager.GenericValue right,
			BinaryFactoredOp formula
		)
	{
		Factorization
			leftValue = valueManager.toDiscrete (left),
			rightValue = valueManager.toDiscrete (right);
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
	 * process a unary operator evaluation
	 * @param parameter the parameter to the operator
	 * @param formula the formula to apply
	 * @return the computed result
	 */
	public ValueManager.GenericValue processFactoredUnary
		(
			ValueManager.GenericValue parameter,
			UnaryFactoredOp formula
		)
	{
		Factorization value =
			valueManager.toDiscrete (parameter);
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

	/**
	 * package a result for return
	 * @param value the value for the result
	 * @return the GenericValue representation
	 */
	public ValueManager.GenericValue bundle (Factorization value)
	{
		return valueManager.newDiscreteValue (value);
	}

	/**
	 * package a result for return
	 * @param value the value for the result
	 * @return the GenericValue representation
	 */
	public ValueManager.GenericValue bundle (BigInteger value)
	{
		return bundle (factoredMgr.bigScalar (value));
	}


}

