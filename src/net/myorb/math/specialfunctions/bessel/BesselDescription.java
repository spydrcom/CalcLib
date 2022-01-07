
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.Library;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.computational.integration.RealDomainImplementation;
import net.myorb.math.expressions.ExpressionSpaceManager;

/**
 * a reporting object for description of special functions
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class BesselDescription<T> extends RealDomainImplementation<T>
	implements SpecialFunctionFamilyManager.FunctionDescription<T>
{

	public enum OrderTypes {NON_SPECIFIC, INT, REAL, COMPLEX, LIM}

	public BesselDescription
		(
			T alpha, OrderTypes orderType,
			String functionId, String orderId,
			ExpressionSpaceManager<T> sm
		)
	{
		super (sm);
		this.a = alpha;
		this.orderType = orderType;
		this.orderIdentifier = orderId;
		this.functionIdentifier = functionId;
		this.formatTypedValue (alpha);
	}
	protected T a;

	/**
	 * @return format order specific to type
	 */
	String orderTypeDisplay ()
	{
		Number order = sm.toNumber (a);
		switch (orderType)
		{
		case INT: return orderAsInt (order);
		case NON_SPECIFIC:
			if (Library.isInteger (order))
				return orderAsInt (order);
		default: return order.toString ();
		}
	}
	protected String orderAsInt (Number order)
	{ return Integer.toString (order.intValue ()); }
	protected OrderTypes orderType;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
	 */
	public StringBuffer getFunctionDescription ()
	{
		Number order = sm.toNumber (a);
		StringBuffer display = new StringBuffer ();

		if (orderType == OrderTypes.LIM && Library.isInteger (sm.toNumber (a)))
		{
			display.append ("(").append (orderIdentifier)
			.append (" -> ").append (orderAsInt (order)).append (")");
		}
		else
		{
			display.append ("(").append (orderIdentifier)
			.append (" = ").append (orderTypeDisplay ()).append (")");
		}

		return new StringBuffer ().append ("Bessel: ")
		.append (functionIdentifier).append (display);
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
	public String getRenderIdentifier () { return functionIdentifier; }
	protected String functionIdentifier, orderIdentifier;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
	 */
	public String getFunctionName ()
	{ return functionIdentifier + orderIdentifier + formattedValue; }
	void formatTypedValue (T value) { formatValue (sm.convertToDouble (a)); }
	void formatValue (double value) { formattedValue = UnderlyingOperators.formatParameterDisplay (value); }
	protected String formattedValue;

}
