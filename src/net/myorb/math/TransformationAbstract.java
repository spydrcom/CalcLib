
package net.myorb.math;

/**
 * boiler plate for wrapping a function to be used as a parameter.
 *  both simple functions Y=f(X) and multi demension functions Y=f(X,Y,...) are representable
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public abstract class TransformationAbstract<T> extends ListOperations<T>
		implements Function<T>, MultiDimensional.Function<T>
{

	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public TransformationAbstract
	(SpaceManager<T> manager) { super (manager); }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#f(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public T eval (T x) { return f (newList (x)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#f(T[])
	 */
	@SuppressWarnings("unchecked")
	public T f (T... x) { return f (newList (x)); }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceDescription () { return manager; }
	public SpaceManager<T> getSpaceManager () { return manager; }
	protected SpaceManager<T> manager;

}
