
package net.myorb.math.matrices.cofactors;

import net.myorb.math.matrices.MatrixOperationsAccelerated;

import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * implementation of threaded version
 *  of cofactor computation mechanism using concurrent executors
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ExecutorCofactorComputationPool<T> extends ThreadedCofactorComputationPool<T>
{


	/**
	 * pass in operations object
	 * @param ops operations object
	 */
	public ExecutorCofactorComputationPool
	(MatrixOperationsAccelerated<T> ops)
	{ super (ops); }


	/**
	 * fork all tasks in the queue as parallel processes and synchronize at completion of all
	 */
	public void forkJoin ()
	{
		for (int i = 0; i < queue.size (); i++)
		{ executorPool.execute (queue.get (i)); }
		waitOnQueue ();
	}


    //RejectedExecutionHandler implementation
    static RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
    
    //Get the ThreadFactory implementation to use
    static ThreadFactory threadFactory = Executors.defaultThreadFactory();

    //creating the ThreadPoolExecutor
    static ThreadPoolExecutor executorPool = new ThreadPoolExecutor
    		(
    				20, 1000, 10, TimeUnit.SECONDS, 
    				new ArrayBlockingQueue<Runnable>(2), 
    				threadFactory, rejectionHandler
    		);

}


class RejectedExecutionHandlerImpl implements RejectedExecutionHandler
{
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println(r.toString() + " is rejected");
    }
}

