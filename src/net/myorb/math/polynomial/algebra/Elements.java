
package net.myorb.math.polynomial.algebra;

import java.util.ArrayList;

/**
 * representations of polynomial expressions
 * @author Michael Druckman
 */
public class Elements
{


	/**
	 * type of element nodes
	 */
	public enum OpTypes {Summation, Multiplication, Operand}


	// atomic Factor description base

	public static interface Factor { OpTypes getType (); }

	// binary operation factors

	/**
	 * child nodes of multi-factor operations
	 */
	public abstract static class Factors extends ArrayList <Factor> implements Factor
	{ private static final long serialVersionUID = 35114701359602093L; }

	/**
	 * a set of factors comprising a sum
	 */
	public static class Sum extends Factors
	{
		public String toString () { return image (this, "+"); }
		private static final long serialVersionUID = -5102897249367062053L;
		public OpTypes getType () { return OpTypes.Summation; }
	}

	/**
	 * a set of factors comprising a product
	 */
	public static class Product extends Factors
	{
		public String toString () { return image (this, "*"); }
		public OpTypes getType () { return OpTypes.Multiplication; }
		private static final long serialVersionUID = 5153646408526934363L;
	}


	// operand factors

	/**
	 * a factor made of a constant value
	 */
	public static class Constant implements Factor
	{
		public String toString ()
		{
			return value.endsWith (".0") ?
				value.substring (0, value.length () - 2) :
				value;
		}
		public OpTypes getType () { return OpTypes.Operand; }
		public Constant (String value) { this.value = value; }
		String value;
	}

	/**
	 * a factor made of a symbolic identifier
	 */
	public static class Variable implements Factor
	{
		public String toString () { return identifier; }
		public OpTypes getType () { return OpTypes.Operand; }
		public Variable (String identifier) { this.identifier = identifier; }
		String identifier;
	}

	/**
	 * a description of base-to-exponent factors
	 */
	public static class Power extends Factors
	{
		public Power () {}
		private static final long serialVersionUID = -7726638099953294189L;
		public String toString () { return base () + "^" + exponent (); }
		public OpTypes getType () { return OpTypes.Operand; }
		public Factor exponent () { return this.get (1); }
		public Factor base () { return this.get (0); }
	}


	// root equation description

	/**
	 * a sub-class of Sum providing a root node of equations
	 */
	public static class Equation extends Sum
	{ private static final long serialVersionUID = 8507384482661667874L; }


	// node linkage

	/**
	 * add a factor to a parent
	 * @param factor the factor to be added
	 * @param parent the parent summation or product node
	 */
	public static void add (Factor factor, Factor parent)
	{
		if (parent instanceof Factors)
		{
			if (factorMatchesParent (factor, parent))
			{ ((Factors) parent).addAll ((Factors) factor); }
			else ((Factors) parent).add (factor);
		}
	}
	static boolean factorMatchesParent (Factor factor, Factor parent)
	{
		OpTypes factorType = factor.getType ();
		if (factorType == OpTypes.Operand) return false;
		return factorType == parent.getType ();
	}


	// display formatter

	/**
	 * format sub-expression display
	 * @param factors the factors making sub-expression
	 * @param op the text of the operator
	 * @return the formatted display
	 */
	public static String image (Factors factors, String op)
	{
		if (factors.size () == 0) return "";

		StringBuffer buf =
			new StringBuffer ("( ")
				.append (factors.get (0).toString ());
		for (int i=1; i<factors.size (); i++)
		{
			buf.append (" ")
				.append (op).append (" ")
				.append (factors.get (i).toString ());
		}
		return buf.append (" )").toString ();
	}


}

