
package net.myorb.math.matrices.cofactors;

import net.myorb.math.matrices.VectorAccess;
import net.myorb.math.matrices.MatrixOperationsAccelerated;
import net.myorb.math.matrices.MinorAccess;

/**
 * pool of cofactor computation tasks (threaded version)
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ThreadedCofactorComputationPool<T> extends CofactorComputationPool<T>
{

	/**
	 * pass in operations object
	 * @param ops operations object
	 */
	public ThreadedCofactorComputationPool (MatrixOperationsAccelerated<T> ops)
	{ super (ops); count = 0; }

	/**
	 * keep count of tasks not yet completed
	 */
	protected int count;

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
		super.addTask (m, column, v);
		count++;
	}

	/**
	 * fork all tasks in the queue as parallel processes and synchronize at completion of all
	 */
	public void forkJoin ()
	{
		for (int i = 0; i < queue.size (); i++)
		{ new Thread (queue.get (i)).start (); }
		waitOnQueue ();
	}

	/**
	 * wait for signaled completion of all tasks
	 */
	public void waitOnQueue ()
	{
		synchronized (queue) { try { queue.wait (); } catch (Exception e) { e.printStackTrace (); } }
	}

	/**
	 * relinquish control allowing all of pool to get started
	 */
	public void relinquish ()
	{
		// sleep to release control to avoid too early a signal
		try { Thread.sleep (10); } catch (Exception e) { e.printStackTrace (); }
	}

	/**
	 * decrement active task count as each task completes
	 */
	public void signal ()
	{
		synchronized (queue) { if (--count == 0) queue.notify (); }
	}

}
