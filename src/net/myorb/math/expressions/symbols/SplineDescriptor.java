
package net.myorb.math.expressions.symbols;

import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.TokenParser;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

import java.util.ArrayList;
import java.util.List;

/**
 * describe a spline built from multiple polynomials
 * @author Michael Druckman
 */
public class SplineDescriptor<T> extends AbstractFunction<T>
{


	/**
	 * general constructor
	 *  for newly declared functions
	 * @param name the name to be given to the function
	 * @param parameterNames the names of the formal parameters in the declaration
	 * @param functionTokens the tokens that made up the declaration
	 * @param manager a space manager for the type
	 */
	public SplineDescriptor
		(
			String name, List<String> parameterNames,
			TokenParser.TokenSequence functionTokens,
			SpaceManager<T> manager
		)
	{
		super (name, parameterNames, functionTokens);
		setup (manager);
	}

	/**
	 * construct using common import parameters
	 * @param manager a space manager for the type
	 */
	public SplineDescriptor (SpaceManager<T> manager)
	{
		super ("", new ArrayList<String>(), new TokenParser.TokenSequence());
		parameterNames.add ("x"); functionTokens.add (IMPORTED_SPLINE_TOKEN);
		setup (manager);
	}
	public static final TokenParser.TokenDescriptor 
	IMPORTED_SPLINE_TOKEN = TokenParser.make ("IMPORTED-SPLINE"),
	SPLINE_TOKEN = TokenParser.make ("SPLINE");

	/**
	 * initialize lists for constraints and coefficients; also a polynomial management object
	 * @param manager the space manager is used to initialize the polynomial manager
	 */
	private void setup (SpaceManager<T> manager)
	{
		segmentLoConstraints = new ArrayList<T> ();
		segmentTransforms = new ArrayList<Polynomial.PowerFunction<T>> ();
		cp = new ChebyshevPolynomialCalculus<T> (manager);
		p = new OrdinaryPolynomialCalculus<T> (manager);
		initialize ();
	}
	protected ChebyshevPolynomialCalculus<T> cp;
	protected OrdinaryPolynomialCalculus<T> p;


	/**
	 * abstract method intended to be overridden in subclass.
	 * allows local properties to be fed from code generation macro.
	 */
	protected void initialize () {}


	/**
	 * the lo constraints identify which segment
	 *  is used to compute the function at a given value
	 * @param lo the lo constraint values
	 */
	@SuppressWarnings("unchecked")
	public void setSegmentLoConstraints (T... lo)
	{
		for (T l : lo) addSegmentLoConstraint (l);
	}


	/**
	 * the fist added segment has the lo for the function constraint
	 * @return the lo value from the first segment
	 */
	public T getLoConstraint ()
	{ return segmentLoConstraints.get (0); }


	/**
	 * the segments must be added in domain order
	 * @param lo the lo value for this segment constraint
	 */
	public void addSegmentLoConstraint (T lo)
	{ segmentLoConstraints.add (lo); }



	/**
	 * compile a coefficients object
	 * @param coefficients the values to compile
	 * @return the compiled object
	 */
	@SuppressWarnings("unchecked")
	public Polynomial.Coefficients<T> compile (T... coefficients)
	{
		Polynomial.Coefficients<T>
		polynomialCoefficients = new Polynomial.Coefficients<T>();
		for (T coefficient : coefficients) polynomialCoefficients.add (coefficient);
		return polynomialCoefficients;
	}


	/**
	 * each segment has a polynomial
	 *  that computes velues in that interval.
	 *  p is the default ordinary polynomial object, cp default for Chebyshev
	 * @param coefficients the coefficients for the polynomial
	 */
	@SuppressWarnings("unchecked")
	public void addSegmentPolynomial (T... coefficients)
	{
		addSegmentPolynomial (p, compile (coefficients));
	}
	@SuppressWarnings("unchecked")
	public void addSegmentOrdinaryPolynomialCalculus (T... coefficients)
	{
		addSegmentPolynomial (p, compile (coefficients));
	}
	@SuppressWarnings("unchecked")
	public void addSegmentChebyshevPolynomial (T... coefficients)
	{
		addSegmentPolynomial (cp, compile (coefficients));
	}
	public void addSegmentPolynomial (Polynomial.Coefficients<T> polynomialCoefficients)
	{
		addSegmentPolynomial (p, polynomialCoefficients);
	}


	/**
	 * add a polynomial as the transform for a segment
	 * @param polynomial the polynomial object that evaluates that segment
	 * @param polynomialCoefficients the coefficients for that polynomial
	 */
	public void addSegmentPolynomial
	(Polynomial<T> polynomial, Polynomial.Coefficients<T> polynomialCoefficients)
	{ segmentTransforms.add (polynomial.getPolynomialFunction (polynomialCoefficients)); }


	public T getHiConstraint() { return hiConstraint; }
	public List<T> getSegmentLoConstraints() { return segmentLoConstraints; }
	public void setHiConstraint(T hiConstraint) { this.hiConstraint = hiConstraint; }
	public void setSegmentLoConstraints(List<T> segmentLoConstraints) { this.segmentLoConstraints = segmentLoConstraints; }

	public void setSegmentTransforms (List<Polynomial.PowerFunction<T>> segmentTransforms) { this.segmentTransforms = segmentTransforms; }
	public List<Polynomial.PowerFunction<T>> getSegmentTransforms () { return segmentTransforms; }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#execute(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public GenericValue execute(GenericValue parameters) { return null; }


	/**
	 * copy descriptor to actual function for computation
	 * @param descriptor the source descriptor
	 */
	public void copy (SplineDescriptor<T> descriptor)
	{
		this.segmentTransforms = descriptor.segmentTransforms;
		this.segmentLoConstraints = descriptor.segmentLoConstraints;
		this.hiConstraint = descriptor.hiConstraint;
	}
	private List<Polynomial.PowerFunction<T>> segmentTransforms;
	private List<T> segmentLoConstraints;
	private T hiConstraint;


}
