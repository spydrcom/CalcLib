
package net.myorb.math.expressions.symbols;

import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * connection to symbol table for transform function
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class DefinedTransform<T> extends AbstractFunction<T>
{


	/**
	 * create a symbol table
	 *  entry for a function definition
	 * @param name the name of the function to be created
	 * @param parameterNames the names of the parameters defined for the function
	 * @param functionTokens the token stream that defines the function behavior
	 * @param transform the function to execute on reference
	 */
	public DefinedTransform
		(
			String name,
			List<String> parameterNames,
			TokenParser.TokenSequence functionTokens,
			Function<T> transform
		)
	{
		super (name, parameterNames, functionTokens);
		this.transform = transform;
	}


	/**
	 * get access to transform
	 * @return the transform connected to this symbol table node
	 */
	public Function<T>
		getTransform () { return transform; }
	protected Function<T> transform;


	/**
	 * cast function to transform
	 * @param f reference to function being cast
	 * @return the transform object
	 * @param <T> data type used
	 */
	public static <T> Function<T> toTransform (SymbolMap.ExecutableUnaryOperator f)
	{
		DefinedTransform<T> definition = checkForTransform (f);
		if (definition !=  null) return definition.getTransform ();
		else return null;
	}


	/**
	 * verify transform class
	 * @param object the object to check
	 * @return a transform or NULL if not verified
	 * @param <T> data type used
	 */
	public static <T> DefinedTransform<T> checkForTransform (Object object)
	{
		@SuppressWarnings("unchecked") DefinedTransform<T>
		definition = SimpleUtilities.verifyClass (object, DefinedTransform.class);
		return definition;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#execute(java.util.List)
	 */
	public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
	{
		T parameter = valueManager.toDiscrete (parameters);
		return valueManager.newDiscreteValue (transform.eval (parameter));
	}


}

