
package net.myorb.math;

/**
 * a wrapper for space manager objects exporting the structure of a mathematical field
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class FieldStructureForSpace<T> implements FieldStructure<T>
{

	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public FieldStructureForSpace (SpaceManager<T> manager)
	{ this.manager = manager; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldStructure#addition(java.lang.Object, java.lang.Object)
	 */
	public T addition (T left, T right) { return manager.add (left, right); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldStructure#multiplication(java.lang.Object, java.lang.Object)
	 */
	public T multiplication (T left, T right) { return manager.multiply (left, right); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldStructure#additiveInverse(java.lang.Object)
	 */
	public T additiveInverse (T value) { return manager.negate (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldStructure#multiplicativeInverse(java.lang.Object)
	 */
	public T multiplicativeInverse (T value)  { return manager.invert (value); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldStructure#additiveIdentity()
	 */
	public T additiveIdentity ()  { return manager.getZero (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldStructure#multiplicativeIdentity()
	 */
	public T multiplicativeIdentity ()  { return manager.getOne (); }

	protected SpaceManager<T> manager;

}
