
package net.myorb.math.specialfunctions;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevSplineFunction;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.specialfunctions.bessel.*;
import net.myorb.math.Function;

/**
 * support for describing Airy (Ai and Bi) functions
 * @author Michael Druckman
 */
public class AiryFunctions
{

	public static final ExpressionFloatingFieldManager manager = new ExpressionFloatingFieldManager ();


	public static final double
	THREE_HALVES = 3.0 / 2.0, TWO_THIRDS = 1 / THREE_HALVES,
	QUARTER = 1.0 / 4.0, QUARTER_PI = QUARTER * Math.PI,
	ROOT_PI = Math.sqrt (Math.PI);


	/**
	 * describe the segments of the spline, each to be computed with different algorithm
	 */
	public enum Segment {NEG_BELOW, NEG_ABOVE, EXACT_ZERO, POS_BELOW, POS_ABOVE, ILLEGAL}


	/**
	 * map domain value to segment
	 * @param x parameter to function in real domain
	 * @return the segment identifier for the parameter
	 */
	public static Segment segmentFor (double x)
	{
		switch (Double.compare (x, 0))
		{
			case 0: return Segment.EXACT_ZERO;
			case 1: return x <= HI_KNOT ? Segment.POS_BELOW : Segment.POS_ABOVE;
			case -1: return x <= LO_KNOT ? Segment.NEG_BELOW : Segment.NEG_ABOVE;
			default: return Segment.ILLEGAL;
		}
	}
	public static final double LO_KNOT = -5, HI_KNOT = 5;


	/**
	 * x POW exponent
	 * @param x the parameter for the computation
	 * @return the computed result
	 */
	public static double xPow15 (double x) { return Math.pow (x, THREE_HALVES); }
	public static double xPow1523 (double x) { return TWO_THIRDS * xPow15 (x); }
	public static double xPow25 (double x) { return Math.pow (x, QUARTER); }


	/**
	 * Ai approximation for positive domain.
	 *  not as accurate as Bessel but converges 
	 *  on larger positive ranges of parameters
	 * @param x real value parameter to Ai
	 * @return Stokes approximation of Ai
	 */
	public static double AiPosDomApprox (double x)
	{
		return Math.exp ( - xPow1523 (x) ) / ( 2 * ROOT_PI * xPow25 (x) );
	}


	/**
	 * Ai approximation for negative domain.
	 *  not as accurate as Bessel but converges 
	 *  on larger negative ranges of parameters
	 * @param x real value parameter to Ai
	 * @return Stokes approximation of Ai
	 */
	public static double AiNegDomApprox (double x)
	{
		return Math.sin ( xPow1523 (x) + QUARTER_PI ) / ( ROOT_PI * xPow25 (x) );
	}


	/**
	 * Bessel Ai identity using Jp.
	 *  used for domain ( -knot .. 0 )
	 * @param x real value parameter to Ai
	 * @return Ai approximation using Bessel Jp
	 */
	public static double AiJ (double x)
	{
		BesselFunctionsForAiry bessel = getBesselFunctions ();
		double xp23 = xPow1523 ( - x ), radx9 = Math.sqrt ( - x / 9 );
		return radx9 * (bessel.getJn13 (xp23) + bessel.getJp13 (xp23));
	}

	/**
	 * Bessel Ai identity using Ka.
	 *  used for domain ( 0 .. +knot ]
	 * @param x real value parameter to Ai
	 * @return Ai approximation using Bessel K
	 */
	public static double AiK (double x)
	{
		double xp23 = xPow1523 (x), radx3 = Math.sqrt ( x / 3 );
		BesselFunctionsForAiry bessel = getBesselFunctions ();
		return radx3 * bessel.getK13 (xp23) / Math.PI;
	}

	/**
	 * calculate Ai(x) using best approach per segment
	 * @param x real value parameter to Ai
	 * @return spline evaluation of Ai
	 */
	public static double Ai (double x)
	{
		switch (segmentFor (x))
		{
			case NEG_ABOVE: return AiJ (x);
			case POS_BELOW: return AiK (x);
			case EXACT_ZERO: return AI_ZERO;
			case NEG_BELOW: return AiNegDomApprox (-x);
			case POS_ABOVE: return AiPosDomApprox (x);
			default: return Double.POSITIVE_INFINITY;
		}
	}
	public static final double AI_ZERO = 0.35502805388781723926;


	/**
	 * Bessel Bi identity using Jp.
	 *  used for domain ( -knot .. 0 )
	 * @param x real value parameter to Bi
	 * @return Bi approximation using Bessel Jp
	 */
	public static double BiJ (double x)
	{
		BesselFunctionsForAiry bessel = getBesselFunctions ();
		double xp23 = xPow1523 ( - x ), radx3 = Math.sqrt ( - x / 3 );
		return radx3 * ( bessel.getJn13 (xp23) - bessel.getJp13 (xp23) );
	}


	/**
	 * Bessel Bi identity using Ia.
	 *  used for domain [ 0 .. +knot ]
	 * @param x real value parameter to Bi
	 * @return Bi approximation using Bessel Ia
	 */
	public static double BiI (double x)
	{
		BesselFunctionsForAiry bessel = getBesselFunctions ();
		double xp23 = xPow1523 (x), radx3 = Math.sqrt ( x / 3 );
		return radx3 * ( bessel.getIp13 (xp23) + bessel.getIn13 (xp23) );
	}


	/**
	 * Bi approximation for negative domain.
	 *  not as accurate as Bessel but converges 
	 *  on larger negative ranges of parameters
	 * @param x real value parameter to Bi
	 * @return Stokes approximation of Bi
	 */
	public static double BiNegDomApprox (double x)
	{
		return Math.cos ( xPow1523 (x) + QUARTER_PI ) / ( ROOT_PI * xPow25 (x) );
	}


	/**
	 * calculate Bi(x) using best approach per segment
	 * @param x real value parameter to Bi
	 * @return spline evaluation of Bi
	 */
	public static double Bi (double x)
	{
		switch (segmentFor (x))
		{
			case POS_ABOVE:
			case POS_BELOW: return BiI (x);
			case NEG_ABOVE: return BiJ (x);
			case EXACT_ZERO: return BI_ZERO;
			case NEG_BELOW: return BiNegDomApprox (-x);
			default: return Double.POSITIVE_INFINITY;
		}
	}
	public static final double BI_ZERO = 0.615;


	/**
	 * @return an object populated with Bessel Ia / Jp / Ka polynomial approximations
	 */
	public static BesselFunctionsForAiry getBesselFunctions ()
	{
		if (besselFunctions == null)
		{ besselFunctions = new BesselFunctionsForAiry (); }
		return besselFunctions;
	}
	static BesselFunctionsForAiry besselFunctions = null;


	/*
	 * Chebyshev Spline Functions for Ai and Bi
	 */


	public static final Double[]
			AI_CHEBYSHEV_COEFFICIENTS = new Double[]
			{
				 0.34756242794511390, 
				-0.21469335282905444,
				-0.009861247120777484,
				 0.014633612650070465, 
				-0.0023273483327565566, 
				-5.231818902381156E-5, 
				 6.115884000346425E-5, 
				-7.062857598518357E-6, 
				-1.0475732608229721E-7,
				 1.0634141782197187E-7, 
				-9.930223463916193E-9, 
				-1.0975358611575691E-10, 
				 1.0079783171991209E-10, 
				-8.026252246337739E-12, 
				-7.053760401534943E-14, 
				 6.000331963096093E-14, 
				-4.19060896406726E-15, 
				 1.4003288970113895E-18, 
				-1.331521180228786E-17, 
				 5.049629699975459E-17, 
				-4.168853437576806E-17, 
				 8.187337466340336E-18, 
				 1.1107082851283838E-17, 
				-2.7265013699980877E-17, 
				 1.055219887208314E-17, 
				 9.880386134393106E-18, 
				-5.008786196451408E-18, 
				-1.3830419801672553E-18, 
				 7.651442502373411E-19, 
				 6.877355097531198E-20, 
				-3.974953304005463E-20
			},
			BI_CHEBYSHEV_COEFFICIENTS = new Double[]
			{
				 0.6300171526815147, 
				 0.5256412003803598, 
				 0.020283265237168783, 
				 0.025929877597114674, 
				 0.005312381794471429, 
				 1.0396341475377781E-4, 
				 1.0766742305040091E-4, 
				 1.5569769857918584E-5, 
				 2.0461418993880143E-7, 
				 1.866014939654555E-7, 
				 2.1412357696401506E-8, 
				 2.120387494960818E-10, 
				 1.7651965546441033E-10, 
				 1.7035898626097297E-11, 
				 1.3508889183480397E-13, 
				 1.0492581386328311E-13, 
				 8.745017056136497E-15, 
				 1.0494261031207508E-16, 
				 1.4588210741220879E-16, 
				 4.8484837150016084E-17, 
				 1.3550900298584322E-16, 
				-1.847928565906359E-17, 
				-3.899225869130933E-17, 
				-5.04633823391549E-18, 
				-2.83316771696689E-17, 
				 3.314920856975327E-18, 
				 1.3992723845966365E-17, 
				-5.3660776284722545E-19, 
				-2.1517256323942008E-18, 
				 2.8459888921118624E-20, 
				 1.1197705909112956E-19
			},
			AI_CHEBYSHEV_COEFFICIENTS_4 =
				new Double[] { 0.032817907582654016, 0.09489432991554156, -0.23941964449607164, 0.0692709458474982, 0.15482275884277594, -0.1615408050852031, 0.04286021321733769, 0.02893542428967879, -0.027535989689569752, 0.007005396711468025, 0.002263606724090038, -0.0021849137023849103, 5.316045528598591E-4, 9.774093042734594E-5, -1.011075311898871E-4, 2.341100030423348E-5, 2.612105758231329E-6, -3.060074994812008E-6, 7.125802631757109E-7, 1.0404069467829194E-7, -2.5477382719367913E-8, 4.272979595852608E-8, 1.1726401638718026E-8, -6.702985460285099E-9, -5.580553065740836E-9, -6.24612549669244E-9, -2.1746414141021414E-9, 1.8001558287910004E-9, 8.140032192159595E-10, -1.2809890877131908E-10, -6.253850337727304E-11 },
			BI_CHEBYSHEV_COEFFICIENTS_4 = new Double[] { 12.784541170588026, 23.40230339639164, 18.458539564202955, 12.478514870495754, 8.084445325282918, 4.474842460719742, 2.2070379096702792, 1.1438598858249795, 0.48785216580723156, 0.19327445570431595, 0.08526621177671542, 0.02965631954813838, 0.009974852409184463, 0.0038808517682520447, 0.0011420204407252637, 3.3770112390386225E-4, 1.1848857856139972E-4, 3.0269739276318664E-5, 8.058832322961617E-6, 2.5815587750114375E-6, 5.89579458085013E-7, 1.384626886158903E-7, 4.3662186145800976E-8, 9.472720618173006E-9, 1.2878631254859156E-9, 1.45029203338272E-9, -1.4110430604173235E-10, -2.50306350328291E-10, 9.754356403554259E-11, 2.1192557847725336E-11, -6.878203233379517E-12 };


	/**
	 * establish Chebyshev polynomial coefficients
	 *  collected by regression over domain -1:1
	 * spline will use Clenshaw to compute results
	 * requested in this domain
	 */
	public AiryFunctions ()
	{
		aiSpline = new ChebyshevSplineFunction <Double> (AI_CHEBYSHEV_COEFFICIENTS, manager);
		biSpline = new ChebyshevSplineFunction <Double> (BI_CHEBYSHEV_COEFFICIENTS, manager);
		aiSpline4 = new ChebyshevSplineFunction <Double> (AI_CHEBYSHEV_COEFFICIENTS_4, 4, manager);
		biSpline4 = new ChebyshevSplineFunction <Double> (BI_CHEBYSHEV_COEFFICIENTS_4, 4, manager);
	}
	ChebyshevSplineFunction <Double> aiSpline, biSpline, aiSpline4, biSpline4;


	/**
	 * @return object holding function descriptions for Ai and Bi splines
	 */
	public static AiryFunctions getAiryFunctions ()
	{
		if (airyFunctions == null)
		{ airyFunctions = new AiryFunctions (); }
		return airyFunctions;
	}
	static AiryFunctions airyFunctions = null;
	

	/**
	 * calculate Ai(x) using Chebyshev spline
	 * 
	 * @param x real value parameter to Ai on domain [ -1.5 .. +1.5 ]
	 * @return calculated value
	 */
	public static double AiS (double x)
	{
		return getAiryFunctions ().aiSpline.eval (x);
	}

	/**
	 * calculate Ai(x) using Chebyshev spline
	 * @param x real value parameter to Ai on domain [ -6 .. +6 ]
	 * @return calculated value
	 */
	public static double AiS4 (double x)
	{
		return getAiryFunctions ().aiSpline4.eval (x);
	}


	/**
	 * calculate Bi(x) using Chebyshev spline
	 * @param x real value parameter to Bi on domain [ -1.5 .. +1.5 ]
	 * @return calculated value
	 */
	public static double BiS (double x)
	{
		return getAiryFunctions ().biSpline.eval (x);
	}

	/**
	 * calculate Bi(x) using Chebyshev spline
	 * @param x real value parameter to Bi on domain [ -6 .. +6 ]
	 * @return calculated value
	 */
	public static double BiS4 (double x)
	{
		return getAiryFunctions ().biSpline4.eval (x);
	}


}


/**
 * prepare Jn, Jp, Ia, and Ka Bessel functions for use in Airy computations
 */
class BesselFunctionsForAiry
{
	
	/**
	 * the count of terms in the polynomials
	 */
	public static final int DEGREE = 20;

	/**
	 * the Bessel functions used in Airy identities are of order 1/3
	 */
	public static final double ORDER = 1.0 / 3.0;

	/**
	 * define polynomial manager
	 */
	ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();
	PolynomialSpaceManager<Double> psm = new PolynomialSpaceManager<Double> (mgr);

	/**
	 * build functions Jp, Jn, Ia, and Ka
	 */
	Function<Double> Jp = OrdinaryFirstKind.getJ (ORDER, DEGREE, psm);
	Function<Double> Jn = OrdinaryFirstKind.getJ (-ORDER, DEGREE, psm);
	Function<Double> Ka = ModifiedSecondKind.getK (ORDER, DEGREE, psm);
	Function<Double> In = ModifiedFirstKind.getI (-ORDER, DEGREE, psm);
	Function<Double> Ip = ModifiedFirstKind.getI (ORDER, DEGREE, psm);

	// simple calls

	/**
	 * Bessel Jp for p=-1/3
	 * @param x the parameter to Bessel function
	 * @return the computed value
	 */
	double getJn13 (double x) { return Jn.eval (x); }

	/**
	 * Bessel Jp for p=1/3
	 * @param x the parameter to Bessel function
	 * @return the computed value
	 */
	double getJp13 (double x) { return Jp.eval (x); }

	/**
	 * Bessel Ka for a=1/3
	 * @param x the parameter to Bessel function
	 * @return the computed value
	 */
	double getK13  (double x) { return Ka.eval (x); }

	/**
	 * Bessel Ia for a=1/3
	 * @param x the parameter to Bessel function
	 * @return the computed value
	 */
	double getIp13 (double x) { return Ip.eval (x); }

	/**
	 * Bessel Ia for a=-1/3
	 * @param x the parameter to Bessel function
	 * @return the computed value
	 */
	double getIn13 (double x) { return In.eval (x); }

}

