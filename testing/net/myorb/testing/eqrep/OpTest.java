
package net.myorb.testing.eqrep;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.algorithms.ConfigurationManager;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import java.util.ArrayList;
import java.util.List;

public class OpTest
{

	public static String eval (List<TokenParser.TokenDescriptor> tokens) throws Exception
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

			System.out.println (ti + "  " + tt);

			if (tt == TokenParser.TokenType.IDN || tt == TokenParser.TokenType.OPR)
			{
				n = (SymbolMap.Named) s.get (ti);
				if (n instanceof SymbolMap.Operation) tt = TokenParser.TokenType.OPR;
			}

			if (tt == TokenParser.TokenType.OPR)
			{
				tx = ti;
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
						lastLeaf = reduce (leafStack, lastOp, lastLeaf);
						if (precStack.size() == 0 || opStack.size() == 0) break;
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

			System.out.println(lastOp + " " + lastPrec);
			System.out.println(lastLeaf);
			System.out.println(leafStack);
			System.out.println(opStack);
			System.out.println("+++");
		}

		precStack.add (lastPrec);

		while (lastPrec > 0)
		{
			lastLeaf = reduce (leafStack, lastOp, lastLeaf);
			if (precStack.size() == 0 || opStack.size() == 0) break;
			lastPrec = precStack.remove (precStack.size() - 1);
			lastOp = opStack.remove (opStack.size() - 1);
		}

		String document = "<math><mrow>" + lastLeaf + "</mrow></math>";

		return document;
	}


	static String reduce (List<String> leafStack, String lastOp, String lastLeaf)
	{
		SymbolMap.Named n = (SymbolMap.Named) s.get (lastOp);
		SymbolMap.Operation o = (SymbolMap.Operation) n;

		if (o instanceof SymbolMap.BinaryOperator)
		{
			String prevLeaf = leafStack.remove (leafStack.size() - 1);
			SymbolMap.BinaryOperator bop = (SymbolMap.BinaryOperator)o;
			lastLeaf = bop.markupForDisplay (lastOp, prevLeaf, lastLeaf, false, false, formatter);
		}
		else if (o instanceof SymbolMap.UnaryPostfixOperator)
		{
			SymbolMap.UnaryPostfixOperator upop = (SymbolMap.UnaryPostfixOperator)o;
			lastLeaf = upop.markupForDisplay (lastLeaf, false, lastOp, formatter);
			
		}
		else if (o instanceof SymbolMap.UnaryOperator)
		{
			SymbolMap.UnaryOperator uop = (SymbolMap.UnaryOperator)o;
			//lastLeaf = uop.markupForDisplay (lastOp, lastLeaf, false, formatter);
			lastLeaf = uop.markupForDisplay (lastOp, lastLeaf, formatter);
		}
		else if (o instanceof SymbolMap.Delimiter)
		{
			switch (o.getPrecedence ())
			{
			case SymbolMap.OPEN_GROUP_PRECEDENCE:
			break;

			case SymbolMap.CLOSE_GROUP_PRECEDENCE:
			break;

			case SymbolMap.OPEN_ARRAY_PRECEDENCE:
			break;

			case SymbolMap.CLOSE_ARRAY_PRECEDENCE:
			break;

			case SymbolMap.CONTINUE_GROUP_PRECEDENCE:
			break;

			default:  throw new RuntimeException ("Delimiter error");
			}
		}

		return lastLeaf;
	}


	public static void main (String... args) throws Exception
	{
		s = new ConfigurationManager ("cfg/default.txt").getSymbolMap ();
		s.addCoreOperators ();

		List<TokenParser.TokenDescriptor> tokens = TokenParser.parse (new StringBuffer (EQ));

		System.out.println (eval (tokens));
	}
	static NodeFormatting formatter;
	//static String EQ = "6 * x^3  + 5 * x^2  - 3 * x + 4";
	static String EQ = "(sqrt (b^2 - 4*a*c) - b) / (2*a)";
	static SymbolMap s;


}
