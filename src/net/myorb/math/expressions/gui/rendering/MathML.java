
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

import java.util.List;

/**
 * conversion class for tokens of expression to MathML document representation
 * @author Michael Druckman
 */
public class MathML extends TokenProcessing
{


	/**
	 * context for conversion if a symbol map
	 * @param s the symbol map object that will be used for context
	 */
	public MathML (SymbolMap s)
	{
		setSymbolMap (s); setNodeFormater (new MathMarkupNodes ());
	}


	/*
	 * rendering
	 */


	/**
	 * process sequence of tokens that comprise an expression
	 * @param tokens the sequence of tokens that comprise the expression
	 * @return the MathML text that represents the token sequence
	 * @throws Exception for any errors
	 */
	public String renderSegment (List<TokenParser.TokenDescriptor> tokens) throws Exception
	{
		String tokenImage; TokenParser.TokenType tokenType;
		TokenSequence seq = new TokenSequence (tokens);
		initializeContexts ();

		while (!seq.atEnd ())
		{
			tokenImage = seq.next (); lookAhead (seq);
			if (arrayFoundAndRangeProcessed (seq)) continue;
			renderSegmentPortion (tokenImage, tokenType = checkContext (seq), tokens);
			lastTokenWas (tokenType, tokenImage);
			dump ();
		}

		while (getLastPrec () > 0 && stillAbleToPop ()) {}
		return bracket (getLastLeaf ());
	}
	void renderSegmentPortion
		(
			String tokenImage, TokenParser.TokenType tokenType,
			List<TokenParser.TokenDescriptor> tokens
		)
	{
		try
		{
			if (tokenType == TokenParser.TokenType.OPR) processOperatorOrDelimiter (tokenImage);								// <mo> operation
			else if (tokenType == TokenParser.TokenType.IDN) pushLeaf (idFor (tokenImage), ATOMIC_LEAF_PRECEDENCE);				// <mi> identifier
			else pushLeaf (nodeFormater.formatNumericReference (tokenImage), ATOMIC_LEAF_PRECEDENCE);							// <mn> number
		} catch (Exception e) { segmentError (tokenImage, tokens, e); }
	}


	/**
	 * process sequence of tokens into "math" document
	 * @param tokens the sequence of tokens that comprise the expression
	 * @return the MathML text that represents the token sequence
	 * @throws Exception for any errors
	 */
	public String render (List<TokenParser.TokenDescriptor> tokens) throws Exception
	{
		String document;
		dump (document = nodeFormater.formatDocumentRoot (renderSegment (tokens)));
		return document;
	}


	/**
	 * show generated MathML document
	 * @param document the text of the document to be shown
	 */
	public void dump (String document)
	{
		if (DUMPING) 
		{
			System.out.println ();
			System.out.println ("===");
			System.out.println ("Generated Markup:  " + document);
			System.out.println ("===");
			System.out.println ();
		}
	}
	static final boolean DUMPING = false;


}

