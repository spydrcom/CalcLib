
package net.myorb.math.polynomial;

import net.myorb.math.expressions.tree.Element;
import net.myorb.math.expressions.tree.JsonBinding;
import net.myorb.math.expressions.tree.JsonRestore;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonTools;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * a wrapper for holding a polynomial evaluation object combined with a set of coefficients
 * @param <T> type of component values on which operations are to be executed
 */
public class PolynomialFunctionWrapper <T>
		implements Polynomial.PowerFunction <T>, JsonBinding.JsonRepresentation <T>
{

	/* (non-Javadoc)
	* @see net.myorb.math.Function#f(java.lang.Object)
	*/
	public T eval (T x)
	{
		return polynomial.evaluatePolynomial (coefficients, x);
	}

	/* (non-Javadoc)
	* @see net.myorb.math.Polynomial.PowerFunction#getPolynomialSpaceManager()
	*/
	public PolynomialSpaceManager<T> getPolynomialSpaceManager ()
	{
		return polynomial.getPolynomialSpaceManager ();
	}

	/* (non-Javadoc)
	* @see java.lang.Object#toString()
	*/
	public String toString ()
	{
		return getPolynomialSpaceManager ().toString (this);
	}

	/* (non-Javadoc)
	* @see net.myorb.math.Polynomial.PolynomialFunction#getDegree()
	*/
	public int getDegree () { return coefficients.size () - 1; }


	/*
	* controlled properties
	*/

	/* (non-Javadoc)
	* @see net.myorb.math.Function#getSpaceManager()
	*/
	public SpaceManager<T> getSpaceDescription () { return manager; }
	public SpaceManager<T> getSpaceManager () { return manager; }
	protected SpaceManager<T> manager;

	/* (non-Javadoc)
	* @see net.myorb.math.Polynomial.PolynomialFunction#getPolynomial()
	*/
	public Polynomial<T>
	getPolynomial () { return polynomial; }
	protected Polynomial<T> polynomial;

	/* (non-Javadoc)
	* @see net.myorb.math.Polynomial.PolynomialFunction#getCoefficients()
	*/
	public Polynomial.Coefficients <T>
	getCoefficients () { return coefficients; }
	protected Polynomial.Coefficients <T> coefficients;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#toJson()
	 */
	@Override public JsonValue toJson ()
	{
		JsonBinding.Node node = new JsonBinding.Node (JsonBinding.NodeTypes.Segment);
		node.addMemberNamed ("Coefficients", JsonTools.toJsonArrayUsing (getCoefficients ()));
		return node;
	}

	@Override public Element fromJson
	(JsonValue context, JsonRestore<T> restoreManager)
	throws Exception { return null; }


	/*
	* constructor establishing property values
	*/
	
	/**
	* wrapper bundles a set of coefficients with a polynomial evaluation object
	* @param polynomial access to a polynomial evaluation object for functions of this type
	* @param coefficients a polynomial Coefficients list
	* @param manager a manager for the type
	*/
	public PolynomialFunctionWrapper
	(
			Polynomial<T> polynomial,
			Polynomial.Coefficients<T> coefficients,
			SpaceManager<T> manager
	)
	{
		this.polynomial = polynomial;
		this.coefficients = coefficients;
		this.manager = manager;
	}


}

