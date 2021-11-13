
package net.myorb.math.expressions;

import java.util.HashMap;
import java.util.Map;

/**
 * provide English to unicode mapping of math notation characters
 * @author Michael Druckman
 */
public class ConventionalNotations extends OperatorNomenclature
{


	public static final Map<String,String>
	Character_Map = new HashMap<String,String>(),
	Notations = new HashMap<String,String>();


	public static final String
	DELTA_XML_ESCAPE = "&#x2202;", DELTA_JAVA_ESCAPE = "\u2202";


	static
	{

		// temporary local collection
		Map<String,String> Operator_Map = new HashMap<String,String>();

		/*
		 * arithmetic and algebraic
		 */

		Operator_Map.put (NEGATE_OPERATOR, "-");
		Operator_Map.put (PLUS_OR_MINUS_OPERATOR, "\u00B1");
		Operator_Map.put (MINUS_OR_PLUS_OPERATOR, "\u2213");
		Operator_Map.put (DIVISION_OPERATOR, "\u00F7");
		Operator_Map.put (SQRT_FUNCTION, "\u221A");

		Operator_Map.put ("INFINITY", "\u221E");
		Operator_Map.put ("CROOT", "\u221B");
		Operator_Map.put ("cbrt", "\u221B");
		Operator_Map.put ("NULL", "\u2205");

		//Operator_Map.put ("Re", "\u2118");
		//Operator_Map.put ("Im", "\u1D751");
		//Operator_Map.put ("Im", "\u00CE");
		//Operator_Map.put ("Im", "\u039E");
		//Operator_Map.put ("Im", "\u046E");
		//Operator_Map.put ("Re", "\u0464");
		Operator_Map.put ("Im", "\u0540");
		Operator_Map.put ("Re", "\u054B");

		/*
		 * calculus
		 */

		Operator_Map.put (PRIME_OPERATOR, "\u2032");
		Operator_Map.put (DPRIME_OPERATOR, "\u2032\u2032");

		Operator_Map.put ("PARTIAL", DELTA_JAVA_ESCAPE);						// used to indicate partial derivative
		Operator_Map.put (DELTA_INCREMENT_OPERATOR, "\u0394");					// delta in the sense of an increment in loop covering a range
		Operator_Map.put (DELTA_INTEGRATION_OPERATOR, DELTA_JAVA_ESCAPE);		// the indicator for the differential of the variable (base of rectangles of infinitesimal width)

		Operator_Map.put (INTEGRAL_OPERATOR, "\u222B");
		Operator_Map.put (INTEGRAL_OPERATOR+"I", "\u23B0");						// single integral, indefinite form
		//Operator_Map.put (INTEGRAL_OPERATOR+"D", "\u222C");
		//Operator_Map.put (INTEGRAL_OPERATOR+"T", "\u222D");
		Operator_Map.put (INTEGRAL_OPERATOR+"D", "\u23B0\u23B0");
		Operator_Map.put (INTEGRAL_OPERATOR+"T", "\u23B0\u23B0\u23B0");
		Operator_Map.put (INTEGRAL_OPERATOR+"C", "\u222E");
		Operator_Map.put (INTEGRAL_OPERATOR+"S", "\u222F");
		Operator_Map.put (INTEGRAL_OPERATOR+"V", "\u2230");
		//Operator_Map.put (INTEGRAL_OPERATOR, "\u0283");

		/*
		 * sums and products
		 */

		Operator_Map.put (SUMMATION_OPERATOR, "\u2211");
		Operator_Map.put (TENSOR_OPERATOR, "\u2297");

		Operator_Map.put ("XPRODUCT", "\u22C5");
		Operator_Map.put ("DPRODUCT", "\u22C5");
		Operator_Map.put ("PRODUCT", "\u220F");

		addNotationCollection (Operator_Map);

	}


	public static void
	addNotationCollection (Map<String,String> collection)
	{
		Character_Map.putAll (collection);
		Notations.putAll (collection);
	}

	public static void
	setNotationFor (String operator, String notation)
	{ Notations.put (operator, notation); }

	public static String findNotationFor (String name)
	{ return Character_Map.get (name); }

	public static String determineNotationFor (String name)
	{
		String notation = Character_Map.get (name);
		if (notation == null) return name;
		else return notation;
	}

	public static String findMarkupFor (String name)
	{ return Notations.get (name); }


}

