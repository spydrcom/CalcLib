
package net.myorb.math.polynomial.algebra;

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
	{
		public void showSymbols (java.io.PrintStream stream)
		{ stream.println ("==="); stream.println (this); stream.println ("==="); stream.println (); }
		public void add (String name, String value) { this.put (name, new NameValuePair (name, value)); }
		public void add (String name, Constant value) { this.put (name, new NameValuePair (name, value)); }
		private static final long serialVersionUID = 70879534035012284L;
	}


}
