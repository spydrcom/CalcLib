
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.symbols.AbstractVectorReduction;
import net.myorb.math.expressions.OperatorNomenclature;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

import java.util.List;

/**
 * parse lo and hi values for a range
 * @author Michael Druckman
 */
public class RangeDescription implements AbstractVectorReduction.Range
{

	/**
	 * convert segment of expression to MathML
	 * @param starting starting position within equation
	 * @param ending ending position within equation
	 * @param segment for error messages
	 * @return MathML for segment
	 */
	public String render (int starting, int ending, String segment)
	{
		try { return parser.renderSegment (tokens.subList (starting, ending)); }
		catch (Exception e) { throw new RuntimeException ("Error in " + segment); }
	}

	/**
	 * locate a token in a sequence
	 * @param item the character(s) used to recognize token
	 * @param starting the starting index within the sequence
	 * @return the position token is found, -1 if not found
	 */
	public int find (String item, int starting)
	{
		int pos = starting;
		while (pos < rangeEnd)
		{
			if (tokens.get (pos).getTokenImage ().startsWith (item)) return pos;
			pos++;
		}
		return -1;
	}

	/**
	 * [loBound LE identifier LE hiBound DELTA step]
	 * the LT character is used to identify the end of the lo bound.
	 * the DELTAA token is optional and if found identifies the end of hi bound
	 */
	public void parseRange ()
	{
		int pos = find (OperatorNomenclature.LT_OPERATOR, 0);
		if (pos < 0) throw new RuntimeException ("Range not recognized");

		int pos2 = find (OperatorNomenclature.LT_OPERATOR, pos+1);
		if (pos2 < 0 || (pos2 != pos + 2)) throw new RuntimeException ("Range not properly formed");

		if (tokens.get (this.idPosition = pos + 1).getTokenType () != TokenParser.TokenType.IDN)
		{
			throw new RuntimeException ("Identifier not recognized");
		}

		if ((pos = find (OperatorNomenclature.DELTA_INCREMENT_OPERATOR, 0)) > 0)
		{ this.rangeEnd = pos; this.deltaPosition = pos; }
		else this.deltaPosition = -1;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractVectorReduction.Range#getIncrement()
	 */
	public String getIncrement ()
	{
		if (deltaPosition > 0)
		{
			return render (deltaPosition+1, tokens.size (), "increment");
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractVectorReduction.Range#getLoBound()
	 */
	public String getLoBound () { return render (0, idPosition-1, "lo bound"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractVectorReduction.Range#getHiBound()
	 */
	public String getHiBound () { return render (idPosition+2, rangeEnd, "hi bound"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractVectorReduction.Range#getIdentifier()
	 */
	public String getIdentifier () { return tokens.get (idPosition).getTokenImage (); }

	/**
	 * find portions of range
	 * @param tokens the tokens describing the full range
	 */
	public void evaluateSequence (List<TokenParser.TokenDescriptor> tokens)
	{
		this.tokens = tokens;
		this.rangeEnd = tokens.size ();
		parseRange ();
	}
	private List<TokenParser.TokenDescriptor> tokens;
	private int idPosition, rangeEnd, deltaPosition;

	public RangeDescription (List<TokenParser.TokenDescriptor> tokens, SymbolMap s)
	{ parser = new MathML (s); evaluateSequence (tokens); }
	private MathML parser;

}

