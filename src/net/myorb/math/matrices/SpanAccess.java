
package net.myorb.math.matrices;

import java.util.List;

/**
 * provide access to portions of large arrays treated as vectors
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SpanAccess<T> implements VectorAccess<T>
{

	/**
	 * provide access to spans of matrix arrays
	 * @param master the list supporting a matrix as linear array
	 * @param starting the starting index of a span
	 * @param vectorSpan the size of the span
	 */
	public SpanAccess (List<T> master, int starting, int vectorSpan)
	{
		this.master = master;
		this.initial = this.starting = starting;
		this.size = this.vectorSpan = vectorSpan;
		this.indexSpan = 1;
	}
	protected int starting, initial, indexSpan, vectorSpan, size;
	protected List<T> master;

	/**
	 * provide access to spans of matrix arrays with index spans GT 1
	 * @param master the list supporting a matrix as linear array
	 * @param indexSpan the span of a unit index increase
	 * @param starting the starting index of a span
	 * @param vectorSpan the size of each vector
	 * @param size externally visible size
	 */
	public SpanAccess (List<T> master, int indexSpan, int starting, int vectorSpan, int size)
	{
		this.master = master;
		this.initial = this.starting = starting;
		this.vectorSpan = vectorSpan;
		this.indexSpan = indexSpan;
		this.size = size;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#size()
	 */
	public int size ()
	{
		return size;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#get(int)
	 */
	public T get (int index)
	{
		return master.get (starting + (index-1)*indexSpan);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#set(int, java.lang.Object)
	 */
	public void set (int index, T value)
	{
		master.set (starting + (index-1)*indexSpan, value);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#fill(java.lang.Object)
	 */
	public void fill (T value)
	{
		for (int i = 1; i <= size; i++) set (i, value);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#nextSpan()
	 */
	public void nextSpan ()
	{
		this.starting += this.vectorSpan;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#resetSpan()
	 */
	public void resetSpan ()
	{
		this.starting = this.initial;
	}

}
