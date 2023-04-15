
package net.myorb.math.polynomial.algebra;

import net.myorb.math.polynomial.InitialConditionsProcessor;

import net.myorb.math.computational.ArithmeticFundamentals;

/**
 * data structures used in solution processing
 * @author Michael Druckman
 */
public class SolutionData extends Utilities
{


	/**
	 * a set of equation to solve with linear algebra
	 */
	public static class SystemOfEquations extends ItemList <Factor>
	{ private static final long serialVersionUID = 2065886325470713453L; }


	/**
	 * text value name with Constant value
	 */
	public static class NameValuePair
	{

		public NameValuePair (String name, Constant value)
		{this.nameOfSymbol = name; this.namedValue = value; }
		public String getNameOfValue () { return nameOfSymbol; }
		private String nameOfSymbol;

		public Constant getConstantValue () { return namedValue; }
		public String toString () { return namedValue.toString (); }
		public ArithmeticFundamentals.Scalar getNamedValue ()
		{ return namedValue.getValue (); }
		private Constant namedValue;

	}


	/**
	 * map name to pair
	 */
	public static class SymbolValues extends SymbolicMap <NameValuePair>
			implements InitialConditionsProcessor.SymbolTranslator
	{

		SymbolValues
		(ArithmeticFundamentals.Conversions <?> converter) { this.converter = converter; }
		public ArithmeticFundamentals.Conversions <?> converter;

		/**
		 * @param stream stream to send symbol list to
		 */
		public void showSymbols (java.io.PrintStream stream)
		{ formatSectionBreak ("", stream); formatSectionBreak (this, stream); stream.println (); }

		// computation of Initial Conditions by Processor object

		/**
		 * compute Initial Conditions using Processor object
		 * @param processorName the name of the Processor object
		 */
		public void processIC (String processorName)
		{
			InitialConditionsProcessor.computeInitialConditions (processorName, this);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.InitialConditionsProcessor.SymbolTranslator#valueFor(java.lang.String)
		 */
		public Double valueFor (String symbol)
		{
			NameValuePair content = this.get (symbol);
			errorForNull ( content, "No value for symbol: " + symbol );
			return content.getNamedValue ().toDouble ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.InitialConditionsProcessor.SymbolTranslator#set(java.lang.String, java.lang.Double)
		 */
		public void set (String symbol, Double to)
		{
			add ( symbol, new Constant ( converter, converter.convertFromDouble (to) ) );
		}

		// symbol mapping primitives

		/**
		 * assign constant to symbol
		 * @param name the name for the symbol
		 * @param value the value for the symbol
		 * @return the NameValuePair constructed
		 */
		public NameValuePair pair (String name, String value)
		{ return new NameValuePair ( name, toConstant (value) ); }

		/**
		 * parse an expression and evaluate to resolve to  constant
		 * @param value the text of the expression
		 * @return the value as a Constant
		 */
		public Constant toConstant (String value) { return new Constant ( converter, converter.fromText (value) ); }

		/**
		 * add a Name/Value pair to a symbol
		 * @param name the name for the symbol
		 * @param value the value to assign to the name
		 */
		public void add (String name, Constant value) { this.put (name, new NameValuePair (name, value)); }

		/**
		 * add a name/value pair
		 * @param name the name to post the pair as
		 * @param value the value to assign to the name
		 */
		public void add (String name, String value)
		{
			if ( ! name.startsWith (INITIAL_CONDITIONS_PROCESSOR_REFERENCE) )
			{ this.put (name, this.pair ( name, value ) ); }
			else processIC (value);
		}
		public static final String INITIAL_CONDITIONS_PROCESSOR_REFERENCE = "#";
		private static final long serialVersionUID = 70879534035012284L;

	}


}

