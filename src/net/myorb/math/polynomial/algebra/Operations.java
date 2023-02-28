
package net.myorb.math.polynomial.algebra;

/**
 * implementations of operations on polynomials
 * @author Michael Druckman
 */
public class Operations extends Elements
{


	/**
	 * sum two factors
	 * @param left the left side of the sum
	 * @param right the right side of the sum
	 * @return description of the sum
	 */
	public static Factor sumOf (Factor left, Factor right)
	{
		Sum sum = new Sum ();
		add (left, sum); add (right, sum);
		return sum;
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
		Sum result = new Sum ();
		for ( Factor leftTerm : (Factors) left )
		{
			for ( Factor rightTerm : (Factors) right )
			{
				add ( simpleProduct (leftTerm, rightTerm), result );
			}
		}
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
		Sum result = new Sum ();
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
		Product result = new Product ();
		add (left, result); add (right, result);
		return result;
	}


}

