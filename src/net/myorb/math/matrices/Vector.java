
package net.myorb.math.matrices;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.ListOperations;
import net.myorb.math.SpaceManager;

import java.util.*;

/**
 * representation of vector of generic elements
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Vector <T> extends ListOperations <T>
		implements VectorAccess <T>
{

	/**
	 * construct a vector without allocating space
	 * @param manager the manager for the type being manipulated
	 */
	public Vector (SpaceManager<T> manager)
	{
		super (manager);
		this.elements = new ArrayList<T> ();
	}

	/**
	 * construct a vector of given size
	 * @param size the count of elements in this vector
	 * @param manager the manager for the type being manipulated
	 */
	public Vector (int size, SpaceManager<T> manager)
	{
		this (manager);
		resize (size);
	}

	/**
	 * allocate space for a vector of given size
	 * @param size the count of elements in this vector
	 */
	public void resize (int size)
	{
		this.elements = new ArrayList<T> (size);
		this.fillAppendingWith (this.elements, discrete (0), size);
	}
	public List<T> getElementsList () { return elements; }
	protected List<T> elements = null;

	/**
	 * set the value of a element
	 * @param index the index number
	 * @param value the new element value
	 */
	public void set (int index, T value)
	{
		elements.set (index - 1, value);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#get(int)
	 */
	public T get (int index)
	{
		return elements.get (index - 1);
	}

	/**
	 * sum two vectors
	 * @param addend vector to be added with THIS
	 * @return computed sum
	 */
	public Vector<T> plus (Vector<T> addend)
	{
		Vector<T> v = null;
		if (this.size () != addend.size ())
		{ raiseException ("Addition of vectors requires like sized addends"); }
		else
		{
			add
			(
				this.elements, addend.elements,
				(v = new Vector<T> (manager)).elements
			);
		}
		return v;
	}

	/**
	 * multiply vector by scalar
	 * @param scalar the value of the scalar
	 * @return the product vector
	 */
	public Vector<T> times (T scalar)
	{
		Vector<T> v = new Vector<T> (manager);
		multiply (scalar, this.elements, v.elements);
		return v;
	}

	/**
	 * multiply vector by scalar
	 * @param scalar the value of the scalar
	 * @param into vector collecting product
	 */
	public void scale (T scalar, Vector<T> into)
	{
		into.elements.clear ();
		multiply (scalar, this.elements, into.elements);
	}

	/**
	 * reverse sign of each element of vector
	 * @return resulting negated vector
	 */
	public Vector<T> negate ()
	{
		return this.times (discrete (-1));
	}

	/**
	 * convert vector to containing conjugate values for each element
	 * @return the converted vector
	 */
	public Vector<T> conjugate ()
	{
		Vector<T> v = new Vector<T> (manager);
		for (int i = 1; i <= elements.size (); i++) v.elements.add (manager.conjugate (elements.get (i)));
		return v;
	}

	/**
	 * append elements of vector to a list
	 * @param toList the list being updated
	 */
	public void addToList (List<T> toList)
	{
		addToList (toList, elements);
	}

	/**
	 * load vector elements from a list
	 * @param fromList the list providing data
	 */
	public void load (List<T> fromList)
	{
		addToList (elements, fromList);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#nextSpan()
	 */
	public void nextSpan ()
	{
		raiseException ("No span adjustment available");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#resetSpan()
	 */
	public void resetSpan ()
	{
		raiseException ("No span reset available");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#size()
	 */
	public int size () { return elements.size (); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return this.elements.toString (); }

	/**
	 * construct a Vector of integer values
	 * @param content integer array to use as content
	 * @param mgr the data type manager for the session
	 * @return a Vector holding the supplied content
	 * @param <T> the type of content data
	 */
	public static <T> Vector <T> toVector
		( int [] content, ExpressionSpaceManager <T> mgr )
	{
		int size = content.length;
		Vector<T> v = new Vector<T> (size, mgr);
		for (int i = 1; i <= size; i++)
		{
			v.set (i, mgr.convertFromDouble ( (double) content[i-1] ) );
		}
		return v;
	}

}
