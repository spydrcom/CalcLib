
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * command implementation for Series Expansion algorithm
 * @author Michael Druckman
 */
public class SeriesExpansion <T>
{


	public SeriesExpansion (Environment <T> environment)
	{ this.environment = environment; }
	Environment <T> environment;


	/**
	 * perform expansion of named polynomial
	 * @param functionName the function name given to the polynomial
	 * @param tokens the command tokens specified on the request
	 * @param tokenPosition the position of the next token
	 */
	public void performExpansion
	(String functionName, CommandSequence tokens, int tokenPosition)
	{
		System.out.println (functionName + " - " + tokenPosition);
		System.out.println (environment.getSymbolMap().get(functionName));
	}


}

