
package net.myorb.math.specialfunctions;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevSplineFunction;

/**
 * spline calculation of Gamma function
 * @author Michael Druckman
 */
public class Gamma extends GammaBetween1and2
{


	public static final double GAMMA_HALF = Math.sqrt (Math.PI);		// GAMMA (0.5) = SQRT (PI)


	/**
	 * super class is spline for ( 1 .. 2 ) domain.
	 * this extends the domain to full real number domain.
	 * negative integer parameters throw exception for asymtote.
	 * large positive parameters will eventually generate overflows.
	 * no check is done for traps of overflows, JRE will catch it.
	 */
	public Gamma () {}


	/**
	 * use Clenshaw generating function
	 *  to evaluate Chebyshev polynomial for domain (1-2)
	 * @param x value of parameter within domain of (1-2)
	 * @return GAMMA(x) as calculated by polynomial
	 */
	public Double evalByPolynomial (Double x)
	{
		return super.eval (x);
	}


	/**
	 * for values 1 &lt;= x &lt;= 2
	 * @param x value of parameter within domain of [1-2]
	 * @return GAMMA(x) as calculated by spline or special case
	 */
	public Double evalInRange (Double x)
	{
		if (x == 1.5) return GAMMA_HALF / 2.0;
		if (x == 1 || x == 2) return 1.0;
		return evalByPolynomial (x);
	}


	/**
	 * recursive evaluation for x &gt; 1.5
	 * @param x value of function parameter
	 * @return value of function at parameter
	 */
	public Double gammaGt (Double x)
	{
		if (x > 2.0)
			return (x - 1) * gammaGt (x - 1);
		else return evalInRange (x);
	}


	/**
	 * recursive evaluation for x &lt; 1.5
	 * @param x value of function parameter
	 * @return value of function at parameter
	 */
	public Double gammaLt (Double x)
	{
		if (x == 0) asymptotic ();
		//else if (x == 0.5) return GAMMA_HALF;			// don't need to catch here, special case when InRange
		else if (x < 1) return gammaLt (x + 1) / x;		// x = 0.5 will cause InRange (1.5) / 0.5, why have extra check
		return evalInRange (x);							// gammaLt is recursive, so removal of check is more efficient
	}
	void asymptotic () { throw new RuntimeException ("Gamma asymptotic for Integer <= 0"); }


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#eval(java.lang.Object)
	 */
	public Double eval (Double x)
	{
		if (x > 2) return gammaGt (x);
		else if (x < 1) return gammaLt (x);
		else return evalInRange (x);
	}


}


/**
 * encapsulation of spline for Gamma in ( 1 .. 2 ) exclusive domain
 */
class GammaBetween1and2 extends ChebyshevSplineFunction <Double>
{

	/**
	 * Chebyshev polynomial coefficients for domain [ -1.5 .. +1.5 ]
	 * Spline was built with offset and tick multiples listed below
	 */
	public static final Double[]
			CHEBYSHEV_COEFFICIENTS = new Double[]
			{
				 0.9215830792537184,    -0.007524775656351972,    0.036544968378030784,   -0.002433157121185563, 
				 5.765567729276681E-4,  -6.889487495928968E-5,    1.0532195165658521E-5,  -1.4470533723360511E-6, 
				 2.0490494929880603E-7, -2.8639234476544337E-8,   4.0100993053284215E-9,  -5.605778136570527E-10, 
				 7.834322134571117E-11, -1.0945851816252082E-11,  1.5281149474704173E-12, -2.1366488662277518E-13, 
				 2.96954983498334E-14,  -4.153795269783326E-15,   9.878849727834096E-16,   3.140141017348394E-17, 
				 2.6522417513594616E-16, 2.4223086374062476E-17, -5.777054802460112E-17,  -5.7701079972431E-17, 
				-9.2814214437395E-17,    1.9943594001108802E-17,  4.166457085927932E-17,  -2.732882476780851E-18, 
				-6.295177000083922E-18,  1.3422214405813347E-19,  3.258243326013009E-19
			};
	public static final double SPLINE_BASE_EQUIVALENT = 0.86, TICK_MULTIPLE = 2.5;

	/**
	 * establish Chebyshev polynomial coefficients
	 *  collected by regression over domain 1:2
	 * spline will use Clenshaw to compute results
	 * requested in this domain
	 */
	public GammaBetween1and2 ()
	{
		super (CHEBYSHEV_COEFFICIENTS, CHEBYSHEV_SPLINE_BASE, TICK_MULTIPLE, manager);
		this.setShift (SPLINE_BASE_EQUIVALENT);
	}
	public static final ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();

}

