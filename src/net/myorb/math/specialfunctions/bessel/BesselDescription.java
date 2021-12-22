
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.SpaceManager;

/**
 * a reporting object for description of special functions
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class BesselDescription<T> implements SpecialFunctionFamilyManager.FunctionDescription<T>
{

	public BesselDescription
	(T a, String functionId, String orderId, ExpressionSpaceManager<T> sm)
	{
		this.a = a; this.sm = sm;
		this.orderIdentifier = orderId;
		this.functionIidentifier = functionId;
		this.formatTypedValue (a);
	}
	protected T a;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
	 */
	public StringBuffer getFunctionDescription ()
	{
		return new StringBuffer ()
		.append ("Bessel: ").append(functionIidentifier)
		.append ("(").append (orderIdentifier).append ("=")
		.append (a).append (")");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getElaborateFunctionDescription()
	 */
	public StringBuffer getElaborateFunctionDescription ()
	{ return getFunctionDescription ().append (getElaboration ()); }

	/**
	 * @return configuration data to be displayed
	 */
	public String getElaboration () { return ""; }

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
	 */
	public String getRenderIdentifier () { return functionIidentifier; }
	protected String functionIidentifier, orderIdentifier;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
	 */
	public String getFunctionName ()
	{ return functionIidentifier + orderIdentifier + formattedValue; }
	void formatTypedValue (T value) { formatValue (sm.convertToDouble (a)); }
	void formatValue (double value) { formattedValue = UnderlyingOperators.formatParameterDisplay (value); }
	protected String formattedValue;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{ return sm.convertFromDouble ( evalReal ( sm.convertToDouble (x) ) ); }
	public double evalReal (double x) { return 0.0; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return sm; }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceManager () { return sm; }
	protected ExpressionSpaceManager<T> sm;

}
