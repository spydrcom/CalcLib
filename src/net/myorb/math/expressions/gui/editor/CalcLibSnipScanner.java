
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.editor.model.SnipToolToken;
import net.myorb.gui.editor.model.SnipToolContext;

import net.myorb.gui.editor.SnipToolScanner;

import java.awt.Color;
import java.awt.Font;

import java.util.List;
import java.util.Collection;
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
		this.commands = properties.getCommands ();
		this.symbols = properties.getSymbols ();
		this.properties = properties;
		this.prepareStyles ();
	}
	protected Collection<String> commands, symbols;
	protected SnipProperties properties;
	protected SnipToolContext context;
	protected LseTokenParser parser;


	/**
	 * allocate style codes for styles to be used.
	 *  construct style map to provide style per token type
	 */
	void prepareStyles ()
	{
		IDstyleOK = post (0, Color.BLUE);
		IDstyleBAD = post (0, Color.RED);

		commandStyle = post (Font.BOLD, Color.decode ("0x80"));
		commentStyle = post (Font.ITALIC, Color.decode ("0x6400"));
		defaultStyle = post (0, Color.BLACK);

		QOTstyle = post (Font.ITALIC, Color.decode ("0x800080"));
		OPstyle = post (Font.BOLD, Color.PINK);

		assignStyleMapEntries ();
	}
	protected String fontName = "Courier"; protected int fontSize = 12;
	protected int commentStyle, commandStyle, IDstyleOK, IDstyleBAD, OPstyle, QOTstyle;


	/**
	 * post a style to context
	 * @param style the style of font
	 * @param color the foreground color
	 * @return the style code
	 */
	int post (int style, Color color)
	{
		Font f = new Font(fontName, style, fontSize);
		return context.postAnonymousStyle (f, color);
	}


	/**
	 * populate map with style codes
	 */
	void assignStyleMapEntries ()
	{
		this.styleMap = new HashMap<LseTokenParser.TokenType,Integer>();

		for (LseTokenParser.TokenType t : LseTokenParser.TokenType.values ())
		{
			styleMap.put (t, defaultStyle);
		}

		styleMap.put (LseTokenParser.TokenType.QOT, QOTstyle);
		styleMap.put (LseTokenParser.TokenType.OPR, OPstyle);
	}


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
		this.start = position; this.setLastSourcePosition (0);
		this.buffer = source;
	}
	protected List<LseTokenParser.Scan> scans;
	protected StringBuffer buffer;
	protected int start, current;


	/**
	 * identify updated position within source buffer
	 * @param offset the offset from the model start of the buffer
	 */
	public void setLastSourcePosition (int offset)
	{ this.lastSourcePosition = this.start + offset; }
	protected int lastSourcePosition;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#getLastSourcePosition()
	 */
	public int getLastSourcePosition () { return lastSourcePosition; }


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#getToken()
	 */
	public SnipToolToken getToken ()
	{
		if (current == scans.size ()) return null;

		LseTokenParser.Scan
			scan = scans.get (current++);
		String image = scan.tokens.getTokenImage ();
		int location = scan.tracking.getLocation ();
		int styleCode;

		if (image.startsWith (COMMENT_TOKEN) || image.toUpperCase ().startsWith (ENTITLED_TOKEN))
		{
			setLastSourcePosition (this.buffer.length ());
			String remainder = buffer.substring (location);
			return new SnipToolToken (remainder, commentStyle);
		}
		else if (commands.contains (image))
		{
			styleCode = commandStyle;
		}
		else
		{
			LseTokenParser.TokenType t = scan.tokens.getTokenType ();

			if (t == LseTokenParser.TokenType.IDN)
			{
				if (symbols.contains (image))
				{ styleCode = this.IDstyleOK; }
				else styleCode = this.IDstyleBAD;
			}
			else
			{
				styleCode = styleMap.get (t);
			}
		}

		setLastSourcePosition (location + image.length ());
		return new SnipToolToken (image, styleCode);
	}
	public static final String ENTITLED_TOKEN = "ENTITLED";
	public static final String COMMENT_TOKEN = "//";


}

