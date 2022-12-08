
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

/**
 * built-in functions available for CalcLib MATH library objects
 * @author Michael Druckman
 */
public class ClMathBIF
{


	/*
	 * 		FieldAccess
	 * 
	 * 		specifically intended for library implementations
	 * 		function GET allows structured data generated by 
	 * 		libraries to be interrogated for component data:
	 * 
	 * 		GenericValue GET (String FieldName, ClMathBIF.FieldAccess Structure)
	 * 
	 * 		ClMathBIF class exports FieldAccess interface which must
	 *		be implemented by the structure in order to export its
	 *		components as values understood by CalcLib
	 *
	 *		components can be discrete, array, matrix, or structure
	 *
	 */


	/**
	 * the interface for implementation by structured data.
	 * the implementer must use the specified identifier to
	 * to recognize the requested component or
	 * throw exception in case of failure
	 */
	public interface FieldAccess
	{
		/**
		 * interrogate a structure
		 *  and return the field identified
		 * @param identifier the textual identification of a component
		 * @return the generically wrapped field data found
		 * @throws RuntimeException for field not found
		 */
		ValueManager.GenericValue getFieldNamed (String identifier) throws RuntimeException;
	}


	/**
	 * interrogate a structure
	 * 			and return a component
	 * @param fieldNamed textual identification
	 * 			specific to the field being sought
	 * @param structure access to a data structure
	 * 			typically provided by a library implementation
	 * @return the generically wrapped field data found
	 * @throws RuntimeException for access unavailable
	 */
	public static ValueManager.GenericValue getField
		(String fieldNamed, Object structure)
			throws RuntimeException
	{
		if (structure instanceof FieldAccess)
		{
			FieldAccess access = (FieldAccess) structure;
			return access.getFieldNamed (fieldNamed);
		}
		throw new RuntimeException ("Structure does not offer field access");
	}


	/**
	 * store a value in association with an ID
	 * @param value a generic version of the value
	 * @param identifier the associated identification
	 * @param manager a manager for the data type of the value
	 * @return the same value as was passed into the store request
	 * @throws RuntimeException for value types that are not portable
	 */
	@SuppressWarnings("unchecked")
	public static <T> ValueManager.GenericValue storeValue
		(ValueManager.GenericValue value, String identifier, ExpressionSpaceManager <T> manager)
	throws RuntimeException
	{
		if (value instanceof ValueManager.PortableValue)
		{
			System.out.println (
					( ( ValueManager.PortableValue <T> ) value ).toJson (manager)
			);
			return value;
		}
		throw new RuntimeException ("Value is not portable");
	}


	/**
	 * load a stored value
	 * @param identifier the associated identification
	 * @param manager a manager for the data type of the value
	 * @return the value loaded using the given identifier
	 * @throws RuntimeException for value not found
	 */
	public static <T> ValueManager.GenericValue loadValue
		(String identifier, ExpressionSpaceManager <T> manager)
	throws RuntimeException
	{
		return null;
	}


}

