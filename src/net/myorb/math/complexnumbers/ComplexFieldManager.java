
package net.myorb.math.complexnumbers;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.computational.GenericArithmetic;

import net.myorb.math.FieldStructureForSpace;
import net.myorb.math.FieldStructure;

import net.myorb.math.SpaceManager;

/**
 * type manager for complex numbers
 * @param <T> the real and imaginary component types
 * @author Michael Druckman
 */
public class ComplexFieldManager<T> implements SpaceManager <ComplexValue<T>>
{

	/**
	 * build a management object based on type manager for real/imag fields
	 * @param manager the manager for the type being manipulated
	 */
	public ComplexFieldManager
		(SpaceManager<T> manager)
	{
		this.manager = manager;
	}
	protected SpaceManager<T> manager;


	/**
	 * build a basic complex manager based on double floating real components
	 * @return a complex field manager based on Double components
	 */
	public static ComplexFieldManager<Double> newInstance ()
	{ return new ComplexFieldManager<Double> (new DoubleFloatingFieldManager ()); }


	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getDataType()
	 */
	public DataType getDataType () { return DataType.Complex; }


	/**
	 * construct a complex value
	 * @param r the real part of the value
	 * @param i the imaginary part of the value
	 * @return the new complex object
	 */
	public ComplexValue<T> C (T r, T i)
	{
		return new ComplexValue<T> (r, i, manager);
	}

	/**
	 * construct a complex value
	 * @param i multiple of i in the value
	 * @return the new complex object
	 */
	public ComplexValue<T> I (T i)
	{ return new ComplexValue<T> (manager.getZero(), i, manager); }
	public ComplexValue<T> I (int i) { return I (manager.newScalar(i)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getName()
	 */
	public String getName () { return "Complex Numbers"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getComponentManager()
	 */
	public SpaceManager<T> getComponentManager () { return manager; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isZero(java.lang.Object)
	 */
	public boolean isZero (ComplexValue<T> x)
	{ return x.isZero (); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isNegative(java.lang.Object)
	 */
	public boolean isNegative (ComplexValue<T> x)
	{
//		if (manager.isZero (x.realpart))
//			return manager.isNegative (x.imagpart);
//		if (manager.isZero (x.imagpart))
//			return manager.isNegative (x.realpart);
		return manager.isNegative (x.realpart);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#newScalar(int)
	 */
	public ComplexValue<T> newScalar (int x)
	{
		return new ComplexValue<T>
		(
			x, manager
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#invert(java.lang.Object)
	 */
	public ComplexValue<T> invert (ComplexValue<T> x)  { return x.inverted(); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#negate(java.lang.Object)
	 */
	public ComplexValue<T> negate (ComplexValue<T> x) 
	{
		return x.negate ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#conjugate(java.lang.Object)
	 */
	public ComplexValue<T> conjugate (ComplexValue<T> x) 
	{
		return x.conjugate ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#add(java.lang.Object, java.lang.Object)
	 */
	public ComplexValue<T> add (ComplexValue<T> x, ComplexValue<T> y)
	{
		return x.plus (y);
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#multiply(java.lang.Object, java.lang.Object)
	 */
	public ComplexValue<T> multiply (ComplexValue<T> x, ComplexValue<T> y)
	{
		return x.times (y);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceDescription#pow(java.lang.Object, int)
	 */
	public ComplexValue<T> pow (ComplexValue<T> x, int exponent)
	{
		return GenericArithmetic.pow (x, exponent, this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#lessThan(java.lang.Object, java.lang.Object)
	 */
	public boolean lessThan (ComplexValue<T> x, ComplexValue<T> y)
	{
		if (manager.isZero (x.imagpart) && manager.isZero (y.imagpart))
			return manager.lessThan (x.realpart, y.realpart);
		return manager.lessThan (x.modSquared (), y.modSquared ());
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toDecimalString(java.lang.Object)
	 */
	public String toDecimalString (ComplexValue<T> x)
	{
		if (displayPrecision == null) return x.toString ();
		else return x.toPrecisionString (displayDigits);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#setDisplayPrecision(int)
	 */
	public void setDisplayPrecision (int digits)
	{
		displayPrecision = "%." + digits + "f";
		displayDigits = digits;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#resetDisplayPrecision()
	 */
	public void resetDisplayPrecision () { displayPrecision = null; }
	protected String displayPrecision = null;
	protected int displayDigits = 0;

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toInternalString(java.lang.Object)
	 */
	public String toInternalString (ComplexValue<T> x) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getPi()
	 */
	public ComplexValue<T> getPi ()
	{
		return new ComplexValue<T> (manager.getPi(), manager.newScalar (0), manager);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getZero()
	 */
	public ComplexValue<T> getZero ()
	{
		return new ComplexValue<T> (manager.newScalar (0), manager.newScalar (0), manager);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getOne()
	 */
	public ComplexValue<T> getOne () { return newScalar (1); }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getFieldStructure()
	 */
	public FieldStructure<ComplexValue<T>> getFieldStructure ()
	{ return new FieldStructureForSpace<ComplexValue<T>> (this); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toNumber(java.lang.Object)
	 */
	public Number toNumber (ComplexValue<T> x)
	{
		throw new RuntimeException ("Complex data not representable as Number");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getEmptyArray()
	 */
	@SuppressWarnings("unchecked")
	public ComplexValue<T>[] getEmptyArray ()
	{
		return new ComplexValue[]{};
	}

}
