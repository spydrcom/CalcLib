
package net.myorb.math.matrices.optimization;

import net.myorb.math.matrices.FullMatrixWrapper;
import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.MinorAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * provide Runnable implementation of cofactor computation
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MinorMatrixComputationTask<T> extends MinorMatrixComputationPool<T>
	implements CofactorComputationTaskFactory<T>, VectorAccess<T>, Runnable
{


	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.optimization.CofactorComputationTaskFactory#cofactorComputationTask(net.myorb.math.matrices.MinorAccess, int, net.myorb.math.matrices.VectorAccess, net.myorb.math.matrices.optimization.MinorMatrixComputationPool, net.myorb.math.matrices.optimization.MatrixOperationsOptimized)
	 */
	public Runnable cofactorComputationTask
		(
			MinorAccess<T> m, int column, VectorAccess<T> v,
			MinorMatrixComputationPool<T> pool, MatrixOperationsOptimized<T> ops
		)
	{
		this.ops = ops;
		this.column = column;
		this.m = m; this.v = v;
		this.pool = pool;
		return this;
	}
	protected MinorMatrixComputationPool<T> pool;
	protected MatrixOperationsOptimized<T> ops;
	protected VectorAccess<T> v;
	protected MinorAccess<T> m;
	protected int column;

	public void dump ()
	{
		System.out.print ("*** TASK " + this.toString());
		System.out.print (" size=" + this.size);
		System.out.print (" rows=" + this.rowCount);
		System.out.print (" cols=" + this.columnCount);
		System.out.print (" column=" + this.column);
		System.out.print (" count=" + this.count);
		System.out.print (" link=" + this.link);
		System.out.println ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		pool.relinquish ();
		// the minor access object covers all rows for the column originally set
		for (int i = 1; i <= v.size(); i++) v.set (i, ops.cofactor (m, i, column));
		// signal task completion
		pool.signal ();
	}


	/*/
	 * access implementation for matrix and minor
	/*/

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.optimization.AbstractOptimizedWrapper#getOptimizedAccess(net.myorb.math.matrices.FullMatrixWrapper)
	 */
	public MinorMatrixComputationTask<T> getOptimizedAccess (FullMatrixWrapper<T> wrapperFactory) { return this; }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MinorAccess#getCofactorComputationTaskFactory()
	 */
	public CofactorComputationTaskFactory<T> getCofactorComputationTaskFactory () { return this; }


	/*/
	 * vector access implementation
	/*/

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#set(int, java.lang.Object)
	 */
	public void set (int index, T value) { elements.set (index - 1, value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#get(int)
	 */
	public T get (int index) { return elements.get (index - 1); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.optimization.AbstractOptimizedWrapper#getVectorAccess(java.lang.Object, int)
	 */
	public MinorMatrixComputationTask<T> getVectorAccess (T initial, int length)
	{
		initializeVector (initial, length);
		return this;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#size()
	 */
	public int size () { return elements.size (); }

	/**
	 * prepare vector of specified size
	 * @param initial value for initialization of items
	 * @param length count of items
	 */
	public void initializeVector (T initial, int length)
	{
		elements.clear ();
		for (int i=0; i<length; i++)
		{ elements.add (initial); }
	}
	protected List<T> elements = new ArrayList<T> ();

	public MinorMatrixComputationTask<T> link = null;

}

