
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.TokenParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * provide for transforms based on Differentiation Rules of calculus
 * @author Michael Druckman
 */
public class DifferentiationRules
{

	public enum Rules
	{
		SUM_RULE,				// F + G			F'(X) + G'(X)
		CHAIN_RULE,				// F (G (X))		F'(G(X)) * G'(X)
		INVERSE_RULE,			// F (G(X)) = X		F'(x) = 1 / G'(x)
		RECIPROCAL_RULE,		// 1 / F(X)			-F'(X) / F(X)^2
		PRODUCT_RULE,			// F * G			F'(X)*G(X) + F(X)*G'(X)
		QUOTIENT_RULE,			// F / G			[ F'(X)*G(X) - F(X)*G'(X) ] / G(X)^2
		POWER_RULE				// F ^ G			F^G * [F' * G/F + G' * LN F]
	}

	/**
	 * recognize identifiers and derivative indicators
	 * @param transformTokens the stream of tokens from the source
	 * @return the list if identifiers found in the stream
	 */
	List<String> parse (TokenParser.TokenSequence transformTokens)
	{
		transformTokens.remove (0); String token, nextNeg = "", mul = "";
		List<String> ids = new ArrayList<String>();

		for (TokenParser.TokenDescriptor t : transformTokens)
		{
			token = t.getTokenImage ();
			if (t.getTokenType() == TokenParser.TokenType.IDN)
			{
				ids.add (nextNeg + mul + token);
				nextNeg = ""; mul = "";
			}
			else if (t.getTokenType() == TokenParser.TokenType.INT || t.getTokenType() == TokenParser.TokenType.DEC)
			{
				if (!token.equals ("1")) mul = " " + token + " * ";
			}
			else if (token.startsWith ("'"))
			{
				ids.add (ids.remove (ids.size()-1) + token);
			}
			else if (token.startsWith ("-"))
			{
				nextNeg = "-";
			}
		}

		return ids;
	}

	/**
	 * apply the rules to source token stream
	 * @param parameterName the name of the function parameter
	 * @param transformTokens the tokens from the source stream
	 * @param definitionTokens the tokens of the transformed function
	 */
	void transform
		(
			String parameterName,
			TokenParser.TokenSequence transformTokens,
			TokenParser.TokenSequence definitionTokens
		)
	{
		String ruleName =
			transformTokens.get(0).getTokenImage ();
		List<String> ids = parse (transformTokens);

		String function1 = ids.get (0),
			function2 = ids.size () < 2? null: ids.get (1);
		StringBuffer buffer = new StringBuffer ();
		Rules rule = RULE_MAP.get (ruleName);
		
		switch (rule)
		{
			case PRODUCT_RULE:			// F'(X)*G(X) + F(X)*G'(X)

				buffer
					.append (function1).append (" (").append (parameterName).append (") * ")
					.append (function2).append (" (").append (parameterName).append (")");
				buffer.append (" ; ");

				buffer
					.append (function1).append ("'(").append (parameterName).append (")*")
					.append (function2).append (" (").append (parameterName).append (") + ")
					.append (function1).append (" (").append (parameterName).append (")*")
					.append (function2).append ("'(").append (parameterName).append (")");
				break;

			case RECIPROCAL_RULE:		// -F'(X) / F(X)^2

				buffer
					.append ("1 / ").append (function1).append (" (").append (parameterName).append (")");
				buffer.append (" ; ");

				buffer.append ("-").append (function1).append ("'(").append (parameterName).append (") / ")
						.append (function1).append ("(").append (parameterName).append (")^2");
				break;

			case QUOTIENT_RULE:			// [ F'(X)*G(X) - F(X)*G'(X) ] / G(X)^2

				buffer
					.append (function1).append (" (").append (parameterName).append (") / ")
					.append (function2).append (" (").append (parameterName).append (")");
				buffer.append (" ; ");

				buffer.append ("( ")
					.append (function1).append ("'(").append (parameterName).append (")*")
					.append (function2).append (" (").append (parameterName).append (") - ")
					.append (function1).append (" (").append (parameterName).append (")*")
					.append (function2).append ("'(").append (parameterName).append (") ) / ")
					.append (function2).append (" (").append (parameterName).append (")^2");
				break;

			case CHAIN_RULE:			// F'(G(X)) * G'(X)

				buffer
					.append (function1)
					.append ("(").append (function2).append (" (").append (parameterName).append (") )");
				buffer.append (" ; ");

				buffer
					.append (function1)
						.append ("'(").append (function2).append (" (").append (parameterName).append (") ) * ")
					.append (function2).append ("'(").append (parameterName).append (")");
				break;

			case INVERSE_RULE:			// 1/ dy/dx

				buffer
					.append (function1).append ("(").append (parameterName).append (")");
				buffer.append (";");

				buffer
					.append ("1 / ").append (function2).append ("'(").append (parameterName).append (")");
				break;

			case POWER_RULE:			// F^G * [F' * G/F + G' * LN F]

				buffer
					.append (function1).append (" (").append (parameterName).append (") ^ ")
					.append (function2).append (" (").append (parameterName).append (")");
				buffer.append (" ; ");

				buffer
					.append (function1).append (" (").append (parameterName).append (") ^ ")		// f(x)^
					.append (function2).append (" (").append (parameterName).append (") * (")		// g(x) * (
					
						.append (function1).append ("'(").append (parameterName).append (") * ")	// F'(x) *
						.append (function2).append (" (").append (parameterName).append (") / ")	// G(x) /
						.append (function1).append (" (").append (parameterName).append (")")		// F(x)

						.append (" + ")

						.append (function2).append ("'(").append (parameterName).append (") / ")	// G'(x) * 
						.append ("ln(")																//	ln(
							.append (function1).append (" (").append (parameterName).append (")")	//		F(x)
						.append (")")																//	)

					.append (")");																	// )
				break;

			case SUM_RULE:				// F'(X) + G'(X)

				String id, idx, op = " + ";
				buffer.append (function1).append ("(").append (parameterName).append (")");

				for (int i=1; i<ids.size(); i++)
				{
					if ((id = idx = ids.get (i)).startsWith ("-")) { op = " - "; idx = id.substring (1); }
					buffer.append (op).append (idx).append ("(").append (parameterName).append (")");
					op = " + ";
				}
				buffer.append (" ; ");

				buffer.append (function1).append ("'(").append (parameterName).append (")");

				for (int i=1; i<ids.size(); i++)
				{
					if ((id = idx = ids.get (i)).startsWith ("-")) { op = " - "; idx = id.substring (1); }
					buffer.append (op).append (idx).append ("'(").append (parameterName).append (")");
					op = " + ";
				}
				break;

		}

		definitionTokens.addAll (TokenParser.parse (buffer));
	}

	static final Map<String,Rules> RULE_MAP = new HashMap<String,Rules>();
	
	static
	{
		RULE_MAP.put ("CHAIN", Rules.CHAIN_RULE);
		RULE_MAP.put ("INVERSE", Rules.INVERSE_RULE);
		RULE_MAP.put ("RECIPROCAL", Rules.RECIPROCAL_RULE);
		RULE_MAP.put ("PRODUCT", Rules.PRODUCT_RULE);
		RULE_MAP.put ("QUOTIENT", Rules.QUOTIENT_RULE);
		RULE_MAP.put ("POWER", Rules.POWER_RULE);
		RULE_MAP.put ("SUM", Rules.SUM_RULE);
	}

	public static String[] listOfRules ()
	{
		String[] list = RULE_MAP.keySet ().toArray (new String[]{});
		java.util.Arrays.sort (list);
		return list;
	}

	public static Rules getRule (String name)
	{
		return RULE_MAP.get (name);
	}

}

