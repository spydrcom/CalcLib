
package net.myorb.testing;

import net.myorb.math.primenumbers.Factorization;

/**
 * computation of PI as a test of the Factorization library
 * @author Michael Druckman
 */
public class PiFactorization
{

	/**
	 * intermediate results having computed
	 *   the Ramanujan series and SQRT 2
	 */
	static Factorization radical2, series;

	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		FactorizationCore.init (1000*1000);

		System.out.println ();
		System.out.println ("SQRT 2"); System.out.println ();
		radical2 = new NewtonRaphsonIterativeTest ().run (12);
		System.out.println (radical2);
		System.out.println ();

		System.out.println ();
		System.out.println ("SERIES"); System.out.println ();
		series = new RamanujanTest ().run (130);
		System.out.println (series);
		System.out.println ();

		String APX;
		System.out.println ();
		Factorization approx = pi ();
		FactorizationCore.mgr.setDisplayPrecision (1200);
		System.out.println ("PI"); System.out.println ();
		System.out.println (APX = FactorizationCore.mgr.toDecimalString (approx));
		System.out.println (RamanujanTest.toRatio (approx));
		System.out.println ();

		System.out.println ();
		System.out.print ("DIF AT = ");
		System.out.println (difAt (APX));
		System.out.println ();

	}


	/**
	 * combine series evaluation with SQRT 2
	 * @return the computed approximation of PI
	 */
	static Factorization pi ()
	{
		Factorization X = FactorizationCore.mgr.newScalar (2);
		Factorization C9801 = FactorizationCore.mgr.newScalar (9801);
		X = FactorizationCore.mgr.multiply (X, FactorizationCore.mgr.invert (C9801));
		X = FactorizationCore.mgr.multiply (X, radical2);
		X = FactorizationCore.mgr.multiply (X, series);
		return FactorizationCore.mgr.invert (X);
	}
	// 1 / pi = ( 2 * sqrt(2) / 9801 ) * SIGMA [0 <= k <= INFINITY] ( (4*k)! * (1103 + 26390*k) / ((k!)^4 * 396 ^ (4*k)) )


	/**
	 * verify value accuracy against reference
	 * @param comparedWith a string to match with PI digits
	 * @return the position where match fails checking character by character
	 * @throws RuntimeException when reference is not adequate
	 */
	public static int difAt (String comparedWith) throws RuntimeException
	{
		int most = Math.min (comparedWith.length (), PI_REF.length ());
		for (int i=0; i<most;i++) { if (PI_REF.charAt(i) != comparedWith.charAt(i)) return i; }
		throw new RuntimeException ("Resulting accuracy greater than reference");
	}
	public static String PI = // 1200 decimal digit reference
		"3.1415926535 8979323846 2643383279 5028841971 6939937510 5820974944 5923078164 0628620899 8628034825 3421170679  " +
        "  8214808651 3282306647 0938446095 5058223172 5359408128 4811174502 8410270193 8521105559 6446229489 5493038196  " +
        "  4428810975 6659334461 2847564823 3786783165 2712019091 4564856692 3460348610 4543266482 1339360726 0249141273  " +
        "  7245870066 0631558817 4881520920 9628292540 9171536436 7892590360 0113305305 4882046652 1384146951 9415116094  " +
        "  3305727036 5759591953 0921861173 8193261179 3105118548 0744623799 6274956735 1885752724 8912279381 8301194912  " +
        "  9833673362 4406566430 8602139494 6395224737 1907021798 6094370277 0539217176 2931767523 8467481846 7669405132  " +
        "  0005681271 4526356082 7785771342 7577896091 7363717872 1468440901 2249534301 4654958537 1050792279 6892589235  " +
        "  4201995611 2129021960 8640344181 5981362977 4771309960 5187072113 4999999837 2978049951 0597317328 1609631859  " + 
        "  5024459455 3469083026 4252230825 3344685035 2619311881 7101000313 7838752886 5875332083 8142061717 7669147303  " +
        "  5982534904 2875546873 1159562863 8823537875 9375195778 1857780532 1712268066 1300192787 6611195909 2164201989  " +
        "  3809525720 1065485863 2788659361 5338182796 8230301952 0353018529 6899577362 2599413891 2497217752 8347913151  " +
        "  5574857242 4541506959 5082953311 6861727855 8890750983 8175463746 4939319255 0604009277 0167113900 9848824012  " ;
	//		taken from http://web.archive.org/web/20140225153300/http://www.exploratorium.edu/pi/pi_archive/Pi10-6.html
	public static String PI_REF = PI.replaceAll (" ", "");


}

