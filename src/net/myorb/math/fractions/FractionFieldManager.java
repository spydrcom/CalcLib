
package net.myorb.math.fractions;

import net.myorb.math.FieldStructure;
import net.myorb.math.FieldStructureForSpace;

import net.myorb.math.computational.GenericArithmetic;
import net.myorb.math.SpaceManager;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;

/**
 * 
 * provide a type manager for fractions which use a generic type for numerator and denominator
 * 
 * @param <T> type of values to be used as numerator and denominator
 * 
 * @author Michael Druckman
 *
 */
public class FractionFieldManager<T> implements SpaceManager <Fraction<T>> 
{

	/**
	 * build a management object based on type manager for numerator and denominator
	 * @param manager the manager for the type being manipulated
	 */
	public FractionFieldManager
		(SpaceManager<T> manager)
	{
		this.manager = manager;
	}
	protected SpaceManager<T> manager;

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getDataType()
	 */
	public DataType getDataType () { return DataType.Fraction; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getName()
	 */
	public String getName () { return "Integer Fractions"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getComponentManager()
	 */
	public SpaceManager<T> getComponentManager () { return manager; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isZero(java.lang.Object)
	 */
	public boolean isZero (Fraction<T> x)
	{ return manager.isZero (x.numerator); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isNegative(java.lang.Object)
	 */
	public boolean isNegative (Fraction<T> x)
	{ return manager.isNegative (x.numerator); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#newScalar(int)
	 */
	public Fraction<T> newScalar (int x)
	{
		return new Fraction<T>
		(
			x, manager
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#invert(java.lang.Object)
	 */
	public Fraction<T> invert (Fraction<T> x)  { return x.inverted (); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#conjugate(java.lang.Object)
	 */
	public Fraction<T> conjugate (Fraction<T> x) { return x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#negate(java.lang.Object)
	 */
	public Fraction<T> negate (Fraction<T> x) 
	{
		T numerator = manager.negate (x.numerator);
		return new Fraction<T> (numerator, x.denominator, manager);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#add(java.lang.Object, java.lang.Object)
	 */
	public Fraction<T> add (Fraction<T> x, Fraction<T> y)
	{
		return x.addWith (y);
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#multiply(java.lang.Object, java.lang.Object)
	 */
	public Fraction<T> multiply (Fraction<T> x, Fraction<T> y)
	{
		return x.multiplyBy (y);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceDescription#pow(java.lang.Object, int)
	 */
	public Fraction<T> pow (Fraction<T> x, int exponent)
	{
		return GenericArithmetic.pow (x, exponent, this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#lessThan(java.lang.Object, java.lang.Object)
	 */
	public boolean lessThan (Fraction<T> x, Fraction<T> y)
	{
		T x1 = manager.multiply (x.getNumerator(), y.getDenominator());
		T y1 = manager.multiply (y.getNumerator(), x.getDenominator());
		return manager.lessThan (x1, y1);
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toDecimalString(java.lang.Object)
	 */
	public String toDecimalString (Fraction<T> x)
	{
		return toNumber (x).toString ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#setDisplayPrecision(int)
	 */
	public void setDisplayPrecision (int digits) {}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#resetDisplayPrecision()
	 */
	public void resetDisplayPrecision () {}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toInternalString(java.lang.Object)
	 */
	public String toInternalString (Fraction<T> x)
	{
		return x.numerator.toString () + " / " + x.denominator.toString ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getPi()
	 */
	public Fraction<T> getPi ()
	{
		return newScalar (355).divideBy (newScalar (113)); // approximation of PI to 6 digits
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getZero()
	 */
	public Fraction<T> getZero () { return newScalar (0); }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getOne()
	 */
	public Fraction<T> getOne () { return newScalar (1); }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getFieldStructure()
	 */
	public FieldStructure<Fraction<T>> getFieldStructure ()
	{ return new FieldStructureForSpace<Fraction<T>> (this); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toNumber(java.lang.Object)
	 */
	public Number toNumber (Fraction<T> x)
	{
		Double ratio = manager.toNumber(x.numerator).doubleValue() /
			manager.toNumber(x.denominator).doubleValue();
		return ratio;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getEmptyArray()
	 */
	@SuppressWarnings("unchecked")
	public Fraction<T>[] getEmptyArray ()
	{
		return new Fraction[]{};
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Portable.AsJson#toJson(java.lang.Object)
	 */
	public JsonValue toJson (Fraction<T> from)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Portable.AsJson#fromJson(net.myorb.data.notations.json.JsonLowLevel.JsonValue)
	 */
	public Fraction<T> fromJson (JsonValue representation)
	{
		return null;
	}

}
