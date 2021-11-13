
package net.myorb.math.expressions.evaluationstates;

import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * generic transform processing specification
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface GenericTransform<T> extends Function<T>
{
	/**
	 * apply appropriate transform
	 * @param functionName the name of the function
	 * @param parameterNames the names of supplied parameters
	 * @param transformTokens the tokens of the decalaration
	 * @param definitionTokens description for the display
	 * @return the transformed function
	 */
	Function<T> apply
	(
		String functionName, List<String> parameterNames,
		DeclarationSupport.TokenStream transformTokens,
		DeclarationSupport.TokenStream definitionTokens
	);
}
