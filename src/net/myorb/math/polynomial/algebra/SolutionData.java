
package net.myorb.math.polynomial.algebra;

import net.myorb.math.polynomial.InitialConditionsProcessor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * data structures used in solution processing
 * @author Michael Druckman
 */
public class SolutionData extends Utilities
{


	/**
	 * a set of equation to solve with linear algebra
	 */
	public static class SystemOfEquations extends ArrayList <Factor>
	{ private static final long serialVersionUID = 2065886325470713453L; }


	/**
	 * text value name with Constant value
	 */
	public static class NameValuePair
	{
		public String getNameOfValue () { return nameOfSymbol; }
		public Constant getConstantValue () { return namedValue; }
		public NameValuePair (String name, Constant value) {this.nameOfSymbol = name; this.namedValue = value; }
		public NameValuePair (String name, String value) { this (name, new Constant (value)); }
		public double getNamedValue () { return namedValue.getValue (); }
		public String toString () { return namedValue.toString (); }
		private String nameOfSymbol; private Constant namedValue;
	}


	/**
	 * map name to pair
	 */
	public static class SymbolValues extends HashMap <String, NameValuePair>
			implements InitialConditionsProcessor.SymbolTranslator
	{
		/**
		 * @param stream stream to send symbol list to
		 */
		public void showSymbols (java.io.PrintStream stream)
		{ stream.println ("==="); stream.println (this); stream.println ("==="); stream.println (); }

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
			NameValuePair content;
			if ( (content = this.get (symbol)) == null )
			{ throw new RuntimeException ("No value for symbol: " + symbol); }
			return content.getNamedValue ();
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.InitialConditionsProcessor.SymbolTranslator#set(java.lang.String, java.lang.Double)
		 */
		public void set (String symbol, Double to)
		{
			add ( symbol, new Constant (to) );
		}

		// symbol mapping primitives

		/**
		 * add a name/value pair
		 * @param name the name to post the pair as
		 * @param value the value to assign to the name
		 */
		public void add (String name, String value)
		{
			if ( ! name.startsWith (INITIAL_CONDITIONS_PROCESSOR_REFERENCE) )
			{ this.put (name, new NameValuePair (name, value)); }
			else processIC (value);
		}
		public void add (String name, Constant value) { this.put (name, new NameValuePair (name, value)); }
		public static final String INITIAL_CONDITIONS_PROCESSOR_REFERENCE = "#";
		private static final long serialVersionUID = 70879534035012284L;
	}


}

