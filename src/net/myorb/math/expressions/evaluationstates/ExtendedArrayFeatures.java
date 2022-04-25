
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.symbols.AssignedVariableStorage;

import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.TokenParser;

import net.myorb.data.abstractions.ErrorHandling;

import java.util.ArrayList;
import java.util.List;

/**
 * extend array descriptor feature to enable "n &lt; x &lt; m" syntax
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ExtendedArrayFeatures<T> extends Arrays<T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Arrays#process(java.util.List, int, net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public int process
		(
			TokenParser.TokenSequence tokens,
			int tokenPosition, Environment<T> environment
		)
	{
		establish (environment, tokens);
		return new ArrayNotationParser ().parse (tokenPosition)
		.buildFullDescriptor ();
	}


	/**
	 * @param tokens the tokens holding the descriptor
	 * @param tokenPosition the starting position of the descriptor
	 * @param environment a control object for this processor
	 * @return the parsed descriptor
	 */
	public ArrayDescriptor<T> getArrayDescriptor
		(
			TokenParser.TokenSequence tokens,
			int tokenPosition, Environment<T> environment
		)
	{
		establish (environment, tokens);
		return new ArrayNotationParser ().parse (tokenPosition)
		.getArrayDescriptor ();
	}


	/**
	 * parser for notations of form [lo <= var <= hi <> increment] (expression)
	 */
	class ArrayNotationParser
	{

		/**
		 * @param tokenPosition starting position of array description
		 * @return access to THIS parser
		 */
		ArrayNotationParser parse (int tokenPosition)
		{
			int endOfArray;
			starting = tokenPosition + 1;
			endOfArray = expect (starting, END_OF_ARRAY_DELIMITER);
			arrayDescriptor = tokens.between (starting, endOfArray);
			parseDelta (locate (arrayDescriptor, DELTA_INCREMENT_OPERATOR));
			starting = endOfArray + 1;
			return this;
		}

		/**
		 * @return descriptor for array notation parsed
		 */
		ArrayDescriptor<T> getArrayDescriptor ()
		{
			return evaluateDescriptor (arrayDescriptor, null, delta);
		}

		/**
		 * @return index for following token
		 */
		int buildFullDescriptor ()
		{
			int ending = identifyEndOfDescriptor ();
			buildDescriptor (arrayDescriptor, tokens.between (starting, ending), delta);
			return ending;
		}
		int identifyEndOfDescriptor ()
		{
			int ending = parseExpression (tokens, starting);
			if (ending == 0) { throw new ErrorHandling.Terminator ("Unexpected array syntax"); }
			return ending + 1;
		}

		/**
		 * @param deltaDelimiter token index of delta operator, -1 for not found
		 */
		void parseDelta (int deltaDelimiter)
		{
			if (deltaDelimiter > 0)
			{
				delta = computeScalar (arrayDescriptor.between (deltaDelimiter + 1, arrayDescriptor.size ()));
				arrayDescriptor = arrayDescriptor.between (0, deltaDelimiter);
			} else delta = spaceManager.getOne ();
		}

		TokenParser.TokenSequence arrayDescriptor;
		int starting;
		T delta;
	}


	/**
	 * parse tokens and construct appropriate descriptor.
	 *  range descriptions use 'n &lt;= x &lt;= m' syntax, '&lt;' alternative added
	 * @param arrayDescriptor the tokens describing the range 'lo OP identifier OP hi', OP is &lt; or &lt;=
	 * @param expression the tokens that comprise the evaluation of the elements
	 * @param delta the increment value to apply to the range
	 */
	public void buildDescriptor
		(
			TokenParser.TokenSequence arrayDescriptor,
			TokenParser.TokenSequence expression,
			T delta
		)
	{
		ArrayDescriptor<T> descriptor = evaluateDescriptor (arrayDescriptor, expression, delta);
		environment.getValueStack ().push (generateArrayFor (descriptor), descriptor);
	}


	/**
	 * @param arrayDescriptor the tokens describing the range 'lo OP identifier OP hi', OP is &lt; or &lt;=
	 * @param expression the tokens that comprise the evaluation of the elements
	 * @param delta the increment value to apply to the range
	 * @return the descriptor object for described array
	 */
	public ArrayDescriptor<T> evaluateDescriptor
		(
			TokenParser.TokenSequence arrayDescriptor,
			TokenParser.TokenSequence expression,
			T delta
		)
	{
		String symbolName;
		int idPos; boolean loLt = false, hiLt = false;
		int le = locate (arrayDescriptor, LE_OPERATOR), lt = locate (arrayDescriptor, LT_OPERATOR);
		checkSyntax (lt>0 || le>0, "range expression is invalid");

		// parsing of range
		if (lt < 0) { idPos = le + 1; }
		else if (le < 0) { loLt = hiLt = true; idPos = lt + 1; }
		else if (le < lt) { hiLt = true; idPos = le + 1; } else { loLt = true; idPos = lt + 1; }
		checkSyntax (verifyOperators (arrayDescriptor, idPos, loLt, hiLt), "operator (< or <=) expected, not found");

		// processing of range lo-hi
		T loBound = computeScalar (arrayDescriptor.between (0, idPos - 1)),
			hiBound = computeScalar (arrayDescriptor.between (idPos + 2, arrayDescriptor.size ()));
		if (hiLt) hiBound = spaceManager.add (hiBound, spaceManager.negate (delta));
		if (loLt) loBound = spaceManager.add (loBound, delta);

		// get identifier symbol and construct descriptor
		generateDeltaSymbol (symbolName = getIdentifierToken (arrayDescriptor, idPos).getTokenImage (), delta);
		return new ArrayDescriptor<T> (loBound, hiBound, delta, symbolName, arrayDescriptor, expression, spaceManager);
	}
	void generateDeltaSymbol (String symbolName, T delta)
	{
		String symbolDelta = ConventionalNotations.DELTA_JAVA_ESCAPE + symbolName;
		// descriptor increment value is assigned to symbol with name {GREEK DELTA} {symbolName}
		environment.getSymbolMap ().add (new AssignedVariableStorage (symbolDelta, valueManager.newDiscreteValue (delta)));
	}


	/**
	 * produce a value object with the array contents described
	 * @param descriptor the descriptor object describing the intended array contents
	 * @return a generic value holding the organized elements
	 */
	@SuppressWarnings("rawtypes")
	public ValueManager.GenericValue generateArrayFor (ArrayDescriptor<T> descriptor)
	{
		List<ValueManager.GenericValue> elements =
			new ArrayList<ValueManager.GenericValue>();
		fill (elements, descriptor);

		if (elements.size () == 0)
		{
			return valueManager.newDimensionedValue ();
		}

		ValueManager.GenericValue v = elements.get (0);
		if (v instanceof ValueManager.DimensionedValue)
		{
			int size = ((ValueManager.DimensionedValue)v).getValues ().size ();
			if (size > 1) return makeParameterList (elements, size);
		}
		return makeDimensionedList (elements);
	}


	/**
	 * fill a list with the values
	 *  described in the array descriptor
	 * @param elements the list of computed values
	 * @param fromDescriptor the array descriptor being realized
	 */
	public void fill
		(
			List<ValueManager.GenericValue> elements,
			ArrayDescriptor<T> fromDescriptor
		)
	{
		T current = fromDescriptor.getLo (), hi = fromDescriptor.getHi (), delta = fromDescriptor.getDelta ();
		ExpressionMacro<T> macro = fromDescriptor.genMacro (environment, false);

		while (!spaceManager.lessThan (hi, current))
		{
			try
			{
				elements.add (macro.evaluate (current));
			}
			catch (Exception e)
			{
				ErrorHandling.checkForTermination (e);
				elements.add (valueManager.newAsymptoticReference());
			}
			current = spaceManager.add (current, delta);
		}
	}


	/**
	 * organize elements as a parameter list
	 * @param elements the list of values generated from the descriptor
	 * @param size the number of sequences described
	 * @return the parameter list object
	 */
	public ValueManager.GenericValue makeParameterList
		(List<ValueManager.GenericValue> elements, int size)
	{
		List<ValueManager.RawValueList<T>> valuesLists =
					new ArrayList<ValueManager.RawValueList<T>>();
		for (int i = 0; i < size; i++) valuesLists.add (new ValueManager.RawValueList<T>());

		for (ValueManager.GenericValue v : elements)
		{
			List<T> points = valueManager.toDiscreteValues (v);
			for (int i = 0; i < size; i++) { valuesLists.get (i).add (points.get (i)); }
		}

		ValueManager.GenericValueList list = new ValueManager.GenericValueList ();
		for (int i = 0; i < size; i++) { list.add (valueManager.newDimensionedValue (valuesLists.get (i))); }
		return valueManager.newValueList (list);
	}


	/**
	 * organize elements as a dimensioned object
	 * @param elements the list of values generated from the descriptor
	 * @return the parameter list object
	 */
	public ValueManager.GenericValue makeDimensionedList
		(List<ValueManager.GenericValue> elements)
	{
		ValueManager.RawValueList<T> array = new ValueManager.RawValueList<T>();
		for (ValueManager.GenericValue v : elements) { array.add (valueManager.toDiscrete (v)); }
		return valueManager.newDimensionedValue (array);
	}


	/**
	 * check the expected location of the identifier
	 * @param arrayDescriptor the list of token that express the descriptor
	 * @param position the location in the token list the identifier should be found
	 * @return TRUE => identifier found as expected
	 */
	private TokenParser.TokenDescriptor getIdentifierToken
	(List<TokenParser.TokenDescriptor> arrayDescriptor, int position)
	{
		TokenParser.TokenDescriptor identifierToken = arrayDescriptor.get (position);
		checkSyntax (identifierToken.getTokenType() == TokenParser.TokenType.IDN, "identifier expected, not found");
		return identifierToken;
	}


	/**
	 * verify the expected operators are found
	 * @param arrayDescriptor the list of token that express the descriptor
	 * @param identifierPosition the location in the token list the identifier was found
	 * @param loLt TRUE =&gt; the lo-bound operator is &lt;
	 * @param hiLt TRUE =&gt; the hi-bound operator is &lt;
	 * @return TRUE => check is verified
	 */
	private boolean verifyOperators
		(
			List<TokenParser.TokenDescriptor> arrayDescriptor,
			int identifierPosition, boolean loLt, boolean hiLt
		)
	{
		return
		arrayDescriptor.get (identifierPosition-1).isIdentifiedAs (LTorLE (loLt)) &&
		arrayDescriptor.get (identifierPosition+1).isIdentifiedAs (LTorLE (hiLt));
	}


	/**
	 * return appropriate operator
	 * @param isLt TRUE =&gt; operator is &lt;, otherwise &lt;=
	 * @return appropriate operator
	 */
	private String LTorLE (boolean isLt)
	{
		return isLt? LT_OPERATOR: LE_OPERATOR;
	}


	/**
	 * locate item within a specified token set
	 * @param tokens the list of tokens to search for item
	 * @param item the text of the item sought
	 * @return position found
	 */
	public int locate
	(List<TokenParser.TokenDescriptor> tokens, String item)
	{
		int position = 0;
		while (position < tokens.size())
		{
			if (tokens.get (position).isIdentifiedAs (item)) return position;
			position++;
		}
		return -1;
	}


}


