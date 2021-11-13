
package net.myorb.math;

import net.myorb.data.abstractions.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * a set of methods for manipulations of lists
 * @param <T> the description of the type of data in the lists
 * @author Michael Druckman
 */
public class ListOperations<T> extends Arithmetic<T>
{


	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public ListOperations
	(SpaceManager<T> manager)
	{ super (manager); }


	/**
	 * construct a new list of elements
	 * @return the new list object
	 */
	public List<T> newList () { return new ArrayList<T>(); }


	/**
	 * append items to a list
	 * @param toList the list being updated
	 * @param items the items to add
	 */
	@SuppressWarnings("unchecked")
	public void addToList (List<T> toList, T... items)
	{
		for (T item : items)
		{
			toList.add (item);
		}
	}


	/**
	 * construct a new list of elements with initial values
	 * @param x the list of values to initialize with
	 * @return the new initialized list
	 */
	@SuppressWarnings("unchecked")
	public List<T> newList (T... x)
	{
		List<T> l = newList ();
		addToList (l, x);
		return l;
	}


	/**
	 * append arithmetic values to a list
	 * @param toList the list being updated
	 * @param items the items to add
	 */
	@SuppressWarnings("unchecked")
	public void addToList (List<T> toList, Value<T>... items)
	{
		for (Value<T> item : items)
		{
			toList.add (item.getUnderlying ());
		}
	}


	/**
	 * append items to a list
	 * @param toList the list being updated
	 * @param items the list of items to add
	 */
	public void addToList (List<T> toList, List<T> items)
	{
		toList.addAll (items);
	}


	/**
	 * transform items in a source list 
	 *  to an output list using a function wrapper
	 * @param from the source data to be transformed
	 * @param to the list for collecting the transformed output
	 * @param using the function defining the transform
	 */
	public void transform
		(List<T> from, List<T> to, Function<T> using)
	{ to.clear (); for (T item : from) to.add (using.eval (item)); }


	/**
	 * change all elements to fill value
	 * @param list the list object being altered
	 * @param fillValue the new value for the elements
	 */
	public void fillList (List<T> list, T fillValue)
	{
		for (int i = 0; i < list.size (); i++) list.set (i, fillValue);
	}
	public void fillList (List<T> list, Value<T> fillValue)
	{ fillList (list, fillValue.getUnderlying ()); }


	/**
	 * alter section of a list
	 * @param list the list object being altered
	 * @param fillValue the new value for the elements
	 * @param starting first position to be changed
	 * @param ending last position to change
	 */
	public void fillList (List<T> list, T fillValue, int starting, int ending)
	{
		for (int i = ending; i >= starting; i--) list.set (i, fillValue);
	}


	/**
	 * append to end of list with copies if fill value
	 * @param list the list being updated with the fill value
	 * @param fillValue the fill value to use
	 * @param count number of items to add
	 */
	public void fillAppendingWith (List<T> list, T fillValue, int count)
	{
		for (int i = count; i > 0; i--) list.add (fillValue);
	}


	/**
	 * append to end of list with copies if fill value
	 * @param list the list being updated with the fill value
	 * @param fillValue the fill value to use
	 * @param ending last index to change
	 */
	public void fillListUpTo
		(List<T> list, T fillValue, int ending)
	{ for (int i = list.size (); i <= ending; i++) list.add (fillValue); }


	/**
	 * sum two lists of values
	 * @param x left side of the summing equation
	 * @param y right side of the summing equation
	 * @param result computed sum output
	 */
	public void add (List<T> x, List<T> y, List<T> result)
	{
		int xsize = x.size (), ysize = y.size ();
		int resultSize = xsize > ysize? xsize: ysize;
		T zero = discrete (0);

		for (int i = 0; i < resultSize; i++)
		{
			T sum = zero;
			if (i < xsize) sum = manager.add (sum, x.get (i));
			if (i < ysize) sum = manager.add (sum, y.get (i));
			result.add (sum);
		}
	}


	/**
	 * multiply values list by scalar
	 * @param scalar the value of the scalar
	 * @param elements the source list of values
	 * @param results the resulting product list output
	 */
	public void multiply (T scalar, List<T> elements, List<T> results)
	{
		if (manager.isZero (scalar))
		{
			fillAppendingWith (results, scalar, elements.size ());
		}
		else
		{
			for (T e : elements) results.add (X (scalar, e));
		}
	}


	/**
	 * multiply scalar into list of elements
	 * @param scalar the value of the scalar to be used
	 * @param elements a list of elements to be scaled
	 */
	public void multiplyInto (T scalar, List<T> elements)
	{
		T product, e,
		zero = discrete (0); 
		if (manager.isZero (scalar))
		{
			fillList (elements, zero);
		}
		else
		{
			for (int i = 0; i < elements.size(); i++)
			{
				product = manager.isZero
					(e = elements.get (i))? zero: X (scalar, e);
				elements.set (i, product);
			}
		}
	}


	/**
	 * check each element of list to verify constant values
	 * @param elements the list of elements to check against constant value
	 * @param constant the value checked for in each cell
	 * @return TRUE = all elements match constant
	 */
	public boolean isConstant (List<T> elements, T constant)
	{
		T negConstant = manager.negate (constant);
		for (T element : elements)
			if (!manager.isZero (manager.add (element, negConstant)))
				return false;
		return true;
	}


	/**
	 * format display text
	 * @param l the list to be displayed
	 * @return the text formatted
	 */
	public String toString (List<T> l)
	{
		StringBuffer buffer = new StringBuffer ();

		buffer.append ('[');
		for (int i = 0; i < l.size (); i++)
		{
			if (i > 0) buffer.append (", ");
			buffer.append (l.get (i));
		}
		buffer.append (']');

		return buffer.toString ();
	}

	/**
	 * display a list
	 * @param l the list to be displayed
	 */
	public void show (List<T> l)
	{
		System.out.println (toString (l));
	}


	/**
	 * line by line dump to system output
	 * @param l the list to be displayed
	 */
	public void dump (List<T> l)
	{
		for (int i = 0; i < l.size (); i++)
		{
			System.out.println (l.get (i));
		}
	}


}

