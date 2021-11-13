
package net.myorb.math;

/**
 * properties of a family of functions
 * @param <T> the data type to operate on
 * @author Michael Druckman
 */
public interface FamilyOfFunctions<T>
{

	/**
	 * @return the name of the family
	 */
	String getName ();

	/**
	 * @param kind a name for the kind (typically first &amp; second, null if only one)
	 * @return the text which typically identifies the functions
	 */
	String getIdentifier (String kind);

	/**
	 * @param manager data type manager
	 */
	void init (SpaceManager<T> manager);

}
