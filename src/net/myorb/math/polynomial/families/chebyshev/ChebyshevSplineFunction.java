
package net.myorb.math.polynomial.families.chebyshev;

import net.myorb.math.polynomial.RepresentationTools;
import net.myorb.math.polynomial.families.ChebyshevPolynomial;

import net.myorb.math.expressions.tree.Element;
import net.myorb.math.expressions.tree.JsonBinding;
import net.myorb.math.expressions.tree.JsonRestore;
import net.myorb.math.expressions.tree.Profile;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonPrettyPrinter;
import net.myorb.data.notations.json.JsonTools;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * function wrapper for Chebyshev spline functions
 * @param <T> the data type manager
 * @author Michael Druckman
 */
public class ChebyshevSplineFunction <T> extends ChebyshevPolynomial <T>
		implements Function <T>, Element, JsonBinding.JsonRepresentation <T>
{

	/**
	 * standard spline with no offset or multiple
	 * @param coefficients the Chebyshev coefficients
	 * @param manager data type manager
	 */
	public ChebyshevSplineFunction
		(T [] coefficients, SpaceManager <T> manager)
	{ this (coefficients, manager.getZero (), manager.getOne (), manager); }
	public ChebyshevSplineFunction (Coefficients <T> coefficients, SpaceManager <T> manager)
	{ this (coefficients, manager.getZero (), manager.getOne (), manager); }

	/**
	 * axis shift and tick multiplier
	 * @param coefficients the Chebyshev coefficients
	 * @param offset the base offset from zero for the mapping
	 * @param multiplier treated as a tick multiple
	 * @param manager data type manager
	 */
	public ChebyshevSplineFunction
	(T [] coefficients, T offset, T multiplier, SpaceManager <T> manager)
	{
		this (manager);
		this.shift = forValue (0);
		GeneratingFunctions<T> gf = new GeneratingFunctions <T> (manager);
		this.gfcoefficients = gf.toCoefficients (coefficients);
		this.multiplier = forValue (multiplier);
		this.offset = forValue (offset);
	}

	public ChebyshevSplineFunction
	(Coefficients <T> coefficients, T offset, T multiplier, SpaceManager <T> manager)
	{
		this (manager);
		this.shift = forValue (0);
		this.gfcoefficients = coefficients;
		this.multiplier = forValue (multiplier);
		this.offset = forValue (offset);
	}

	/**
	 * domain expansion factor version
	 * @param coefficients the Chebyshev coefficients
	 * @param multiplier used to expand the domain width
	 * @param manager data type manager
	 */
	public ChebyshevSplineFunction (T [] coefficients, int multiplier, SpaceManager <T> manager)
	{
		this (coefficients, manager.getZero (), manager.invert (manager.newScalar (multiplier)), manager);
	}
	protected ChebyshevSplineFunction (SpaceManager <T> manager) { super (manager); }

	/**
	 * @param shift x-axis shift value to use
	 */
	public void setShift (T shift) { this.shift = forValue (shift).negate (); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	@Override
	public T eval (T x)
	{
		Value<T> shifted = forValue (x).plus (shift);
		Value<T> v = shifted.times (multiplier).plus (offset);
		Value<T> result = evaluatePolynomialV (gfcoefficients, v);
		return result.getUnderlying ();
	}
	public static final double CHEBYSHEV_SPLINE_BASE = -1.5;
	public GeneratingFunctions.Coefficients <T> getCoefficients () { return gfcoefficients; }
	GeneratingFunctions.Coefficients <T> gfcoefficients;
	Value <T> offset, multiplier, shift;

	/**
	 * describe segment in JSON node
	 */
	public class SegmentNode extends JsonBinding.Node
	{

		public SegmentNode () { super (JsonBinding.NodeTypes.Segment); }

		public void setProfileMembers (String name, String parameterName)
		{
			Profile.ParameterList parameters =
				new Profile.ParameterList (parameterName);
			Profile.addProfileMembers (name, parameters, this);
		}

		public void addCoefficients ()
		{
			addMemberNamed ("Coefficients", JsonTools.toJsonArrayUsing (gfcoefficients));
		}

	}

	/**
	 * @param name the setting for name parameter
	 * @param parameterName the setting for parameter name
	 * @return a JSON node for this segment
	 */
	public JsonBinding.Node format (String name, String parameterName)
	{
		SegmentNode node = new SegmentNode ();
		node.setProfileMembers (name, parameterName);
		node.addCoefficients ();
		return node;
	}

	/**
	 * display to sys out
	 */
	public void format ()
	{
		try { JsonPrettyPrinter.sendTo (format ("f", "x"), System.out); }
		catch (Exception e) { e.printStackTrace (); }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#toJson()
	 */
	@Override public JsonValue toJson ()
	{
		SegmentNode node = new SegmentNode ();
		node.addCoefficients ();
		return node;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#fromJson(net.myorb.data.notations.json.JsonLowLevel.JsonValue, net.myorb.math.expressions.tree.JsonRestore)
	 */
	@Override public Element fromJson
	(JsonValue context, JsonRestore <T> restoreManager)
	throws Exception
	{
		gfcoefficients = new Coefficients <T> ();
		RepresentationTools.loadCoefficients
		(
			gfcoefficients, new JsonBinding.Node (context),
			restoreManager.getExpressionSpaceManager ()
		);
		return this;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.Element#isOfType(net.myorb.math.expressions.tree.Element.Types)
	 */
	@Override public boolean isOfType (Types type) { return type == Types.INVOCATION; }
	@Override public Types getElementType () { return Types.INVOCATION; }

}
