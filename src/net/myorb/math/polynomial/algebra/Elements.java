
package net.myorb.math.polynomial.algebra;

import java.util.ArrayList;

/**
 * representations of polynomial expressions
 * @author Michael Druckman
 */
public abstract class Elements
{


	/**
	 * type of element nodes
	 */
	public enum OpTypes {Summation, Multiplication, Operand}


	// atomic Factor description base

	public static interface Reference { boolean refersTo (String symbol); }
	public static interface Factor { OpTypes getType (); }


	// binary operation factors

	/**
	 * child nodes of multi-factor operations
	 */
	public abstract static class Factors
			extends ArrayList <Factor> implements Factor
	{
		public static Factor firstOf (Factor factors) { return ( (Factors) factors ).get (0); }
		private static final long serialVersionUID = 35114701359602093L;
	}

	/**
	 * a set of factors comprising a sum
	 */
	public static class Sum extends Factors
	{
		public String toString () { return bracketedImage (this, " + "); }
		private static final long serialVersionUID = -5102897249367062053L;
		public OpTypes getType () { return OpTypes.Summation; }
	}

	/**
	 * sum extension indicating subtraction
	 */
	public static class Difference extends Sum
	{
		public String toString () { return bracketedImage (this, " - "); }
		private static final long serialVersionUID = -5098433414832709926L;
	}

	/**
	 * mark factor as negative
	 */
	public static class Negated implements Factor
	{
		public OpTypes getType () { return null; }
		public Negated (Factor factor) { this.child = factor; }
		public Factor getFactor () { return child; }
		Factor child;
	}

	/**
	 * a set of factors comprising a product
	 */
	public static class Product extends Factors
	{
		public String toString () { return image (this, "*"); }
		public OpTypes getType () { return OpTypes.Multiplication; }
		private static final long serialVersionUID = 5153646408526934363L;
		public Product (Factor factor) { Utilities.add (factor, this); }
		public Product () {}
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

		public double getValue ()
		{ return Double.parseDouble (value); }
		public static double getValue (Factor factor)
		{ return ( (Constant) factor ).getValue (); }

		public OpTypes getType () { return OpTypes.Operand; }
		public Constant (Double value) { this.value = value.toString (); }
		public Constant (String value) { this.value = value; }

		public void negate ()
		{
			value = Double.toString ( - getValue () );
		}

		String value;
	}

	/**
	 * a factor made of a symbolic identifier
	 */
	public static class Variable implements Factor, Reference
	{
		public String toString () { return identifier; }
		public OpTypes getType () { return OpTypes.Operand; }
		public boolean refersTo (String symbol)
		{ return identifier.equals (symbol); }
		public Variable (String identifier)
		{ this.identifier = identifier; }
		String identifier;
	}

	/**
	 * a description of base-to-exponent factors
	 */
	public static class Power extends Factors implements Reference
	{
		public Power () {}
		public boolean refersTo (String symbol)
		{ return ( (Reference) base () ).refersTo (symbol); }

		private static final long serialVersionUID = -7726638099953294189L;
		public String toString () { return base () + "^" + exponent (); }

		public OpTypes getType () { return OpTypes.Operand; }
		public Factor exponent () { return this.get (1); }
		public Factor base () { return this.get (0); }

		public static Factor reference (Variable variable, Double order)
		{
			Factors power = new Power ();
			Constant exp = new Constant (order);
			power.add (variable); power.add (exp);
			return power;
		}
	}


	// root equation description

	/**
	 * a sub-class of Sum providing a root node of equations
	 */
	public static class Equation extends Sum
	{ private static final long serialVersionUID = 8507384482661667874L; }


	// display formatter

	/**
	 * format sub-expression display
	 * @param factors the factors making sub-expression
	 * @param op the text of the operator
	 * @return the formatted display
	 */
	public static String image (Factors factors, String op)
	{
		int factorCount; StringBuffer buf; Factor next;
		if ((factorCount = factors.size ()) > 0)
		{
			buf = new StringBuffer ()
				.append (factors.get (0).toString ());
			for (int i = 1; i < factorCount; i++)
			{
				if ( (next = Utilities.reducedForm (factors.get (i))) instanceof Negated )
				{ buf.append (" - ").append ( ( (Negated) next ).getFactor () ); }
				else buf.append (op).append (next);
			}
			return buf.toString ();
		}
		return "";
	}

	/**
	 * parenthetical version
	 * @param factors the factors making sub-expression
	 * @param op the text of the operator
	 * @return the formatted display
	 */
	public static String bracketedImage (Factors factors, String op)
	{
		return "( " + image (factors, op) + " )";
	}


}

