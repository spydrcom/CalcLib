
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


	public PolynomialSpline (SpaceManager <T> sm)
	{
		super (sm);
		this.psm = new PolynomialSpaceManager <T> (sm);
	}
	protected PolynomialSpaceManager <T> psm;


	/**
	 * change the computer object on each knot
	 */
	public void generatePolynomials ()
	{
		for (CubicSpline.Knot <T> k : this.getKnots ())
		{
			generatePolynomialfor (k);
		}
	}


	/**
	 * @param knot the knot object being changed
	 */
	public void generatePolynomialfor (CubicSpline.Knot <T> knot)
	{
		T H = knot.h ();
		if (H == null || sm.isZero (H)) return;

		HSQ = sm.multiply (H, H); SIX = sm.newScalar (6);
		T i6h = sm.invert (sm.multiply (SIX, H));

		PowerFunction <T>
			toKnot = psm.linearFunctionOfX
				(sm.newScalar (-1), knot.t ()),
			fromPrior = psm.linearFunctionOfX
				(sm.newScalar (1), sm.negate (knot.prior ().t ()));
		PowerFunction <T> sum =
			psm.add
			(
				psm.times (knot.z (), psm.pow (fromPrior, 3)),
				psm.times (knot.prior ().z (), psm.pow (toKnot, 3))
			);
		sum = plusProduct (knot, fromPrior, sum);
		sum = plusProduct (knot.prior (), toKnot, sum);
		knot.setComputer (psm.times (i6h, sum));
	}
	PowerFunction <T> plusProduct
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
	protected T HSQ, SIX;


}

