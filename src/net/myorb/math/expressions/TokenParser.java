
package net.myorb.math.expressions;

import net.myorb.data.abstractions.ExpressionTokenParser;

import java.util.List;

/**
 * parse text expressions into token streams
 * @author Michael Druckman
 */
public class TokenParser extends ExpressionTokenParser
{

	/**
	 * provide for Greek character substitutions
	 */
	static ExpressionTokenParser.Notation notations =
			new ExpressionTokenParser.Notation ()
			{
				@Override
				public String lookFor(String text)
				{
					return GreekSymbols.findNotationFor (text);
				}
			};

	/**
	 * pretty print a token sequence
	 * @param tokens the sequence to be formatted
	 * @return the formatted sequence
	 */
	public static String toPrettyText
	(List<TokenDescriptor> tokens)
	{
		return toFormatted (tokens, notations);
	}

	/**
	 * optional pretty printing
	 * @param tokens the sequence to be formatted
	 * @param pretty TRUE = pretty print, FALSE = raw
	 * @return the formatted sequence
	 */
	public static String toFormatted
	(List<TokenDescriptor> tokens, boolean pretty)
	{
		return pretty? toPrettyText (tokens) : toString (tokens);
	}

}