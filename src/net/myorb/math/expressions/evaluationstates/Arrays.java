
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.*;

import net.myorb.data.abstractions.CommonDataStructures;

import java.util.ArrayList;
import java.util.List;

/**
 * process array aggregate expressions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Arrays<T> extends OperatorNomenclature
{


	/**
	 * description of domain constraints
	 * @param <T> type on which operations are to be executed
	 */
	public interface ConstrainedDomain<T> extends ValueManager.Metadata
	{
		/**
		 * the lo domain value of an array function
		 * @return the lo value
		 */
		T getLo ();

		/**
		 * the hi domain value of an array function
		 * @return the hi value
		 */
		T getHi ();

		/**
		 * the domain increment of an array function
		 * @return the increment value
		 */
		T getDelta ();
		
		/**
		 * throw exception if parameter not within constraints
		 * @param x value to check aginst constraints
		 */
		void checkConstraintsAgainst (T x);
	}


	/**
	 * meta-data capture describing an array
	 * @param <T> type on which operations are to be executed
	 */
	public interface Descriptor<T> extends ConstrainedDomain<T>
	{
		/**
		 * generate a macro that will evaluate the token list
		 * @param environment the central workspace object for this engine
		 * @param silent TRUE = suppress generated error messages
		 * @return a subroutine configured with the token list
		 */
		ExpressionMacro<T> genMacro (Environment<T> environment, boolean silent);

		/**
		 * get the constraints imposed by this array
		 * @return a descriptor for the constraints
		 */
		ConstrainedDomain<T> getDomainConstraints ();

		/**
		 * build a descriptor for a reduced constraint
		 * @param lo the new lo value for the constraint
		 * @param hi the new hi value for the constraint
		 * @param op the operation constraining the domain
		 * @return the new descriptor
		 */
		ArrayFunctionDescriptor<T> describeReducedInterval (T lo, T hi, String op);

		/**
		 * build a descriptor for a similar constraint
		 * @param op the operation constraining the domain
		 * @return the new descriptor
		 */
		ArrayFunctionDescriptor<T> describeSimilarInterval (String op);

		/**
		 * get the list of tokens that describe the element values
		 * @return a list of expression tokens
		 */
		TokenParser.TokenSequence getExpression ();

		/**
		 * get a text display of the expression
		 * @return text string describing expression
		 */
		String getExpressionText ();

		/**
		 * format text titles columns of displays related to this array
		 * @return the formatted text titles
		 */
		CommonDataStructures.TextItems columnTitles ();

		/**
		 * format a text title for displays related to this array
		 * @return the formatted text title
		 */
		String formatTitle ();

		/**
		 * provide a domain value list
		 * @param count number of elements expected
		 * @return the ordered list of domain values
		 */
		List<T> enumerateDomain (int count);

		/**
		 * identify the variable that defines this array
		 * @return the variable name (from the token list)
		 */
		String getVariable ();
	}


	/**
	 * get copies of control objects
	 * @param environment the environment object
	 * @param tokens the list of tokens comprising the stream
	 */
	public void establish
		(
			Environment<T> environment,
			TokenParser.TokenSequence tokens
		)
	{
		this.spaceManager = environment.getSpaceManager ();
		this.valueManager = environment.getValueManager ();
		this.environment = environment;
		this.tokens = tokens;
	}
	protected Environment<T> environment;
	protected ExpressionSpaceManager<T> spaceManager;
	protected TokenParser.TokenSequence tokens;
	protected ValueManager<T> valueManager;


	/**
	 * process a token stream to generate an array
	 * @param tokens the token stream being processed
	 * @param tokenPosition the starting position in the stream
	 * @param environment the evaluation environment driving the processing
	 * @return the token stream position past end of array generator
	 */
	public int process
		(
			TokenParser.TokenSequence tokens,
			int tokenPosition, Environment<T> environment
		)
	{
		establish (environment, tokens);
		int starting = tokenPosition + 1, ending;
		T loBound = computeScalar (tokens.between (starting, ending = expect (starting, LE_OPERATOR)));
		TokenParser.TokenDescriptor identifierToken = tokens.get (starting = ending + 1);

		String parameter = identifierToken.getTokenImage ();
		checkSyntax (identifierToken.getTokenType() == TokenParser.TokenType.IDN, "identifier expected, not found");
		checkSyntax (tokenFoundAt (++starting, LE_OPERATOR), "operator <= expected, not found");

		ending = parseHighBound (++starting);
		T hiBound = computeScalar (tokens.between (starting, ending)), delta = spaceManager.getOne ();
		if (tokenFoundAt (ending, DELTA_INCREMENT_OPERATOR))
		{
			ending = expect (starting = ending + 1, END_OF_ARRAY_DELIMITER);
			delta = computeScalar (tokens.between (starting, ending));
		}

		ending = parseExpression (tokens, starting = ending + 1);
		TokenParser.TokenSequence expression = tokens.between (starting, ending + 1);
		ArrayDescriptor<T> descriptor = new ArrayDescriptor<T> (loBound, hiBound, delta, parameter, expression, null, spaceManager);
		environment.getValueStack ().push (generateComputedArray (descriptor), descriptor);
		return ending + 1;
	}


	/**
	 * look for delta or array end options
	 * @param starting the starting position for search
	 * @return the ending position for high bound
	 */
	public int parseHighBound (int starting)
	{
		int deltaEnding = locate (DELTA_INCREMENT_OPERATOR, starting);
		int arrayEnding = expect (starting, END_OF_ARRAY_DELIMITER);
		if (deltaEnding > 0 && deltaEnding < arrayEnding) return deltaEnding;
		return arrayEnding;
	}


	/**
	 * isolate the expression tokens
	 * @param tokens the full token stream
	 * @param starting the starting position of the expression
	 * @return the ending position of the expression
	 */
	public int parseExpression
	(List<TokenParser.TokenDescriptor> tokens, int starting)
	{
		int level = 0;
		int position = starting;
		while (position < tokens.size ())
		{
			String token = tokens.get (position).getTokenImage ();
			if (token.equals (START_OF_GROUP_DELIMITER)) level++;
			if (token.equals (END_OF_GROUP_DELIMITER))
			{
				 if (--level == 0) return position;
			}
			position++;
		}
		return 0;
	}


	/**
	 * throw exception if condition is not verified
	 * @param syntaxVerified TRUE = condition has been verified
	 * @param message text of error message for condition not met
	 */
	public void checkSyntax (boolean syntaxVerified, String message)
	{
		if (!syntaxVerified) { throw new RuntimeException ("syntax error, " + message); }
	}


	/**
	 * generate an array of the iterated values
	 * @param loBound the low bound of the range that was described
	 * @param hiBound the high bound of the range that was described
	 * @return the generated array
	 */
	public List<T> generateArray (T loBound, T hiBound)
	{
		List<T> array = new ArrayList<T> ();
		T current = loBound, ONE = spaceManager.getOne ();
		while (!spaceManager.lessThan (hiBound, current))
		{
			array.add (current);
			current = spaceManager.add (current, ONE);
		}
		return array;
	}


	/**
	 * expect to find a specified token
	 * @param position the starting position of the search
	 * @param item the token to be located
	 * @return position of the token
	 */
	public int expect (int position, String item)
	{
		int foundAt = locate (item, position);
		checkSyntax (foundAt>0, "expected " + item + ", not found");
		return foundAt;
	}


	/**
	 * find a specified token
	 * @param item the token to be located
	 * @param starting the starting position of the search
	 * @return position of the token, -1 = not found
	 */
	public int locate (String item, int starting)
	{
		int position = starting;
		while (position < tokens.size())
		{
			if (tokenFoundAt (position, item)) return position;
			position++;
		}
		return -1;
	}


	/**
	 * check token stream at position
	 * @param position the position within the stream
	 * @param token the token image in question
	 * @return TRUE = token matches
	 */
	public boolean tokenFoundAt (int position, String token)
	{
		return tokens.get (position).isIdentifiedAs (token);
	}


	/**
	 * compute the bounds of the array
	 * @param tokens the tokens that define the boundary computation
	 * @return the computed value
	 */
	public T computeScalar (TokenParser.TokenSequence tokens)
	{
		return valueManager.toDiscrete (invoke (null, tokens, null).topOfStack ());
	}


	/**
	 * iterate over the elements computing the array value
	 * @param descriptor the descriptor of the array
	 * @return the list of computed elements
	 */
	public ValueManager.RawValueList<T> generateComputedArray (ArrayDescriptor<T> descriptor)
	{
		if (environment.isDumpingSet ())
		{
			System.out.println ("loBound: "+descriptor.getLo());
			System.out.println ("parameter: "+descriptor.getVariable());
			System.out.println ("hiBound: "+descriptor.getHi());
			System.out.println ("delta: "+descriptor.getDelta());
		}

		ValueManager.RawValueList<T>
			array = new ValueManager.RawValueList<T> ();
		ExpressionMacro<T> macro = descriptor.genMacro (environment, false);
		T current = descriptor.getLo (), hi = descriptor.getHi (), delta = descriptor.getDelta ();
		while (!spaceManager.lessThan (hi, current))
		{
			T computedValue =
				valueManager.toDiscrete (macro.evaluate (current));
			current = spaceManager.add (current, delta);
			array.add (computedValue);
		}

		return array;
	}


	/**
	 * fork an evaluation engine
	 *  to process sections of the token stream
	 * @param parameterNames the names of parameters to the subroutine
	 * @param tokens the tokens that define the behavior of the routine
	 * @param parameterValue the value to be assigned to the parameter
	 * @return the subroutine object that can be used to get result
	 */
	public Subroutine<T> invoke
		(
			List<String> parameterNames,
			TokenParser.TokenSequence tokens,
			ValueManager.GenericValue parameterValue
		)
	{
		Subroutine<T> subroutine =
			new Subroutine<T>(parameterNames, tokens);
		environment.processSubroutine (subroutine);

		if (parameterValue != null)
			subroutine.setParameterValue (parameterValue);
		subroutine.run (); return subroutine;
	}


}


