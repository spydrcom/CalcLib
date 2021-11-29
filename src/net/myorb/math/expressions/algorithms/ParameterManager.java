
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MathML;
import net.myorb.math.expressions.TokenParser;

import net.myorb.data.abstractions.ExpressionTokenParser;

public class ParameterManager<T>
{

	public ParameterManager (Environment<T> environment)
	{
		this.environment = environment;
		this.control = environment.getControl ();
		this.mml = new MathML (environment.getSymbolMap ());
	}
	EvaluationControlI <T> control;
	Environment <T> environment;
	MathML mml;
	
	public T eval ()
	{
		return control.evaluate (xpr.toString ());
	}
	
	public String render () throws Exception
	{
		tokens = TokenParser.parse (xpr);
		return strip (mml.render (tokens));
	}
	String strip (String mml) { return mml.substring (6, mml.length()-7); }
	ExpressionTokenParser.TokenSequence tokens;

	public void setExpression (String expression)
	{ xpr = new StringBuffer (expression); }
	StringBuffer xpr;

}
