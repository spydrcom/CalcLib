
package net.myorb.testing.eqrep;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.algorithms.ConfigurationManager;
import net.myorb.math.expressions.SymbolMap;

import java.util.*;

class ParserTest
{

	public static void main (String... args) throws Exception
	{
		SymbolMap s = new ConfigurationManager ("cfg/default.txt").getSymbolMap ();
		List<TokenParser.TokenDescriptor> tokens = TokenParser.parse (new StringBuffer (EQ));
		s.addCoreOperators ();
		
		for (TokenParser.TokenDescriptor t : tokens)
		{
			TokenParser.TokenType tt = t.getTokenType();

			Object sym = s.get(t.getTokenImage ());

			if (sym != null && sym instanceof SymbolMap.Operation)
			{
				tt = TokenParser.TokenType.OPR;
			}

			System.out.print (t);
			System.out.print ("     ");
			System.out.print (tt);

			if (sym != null && sym instanceof SymbolMap.Operation)
			{
				System.out.print ("     ");
				System.out.print (sym);
				System.out.print ("  ");
				System.out.print (sym.getClass().getSimpleName());
				System.out.print ("  ");
				System.out.print (sym.getClass().getSuperclass().getSimpleName());
				System.out.print ("  ");
				System.out.print (((SymbolMap.Operation)sym).getSymbolType());
				System.out.print ("  ");
				System.out.print (((SymbolMap.Operation)sym).getPrecedence());
			}

			System.out.println ();
		}
	}
	static String EQ = "p(x) = - 2 * x^2 - 3 * x! + 4 * sin x * cos x * sqrt 2 * atan x";

}
