
package net.myorb.math.expressions.algorithms;

import net.myorb.math.primenumbers.Distribution;

import net.myorb.math.primenumbers.FactorizationSpecificFunctions;
import net.myorb.math.primenumbers.FactorizationPrimitives;
import net.myorb.math.primenumbers.Factorization;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ValueManager;


/**
 * implementations of algorithms specific to factored values
 * @author Michael Druckman
 */
public class FactorizationOverrides extends AlgorithmCore <Factorization>
{


	public FactorizationOverrides (Environment <Factorization> environment)
	{
		super (environment);
		this.abstractions = new PrimeFormulas (environment);
		this.functions = new FactorizationSpecificFunctions (environment);
		this.helpers = this.functions;
	}
	protected FactorizationSpecificFunctions functions = null;
	protected FactorizationPrimitives helpers = null;
	protected PrimeFormulas abstractions = null;


	/**
	 * POW operator ^
	 * @param left parameter left of operator
	 * @param right parameter right of operator
	 * @return array of left^right
	 */
	public ValueManager.GenericValue pow
	(ValueManager.GenericValue left, ValueManager.GenericValue right)
	{
		return helpers.processFactoredBinary ( left, right, (l, r) -> functions.pow (l, r) );
	}


	/**
	 * get LCM of integer values
	 * @param parameters stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue lcm (ValueManager.GenericValue parameters)
	{
		return helpers.processFactoredBinary ( parameters, (x, y) -> Distribution.LCM (x, y) );
	}


	/**
	 * get GCF of integer values
	 * @param parameters stack constructed parameter object
	 * @return the computed result
	 */
	public ValueManager.GenericValue gcf (ValueManager.GenericValue parameters)
	{
		return helpers.processFactoredBinary ( parameters, (x, y) -> Distribution.GCF (x, y) );
	}


	/**
	 * get floor of factored value
	 * @param parameters stack constructed parameter objects
	 * @return the computed result
	 */
	public ValueManager.GenericValue floor (ValueManager.GenericValue parameters)
	{
		return helpers.process ( parameters, (x, y) -> functions.floor (x, y) );
	}


	/**
	 * get ceiling of factored value
	 * @param parameters stack constructed parameter objects
	 * @return the computed result
	 */
	public ValueManager.GenericValue ceil (ValueManager.GenericValue parameters)
	{
		return helpers.process ( parameters, (x, y) -> functions.ceil (x, y) );
	}


	/**
	 * get rounded result of factored value
	 * @param parameters stack constructed parameter objects
	 * @return the computed result
	 */
	public ValueManager.GenericValue round (ValueManager.GenericValue parameters)
	{
		return helpers.process ( parameters, (x, y) -> functions.round (x, y) );
	}


}

