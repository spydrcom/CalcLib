
package net.myorb.math.realnumbers;

import net.myorb.math.SpaceManager;
import net.myorb.math.FieldStructure;
import net.myorb.math.FieldStructureForSpace;

import net.myorb.data.abstractions.ValueDisplayProperties;

/**
 * 
 * provide a type manager for double precision floating point real values
 * 
 * @author Michael Druckman
 * 
 */
public class DoubleFloatingFieldManager implements SpaceManager<Double>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getName()
	 */
	public String getName () { return "Real Numbers"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getDataType()
	 */
	public DataType getDataType () { return DataType.Real; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getComponentManager()
	 */
	public SpaceManager<Double> getComponentManager () { return this; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isZero(java.lang.Object)
	 */
	public boolean isZero (Double x) { return x == null || x == 0; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isNegative(java.lang.Object)
	 */
	public boolean isNegative (Double x) { return x < 0; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#newScalar(int)
	 */
	public Double newScalar (int x) { return (double)x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#lessThan(java.lang.Object, java.lang.Object)
	 */
	public boolean lessThan (Double x, Double y) { return x < y; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#add(java.lang.Object, java.lang.Object)
	 */
	public Double add (Double x, Double y) { return (double)x + (double)y; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#multiply(java.lang.Object, java.lang.Object)
	 */
	public Double multiply (Double x, Double y) { return (double)x * (double)y; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#invert(java.lang.Object)
	 */
	public Double invert (Double x)
	{
		if (x == 0)
		{ throw new RuntimeException ("Division by zero"); }
		return 1.0 / (double)x;
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#negate(java.lang.Object)
	 */
	public Double negate (Double x) { return -(double)x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#conjugate(java.lang.Object)
	 */
	public Double conjugate (Double x) { return x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toInternalString(java.lang.Object)
	 */
	public String toInternalString (Double x) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getPi()
	 */
	public Double getPi () { return 3.14159265358979323; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getZero()
	 */
	public Double getZero () { return 0.0; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getOne()
	 */
	public Double getOne () { return 1.0; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getFieldStructure()
	 */
	public FieldStructure<Double> getFieldStructure ()
	{ return new FieldStructureForSpace<Double> (this); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toNumber(java.lang.Object)
	 */
	public Number toNumber (Double x) { return x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toDecimalString(java.lang.Object)
	 */
	public String toDecimalString (Double x)
	{
		return ValueDisplayProperties.formatDecimalString (x, displayPrecision);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#setDisplayPrecision(int)
	 */
	public void setDisplayPrecision (int digits)
	{
		displayPrecision = digits;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getEmptyArray()
	 */
	public Double[] getEmptyArray () { return EMPTY; }
	protected Double[] EMPTY = new Double[]{};

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#resetDisplayPrecision()
	 */
	public void resetDisplayPrecision () { displayPrecision = 0; }
	protected int displayPrecision = 0;

}
