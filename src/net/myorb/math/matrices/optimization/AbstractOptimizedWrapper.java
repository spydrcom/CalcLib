
package net.myorb.math.matrices.optimization;

import net.myorb.math.matrices.AbstractMatrixWrapper;
import net.myorb.math.matrices.FullMatrixWrapper;

/**
 * provide default behaviors for portions of interfaces which are not implemented at some super-structure layers
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public abstract class AbstractOptimizedWrapper<T>  extends AbstractMatrixWrapper<T>
{

	public AbstractOptimizedWrapper () {}
	public AbstractOptimizedWrapper (int size) { super (size); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MatrixAccess#getOptimizedAccess(net.myorb.math.matrices.FullMatrixWrapper)
	 */
	public MinorMatrixComputationTask<T> getOptimizedAccess (FullMatrixWrapper<T> wrapperFactory) { notImplemented (); return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.MinorAccess#getCofactorComputationTaskFactory()
	 */
	public CofactorComputationTaskFactory<T> getCofactorComputationTaskFactory () { notImplemented (); return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.optimization.AbstractOptimizedWrapper#getVectorAccess(java.lang.Object, int)
	 */
	public MinorMatrixComputationTask<T> getVectorAccess (T initial, int length) { notImplemented (); return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#nextSpan()
	 */
	public void nextSpan () { notImplemented (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.matrices.VectorAccess#resetSpan()
	 */
	public void resetSpan () { notImplemented (); }

	/**
	 * exception thrown for unimplemented functionality
	 */
	public void notImplemented () { throw new RuntimeException ("Optimized access not implemented"); }

}
