
package net.myorb.math.expressions.charting;

// complex numbers
import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.CoordinateSystems;

// charting transforms
import net.myorb.math.expressions.charting.Transform2D;
import net.myorb.math.expressions.charting.Transform2D.Domain;

// real numbers
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

// charting types
import net.myorb.charting.DisplayGraphTypes.Point;

/**
 * provide polar coordinate transformation for complex mapping
 * @author Michael Druckman
 */
public class TransformPolar
{


	/**
	 * describe a function with complex domain and range
	 */
	public interface ComplexTransform
	{
		/**
		 * method of function call
		 * @param from complex parameter to function
		 * @return complex result
		 */
		ComplexValue<Double> translate (ComplexValue<Double> from);
	}


	/**
	 * plot a complex function
	 *  using polar domain coordinates.
	 *  r is plotted as the inner domain
	 * @param title the title for the chart frame
	 * @param transform the complex function to be mapped
	 * @param rDomain the domain of the 'r' (origin distance) parameter
	 * @param thetaDomain the domain of the 'theta' parameter
	 * @param colorDomain the domain of color values
	 */
	public static void plot
	(String title, ComplexTransform transform, Domain rDomain, Domain thetaDomain, Domain colorDomain)
	{
		Transform2D.plot (title, new PolarTransform (transform, false), rDomain, thetaDomain, colorDomain);
	}


	/**
	 * plot a complex function
	 *  using polar domain coordinates.
	 *  theta  is plotted as the inner domain
	 * @param title the title for the chart frame
	 * @param transform the complex function to be mapped
	 * @param rDomain the domain of the 'r' (origin distance) parameter
	 * @param thetaDomain the domain of the 'theta' parameter
	 * @param colorDomain the domain of color values
	 */
	public static void plotInverted
	(String title, ComplexTransform transform, Domain rDomain, Domain thetaDomain, Domain colorDomain)
	{
		Transform2D.plot (title, new PolarTransform (transform, true), thetaDomain, rDomain, colorDomain);
	}


	static DoubleFloatingFieldManager manager = new DoubleFloatingFieldManager ();
	public static ComplexValue<Double> toComplex (Point p) { return new ComplexValue<Double> (p.x, p.y, manager); }
	public static Point fromComplex (ComplexValue<Double> z) { return new Point (z.Re (), z.Im ()); }


}


/**
 * call the transform function with translation from polar coordinates
 */
class PolarTransform implements Transform2D.Transform
{

	CoordinateSystems<Double> translator =
		new CoordinateSystems<Double> (TransformPolar.manager);
	TransformPolar.ComplexTransform transform;
	boolean inverted;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Transform2D.Transform#translate(net.myorb.math.expressions.charting.DisplayGraph.Point)
	 */
	public Point translate (Point from)
	{
		double r = inverted? from.y: from.x, theta = inverted? from.x: from.y;
		ComplexValue<Double> z = translator.newPolarInstance (r, theta).toComplexValue ();
		return TransformPolar.fromComplex (transform.translate (z));
	}

	/**
	 * save the complex function
	 * @param transform the complex function based on rectangular  coordinates
	 * @param inverted TRUE => theta treated as inner domain
	 */
	public PolarTransform
	(TransformPolar.ComplexTransform transform, boolean inverted)
	{ this.transform = transform; this.inverted = inverted; }

}

