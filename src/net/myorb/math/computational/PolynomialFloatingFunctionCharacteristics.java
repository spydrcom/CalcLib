
package net.myorb.math.computational;

import net.myorb.math.ExponentiationLib;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * a convenient static version of the function characterizer for floating functions
 * @author Michael Druckman
 */
public class PolynomialFloatingFunctionCharacteristics extends PolynomialFunctionCharacteristics<Double>
{

	static DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();

	/**
	 * for internal use, external access uses static entry point
	 */
	protected PolynomialFloatingFunctionCharacteristics ()
	{
		super (mgr, new ExponentiationLib<Double> (mgr));
	}

	/**
	 * produce a list of the characteristic points of this function space
	 * @param polynomial the power function to be characterized
	 * @return a list of characterized elements
	 */
	@SuppressWarnings("rawtypes")
	public static List<CharacteristicAttributes>
		characterize (PowerFunction<Double> polynomial)
	{
		Map<Double,CharacteristicAttributes<Double>> evaluation =
			new PolynomialFloatingFunctionCharacteristics ().evaluate (polynomial);
		Double[] xs = evaluation.keySet ().toArray (new Double[1]);
		Arrays.sort (xs);

		List<CharacteristicAttributes>
			characteristics = new ArrayList<CharacteristicAttributes>();
		for (double x : xs) characteristics.add (evaluation.get (x));
		return characteristics;
	}

	/**
	 * create a text representation of a characterization
	 * @param c a Characteristics object describing an element
	 * @return a text string describing the element
	 */
	public static String toString (CharacteristicAttributes<Double> c)
	{
		return c.getX () + " " + c.getCharacteristicType () + " " + c.getFOfX ();
	}

	/**
	 * construct an XML document describing this function
	 * @param attributes the attributes collected for this function
	 * @return the text of the XML document generated
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String toXml (List<CharacteristicAttributes> attributes)
	{
		StringBuffer buffer = new StringBuffer ();
		buffer.append ("<FunctionCharacteristics>\r");
		String function = new PolynomialSpaceManager<Double> (mgr).toString (attributes.get (0).getFunction ());
		buffer.append ("   <FunctionEquation>").append (function).append ("</FunctionEquation>\r");

		for (CharacteristicAttributes<Double> c : attributes)
		{
			String type = c.getCharacteristicType ().toString (), x = c.getX ().toString ();
			String fOfX = c.getFOfX ().toString (), fPrime = c.getFPrimeOfX ().toString ();

			buffer.append ("   <Descriptor>\r");
			buffer.append ("      <X>").append (x).append ("</X>\r");
			buffer.append ("      <FofX>").append (fOfX).append ("</FofX>\r");
			buffer.append ("      <DescriptionType>").append (type).append ("</DescriptionType>\r");
			buffer.append ("      <FPrimeOfX>").append (fPrime).append ("</FPrimeOfX>\r");
			buffer.append ("   </Descriptor>\r");
		}
		buffer.append ("</FunctionCharacteristics>\r");
		return buffer.toString ();
	}

}
