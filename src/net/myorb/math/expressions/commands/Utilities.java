
package net.myorb.math.expressions.commands;

import net.myorb.charting.DisplayGraphSegmentTools;
import net.myorb.math.expressions.gui.DisplayConsole;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.TokenParser;

import net.myorb.utilities.ApplicationShell;
import net.myorb.httpd.JavaDocsServer;
import net.myorb.httpd.HttpServer;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import java.io.PrintStream;
import java.io.File;

/**
 * support for command processing
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Utilities<T>
{


	/**
	 * common storage of environment for command processing layer.
	 *  this allows exposure of utility methods and the storage of environment both in one super-class
	 * @param environment the environment object that provides atomic functionality
	 */
	public Utilities
	(Environment<T> environment) { this.environment = environment; }
	protected Environment<T> environment;


	/**
	 * get text of token at index
	 * @param tokens a command sequence
	 * @param number the index of the token to get
	 * @return the image of the indexed token
	 */
	public static String imageOf
	(List<TokenParser.TokenDescriptor> tokens, int number)
	{ return tokens.get (number).getTokenImage (); }


	/**
	 * get text of token at index
	 * @param tokens a command sequence
	 * @param number the index of the token to get
	 * @param defaultValue value to return if number out of range
	 * @return the image of the indexed token or the default as described
	 */
	public static String imageOf
	(List<TokenParser.TokenDescriptor> tokens, int number, String defaultValue)
	{ return number < tokens.size () ? imageOf (tokens, number) : defaultValue; }


	/**
	 * commands are forced to lower case to effect case independence
	 * @param ofTokens the list of tokens forming the command
	 * @return the first token in lower case
	 */
	protected static String commandToken (List<TokenParser.TokenDescriptor> ofTokens)
	{
		return ofTokens.get (0).getTokenImage ().toLowerCase ();
	}


	/**
	 * @param position the starting point in the sequence
	 * @param fromSequence the source sequence of tokens
	 * @return the text sequence after position
	 */
	public static String getSequenceFollowing
		(int position, TokenParser.TokenSequence fromSequence)
	{ return getTextOfSequence (startingFrom (position, fromSequence)); }


	/**
	 * @param sequence the sequence of tokens
	 * @return the text of the sequence
	 */
	public static String
		getTextOfSequence (TokenParser.TokenSequence sequence)
	{ return TokenParser.toString (sequence).replace (" ", ""); }


	/**
	 * @param startingPosition starting position
	 * @param tokens the source tokens from the command
	 * @return the sub-list starting at startingPosition
	 */
	public static TokenParser.TokenSequence
		startingFrom (int startingPosition, TokenParser.TokenSequence tokens)
	{ return tokens.between (startingPosition, tokens.size ()); }


	/**
	 * step to next token
	 * @param tokens the source tokens from the command
	 * @return first token after 0 removed
	 */
	public static TokenParser.TokenDescriptor
		getNextOperandToken (TokenParser.TokenSequence tokens)
	{ tokens.remove (0); return tokens.get (0); }


	/**
	 * step to next token
	 * @param tokens the source tokens from the command
	 * @return text of first token after 0 removed
	 */
	public static String
		getNextOperandImage (TokenParser.TokenSequence tokens)
	{ return getNextOperandToken (tokens).getTokenImage (); }


	/**
	 * step to next token
	 * @param tokens the source tokens from the command
	 * @return text of first token in list which is removed from list
	 */
	public static String
		getCurrentOperandImage (TokenParser.TokenSequence tokens)
	{ return tokens.remove (0).getTokenImage (); }


	/**
	 * get name text from token list if present
	 * @param tokens the source tokens from the command
	 * @return name found in token list, otherwise default UNNAMED
	 */
	public static String
		getNameTag (TokenParser.TokenSequence tokens)
	{
		if (getNextOperandToken (tokens).getTokenType () == TokenParser.TokenType.QOT)
		{ return getCurrentOperandImage (tokens); }
		else return "[UNNAMED]";
	}


	/**
	 * @param tokens source of token sequence
	 * @return token list converted to string list
	 */
	public static List<String> toList (TokenParser.TokenSequence tokens)
	{
		List<String> items = new ArrayList<String>();
		for (TokenParser.TokenDescriptor t : tokens) { items.add (t.getTokenImage ()); }
		return items;
	}


	/**
	 * show associated JavaDocs in browser
	 * @param tokens the source tokens from the command
	 */
	public static void showJavaDocs (TokenParser.TokenSequence tokens)
	{
		TokenParser.TokenDescriptor token = null;
		if (tokens.size () > 1) token = tokens.get (1);
		if (token == null || token.getTokenType () != TokenParser.TokenType.INT)		// specify port number to select URL access
		{
			String dir = null, resource = "index.html";
			if (tokens.size () > 1) dir = getNextOperandImage (tokens);
			if (tokens.size () > 1) resource = getNextOperandImage (tokens);
			showJavaDocsByFile (dir, resource);											// access docs by direct use of file system
		}
		else showJavaDocsByUrl (token.getTokenValue ().intValue (), tokens);			// access docs by localhost URL
	}


	/**
	 * use browser with URL
	 * @param port server port number
	 * @param tokens optional tokens
	 */
	public static void showJavaDocsByUrl (int port, TokenParser.TokenSequence tokens)
	{
		TokenParser.TokenDescriptor token = null;
		String dir = null, resource = "index.html";
		if (tokens.size () > 2) token = tokens.get (2);

		if (token != null)
		{
			switch (token.getTokenType ())
			{
				case IDN:
					dir = token.getTokenImage ();
					if (tokens.size () > 3) resource = tokens.get (3).getTokenImage ();
					break;
				default:
					resource = tokens.get (3).getTokenImage () + ".cmd";
					break;
			}
		}

		verifyServerOn (port);
		showJavaDocsByUrl (port, dir, resource);
	}


	/**
	 * @param port server port number
	 * @throws RuntimeException for server start failure
	 */
	public static void verifyServerOn (int port) throws RuntimeException
	{
		HttpServer server = SERVERS.get (port);
		if ( ! (server == null || server.isActive ()) ) server = null;

		if (server == null)
		{
			try
			{
				SERVERS.put (port, JavaDocsServer.start (port));
			}
			catch (Exception e)
			{
				throw new RuntimeException ("Unable to staert JavaDocs server", e);
			}
		}
	}
	static HashMap <Integer, HttpServer> SERVERS = new HashMap <> ();


	/**
	 * use browser with URL
	 * @param port server port number
	 * @param dir first directory level or NULL for none
	 * @param resource the name of the resource
	 */
	public static void showJavaDocsByUrl (int port, String dir, String resource)
	{
		try
		{
			String url = "http://localhost:" + port + "/";
			if (dir != null) url += dir + "/"; url += resource;
			ApplicationShell.showForType (url);
		}
		catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * use browser with file system reference
	 * @param dir first directory level or NULL for none
	 * @param resource the name of the resource
	 */
	public static void showJavaDocsByFile (String dir, String resource)
	{
		File path = new File ("doc");
		if (dir != null) path = new File (path, dir);
		try { ApplicationShell.showForType (new File (path, resource).getAbsolutePath ()); }
		catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * function name allowing from f' notation
	 * @param pos the starting position in the token list
	 * @param tokens the list of tokens being parsed
	 * @param funtionName the name parsed
	 * @return the end position
	 */
	public static int getFunctionName
		(int pos, TokenParser.TokenSequence tokens, StringBuffer funtionName)
	{
		String p, PRIME = OperatorNomenclature.PRIME_OPERATOR;
		funtionName.append (tokens.get (++pos).getTokenImage ()); pos++;
		if (pos < tokens.size () && (p = tokens.get (pos).getTokenImage ()).startsWith (PRIME))
		{ funtionName.append (p); pos++; }
		return pos;
	}


	/**
	 * default function name starting at 0
	 * @param tokens the list of tokens being parsed
	 * @return the text of the name, pos is lost
	 */
	public static String getFunctionName (TokenParser.TokenSequence tokens)
	{
		StringBuffer fullName = new StringBuffer (); getFunctionName (0, tokens, fullName); return fullName.toString ();
	}


	/**
	 * get limit parameter if present
	 * @param tokens the tokens of the command
	 */
	public static void processLimit (TokenParser.TokenSequence tokens)
	{
		if (tokens.get (1).getTokenImage ().startsWith ("LIM"))
		{
			double rangeLimitToUse = Double.parseDouble (tokens.get (2).getTokenImage ());
			DisplayGraphSegmentTools.getSegmentControl ().setRangeLimit (rangeLimitToUse);
			tokens.remove (2); tokens.remove (1);
		}
	}
	public static void resetLimit () { DisplayGraphSegmentTools.resetSegmentControl (); }


	/**
	 * open new frame for output
	 * @param title text of title for new display
	 * @param environment the environment properties collection object
	 * @return a stream object for output
	 * @param <T> data type
	 */
	public static <T> PrintStream openNewConsole (String title, Environment<T> environment)
	{
		return DisplayConsole.showConsole (title, environment.getControl ().getGuiMap (), 700);
	}


	/**
	 * get file access for path
	 * @param path the path to the file
	 * @return access to the file
	 */
	public static File fileFor (String path)
	{
		return new File (path);
	}


}

