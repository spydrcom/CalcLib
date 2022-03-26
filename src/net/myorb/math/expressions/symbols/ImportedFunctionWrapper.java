
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;

import net.myorb.math.Function;

/**
 * wrapper for imported function objects
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ImportedFunctionWrapper <T>
		extends AbstractParameterizedFunction
{


	public ImportedFunctionWrapper
	(String functionName, String parameterName, Function <T> function)
	{
		super (functionName);
		this.vm = new ValueManager <T> ();
		this.parameterName = parameterName;
		this.function = function;
	}
	protected Function <T> function;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public GenericValue execute (GenericValue parameter)
	{
		return vm.newDiscreteValue
		(
			function.eval
			(
				vm.toDiscrete (parameter)
			)
		);
	}
	protected ValueManager <T> vm;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractParameterizedFunction#getParameterList()
	 */
	public String getParameterList () { return parameterName; }
	protected String parameterName;


}

