
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.editor.model.SnipToolToken;
import net.myorb.gui.editor.model.SnipToolContext;

import net.myorb.gui.editor.SnipToolScanner;

import java.awt.Color;
import java.awt.Font;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * extended token parser tailored to CalcLib script syntax
 *  and made to support style assignment for Language Sensitive Editing features
 * @author Michael Druckman
 */
public class CalcLibSnipScanner implements SnipToolScanner
{


	/*
		IDN, // an identifier
		OPR, // an operator, possible multi-character
		QOT, // a quoted body of text, a string literal
		NUM, // any numeric value (with no sub-class)
		INT, // an integer value (no decimal point)
		RDX, // an integer value with specified radix
		DEC, // a decimal value (decimal point) 
		FLT  // a floating point value
	 */

	/**
	 * provide a direct map between LseTokenParser.TokenType and style codes
	 */
	protected Map<LseTokenParser.TokenType,Integer> styleMap;


	public CalcLibSnipScanner (SnipProperties properties)
	{
		this.parser = new LseTokenParser ();
		this.context = properties.newContext ();
		this.properties = properties;
		prepareStyles ();
	}
	protected SnipProperties properties;
	protected SnipToolContext context;
	protected LseTokenParser parser;


	/**
	 * allocate style codes for styles to be used.
	 *  construct style map to provide style per token type
	 */
	void prepareStyles ()
	{
		this.styleMap = new HashMap<LseTokenParser.TokenType,Integer>();

		Font f = new Font(fontName, 0, fontSize);
		int IDstyle = context.postAnonymousStyle (f, Color.BLUE);

		f = new Font(fontName, 0, fontSize);
		defaultStyle = context.postAnonymousStyle (f, Color.BLACK);

		f = new Font(fontName, Font.ITALIC, fontSize);
		int QOTstyle = context.postAnonymousStyle (f, Color.RED);

		f = new Font(fontName, Font.ITALIC, fontSize);
		commentStyle = context.postAnonymousStyle (f, Color.ORANGE);

		f = new Font(fontName, Font.BOLD, fontSize);
		int OPstyle = context.postAnonymousStyle (f, Color.GREEN);

		for (LseTokenParser.TokenType t : LseTokenParser.TokenType.values ())
		{ styleMap.put (t, defaultStyle); }

		styleMap.put (LseTokenParser.TokenType.IDN, IDstyle);
		styleMap.put (LseTokenParser.TokenType.QOT, QOTstyle);
		styleMap.put (LseTokenParser.TokenType.OPR, OPstyle);
	}
	protected String fontName = "Courier"; protected int fontSize = 12;
	protected int commentStyle;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#getDefaultStyleCode()
	 */
	public int getDefaultStyleCode () { return defaultStyle; }
	protected int defaultStyle;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#updateSource(java.lang.StringBuffer, int)
	 */
	public void updateSource (StringBuffer source, int position)
	{
		this.scans = parser.ScanLine (source); this.current = 0;
		this.start = position; this.tokenEnd = position;
		this.buffer = source;
	}
	protected List<LseTokenParser.Scan> scans;
	protected int start, current, tokenEnd;
	protected StringBuffer buffer;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#getLastSourcePosition()
	 */
	public int getLastSourcePosition () { return tokenEnd; }


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#getToken()
	 */
	public SnipToolToken getToken ()
	{
		SnipToolToken token;
		if (current == scans.size ()) return null;

		LseTokenParser.Scan
			scan = scans.get (current++);
		String image = scan.tokens.getTokenImage ();
		int location = scan.tracking.getLocation ();

		if (image.startsWith (COMMENT_TOKEN))
		{
			token = new SnipToolToken (buffer.substring (location), commentStyle);
			tokenEnd = this.start + this.buffer.length ();
		}
		else
		{
			token = new SnipToolToken
				(image, styleMap.get (scan.tokens.getTokenType ()));
			tokenEnd = this.start + location + image.length ();
		}

		return token;
	}
	public static final String COMMENT_TOKEN = "//";


}

