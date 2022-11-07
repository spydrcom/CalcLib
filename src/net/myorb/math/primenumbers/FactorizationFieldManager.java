
package net.myorb.math.primenumbers;

import net.myorb.data.abstractions.ValueDisplayProperties;
import net.myorb.math.FieldStructureForSpace;
import net.myorb.math.FieldStructure;
import net.myorb.math.SpaceManager;

import java.math.BigInteger;

/**
 * 
 * provide a type manager for integer values represented as prime number factorizations
 * 
 * @author Michael Druckman
 *
 */
public class FactorizationFieldManager implements SpaceManager<Factorization>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getName()
	 */
	public String getName () { return "Prime Factorization"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getDataType()
	 */
	public DataType getDataType () { return DataType.Factorization; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getComponentManager()
	 */
	public SpaceManager<Factorization> getComponentManager () { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isNegative(java.lang.Object)
	 */
	public boolean isNegative (Factorization x) { return isZero (x)? false: x.isNegative (); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#newScalar(int)
	 */
	public Factorization newScalar (int x) { return FactorizationManager.forValue (x); }
	public Factorization bigScalar (BigInteger x) { return FactorizationManager.forValue (x); }
	public Factorization longScalar (long x) { return FactorizationManager.forValue (x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#add(java.lang.Object, java.lang.Object)
	 */
	public Factorization add (Factorization x, Factorization y) {return isZero (x)? y: isZero (y)? x: x.add (y); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#multiply(java.lang.Object, java.lang.Object)
	 */
	public Factorization multiply (Factorization x, Factorization y) { return isZero (x)? getZero (): x.multiplyBy (y); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#invert(java.lang.Object)
	 */
	public Factorization invert (Factorization x) { return FactorizationManager.onePrime ().divideBy (x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#conjugate(java.lang.Object)
	 */
	public Factorization conjugate (Factorization x) { return x; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#negate(java.lang.Object)
	 */
	public Factorization negate (Factorization x) { return isZero (x)? getZero (): x.negate (); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#lessThan(java.lang.Object, java.lang.Object)
	 */
	public boolean lessThan (Factorization x, Factorization y)
	{
		if (isZero (x))
		{
			if (isZero (y)) return false;
			else return !isNegative (y);
		}
		else if (isZero (y))
		{
			return isNegative (x);
		}
		else if (isNegative (x))
		{
			if (!isNegative (y)) return true;
		}
		else if (isNegative (y))
		{
			if (!isNegative (x)) return false;
		}
		return Distribution.lessThan (x, y);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isZero(java.lang.Object)
	 */
	public boolean isZero (Factorization x) { return x == null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#setDisplayPrecision(int)
	 */
	public void setDisplayPrecision (int digits)
	{
		this.overridePrecision = digits;
		this.override = true;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#resetDisplayPrecision()
	 */
	public void resetDisplayPrecision () { this.override = false; }

	/**
	 * @return default or overridden precision value
	 */
	public int getSelectedPrecision ()
	{
		if (override) return overridePrecision;
		else return ValueDisplayProperties.DEFAULT_DISPLAY_PRECISION;
	}
	private int overridePrecision = 0;
	private boolean override = false;

	/**
	 * evaluate scaling properties of value
	 * @param x the value to be evaluated for scaling properties
	 * @return a scaling object for the value
	 */
	public DistributionScaling scalingOf (Factorization x)
	{
		return DistributionScaling.newInstance (x, getSelectedPrecision (), this);
	}

	/**
	 * display series of factors
	 * @param x value being formatted
	 * @return formatted value as prime factors
	 */
	public String toPrimeFactors (Factorization x)
	{
		return isZero (x)? "0": scalingOf (x).toInternalString ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toNumber(java.lang.Object)
	 */
	public Number toNumber (Factorization x) { return scalingOf (x).toNumber (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toDecimalString(java.lang.Object)
	 */
	public String toDecimalString (Factorization x) { return toNumber (x).toString (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getPi()
	 */
	public Factorization getPi ()
	{
		return newScalar (355).divideBy (newScalar (113)); // approximation of PI to 6 digits
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getZero()
	 */
	public Factorization getZero () { return newScalar (0); }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getOne()
	 */
	public Factorization getOne () { return newScalar (1); }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getFieldStructure()
	 */
	public FieldStructure<Factorization> getFieldStructure ()
	{ return new FieldStructureForSpace<Factorization> (this); }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getEmptyArray()
	 */
	public Factorization[] getEmptyArray ()
	{
		return new Factorization[]{};
	}

}
