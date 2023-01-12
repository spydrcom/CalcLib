
package net.myorb.testing.splines;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.math.computational.splines.ChebyshevSpline;
import net.myorb.math.computational.splines.FittedFunction;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

public class ItiTest
{

	public static void main (String[] a) throws Exception
	{

		SimpleStreamIO.TextSource source =
			SimpleStreamIO.getFileSource ("expressions/IT.json");
		FittedFunction<Double> f = new FittedFunction <> (mgr, new ChebyshevSpline ());
		f.readFrom (source);

		System.out.println ();
		System.out.println ("integral");
		System.out.println (f.evalIntegralOver (0, 1));

	}

	static ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();

}
