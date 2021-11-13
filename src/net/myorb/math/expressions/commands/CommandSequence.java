
package net.myorb.math.expressions.commands;

import net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor;
import net.myorb.math.expressions.TokenParser;

import java.util.List;

/**
 * treat sequence of tokens as command constructed from keywords and positional parameters
 * @author Michael Druckman
 */
public class CommandSequence extends TokenParser.TokenSequence
{

	public CommandSequence () {}
	
	public CommandSequence (List<TokenDescriptor> tokens)
	{ this.addAll (tokens); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ExpressionTokenParser.TokenSequence#between(int, int)
	 */
	public CommandSequence between (int lo, int hi)
	{ return new CommandSequence (subList (lo, hi)); }

	/**
	 * @param buffer the source text
	 * @return the parsed token list
	 */
	public static CommandSequence parse (StringBuffer buffer)
	{ return new CommandSequence (TokenParser.parse (buffer)); }

	private static final long serialVersionUID = -8565541784928304152L;
}