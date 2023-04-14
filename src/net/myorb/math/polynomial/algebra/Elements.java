
package net.myorb.math.polynomial.algebra;

import net.myorb.math.polynomial.algebra.SolutionData.NameValuePair;
import net.myorb.math.polynomial.algebra.SolutionData.SymbolValues;
import net.myorb.math.polynomial.OP;

/**
 * representations of polynomial expressions
 * @author Michael Druckman
 */
public abstract class Elements
{


	/**
	 * list of items of specified object type
	 * @param <T> type of items
	 */
	public static class ItemList <T> extends java.util.ArrayList <T>
	{
		public ItemList () {}
		public ItemList (java.util.List <T> items) { this.addAll (items); }
		private static final long serialVersionUID = -4619972438389917002L;
	}

	/**
	 * lists of symbols
	 */
	public static class TextItems extends ItemList <String>
	{ private static final long serialVersionUID = -58202141273735090L; }


	/**
	 * type of element nodes
	 */
	public enum OpTypes {Summation, Negation, Multiplication, Operand}


	/**
	 * enumeration sets of Symbolic References
	 */
	public static class SymbolicReferences extends java.util.HashSet <String>
	{
		/**
		 * @return expected single symbol referenced
		 * @throws RuntimeException when more than one symbol referenced
		 */
		public String getReferencedSymbol () throws RuntimeException
		{
			if (isEmpty ()) return null;
			if (size () > 1) throw new RuntimeException ("Term not reduced");
			return toArray (EMPTY) [0];
		}
		private static final long serialVersionUID = -3309761616352672364L;
		protected static final String [] EMPTY = new String [] {};
	}

	/**
	 * enumerated list of symbol names
	 */
	public static class SymbolList extends TextItems
	{ private static final long serialVersionUID = -1596326735237091669L; }


	// atomic Factor description base

	/**
	 * provide response to query of referenced symbols
	 */
	public static interface References
	{
		/**
		 * @param symbols a set that will be set to referenced symbols
		 */
		void identify (SymbolicReferences symbols);
	}

	/**
	 * provide response to query of specific symbol reference
	 */
	public static interface Reference
	{
		/**
		 * @param symbol identifier to check
		 * @return TRUE when symbol is referenced by node
		 */
		boolean refersTo (String symbol);
	}

	/**
	 * provide response to query of node type
	 */
	public static interface Factor extends References
	{
		/**
		 * Factor objects must carry support for node Arithmetic
		 * @return the assigned Arithmetic converter
		 */
		Arithmetic.Conversions <?> getConverter ();

		/**
		 * @return type of implementing node
		 */
		OpTypes getType ();
	}


	// binary operation factors

	/**
	 * child nodes of multi-factor operations
	 */
	public abstract static class Factors extends ItemList <Factor> implements Factor
	{
		public Factors (Arithmetic.Conversions <?> converter) { this.converter = converter; }
		public Arithmetic.Conversions <?> getConverter () { return converter; }
		public Arithmetic.Conversions <?> converter;

		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.algebra.Elements.References#identify(net.myorb.math.polynomial.algebra.Elements.SymbolicReferences)
		 */
		public void identify (SymbolicReferences symbols) { for (Factor factor : this) factor.identify (symbols); }

		public static Factor firstOf (Factor factors) { return ( (Factors) factors ).getFirstChild (); }
		public boolean isSingleton () { return this.size () == 1; }
		public Factor getFirstChild () { return this.get (0); }

		private static final long serialVersionUID = 35114701359602093L;
	}

	/**
	 * a set of factors comprising a sum
	 */
	public static class Sum extends Factors
	{
		public Sum (Arithmetic.Conversions <?> converter) { super (converter); }
		public String toString () { return bracketedImage (this, OP.PLUS); }
		private static final long serialVersionUID = -5102897249367062053L;
		public OpTypes getType () { return OpTypes.Summation; }
	}

	/**
	 * sum extension indicating subtraction
	 */
	public static class Difference extends Sum
	{
		public Difference (Arithmetic.Conversions <?> converter) { super (converter); }
		public String toString () { return bracketedImage (this, OP.MINUS); }
		private static final long serialVersionUID = -5098433414832709926L;
	}

	/**
	 * mark factor as negative
	 */
	public static class Negated implements Factor
	{
		public Arithmetic.Conversions <?> converter;
		public Negated (Arithmetic.Conversions <?> converter) { this.converter = converter; }
		public Arithmetic.Conversions <?> getConverter () { return converter; }

		public OpTypes getType () { return OpTypes.Negation; }
		public Negated (Factor factor) { this.child = factor; }
		public void identify (SymbolicReferences symbols) { child.identify (symbols); }
		public Factor getFactor () { return child; }
		private Factor child;
	}

	/**
	 * a set of factors comprising a product
	 */
	public static class Product extends Factors
	{
		public String toString ()
		{ return image (this, OP.TIMES); }
		public OpTypes getType () { return OpTypes.Multiplication; }
		private static final long serialVersionUID = 5153646408526934363L;
		public Product (Arithmetic.Conversions <?> converter) { super (converter); }
		public Product (Arithmetic.Conversions <?> converter, Factor factor)
		{ super (converter); Utilities.add (factor, this); }
	}


	// operand factors

	/**
	 * a factor made of a constant value
	 */
	public static class Constant implements Factor
	{

		public Arithmetic.Conversions <?> converter;
		public Constant (Arithmetic.Conversions <?> converter) { this.converter = converter; }
		public Arithmetic.Conversions <?> getConverter () { return converter; }

		// factor abstraction compliance

		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.algebra.Elements.Factor#getType()
		 */
		public OpTypes getType () { return OpTypes.Operand; }
		public void identify (SymbolicReferences symbols) {}

		// image processing

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString () { return formatImage ( value.toString () ); }

		/**
		 * truncate trailing zeros
		 * @param text the value to process
		 * @return truncated where appropriate
		 */
		public String formatImage (String text)
		{ return ! representsInteger (text) ? text : asInteger (text); }
		private static String asInteger (String text) { return text.substring (0, text.length () - 2); }
		public static boolean representsInteger (String text) { return text.endsWith (".0"); }

		// constructors

		/**
		 * accept a scalar as value representation for a Constant
		 * @param converter the conversions manager for the values
		 * @param value source from double value
		 */
		public Constant (Arithmetic.Conversions <?> converter, Arithmetic.Scalar value)
		{ this (converter); this.value = value; }

		// value processing

		/**
		 * determine if constant should be ignored
		 * @param filter the value that should be ignored if seen
		 * @return TRUE when constant not the filtered value
		 */
		public boolean otherThan (Arithmetic.Scalar filter)
		{
			if ( filter == null ) return true;
			if ( value.EQ (filter) ) return false;
			return true;
		}

		/**
		 * build negative valued constant
		 * @return a constant with negative value of THIS
		 */
		public Constant negated () { return new Constant ( converter, value.negated () ); }

		/**
		 * get negated value of constant factor
		 * @param constant a factor expected to be a Constant object
		 * @return the negated value Constant
		 */
		public static Constant negated (Factor constant) { return ( (Constant) constant ).negated (); }

		/**
		 * @return value of constant as double float
		 */
		public Arithmetic.Scalar getValue () { return value; }
		public static Arithmetic.Scalar getValueFrom (Factor factor)
		{ return ( (Constant) factor ).getValue (); }
		private Arithmetic.Scalar value;

	}

	/**
	 * a factor made of a symbolic identifier
	 */
	public static class Variable implements Factor, Reference
	{
		public Arithmetic.Conversions <?> converter;
		public Arithmetic.Conversions <?> getConverter () { return converter; }
		public Variable (Arithmetic.Conversions <?> converter) { this.converter = converter; }
		public Variable (Arithmetic.Conversions <?> converter, String identifier)
		{ this (converter); this.identifier = identifier; }

		public void identify
		(SymbolicReferences symbols) { symbols.add (identifier); }
		public OpTypes getType () { return OpTypes.Operand; }
		public String toString () { return identifier; }
		public boolean refersTo (String symbol)
		{ return identifier.equals (symbol); }
		private String identifier;
	}

	/**
	 * a description of base-to-exponent factors
	 */
	public static class Power extends Factors implements Reference
	{
		public Power
		(Arithmetic.Conversions <?> converter)
		{ super (converter); }

		public boolean refersTo (String symbol)
		{ return ( (Reference) base () ).refersTo (symbol); }

		private static final long serialVersionUID = -7726638099953294189L;
		public String toString () { return base () + OP.POW + exponent (); }

		public OpTypes getType () { return OpTypes.Operand; }
		public Factor exponent () { return this.get (1); }
		public Factor base () { return this.get (0); }

		/**
		 * establish convention of scalar product with power
		 * @return product wrapper for power object
		 * @param <T> data type
		 */
		public <T> Factor powerProduct ()
		{
			// ONE will fold into scalar multiplied with other factors
			Constant ONE = new Constant (converter, converter.getOne ());
			Factor product = new Product (converter, ONE);
			Utilities.add (this, product);
			return product;
		}

		/**
		 * compute value of a constant power expression
		 * @param symbolTable symbols recognized as constant
		 * @return the computed value
		 */
		public Arithmetic.Scalar evaluate (SymbolValues symbolTable)
		{
			Variable v = (Variable) base ();
			NameValuePair nvp = symbolTable.get ( v.toString () );
			Utilities.errorForNull ( nvp, "Non constant power base" );
			Arithmetic.Scalar exponent = ( (Constant) exponent () ).getValue ();
			return nvp.getNamedValue ().pow ( exponent );
		}

		/**
		 * build a power expression with base and order
		 * @param variable symbol to use as base of power expression
		 * @param order the exponent value for the term
		 * @return the constructed Power Factor
		 */
		public static Factor reference
		(Variable variable, Arithmetic.Scalar order)
		{
			Factors power = new Power (variable.converter);
			Constant exp = new Constant (variable.converter, order);
			power.add (variable); power.add (exp);
			return power;
		}
	}


	// root equation description

	/**
	 * a sub-class of Sum providing a root node of equations
	 */
	public static class Equation extends Sum
	{
		public Equation (Arithmetic.Conversions <?> converter) { super (converter); }
		private static final long serialVersionUID = 8507384482661667874L;
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
		int factorCount; StringBuffer buf; Factor next;
		if ((factorCount = factors.size ()) > 0)
		{
			buf = new StringBuffer ()
				.append (factors.get (0).toString ());
			for (int i = 1; i < factorCount; i++)
			{
				if ( (next = Utilities.reducedForm (factors.get (i))) instanceof Negated )
				{ buf.append (OP.MINUS).append ( ( (Negated) next ).getFactor () ); }
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
	{ return OPEN + image (factors, op) + CLOSE; }
	static final String CLOSE = " " + OP.CLOSE;
	static final String OPEN = OP.OPEN + " ";


}

