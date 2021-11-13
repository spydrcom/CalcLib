
package net.myorb.testing;

import net.myorb.math.*;
import net.myorb.math.expressions.*;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.managers.*;

public class SimpleExpressionTest
{


	static ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();
	static Environment<Double> environment = new Environment<Double>(mgr);
	static SymbolMap symbols = environment.getSymbolMap ();

	static
	{
		symbols.addCoreOperators ();
		HighSpeedMathLibrary powerLibrary;
		SymbolTableManager<Double> stabMgr = new SymbolTableManager<Double> (environment);
		stabMgr.importFromTrigLibrary (new OptimizedMathLibrary<Double> (mgr), symbols);
		stabMgr.importFromSpeedLibrary (powerLibrary = new HighSpeedMathLibrary (), symbols);
		stabMgr.importFromSpaceManager (symbols); stabMgr.importFromPowerLibrary (powerLibrary, symbols);
	}


	static EvaluationEngine<Double> engine = new EvaluationEngine<Double> (symbols, mgr, null);


	public static void execute (String source)
	{
		TokenParser.TokenSequence tokens =
			TokenParser.parse (new StringBuffer (source));
		engine.processWithCatch (tokens);
	}


	public static void dump (String symbol)
	{
		System.out.println (symbol + " = " + symbols.lookup (symbol));
	}


	public static void main(String[] args)
	{
		//String source =  // "let result = -2 * (-PI)";
			//"let result =  2 + 1/2 + 1/6 + 1/24 + 1/120 + 1/720 + 1/5040 + 1/40320 + 1/362880";
			//"let result =  1/0! + 1/1! + 1/2! + 1/3! + 1/4! + 1/5! + 1/6! + 1/7! + 1/8! + 1/9!";
			//"let result = abs(1.2 + 2*3**2 - 4*2^3)";
			//"let result = (1 + 2)^3 * (3 + 4)**(1/2)";
			//"let result = (1 + 2)^3 * sqrt(3 + 4)";
			//"let result = exp (ln 25 / 2)";
			//"let result = cos (PI/4)^2 + sin (PI/4)^2";
			//"let result = PI - 6 * asin 0.5";
			//"let result = (1, 2, -3, 4)";
			//"let result = PI - 3 * atan (sqrt 3, 1)";
			//"let result = PI - 3 * atan (sqrt 3)";
			//"let result =  2^(- 2)";
			//"define f(x, y) = x^2 + 2*x*y + y^2";


		execute ("let pi2 = -2 * (-PI)");
		dump ("pi2");

		execute ("let e =  1/0! + 1/1! + 1/2! + 1/3! + 1/4! + 1/5! + 1/6! + 1/7! + 1/8! + 1/9!");
		dump ("e");

		execute ("let DUMPING = 0");

		execute ("define q(a,b,c) = (sqrt(b^2 - 4*a*c) - b) / (2*a)");

		execute ("define f(x, y) = -x^2 + 2*x*y + y^2");
		execute ("let result =  f (2, 3)");
		dump ("result");

		execute ("define d(a,b,c) = b^2 - 4*a*c");
		execute ("define q1(a,b,c) = (sqrt (d (a,b,c)) - b) / (2*a)");
		execute ("define q2(a,b,c) = (-sqrt (d (a,b,c)) - b) / (2*a)");


		execute ("let PHI =  q1 (1, -1, -1)");
		dump ("PHI");

		execute ("let phi =  q2 (1, -1, -1)");
		dump ("phi");

		//execute ("let DUMPING = 1");

		execute ("let m = 2");
		execute ("let n = 3");

		execute ("define f(m,n) = (n - 1) * (m + 1)");
		execute ("let test1 =  f(4,5)");
		dump ("test1");

		execute ("let test2 =  f(6)");
		dump ("test2");
	}

}
