
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.symbols.AbstractVectorReduction.Range;

/**
 * static helper methods for building MML symbol reference constructs
 * @author Michael Druckman
 */
public class Atomics
{


	/**
	 * construct an MML operator reference for a standard expression language operator
	 * @param operatorIdentifier the symbol used in the standard expression language
	 * @param using the node formatting support object supplied for the render
	 * @return mark-up for the operation
	 */
	public static String operatorReference (String operatorIdentifier, NodeFormatting using)
	{
		String renderAs = operatorIdentifier;
		String identifiedNotation = ConventionalNotations.findMarkupFor (renderAs);
		return using.formatOperatorReference (identifiedNotation==null? renderAs: identifiedNotation);
	}


	/**
	 * construct a reference to a multiplication operator
	 * @param using the node formatting support object supplied for the render
	 * @return mark-up for a multiplication operation
	 */
	public static String multiplicationOperatorReference (NodeFormatting using)
	{
		return operatorReference (OperatorNomenclature.MULTIPLICATION_OPERATOR, using);
	}


	/**
	 * construct a reference to a negation operator
	 * @param using the node formatting support object supplied for the render
	 * @return mark-up for a negation operation
	 */
	public static String negateOperatorReference (NodeFormatting using)
	{
		return operatorReference (OperatorNomenclature.SUBTRACTION_OPERATOR, using);
	}


	/**
	 * check for a notation for an identifier
	 * @param symbol the identifier used in the reference
	 * @param using the node formatting support object supplied for the render
	 * @return mark-up for an identifier reference
	 */
	public static String reference (String symbol, NodeFormatting using)
	{
		String identifier =
			ConventionalNotations.determineNotationFor (symbol);
		return using.formatIdentifierReference (identifier);
	}


	/**
	 * format non-local operation reference
	 * @param identifier the identifier that symbolizes the operation
	 * @param range the range for the operation identifying the local
	 * @param order the order of the operation to be performed
	 * @return the formatted operation reference
	 */
	public static String nonLocalOperation
		(
			String identifier,
			Range range, String order
		)
	{
		String text =

				"  <mrow>" +
				"    <msub>" + 
				"      <mrow><mo>.</mo></mrow>" + 
						range.getLoBound () +
				"    </msub>" +
				"  </mrow>" +

				"  <mrow>" +
				"    <msubsup>" +
				"      <mrow> <mi>" + identifier + "</mi> </mrow>" +
				"      <mrow>" + range.getHiBound () + "</mrow>" +
				"      <mrow>" + order + "</mrow>" +
				"    </msubsup>" +
				"  </mrow>";

		return text;
	}


}

