
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;
import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * function implementations for use as complex integrands
 * @author Michael Druckman
 */
public class ComplexIntegrandFunctionBase implements Function<ComplexValue<Double>>
{

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> t)
	{
		throw new RuntimeException ("Integrand body not provided");
	}

	/**
	 * @param x the transform variable value
	 */
	public void setParameter (ComplexValue<Double> x) { this.x = x; }
	protected ComplexValue<Double> x;

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<ComplexValue<Double>> getSpaceManager () { return manager; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceManager<ComplexValue<Double>> getSpaceDescription () { return manager; }

	@SuppressWarnings("unchecked") public ComplexIntegrandFunctionBase ()
	{
		this.manager = new ExpressionComplexFieldManager ();
		this.components = (ExpressionSpaceManager<Double>) manager.getComponentManager ();
	}
	protected final ExpressionSpaceManager<ComplexValue<Double>> manager;
	protected final ExpressionSpaceManager<Double> components;

}

