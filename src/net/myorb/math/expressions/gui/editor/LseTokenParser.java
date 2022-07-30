
package net.myorb.math.expressions.gui.editor;

import net.myorb.data.abstractions.ExpressionTokenParser;

import java.util.ArrayList;
import java.util.List;

public class LseTokenParser extends ExpressionTokenParser
{

	public LseTokenParser ()
	{
		this.tokens = new ArrayList<TokenDescriptor>();
		this.tracking = new ArrayList<TokenTrack>();
	}

	public static ExpressionTokenParser.TokenSequence parse (StringBuffer buffer)
	{
		return new ExpressionTokenParser.TokenSequence (parseCommon (buffer, new LseTokenParser ()));
	}

	public int parseNext (StringBuffer buffer, int position, List<TokenDescriptor> tokens, List<TokenTrack> tracking)
	{
		return parseNext (buffer, this, position, tokens, tracking);
	}

	public void parseLine (StringBuffer buffer)
	{
		tokens.clear (); tracking.clear ();
		for (int pos = 0; pos < buffer.length (); )
		{
			pos = parseNext (buffer, pos, tokens, tracking);
		}
	}
	List<TokenDescriptor> tokens; List<TokenTrack> tracking;

	public static class Scan
	{
		Scan (TokenDescriptor tokens, TokenTrack tracking)
		{
			this.tracking = tracking;
			this.tokens = tokens;
		}
		TokenDescriptor tokens;
		TokenTrack tracking;
		
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
