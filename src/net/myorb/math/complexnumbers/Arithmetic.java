
package net.myorb.math.complexnumbers;

import net.myorb.math.SpaceManager;
import net.myorb.math.ListOperations;
import net.myorb.math.PowerLibrary;

/**
 * arithmetic operators for use on components of multi-dimensional value
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Arithmetic<T> extends ListOperations<T>
{

	/**
	 * values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public Arithmetic
		(SpaceManager<T> manager)
	{
		super (manager);
	}

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
	{
		return new ComplexValue<T> (discrete (0), i, manager);
	}

	/**
	 * convert integer to component type
	 * @param value the integer value to convert
	 * @return the object representation of the value
	 */
	public T real (int value)
	{
		return discrete (value);
	}

	/**
	 * get access to library object
	 * @return library object
	 */
	public ComplexSupportLibrary<T> getMathLib ()
	{
		if (mathLib == null)
			mathLib = new ComplexSupportImplementation<T> (manager);
		return mathLib;
	}
	public void setMathLib (ComplexSupportLibrary<T> lib) { mathLib = lib; }
	protected ComplexSupportLibrary<T> mathLib = null;

	/**
	 * check for x == 0
	 * @param x value to be checked
	 * @return TRUE = x == 0
	 */
	public boolean isZero (T x)
	{
		return manager.isZero (x);
	}

	/**
	 * use math lib to compute sqrt(x)
	 * @param x the value to use for computation
	 * @return the computed sqrt(x)
	 */
	public T sqt (T x)
	{ return getMathLib ().sqrt (x); }

	/**
	 * general case root
	 * @param x find root of X
	 * @param n the Nth root is to be found
	 * @return computed root
	 */
	public T root (T x, int n)
	{
		PowerLibrary<T> lib = getMathLib ();
		T rootFactor = manager.invert (manager.newScalar (n));
		return lib.exp (manager.multiply (lib.ln (x), rootFactor));
	}

	/**
	 * conjugate of z
	 * @param z the value to use for computation
	 * @return conjugate of z
	 */
	public T conjugate (T z)
	{ return manager.conjugate(z); }

	/**
	 * compute the sum of a series of terms
	 * @param complexValues the series of terms
	 * @return the sum
	 */
	@SuppressWarnings("unchecked")
	public T sigma (T... complexValues) 
	{
		T total = manager.getZero ();
		for (T z : complexValues) { total = manager.add (total, z); }
		return total;
	}

	/**
	 * format terms of complex value display
	 * @param value the digit value of this term of display
	 * @param toBuffer the StringBuffer being constructed
	 * @param axis *i, *j, *k as appropriate
	 */
	public void appendPart (T value, StringBuffer toBuffer, String axis)
	{
		T display = value;
		String sign = " + ";
		if (isNeg (value))
		{
			sign = " - ";
			display = neg (value);
		}

		if (isOne (display))
		{
			toBuffer.append (sign).append (axis);
		}
		else
		{
			String digits = manager.toDecimalString (display);
			toBuffer.append (sign).append (digits).append ("*").append (axis);
		}
	}
	boolean isOne (T value) { return isZero (manager.add (value, manager.newScalar (-1))); }

}
