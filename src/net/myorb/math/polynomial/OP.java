
package net.myorb.math.polynomial;

import net.myorb.math.expressions.OperatorNomenclature;

/**
 * nomenclature for operators specifically being use to express polynomials
 * @author Michael Druckman
 */
public class OP
{

	// derivative indicators
	public static final String FIRST = OperatorNomenclature.PRIME_OPERATOR;
	public static final String SECOND = OperatorNomenclature.DPRIME_OPERATOR;

	// parenthesis specifiers
	public static final String OPEN = OperatorNomenclature.START_OF_GROUP_DELIMITER;
	public static final String CLOSE = OperatorNomenclature.END_OF_GROUP_DELIMITER;

	// binary operations
	public static final String PLUS = " "+OperatorNomenclature.ADDITION_OPERATOR+" ";		// terms format with extra spacing
	public static final String MINUS = " "+OperatorNomenclature.SUBTRACTION_OPERATOR+" ";	// this seems to improve readability
	public static final String TIMES = OperatorNomenclature.MULTIPLICATION_OPERATOR;
	public static final String POW = OperatorNomenclature.POW_OPERATOR;

	// subscript indicator
	public static final String SUB = OperatorNomenclature.SUBSCRIPT_RENDER_TICK;

}
