
package net.myorb.math.computational;

import net.myorb.data.abstractions.DataSequence;
import net.myorb.math.*;

/**
 * implementation of standard statistical algorithms
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class Statistics<T> extends Tolerances<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public Statistics
		(SpaceManager<T> manager)
	{ super (manager); setLibrary (new OptimizedMathLibrary<T> (manager)); }
	public T sqrt (T x) { return sroot (x); }


	/**
	 * summation of all data in a set
	 * @param dataSet the data sequence being evaluated
	 * @return sum of the data
	 */
	@SuppressWarnings("unchecked")
	public T sumOf (DataSequence<T> dataSet)
	{
		T sum = manager.getZero ();
		for (T element : dataSet) sum = sumOf (sum, element);
		return sum;
	}


	/**
	 * compute the average of a set of data
	 * @param dataSet the data sequence being evaluated
	 * @return the computed average
	 */
	public T mean (DataSequence<T> dataSet)
	{
		int n = dataSet.size ();
		T oneOverN = inverted (discrete (n));
		return X (sumOf (dataSet), oneOverN);
	}


	/**
	 * compute sum of square mean deviations
	 * @param dataSet the data sequence being evaluated
	 * @return the computed result
	 */
	@SuppressWarnings("unchecked")
	public T sumSquareMeanDeviation (DataSequence<T> dataSet)
	{
		T dataMean = mean (dataSet),
			xDev2Sum = manager.getZero (), xDev;
		for (int i = 0; i < dataSet.size (); i++)
		{
			xDev = subtract (dataSet.get (i), dataMean);
			xDev2Sum = sumOf (xDev2Sum, squared (xDev));
		}
		return xDev2Sum;
	}


	/**
	 * compute variance of data set
	 * @param dataSet the data sequence being evaluated
	 * @return the variance of the data
	 */
	public T variance (DataSequence<T> dataSet)
	{
		T xDev2Sum = sumSquareMeanDeviation (dataSet);
		return X (xDev2Sum, inverted (discrete (dataSet.size ())));
	}


	/**
	 * standard deviation
	 * @param dataSet the data set being evaluated
	 * @return computed standard deviation
	 */
	public T stdDev (DataSequence<T> dataSet)
	{
		T xDev2Sum = sumSquareMeanDeviation (dataSet);
		return sqrt (X (xDev2Sum, inverted (discrete (dataSet.size ()))));
	}


	/**
	 * coefficient of variation
	 * @param dataSet the data set being evaluated
	 * @return computed coefficient of variation
	 */
	public T cov (DataSequence<T> dataSet)
	{
		return X (stdDev (dataSet), inverted (mean (dataSet)));
	}


	/**
	 * maximum value of a data sequence
	 * @param dataSet the data set being evaluated
	 * @return the maximum value
	 */
	public T max (DataSequence<T> dataSet)
	{
		T v = dataSet.get (0);
		for (T el : dataSet) if (manager.lessThan (v, el)) v = el;
		return v;
	}


	/**
	 * minimum value of a data sequence
	 * @param dataSet the data set being evaluated
	 * @return the minimum value
	 */
	public T min (DataSequence<T> dataSet)
	{
		T v = dataSet.get (0);
		for (T el : dataSet) if (manager.lessThan (el, v)) v = el;
		return v;
	}


	/**
	 * median value of a data sequence
	 * @param dataSet the data set being evaluated
	 * @return the median value
	 */
	@SuppressWarnings("unchecked")
	public T median (DataSequence<T> dataSet)
	{
		Object[]
		items = dataSet.toArray ();
		java.util.Arrays.sort (items);
		return (T)items[items.length/2];
	}


	/**
	 * mode value of a data sequence
	 * @param dataSet the data set being evaluated
	 * @return the mode value
	 */
	public T mode (DataSequence<T> dataSet)
	{
		throw new RuntimeException ("Not implemented");
	}


}

