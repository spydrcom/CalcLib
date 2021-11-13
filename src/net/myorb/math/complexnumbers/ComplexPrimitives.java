
package net.myorb.math.complexnumbers;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.myorb.math.Polynomial;
import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

/**
 * a set of primitive methods to use working with complex values
 * @author Michael Druckman
 */
public class ComplexPrimitives
{


	static final double INSIGNIFICANT_THRESHOLD = 1.0E-14;


	static final DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	static final ComplexFieldManager<Double> cfm = new ComplexFieldManager<Double> (mgr);

	static final Polynomial<ComplexValue<Double>> polyMgr = new Polynomial<ComplexValue<Double>> (cfm);


	/**
	 * determine if value is complex or real
	 * @param z complex value to check
	 * @return TRUE = complex
	 */
	public static boolean isImaginary (ComplexValue<Double> z)
	{
		if (!significant (z.Re ()))
		{
			if (!significant (z.Im ())) return false;
		}
		else
		{
			if (!significant (z.Im () / z.Re ())) return false;
		}
		return true;
	}


	/**
	 * convert from real to complex coefficients
	 * @param source the power function having real coefficients
	 * @return the complex version of the power function
	 */
	public static PowerFunction<ComplexValue<Double>> convertToComplex (PowerFunction<Double> source)
	{
		Polynomial.Coefficients<ComplexValue<Double>> complexCoefficients = new Polynomial.Coefficients<ComplexValue<Double>>();
		for (Double realCoefficient : source.getCoefficients ()) complexCoefficients.add (new ComplexValue<Double> (realCoefficient, mgr));
		return polyMgr.getPolynomialFunction (complexCoefficients);
	}


	/**
	 * convert from complex to real coefficients
	 * @param source the power function having complex coefficients
	 * @return the real version of the power function
	 */
	public static PowerFunction<Double> convertToReal (PowerFunction<ComplexValue<Double>> source)
	{
		Polynomial.Coefficients<Double> realCoefficients = new Polynomial.Coefficients<Double>();
		for (ComplexValue<Double> complexCoefficient : source.getCoefficients ()) realCoefficients.add (complexCoefficient.Re ());
		return new Polynomial<Double> (mgr).getPolynomialFunction (realCoefficients);
	}


	/**
	 * sort a set of complex values
	 * @param values the values to be sorted
	 * @return a sorted list
	 */
	public static List<ComplexValue<Double>> sort (List<ComplexValue<Double>> values)
	{
		HashMap<Double, ComplexValue<Double>> map = new HashMap<Double, ComplexValue<Double>>();
		List<ComplexValue<Double>> sorted = new ArrayList<ComplexValue<Double>>();
		for (ComplexValue<Double> value : values) map.put (value.Re(), value);
		Double[] keys = map.keySet ().toArray (new Double[1]);

		Arrays.sort (keys);
		for (Double key : keys) { sorted.add (map.get (key)); }
		return sorted;
	}


	/**
	 * reduce displayed items to only significant values
	 * @param z the complex object to be displayed
	 * @return the reduced value as text
	 */
	public static String toDisplay (ComplexValue<Double> z)
	{
		if (!significant (z.modSquared ()))
			return "0";
		if (isImaginary (z))
			return z.toString ();
		else
		{
			Double value = z.Re ();
			int intValue = value.intValue ();

			if (value == intValue)
				return Integer.toString (intValue);
			return value.toString ();
		}
	}


	/**
	 * determine if a value is significant
	 * @param value the value to be analyzed
	 * @return TRUE = significant
	 */
	public static boolean significant (Double value)
	{
		return abs (value) > INSIGNIFICANT_THRESHOLD;
	}


	/**
	 * standard absolute value function
	 * @param x the value to use for evaluation
	 * @return x LT 0? -x: x
	 */
	public static Double abs (Double x) { return x < 0? -x: x; }


}
