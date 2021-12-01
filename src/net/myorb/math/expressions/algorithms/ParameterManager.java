
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MathML;
import net.myorb.math.expressions.TokenParser;

import net.myorb.data.abstractions.ExpressionTokenParser;

/**
 * manage configuration parameters as specified expressions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ParameterManager<T>
{

	/**
	 * all processing tools found in environment object
	 * @param environment the session description
	 */
	public ParameterManager (Environment<T> environment)
	{
		this.environment = environment;
		this.control = environment.getControl ();
		this.mml = new MathML (environment.getSymbolMap ());
	}
	EvaluationControlI <T> control;
	Environment <T> environment;
	MathML mml;
	
	/**
	 * @return the computed value of the expression
	 */
	public T eval ()
	{
		return control.evaluate (xpr.toString ());
	}
	
	/**
	 * render the expression tokens as MML
	 * @return the text of the value specified in configuration expression as MML
	 * @throws Exception for any errors
	 */
	public String render () throws Exception
	{
		tokens = TokenParser.parse (xpr);
		return strip (mml.render (tokens));
	}
	String strip (String mml) { return mml.substring (6, mml.length()-7); }
	ExpressionTokenParser.TokenSequence tokens;

	/**
	 * capture the parameter expression
	 * @param expression the text of the expression describing the parameter value
	 */
	public void setExpression (String expression)
	{ xpr = new StringBuffer (expression); }
	StringBuffer xpr;

}
