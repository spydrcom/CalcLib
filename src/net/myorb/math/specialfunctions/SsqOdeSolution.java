
package net.myorb.math.specialfunctions;

import net.myorb.math.computational.CommonSplineDescription;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import java.util.ArrayList;
import java.util.List;

/**
 * an attempt to demonstrate building a spline for a sequence of RK4 solution approximations.
 *  the ODE { f'(t, y) = y * sin^2 t } was approximated in 3 parts [0..0.7], [0.7..1.8], and [1.8..2.5].
 *  the y and yPrime exports of this class are the y(x) and y'(x) entries for calculations using the splines.
 *  see SinsqSplineTest.txt for a script running precision tests on this spline as a solution to the ODE.
 * @author Michael Druckman
 */
public class SsqOdeSolution extends CommonSplineDescription <Double>
{

	static Double KNOTS [];
	static List <Double[]> COEFFICIENTS;

	public static final ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();

	// Chebyshev T polynomial coefficients for the spline segments

	static
	{
		KNOTS = new Double[]{0.7, 1.8};						// 3 segments in this spline
		COEFFICIENTS = new ArrayList <Double[]> ();			// a set of coefficients per segment
		COEFFICIENTS.add (new Double[]{1.0895632513623186, 0.06458673051971418, 0.15079469060870548, -0.04408452845150149, 0.09012942243160649, -0.061718580754907645, 0.0382754190235348, -0.02165996485687309, 0.011233691573052042, -0.005141014206369713, 0.001953817275589121, -5.52796243397515E-4, 6.713219561584299E-5, 3.965241689906431E-5, -3.23093754205289E-5, 1.1254612821226888E-5, -2.58905266654319E-7, -2.346628776509874E-6, 1.7412651386351266E-6, -7.822626207138976E-7, 2.4008738828824375E-7, -4.268194244076562E-8, -2.5425012153379715E-9, 5.090640794785116E-9, -2.106564256724852E-9, 5.510138559438566E-10, -1.0078668403806705E-10, 1.2747929966072315E-11, -1.0299886700365168E-12, 4.3432759612355625E-14, -4.499249971361978E-16});
		COEFFICIENTS.add (new Double[]{1.1751568394368097, -0.08932001478582785, 0.2604359396940625, -0.10046189534930317, 0.10228490770675958, -0.04817559412954894, 0.016795785078464694, -0.003172773100032389, -6.100017368537193E-4, 8.843834891339355E-4, -4.870052202712487E-4, 1.9202103208705145E-4, -6.458280003964433E-5, 2.1703660877713915E-5, -7.764782920928923E-6, 2.62469559704386E-6, -7.036168107175357E-7, 1.233318399064999E-7, -6.381308769146205E-9, -3.164991098845108E-9, 1.0015495711720385E-9, -1.8924219065821584E-10, 6.038218456688127E-11, -2.617539178747955E-11, 8.282641688304383E-12, -1.7795186698149396E-12, 2.6404723801844675E-13, -2.6967105808630752E-14, 1.8226630487019603E-15, -7.370836064609921E-17, 1.3531028833933168E-18});
		COEFFICIENTS.add (new Double[]{5.755182382359214, -8.783346030710689, 7.304943526393039, -4.5256722588491485, 2.099629429316062, -0.6502335799732499, 0.1209753513155297, -0.00759903997121112, -0.0018139642059871244, 3.851219554589564E-4, 2.2309706775338646E-5, -3.60651052486111E-5, 1.5735076643193647E-5, -4.162499917882527E-6, 6.928762141058665E-7, -9.351112467763583E-8, 1.2368251649442404E-8, 4.71103423395488E-10, -6.169020365749427E-10, 6.861547978884875E-12, 4.4589347703290686E-11, -1.0832647172132336E-11, 9.315294353087516E-13, 8.452773487440045E-14, -4.4292574268773225E-14, 9.31290667555903E-15, -1.3116854602509917E-15, 1.247119771391984E-16, -7.572465397035449E-18, 2.639043663428038E-19, -4.009063042591142E-21});
	}

	protected SsqOdeSolution ()
	{ super (manager); buildFromArray (KNOTS, COEFFICIENTS); }
	private static SsqOdeSolution solution = new SsqOdeSolution ();

	/**
	 * evaluation of y(x)
	 * @param x the parameter to the function
	 * @return the function result
	 */
	public static double y (double x)
	{
		return solution.functionEval (x);
	}

	/**
	 * evaluation of y'(x)
	 * @param x the parameter to the derivative function
	 * @return the function result
	 */
	public static double yPrime (double x)
	{
		return solution.derivativeEval (x);
	}

}
