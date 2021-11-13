
package net.myorb.testing.eqrep;

import java.util.List;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.algorithms.ConfigurationManager;

import net.myorb.math.expressions.gui.rendering.*;

public class ReduceTest
{

	public static void main (String... args) throws Exception
	{
		SymbolMap s;
		(s = new ConfigurationManager ("cfg/default.txt").getSymbolMap ()).addCoreOperators ();
		List<TokenParser.TokenDescriptor> tokens = TokenParser.parse (new StringBuffer (EQ));
		System.out.println ((new MathML (s)).render (tokens));
	}
	static String EQ = "6 * x^3  + 5 * x^2  - 3 * x + 4";
	//static String EQ = "(sqrt (b^2 - 4*a*c) - b) / (2*a)";

}
