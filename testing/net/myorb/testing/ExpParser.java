
package net.myorb.testing;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.gui.rendering.MathML;
import net.myorb.data.abstractions.ExpressionTokenParser;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.EvaluationControlI;
public class ExpParser<T>
{

	EvaluationControlI <T> control;
	
	T eval (String source) { return control.evaluate (source); }
	
	public static void main (String[] args) throws Exception
	{
		SymbolMap s = new SymbolMap ();
		MathML mml = new MathML (s);
		StringBuffer xpr = new StringBuffer ("( 1 / 3 )");
		ExpressionTokenParser.TokenSequence tokens = TokenParser.parse (xpr);
		System.out.println (tokens);
		String rendered = mml.render (tokens);
		System.out.println (rendered);
	}

}
