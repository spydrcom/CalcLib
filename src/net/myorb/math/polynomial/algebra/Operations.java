
package net.myorb.math.polynomial.algebra;

import net.myorb.math.computational.ArithmeticFundamentals;

/**
 * implementations of operations on polynomials
 * @author Michael Druckman
 */
public class Operations extends Utilities
{


	/**
	 * sum two factors
	 * @param left the left side of the sum
	 * @param right the right side of the sum
	 * @return description of the sum
	 */
	public static Factor sumOf (Factor left, Factor right)
	{
		return binary (left, right, new Sum (left.getConverter ()));
	}


	/**
	 * multiply two factors
	 * @param left the left side of the product
	 * @param right the right side of the product
	 * @return description of the product
	 */
	public static Factor productOf (Factor left, Factor right)
	{
		if (hasTerms (left))
		{
			if (hasTerms (right))
			{ return sumTimesSum (left, right); }
			return sumTimesFactor (left, right);
		}
		else if (hasTerms (right))
		{ return sumTimesFactor (right, left); }
		return simpleProduct (left, right);
	}
	static boolean hasTerms (Factor factor)
	{ return factor.getType () == OpTypes.Summation; }


	/**
	 * product of two sets of terms
	 * @param left the left side of the product
	 * @param right the right side of the product
	 * @return description of the product
	 */
	public static Factor sumTimesSum (Factor left, Factor right)
	{
		Sum result =
			new Sum (left.getConverter ());
		for ( Factor leftTerm : (Factors) left )
		{ add ( sumTimesFactor (right, leftTerm), result ); }
		return result;
	}


	/**
	 * product of terms with a factor
	 * @param sum the terms represented as a sum
	 * @param factor the factor to apply to each term
	 * @return description of the product
	 */
	public static Factor sumTimesFactor (Factor sum, Factor factor)
	{
		Sum result =
			new Sum (sum.getConverter ());
		for ( Factor term : (Factors) sum )
		{ add ( simpleProduct (factor, term), result ); }
		return result;
	}


	/**
	 * append a factor to a product
	 * @param left the left side of the product
	 * @param right the right side of the product
	 * @return description of the product
	 */
	public static Factor simpleProduct (Factor left, Factor right)
	{
		return binary (left, right, new Product (left.getConverter ()));
	}


	/**
	 * apply binary function to operands
	 * @param left the left side operand of the function
	 * @param right the right side operand of the function
	 * @param result the collection object for the result
	 * @return the filled collection object
	 */
	public static Factor binary
		(Factor left, Factor right, Factor result)
	{
		add (left, result);
		add (right, result);
		return result;
	}


	/**
	 * multiply parameter by negative 1 to form negated factor
	 * @param factor the factor to negate
	 * @return negated factor
	 */
	public static Factor negative (Factor factor)
	{
		ArithmeticFundamentals.Conversions <?> C = factor.getConverter ();
		return productOf ( new Constant ( C, C.getNegOne () ), factor );
	}


	// constant folding with additive or multiplicative operators


	/**
	 * fold factor into scalar
	 * - multiply constant factor into scalar
	 * @param scalar the scalar being modified
	 * @param factor the constant factor
	 */
	public static void multiplicativeFolding
		(
			ArithmeticFundamentals.Scalar scalar,
			Factor factor
		)
	{
		ArithmeticFundamentals.timesEquals
		(
			scalar, Constant.getValueFrom (factor)
		);
	}


	/**
	 * fold addend into scalar
	 * - multiply constant factor into scalar
	 * @param scalar the scalar being modified
	 * @param addend the constant factor
	 */
	public static void additiveFolding
		(
			ArithmeticFundamentals.Scalar scalar,
			Factor addend
		)
	{
		ArithmeticFundamentals.plusEquals
		(
			scalar, Constant.getValueFrom (addend)
		);
	}


}

