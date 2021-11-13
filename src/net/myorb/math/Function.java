
package net.myorb.math;

/**
 * a wrapper for passing procedures as parameters
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public interface Function<T>
	extends net.myorb.data.abstractions.Function<T>
{
	SpaceManager<T> getSpaceManager ();
}