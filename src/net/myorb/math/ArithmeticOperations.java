
package net.myorb.math;

/**
 * simple arithmetic implemented as wrapper for type manager
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ArithmeticOperations<T>
{

	/**
	 * values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public ArithmeticOperations
		(SpaceManager<T> manager)
	{
		this.manager = manager;
	}
	protected SpaceManager<T> manager;

	/**
	 * convert integer to component type
	 * @param value the integer value to convert
	 * @return the object representation of the value
	 */
	public T discrete (int value)
	{
		return manager.newScalar (value);
	}

	/**
	 * scalar zero value
	 * @return the object representation of zero
	 */
	public T zero ()
	{
		return discrete (0);
	}

	/**
	 * get value of pi
	 * @return value of pi in internal representation
	 */
	public T getPi () { return manager.getPi (); }

	/**
	 * invert sign of value
	 * @param value the value to be negated
	 * @return the object representation of the negated value
	 */
	public T neg (T value)
	{
		return manager.negate (value);
	}
	
	/**
	 * determine if value is zero
	 * @param value the value to be checked
	 * @return TRUE = value is zero
	 */
	public boolean isZero (T value)
	{
		return manager.isZero (value);
	}

	/**
	 * determine if value is negative
	 * @param value the value to be checked
	 * @return TRUE = value is negative
	 */
	public boolean isNegative (T value)
	{
		return manager.isNegative (value);
	}

	/**
	 * multiply two factors
	 * @param factor1 the first of the factors
	 * @param factor2 the second of the factors
	 * @return the product
	 */
	public T X (T factor1, T factor2)
	{
		return manager.multiply (factor1, factor2);
	}
	
	/**
	 * sum a group of terms
	 * @param terms the terms to be summed
	 * @return the sum
	 */
	@SuppressWarnings("unchecked")
	public T sum (T... terms)
	{
		T total = zero ();
		for (T t : terms) total = manager.add (total, t);
		return total;
	}

	/**
	 * multiply a series of factors
	 * @param factors the factors to be multiplied
	 * @return the product
	 */
	@SuppressWarnings("unchecked")
	public T product (T... factors)
	{
		T total = discrete (1);
		for (T t : factors) total = manager.multiply (total, t);
		return total;
	}

	/**
	 * compute multiplicitive inverse of the value
	 * @param value the basis of the computation
	 * @return the computed result
	 */
	public T invert (T value)
	{
		return manager.invert (value);
	}

	/**
	 * compute sign function of value
	 * @param value the basis of the computation
	 * @return -1, 0, 1 depending on sign of value
	 */
	public T sgn (T value)
	{
		if (isZero (value)) return zero ();
		return discrete (isNegative (value)? -1: 1);
	}

	/**
	 * compute absolute value based on negate function of manager
	 * @param value the value to be negated
	 * @return computation result object
	 */
	public T abs (T value)
	{
		if (manager.isNegative (value))
			return manager.negate (value);
		else return value;
	}

	/**
	 * @param x source of value
	 * @param y source of sign
	 * @return product of two
	 */
	public T SIGN (T x, T y)
	{
		T mag = abs (x);
		if (manager.isNegative (y)) return manager.negate (mag);
		else return mag;
	}

	/**
	 * throw RuntimeException with message
	 * @param message text of message
	 */
	public void raiseException (String message)
	{
		throw new RuntimeException (message);		
	}

}
