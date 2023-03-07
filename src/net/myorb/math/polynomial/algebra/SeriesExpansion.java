
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.data.notations.json.JsonPrettyPrinter;

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
		Subroutine<T> s = Subroutine.cast (environment.getSymbolMap ().get (functionName));
		if (s == null) throw new RuntimeException ("Symbol is not a user defined function: " + functionName);

		try { s.allowExpressionTree (); s.enableExpression (); }
		catch (Exception e) { throw new RuntimeException ("Error building tree", e); }

		try { JsonPrettyPrinter.sendTo (s.getExpression ().toJson (), System.out); }
		catch (Exception e) { e.printStackTrace (); }

		Elements.Equation eqn = RepresentationConversions.translate (s.getExpression ().toJson ());
		System.out.println (eqn);
	}


}

