
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.OperatorNomenclature;

import net.myorb.data.abstractions.language.ContextSpecificParser;
import net.myorb.data.abstractions.language.ContextSpecificAnalyzer;

import net.myorb.data.abstractions.ExpressionTokenParser;
import net.myorb.data.abstractions.CommonCommandParser;

import net.myorb.gui.editor.model.SnipToolContext;
import net.myorb.gui.editor.model.SnipToolToken;

import javax.swing.text.Style;

import java.util.Collection;

import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;

/**
 * extended token parser tailored to CalcLib script syntax
 *  and made to support style assignment for Language Sensitive Editing features
 * @author Michael Druckman
 */
public class CalcLibSnipScanner extends ContextSpecificAnalyzer
{


	/*
	 * the token types assigned by the parser
	 * 
		IDN, // an identifier (command, keyword, symbol)
		OPR, // an operator, possible multi-character (MCO)
		QOT, // a quoted body of text, a string literal
		NUM, // any numeric value (with no sub-class)
		RDX, // an integer value with specified radix
		INT, // an integer value (no decimal point)
		DEC, // a decimal value (decimal point) 
		FLT, // a floating point value
		CMT, // a comment to EOL
		WS   // white space
	 */


	/**
	 * provide a direct map between
	 *	ContextSpecificParser.TokenType
	 * and style codes
	 */
	protected Map<ContextSpecificParser.TokenType,Integer> styleMap;


	/**
	 * construct scanner based on application properties for Snip tool
	 * @param properties the application properties specific to Snip tool
	 */
	public CalcLibSnipScanner (SnipProperties properties)
	{
		super (new ExpressionSegments ());		// supply context to analyzer
		this.processCollections (properties);	// read symbol data from properties
	}


	/**
	 * identify collections of commands, keywords, and Symbols
	 * @param properties the descriptor for this Snip tool
	 */
	public void processCollections (SnipProperties properties)
	{
		this.styles = properties.newContext ();
		this.commands = properties.getCommands ();
		this.keywords = properties.getKeywords ();
		this.symbols = properties.getSymbols ();
		this.properties = properties;
		this.prepareStyles ();
	}
	protected Collection<String> commands, symbols, keywords;
	protected SnipProperties properties;
	protected SnipToolContext styles;


	/*
	 * processing of style data from the JXR snip styles
	 */


	/**
	 * allocate style codes for styles to be used.
	 *  construct style map to provide style per token type
	 */
	public void prepareStyles ()
	{
		// styles generated in StyleManager using JXR
		commandStyle			= styles.getStyleCodeFor ("Commands");
		recognizedIdentifier	= styles.getStyleCodeFor ("Identifiers");
		unrecognizedIdentifier	= styles.getStyleCodeFor ("UnknownID");
		keywordStyle			= styles.getStyleCodeFor ("Keywords");

		// produce a map that provides defaults for all token types
		assignStyleMapEntries ();
	}
	protected int recognizedIdentifier, unrecognizedIdentifier;
	protected int commandStyle, keywordStyle;


	/**
	 * populate map with style codes
	 */
	void assignStyleMapEntries ()
	{
		/*
		 * default style assigned when other assignment is absent
		 */
		this.defaultStyle = styles.getStyleCodeFor ("Non-Specific");

		/*
		 * this provides just lexical analysis
		 */
		this.styleMap = new HashMap<ContextSpecificParser.TokenType,Integer>();

		/*
		 * this provides a straight map from the type of lexical element to a style
		 */
		for (ContextSpecificParser.TokenType t : ContextSpecificParser.TokenType.values ())
		{
			this.styleMap.put (t, defaultStyle);
		}

		/*
		 * this is just a simple lexical analysis, semantics are added below
		 */
		styleMap.put (ContextSpecificParser.TokenType.QOT, styles.getStyleCodeFor ("QuotedText"));
		styleMap.put (ContextSpecificParser.TokenType.OPR, styles.getStyleCodeFor ("Operators"));
		styleMap.put (ContextSpecificParser.TokenType.CMT, styles.getStyleCodeFor ("Comments"));
	}


	/*
	 * token related processing
	 */


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolScanner#getToken()
	 */
	public SnipToolToken getToken ()
	{
		if (current < scans.size ())
		{
			ContextSpecificParser.Scan
				scan = scans.get (current++);
			String image = scan.tokens.getTokenImage ();
			int location = scan.tracking.getLocation ();
	
			/*
			 * this is where semantic analysis can be done in detail
			 */
			return doSemanticAnalysis
			(
				image, location,
				scan.tokens.getTokenType ()
			);
		} else return null;
	}


	/*
	 * analysis of tokens and style assignments based on analysis
	 */


	/**
	 * process tokens by type
	 * @param image the text of the token
	 * @param location the location in the document
	 * @param type the token type from the parser
	 * @return the snip token with assigned style
	 */
	public SnipToolToken doSemanticAnalysis
		(
			String image, int location,
			ContextSpecificParser.TokenType type
		)
	{
		int styleCode;

		if ( ! isIdentifier (type) )
		{
			styleCode =
				nonIdentifierLexicalElement
					(image, type);						// not an identifier
		}
		else if (isCommand (image))
		{
			styleCode = this.commandStyle;				// COMMAND identifier
		}
		else if (isKeyword (image))
		{
			styleCode = this.keywordStyle;				// KEYWORD identifier
		}
		else if (isRecognized (image))
		{
			styleCode =
				recognizedIdentifier
					(image, type);						// recognized symbol
		}
		else
		{
			styleCode = this.unrecognizedIdentifier;	// unrecognized symbol
		}

		setLastSourcePosition (location + image.length ());
		return new SnipToolToken (image, styleCode);
	}


	/*
	 * logic for assigned analysis results
	 */


	/**
	 * identifiers have a specific type
	 * @param type the category assigned to a token
	 * @return TRUE when type indicates a token type
	 */
	public boolean isIdentifier (ContextSpecificParser.TokenType type)
	{
		return type == ContextSpecificParser.TokenType.IDN;
	}


	/**
	 * identifiers are recognized
	 *  when present in symbols table
	 * @param image the text of the token
	 * @return TRUE for recognized
	 */
	public boolean isRecognized (String image)
	{
		return symbols.contains (image);
	}


	/*
	 * commands and keywords are recognized identifier tokens
	 */


	/**
	 * check token for command recognition
	 * @param image the text of the token
	 * @return TRUE for command
	 */
	public boolean isCommand (String image)
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
	public boolean isKeyword (String image)
	{
		return keywords.contains (image.toLowerCase ());
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
	public int recognizedIdentifier
		(
			String image,
			ContextSpecificParser.TokenType type
		)
	{
		//show (image, "REC");

		/*
		 * at this point all we know is
		 * the symbol table has this symbol...
		 * OK just means the symbol table has it.
		 */
		return choose (image, recognizedIdentifier);
	}


	/**
	 * non-identifier not yet recognized
	 * @param image the text image of the symbol
	 * @param type the token type identified in the parser
	 * @return the styleCode to use for this token
	 */
	public int nonIdentifierLexicalElement
		(
			String image,
			ContextSpecificParser.TokenType type
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
	public int choose
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
	public void show (String image, String kind)
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
	public String lookup (String image)
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
	public Number chooseForType (String type)
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


/**
 * token descriptions specific to CalcLib
 */
class ExpressionSegments
	extends ExpressionTokenParser
	implements CommonCommandParser.SpecialTokenSegments
{

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ExpressionTokenParser#getSequenceCaptureMarkers()
	 */
	public String getSequenceCaptureMarkers () { return "{}"; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ExpressionTokenParser#getCommentIndicators()
	 */
	public Collection <String> getCommentIndicators () { return COMMENT_INDICATORS; }

	/**
	 * comment syntax and recognition was prone to causing bugs
	 * 	****  so it is being specifically outlined here  ****
	 */
	static final Set <String> COMMENT_INDICATORS;

	static
	{

		// recognition of operators that work as EOL comments
		COMMENT_INDICATORS = new HashSet <String> ();

		// refer back to language Nomenclature
		COMMENT_INDICATORS.add (OperatorNomenclature.COMMENT_PREFIX);
		COMMENT_INDICATORS.add (OperatorNomenclature.ENTITLED_KEYWORD);
		COMMENT_INDICATORS.add (OperatorNomenclature.TIP_PREFIX);

	}

}

