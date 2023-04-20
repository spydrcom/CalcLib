
package net.myorb.math.expressions.commands;

import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.DifferentialEquationsManager;
import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.PrettyPrinter;
import net.myorb.math.expressions.TokenParser;

import java.util.List;

/**
 * support for commands performing renders
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Rendering <T> extends PrettyPrinter <T>
{


	/**
	 * extending PrettyPrinter with specific properties
	 * @param nameForFrame the name to be given to the display
	 * @param environment the environment object for this run
	 */
	public Rendering (String nameForFrame, Environment<T> environment)
	{ super (nameForFrame, 500, 1000, environment); this.environment = environment; }
	protected Environment<T> environment;


	/**
	 * lookup function in symbol table
	 * @param tokens the source from the command line
	 * @return the function if found in the symbol table
	 * @throws RuntimeException when function not found
	 */
	public AbstractFunction <T>
		identify (CommandSequence tokens)
	throws RuntimeException
	{
		String functionName = Utilities.getSequenceFollowing (1, tokens);
		AbstractFunction <T> f = AbstractFunction.cast ( formatter.getSymbolMap ().get (functionName) );
		if (f == null) { throw new RuntimeException ("No such function: " + functionName); }
		return f;
	}


	/**
	 * command line tokens are rendered as expression/equation
	 * @param sequence the list of tokens from the command
	 */
	public void RenderFrom (CommandSequence sequence)
	{
		if ( renderSuppressed )
		{ renderSuppressed = false; return; }
		prettyPrint (Utilities.startingFrom (1, sequence), null);
	}
	protected boolean renderSuppressed = false;


	/**
	 * render an expanded series posted to the symbol table
	 * @param sequence the list of tokens from the command
	 */
	public void RenderSeries (CommandSequence sequence)
	{
		AbstractFunction <T> F = identify (sequence);
		new PrettyPrinter <T> (environment).formatSeries
		(F.getName (), F.getSeries (), this);
	}


	/**
	 * render an entry from the functions list
	 * @param sequence the list of tokens from the command
	 */
	public void RenderFunction (CommandSequence sequence)
	{
		TokenParser.TokenSequence tokens; AbstractFunction<T> f;
		String functionName = ( f = identify (sequence) ).getName ();
		if ( ! OperatorNomenclature.isIndexReference (functionName) )
		{   tokens = getProfileTokens (functionName, f);   }
		else tokens = getLambdaTokens (functionName, f);
		prettyPrint (tokens, f.getParameterNames ());
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
		DifferentialEquationsManager <T>
			deqMgr = environment.getDifferentialEquationsManager ();
		String functionName = Utilities.getSequenceFollowing (1, sequence);

		if ( ! deqMgr.wasRendered (functionName) )
		{ prettyPrint (deqMgr, functionName); }
		else { renderSuppressed = true; }
	}
	void prettyPrint (DifferentialEquationsManager <T> DEQ, String functionName)
	{
		try { prettyPrint (DEQ.getRenderSequence (functionName), null); }
		catch (Alert alert) { alert.presentDialog (); }
	}


	/**
	 * call pretty printer to render function
	 * @param functionTokens the tokens that make up the function
	 * @param parameterNames a list of the names of the parameters
	 */
	private void prettyPrint
	(TokenParser.TokenSequence functionTokens, List <String> parameterNames)
	{
		try
		{
			render
			(
				functionTokens, parameterNames,
				TokenParser.toPrettyText (functionTokens)
			);
		}
		catch (Exception e) { e.printStackTrace (); }
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


	/**
	 * prepare lambda expression to be rendered in function display
	 * @param functionName the name of the function being rendered
	 * @param function the low level representation of the function
	 * @return the list of tokens making up the profile
	 */
	private TokenParser.TokenSequence getLambdaTokens
		(String functionName, AbstractFunction<T> function)
	{
		TokenParser.TokenSequence profileTokens = TokenParser.parse
			(
				append
				(
					function.getParameterNameList ()
						.formatNameList (true),
					functionName
				)
			);
		profileTokens.addAll (function.getFunctionTokens ());
		return profileTokens;
	}
	StringBuffer append (StringBuffer parameterList, String functionName)
	{
		return new StringBuffer ().append (functionName).append (" = ")
			.append (START).append (" ").append (parameterList).append (" ")
			.append (END).append (" ").append (LAMBDA).append (" ");
	}
	public static final String
	LAMBDA = OperatorNomenclature.LAMBDA_EXPRESSION_INDICATOR,
	START = OperatorNomenclature.START_OF_FORMAL_LIST_DELIMITER,
	END = OperatorNomenclature.END_OF_FORMAL_LIST_DELIMITER;


}

