
package net.myorb.math.matrices.optimization;

import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.MinorAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * provide thread control (fork/join) logic for minor matrix cofactor computations
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MinorMatrixComputationPool<T> extends MinorMatrixWrapper<T>
{

	static final boolean RUN_THREADED = false;

	/**
	 * initialize control objects
	 */
	public MinorMatrixComputationPool ()
	{
		this.queue = new ArrayList<Runnable> ();
		this.count = 0;
	}
	protected List<Runnable> queue;
	protected int count;


	/**
	 * add a task to the pool
	 * @param m the matrix access object
	 * @param column the column removed from the matrix
	 * @param v the vector access object to write the cofactors to
	 * @param factory a factory for building the task objects
	 * @param ops the operations object for cofactor computation
	 */
	public void addTask
		(
			MinorAccess<T> m, int column, VectorAccess<T> v,
			CofactorComputationTaskFactory<T> factory,
			MatrixOperationsOptimized<T> ops
		)
	{
		Runnable task =
			factory.cofactorComputationTask (m, column, v, this, ops);
		queue.add (task);
		count++;
	}


	/**
	 * fork all tasks in the queue as parallel processes and synchronize at completion of all
	 * @param ops the matrix operations controller object
	 */
	public void forkJoin (MatrixOperationsOptimized <T> ops)
	{
		for (int i = 0; i < queue.size (); i++)
		{
			Runnable task = queue.get (i);

			if (!RUN_THREADED)
			{
				task.run ();
			}
			else
			{
				new Thread (task).start ();
			}
		}
		waitOnQueue ();
		queue.clear ();
		//clear (ops);
	}

	/**
	 * mark tasks in completed queue as released.
	 *  the queue list is then cleared for a future reuse
	 * @param ops the matrix operations controller object
	 */
	public void clear (MatrixOperationsOptimized <T> ops)
	{
		for (int i = 0; i < queue.size (); i++)
			release (queue.get (i), ops);
		count = 0;
	}

	/**
	 * mark identified object as released
	 * @param o the object to be added to the released task chain
	 * @param ops the matrix operations controller object
	 */
	@SuppressWarnings("unchecked")
	public void release (Object o, MatrixOperationsOptimized <T> ops)
	{ ops.releaseTask ((MinorMatrixComputationTask<T>)o); }

	/**
	 * wait for signaled completion of all tasks
	 */
	public void waitOnQueue ()
	{
		if (!RUN_THREADED) return;
		synchronized (queue) { try { queue.wait (); } catch (Exception e) { e.printStackTrace (); } }
	}


	/**
	 * relinquish control allowing all of pool to get started
	 */
	public void relinquish ()
	{
		if (!RUN_THREADED) return;
		// sleep to release control to avoid too early a signal
		try { Thread.sleep (10); } catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * decrement active task count as each task completes
	 */
	public void signal ()
	{
		if (!RUN_THREADED) return;
		synchronized (queue) { if (--count == 0) queue.notify (); }
	}


}

