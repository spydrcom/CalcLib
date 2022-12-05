
package net.myorb.math.expressions;

import java.util.List;

public class CommonBuiltInFunctions
{


	/*
	 *		getParameters - a helper method for functions
	 * 
	 *		function calls are represented internally with a List object
	 *		referred to as ValueManager.ValueList.  the parameter values
	 *		are the List[0], List[1], ... etc.
	 * 
	 */


	/**
	 * separate parameters from a value list
	 * - functions with multiple parameters wrap them in a ValueList
	 * @param values the values which have been prepared as the parameters to a function
	 * @return the positional List of generically wrapped parameter values
	 */
	public static List <ValueManager.GenericValue> getParameters
				(ValueManager.GenericValue values)
	{
		return ( ( ValueManager.ValueList ) values ).getValues ();
	}


}

