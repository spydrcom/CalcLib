
package net.myorb.math.polynomial.algebra;

import net.myorb.math.polynomial.InitialConditionsProcessor;

import net.myorb.math.computational.ArithmeticFundamentals.Scalar;
import net.myorb.math.computational.ArithmeticFundamentals.Conversions;

/**
 * data structures used in solution processing
 * @author Michael Druckman
 */
public class SolutionData extends Utilities
{


	/**
	 * a set of equation to solve with linear algebra
	 */
	public static class SystemOfEquations extends ItemList < Factor >
	{ private static final long serialVersionUID = 2065886325470713453L; }


	/**
	 * symbol table for constants
	 */
	public static class NamedConstant extends IdentifiedValue < Constant >
	{

		NamedConstant
		(Conversions <?> converter) { this.converter = converter; }
		public Conversions <?> converter;

		/**
		 * parse an expression
		 *  and evaluate to resolve to constant
		 * @param name the identifier for the evaluated constant
		 * @param value the text of the expression
		 */
		public void include (String name, String value)
		{
			include (name, converter.fromText (value) );
		}

		/**
		 * use constant taken from double value
		 * @param name the identifier for the evaluated constant
		 * @param value the value to use for this identifier
		 */
		public void include (String name, Double value)
		{
			include (name, converter.convertFromDouble (value) );
		}

		/**
		 * use constant taken from Scalar value
		 * @param name the identifier for the evaluated constant
		 * @param value the value to use for this identifier
		 */
		public void include (String name, Scalar value)
		{
			include (name, new Constant ( converter, value) );			
		}

		private static final long serialVersionUID = 2336556298313040835L;
	}


	/**
	 * map name to pair
	 */
	public static class SymbolValues extends NamedConstant
			implements InitialConditionsProcessor.SymbolTranslator
	{

		SymbolValues (Conversions <?> converter) { super (converter); }

		// computation of Initial Conditions by Processor object

		/**
		 * compute Initial Conditions using Processor object
		 * @param processorName the name of the Processor object
		 */
		public void processIC (String processorName)
		{
			InitialConditionsProcessor.computeInitialConditions (processorName, this);
		}

		// implementation of SymbolTranslator

		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.InitialConditionsProcessor.SymbolTranslator#valueFor(java.lang.String)
		 */
		public Double valueFor (String symbol)
		{
			NamedValue <Constant> content = this.get (symbol);
			errorForNull ( content, "No value for symbol: " + symbol );
			return content.getIdentifiedContent ().getValue ().toDouble ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.InitialConditionsProcessor.SymbolTranslator#set(java.lang.String, java.lang.Double)
		 */
		public void set (String symbol, Double to) { include (symbol, to); }

		// symbol mapping primitives

		/**
		 * add a name/value pair
		 * @param name the name to post the pair as
		 * @param value the value to assign to the name
		 */
		public void add (String name, String value)
		{
			if ( ! name.startsWith (INITIAL_CONDITIONS_PROCESSOR_REFERENCE) )
			{ include ( name, value ); } else { processIC ( value ); }
		}
		public static final String INITIAL_CONDITIONS_PROCESSOR_REFERENCE = "#";

		// trace formatter

		/**
		 * @param stream stream to send symbol list to
		 */
		public void showSymbols (java.io.PrintStream stream)
		{ formatSectionBreak ("", stream); formatSectionBreak (this, stream); stream.println (); }

		private static final long serialVersionUID = 70879534035012284L;

	}


}

