
package net.myorb.math.realnumbers;

import net.myorb.math.FieldStructure;
import net.myorb.math.SpaceManager;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonSemantics;

import java.math.BigInteger;

/**
 * 
 * provide a type manager for extended precision integer values
 * 
 * @author Michael Druckman
 *
 */
public class IntegerSpaceManager implements SpaceManager<BigInteger>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getName()
	 */
	public String getName () { return "Integer Numbers"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getDataType()
	 */
	public DataType getDataType () { return DataType.Integer; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getComponentManager()
	 */
	public SpaceManager<BigInteger> getComponentManager () { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isNegative(java.lang.Object)
	 */
	public boolean isNegative (BigInteger x) { return BigInteger.ZERO.compareTo(x) > 0; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#newScalar(int)
	 */
	public BigInteger newScalar (int x) { return BigInteger.valueOf(x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#add(java.lang.Object, java.lang.Object)
	 */
	public BigInteger add (BigInteger x, BigInteger y) { return x.add(y); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#multiply(java.lang.Object, java.lang.Object)
	 */
	public BigInteger multiply (BigInteger x, BigInteger y) { return x.multiply(y); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceDescription#pow(java.lang.Object, int)
	 */
	public BigInteger pow (BigInteger x, int exponent) { return x.pow (exponent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#invert(java.lang.Object)
	 */
	public BigInteger invert (BigInteger x) { throw new RuntimeException ("Attempt to invert integer"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#conjugate(java.lang.Object)
	 */
	public BigInteger conjugate (BigInteger x) { return x; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#negate(java.lang.Object)
	 */
	public BigInteger negate (BigInteger x) { return x.negate(); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#lessThan(java.lang.Object, java.lang.Object)
	 */
	public boolean lessThan (BigInteger x, BigInteger y) { return x.compareTo(y) < 0; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isZero(java.lang.Object)
	 */
	public boolean isZero (BigInteger x) { return x.compareTo(BigInteger.ZERO) == 0; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toDecimalString(java.lang.Object)
	 */
	public String toDecimalString (BigInteger x) { return x.toString(); }

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
	public String toInternalString (BigInteger x) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getPi()
	 */
	public BigInteger getPi () { throw new RuntimeException ("Type is unable to represent value of PI"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getZero()
	 */
	public BigInteger getZero () { return BigInteger.ZERO; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getOne()
	 */
	public BigInteger getOne () { return BigInteger.ONE; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getFieldStructure()
	 */
	public FieldStructure<BigInteger> getFieldStructure ()
	{ throw new RuntimeException ("The Integer space does not describe a field, multiplicative inverse not implementable"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toNumber(java.lang.Object)
	 */
	public Number toNumber (BigInteger x)
	{
		return x;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getEmptyArray()
	 */
	public BigInteger[] getEmptyArray ()
	{
		return new BigInteger[]{};
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Portable.AsJson#toJson(java.lang.Object)
	 */
	public JsonValue toJson (BigInteger from)
	{
		return new JsonSemantics.JsonNumber (from);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Portable.AsJson#fromJson(net.myorb.data.notations.json.JsonLowLevel.JsonValue)
	 */
	public BigInteger fromJson (JsonValue representation)
	{
		if (representation.getJsonValueType () != JsonValue.ValueTypes.NUMERIC)
		{ throw new RuntimeException ("Invalue JSON representation for Integer value"); }
		JsonSemantics.JsonNumber n = (JsonSemantics.JsonNumber) representation;
		return new BigInteger (n.getNumber ().toString ());
	}

}
