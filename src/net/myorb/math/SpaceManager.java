
package net.myorb.math;

import net.myorb.data.abstractions.SpaceDescription;

/**
 * primitive operations defining mathematical space
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface SpaceManager <T> extends SpaceDescription <T>
{

	/**
	 * the type of data being managed
	 */
	public enum DataType
	{
		Integer, Fraction, Real, Complex, Factorization, Matrix, System, Other
	}
	public DataType getDataType ();

	/**
	 * get the name attributed to the space
	 * @return the name text
	 */
	String getName ();

	/**
	 * get the descriptor of this space as a field
	 * @return a descriptor identifying each of the structure elements
	 */
	FieldStructure<T> getFieldStructure ();

	/**
	 * for types that have constituent components
	 * @return the type manager for the constituent components
	 */
	@SuppressWarnings("rawtypes") SpaceManager getComponentManager ();

	
	/**
	 * convert underlying type to java Number interface
	 * @param x the underlying data to be converted
	 * @return object implementing Number interface
	 */
	Number toNumber (T x);

	/**
	 * build a decimal display for the value
	 * @param x the generic value to be displayed
	 * @return the text of the display value
	 */
	String toDecimalString (T x);

	/**
	 * specify the display precision
	 * @param digits the number of digits to display
	 */
	void setDisplayPrecision (int digits);

	/**
	 * remove precision level formatting
	 */
	void resetDisplayPrecision ();

	/**
	 * provide empty array for list conversions
	 * @return an empty array of elements
	 */
	T[] getEmptyArray ();

	/**
	 * get value of PI represented in this type
	 * @return value of PI
	 */
	T getPi ();

	/**
	 * @param description a base class version
	 * @return the SpaceManager version
	 * @param <T> the data type
	 */
	public static <T> SpaceManager <T> toSpaceManager (SpaceDescription <T> description)
	{
		return (SpaceManager <T>) description;
	}

}
