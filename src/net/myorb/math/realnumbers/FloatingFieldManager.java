
package net.myorb.math.realnumbers;

import net.myorb.math.FieldStructure;
import net.myorb.math.FieldStructureForSpace;
import net.myorb.math.SpaceManager;

/**
 * 
 * provide a type manager for floating point real values
 * 
 * @author Michael Druckman
 * 
 */
public class FloatingFieldManager implements SpaceManager<Float>
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
	public SpaceManager<Float> getComponentManager () { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isZero(java.lang.Object)
	 */
	public boolean isZero (Float x) { return x == null || x == 0; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isNegative(java.lang.Object)
	 */
	public boolean isNegative (Float x) { return x < 0; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#newScalar(int)
	 */
	public Float newScalar (int x) { return (float)x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#lessThan(java.lang.Object, java.lang.Object)
	 */
	public boolean lessThan (Float x, Float y) { return x < y; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#add(java.lang.Object, java.lang.Object)
	 */
	public Float add (Float x, Float y) { return (float)x + (float)y; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#multiply(java.lang.Object, java.lang.Object)
	 */
	public Float multiply (Float x, Float y) { return (float)x * (float)y; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceDescription#pow(java.lang.Object, int)
	 */
	public Float pow (Float x, int exponent) { return (float) Math.pow (x, exponent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#invert(java.lang.Object)
	 */
	public Float invert (Float x) { return 1.0f / (float)x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#conjugate(java.lang.Object)
	 */
	public Float conjugate (Float x) { return x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#negate(java.lang.Object)
	 */
	public Float negate (Float x) { return -(float)x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toDecimalString(java.lang.Object)
	 */
	public String toDecimalString (Float x)
	{
		int intVal = x.intValue ();
		if (x == intVal) return Integer.toString (intVal);
		if (displayPrecision != null) return String.format (displayPrecision, x);
		else return Float.toString (x);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#setDisplayPrecision(int)
	 */
	public void setDisplayPrecision (int digits)
	{
		displayPrecision = "%." + digits + "f";
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#resetDisplayPrecision()
	 */
	public void resetDisplayPrecision () { displayPrecision = null; }
	protected String displayPrecision = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toInternalString(java.lang.Object)
	 */
	public String toInternalString (Float x) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getPi()
	 */
	public Float getPi () { return 3.14159265358979323f; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getZero()
	 */
	public Float getZero () { return 0.0f; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getOne()
	 */
	public Float getOne () { return 1.0f; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getFieldStructure()
	 */
	public FieldStructure<Float> getFieldStructure ()
	{ return new FieldStructureForSpace<Float> (this); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toNumber(java.lang.Object)
	 */
	public Number toNumber (Float x)
	{
		return x;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getEmptyArray()
	 */
	public Float[] getEmptyArray ()
	{
		return new Float[]{};
	}

}
