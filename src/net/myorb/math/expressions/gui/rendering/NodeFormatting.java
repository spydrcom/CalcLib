
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.symbols.AbstractVectorReduction.Range;

/**
 * format markup nodes
 * @author Michael Druckman
 */
public interface NodeFormatting
{

	/**
	 * format outer node of document
	 * @param documentBody the body of the document
	 * @return the formatted node text
	 */
	String formatDocumentRoot (String documentBody);

	/**
	 * format reference to a constant numeric value
	 * @param nodeContents the contents of the resulting node
	 * @return the formatted node text
	 */
	String formatNumericReference (String nodeContents);

	/**
	 * format reference to an operator
	 * @param nodeContents the contents of the resulting node
	 * @return the formatted node text
	 */
	String formatOperatorReference (String nodeContents);

	/**
	 * format reference to an identifier
	 * @param nodeContents the contents of the resulting node
	 * @return the formatted node text
	 */
	String formatIdentifierReference (String nodeContents);

	/**
	 * construct bracket around node
	 * @param nodeContents the contents of the resulting node
	 * @return the formatted node text
	 */
	String formatBracket (String nodeContents);

	/**
	 * construct parenthetical sub-expression node
	 * @param nodeContents the contents of the resulting node
	 * @return the formatted node text
	 */
	String formatParenthetical (String nodeContents);

	/**
	 * construct parenthetical sub-expression node (optionally)
	 * @param nodeContents the contents of the resulting node
	 * @param when TRUE = parenthesis is required
	 * @return the formatted node text
	 */
	String formatParenthetical (String nodeContents, boolean when);

	/**
	 * format node describing binary operation
	 * @param left left side operand of operation
	 * @param operation the operation of the sub-expression
	 * @param right right side operand of operation
	 * @return the formatted node text
	 */
	String formatBinaryOperation (String left, String operation, String right);

	/**
	 * long division expressed as over / under
	 * @param over top expression for the operation
	 * @param under bottom expression for the operation
	 * @return the formatted node text
	 */
	String formatOverUnderOperation (String over, String under);

	/**
	 * formatting for unary operation (postfix)
	 * @param operand the operand of the operation
	 * @param operation the operation of the sub-expression
	 * @return the formatted node text
	 */
	String formatUnaryPostfixOperation (String operand, String operation);

	/**
	 * formatting for unary operation (prefix)
	 * @param operation the operation of the sub-expression
	 * @param operand the operand of the operation
	 * @return the formatted node text
	 */
	String formatUnaryPrefixOperation (String operation, String operand);

	/**
	 * format SQRT expression
	 * @param operand the expression within the SQRT operator
	 * @return the formatted node text
	 */
	String formatSqrtOperation (String operand);

	/**
	 * format Nth root expression
	 * @param operand the operand expression
	 * @param root the value of the root
	 * @return the formatted node text
	 */
	String formatRootOperation (String operand, String root);

	/**
	 * format superscripted expression
	 *  i.e. exponentiation with base and exponent
	 * @param base expression of base of superscripted expression
	 * @param superscript expression of superscript
	 * @return the formatted node text
	 */
	String formatSuperScript (String base, String superscript);

	/**
	 * format subscripted expression
	 *  i.e. element reference from dimensioned object
	 * @param dimensioned expression of dimensioned reference
	 * @param subscript expression of subscript
	 * @return the formatted node text
	 */
	String formatSubScript (String dimensioned, String subscript);

	/**
	 * format an assignment representation
	 * @param destination the description of the assignment destination
	 * @param source the expression result to be assigned
	 * @return the formatted node text
	 */
	String formatAssignment (String destination, String source);

	/*
	 * special case notations
	 */

	public enum Bractets { PAREN, SQUARE, ANGLE, CURLY }

	/**
	 * format bracketed over/under
	 * @param over count of items in set
	 * @param under taken a number at a time
	 * @param bractetType
	 * @return the formatted node text
	 */
	String formatBracketed (String over, String under, Bractets bractetType);

	/**
	 * format a Pochhammer Rising
	 * @param value source being manipulated
	 * @param count number of factors rising
	 * @return the formatted node text
	 */
	String formatPochhammerRising (String value, String count);

	/*
	 * notations that contain range specifications
	 */

	/**
	 * format integral, sigma, PI, ... notations
	 * @param rangeNotation the symbol that identifies the type of notation altered to the specific range
	 * @param parameters the expression that are being evaluated over the range
	 * @return the formatted node text
	 */
	String rangeSpecificationNotation (String rangeNotation,  String parameters);

	/*
	 * formatters for operators that invoke range specification notations
	 */

	/**
	 * indefinite integral
	 * @param operator the specific integration operation
	 * @return the formatted node text
	 */
	String integralIndefinite (String operator);

	/**
	 * definite integral notation
	 * @param operator the specific integration operation
	 * @param range the range specified in the notation
	 * @return the formatted range node text
	 */
	String integralRange (String operator, Range range);

	/**
	 * reduction operator notations
	 * @param operator the specific reduction operation (sigma, PI, ...)
	 * @param range the range specified in the notation
	 * @return the formatted range node text
	 */
	String indexedRange (String operator, Range range);

	/**
	 * contour integral
	 * @param operator the specific integration operation (INTEGRALC)
	 * @return the formatted range node text
	 */
	String contourRange (String operator);

}

