
package net.myorb.testing.eqrep;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.algorithms.ConfigurationManager;

import java.util.*;

public class PrecTest
{

	public static void eval (List<TokenParser.TokenDescriptor> tokens) throws Exception
	{
		List<String> opStack = new ArrayList<String>();
		List<String> leafStack = new ArrayList<String>();
		List<Integer> precStack = new ArrayList<Integer>();

		String lastLeaf = "", lastOp = "";
		int t = 0, prec = 0, lastPrec = 0;
		TokenParser.TokenDescriptor td;
		TokenParser.TokenType tt;
		SymbolMap.Named n;
		String tx, ti;

		while (t < tokens.size ())
		{
			td = tokens.get (t++);
			tt = td.getTokenType ();
			ti = td.getTokenImage ();
			n = null;

			if (tt == TokenParser.TokenType.IDN || tt == TokenParser.TokenType.OPR)
			{
				n = (SymbolMap.Named) s.get (ti);
				if (n instanceof SymbolMap.Operation) tt = TokenParser.TokenType.OPR;
			}

			if (tt == TokenParser.TokenType.OPR)
			{
				tx = "<mo>" + ti + "</mo>";
				SymbolMap.Operation o = (SymbolMap.Operation) n;
				prec = o.getPrecedence ();
				
				if (prec > lastPrec)
				{
					precStack.add (lastPrec);
					opStack.add (lastOp);
				}
				else
				{
					while (prec <= lastPrec)
					{
						String prevLeaf = leafStack.remove (leafStack.size() - 1);
						lastLeaf = prevLeaf + lastOp + lastLeaf;

						if (precStack.size() == 0) break;
						lastPrec = precStack.remove (precStack.size() - 1);
						lastOp = opStack.remove (opStack.size() - 1);
					}
				}
				lastPrec = prec;
				lastOp = tx;
			}
			else if (tt == TokenParser.TokenType.IDN)
			{
				leafStack.add (lastLeaf);
				lastLeaf = "<mi>" + ti + "</mi>";
			}
			else
			{
				leafStack.add (lastLeaf);
				lastLeaf = "<mn>" + ti + "</mn>";
			}
			System.out.println(lastLeaf);
			System.out.println(leafStack);
			System.out.println(opStack);
			System.out.println("+++");
		}

		precStack.add (lastPrec);
		opStack.add (lastOp);

		while (lastPrec > 0)
		{
			if (opStack.size() == 0) break;
			lastLeaf = leafStack.remove (leafStack.size() - 1) +
					opStack.remove (opStack.size() - 1) + lastLeaf;

			if (precStack.size() == 0) break;
			lastPrec = precStack.remove (precStack.size() - 1);
		}

		System.out.println(lastLeaf);
	}

	public static void main (String... args) throws Exception
	{
		s = new ConfigurationManager ("cfg/default.txt").getSymbolMap ();
		s.addCoreOperators ();

		List<TokenParser.TokenDescriptor> tokens = TokenParser.parse (new StringBuffer (EQ));

		eval (tokens);
	}
	static String EQ = "6 * x^3  + 5 * x^2  - 3 * x + 4";
	static SymbolMap s;

}
