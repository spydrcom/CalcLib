
package net.myorb.testing;

import net.myorb.math.complexnumbers.*;
import net.myorb.math.expressions.charting.*;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.*;

/**
 * use polar coordinate translation to slice planes of 2D to 2D functions
 * @author Michael Druckman
 */
public class TransformPolarTesting
{


	static double pi = 3.1415926;
	static double delta = pi / 16;


	static final boolean USE_INVERTED = true;


	/**
	 * plot r CIS theta with r as inner domain and theta outer
	 */
	public static void nonInverted ()
	{
		ComplexFunction function = new ComplexFunction ();
		Transform2D.Domain   rDomain   = Transform2D.getDomain (-2, 2, 0.01);
		Transform2D.Domain thetaDomain = Transform2D.getDomain (delta, pi - delta, delta);
		Transform2D.Domain colorDomain = Transform2D.getDomain (600600600, 950950950, 20020020);
		TransformPolar.plot (function.getName (), function, rDomain, thetaDomain, colorDomain);
	}


	/**
	 * plot r CIS theta with theta as inner domain and r outer
	 */
	public static void inverted ()
	{
		ComplexFunction function = new ComplexFunction ();
		Transform2D.Domain   rDomain   = Transform2D.getDomain (-2, 2, 0.05);
		Transform2D.Domain thetaDomain = Transform2D.getDomain (0, pi, 0.01);
		Transform2D.Domain colorDomain = Transform2D.getDomain (700700700, 900900900, 10010010);
		TransformPolar.plotInverted (function.getName (), function, rDomain, thetaDomain, colorDomain);
	}

	/**
	 * call the plot layer with domains set
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		if (USE_INVERTED) inverted (); else nonInverted ();
	}

}

/**
 * translate coordinates used by function and chart layers
 */
class ComplexFunction  implements TransformPolar.ComplexTransform
{

	/**
	 * construct the transform function
	 */
	@SuppressWarnings("unchecked") ComplexFunction ()
	{
		transform = poly.functionOfX (NEG_ONE, complexManager.newScalar(5), complexManager.newScalar(8), NEG_ONE, NEG_ONE);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.TransformPolar.ComplexTransform#translate(net.myorb.math.complexnumbers.ComplexValue)
	 */
	public ComplexValue<Double> translate (ComplexValue<Double> from)
	{
		return transform.eval (from);
	}
	public String getName () { return psm.toString (transform); }

	static DoubleFloatingFieldManager manager = new DoubleFloatingFieldManager ();
	static ComplexFieldManager<Double> complexManager = new ComplexFieldManager<Double> (manager);
	static Polynomial<ComplexValue<Double>> poly = new Polynomial<ComplexValue<Double>> (complexManager);
	static PolynomialSpaceManager<ComplexValue<Double>> psm = new PolynomialSpaceManager<ComplexValue<Double>> (complexManager);
	static ComplexValue<Double> ONE = complexManager.getOne (), NEG_ONE = complexManager.negate (ONE);
	static Polynomial.PowerFunction<ComplexValue<Double>> transform;

}

