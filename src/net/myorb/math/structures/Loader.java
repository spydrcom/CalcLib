
package net.myorb.math.structures;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.math.expressions.evaluationstates.Environment;

/**
 * the manager for the loading of PortableValue coming from ValueManager
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Loader <T>
{


	/**
	 * identify mechanisms for loading of stored values
	 * @param <S> the type of object stored as JSON
	 * @param <T> the data type
	 */
	public interface PortableValueRestoration <T, S>
	{
		/**
		 * convert a JSON tree to a structured value
		 * @param value the JSON representation to be converted
		 * @param environment the core data source for this session
		 * @return the original representation of the object
		 */
		S fromJson (JsonValue value, Environment <T> environment);
	}


}

