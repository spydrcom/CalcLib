
package net.myorb.math.expressions.gui.editor;

import net.myorb.data.abstractions.ExpressionTokenParser;

import java.util.ArrayList;
import java.util.List;

/**
 * lexical parser for language sensitive editor features
 * @author Michael Druckman
 */
public class LseTokenParser extends ExpressionTokenParser
{

	public LseTokenParser ()
	{
		this.tokens = new ArrayList<TokenDescriptor>();
		this.tracking = new ArrayList<TokenTrack>();
	}

	/**
	 * use common parser
	 * @param buffer source text for parser
	 * @return a token sequence of parsed content
	 */
	public static ExpressionTokenParser.TokenSequence parse (StringBuffer buffer)
	{
		return new ExpressionTokenParser.TokenSequence (parseCommon (buffer, new LseTokenParser ()));
	}

	/**
	 * use LSE parser to collect tokens
	 * @param buffer source text for parser
	 * @param position current position within the line
	 * @param tokens the list of tokens that becomes parsed
	 * @param tracking the parallel list of tracking records
	 * @return the position at the end of the parse
	 */
	public int parseNext
		(
			StringBuffer buffer, int position,
			List<TokenDescriptor> tokens,
			List<TokenTrack> tracking
		)
	{
		return parseNext (buffer, this, position, tokens, tracking);
	}

	/**
	 * parse a source line into tokens
	 * @param buffer the source text buffer
	 */
	public void parseLine (StringBuffer buffer)
	{
		tokens.clear (); tracking.clear ();
		for (int pos = 0; pos < buffer.length (); )
		{
			pos = parseNext (buffer, pos, tokens, tracking);
		}
	}
	protected List<TokenDescriptor> tokens; protected List<TokenTrack> tracking;

	/**
	 * wrapper for token parser output
	 */
	public static class Scan
	{
		/**
		 * @param tokens the token descriptor
		 * @param tracking tracking data from the parser
		 */
		Scan (TokenDescriptor tokens, TokenTrack tracking)
		{
			this.tracking = tracking;
			this.tokens = tokens;
		}
		TokenDescriptor tokens;
		TokenTrack tracking;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			StringBuffer buf = new StringBuffer ()
					.append ("txt=").append (tokens.getTokenImage ())
					.append (" typ=").append (tokens.getTokenType ())
					.append (" start=").append (tracking.getLocation ())
					.append (" cat=").append (tracking.getType ());
			return buf.toString ();
		}
	}

	/**
	 * @param buffer source text for parser
	 * @return the list of scan records
	 */
	public List<Scan> ScanLine (StringBuffer buffer)
	{
		parseLine (buffer);
		
		List<Scan> scans = new ArrayList<Scan> ();
		for (int i=0; i<tokens.size(); i++)
		{
			scans.add (new Scan (tokens.get (i), tracking.get (i)));
		}

		return scans;
	}


	/**
	 * unit test
	 * @param args N/A
	 */
	public static void main (String[] args)
	{
		StringBuffer buf = new StringBuffer ()
				.append ("RENDER k * psi0(k*z) = k * ln k + SIGMA [0 <= n <= k-1] ( psi0 ( z + n/k ) )");
		List<Scan> scans = new LseTokenParser ().ScanLine (buf);
		for (Scan s : scans)
		{
			System.out.println (s);
		}
	}


}
