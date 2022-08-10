
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.editor.SnipToolScanner;

import net.myorb.gui.editor.model.SnipToolToken;
import net.myorb.gui.editor.model.SnipToolContext;

import javax.swing.text.StyleConstants;
import javax.swing.text.Style;

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
	 * the token types assigned by the parser
	 * 
		IDN, // an identifier
		OPR, // an operator, possible multi-character
		QOT, // a quoted body of text, a string literal
		NUM, // any numeric value (with no sub-class)
		INT, // an integer value (no decimal point)
		RDX, // an integer value with specified radix
		DEC, // a decimal value (decimal point) 
		FLT, // a floating point value
		CMT, // comments
		WS
	 */

	/**
	 * provide a direct map between LseTokenParser.TokenType and style codes
	 */
	protected Map<LseTokenParser.TokenType,Integer> styleMap;


	public CalcLibSnipScanner (SnipProperties properties)
	{
		this.styles = properties.newContext ();
		this.fontSize = properties.getFontSize ();
		this.fontName = properties.getFontFamily ();
		this.commands = properties.getCommands ();
		this.keywords = properties.getKeywords ();
		this.symbols = properties.getSymbols ();
		this.parser = new LseTokenParser ();
		this.properties = properties;
		this.prepareStyles ();
	}
	protected Collection<String> commands, symbols, keywords;
	protected SnipProperties properties;
	protected SnipToolContext styles;
	protected LseTokenParser parser;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#trackWS()
	 */
	public void trackWS () { parser.trackWS (); trackingWS = true; }
	boolean trackingWS = false;


	/**
	 * allocate style codes for styles to be used.
	 *  construct style map to provide style per token type
	 */
	void prepareStyles ()
	{
		// styles generates in StyleManager and JXR
		IDstyleOK		= styles.getStyleCodeFor ("Identifiers");
		IDstyleBAD		= styles.getStyleCodeFor ("UnknownID");
		commandStyle	= styles.getStyleCodeFor ("Commands");
		keywordStyle	= styles.getStyleCodeFor ("Keywords");

		// posted inline here
		//IDstyleOK = postStyle ("Identifier", Font.PLAIN, Color.BLUE);
		//defaultStyle = postStyle ("General", Font.PLAIN, Color.BLACK);

		assignStyleMapEntries ();
		//postSymbolStyles ();
	}
	protected int
	commandStyle, keywordStyle,
	IDstyleOK, IDstyleBAD;


	/**
	 * typeTable identified symbol types can be given unique styles
	 */
	void postSymbolStyles ()
	{
		//postStyle ("Library", Font.PLAIN, "0x800080");
		//postStyle ("Built-In Delimiter", Font.BOLD, "0x2E8B57");
		//postStyle ("Group Delimiters", Font.BOLD, "0x228B22");

		// these named categories are now configured in
		// the JXR style sheet configuration processing
		// SEE: cfg/gui/SnipStyles.xml referred to by
		// CalcLibSnipStyles
	}


	/**
	 * post a style using a color code
	 * @param name a name to be given to the style
	 * @param fontCode the font code for the style of the font
	 * @param colorCode the encoded color identifier (as hex string)
	 * @return the style code assigned to this posted style
	 */
	int postStyle (String name, int fontCode, String colorCode)
	{
		return postStyle (name, fontCode, Color.decode (colorCode));
	}


	/**
	 * post a style using a color object
	 * @param name a name to be given to the style
	 * @param fontCode the font code for the style of the font
	 * @param color the AWT color object to use as foreground color
	 * @return the style code assigned to this posted style
	 */
	int postStyle (String name, int fontCode, Color color)
	{
		Style style = styles.addStyle (name);

		StyleConstants.setForeground (style, color);
		StyleConstants.setFontFamily (style, fontName);
		StyleConstants.setFontSize (style, fontSize);

		StyleConstants.setItalic (style, fontCode == Font.ITALIC);
		StyleConstants.setBold (style, fontCode == Font.BOLD);

		return styles.assignStyleCode (style);
	}
	protected String fontName; protected int fontSize; // set from properties


	/**
	 * populate map with style codes
	 */
	void assignStyleMapEntries ()
	{
		/*
		 * default style assigned when other assignment is absent
		 */
		defaultStyle = styles.getStyleCodeFor ("Non-Specific");

		/*
		 * this provides just lexical analysis
		 */
		this.styleMap = new HashMap<LseTokenParser.TokenType,Integer>();

		/*
		 * this provides a straight map from the type of lexical element to a style
		 */
		for (LseTokenParser.TokenType t : LseTokenParser.TokenType.values ())
		{
			styleMap.put (t, defaultStyle);
		}

		/*
		 * this is just a simple lexical analysis, semantics are added below
		 */
		styleMap.put (LseTokenParser.TokenType.QOT, styles.getStyleCodeFor ("QuotedText"));
		styleMap.put (LseTokenParser.TokenType.OPR, styles.getStyleCodeFor ("Operators"));
		styleMap.put (LseTokenParser.TokenType.CMT, styles.getStyleCodeFor ("Comments"));
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

		/*
		 * this is where semantic analysis can be done in detail
		 */
		return otherLexicalElement
		(
			image, location,
			scan.tokens.getTokenType ()
		);
	}


	/*
	 * commands and comments are simple lexical elements
	 */


	/**
	 * check token for command recognition
	 * @param image the text of the token
	 * @return TRUE for command
	 */
	boolean isCommand (String image)
	{
		return
			commands.contains (image.toUpperCase ()) ||
			commands.contains (image.toLowerCase ());
	}


	/**
	 * check token for keyword recognition
	 * @param image the text of the token
	 * @return TRUE for keyword
	 */
	boolean isKeyword (String image)
	{
		return keywords.contains (image.toLowerCase ());
	}


//	/**
//	 * check token for comment syntax
//	 * @param image the text of the token
//	 * @return TRUE for comment
//	 */
//	boolean isComment (String image)
//	{
//		return
//			image.startsWith (COMMENT_TOKEN) ||
//			image.toUpperCase ().startsWith (ENTITLED_TOKEN);
//	}
//	public static final String ENTITLED_TOKEN = "ENTITLED";
//	public static final String COMMENT_TOKEN = "//";


	/**
	 * process non-comment token
	 * @param image the text of the token
	 * @param location the location in the document
	 * @param type the token type from the parser
	 * @return the snip token with assigned style
	 */
	SnipToolToken otherLexicalElement
		(
			String image, int location,
			LseTokenParser.TokenType type
		)
	{
		int styleCode;

		if (type != LseTokenParser.TokenType.IDN)
		{
			styleCode =
				nonIdentifierLexicalElement
					(image, type);				// not an identifier
		}
		else if (isCommand (image))
		{
			styleCode = this.commandStyle;		// COMMAND identifier
		}
		else if (isKeyword (image))
		{
			styleCode = this.keywordStyle;		// KEYWORD identifier
		}
		else if (symbols.contains (image))
		{
			styleCode =
				recognizedIdentifier
					(image, type);				// recognized symbol
		}
		else
		{
			styleCode = this.IDstyleBAD;		// unrecognized symbol
		}

		setLastSourcePosition (location + image.length ());
		return new SnipToolToken (image, styleCode);
	}


	/*
	 * semantic analysis amounts to 
	 * how far down the tree symbols are analyzed
	 */

	/**
	 * identifier recognized but not typed
	 * @param image the text image of the symbol
	 * @param type the token type identified in the parser
	 * @return the styleCode to use for this token
	 */
	int recognizedIdentifier
		(
			String image,
			LseTokenParser.TokenType type
		)
	{
		//show (image, "REC");

		/*
		 * at this point all we know is
		 * the symbol table has this symbol...
		 * OK just means the symbol table has it.
		 */
		return choose (image, IDstyleOK);
	}


	/**
	 * non-identifier not yet recognized
	 * @param image the text image of the symbol
	 * @param type the token type identified in the parser
	 * @return the styleCode to use for this token
	 */
	int nonIdentifierLexicalElement
		(
			String image,
			LseTokenParser.TokenType type
		)
	{
		//show (image, "UNK");

		/*
		 * at this point we know 
		 * we are not looking at identifiers.
		 * the style map will just treat it as an operator.
		 * all operators are treated with a single style here.
		 */
		return choose (image, styleMap.get (type));
	}


	/**
	 * choose the style to render this token
	 * @param image the text image of the symbol
	 * @param defaultChoice the default to be used lacking alternate choice
	 * @return the styleCode to use for this token
	 */
	int choose
		(
			String image, int defaultChoice
		)
	{
		Number choice = chooseForType (lookup (image));
		if (choice == null) return defaultChoice;
		else return choice.intValue ();
	}


	/**
	 * provide trace of symbol type
	 * @param image the text image of the symbol
	 * @param kind REC or UNK as identified by caller
	 */
	void show (String image, String kind)
	{
		String type = "NO TYPE";
		Object sym = properties.getSymbolMap ().get (image);
		if (sym != null) type = sym.getClass ().getSuperclass ().getName () + ";" + properties.whatIs (sym);
		System.out.println (kind + ":" + image + " = " + type);
	}


	/*
	 * from here we are down the rabbit hole.
	 * the symbol table offers whatIs as first analysis.
	 * analysis of this answer provides semantic details.
	 */


	/**
	 * look at symbol table for classification
	 * @param image the text image of the symbol
	 * @return the classification name
	 */
	String lookup (String image)
	{
		Object sym = properties.getSymbolMap ().get (image);
		if (sym == null) return "Unrecognized";
		else return properties.whatIs (sym);
	}


	/**
	 * find style by name in manager
	 * @param type the name of the type
	 * @return the style code for the posted type
	 */
	Number chooseForType (String type)
	{
		Style style =
			styles.getStyle (type);
		if (style == null) return null;
		return styles.getStyleCodeFor (style);
	}


	/*
	 * the map of symbol classifications
	 * 
		typeTable.put ("Library", "OperationObject");
		typeTable.put ("SplineDescriptor", "Splines");
		typeTable.put ("Splines", "SplineDescriptor");
		typeTable.put ("Functions", "AbstractFunction");
		typeTable.put ("Symbols", "AbstractVariableLookup");
		typeTable.put ("BuiltIn", "AbstractBuiltinVariableLookup");
		typeTable.put ("AbstractParameterizedFunction", "Built-In Functions");
		typeTable.put ("AbstractUnaryPostfixOperator", "Unary Post-Fix Operators");
		typeTable.put ("AbstractBuiltinVariableLookup", "Built-In Symbols");
		typeTable.put ("AbstractBinaryOperator", "Binary Operators");
		typeTable.put ("AbstractModifiedOperator", "Operator Modifiers");
		typeTable.put ("AbstractUnaryOperator", "Unary Operators");
		typeTable.put ("Assignment", "Assignment Operators");
		typeTable.put ("AbstractVariableLookup", "Symbols");
		typeTable.put ("Delimiter", "Group Delimiters");
		typeTable.put ("AbstractFunction", "Functions");
		typeTable.put ("Object", "Built-In Delimiter");
		typeTable.put ("OperationObject", "Library");
	 */


}

