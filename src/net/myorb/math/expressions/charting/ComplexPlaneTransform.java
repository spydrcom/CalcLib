
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.complexnumbers.ComplexFieldManager;
import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.Polynomial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * implementation of polar plot functionality
 * @author Michael Druckman
 */
public class ComplexPlaneTransform extends TransformPolar
{


	static ComplexFieldManager<Double> cfm =
		new ComplexFieldManager<Double> (new DoubleFloatingFieldManager ());
	static Polynomial<ComplexValue<Double>> poly = new Polynomial<ComplexValue<Double>>(cfm);
	static PolynomialSpaceManager<ComplexValue<Double>> psm = new PolynomialSpaceManager<ComplexValue<Double>> (cfm);
	static ValueManager<ComplexValue<Double>> manager = new ValueManager<ComplexValue<Double>> ();


	/**
	 * convert 3 element array into domain description
	 * @param v the array value to treat as domain descriptor
	 * @return a domain object for Transform2D
	 */
	public static Transform2D.Domain getDomain (ValueManager.GenericValue v)
	{
		List<ComplexValue<Double>> list = manager.toArray (v);
		return Transform2D.getDomain (list.get (0).Re (), list.get (1).Re (), list.get (2).Re ());
	}
	public static void copyTo (Map<String,Object> parameters, Transform2D.Domain from, String named)
	{
		parameters.put (named + "DomainLo", from.getLo ());
		parameters.put (named + "DomainHi", from.getHi ());
		parameters.put (named + "DomainInc", from.getIncrement ());
	}
	public static Transform2D.Domain getDomain (Map<String,Object> parameters, String name)
	{
		return Transform2D.getDomain
			(
				Double.parseDouble (parameters.get (name + "DomainLo").toString ()),
				Double.parseDouble (parameters.get (name + "DomainHi").toString ()),
				Double.parseDouble (parameters.get (name + "DomainInc").toString ())
			);
	}


	/**
	 * collect parameter data and build plot
	 * @param parameters a map of the parameters to the plot
	 * @param symbols a symbol table object to find the parameters
	 */
	public static void constructPlot (Map<String,Object> parameters, SymbolMap symbols)
	{
		ValueManager.GenericValue outer = symbols.getValue (parameters.get ("outerDomainName").toString ());
		ValueManager.GenericValue inner = symbols.getValue (parameters.get ("innerDomainName").toString ());
		ValueManager.GenericValue color = symbols.getValue (parameters.get ("colorDomainName").toString ());

		Transform2D.Domain od = getDomain (outer), id = getDomain (inner), cd = getDomain (color);
		ValueManager.GenericValue f = symbols.getValue (parameters.get ("functionName").toString ());

		List<ComplexValue<Double>> polyCo = manager.toArray (f); int polyDeg = polyCo.size ();
		for (int i = 0; i < polyDeg; i++) parameters.put (POLYCO+i, polyCo.get (i));
		parameters.put (POLYDEG, polyDeg);

		TransformPolar.ComplexTransform transform = new ComplexTransformInstance (polyCo);
		copyTo (parameters, od, "outer"); copyTo (parameters, id, "inner"); copyTo (parameters, cd, "color");
		String equation = transform.toString (); parameters.put ("Equation", equation); parameters.put ("Title", equation);
		constructPlot (parameters, transform, id, od, cd);
	}


	/**
	 * @param parameters map of all required parameters
	 */
	public static void constructPlot (Map<String,Object> parameters)
	{
		Transform2D.Domain od = getDomain (parameters, "outer"),
			id = getDomain (parameters, "inner"), cd = getDomain (parameters, "color");
		TransformPolar.ComplexTransform transform = getTransform (parameters);
		constructPlot (parameters, transform, id, od, cd);
	}
	static TransformPolar.ComplexTransform getTransform (Map<String,Object> parameters)
	{
		int polyDegree = Integer.parseInt (parameters.get (POLYDEG).toString ());
		List<ComplexValue<Double>> coef = new ArrayList<ComplexValue<Double>> ();
		for (int i = 0; i < polyDegree; i++) coef.add (cfm.getZero ());

		for (String item : parameters.keySet ())
		{
			if (item.startsWith (POLYCO))
			{
				int n = Integer.parseInt (item.substring (POLYCO.length ()));
				coef.set (n, cfm.newScalar (Integer.parseInt (parameters.get (item).toString ())));
			}
		}

		return new ComplexTransformInstance (coef);
	}
	static final String POLYDEG = "POLYDEG", POLYCO = "POLYCO";


	/**
	 * @param parameters map to inner variable
	 * @param transform the transform equation
	 * @param inner the inner domain descriptor
	 * @param outer the outer domain descriptor
	 * @param color the color domain descriptor
	 */
	public static void constructPlot
		(
			Map<String,Object> parameters, TransformPolar.ComplexTransform transform,
			Transform2D.Domain inner, Transform2D.Domain outer, Transform2D.Domain color
		)
	{
		String title = parameters.get ("Title").toString ();
		if (parameters.get ("InnerVariable").equals ("THETA"))
			TransformPolar.plotInverted (title, transform, outer, inner, color);
		else TransformPolar.plot (title, transform, inner, outer, color);
	}


}


/**
 * wrapper for Polynomial.PowerFunction
 *  exporting functionality as TransformPolar.ComplexTransform
 */
class ComplexTransformInstance implements TransformPolar.ComplexTransform
{

	ComplexTransformInstance (List<ComplexValue<Double>> coef)
	{
		Polynomial.Coefficients<ComplexValue<Double>> pc =
			new Polynomial.Coefficients<ComplexValue<Double>>();
		for (ComplexValue<Double> z : coef) { pc.add (z); }

		transform = ComplexPlaneTransform.poly.getPolynomialFunction (pc);
	}
	Polynomial.PowerFunction<ComplexValue<Double>> transform;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.TransformPolar.ComplexTransform#translate(net.myorb.math.complexnumbers.ComplexValue)
	 */
	public ComplexValue<Double> translate(ComplexValue<Double> from)
	{
		return transform.eval (from);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return ComplexPlaneTransform.psm.toString (transform); }
	
}

