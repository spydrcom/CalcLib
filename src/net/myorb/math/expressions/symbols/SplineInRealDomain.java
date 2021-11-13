
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.MultiDimensional;

/**
 * a hard-coded floating version of the descriptor.
 * code generation for segmented function export will reference this class.
 * the export feature is only intended for use with 'double' type parameters.
 * using the version of the class reduces the syntax
 * complexities of the generic version.
 * @author Michael Druckman
 */
public class SplineInRealDomain
	extends SplineDescriptor<Double>
{

	// all objects will use only this space manager
	static final ExpressionFloatingFieldManager floatManager =
			new ExpressionFloatingFieldManager ();
	public SplineInRealDomain () { super (floatManager); }
	
	/**
	 * use descriptor properties to build actual function
	 * @return the spline object spawned from this descriptor
	 */
	public Spline<Double> getSpline ()
	{
		setSpaceManager (floatManager);
		return new Spline<Double> (this);
	}

	/**
	 * treat segmented function as a simple function
	 * @return the object exposed only as a simple function
	 */
	public MultiDimensional.Function<Double>
	getFunction () { return getSpline (); }

}

