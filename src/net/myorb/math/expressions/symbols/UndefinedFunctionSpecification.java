
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.ValueManager.GenericValue;

/**
 * this serves as a specification of a function referenced as a variable.
 * typically this will be seen in differential equations where the function is being solved for.
 * @author Michael Druckman
 */
public class UndefinedFunctionSpecification extends AbstractParameterizedFunction
{

	/**
	 * the only maintained property is the symbol name
	 * @param name the name of the function
	 */
	public UndefinedFunctionSpecification (String name)
	{
		super (name);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#execute(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public GenericValue execute(GenericValue parameters)
	{
		return null;
	}

}
