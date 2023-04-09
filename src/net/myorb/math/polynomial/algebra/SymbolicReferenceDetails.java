
package net.myorb.math.polynomial.algebra;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;

/**
 * interface for polynomial algebra access to CalcLib symbols
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface SymbolicReferenceDetails <T>
{

	/**
	 * the operations available for a function
	 * @param <T> the data type
	 */
	public interface FunctionProfile <T>
	{

		/**
		 * @return the parameter from the function profile
		 */
		String getParameterName ();

		/**
		 * get access to the expression tree that describes the function
		 * @return the expression tree associated with the function
		 * @throws Exception errors generating the expression tree
		 */
		JsonValue getExpressionTree () throws Exception;

		/**
		 * @param series the series to be associated with the function
		 */
		void setSeries (SeriesExpansion <T> series);

		/**
		 * @return the series associated with the function
		 */
		SeriesExpansion <T> getSeries ();

	}

	/**
	 * query the symbol table to find a function
	 * @param functionName the name of the function
	 * @return access to the function profile
	 */
	FunctionProfile <T> getProfile (String functionName);

}
