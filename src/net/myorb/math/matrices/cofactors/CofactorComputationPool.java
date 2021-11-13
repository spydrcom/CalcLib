
package net.myorb.math.matrices.cofactors;

import net.myorb.math.matrices.*;

import java.util.ArrayList;
import java.util.List;

/**
 * pool of cofactor computation tasks
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class CofactorComputationPool<T>
{

	/**
	 * create a pool object for the computation tasks
	 * @param ops a matrix operations management object
	 */
	public CofactorComputationPool (MatrixOperationsAccelerated<T> ops)
	{
		this.queue = new ArrayList<Runnable>();
		this.ops = ops;
	}
	protected MatrixOperationsAccelerated<T> ops;
	protected List<Runnable> queue;

	/**
	 * add a cofactor computation task to the queue
	 * @param m the minor matrix access object which maps minor to parent matrix without copies
	 * @param column the column number removed from the minor matrix 
	 * @param v the vector that will collect the results
	 */
	public void addTask
		(
			MinorAccess<T> m, int column, VectorAccess<T> v
		)
	{
		Runnable task =
			new CofactorComputationTask<T> (m, column, v, ops, this);
		queue.add (task);
	}
	
	/**
	 * sequential execution of tasks used in absense of threading selected
	 */
	public void forkJoin ()
	{
		for (int i = 0; i < queue.size (); i++) { queue.get (i).run (); }
	}

	/**
	 * relinquish control allowing all of pool to get started (for threading solution)
	 */
	public void relinquish () {}

	/**
	 * decrement active task count as each task completes (for threading solution)
	 */
	public void signal () {}

}


/**
 * a thread wrapper for computation of cofactors
 * @param <T> type on which operations are to be executed
 */
class CofactorComputationTask<T> implements Runnable
{

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

	/**
	 * bundle the parameters needed for computation of a set of cofactors
	 * @param m the minor matrix access object which maps minor to parent matrix without copies
	 * @param column the column number removed from the minor matrix
	 * @param v the vector that will collect the results
	 * @param ops a matrix operations object
	 */
	public CofactorComputationTask
		(
			MinorAccess<T> m,
			int column, VectorAccess<T> v,
			MatrixOperationsAccelerated<T> ops,
			CofactorComputationPool<T> pool
		)
	{
		this.v = v; this.m = m;
		this.column = column;
		this.pool = pool;
		this.ops = ops;
	}
	protected CofactorComputationPool<T> pool;
	protected MatrixOperationsAccelerated<T> ops;
	protected VectorAccess<T> v;
	protected MinorAccess<T> m;
	protected int column;

}

