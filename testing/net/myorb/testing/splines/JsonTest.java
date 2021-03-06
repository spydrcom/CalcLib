
package net.myorb.testing.splines;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.computational.splines.ChebyshevSpline;
import net.myorb.math.computational.splines.FittedFunction;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;
import net.myorb.data.abstractions.SimpleStreamIO;

public class JsonTest
{

	public static void main (String[] a) throws Exception
	{

		SimpleStreamIO.TextSource source =
			SimpleStreamIO.getFileSource ("expressions/G55.json");
		ChebyshevSpline spline = new ChebyshevSpline ();
		FittedFunction<ComplexValue <Double>> f =
			new FittedFunction <> (mgr, spline);
		f.readFrom (source);

		for (int i=1; i<10; i++)
		{
			System.out.println (f.eval (mgr.C ((double)i, 0.0)));
		}

		System.out.println ();
		System.out.println ("integral");
		System.out.println (f.evalIntegralOver (0, 100));

	}

	//	ComplexValue<Double> Z = C (1.5, 5.0);		
	// G(2.5+5j) = mpc(real='0.022673603189800138', imag='-0.011722844041715128')

	static ExpressionComplexFieldManager mgr = new ExpressionComplexFieldManager ();

}
