
package net.myorb.testing;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.math.computational.*;

public class RegressionTest
{

	static void runTest ()
	{
		DataSequence2D<Double> data = new DataSequence2D<Double> ();
		
		data.addSample (2.0, 8.0);
		data.addSample (2.56, 9.45);
		data.addSample (5.3, 17.1);
		data.addSample (7.1, 22.9);

		System.out.println ("========================");
		Regression<Double> regression = Regression.newInstance ();
		Regression.Model<Double> line = regression.leastSquares (data);
		line.getPolynomialSpaceManager ().show (line);

		System.out.println ("========================");

		System.out.println ("r^2 = " + line.rSquared ());
		System.out.println ("STD = " + line.computedStd ());
		System.out.println ("COV = " + line.computedCOV ());
		System.out.println ("MSE = " + line.computedMSE ());
		System.out.println ("SSR = " + line.computedSSR ());
		System.out.println ("SSE = " + line.computedSSE ());
		System.out.println ("SST = " + line.computedSST ());
		System.out.println ("PSr = " + line.pearsonCoefficient ());

		System.out.println ("========================");
		
		Regression.Model<Double> poly = regression.byPolynomial (data);
		poly.getPolynomialSpaceManager ().show (poly);

		System.out.println ("========================");

		System.out.println ("r^2 = " + poly.rSquared ());
		System.out.println ("STD = " + poly.computedStd ());
		System.out.println ("COV = " + poly.computedCOV ());
		System.out.println ("MSE = " + poly.computedMSE ());
		System.out.println ("SSR = " + poly.computedSSR ());
		System.out.println ("SSE = " + poly.computedSSE ());
		System.out.println ("SST = " + poly.computedSST ());

		System.out.println ("========================");
		
	}

	/**
	 * execute tests
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		runTest ();
	}

}
