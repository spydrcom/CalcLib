
package net.myorb.math.specialfunctions;

import net.myorb.math.computational.splines.FittedFunction;
import net.myorb.math.computational.splines.ChebyshevSpline;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.data.abstractions.SimpleStreamIO.TextSource;

import java.io.StringReader;

/**
 * spline for use in computation of InverseTangentIntegral
 * @author Michael Druckman
 */
public class InverseTangentIntegral 
{


	/* the low order functions */

	public static Double Ti0 (Double x)
	{
		return x / (1 + x*x);
	}

	public static Double Ti1 (Double x)
	{
		return Math.atan (x);
	}


	/* higher order functions in a series of integrals */


	/**
	 * integrand of the Inverse Tangent Integral
	 * @param x parameter to function
	 * @return atan x / x
	 */
	public static Double atanc (Double x)
	{
		if (x == 0.0) return 1.0;
		return Math.atan (x) / x;
	}


	/**
	 * hyperbolic version of atanc
	 * @param x parameter to function
	 * @return artanh x / x
	 */
	public static Double artanhc (Double x)
	{
		if (x == 0.0) return 1.0;
		else if (x == 1.0) return 0.0;
		return Math.log ( (1+x) / (1-x) ) / (2*x);
	}


	/**
	 * Ti2 function evaluation
	 * - using Chebyshev spline of ATANC
	 * - as integrand of Chebyshev T-polynomial calculus
	 * @param x parameter to function for evaluation
	 * @return INTEGRAL[0..x] ( atanc )
	 */
	public Double Ti2 (Double x)
	{
		if (x == 0.0) return 0.0;
		else if (x < 0.0) return - Ti2 (-x);	// odd function
		return atanSpline.evalIntegralOver (0, x);
	}


	/**
	 * chi2 function evaluation
	 * - using Chebyshev spline of ARTANHC
	 * - as integrand of Chebyshev T-polynomial calculus
	 * @param x parameter to function for evaluation
	 * @return INTEGRAL[0..x] ( artanhc )
	 */
	public Double chi2 (Double x)
	{
		if (x == 0.0) return 0.0;
		return artanhSpline.evalIntegralOver (0, x);
	}


	/*
	 * 
	 * other identities:
	 * 
	 *		Ti2(x) - Ti2(1/x) = pi/2 ln x
	 *
	 * 		Ti2(x)    = x - x^3/3^2 + x^5/5^2 - x^7/7^2 + ...
	 *		Ti[n](x)  = x - x^3/3^n + x^5/5^n - x^7/7^n + ...
	 * 
	 * 
	 * relative to Li2 and chi2:
	 * 
	 * 		Li2(x)    = x + x^2/2^2 + x^3/3^2 + x^4/4^2 + ...
	 * 		chi2(x)   = x + x^3/3^2 + x^5/5^2 + x^7/7^2 + ...
	 * 
	 * 		Ti[s](z)  = ( Li[s](iz) - Li[s](-iz) ) / (2i)
	 * 		Ti2(x)	  = Im ( Li2 (iz) )
	 * 
	 * 		Ti2(x)    = -i chi2 (ix)
	 * 
	 * 
	 * other orders of Ti:
	 * 
	 * 						  x			  dt
	 *		Ti (x) = INTEGRAL	Ti   (t) ----
	 * 		  n				  0   n-1	   t
	 * 
	 */


	/**
	 * parse the spline description
	 */
	public InverseTangentIntegral ()
	{
		this.atanSpline = new FittedFunction <> (mgr, new ChebyshevSpline ());
		this.artanhSpline = new FittedFunction <> (mgr, new ChebyshevSpline ());
		loadSpline (new TextSource (new StringReader (ATANC_SPLINE)), atanSpline);
		loadSpline (new TextSource (new StringReader (ARTANHC_SPLINE)), artanhSpline);
	}
	void loadSpline (TextSource source, FittedFunction<Double> spline) throws RuntimeException
	{
		try
		{
			spline.readFrom (source);
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Inverse Tangent Integral spline load failed");
		}
	}
	protected FittedFunction<Double> atanSpline, artanhSpline;


	/**
	 * computation of Catalans constant and chi2(phi-1)
	 * - providing a metric of the precision of the splines
	 * - this typically will produce the correct value to 9 decimal digits for Ti2
	 * - and 12 decimal digits for chi2
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		double approx, error;
		InverseTangentIntegral ITI = new InverseTangentIntegral ();

		System.out.println ();
		System.out.println ("Ti2(1)");
		System.out.println (approx = ITI.Ti2 (1.0));
		
		error = A006752 - approx;

		System.out.println ();
		System.out.println ("Ti2 error");
		System.out.println (error);

		System.out.println ();
		System.out.println ("chi2(phi-1)");
		double phi = ( Math.sqrt (5) + 1 ) / 2;
		System.out.println (approx = ITI.chi2 (phi-1.0));
		
		error = CHI_PHI - approx;

		System.out.println ();
		System.out.println ("chi2 error");
		System.out.println (error);
	}


	// known correct function result values
	static final double A006752 = 0.9159655941772190150546;			// Catalan's constant
	static final double CHI_PHI = 0.6487934179912174238635;			// chi2(phi-1)


	/**
	 * JSON source for the splines built using the Spline Tool of the CalcLib functionality
	 */
	static final String ATANC_SPLINE =

			"{  \"Name\": \"IT\", \"Parameter\": \"x\", \"Description\": \"ATANC function spline\", "

			+ " \"Interpreter\": \"net.myorb.math.computational.splines.ChebyshevSpline\", " 
			+ " \"NodeType\": \"Profile\", "

			+ " \"Expression\": { "
			+ "       \"OpName\": \"atanc\", \"PostFix\": false, \"NodeType\": \"UnaryOP\", " 

			+ "       \"Parameter\": {"
			+ "            \"Operator\": null, \"Symbol\": \"AssignedVariableStorage\", \"NodeType\": \"Identifier\", "
			+ "            \"Kind\": \"Variable\", \"Name\": \"x\" "
			+"        } "
			+"	}," 

			+ "	\"Sections\": " 
			+ " [ " 

			+ "      { "
			+ "         \"lo\": 0.0,  \"hi\": 5.0, "
			+ "         \"delta\": 0.08333333333333333,  \"error\": 3.9020209455744674E-11, "
			+ "         \"slope\": 1.6666666666666667,   \"unit\":  0.6000000000000001, "

			+ "         \"coefficients\": " 
			+ "         [" 
			+ "            ["
			+ "                0.5266555484546974, "
			+ "               -0.24989350927627335, " + "               0.050767886298301944, "
			+ "               -0.0074021023267951715, " + "            -3.32116794809633E-5, "
			+ "                5.763457484882055E-4, " + "             -2.8868645771638733E-4, "
			+ "                1.0081386146223617E-4, " + "            -2.7950164337315384E-5, "
			+ "                5.919518298031157E-6, " + "             -6.504690503759622E-7, "
			+ "               -1.9234651313258223E-7, " + "             1.6570728376323522E-7, "
			+ "               -7.28387403723319E-8, " + "               2.440261218047119E-8, "
			+ "               -6.521830851384266E-9, " + "              1.2642041973699128E-9, "
			+ "               -6.937281162009933E-11, " + "            -8.538850790479351E-11, "
			+ "                5.467659528376259E-11, " + "            -2.2404399564561513E-11, "
			+ "                7.130501854573784E-12, " + "            -1.8302125606116763E-12, "
			+ "                3.6326749498045024E-13, " + "           -8.414630754276988E-15, "
			+ "               -1.0483242193128024E-14, " + "            1.847928120033256E-14, "
			+ "               -2.2213136603315956E-14, " + "            5.013674036695197E-15, "
			+ "                1.4855938561850482E-15, " + "           -4.61639070525227E-16 " 
			+ "            ] "
			+ "         ] " 
			+ "      }, " 

			+ "      { " 
			+ "         \"lo\": 5.0,  \"hi\": 10.0, "
			+ "         \"delta\": 0.08333333333333333,  \"error\": 3.49554830548176E-18, "
			+ "         \"slope\": 1.6666666666666667,   \"unit\":  0.6000000000000001, "

			+ "         \"coefficients\":  " 
			+ "         [ " 
			+ "            [ " 
			+ "                0.19576300023491322, "
			+ "               -0.03992629934549058, " + "                 0.004036679903789994, "
			+ "               -4.041892568497673E-4, " + "                4.0025731687448176E-5, "
			+ "               -3.912940139894144E-6, " + "                3.767235157800321E-7, "
			+ "               -3.559853780992428E-8, " + "                3.2854974634521772E-9, "
			+ "               -2.9393482064610266E-10, " + "              2.5172394533541522E-11, "
			+ "               -2.0159967289668117E-12, " + "              1.4308139658356975E-13, "
			+ "               -7.69653582029721E-15, " + "               -1.4245961392340834E-16, "
			+ "                8.188335089446337E-17, " + "              -3.663064500922884E-17, "
			+ "               -3.650264809543872E-20, " + "               7.181889265724262E-17, "
			+ "                3.9470165646069234E-18, " + "              4.9587033281757576E-17, "
			+ "               -3.138601264138295E-19, " + "              -1.0693449571483127E-17, "
			+ "               -8.446663795002843E-19, " + "              -1.795244386076544E-17, "
			+ "                3.3160582712058594E-19, " + "              8.000603477021258E-18, "
			+ "               -4.71618299446076E-20, " + "               -1.2060505869076016E-18, "
			+ "                2.3553598517281365E-21, " + "              6.235227574529528E-20 " 
			+ "            ] "
			+ "         ] " 
			+ "      } " 

			+ "   ] " +

			"}";

	static final String ARTANHC_SPLINE =

			"{  \"Name\": \"ITH\", \"Parameter\": \"x\", \"Description\": \"ARTANHC function spline\", "

			+ " \"Interpreter\": \"net.myorb.math.computational.splines.ChebyshevSpline\", " 
			+ " \"NodeType\": \"Profile\", "

			+ " \"Expression\": { "
			+ "       \"OpName\": \"artanhc\", \"PostFix\": false, \"NodeType\": \"UnaryOP\", " 

			+ "       \"Parameter\": {"
			+ "            \"Operator\": null, \"Symbol\": \"AssignedVariableStorage\", \"NodeType\": \"Identifier\", "
			+ "            \"Kind\": \"Variable\", \"Name\": \"x\" "
			+"        } "
			+"  }," 

			+ "	\"Sections\": " 
			+ " [ " 

			+ "      { "
			+ "         \"lo\": 0.0, \"hi\": 0.5, "
			+ "         \"delta\": 0.008333333333333333,  \"error\": 8.45417426392642E-18, "
			+ "         \"slope\": 0.16666666666666666,   \"unit\":  6.0, "

			+ "         \"coefficients\": " 
			+ "         [" 
			+ "            ["
			+ "                 1.027560237878575,                     0.03091152232203648, "
            + "                 0.005946149012975829,                  3.0520038449042793E-4, "
            + "                 3.7468403550380396E-5,                 2.9113081417474763E-6, "
            + "                 3.126856336130161E-7,                  2.8699472039968027E-8, "
            + "                 2.9785617618745564E-9,                 2.9376389662201795E-10, "
            + "                 3.041660071474715E-11,                 3.104677000233991E-12, "
            + "                 3.2341237468261376E-13,                3.336858318432287E-14, "
            + "                 3.2731601975769014E-15,                3.736654453670475E-16, "
            + "                 6.100273814729563E-17,                -6.415110582852898E-17, "
            + "                 8.50993589195652E-17,                  1.2131578773767037E-16, "
            + "                 7.156235455553213E-17,                 5.928440865238797E-17, "
            + "                -5.0835606227058994E-17,               -7.937448993600753E-17, "
            + "                 2.6716575309124823E-18,                2.5576375772813037E-17, "
            + "                 3.1264186429905624E-18,               -3.404119248220018E-18, "
            + "                -6.555170699253232E-19,                 1.6458661187308922E-19, "
            + "                 3.831055850068566E-20 "
			+ "            ] "
			+ "         ] " 
			+ "      }, " 

			+ "      { " 
			+ "         \"lo\": 0.5, \"hi\": 0.75, "
			+ "         \"delta\": 0.004166666666666667,  \"error\": 1.4967538922485261E-15, "
			+ "         \"slope\": 0.08333333333333333,   \"unit\": 12.0, "

			+ "         \"coefficients\":  " 
			+ "         [ " 
			+ "            [ " 
			+ "                 1.1783671306854107,                    0.06360995565710008, "
            + "                 0.005333495456473756,                  4.107557301014125E-4, "
            + "                 3.6333682880003927E-5,                 3.3312547047957343E-6, "
            + "                 3.1690637440290686E-7,                 3.085857339253805E-8, "
            + "                 3.060277932784687E-9,                  3.077841902619185E-10, "
            + "                 3.1310926721062036E-11,                3.2144741522306857E-12, "
            + "                 3.3766046866367475E-13,                3.5180313139667945E-14, "
            + "                 6.960250997113018E-15,                 5.094361295419631E-16, "
            + "                 3.9207853138370647E-16,               -1.5177187379455154E-16, "
            + "                -1.4246537498171737E-15,               -1.925966089770481E-16, "
            + "                -9.965390705662303E-16,                 1.269984171757214E-17, "
            + "                 2.289213865552229E-16,                 7.327093796866093E-17, "
            + "                 3.4793086520501155E-16,               -2.93875536902921E-17, "
            + "                -1.5681481056063987E-16,                4.2646169189871264E-18, "
            + "                 2.3713562856092867E-17,               -2.1603200092608893E-19, "
            + "                -1.227765009874003E-18 "
			+ "            ] "
			+ "         ] " 
			+ "      }, " 

			+ "      { " 
			+ "         \"lo\": 0.75, \"hi\": 0.875, "
	        + "         \"delta\": 0.0020833333333333333, \"error\": 6.75754259003665E-14, "
	        + "         \"slope\": 0.041666666666666664,  \"unit\": 24.0, "

			+ "         \"coefficients\":  " 
			+ "         [ " 
			+ "            [ " 
			+ "                1.4017068486316837,       0.08060529560462609, "
			+ "                0.005631526550114631,     4.3966409147493894E-4, "
			+ "                3.795009408996259E-5,     3.457875085899927E-6, "
			+ "                3.267723463859789E-7,     3.168554538807506E-8, "
            + "                3.1318402038573457E-9,    3.141753020197717E-10, "
            + "                3.185798497394854E-11,    3.265932819960804E-12, "
            + "                3.0175994155515943E-13,   3.322107917575125E-14, "
            + "                -1.9370157525925526E-14, -2.0499448192527375E-16, "
            + "                -2.5682001103650803E-15,  6.568824814716073E-16, "
            + "                9.985803493294345E-15,    6.504929202337695E-16, "
            + "                6.945522834717306E-15,   -4.220173345521894E-17, "
            + "                -1.6661291650084888E-15, -2.5882731254751176E-16, "
            + "                -2.3727849951833543E-15,  1.0391993072185584E-16, "
            + "                1.078089306017581E-15,   -1.5093940254457175E-17, "
            + "                -1.6339283284120082E-16,  7.65059055474734E-19, "
            + "                8.468301431919637E-18 "

			+ "            ] "
			+ "         ] " 
			+ "      }, " 

			+ "      { " 
			+ "         \"lo\": 0.875, \"hi\": 0.9375, "
	        + "         \"delta\": 0.0010416666666666667, \"error\": 5.220328687184389E-14, "
	        + "         \"slope\": 0.020833333333333332,  \"unit\": 48.0, "

			+ "         \"coefficients\":  " 
			+ "         [ " 
			+ "            [ " 
			+ "                1.6678071349759749,      0.09177922577291948, "
            + "                0.005908322403958771,    4.556563235346665E-4, "
            + "                3.893751746028188E-5,    3.528671853000591E-6, "
            + "                3.322596861987676E-7,    3.21362106879775E-8, "
            + "                3.1704500319924546E-9,   3.1759075864546767E-10, "
            + "                3.217162004012632E-11,   3.2958201977896253E-12, "
            + "                3.088755958577515E-13,   3.6302389605439486E-14, "
            + "               -1.676000798036988E-14,  -1.776556450603661E-15, "
            + "               -2.4234496811709386E-15,  1.154507913029369E-15, "
            + "                8.939540803351263E-15,   2.0863964753678179E-16, "
            + "                6.151361569313298E-15,  -2.4244210186604332E-18, "
            + "               -1.479161928989238E-15,  -1.558666454637131E-16, "
            + "               -2.105679464421507E-15,   6.213093047390576E-17, "
            + "                9.568685801077745E-16,  -9.03011306319508E-18, "
            + "               -1.4502697522403212E-16,  4.58125986239148E-19, "
            + "                7.516556804274174E-18 "
			+ "            ] "
			+ "         ] " 
			+ "      } " 

			+ "   ] " +
		"}";
	static ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();


}

