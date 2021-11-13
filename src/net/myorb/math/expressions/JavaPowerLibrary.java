
package net.myorb.math.expressions;

import net.myorb.math.*;

/**
 * use Java Math library for performance and precision
 *  when features of using internally coded versions are not required
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class JavaPowerLibrary<T> implements PowerLibrary<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#factorial(java.lang.Object)
	 */
	public T factorial (T value)
	{
		double result = 1;
		if (manager.isNegative (value))
			throw new RuntimeException ("Factorial of negative number");
		long source = manager.toNumber (value).longValue ();
		while (source > 1)  { result *= (source--); }
		return manager.convertFromDouble (result);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#dFactorial(java.lang.Object)
	 */
	public T dFactorial (T value)
	{
		double result = 1;
		if (manager.isNegative (value))
			throw new RuntimeException ("Factorial of negative number");
		long source = manager.toNumber (value).longValue ();
		while (source > 1)  { result *= (source-=2); }
		return manager.convertFromDouble (result);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#ln(java.lang.Object)
	 */
	public T ln(T value)
	{
		return manager.convertFromDouble (Math.log (manager.convertToDouble (value)));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#exp(java.lang.Object)
	 */
	public T exp(T value)
	{
		return manager.convertFromDouble (Math.exp (manager.convertToDouble (value)));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#nativeExp(java.lang.Object)
	 */
	public T nativeExp (T value)
	{
		return manager.convertFromDouble (Math.exp (manager.convertToDouble (value)));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#pow(java.lang.Object, int)
	 */
	public T pow(T value, int exponent)
	{
		return manager.convertFromDouble (Math.pow (manager.convertToDouble (value), exponent));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#sqrt(java.lang.Object)
	 */
	public T sqrt(T value)
	{
		return manager.convertFromDouble (Math.sqrt (manager.convertToDouble (value)));
	}

	/**
	 * type manager needed to covert to/from double float
	 * @param manager an expression space manager for the type
	 */
	public JavaPowerLibrary (ExpressionSpaceManager<T> manager)
	{
		this.manager = manager;
	}
	ExpressionSpaceManager<T> manager;

}

