
package net.myorb.math.expressions;

import java.util.HashMap;
import java.util.Map;

/**
 * provide English to unicode mapping of Greek character set
 * @author Michael Druckman
 */
public class GreekSymbols
{


	static final Map<String,String>
	Symbol_Map = new HashMap<String,String>(),
	Special_Cases = new HashMap<String,String>();


	static
	{

		/*
		 * greek letters
		 */

		Symbol_Map.put ("ALPHA", "\u0391");
		Symbol_Map.put ("alpha", "\u03B1");
		Symbol_Map.put ("BETA", "\u0392");
		Symbol_Map.put ("beta", "\u03B2");
		Symbol_Map.put ("GAMMA", "\u0393");
		Symbol_Map.put ("gamma", "\u03B3"); // 03D2
		Symbol_Map.put ("DELTA", "\u0394");
		Symbol_Map.put ("delta", "\u03B4");
		Symbol_Map.put ("EPSILON", "\u0395");
		Symbol_Map.put ("epsilon", "\u03B5");
		Symbol_Map.put ("ZETA", "\u0396");
		Symbol_Map.put ("zeta", "\u03B6");
		Symbol_Map.put ("ETA", "\u0397");
		Symbol_Map.put ("eta", "\u03B7");
		Special_Cases.put ("ETA", "H");
		Special_Cases.put ("eta", "h");
		Symbol_Map.put ("THETA", "\u0398"); // 019F
		Symbol_Map.put ("theta", "\u03B8"); // 03D1
		Special_Cases.put ("THETA", "Q");
		Special_Cases.put ("theta", "q");
		Symbol_Map.put ("IOTA", "\u0399");
		Symbol_Map.put ("iota", "\u03B9");
		Symbol_Map.put ("KAPPA", "\u039A");
		Symbol_Map.put ("kappa", "\u03BA");
		Symbol_Map.put ("LAMBDA", "\u039B");
		Symbol_Map.put ("lambda", "\u03BB");
		Symbol_Map.put ("MU", "\u039C");
		Symbol_Map.put ("mu", "\u03BC");
		Symbol_Map.put ("NU", "\u039D");
		Symbol_Map.put ("nu", "\u03BD");
		Symbol_Map.put ("XI", "\u039E");
		Symbol_Map.put ("xi", "\u03BE");
		Symbol_Map.put ("OMICRON", "\u039F");
		Symbol_Map.put ("omicron", "\u03BF");
		Symbol_Map.put ("PI", "\u03A0");
		Symbol_Map.put ("pi", "\u03C0");
		Symbol_Map.put ("RHO", "\u03A1");
		Symbol_Map.put ("rho", "\u03C1");
		Symbol_Map.put ("SIGMA", "\u03A3"); // 01A9
		Symbol_Map.put ("sigma", "\u03C3");
		Symbol_Map.put ("TAU", "\u03A4");
		Symbol_Map.put ("tau", "\u03C4");
		Symbol_Map.put ("UPSILON", "\u03A5");
		Symbol_Map.put ("upsilon", "\u03C5");
		Symbol_Map.put ("PHI", "\u03A6"); // 0278
		Symbol_Map.put ("phi", "\u03D5"); // 03C6
		Special_Cases.put ("PHI", "F");
		Special_Cases.put ("phi", "f");
		Symbol_Map.put ("CHI", "\u03A7");
		Symbol_Map.put ("chi", "\u03C7");
		Symbol_Map.put ("PSI", "\u03A8");
		Symbol_Map.put ("psi", "\u03C8");
		Special_Cases.put ("PSI", "Y");
		Special_Cases.put ("psi", "y");
		Symbol_Map.put ("OMEGA", "\u03A9"); // 0398 038F
		Symbol_Map.put ("omega", "\u03C9");
		Special_Cases.put ("OMEGA", "W");
		Special_Cases.put ("omega", "w");

		ConventionalNotations.addNotationCollection (Symbol_Map);

	}


	public static String findNotationFor (String name)
	{ return ConventionalNotations.findNotationFor (name); }

	public static Map<String,String> getEnglishToGreekMap () { return Symbol_Map; }
	public static String getSpecialCaseFor (String name) { return Special_Cases.get (name); }


	/**
	 * check for derivative syntax on function which uses Greek symbol name
	 * @param name the name of the symbol to find notation for
	 * @return the notation to use for symbol
	 */
	public static String determineNotationFor (String name)
	{
		if (!name.endsWith (PRIME)) return lookupNotationFor (name);
		return lookupNotationFor (name.substring (0, name.length () - 1)) + PRIME;
	}
	public static String lookupNotationFor (String name)
	{
		String notation = findNotationFor (name);
		return notation == null ? name : notation;
	}
	static final String PRIME = OperatorNomenclature.PRIME_OPERATOR;


}

