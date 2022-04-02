
package net.myorb.math.computational.splines;

import java.util.List;

/**
 * access spline segment defining properties
 * @author Michael Druckman
 */
public class SegmentParameters implements SegmentRepresentation
{


	public SegmentParameters () {}

	/**
	 * @param source a SegmentRepresentation containing data
	 */
	public SegmentParameters (SegmentRepresentation source)
	{
		copyFrom (source);
	}


	/**
	 * @param source a SegmentRepresentation containing data
	 */
	public void copyFrom (SegmentRepresentation source)
	{
		this.lo = source.getSegmentLo ();
		this.hi = source.getSegmentHi ();

		this.unit = source.getUnitSlope ();
		this.delta = source.getSegmentDelta ();
		this.error = source.getSegmentError ();
		this.slope = source.getSegmentSlope ();

		this.componentCoefficients = source.getCoefficients ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#setCoefficients(java.util.List)
	 */
	public void setCoefficients
		(List < List <Double> > componentCoefficients)
	{ this.componentCoefficients = componentCoefficients; }
	protected List < List <Double> > componentCoefficients;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getCoefficients()
	 */
	public List < List <Double> > getCoefficients () { return componentCoefficients; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getComponentCount()
	 */
	public int getComponentCount () { return componentCoefficients.size (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getCoefficientsFor(int)
	 */
	public List<Double> getCoefficientsFor (int component)
	{ return componentCoefficients.get (component); }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentLo()
	 */
	public double getSegmentLo () { return lo; }
	public double getUnitSlope () { return unit; }
	public double getSegmentHi () { return hi; }
	protected double lo, hi, unit;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SegmentRepresentation#getSegmentDelta()
	 */
	public double getSegmentDelta () { return delta; }
	public double getSegmentError () { return error; }
	public double getSegmentSlope () { return slope; }
	protected double delta, error, slope;

}
