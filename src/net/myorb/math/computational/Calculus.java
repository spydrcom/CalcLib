
package net.myorb.math.computational;

import net.myorb.math.*;
import net.myorb.math.matrices.*;

import java.util.ArrayList;
import java.util.List;

/**
 * provide computational approximations of function derivatives and integrals
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Calculus<T> extends ListOperations<T>
{


	/**
	 * MultiDimensional function operators have delta in the parameters
	 * @param manager the manager for the type being manipulated
	 */
	public Calculus (SpaceManager<T> manager) { super (manager); }
	protected List<T> deltas = new ArrayList<T>();

	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 * @param delta size of x-axis increments in approximations
	 */
	@SuppressWarnings("unchecked")
	public Calculus (SpaceManager<T> manager, T... delta)
	{ this (manager); this.addToList (this.deltas, delta); }

	/**
	 * MultiDimensional function operators can be configured
	 *   to work on constant set of delta values i.e. dX, dY, dZ, ...
	 * @param manager the manager for the type being manipulated
	 * @param delta an ordered set of Dx objects (dX, dY, ...)
	 */
	@SuppressWarnings("rawtypes") public Calculus
		(SpaceManager<T> manager, MultiDimensional.Dx[] delta)
	{ this (manager); copyDeltaValues (delta); }


	/**
	 * copy delta values from Dx objects
	 * @param delta the ordered array of Dx objects (dX, dY, ...)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void copyDeltaValues (MultiDimensional.Dx[] delta)
	{
		for (MultiDimensional.Dx<T> d : delta)
		{ this.deltas.add (d.getDelta ()); }
	}


	/**
	 * compute (f(x+delta) - f(x)) / delta
	 * @param fX the value of the function at X
	 * @param fXplusDelta the value of the function at X+delta
	 * @param delta the value of the change in X (LIM delta GOESTO 0)
	 * @return value of 'rise over run'
	 */
	public T riseOverRun (T fX, T fXplusDelta, T delta)
	{
		T difference = subtract (fXplusDelta, fX);
		return X (difference, inverted (delta));
	}


	/**
	 * compute value of first derivative at specified value of X axis
	 * @param function the function wrapped as a procedure parameter
	 * @param x the value of X to use in the evaluation
	 * @return the computed result
	 */
	@SuppressWarnings("unchecked")
	public T derivative (Function<T> function, T x)
	{
		T fX = function.eval (x), delta = deltas.get (0);
		T fXplusDelta = function.eval (sumOf (x, delta));
		return riseOverRun (fX, fXplusDelta, delta);
	}


	/**
	 * compute vector of
	 *  partial derivatives across each variable of equation
	 * @param function the function of multiple variables being evaluated
	 * @param x a data point in the N-space of the function domain
	 * @return vector of partial derivatives
	 */
	public Vector<T> derivative (MultiDimensional.Function<T> function, List<T> x)
	{
		T fX = function.f (x);
		Vector<T> result = new Vector<T> (manager);
		for (int i = 0; i < x.size(); i++)
		{
			T delta = deltas.get (i);
			T fXplusDelta = function.f (xPlusDelta (x, delta, i));
			result.set (i, riseOverRun (fX, fXplusDelta, delta));
		}
		return result;
	}


	/**
	 * compute vector of
	 *  partial derivatives across each variable axis of equation
	 * @param function the function of multiple variables being evaluated
	 * @param x a data point in the N-space of the function domain i.e. x0=x, x1=y, ...
	 * @return vector of partial derivatives
	 */
	@SuppressWarnings("unchecked") public Vector<T> derivative
		(MultiDimensional.Function<T> function, T... x)
	{ return derivative (function, newList (x)); }


	/**
	 * compute the area under a function curve over a range
	 * @param function the function wrapped as a procedure parameter
	 * @param from starting value of the range
	 * @param to ending value of the range
	 * @return computed area
	 */
	@SuppressWarnings("unchecked")
	public T integral (Function<T> function, T from, T to)
	{
		T delta = deltas.get (0);
		T sum = discrete (0), fx, area;
		for (T x = from; !isLessThan (to, x); x = sumOf (x, delta))
		{
			fx = function.eval (x);
			area = X (fx, delta);
			sum = sumOf (sum, area);
		}
		return sum;
	}


	/**
	 * compute the path length of a function over a source range
	 * @param function the function specifying the path being integrated
	 * @param from starting value of the range
	 * @param to ending value of the range
	 * @return computed path length
	 */
	@SuppressWarnings("unchecked")
	public T lineIntegral (Function<T> function, T from, T to)
	{
		T delta = deltas.get (0);
		T sum = discrete (0), delta2 = squared (delta);
		T fxn = function.eval (from), fxnP1, distance, difference2;
		for (T x = sumOf (from, delta); !isLessThan (to, x); x = sumOf (x, delta))
		{
			fxnP1 = function.eval (x);
			difference2 = squared (subtract (fxnP1, fxn));
			distance = sroot (sumOf (difference2, delta2));
			sum = sumOf (sum, distance);
			fxn = fxnP1;
		}
		return sum;
	}


	/**
	 * compute sum of the space mapped out
	 *  by a multi dimensional function over ranges across each axis
	 * @param overRanges the range parameters for each variable axis
	 * @param function the multi variable function being evaluated
	 * @param deltas the delta for each axis i.e. dX, dY, ...
	 * @return the computed sum of the spaces
	 */
	@SuppressWarnings("unchecked")
	public T surfaceIntegral
		(
			List<MultiDimensional.Domain<T>> overRanges,
			MultiDimensional.Function<T> function,
			List<MultiDimensional.Dx<T>> deltas
		)
	{
		List<T>
		dataPoint = newList (), increment = newList ();
		for (int i = 0; i < overRanges.size (); i++)
		{
			increment.set (i, deltas.get (i).getDelta ());
			dataPoint.set (i, overRanges.get (i).getLo ());
		}
		T total = discrete (0);
		while (dataPoint != null)
		{
			total = sumOf (total, computeContributionFrom (dataPoint, function, increment));
			dataPoint = nextDataPoint (dataPoint, increment, overRanges);
		}
		return total;
	}


	/**
	 * from specified data point
	 *  compute the space mapped by delta along each axis
	 * @param dataPoint the current location in the axis space being evaluated
	 * @param function the function being evaluated
	 * @param deltas the increment values by axis
	 * @return the contribution for this point
	 */
	public T computeContributionFrom (List<T> dataPoint, MultiDimensional.Function<T> function, List<T> deltas)
	{
		T total = discrete (1);
		T fX = function.f (dataPoint);
		for (int i = 0; i < dataPoint.size (); i++)
		{
			T fXpDx = function.f (xPlusDelta (dataPoint, deltas.get (i), i));
			total = X (total, abs (subtract (fXpDx, fX)));
		}
		return total;
	}


	/**
	 * compute delta from X along one dimensional vector
	 * @param x the data point in the N-space establishing initial position
	 * @param delta the amount of offset from the X position along specified axis
	 * @param variable the index identifying which variable axis is being altered
	 * @return the data point augmented along specified axis
	 */
	@SuppressWarnings("unchecked")
	public List<T> xPlusDelta (List<T> x, T delta, int variable)
	{
		List<T> xPlus;
		addToList (xPlus = newList (), x);
		xPlus.set (variable, sumOf (x.get (variable), delta));
		return xPlus;
	}


	/**
	 * add a delta increment to current data point.
	 *  reset and move to next axis when end of range is found
	 * @param dataPoint the current location in the axis space being evaluated
	 * @param deltas the increment values by axis
	 * @param overRanges range on each axis
	 * @return the next point to use
	 */
	@SuppressWarnings("unchecked")
	public List<T> nextDataPoint
		(
			List<T> dataPoint, List<T> deltas,
			List<MultiDimensional.Domain<T>> overRanges
		)
	{
		for (int i = 0; i < dataPoint.size (); i++) 
		{
			T incremented = sumOf (dataPoint.get (i), deltas.get (i));
			if (isLessThan (incremented, overRanges.get (i).getHi ()))
			{
				dataPoint.set (i, incremented);
				return dataPoint;
			}
			dataPoint.set (i, overRanges.get (i).getLo ());
		}
		return null;
	}


}

