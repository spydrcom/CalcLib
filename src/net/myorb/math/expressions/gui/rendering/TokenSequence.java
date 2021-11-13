
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.expressions.TokenParser;

import java.util.List;

/**
 * container for tokens in sequence and
 *  pointers to sequence position and sequence end
 * @author Michael Druckman
 */
public class TokenSequence
{


	/**
	 * sequence is built from list of tokens
	 * @param tokens the token list for the new sequence
	 */
	public TokenSequence (List<TokenParser.TokenDescriptor> tokens)
	{
		this.tokens = tokens;
		this.tokenCount = tokens.size ();
		this.tokenIndex = 0;
	}
	private List<TokenParser.TokenDescriptor> tokens;
	private TokenParser.TokenDescriptor currentToken;
	private int tokenIndex = 0, tokenCount;


	/*
	 * properties of current token
	 */
	public String currentTokenImage () { return tokenImage; }
	public TokenParser.TokenType currentTokenType () { return tokenType; }

	private TokenParser.TokenType tokenType;
	private String tokenImage;


	/**
	 * read current token position.
	 *  increment index, get type and image
	 * @return image of new current token
	 */
	public String next ()
	{
		currentToken = tokens.get (tokenIndex++);
		tokenImage = currentToken.getTokenImage (); tokenType = currentToken.getTokenType ();
		if (MathML.DUMPING) System.out.println ("Token:  symbol=" + tokenImage + "   type=" + tokenType);
		return tokenImage;
	}


	/**
	 * mark current token position for range selection
	 */
	public void mark ()
	{
		markedAt = tokenIndex;
	}
	private int markedAt = 0;


	/**
	 * get sub-list of tokens starting at marked position
	 * @return tokens from marked position to current location
	 */
	public List<TokenParser.TokenDescriptor> getMarked ()
	{
		return tokens.subList (markedAt, tokenIndex - 1);
	}


	/**
	 * has current position gone beyond of sequence
	 * @return TRUE = position is beyond sequence end
	 */
	public boolean atEnd ()
	{
		return tokenIndex >= tokenCount;
	}


	/**
	 * read next token without change of position
	 * @return token at next position
	 */
	public String peekAtNext ()
	{
		if (atEnd ()) return null;
		return tokens.get (tokenIndex).getTokenImage ();
	}


}

