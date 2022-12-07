
package net.myorb.math.matrices;

import net.myorb.math.*;

import net.myorb.data.abstractions.Portable;
import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.*;

import java.util.List;

/**
 * operations available on vector objects
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class VectorOperations <T> extends Tolerances <T>
	implements Portable.AsJson <Vector <T>>
{

	/**
	 * values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public VectorOperations
		(SpaceManager<T> manager)
	{
		super (manager);
	}

	/**
	 * set the elements of a vector
	 * @param v the vector to be updated
	 * @param starting the first index to be changed
	 * @param elements the element values to use
	 */
	@SuppressWarnings("unchecked")
	public void set (Vector<T> v, int starting, T... elements)
	{
		int index = starting;
		for (T element : elements)
		{ v.set (index++, element); }
	}

	/**
	 * set the elements of a vector
	 * @param v the vector to be updated
	 * @param elements the element values to use
	 */
	@SuppressWarnings("unchecked")
	public void set (Vector<T> v, T... elements)
	{
		set (v, 1, elements);
	}

	/**
	 * copy cells of content held in a vector to an accessed location
	 * @param location the destination of the cells being copied
	 * @param content the cells of content being copied
	 * @param <T> data type
	 */
	public static <T> void copyContent
		(VectorAccess <T> location, Vector <T> content)
	{ copyContent (location, content, content.size ()); }

	/**
	 * copy content from location to an accessed location
	 * @param location the destination of the cells being copied
	 * @param content the source location of the cells being copied
	 * @param items the number of cells
	 * @param <T> data type
	 */
	public static <T> void copyContent
	(VectorAccess <T> location, VectorAccess <T> content, int items)
	{ for (int i = 1; i <= items; i++) location.set (i, content.get (i)); }

	/**
	 * construct a vector from a set of elements
	 * @param elements the elements to be set in the vector
	 * @return the newly constructed vector
	 */
	@SuppressWarnings("unchecked")
	public Vector<T> V (T... elements)
	{
		Vector<T> v = new Vector<T> (elements.length, manager);
		set (v, elements);
		return v;
	}

	/**
	 * copy vector contents to list
	 * @param list the list being appended
	 * @param access the vector
	 */
	public void addToList (List<T> list, VectorAccess<T> access)
	{
		for (int i = 1; i <= access.size (); i++) list.add (access.get (i));
	}

	/**
	 * sum a series of vectors
	 * @param terms the vector list to be summed
	 * @return computed total sum
	 */
	@SuppressWarnings("unchecked")
	public Vector<T> sum (Vector<T>... terms)
	{
		Vector<T> total = new Vector<T> (terms[0].size (), manager);
		for (Vector<T> v : terms) total = total.plus (v);
		return total;
	}

	/**
	 * compute dot product of two vectors
	 * @param left the left side of the product
	 * @param right the right side of the product
	 * @return the resulting computed result
	 */
	@SuppressWarnings("unchecked")
	public T dotProduct (VectorAccess<T> left, VectorAccess<T> right)
	{
		T result = discrete (0);
		int size = left.size ();
		if (size != right.size ())
		{
			raiseException ("Dot product requires like sized vectors (" + size + " != " + right.size () + ")");
		}
		for (int i = 1; i <= size; i++)
		{
			result = sumOf (result, X (left.get (i), right.get (i)));
		}
		return result;
	}

	/**
	 * compute dot product of two vectors.
	 *  left side vector is altered to conjugate before product
	 * @param left the left side of the product
	 * @param right the right side of the product
	 * @return the resulting computed result
	 */
	@SuppressWarnings("unchecked")
	public T conjDotProduct (VectorAccess<T> left, VectorAccess<T> right)
	{
		T result = discrete (0);
		int size = left.size ();
		if (size != right.size ())
		{
			raiseException ("Dot product requires like sized vectors (" + size + " != " + right.size () + ")");
		}
		for (int i = 1; i <= size; i++)
		{
			result = sumOf (result, X (manager.conjugate (left.get (i)), right.get (i)));
		}
		return result;
	}

	/**
	 * compute 3-D cross product of 2 vectors
	 * @param left left side of the product equation
	 * @param right right side of the equation
	 * @return the resulting product
	 */
	@SuppressWarnings("unchecked")
	public Vector<T> crossProduct (VectorAccess<T> left, VectorAccess<T> right)
	{
		if (left.size() != 3 || right.size() != 3)
		{ raiseException ("Vector cross product for 3-D only"); }
		MatrixOperations<T> matrix = new MatrixOperations<T> (manager);
		T a1 = left.get (1), a2 = left.get (2), a3 = left.get (3);

		T z = discrete (0);
		Matrix<T> m = new Matrix<T> (3, 3, manager);
		matrix.setRow (1, m, V (z, neg (a3), a2));
		matrix.setRow (2, m, V (a3, z, neg (a1)));
		matrix.setRow (3, m, V (neg (a2), a1, z));

		return matrix.product (m, matrix.columnMatrix (right)).getCol (1);
	}

	/**
	 * compute product of a column vector and a row vector
	 * @param columnVector the column vector factor of the product
	 * @param rowVector the row vector factor of the product
	 * @return the matrix result product
	 */
	public Matrix<T> dyadicProduct (VectorAccess<T> columnVector, VectorAccess<T> rowVector)
	{
		int rows = rowVector.size (), cols = columnVector.size ();
		Matrix<T> m = new Matrix<T> (rows, cols, manager);
		for (int r = 1; r <= rows; r++)
		{
			for (int c = 1; c <= rows; c++)
			{
				m.set (r, c, X (columnVector.get (c), rowVector.get (r)));
			}
		}
		return m;
	}

	/**
	 * sum the squares of the elements
	 * @param v the vector being evaluated
	 * @return e0*e0 + e1*e1 + e2*e2 + ...
	 */
	public T magnitudeSquared (VectorAccess<T> v) { return dotProduct (v, v); }
	public T magnitude (VectorAccess<T> v) { return sroot (magnitudeSquared (v)); }

	/**
	 * check for vector equality within tolerance
	 * @param left first of vectors to be compared
	 * @param right second of vectors to be compared
	 * @return TRUE = vectors are within tolerance
	 */
	public boolean isWithinTolerance (Vector<T> left, Vector<T> right)
	{
		for (int i = 1; i <= left.size(); i++)
		{ if (!withinTolerance (subtract (left.get(i), right.get(i)))) return false; }
		return true;
	}

	/**
	 * compute mean square error
	 *  based on difference of two vectors
	 * @param v1 a vector of values to compare
	 * @param v2 another vector used to establish difference
	 * @return mean square error between vectors
	 */
	public T mse
	(VectorAccess<T> v1, VectorAccess<T> v2)
	{
		Value<T> err;
		int n = v1.size ();
		Value<T> sum = forValue (0);
		for (int i = 1; i<= n; i++)
		{
			err = forValue (v1.get (i))
				.minus (forValue (v2.get (i)));
			sum = sum.plus (err.squared ());
		}
		return sum.over (forValue (n)).getUnderlying ();
	}

	/**
	 * format display text
	 * @param v the vector to be displayed
	 * @return the text formatted
	 */
	public String toString (VectorAccess<T> v)
	{
		StringBuffer buffer = new StringBuffer ();

		buffer.append ('[');
		for (int i = 1; i<= v.size (); i++)
		{
			if (i > 1) buffer.append (", ");
			buffer.append (v.get (i));
		}
		buffer.append (']');

		return buffer.toString ();
	}

	/**
	 * display vector
	 * @param v the vector to be displayed
	 */
	public void show (VectorAccess<T> v)
	{
		System.out.println (toString (v));
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Portable.AsJson#toJson(java.lang.Object)
	 */
	public JsonValue toJson (Vector <T> from)
	{
		return toJson ((VectorAccess <T>) from);
	}
	public JsonValue toJson (VectorAccess <T> from)
	{
		JsonSemantics.JsonArray array = new JsonSemantics.JsonArray ();
		for (int item = 1; item <= from.size (); item++) array.add (manager.toJson (from.get (item))); 
		return array;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Portable.AsJson#fromJson(net.myorb.data.notations.json.JsonLowLevel.JsonValue)
	 */
	public Vector <T> fromJson (JsonValue representation)
	{
		JsonSemantics.JsonArray elements =
				JsonTools.toArray (representation);
		Vector <T> v = new Vector <T> (elements.size (), manager);
		int i = 1; for (JsonValue element : elements) v.set (i++, manager.fromJson (element)); 
		return v;
	}

}
