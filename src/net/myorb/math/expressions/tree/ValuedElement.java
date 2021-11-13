
package net.myorb.math.expressions.tree;

/**
 * POJO layer holding values of objects
 * @param <T> type of values held
 * @author Michael Druckman
 */
public abstract class ValuedElement<T> implements Element, Valued<T>
{
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.Valued#getValue()
	 */
	public T getValue () { return value; }
	public void setValue (T value) { this.value = value; }
	protected T value = null;
}
