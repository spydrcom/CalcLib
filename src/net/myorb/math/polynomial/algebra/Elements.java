
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

	public static interface Reference { boolean refersTo (String symbol); }
	public static interface Factor { OpTypes getType (); }


	// binary operation factors

	/**
	 * child nodes of multi-factor operations
	 */
	public abstract static class Factors
			extends ArrayList <Factor> implements Factor
	{ private static final long serialVersionUID = 35114701359602093L; }

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
		private static final long serialVersionUID = -5098433414832709926L;
	}

	/**
	 * a set of factors comprising a product
	 */
	public static class Product extends Factors
	{
		public String toString () { return image (this, "*"); }
		private static final long serialVersionUID = 5153646408526934363L;
		public OpTypes getType () { return OpTypes.Multiplication; }
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
		public OpTypes getType () { return OpTypes.Operand; }
		public Constant (String value) { this.value = value; }
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
			Constant exp = new Constant (order.toString ());
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


	/**
	 * construct a reference to a variable
	 * @param variable the identifier referenced
	 * @param order the polynomial term order
	 * @return the appropriate reference to
	 */
	public static Factor powerFactor (String variable, Double order)
	{
		Variable symbol = new Variable (variable);
		if (order != 1) return Power.reference (symbol, order);
		return symbol;
	}


	// complexity management

	/**
	 * check for child nodes
	 * @param factor source to check
	 * @return TRUE when instance-of Factors
	 */
	public static boolean isMultiFactored (Factor factor)
	{
		return factor instanceof Factors;
	}

	/**
	 * check for single child case
	 * @param factors source to check
	 * @return TRUE when child count is one
	 */
	public static boolean hasSingleChild (Factors factors)
	{
		return factors.size () == 1;
	}

	/**
	 * get singleton child node
	 * @param factors source to check
	 * @return child node or NULL when not singleton
	 */
	public static Factor getSingleChild (Factors factors)
	{
		if ( hasSingleChild (factors) )
			return factors.get (0);
		else return null;
	}

	/**
	 * promote single factors
	 * @param factor a factor that may be a single factor set
	 * @return the single factor where appropriate otherwise parameter factor
	 */
	public static Factor reduceSingle (Factor factor)
	{
		if (isMultiFactored (factor))
		{
			Factor child = getSingleChild ( (Factors) factor );
			if ( child != null ) return child;
		}
		return factor;
	}

	/**
	 * check for single grand-child
	 * @param terms the source to check
	 * @return TRUE if contains single grand-child
	 */
	public static boolean simpleReference (Sum terms)
	{
		Factor child = getSingleChild (terms);
		if ( child == null || ! isMultiFactored (child) ) return false;
		return hasSingleChild ( (Factors) child );
	}

	/**
	 * treat factor as factor list when appropriate
	 * @param factor the source node to check for children
	 * @return NULL if not appropriate otherwise child list
	 */
	public static Factors getChildList (Factor factor)
	{
		if ( ! isMultiFactored (factor) ) return null;
		else return (Factors) factor;
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
		int factorCount; StringBuffer buf;
		if ((factorCount = factors.size ()) > 0)
		{
			buf = new StringBuffer ()
				.append (factors.get (0).toString ());
			for (int i = 1; i < factorCount; i++)
			{
				buf.append (op).append (factors.get (i).toString ());
			}
			return buf.toString ();
		}
		return "";
	}
	public static String bracketedImage (Factors factors, String op)
	{
		return "( " + image (factors, op) + " )";
	}


}

