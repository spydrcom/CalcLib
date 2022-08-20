
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

/**
 * context established by proximity of tokens
 * @author Michael Druckman
 */
public class SubExpressionContexts extends SymbolProcessing
{


	private static final String PRIME		= OperatorNomenclature.PRIME_OPERATOR;
	private static final String OPEN		= OperatorNomenclature.START_OF_GROUP_DELIMITER;
	private static final String START_ARRAY	= OperatorNomenclature.START_OF_ARRAY_DELIMITER;
	private static final String END_ARRAY	= OperatorNomenclature.END_OF_ARRAY_DELIMITER;
	private static final String MINUS		= OperatorNomenclature.SUBTRACTION_OPERATOR;
	private static final String NEGATE		= OperatorNomenclature.NEGATE_OPERATOR;


	/*
	 * recognize operation syntax
	 */


	public boolean isParenthesis (String token) { return OPEN.equals (token); }			// open parenthesis token
	public boolean isSubtraction (String token) { return MINUS.equals (token); }		// subtraction operator token
	public boolean isDerivative  (String token) { return token.startsWith (PRIME); }	// prime (') character token

	public boolean isArrayStart  (String token) { return START_ARRAY.equals (token); }	// start array syntax [
	public boolean isArrayEnd    (String token) { return END_ARRAY.equals (token); }	// end array syntax ]

	public SymbolMap.Operation getNegateOperationSymbol ()								// NEGATE as opposed to subtract
	{ return lookupOperation (NEGATE); }


	/*
	 * token context
	 */


	/**
	 * examine context of reference
	 * @param seq the token sequence being processed
	 */
	public void lookAhead (TokenSequence seq)
	{
		String nextTokenImage;
		contextImpliesFunction = false;
		if ((nextTokenImage = seq.peekAtNext ()) == null) return;
		boolean recognizeParameterList = isParenthesis (nextTokenImage);
		contextImpliesFunction = recognizeParameterList || isDerivative (nextTokenImage);
	}
	private boolean contextImpliesFunction = false;


	/**
	 * check context of token
	 * @param token the text of the token
	 * @return TRUE = token is operator
	 */
	public boolean contextIndicatesOperator (String token)
	{
		return contextImpliesFunction || isIdentifiedAsOperation (token);
	}


	/**
	 * collect token context
	 * @param seq description of the sequence and current token
	 * @return the type from context
	 */
	public TokenParser.TokenType checkContext (TokenSequence seq)
	{
		TokenParser.TokenType
			type = seq.currentTokenType ();
		if (type != TokenParser.TokenType.IDN) return type;
		if (contextIndicatesOperator (seq.currentTokenImage ()))
		{ return TokenParser.TokenType.OPR; }
		return TokenParser.TokenType.IDN;
	}


	/**
	 * treat function as identifier so NEGATE will not be invoked
	 * @param tokenType type of last token
	 * @return type to be used
	 */
	public TokenParser.TokenType checkLastType (TokenParser.TokenType tokenType)
	{
		if (tokenType == TokenParser.TokenType.OPR)
		{
			SymbolMap.Operation op = lookupLastOperation ();

			if
				(
					op instanceof SymbolMap.ParameterizedFunction ||
					op instanceof SymbolMap.UnaryOperator
				)
			{
				return TokenParser.TokenType.IDN;
			}
		}

		return tokenType;
	}


	/**
	 * perform stack reduction
	 */
	public void reduce ()
	{
		reduce (lookupLastOperation ());
	}


	/**
	 * reduce and check stack
	 * @return TRUE = item successfully popped after reduce
	 */
	public boolean stillAbleToPop ()
	{
		reduce ();
		return popOp ();
	}


	/**
	 * pop operation stack while
	 * current operation precedence
	 * is less than top of stack
	 */
	public void reduceByPrecedence ()
	{
		while (hasLowerPrecedence () && stillAbleToPop ()) {}
	}


	/**
	 * push both current leaf and current operation
	 */
	public void pushLeafAndOperation ()
	{
		pushLeaf (); setLastLeaf (""); pushOp ();
	}


}

