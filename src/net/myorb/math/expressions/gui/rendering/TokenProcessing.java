
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

/**
 * process tokens identified as operators or delimiters
 * @author Michael Druckman
 */
public class TokenProcessing extends SubExpressionContexts
{


	/**
	 * look for array start token.
	 *  process range descriptor if found.
	 * @param seq the token sequence being processed
	 * @return TRUE = array was processed
	 */
	public boolean arrayFoundAndRangeProcessed (TokenSequence seq)
	{
		if (!isArrayStart (seq.currentTokenImage ())) return false;
		seq.mark (); while (!seq.atEnd () && !isArrayEnd (seq.next ())) {}
		if (seq.atEnd ()) throw new RuntimeException ("Malformed range description");
		processRangeDescription (seq);
		return true;
	}


	/**
	 * change subtraction to negate
	 * @param operator the original subtraction operator
	 * @return subtraction or negate depending on context
	 */
	public SymbolMap.Operation checkForNegate (SymbolMap.Operation operator)
	{
		if (lastTokenTypeWas == TokenParser.TokenType.OPR && !lastTokenWas.startsWith (")"))
		{ return getNegateOperationSymbol (); }
		else return operator;
	}
	public void lastTokenWas (TokenParser.TokenType type, String image)
	{ lastTokenTypeWas = checkLastType (type); lastTokenWas = image; }
	private TokenParser.TokenType lastTokenTypeWas = null;
	private String lastTokenWas = null;


	/**
	 * establish context of recognized operator
	 * @param token the token symbol recognized as an operator
	 */
	public void processOperatorOrDelimiter (String token)
	{
		boolean save = true;
		SymbolMap.Named sym = forceSpecification (token);
		SymbolMap.Operation operator = establishOperation (sym);

		if (isSubtraction (operator.getName ()))
		{
			// potentially change subtraction operation to negate
			setPrecedenceTo (operator = checkForNegate (operator));
			token = operator.getName ();
		}

		if (operator instanceof SymbolMap.Delimiter) save = processDelimiter ();
		else if (operator instanceof SymbolMap.CalculusOperator) save = processModifier (operator);
		else processOperator ();

		if (save)
		{
			savePrecedence ();
			setLastOp (token);
		}
	}


	/**
	 * perform stack reduction by operation precedence
	 */
	public void processOperator ()
	{
		while
		(
			hasLowerPrecedence () &&
			getPrec () > SymbolMap.ASSIGNMENT_PRECEDENCE &&
			stillAbleToPop ()
		) {}
		pushOp ();
	}


	/**
	 * ' and '' act to modify function behavior
	 * @param op the name of the modification operator
	 * @return FALSE = suppress saving
	 */
	public boolean processModifier (SymbolMap.Operation op)
	{
		SymbolMap.CalculusOperator mod = (SymbolMap.CalculusOperator) op;
		//setLastOp (mod.markupForDisplay (op.getName (), getLastOp (), false, nodeFormater));
		setLastOp (mod.markupForDisplay (op.getName (), getLastOp (), nodeFormater));
		forceSpecification (getLastOp ());
		return false;
	}


	/**
	 * process an operator that functions as a delimiter
	 * @return TRUE = lastOp should retain this delimiter
	 */
	public boolean processDelimiter ()
	{
		int P;
		boolean save = false;

		switch (P = getPrec ())
		{
			case SymbolMap.STORAGE_PRECEDENCE:
			{
				reduceByPrecedence ();
				pushLeafAndOperation ();
				save = true;
			}
			break;

			case SymbolMap.OPEN_GROUP_PRECEDENCE:
			{
				pushParameterStack ();
				pushLeafAndOperation ();
				save = true;
			}
			break;
	
			case SymbolMap.CLOSE_GROUP_PRECEDENCE:
			{
				reduceByPrecedence ();
				popParameterStack ();
				popOp ();
			}
			break;
	
			case SymbolMap.CONTINUE_GROUP_PRECEDENCE:
			{
				reduceByPrecedence ();
				addToParameterTOS ();
			}
			break;
	
			default:  throw new RuntimeException ("Delimiter error, precedence="+P);
		}

		return save;
	}


}

