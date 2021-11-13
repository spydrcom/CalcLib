
package net.myorb.math.computational;

import net.myorb.math.expressions.tree.Profile;
import net.myorb.math.expressions.tree.JsonBinding;
import net.myorb.math.expressions.tree.JsonRestore;
import net.myorb.math.expressions.tree.Element;

import net.myorb.math.expressions.symbols.FunctionWrapper;
import net.myorb.math.expressions.evaluationstates.Primitives;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.TokenParser;

import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevSplineFunction;

import net.myorb.math.polynomial.RepresentationTools;
import net.myorb.math.GeneratingFunctions.Coefficients;
import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonPrettyPrinter;
import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.notations.json.JsonTools;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.*;

/**
 * base class for spline boiler-plate.
 *  coefficients are assumed to be Chebyshev T polynomials.
 * @param <T> the data type manager
 * @author Michael Druckman
 */
public class CommonSplineDescription <T>
	implements Element, JsonBinding.JsonRepresentation <T>
{

	// the calculus object for manipulation of the polynomials

	protected ChebyshevPolynomialCalculus <T> calculus;

	/**
	 * for direct use only by JSON restore
	 * @param mgr data type manager
	 */
	protected CommonSplineDescription (SpaceManager <T> mgr)
	{
		this.mgr = (ExpressionSpaceManager <T>) mgr;
		this.calculus = new ChebyshevPolynomialCalculus <T> (mgr);
	}
	protected ExpressionSpaceManager <T> mgr;

	/**
	 * a description of a segment
	 */
	public class Segment
	{
		Segment (Number [] coefficients)
		{
			this (RepresentationTools.toList (coefficients, mgr));
		}
		Segment (T [] coefficients)
		{
			this (RepresentationTools.toList (coefficients));
		}
		Segment (List <T> coefficients)
		{
			this (new Coefficients<T> (coefficients));
		}
		Segment (Coefficients <T> coefficients)
		{
			Coefficients <T>
			f = new Coefficients <> (coefficients),
			fPrime = calculus.getFirstKindDerivative (f),
			secondPrime = calculus.getFirstKindDerivative (fPrime);
			secondDerivative = new ChebyshevSplineFunction <> (secondPrime, mgr);
			derivative = new ChebyshevSplineFunction <> (fPrime, mgr);
			function = new ChebyshevSplineFunction <> (f, mgr);
		}
		ChebyshevSplineFunction <T> function, derivative, secondDerivative;
	}

	/**
	 * coefficients from containers used to build segments
	 * @param <C> coefficient container type
	 */
	protected abstract class SplineFactory <C>
	{

		/**
		 * add a segment with these coefficients
		 * @param c the coefficients for a segment
		 */
		public abstract void addSegmentFor (C c);

		/**
		 * build a segment list from these lists
		 * @param knots the array of knot values to use defining the segment boundaries
		 * @param coefficientList a list holding a coefficient set per segment
		 */
		public void buildSegmentList (Double knots [], List < C > coefficientList)
		{
			for (int i = 0; i < segmentCountFrom (knots, coefficientList); i++)
			{ addSegmentFor (coefficientList.get (i)); }
		}

	}
	protected List <Segment> segments = new ArrayList <> ();

	/**
	 * factory for array of values as coefficient container
	 */
	protected class ArraySplineFactory extends SplineFactory < T [] >
	{ public void addSegmentFor (T [] c) { segments.add (new Segment (c)); } }
	protected void buildFromArray (Double knots [], List < T [] > coefficientList)
	{ new ArraySplineFactory ().buildSegmentList (knots, coefficientList); }

	/**
	 * factory for Coefficient object as coefficient container
	 */
	protected class ListSplineFactory extends SplineFactory < Coefficients <T> >
	{ public void addSegmentFor (Coefficients <T> c) { segments.add (new Segment (c)); } }
	protected void buildFromList (Double knots [], List < Coefficients <T> > coefficientList)
	{ new ListSplineFactory ().buildSegmentList (knots, coefficientList); }

	/**
	 * each set of coefficients is used to build a segment
	 * @param knots the values of the knots in the spline which indicate the segment breaks
	 * @param coefficientList the list of coefficients per segment
	 * @param mgr a manager for the data type
	 * @return the configured spline
	 * @param <T> the type manager
	 */
	public static <T> CommonSplineDescription <T> buildSegmentListFromCoefficients
		(Double knots [], List <Coefficients <T>> coefficientList, SpaceManager <T> mgr)
	{
		CommonSplineDescription <T> spline = new CommonSplineDescription <T> (mgr);
		spline.buildFromList (knots, coefficientList);
		return spline;
	}

	/**
	 * each set of coefficients is used to build a segment
	 * @param knots the values of the knots in the spline which indicate the segment breaks
	 * @param coefficientList the array of coefficients per segment
	 * @param mgr a manager for the data type
	 * @return the configured spline
	 * @param <T> the type manager
	 */
	public static <T> CommonSplineDescription <T> buildSegmentListFromArray
		(Double knots [], List < T [] > coefficientList, SpaceManager <T> mgr)
	{
		CommonSplineDescription <T> spline = new CommonSplineDescription <T> (mgr);
		spline.buildFromArray (knots, coefficientList);
		return spline;
	}

	/**
	 * compute the expected count of segments for a spline
	 * @param knots the array of knot values for use in the spline
	 * @param coefficientList a list of coefficients per segment
	 * @return the expected count of segments
	 */
	public int segmentCountFrom (Double knots [], List <?> coefficientList)
	{
		// a segment for each knot plus one additional
		int segmentCount = (this.knots = knots).length + 1;
		if (coefficientList.size () != segmentCount) throw new RuntimeException (SEGERR);
		return segmentCount;
	}
	public static final String SEGERR = "Segment count not consistent with specified list of knots";

	/**
	 * find the knot associated with the segment for this domain value
	 * @param x the domain parameter to the spline to be associated with a knot range
	 * @return the segment to use for this domain value
	 */
	public int segment (T x)
	{
		double xValue = mgr.toNumber (x).doubleValue ();
		for (int i = 0; i < knots.length; i++) { if (xValue < knots[i]) return i; }
		return knots.length;
	}
	protected Double knots [];

	/**
	 * evaluation of f(x)
	 * @param x the parameter to the function
	 * @return the function result
	 */
	public T functionEval (T x)
	{
		return segments.get (segment (x)).function.eval (x);
	}

	/**
	 * evaluation of f'(x)
	 * @param x the parameter to the derivative function
	 * @return the function result
	 */
	public T derivativeEval (T x)
	{
		return segments.get (segment (x)).derivative.eval (x);
	}

	/**
	 * evaluation of f''(x)
	 * @param x the parameter to the derivative function
	 * @return the function result
	 */
	public T secondDerivativeEval (T x)
	{
		return segments.get (segment (x)).secondDerivative.eval (x);
	}

	/**
	 * entries exported as functions
	 */
	public class ChebyshevSpline implements Function <T>, JsonBinding.JsonRepresentation <T>
	{
		@Override public T eval (T x) { return functionEval (x); }
		@Override public SpaceManager <T> getSpaceManager () { return mgr; }
		@Override public SpaceDescription<T> getSpaceDescription () { return mgr; }
		@Override public JsonValue toJson () { return CommonSplineDescription.this.toJson (); }
		@Override public Element fromJson (JsonValue context, JsonRestore <T> restoreManager) throws Exception
		{ return CommonSplineDescription.this.fromJson (context, restoreManager); }
	}
	public class SplineFirstDerivative extends ChebyshevSpline
	{
		@Override public T eval (T x) { return derivativeEval (x); }
	}
	public class SplineSecondDerivative extends ChebyshevSpline
	{
		@Override public T eval (T x) { return secondDerivativeEval (x); }
	}

	/*
	 * access to spline functions
	 */

	public Function <T> getFunction () { return new ChebyshevSpline (); }
	public Function <T> getFirstDerivative () { return new SplineFirstDerivative (); }
	public Function <T> getSecondDerivative () { return new SplineSecondDerivative (); }

	/**
	 * add function and derivatives to symbol table
	 * @param functionName the name for the function declaration
	 * @param parameterName the name of the function parameter
	 * @param environment access to utility objects
	 */
	@SuppressWarnings ({"rawtypes","unchecked"}) public void postSymbols
			(String functionName, String parameterName, Primitives environment)
	{
		environment.processDefinedFunction
		(
			new FunctionWrapper <> (functionName, parameterName, FUNCTION_SPLINE, getFunction ())
		);
		environment.processDefinedFunction
		(
			new FunctionWrapper <> (functionName+PRIME, parameterName, FUNCTION_DERIVATIVE, getFirstDerivative ())
		);
		environment.processDefinedFunction
		(
			new FunctionWrapper <> (functionName+DPRIME, parameterName, SECOND_DERIVATIVE, getSecondDerivative ())
		);
	}
	public static final TokenParser.TokenSequence
		FUNCTION_SPLINE = FunctionWrapper.tokensFor ("Function Spline"),
		SECOND_DERIVATIVE = FunctionWrapper.tokensFor ("Second Derivative Spline"),
		FUNCTION_DERIVATIVE = FunctionWrapper.tokensFor ("Function Derivative Spline");
	public static final String PRIME = OperatorNomenclature.PRIME_OPERATOR, DPRIME = OperatorNomenclature.DPRIME_OPERATOR;

	/**
	 * display contents of spline
	 */
	public void format ()
	{
		System.out.print ("Spline Knots: ");
		for (double knot : knots)
		{
			System.out.print (knot);
			System.out.print ("; ");
		}
		System.out.println ();

		for (Segment s : segments)
		{
			System.out.println (s.function.getCoefficients ());
			s.function.format ();
		}

		try { JsonPrettyPrinter.sendTo (format ("f", "x"), System.out); }
		catch (Exception e) { e.printStackTrace(); }
	}

	/**
	 * describe spline in JSON node
	 */
	public class SplineNode extends JsonBinding.Node
	{

		public SplineNode () { super (JsonBinding.NodeTypes.Spline); }

		public void setProfileMembers (String name, String parameterName)
		{
			Profile.ParameterList parameters =
				new Profile.ParameterList (parameterName);
			Profile.addProfileMembers (name, parameters, this);
		}

		/**
		 * format Segments member
		 */
		public void addSegments ()
		{
			JsonSemantics.JsonArray segmentArray = new JsonSemantics.JsonArray ();

			for (Segment s : segments)
			{
				segmentArray.add (JsonTools.toJsonArrayUsing (s.function.getCoefficients ()));
			}

			addMemberNamed ("Segments", segmentArray);
		}

		/**
		 * format Knots member
		 */
		public void addKnots ()
		{
			addMemberNamed ("Knots", JsonTools.toJsonArray (knots));
		}

	}

	/**
	 * @param name the name of the function
	 * @param parameterName the name of the function parameter
	 * @return the JSON tree root node
	 */
	public JsonBinding.Node format (String name, String parameterName)
	{
		SplineNode node = new SplineNode ();
		node.setProfileMembers (name, parameterName);
		node.addKnots (); node.addSegments ();
		return node;
	}

	/**
	 * build the segment list from JSON matrix of coefficients
	 * @param segmentArray JSON array of array of coefficients
	 */
	void toSegments (JsonSemantics.JsonArray segmentArray)
	{
		for (JsonValue value : segmentArray)
		{
			segments.add (new Segment (JsonTools.toNumberArray (value)));
		}
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#toJson()
	 */
	@Override public JsonValue toJson ()
	{
		SplineNode node = new SplineNode ();
		node.addKnots (); node.addSegments ();
		return node;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation#fromJson(net.myorb.data.notations.json.JsonLowLevel.JsonValue, net.myorb.math.expressions.tree.JsonRestore)
	 */
	@Override public Element fromJson
	(JsonValue context, JsonRestore<T> restoreManager)
	throws Exception
	{
		JsonBinding.Node node = new JsonBinding.Node (context);
		toSegments (JsonTools.toArray (node.getMember ("Segments")));
		knots = JsonTools.toDoubleArray (node.getMember ("Knots"));
		return this;
	}

	/**
	 * restore spline from JSON
	 * @param mgr data type manager
	 * @param context the JSON object tree
	 * @param restoreManager the JSON management object
	 * @return the new spline object with data populated
	 * @throws Exception for restore errors
	 * @param <T> data type
	 */
	public static <T> CommonSplineDescription <T> restoreFormJson
	(SpaceManager <T> mgr, JsonValue context, JsonRestore <T> restoreManager)
	throws Exception
	{
		CommonSplineDescription <T> spline = new CommonSplineDescription <T> (mgr);
		spline.fromJson (context, restoreManager);
		return spline;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.Element#isOfType(net.myorb.math.expressions.tree.Element.Types)
	 */
	@Override public boolean isOfType (Types type) { return type == Types.INVOCATION; }
	@Override public Types getElementType () { return Types.INVOCATION; }

}
