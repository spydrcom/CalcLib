
package net.myorb.math.expressions;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.myorb.data.abstractions.CommonCommandParser;

public class OriginalTokenParser extends CommonCommandParser
	implements CommonCommandParser.SpecialTokenSegments
{

	/*
	 * character collections grouped into classification sets
	 */
	
	public static final String
	OPERATOR = "+-*/^~!@#%&$|<>,.:;[]{}()=\\?'",
	MULTI_CHARACTER_OPERATOR = "<>\\+-~=*^!$@&|#'.:;/%",
	OPERATOR_EXTENDED = MULTI_CHARACTER_OPERATOR;
	
	public static final String
	IDN_LEAD = LETTER, IDN_BODY = LETTER + DIGIT + UNDERSCORE;
	
	public String getIdnLead () { return IDN_LEAD; }
	public String getWhiteSpace () { return WHITE_SPACE; }
	public String getSequenceCaptureMarkers () { return null; }
	public String getMultiCharacterOperator () { return MULTI_CHARACTER_OPERATOR; }
	public String getExtendedOperator () { return OPERATOR_EXTENDED; }
	public Collection<String> getCommentIndicators () { return null; }
	public String getOperator () { return OPERATOR; }
	public String getIdnBody () { return IDN_BODY; }
	
	public static final String WHITE_SPACE = " \t\r\n_";

	/**
	 * @param buffer text buffer to be parsed
	 * @return list of tokens parsed from source
	 */
	public static TokenParser.TokenSequence parse (StringBuffer buffer)
	{
		return new TokenParser.TokenSequence (parseCommon (buffer, new TokenParser ()));
	}
	
	/**
	 * build an expression from a token stream
	 * @param tokens the list of tokens to revert to expression
	 * @param prettyPrinted attempt to improve display
	 * @return the text of the expression stream
	 */
	public static String toFormatted
	(List<TokenDescriptor> tokens, boolean prettyPrinted)
	{
		StringBuffer buffer = new StringBuffer ();
		for (TokenDescriptor token : tokens)
		{
			String t = token.getTokenImage ();
			if (prettyPrinted)
			{
				String greek = GreekSymbols.findNotationFor (t);
				if (greek != null) t = greek;
			}
			buffer.append (t).append (" ");
		}
		return buffer.toString ();
	}
	public static String toPrettyText
	(List<TokenDescriptor> tokens) { return toFormatted (tokens, true); }
	public static String toString (List<TokenDescriptor> tokens)
	{ return toFormatted (tokens, false); }
	
	/*
	 * recognizable tokens in expressions
	 */
	
	static final Set<TokenType>
	EXPRESSION_TOKENS = new HashSet<TokenType>();
	static
	{
		EXPRESSION_TOKENS.add (TokenType.IDN);
		EXPRESSION_TOKENS.add (TokenType.OPR);
	}
	public static boolean isRecognizable (TokenType t)
	{ return EXPRESSION_TOKENS.contains (t); }
	
	
	public static class TokenSequence extends CommonCommandParser.TokenList
	{
		public TokenSequence () {}
		public TokenSequence (List<TokenDescriptor> tokens) { this.addAll (tokens); }
		public TokenSequence between (int lo, int hi) { return new TokenSequence (subList (lo, hi)); }
		private static final long serialVersionUID = -4208940819480200476L;
	}

}
