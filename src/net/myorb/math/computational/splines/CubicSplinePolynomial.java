
package net.myorb.math.computational.splines;

import net.myorb.math.Polynomial.PowerFunction;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.data.abstractions.Function;
import net.myorb.math.SpaceManager;

import java.util.List;

/**
 * Interpolating Cubic Spline with Polynomial representation
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class CubicSplinePolynomial <T>
	extends CubicSpline <T>
{

	public CubicSplinePolynomial (SpaceManager <T> sm) { super (sm); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.CubicSpline#interpolationFor(net.myorb.data.abstractions.Function, java.util.List, java.lang.Object)
	 */
	public Interpolation <T> interpolationFor
		(Function <T> f, List <T> knotPoints, T delta)
	{
		PolynomialSpline <T> spline = new PolynomialSpline <T> (sm);
		spline.interpolate (f, knotPoints, delta);
		spline.generatePolynomials ();
		return spline;
	}

}


/**
 * extended version of Spline supporting use of Polynomial.PowerFunction;
 *  cubic equation is evaluated as true polynomial with exposed coefficients
 * @param <T> type of component values on which operations are to be executed
 */
class PolynomialSpline <T> extends Spline <T>
{


	public PolynomialSpline (SpaceManager <T> sm) { super (sm); }


	/**
	 * change the computer object on each knot
	 */
	public void generatePolynomials ()
	{
		prepareScalarConstants ();
		preparePolynomialManager ();
		for (CubicSpline.Knot <T> k : this.getKnots ())
		{ generatePolynomialfor (k); }
	}


	/**
	 * @param knot the knot object being changed
	 */
	public void generatePolynomialfor (CubicSpline.Knot <T> knot)
	{
		/*
		 * the initial knot of a sequence will be empty
		 */
		if (isNonNullRun (knot.h ()))
		{
			/*
			 * following knots will have non-zero run values,
			 * SO using 6H as the common denominator suggests
			 * zero H values must be avoided, hence this test
			 */
			knot.setComputer (sumOfTerms (knot));
		}
	}


	/**
	 * construct a polynomial description of a knot
	 * @param knot the knot object being treated as a polynomial
	 * @return a description of the polynomial for the knot
	 */
	public PowerFunction <T> sumOfTerms (CubicSpline.Knot <T> knot)
	{
		PowerFunction <T>
			fromPrior = psm.linearFunctionOfX
				(ONE, sm.negate (knot.prior ().t ())),
			toKnot = psm.linearFunctionOfX (MINUS_ONE, knot.t ());
		PowerFunction <T> sum =
			psm.add
			(
				psm.times (knot.z (), psm.pow (fromPrior, 3)),
				psm.times (knot.prior ().z (), psm.pow (toKnot, 3))
			);
		sum = plusProductTerm (knot, fromPrior, sum);
		sum = plusProductTerm (knot.prior (), toKnot, sum);
		return psm.times (sm.invert (SIXH), sum);
	}


	/**
	 * algebra applied to terms to force 
	 *  common denominator of 6H across sums
	 * @param knot the description of the knot
	 * @param f the function part of the term being built
	 * @param sum the sum of terms built so far
	 * @return the sum including the new term
	 */
	public PowerFunction <T> plusProductTerm
		(
			CubicSpline.Knot <T> knot,
			PowerFunction <T> f, PowerFunction <T> sum
		)
	{
		T f6 = sm.multiply (knot.f (), SIX);
		T zh2 = sm.multiply (knot.z (), HSQ);
		T dif = sm.add (f6, sm.negate (zh2));
		return psm.add (sum, psm.times (dif, f));
	}


	/**
	 * the initial knot 
	 *  of a sequence will have a null prior node
	 * @param h the value of the run since prior knot
	 * @return TRUE when knot not empty, FALSE otherwise
	 */
	public boolean isNonNullRun (T h)
	{
		if (h != null && ! sm.isZero (h))
		{
			computeKnotConstants (h);
			return true;
		}
		return false;
	}


	/**
	 * compute HSQ and SIXH for a knot.
	 *  SIXH is 6h to be used as denominator and HSQ is H^2
	 * @param h value of run since prior knot
	 */
	public void computeKnotConstants (T h)
	{
		this.HSQ = sm.multiply (h, h);		// square of h, H^2
		this.SIXH = sm.multiply (SIX, h);	// product 6 * h
	}
	protected T HSQ, SIXH;


	/**
	 * simple scalar constants
	 */
	public void prepareScalarConstants ()
	{
		this.SIX = sm.newScalar (6);		// simple scalar 6
		this.MINUS_ONE = sm.newScalar (-1);	// negative one
		this.ONE = sm.newScalar (1);
	}
	protected T SIX, ONE, MINUS_ONE;


	/**
	 * allocate Polynomial Manager object
	 */
	public void preparePolynomialManager ()
	{ this.psm = new PolynomialSpaceManager <T> (sm); }
	protected PolynomialSpaceManager <T> psm;


}

