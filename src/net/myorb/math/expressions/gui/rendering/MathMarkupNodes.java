
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.symbols.AbstractVectorReduction.Range;

import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.GreekSymbols;

import java.util.HashMap;

/**
 * formatter for MathML document type
 * @author Michael Druckman
 */
public class MathMarkupNodes implements NodeFormatting
{

	public static final String
				ROOT_TAG		= "math",
				SPACE_TAG		= "mspace",
				IDENTIFIER_TAG	= "mi",
				OPERATOR_TAG	= "mo",
				VALUE_TAG		= "mn",
				BRACKET_TAG		= "mrow",
				FENCED_TAG		= "mfenced",
				SUP_TAG			= "msup",
				SUB_TAG			= "msub",
				FRAC_TAG		= "mfrac",
				SQRT_TAG		= "msqrt",
				NROOT_TAG		= "mroot",
				SUM_RANGE_TAG	= "munderover",
				INT_RANGE_TAG	= "msubsup";

	/**
	 * construct node of contents enclosed within tag pair
	 * @param contents the contents to be placed inside node
	 * @param attributes formatted attribute name='value' pairs
	 * @param tag the name of the tag to be used
	 * @return the formatted node text
	 */
	public static String enclose (String contents, String attributes, String tag)
	{
		String formatted, tagAttributed = tag + attributes;
		if (contents == null) formatted = "<" + tagAttributed + " />";
		else formatted = "<" + tagAttributed + ">" + contents + "</" + tag + ">";
		if (MathML.DUMPING) System.out.println("enclosed: " + formatted);
		return formatted;
	}
	public static String enclose (String contents, String tag)
	{ return enclose (contents, "", tag); }

	/**
	 * insert space between rendered items
	 * @param size count of pixels in the generated space
	 * @return the text of the spacing node
	 */
	public static String space (String size)
	{
		return enclose (null, " width='" + size + "px'", SPACE_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatDocumentRoot(java.lang.String)
	 */
	public String formatDocumentRoot (String documentBody)
	{
		return enclose (documentBody, ROOT_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatIdentifierReference(java.lang.String)
	 */
	public String formatIdentifierReference (String nodeContents)
	{
		String id = nodeContents.replaceAll
			(OperatorNomenclature.PRIME_OPERATOR, OperatorNomenclature.PRIME_RENDER);
		return enclose (id, IDENTIFIER_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatNumericReference(java.lang.String)
	 */
	public String formatNumericReference (String nodeContents)
	{
		return enclose (nodeContents, VALUE_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatOperatorReference(java.lang.String)
	 */
	public String formatOperatorReference (String nodeContents)
	{
		return enclose (nodeContents, OPERATOR_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatBracket(java.lang.String)
	 */
	public String formatBracket (String nodeContents)
	{
		return enclose (nodeContents, BRACKET_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatParenthetical(java.lang.String)
	 */
	public String formatParenthetical (String nodeContents)
	{
		return enclose (nodeContents, FENCED_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatParenthetical(java.lang.String, boolean)
	 */
	public String formatParenthetical (String nodeContents, boolean when)
	{
		if (!when) return nodeContents;
		return formatParenthetical (nodeContents);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatBinaryOperation(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String formatBinaryOperation (String left, String operation, String right)
	{
		return left + formatOperatorReference (operation) + right;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatUnaryPostfixOperation(java.lang.String, java.lang.String)
	 */
	public String formatUnaryPostfixOperation (String operand, String operation)
	{
		return operand + formatOperatorReference (operation);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatUnaryPrefixOperation(java.lang.String, java.lang.String)
	 */
	public String formatUnaryPrefixOperation (String operation, String operand)
	{
		return formatOperatorReference (operation) + formatParenthetical (operand);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatSuperScript(java.lang.String, java.lang.String)
	 */
	public String formatSuperScript (String base, String superscript)
	{
		return enclose (formatBracket (base) + formatBracket (superscript), SUP_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatSubScript(java.lang.String, java.lang.String)
	 */
	public String formatSubScript (String dimensioned, String subscript)
	{
		return enclose (formatBracket (dimensioned) + formatBracket (subscript), SUB_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatOverUnderOperation(java.lang.String, java.lang.String)
	 */
	public String formatOverUnderOperation (String over, String under)
	{
		return enclose (formatBracket (over) + formatBracket (under), FRAC_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatSqrtOperation(java.lang.String)
	 */
	public String formatSqrtOperation (String operand)
	{
		return enclose (formatBracket (operand), SQRT_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatRootOperation(java.lang.String, java.lang.String)
	 */
	public String formatRootOperation (String operand, String root)
	{
		return enclose (formatBracket (operand) + formatBracket (root), NROOT_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatAssignment(java.lang.String, java.lang.String)
	 */
	public String formatAssignment (String destination, String source)
	{
		return destination + ASSIGN + source;
	}
	public final String ASSIGN = formatOperatorReference (OperatorNomenclature.ASSIGNMENT_DELIMITER);

	/*
	 * higher math notation formatting
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatBracketed(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting.Bractets)
	 */
	public String formatBracketed (String over, String under, Bractets bractetType)
	{
		return formatBracketed (over, under, OPEN.get (bractetType), CLOSE.get (bractetType));
	}

	/**
	 * @param over top part of the over-under pair
	 * @param under bottom part of the over-under pair
	 * @param open the opening bracket character for the pair
	 * @param close the closing bracket character for the pair
	 * @return the formatted text
	 */
	public String formatBracketed (String over, String under, String open, String close)
	{
		return "<mo mathsize='10'>" + open + "</mo><mrow><munderover><mo/>" + under + over + "</munderover></mrow><mo mathsize='10'>" + close + "</mo>";
	}
	static final HashMap < Bractets, String >
		OPEN = new HashMap <> (), CLOSE = new HashMap <> ();
	static
	{
		OPEN.put (Bractets.PAREN,  "&#x00028;");	CLOSE.put (Bractets.PAREN,  "&#x00029;");
		OPEN.put (Bractets.SQUARE, "&#x005B;");		CLOSE.put (Bractets.SQUARE, "&#x005D;");
		OPEN.put (Bractets.CURLY,  "&#x007B;");		CLOSE.put (Bractets.CURLY,  "&#x007D;");
		OPEN.put (Bractets.ANGLE,  "&#x27E8;");		CLOSE.put (Bractets.ANGLE,  "&#x27E9;");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#formatPochhammerRising(java.lang.String, java.lang.String)
	 */
	public String formatPochhammerRising (String value, String count)
	{
		return formatSubScript (formatParenthetical (value), count);
	}

	/**
	 * a display mechanism for delta step specification
	 * @param range the range object being formatted
	 * @return the formatted node text
	 */
	public String getStep (Range range)
	{
		String step;
		if ((step = range.getIncrement ()) != null)
		{
			return DELTA_OP + step;
		}
		return "";
	}
	public final String DELTA = "&#x0394;", DELTA_OP = formatOperatorReference (DELTA);

	/**
	 * the generic format of a range
	 * @param lo the lo value of the range
	 * @param hi the hi value of the range
	 * @return the formatted node text
	 */
	public String generalRange (String lo, String hi)
	{
		return formatBracket (lo) + formatBracket (hi);
	}

	/**
	 * check for error in range
	 * @param range the range object parsed from expression
	 */
	public static void checkRange (Range range)
	{
		if (range == null) { throw new RuntimeException ("Range must be specified"); }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#integralRange(java.lang.String, net.myorb.math.expressions.symbols.AbstractVectorReduction.Range)
	 */
	public String integralRange (String operator, Range range)
	{
		checkRange (range);
		String formattedRange = generalRange (range.getLoBound (), range.getHiBound ());
		return formatRangeNotation (operator, formattedRange, INT_RANGE_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#integralIndefinite(java.lang.String)
	 */
	public String integralIndefinite (String operator)
	{
		return enclose (formatOperatorNotation (operator), BRACKET_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#indexedRange(java.lang.String, net.myorb.math.expressions.symbols.AbstractVectorReduction.Range)
	 */
	public String indexedRange (String operator, Range range)
	{
		checkRange (range);
		String formattedRange = generalRange
		(
			formatIdentifierReference (range.getIdentifier ()) + ASSIGN + range.getLoBound (),
			range.getHiBound () + getStep (range)
		);
		return formatRangeNotation (operator, formattedRange, SUM_RANGE_TAG);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#contourRange(java.lang.String)
	 */
	public String contourRange (String operator)
	{
		String formattedRange = generalRange (CONTOUR_ID, formatNumericReference (null));
		return formatRangeNotation (operator, formattedRange, INT_RANGE_TAG);
	}
	public final String CONTOUR = "C", CONTOUR_ID = formatIdentifierReference (CONTOUR);

	/**
	 * lookup notation and
	 *  build operator reference node
	 * @param operator the operator to be translated to appropriate notation
	 * @return the text of the formatted markup
	 */
	public String formatOperatorNotation (String operator)
	{
		String notation = GreekSymbols.findNotationFor (operator);
		if (MathML.DUMPING) System.out.println ("Notation translation : " + operator + " = " + notation);
		return formatOperatorReference (notation);
	}

	/**
	 * format the symbol with range attributes included
	 * @param operator the operator to be translated to range notation
	 * @param range the Range object with values specific to instance
	 * @param rangeType the tag to be used for formatting
	 * @return the text of the formatted markup
	 */
	public String formatRangeNotation (String operator, String range, String rangeType)
	{
		return enclose (formatOperatorNotation (operator) + range, rangeType);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.rendering.NodeFormatting#rangeSpecificationNotation(java.lang.String, java.lang.String)
	 */
	public String rangeSpecificationNotation (String rangeNotation,  String parameters)
	{
		return rangeNotation + space ("10") + parameters;
	}

}

