
package net.myorb.math.complexnumbers;

import net.myorb.math.TrigPowImplementation;

import net.myorb.math.expressions.algorithms.TrigPowPrimitives;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.SpaceManager;

import net.myorb.data.abstractions.SpaceConversion;

/**
 * a library of primitives for complex number manipulation
 * @author Michael Druckman
 */
public class ComplexAlgorithmAccess
{

	/**
	 * @return a value manager for complex data
	 */
	public static ValueManager<ComplexValue<Double>> getValueManager () { return valueManager; }
	public static ValueManager<ComplexValue<Double>> valueManager = new ValueManager<ComplexValue<Double>> ();

	/**
	 * @return a space conversion manager for complex data
	 */
	public static SpaceConversion<ComplexValue<Double>> getSpaceConversion () { return getSpaceManager (); }
	public static ExpressionComponentSpaceManager<ComplexValue<Double>> getSpaceManager () { return spaceManager; }
	public static ExpressionComplexFieldManager spaceManager = new ExpressionComplexFieldManager ();

	/**
	 * @return a space manager for complex data components
	 */
	public static SpaceManager<Double> getComponentSpaceManager ()
	{ return (SpaceManager<Double>) spaceManager.getComponentManager (); }
	@SuppressWarnings("unchecked") public static SpaceConversion<Double> getComponentSpaceConversion ()
	{ return (SpaceConversion<Double>) getComponentSpaceManager (); }

	/**
	 * @return an instance of the complex library implementation
	 */
	public static ComplexSupportLibrary<Double> getComplexSupportLibrary ()
	{ return new JreComplexSupportLibrary (getComponentSpaceConversion ()); }

	/**
	 * @return a trig-pow primitives object
	 */
	public static TrigPowPrimitives<ComplexValue<Double>> getTrigPowAlgorithms ()
	{
		return new TrigPowPrimitives<ComplexValue<Double>>
			(
				spaceManager, valueManager,
				getTrigPowImplementation ()
			);
	}

	/**
	 * @return a trig-pow implementation object
	 */
	public static TrigPowImplementation<ComplexValue<Double>> getTrigPowImplementation ()
	{
		return new TrigPowImplementation<ComplexValue<Double>>
			(
				getComplexLibrary (),
				spaceManager
			);
	}

	/**
	 * @return a complex library object
	 */
	public static ComplexLibrary<Double> getComplexLibrary ()
	{
		return new ComplexLibrary<Double>
			(
				spaceManager.getComponentManager (),
				spaceManager
			);
	}

}
