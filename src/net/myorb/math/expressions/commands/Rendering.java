
package net.myorb.math.expressions.commands;

import net.myorb.math.expressions.DifferentialEquationsManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.PrettyPrinter;
import net.myorb.math.expressions.TokenParser;

/**
 * support for commands performing renders
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Rendering<T> extends PrettyPrinter<T>
{


	/**
	 * extending PrettyPrinter with specific properties
	 * @param nameForFrame the name to be given to the display
	 * @param environment the environment object for this run
	 */
	public Rendering (String nameForFrame, Environment<T> environment)
	{ super (nameForFrame, 500, 1000, environment); this.environment = environment; }
	Environment<T> environment;


	/**
	 * command line tokens are rendered as expression/equation
	 * @param sequence the list of tokens from the command
	 */
	public void RenderFrom (CommandSequence sequence)
	{
		if ( renderSuppressed )
		{ renderSuppressed = false; return; }
		prettyPrint (Utilities.startingFrom (1, sequence));
	}
	boolean renderSuppressed = false;


	/**
	 * render an entry from the functions list
	 * @param sequence the list of tokens from the command
	 */
	public void RenderFunction (CommandSequence sequence)
	{
		String functionName = Utilities.getSequenceFollowing (1, sequence);
		AbstractFunction<T> f = AbstractFunction.cast (formatter.getSymbolMap ().get (functionName));
		if (f == null) throw new RuntimeException ("No such function: " + functionName);
		prettyPrint (getProfileTokens (functionName, f));
	}


	/**
	 * post Differential Equations to Manager
	 * @param functionName the name of the defining function
	 * @param definition the function declaration object
	 * @param tokens the tokens of the display function
	 */
	public void post (String functionName, AbstractFunction<T> definition, TokenParser.TokenSequence tokens)
	{
		environment.getDifferentialEquationsManager ().post (functionName, definition, tokens);
	}


	/**
	 * render an entry from the functions list as Differential Equation
	 * @param sequence the list of tokens from the command
	 */
	public void RenderDifferentialEquation (CommandSequence sequence)
	{
		String functionName = Utilities.getSequenceFollowing (1, sequence);
		DifferentialEquationsManager <T> deqMgr = environment.getDifferentialEquationsManager ();

		if (deqMgr.wasRendered (functionName))
		{
			renderSuppressed = true;
		}
		else
		{
			try { prettyPrint (deqMgr.getRenderSequence (functionName)); }
			catch (Alert alert) { alert.presentDialog (); }
		}
	}


	/**
	 * call pretty printer to render function
	 * @param functionTokens the tokens that make up the function
	 */
	private void prettyPrint (TokenParser.TokenSequence functionTokens)
	{
		try
		{
			render (functionTokens, TokenParser.toPrettyText (functionTokens));
		}
		catch (Exception e) { e.printStackTrace(); }
	}


	/**
	 * prepare profile to be rendered in function display
	 * @param functionName the name of the function being rendered
	 * @param function the low level representation of the function
	 * @return the list of tokens making up the profile
	 */
	private TokenParser.TokenSequence getProfileTokens (String functionName, AbstractFunction<T> function)
	{
		StringBuffer profile =
			new StringBuffer (functionName).append (function.getParameterNameList ().getProfile ());
		TokenParser.TokenSequence profileTokens = TokenParser.parse (profile.append (" = "));
		profileTokens.addAll (function.getFunctionTokens ());
		return profileTokens;
	}


}

