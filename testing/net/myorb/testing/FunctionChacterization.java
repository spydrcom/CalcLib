
package net.myorb.testing;

import net.myorb.math.*;
import net.myorb.math.computational.*;
import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.computational.PolynomialFunctionCharacteristics.CharacteristicAttributes;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import java.util.Arrays;
import java.util.Map;

public class FunctionChacterization
{


	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	static PolynomialSpaceManager<Double> psm = new PolynomialSpaceManager<Double> (mgr);
	static ExponentiationLib<Double> lib = new ExponentiationLib<Double> (mgr);


	/**
	 * execute tests
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		PowerFunction<Double> pf =
			psm.getPolynomialFunction (psm.newCoefficients (5.0, 1.0));
		pf = multiply (pf, psm.newCoefficients (-4.0, 1.0));
		pf = multiply (pf, psm.newCoefficients (-1.0, 5.0));
		pf = multiply (pf, psm.newCoefficients (-7.0, 1.0));
		pf = multiply (pf, psm.newCoefficients (-12.0, 1.0));
		pf = multiply (pf, psm.newCoefficients (-3.0, 2.0));

		PolynomialFunctionCharacteristics<Double> fc =
			new PolynomialFunctionCharacteristics<Double> (mgr, lib);
		Map<Double,CharacteristicAttributes<Double>> evaluation = fc.evaluate (pf);
		Double[] xs = evaluation.keySet ().toArray (new Double[1]);
		Arrays.sort (xs);
		
		for (double x : xs)
		{
			CharacteristicAttributes<Double> charX = evaluation.get (x);
			System.out.println (x + " " + charX.getCharacteristicType() + " " + charX.getFOfX());
		}
	}

	static PowerFunction<Double> multiply (PowerFunction<Double> p, Polynomial.Coefficients<Double> c)
	{ return psm.multiply (p, psm.getPolynomialFunction (c)); }

}
