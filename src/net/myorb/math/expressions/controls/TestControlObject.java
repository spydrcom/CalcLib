
package net.myorb.math.expressions.controls;

//import net.myorb.math.expressions.evaluationstates.*;
//import net.myorb.math.expressions.symbols.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;

public class TestControlObject extends FloatingEvaluationControl
{

	String
	e1 = "define sec(x) = 1 / cos x",
	e2 = "define csc(x) = 1 / sin x",
	e3 = "define cot(x) = 1 / tan x",
	e4 = "define acos(x) = pi/2 - asin x",
	e5 = "define acot(x) = pi/2 - atan x",
	e6 = "define asec(x) = acos (1/x)",
	e7 = "define acsc(x) = pi/2 - asec x",
	e8 = "define sinh(x) = (exp(x) - exp(-x)) / 2",
	e9 = "define cosh(x) = (exp(x) + exp(-x)) / 2",
	e10 = "define tanh(x) = sinh(x) / cosh(x)",
	e11 = "define coth(x) = cosh(x) / sinh(x)",
	e12 = "define arsinh(x) = ln (x + sqrt (x^2 + 1))",
	e13 = "define arcosh(x) = ln (x + sqrt (x^2 - 1))",
	e14 = "define artanh(x) = (ln (x + 1) - ln (x - 1)) / 2",
	e15 = "define arcoth(x) = artanh (1/x)",
	e20 = "";
	
	public void run ()
	{

		execute ("define sec(x) = 1 / cos x");
		execute ("define csc(x) = 1 / sin x");
		execute ("define cot(x) = 1 / tan x");
		execute ("define acos(x) = pi/2 - asin x");
		execute ("define acot(x) = pi/2 - atan x");
		execute ("define asec(x) = acos (1/x)");
		execute ("define acsc(x) = pi/2 - asec x");
		execute ("define arcosh(x) = ln (x + sqrt (x^2 - 1))");
		execute ("define artanh(x) = (ln (x + 1) - ln (x - 1)) / 2");
		execute ("define arsinh(x) = ln (x + sqrt (x^2 + 1))");
		execute ("define sinh(x) = (exp(x) - exp(-x)) / 2");
		execute ("define cosh(x) = (exp(x) + exp(-x)) / 2");
		execute ("define tanh(x) = sinh(x) / cosh(x)");
		execute ("define arcoth(x) = artanh (1/x)");
		execute ("define euler() = exp (1)");

		execute ("let pi2 = -2 * (-pi)");
		dump ("pi2");

		execute ("let e =  1/0! + 1/1! + 1/2! + 1/3! + 1/4! + 1/5! + 1/6! + 1/7! + 1/8! + 1/9!");
		dump ("e");

		execute ("let DUMPING = 0");

		execute ("define q(a,b,c) = (sqrt(b^2 - 4*a*c) - b) / (2*a)");

		execute ("define f(x, y) = -x^2 + 2*x*y + y^2");
		execute ("let result =  f (2, 3)"); // 17
		dump ("result");

		execute ("define d(a,b,c) = b^2 - 4*a*c");
		execute ("define q1(a,b,c) = (sqrt (d (a,b,c)) - b) / (2*a)");
		execute ("define q2(a,b,c) = (-sqrt (d (a,b,c)) - b) / (2*a)");


		execute ("let PHI =  q1 (1, -1, -1)");
		dump ("PHI");

		execute ("let phi =  q2 (1, -1, -1)");
		dump ("phi");

		execute ("let m = 2");
		execute ("let n = 3");

		execute ("define f(m,n) = (n - 1) * (m + 1)");
		execute ("let test1 =  f(4,5)"); // 20
		dump ("test1");

		execute ("let test2 =  f(6); "); // 28
		dump ("test2"); 

		execute ("let test2 =  f(6,0); "); // -7 !!OK!!
		dump ("test2");

		execute ("let test2 =  f(6,2); let test3 =  f(7); "); // 7 8
		dump ("test2"); dump ("test3");

		execute ("let test4 =  f(4,5) < 19"); // 0
		dump ("test4");

		execute ("let test5 =  test1 == 20"); // 1
		dump ("test5");

		execute ("let test6 =  arsinh (sinh(2))"); // 2
		dump ("test6");
		
		execute ("let test7 =  acos(-1)"); // pi
		dump ("test7");

		execute ("let test8 =  4 * atan (cot(pi/4))"); // pi
		dump ("test8");

		execute ("let test9 =  euler()"); // e
		dump ("test9");

		execute ("@ testa =  (test1 ~= 20)? 7: 8"); // 8
		dump ("testa");


		execute ("@ testb =  SIGMA(1/0!, 1/1!, 1/2!, 1/3!, 1/4!, 1/5!, 1/6!, 1/7!, 1/8!, 1/9!)"); // e
		dump ("testb");

		execute ("@ testc =  PI(2,3,4,5)"); // 120
		dump ("testc");

		execute ("@ testd =  (2, 3, 4, 5)");
		dump ("testd");

		execute ("@ teste =  PI(testd)"); // 120
		dump ("teste");

		execute ("@ testf =  (4, 1, 3, 2)");
		dump ("testf");

		execute ("@ testg =  (5, 4, 2, 3)");
		dump ("testg");

		execute ("@ testh =  DOT(testf,testg)"); // 36
		dump ("testh"); 

		execute ("@ testi =  testf#(2,2)"); // 3
		dump ("testi");

		//execute ("let DUMPING = 1");

		execute ("@ testj =  testf#(3)"); // 2
		dump ("testj");

		execute ("@ testk =  testf#1"); // 1
		dump ("testk");

		execute ("@ i = 2");
		execute ("@ testl =  testf#(i-2)"); // 4
		dump ("testl");

		execute ("@ testm =  testf . testg"); // 36
		dump ("testm"); 

		execute ("@testn= PI [i-1 <= x <= 5](x)"); // 120
		dump ("testn");

		//execute ("array f(x) = (3,4,5,6)");
		execute ("@testo= (3,4,5,6) . [1 <= x <= 4](x)"); // 50
		dump ("testo");

		execute ("@testf= SIGMA[0 <= x <= 20](1/x!)"); // e
		dump ("testf");

		execute ("@ c =  (3, 5, 4, 6, 5)");
		dump ("c");

		execute ("define poly(x, n) = [0 <= i <= n](x^i)");
		execute ("@ testp =  DOT(c, poly(0.5, 4))");
		dump ("testp");

		execute ("!! series(n) = [0 <= i <= n](1/i!)");
		execute ("!! expx(x, n) =  DOT(series(n), poly(x, n))");
		//execute ("@ teste =  poly(0.5, 10) . series(10)");
		execute ("@ teste =  [5 <= i <= 10](expx(1,i))");
		dump ("teste");

		execute ("@ testd =  [5 <= x <= 7 <> 1/2](x)");
		dump ("testd");

		System.out.println ("==============");

		execute ("@ dx = 0.01");
		execute ("@ integralEminus1 =  SIGMA [0 <= x <= 1 <> dx](expx(x,10)*dx)");
		dump ("integralEminus1");

		execute ("@ dx = 0.001");
		execute ("@ integralEminus1 =  SIGMA [0 <= x <= 1 <> dx](exp(x)*dx)");
		dump ("integralEminus1");

		System.out.println ("==============");

		execute
		("dx = 0.0001"); //3.141
		// 0.000001 give 6 places of PI
		execute ("!! f(x) = sqrt (1 - x^2)");
		execute ("integral0to1 =  SIGMA [0 <= x <= 1 <> dx](f(x)*dx)");
		execute ("piComputed =  4 * integral0to1");
		dump ("piComputed");

		System.out.println ("==============");

		execute ("@ teste =  1 + abc * 3");
		dump ("teste");
		System.out.println ("==============");

		execute ("teste =  1 + 2 * 3");
		dump ("teste");
		System.out.println ("==============");

		execute ("abc =  1 + 2 * 3");
		dump ("abc");
		System.out.println ("==============");

		execute ("def#(i+1) =  1 + 2 * 3");
		dump ("def");
		System.out.println ("==============");

		execute ("def#2 =  def#3 + 1");
		dump ("def");
		System.out.println ("==============");

		execute ("def#1 =  def#2 - 1");
		dump ("def");
		System.out.println ("==============");

		execute ("testl =  LENGTH (9,8,7,6)");
		dump ("testl");

		System.out.println ("==============");

		//execute ("DUMPING = 1");
		execute ("c =  (3, 5, 4, 6, 5)");
		dump ("c");

		execute ("define poly(x, n) = [0 <= i <= n-1](x^i)");
		execute ("testp =  DOT(c, poly(0.7, LENGTH(c)))");
		dump ("testp");

		System.out.println ("==============");

		execute ("n = 10");
		execute ("c = [0 <= i <= n](1/i!)");
		dump ("c");

		execute ("define powers(x,d) = [0 <= i <= d](x^i)");
		execute ("define poly(x, co) = DOT ( co, powers(x,LENGTH(co)-1) )");

		//execute ("DUMPING = 1");
		execute ("y =  poly(1, c)");
		dump ("y");


		System.out.println ("==============");

	}

	public void run3 ()
	{
		execute ("!! series(n) = [0 <= i <= n] (1/i!) ");
		execute ("!! poly(x,n) = [0 <= i <= n] (x^i)  ");
		execute ("!! expx(x,n) = DOT ( series(n), poly(x,n) ) ");

		System.out.println ("==============");

		execute ("dx = 0.01");
		// expx is defined in local expression definition
		execute ("integralEminus1 = SIGMA [0 <= x <= 1 <> dx](expx(x,10)*dx)");
		dump ("integralEminus1");

		execute ("dx = 0.001");
		// exp is the system built-in function version
		execute ("integralEminus1 = SIGMA [0 <= x <= 1 <> dx](exp(x)*dx)");
		dump ("integralEminus1");

		System.out.println ("==============");

		execute
		("dx = 0.0001"); //3.141
		// 0.000001 give 6 places of PI
		execute ("!! f(x) = sqrt (1 - x^2)"); // built-in sqrt
		execute ("integral0to1 =  SIGMA [0 <= x <= 1 <> dx](f(x)*dx)");
		execute ("piComputed =  4 * integral0to1");
		dump ("piComputed");

		System.out.println ("==============");
	}

	public void run2 ()
	{
		//execute ("DUMPING = 1");

		execute ("terms = 20");
		execute ("!! expNth(n) = 1/n!");
		execute ("!! expCoefficients(n) = [0 <= i <= n](expNth(i))");
		execute ("!! poly(coefficients, x) = coefficients . [0 <= i <= LENGTH(coefficients)-1](x^i) ");
		execute ("eSqrt = poly( expCoefficients(terms) , 0.5 )");
		dump ("eSqrt");

		execute ("correctValue = 1.648721270700128146848");
		execute ("error = correctValue - eSqrt");
		dump ("error");
	}

	public void run4 ()
	{
		//execute ("@DUMPING = 1");
//		execute ("calc 1 + 2 * 3 ^4");
//		execute ("calc (1, 2, 3, 4, 5, 6)");
		execute ("graph [-3.15 <= x <= 3.2 <> 0.1](sin x)");
//		execute ("graph [-3 <= x <= 3 <> 0.1](exp x)");
//		execute ("graph [0.5 <= x <= 5 <> 0.05](ln x)");
//		execute ("graph [0.5 <= x <= 3 <> 0.05](1 - x * ln x)");
	}

	public void run5 ()
	{
		//execute ("polynomial (1, 1, 1)");
		//execute ("polynomial (1, 1, -1)");
		//execute ("polynomial (-5040.0, 29952.0, -24553.0, 3821.0, 759.0, -197.0, 10.0)");
		//execute ("polynomial (432, -144, -3, 1)");
		//execute ("polynomial (1, 3, -3, 1)");
		//execute ("polynomial (4, -7, 2, 1)");
		//execute ("polynomial (4, -7, -9, -1, 1)");
		//execute ("polynomial (54, -7, -9, -1, 1)");
		execute ("polynomial (4, 0, -5, 0, 1)");
	}

	public void run6 ()
	{
		//execute ("calc pi^2 / 6 - SIGMA [1 <= i <= 400000](1/i^2)"); // 2.5E-6
		execute ("calc pi^2 / 6 - SIGMA [1 <= i <= 40000](1/i^2)");
	}

	public void run7 ()
	{
		//execute ("calc pi/4 - SIGMA [1 <= i <= 400000 <> 4](1/i - 1/(i+2))");
		//execute ("calc SIGMA [1 <= i <= 5000000 <> 4](8/(i*i+2*i))");// 3.141592253
		execute ("calc SIGMA [1 <= i <= 50000 <> 4](8/(i*i+2*i))");
	}

	public void run8 ()
	{
		execute ("derive (54, -76, 94, -17, 11, 1)");
	}

	public void run9 ()
	{
		execute ("data = (1,2,3,4)");
		execute ("datamean = MEAN data");
		execute ("datavar = VAR data");
		execute ("datacov = COV data");
		execute ("datastdev = STDEV data");
	}

	public void runDump ()
	{
		//execute ("SAVE abc.txt");
		//execute ("READ abc.txt");
		execute ("SHOW Symbols");
		execute ("SHOW Functions");
		execute ("SHOW ALL");
		//execute ("HELP");
	}

	public void runSym ()
	{
		execute ("define sec(x) = 1 / cos x");
		execute ("define csc(x) = 1 / sin x");
		execute ("define cot(x) = 1 / tan x");
		execute ("define acos(x) = pi/2 - asin x");
		execute ("define acot(x) = pi/2 - atan x");
		execute ("define asec(x) = acos (1/x)");
		execute ("define acsc(x) = pi/2 - asec x");
		execute ("define arcosh(x) = ln (x + sqrt (x^2 - 1))");
		execute ("define artanh(x) = (ln (x + 1) - ln (x - 1)) / 2");
		execute ("define arsinh(x) = ln (x + sqrt (x^2 + 1))");
		execute ("define sinh(x) = (exp(x) - exp(-x)) / 2");
		execute ("define cosh(x) = (exp(x) + exp(-x)) / 2");
		execute ("define tanh(x) = sinh(x) / cosh(x)");
		execute ("define arcoth(x) = artanh (1/x)");
		execute ("SAVE trig-ops.txt");
	}

	public void runScript ()
	{
		//execute ("@DUMPING = 1");
		execute ("y = (-1, -1, 1)");
		execute ("READ script.txt");
		execute ("z = 1");
	}

	public void runQuad ()
	{
		execute ("w = (-1, -2, 1)");
		execute ("x = (-1, -1, 1)");
		execute ("READ quadratic.txt");
		execute ("SAVE script.txt");
	}

	public void runQuart ()
	{
		//execute ("@DUMPING = 1");
		execute ("x = (4, 0, -5, 0, 1)");
		execute ("READ quartic.txt");
		execute ("SAVE script.txt");
	}

	public void runFit ()
	{
		execute ("x = (-3,-2,1,4,5)");
		execute ("y = (4, -7.1, 9.5, -13.5, 16.2)");
		execute ("pc = FITPOLY (x,y)");
		execute ("SHOW Symbols");

		execute ("y = (4,7.1,9.5,13.5,16.2)");
		execute ("lc = FITLINE (x,y)");
		execute ("SHOW Symbols");

		execute ("y = (0.1, 0.3, 6, 23.5, 66.2)");
		execute ("nlc = FITEXP (x,y)");
		execute ("SHOW Symbols");
	}

	public void plotTest ()
	{
		
		//execute ("@DUMPING = 1");
		execute ("omega = pi / 50");
		execute ("!! f(x) = cos(x) + 8*cos(2*x) + 3*cos(3*x)");
		execute ("!! g(x) = cos(x*omega) + 8*cos(2*x*omega) + 3*cos(3*x*omega)");
		execute ("y = [0 <= i <= 511](g(i))");
		execute ("GRAPH [-50 <= v <= 50 <> 0.1](g(v))");
		execute ("vf =  [0 <= v <= pi/2 <> 0.2](f(v))");
		execute ("FFT y");

		execute ("y = [1 <= i <= 20](ln (i))");
		execute ("x = [1 <= i <= 20](i)");
		execute ("SCATTER x,y");

		execute ("SHOW Symbols");

	}

	public void runSeries ()
	{
		execute ("c = (1,2,3)");
		execute ("GRAPH [-pi <= t <= pi <> 0.1](c +#* t)");
	}

	public void runOmegaSeries ()
	{
		execute ("c = (1,2,3)");
		execute ("omega = pi/10");
		execute ("GRAPH [-10 <= t <= 10 <> 0.1](c +#* (omega*t))");
	}

	public void runHarmonicSeries ()
	{
		execute ("y = (4, -7.1, 9.5, -13.5, 16.2)");
		execute ("c = FITHARMONIC (y,0.5)");
		execute ("GRAPH [-pi <= t <= pi <> 0.01](c +#* t)");
		execute ("SHOW Symbols");
	}

	public void exportTest ()
	{
		execute ("a1 = (1,2,3)");
		execute ("a2 = (4,5,6)");
		execute ("mx = DYADIC(a1,a2)");
		execute ("PRETTYPRINT mx");
		execute ("EXPORT mx mx.tdf");

		execute ("IMPORT m data.txt");
		execute ("SHOW Symbols");
		execute ("");
		execute ("SAVE export.txt");
	}

	public void importTest ()
	{
		execute ("READ export.txt");
		
		execute ("SHOW Symbols");
		execute ("PRETTYPRINT m");
		execute ("PRETTYPRINT mx");
	}

	public static void main(String[] args)
	{
		new TestControlObject ();

		//TestControlObject t = new TestControlObject ();

		//t.exportTest();
		//t.importTest();

		//t.plotTest ();
		//t.runOmegaSeries ();
		//t.runHarmonicSeries ();
		//t.runFit ();

//		t.run ();
//		t.run2 ();
//		t.run3 ();
//		t.run4 ();
//		t.run5 ();
//		t.run6 ();
//		t.run7 ();
//		t.run8 ();
//		t.run9 ();

		//t.runSym ();
		//t.runQuad ();
		//t.runQuart ();

		//t.runScript ();
		//t.runDump ();

//		t.symbols.dump ("Symbols");
//		t.symbols.dump ("Functions");
//		t.symbols.dump ("ALL");
//		t.symbols.help ();
	}

}

