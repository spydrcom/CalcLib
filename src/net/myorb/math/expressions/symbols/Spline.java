
package net.myorb.math.expressions.symbols;

import net.myorb.math.*;
import net.myorb.math.expressions.*;
import net.myorb.math.computational.*;
import net.myorb.math.expressions.evaluationstates.*;
import net.myorb.math.polynomial.PolynomialCalculus;
import net.myorb.math.polynomial.families.ChebyshevPolynomial;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * describe a function defined by segments realized as interpolated polynomials
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Spline<T> extends SplineDescriptor<T>
{


	/**
	 * build a full Spline object from a descriptor
	 * @param descriptor the descriptor holding all properties for import
	 */
	public Spline (SplineDescriptor<T> descriptor)
	{
		super
		(
			descriptor.getName (), descriptor.getParameterNames (),
			new TokenParser.TokenSequence (), descriptor.getSpaceDescription ()
		);
		functionTokens.add (0, IMPORTED_SPLINE_TOKEN);
		sm = descriptor.getExpressionSpaceManager ();
		vm = new ValueManager<T> ();
		copy (descriptor);
	}


	/**
	 * declaration of a segmented function
	 * @param name the name assigned to the function
	 * @param parameterNames the names of the parameters from the declaration
	 * @param segmentNames the names of the segments from the declaration
	 * @param functionTokens the tokens that specified the declaration
	 * @param environment the central object store for this engine
	 */
	public Spline
		(
			String name,
			List<String> parameterNames,
			List<String> segmentNames,
			TokenParser.TokenSequence functionTokens,
			Environment<T> environment
		)
	{
		super
		(name, parameterNames, functionTokens, environment.getSpaceManager ());
		sm = environment.getSpaceManager (); vm = environment.getValueManager ();
		functionTokens.add (0, SPLINE_TOKEN); processSegments (segmentNames, environment);
	}
	protected ExpressionSpaceManager<T> sm;
	protected ValueManager<T> vm;



	/**
	 * evaluate the named symbols
	 * @param segmentNames the names specified in the function declatation
	 * @param environment the central object store for this engine
	 */
	@SuppressWarnings("unchecked")
	public void processSegments (List<String> segmentNames, Environment<T> environment)
	{
		Double[] constraintLo;
		Map <Double, SymbolMap.VariableLookup> segmentMap = new HashMap <Double, SymbolMap.VariableLookup> ();
		SymbolMap symbols = environment.getSymbolMap (); SymbolMap.VariableLookup variable;

		for (String name : segmentNames)
		{
			SymbolMap.Named segment = symbols.lookup (name);
			if (segment == null || !(segment instanceof SymbolMap.VariableLookup)) invalid (name, " is not a variable");
			ValueManager.GenericValue value = (variable = (SymbolMap.VariableLookup)segment).getValue ();
			if (value == null || !(value instanceof ValueManager.DimensionedValue))
			{ invalid (name, " does not represent an array"); }

			ValueManager.Metadata metadata = value.getMetadata ();
			if (metadata == null) invalid (name, " does not have metadata");
			if (!(metadata instanceof Arrays.ConstrainedDomain)) invalid (name, " does not have constraints");
			Arrays.ConstrainedDomain<T> constraints = (Arrays.ConstrainedDomain<T>)metadata;
			segmentMap.put (sm.convertToDouble (loPlus (constraints)), variable);
		}

		segments = new ArrayList<SymbolMap.VariableLookup>();
		java.util.Arrays.sort (constraintLo = segmentMap.keySet ().toArray (new Double[1]));
		for (Double lo : constraintLo) { segments.add (segmentMap.get (lo)); }
		validateSegments (environment);
	}
	protected List<SymbolMap.VariableLookup> segments;


	T loPlus (Arrays.ConstrainedDomain<T> constraints)
	{
		return sm.add (constraints.getLo (), constraints.getDelta ());
	}
	T hiMinus (Arrays.ConstrainedDomain<T> constraints)
	{
		return sm.add (constraints.getHi (), sm.negate (constraints.getDelta ()));
	}


	/**
	 * check the segment list for consistency
	 * @param environment the central object store for this engine
	 */
	public void validateSegments (Environment<T> environment)
	{
		Arrays.ConstrainedDomain<T>
		prev = getConstraints (segments.get (0));
		for (int i = 1; i < segments.size (); i++)
		{
			Arrays.ConstrainedDomain<T>
			next = getConstraints (segments.get (i));
			if (sm.lessThan (hiMinus (prev), loPlus (next)))
			{ invalid (segments.get (i).getName (), " segment found to have a gap"); }
			prev = next;
		}
		describeFunction (environment);
	}


	/**
	 * provide output describing the constructed segmented function
	 * @param environment the central object store for this engine
	 */
	public void describeFunction (Environment<T> environment)
	{
		SymbolMap.VariableLookup
		lowEnd = segments.get (0), hiEnd = segments.get (segments.size () - 1);
		T lo = loPlus (getConstraints (lowEnd)), hi = hiMinus (getConstraints (hiEnd));
		setHiConstraint (hi);

		PrintStream out = environment.getOutStream (); out.println ();
		out.println (name + " is found to be continuous on the interval " + lo + " to " + hi);
		out.println ();

		Polynomial<T> poly;
		Arrays.ConstrainedDomain<T> c;
		for (SymbolMap.VariableLookup s : segments)
		{
			poly = null;
			c = getConstraints (s);
			T l = loPlus (c), h = hiMinus (c);
			out.print ("\t" + s.getName () + "\t" + l + "\t" + h);
			if (c instanceof TransformConstraints)
			{
				poly = ((TransformConstraints<T>)c).getPolynomial ();
				if (poly instanceof ChebyshevPolynomial)
				{ out.print ("\tCHEBYSHEV"); }
			}
			addSegmentLoConstraint (l);
			addTransform (s, c, poly);
			out.println ();
		}
		out.println ();
	}


	/**
	 * keep a list of polynomial transform objects
	 *  parallel to the list of segment/constraint objects
	 * @param symbol the symbol holding the polynomial coefficients for this segment
	 * @param constraints the domain constraints applied to this segment
	 * @param poly the polynomial connected to transform constraints
	 */
	public void addTransform
		(
			SymbolMap.VariableLookup symbol,
			Arrays.ConstrainedDomain<T> constraints,
			Polynomial<T> poly
		)
	{
		ValueManager.GenericValue value = symbol.getValue ();
		Polynomial.Coefficients<T> c = new Polynomial.Coefficients<T>();
		c.addAll (vm.toArray (value));

		if (poly != null)
		{
			addSegmentPolynomial (poly, c);
		}
		else addSegmentPolynomial (c);
	}


	/**
	 * get the constraints of the specified segment
	 * @param variable the variable of interest in the symbol table
	 * @return the constraints from the metadata attached to the value assigned to this variable
	 */
	@SuppressWarnings("unchecked")
	public Arrays.ConstrainedDomain<T> getConstraints (SymbolMap.VariableLookup variable)
	{
		return (Arrays.ConstrainedDomain<T>)variable.getValue ().getMetadata ();
	}


	/**
	 * throw exception with an error message
	 * @param item the name of the item found to be in error
	 * @param problem description of the problem
	 */
	private void invalid (String item, String problem)
	{
		throw new RuntimeException (item + problem);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#f(java.util.List)
	 */
	public T f (List<T> parameterValues)
	{
		try
		{
			return call (parameterValues.get (0));
		}
		catch (Exception e)
		{
			if (supressingErrorMessages) return null;
			else throw new RuntimeException (e);
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#execute(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
	{
		T parameter = vm.toDiscrete (parameters);
		return vm.newDiscreteValue (call (parameter));
	}


	/**
	 * simplist unary function form
	 * @param x simpl discrete parameter
	 * @return simple discrete result
	 */
	public T call (T x)
	{
		T lo = getLoConstraint (), hi = getHiConstraint ();
		if (sm.lessThan (x, lo) || sm.lessThan (hi, x))
		{
			if (supressingErrorMessages) return null;
			invalid (sm.toDecimalString (x), " is not within defined function domain constraints");
		}
		return evaluateAt (x);
	}


	/**
	 * identify the segment that has a domain match for the parameter
	 * @param x the value of x at which the function is to be evaluated
	 * @return the ordinal value of the segment table entry that matches
	 */
	private int getSegmentNumber (T x)
	{
		List<T> lo = getSegmentLoConstraints ();
		for (int i = lo.size () - 1; i > 0; i--)
		{
			if (!sm.lessThan (x, lo.get (i))) return i;
		}
		return 0;
	}


	/**
	 * find correct segment and compute function value at parameter
	 * @param x the value of x at which the function is to be evaluated
	 * @return the value of the spline at x
	 */
	public T evaluateAt (T x) { return getSegmentTransformAt (x).eval (x); }


	/**
	 * identify the power function for a value of x
	 * @param x the value of x to use to locate the segment
	 * @return the power function object for the given segment
	 */
	public Polynomial.PowerFunction<T>  getSegmentTransformAt (T x)
	{ return getSegmentTransforms ().get (getSegmentNumber (x)); }


	/**
	 * compute function derivative using appropriate segment function
	 * @param x the value of x to be used for the evaluation
	 * @return the derivative value of the spline at x
	 */
	public T evaluateDerivativeAt (T x)
	{ return getPolynomialCalculus ().evaluatePolynomialDerivative (getSegmentTransformAt (x), x); }
	public PolynomialCalculus<T> getPolynomialCalculus () { return new PolynomialCalculus<T> (spaceManager); }


}

