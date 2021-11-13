
package net.myorb.math.expressions.controls;

public class TestComplexControlObject
extends ComplexEvaluationControl
//extends FloatingEvaluationControl
{

	public void runDump ()
	{
		execute ("SHOW Symbols");
//		execute ("SHOW Functions");
		execute ("SHOW ALL");
		execute ("HELP");
	}

	public void runQuart ()
	{
		//execute ("@DUMPING = 1");
		execute ("x = (4, 0, -5, 0, 1)");
		execute ("READ quartic.txt");
	}

	public void runCubic ()
	{
		//execute ("@DUMPING = 1");
		execute ("x = (2, -3, -3, 2)");
		execute ("READ cubic.txt");
	}

	public void runQuad ()
	{
		execute ("x = (-1, -1, 1)");
		execute ("READ quadratic.txt");
	}

	public void runRoots ()
	{
		execute ("t = (5, 4, 6, 3)");
		execute ("v = POLYDER t");
		execute ("x = (-1, -1, 1)");
		execute ("w = x +*^ 3");
		execute ("y = ROOTS x");
	}

	public void runApproxDer ()
	{
		execute ("fc = (6,4,5,3,1)");
		execute ("!! f(x) = fc +*^ x");
		execute ("fd = POLYDER fc");
		execute ("fd2 = POLYDER fd");
		execute ("fx = f(2)");
		execute ("fPrime = fd +*^ 2");
		execute ("fPrime2 = fd2 +*^ 2");
		execute ("dx = 10^(-6)");
		execute ("approxD = (f(2 + dx/2) - f(2 - dx/2)) / dx");
		execute ("approxDm1 = (f(2) - f(2 - dx)) / dx");
		execute ("approxDp1 = (f(2 + dx) - f(2)) / dx");
		execute ("approxD2 = (approxDp1 - approxDm1) / dx");
		
	}

	void runAppend ()
	{
		execute ("u = APPEND (-1/2 +|- i*2\\3 / 2, 1)");
	}

	void runImport ()
	{
		execute ("IMPORT m data.txt");
		execute ("c4 = COL (m, 4)");
		execute ("r3 = ROW (m, 3)");
		runDump ();
	}

	void runLS ()
	{
		execute ("X = (2, 7.1, 11)");
		execute ("Y = (5, 15, 23.3)");
		execute ("pearson = PEARSON (X, Y)");
		execute ("ls_coef = FITLINE (X, Y)");
		
	}

	void runExpFit ()
	{
		//execute ("@DUMPING = 1");
		execute ("X = (2, 5, 7)");
		execute ("Y = (163.9, 66075, 3607813)");
		execute ("exp_coef = FITEXP (X, Y)");
		
	}

	void runPoly ()
	{
		//execute ("@DUMPING = 1");
		execute ("X = (2, 5, 7, 8, 11)");
		execute ("!! f(x) = x^3 - 2*x^2 + 4*x - 1");
		execute ("Y = [0 <= i <= 4](f(X#i))");
		execute ("Y#2 = Y#2 + 0.5; Y#3 = Y#3 - 0.5;");
		
		//execute ("@DUMPING = 1");
		execute ("poly_coef = FITPOLY (X, Y)");
		
	}

	void runMatrixInv ()
	{
		//execute ("@DUMPING = 1");
		execute ("A = (3, -7, -3, 2)");
		execute ("M = MATRIX (A, 2, 2)");
		execute ("PRETTYPRINT M");

		execute ("A2 = (-4, -1, 5, 2)");
		execute ("M2 = MATRIX (A2, 2, 2)");
		execute ("PRETTYPRINT M2");

		//execute ("s = MATADD(M2,M)");
		execute ("s = M2 + M");
		execute ("PRETTYPRINT s");

		//execute ("scl = MATMUL(3,M)");
		execute ("scl = 3 * M");
		execute ("PRETTYPRINT scl");

		execute ("vscl = 2 * scl|#2");

		execute ("t = TR (M)");
		execute ("d = DET (M)");
		execute ("inverse = INV (M)");
		//execute ("prd = MATMUL(M,inverse)");
		execute ("prd = M * inverse");
		execute ("PRETTYPRINT inverse");
		execute ("PRETTYPRINT prd");

		execute ("A = (21, -13, -44, 15, 7, -4, 12, 1, -9)");
		execute ("M = MATRIX (A, 3, 3)");
		execute ("inverse = INV (M)");
		//execute ("prd = MATMUL(M,inverse)");
		execute ("prd = M * inverse");
		execute ("PRETTYPRINT M");
		execute ("PRETTYPRINT inverse");
		execute ("PRETTYPRINT prd");
	}

	void runMatrix ()
	{
		execute ("A = (21, -13, -44, 15, 7, -4, 12, 1, -9)");
		execute ("M = MATRIX (A, 3, 3)");
		execute ("r2 = M#2");
		execute ("r3 = M-#3");
		execute ("c3 = M|#3");
		execute ("c33 = M#(3,3)");
		execute ("d = DET (M)");
		execute ("trn = TRANSPOSE M");
		execute ("inverse = INV (trn)");
		execute ("id = IDENTITY 3");
		execute ("mn = MINOR(id,2,2)");

		execute ("PRETTYPRINT M");
		execute ("cp = CHARACTERISTIC M");
		execute ("eigval = ROOTS cp");
		execute ("domval = EIG (M,domvec)");

		execute ("PRETTYPRINT trn");
		execute ("PRETTYPRINT inverse");
		execute ("PRETTYPRINT id");
		execute ("PRETTYPRINT mn");
	}

	void gaussian ()
	{
		execute ("A = (1, 3, -2, 3, 5, 6, 2, 4, 3)");
		execute ("equations = MATRIX (A, 3, 3)");
		execute ("A = (5, 7, 8)");
		execute ("gaussian_solution = GAUSSIAN (equations, A)"); // [-15.0, 8.0, 2.0]
		execute ("aug = AUGMENTED(equations,A)");
		execute ("PRETTYPRINT aug");
	}

	void solve ()
	{
		execute ("A = (1, 3, -2, 3, 5, 6, 2, 4, 3)");
		execute ("equations = MATRIX (A, 3, 3)");
		execute ("A = (5, 7, 8)");
		execute ("solve_solution = SOLVE (equations, A)"); // [-15.0, 8.0, 2.0]
	}

	public void exportTest ()
	{
		execute ("a1 = (-3 +!* 2, - 2 -!* 3, 3 -!* 2)");
		execute ("a2 = (4 +!* 1, 5 +!* 2, -6)");
		execute ("mx = DYADIC(a1,a2)");
		execute ("PRETTYPRINT mx");
		//execute ("EXPORT mx mx.tdf");

		execute ("IMPORT m data.txt");
		execute ("SHOW Symbols");
		execute ("");
		execute ("SAVE export.txt");
	}

	public void importTest ()
	{
		//execute ("@DUMPING = 1");
		execute ("READ export.txt");
		execute ("SHOW Symbols");
		execute ("mxx = mx - 3 * IDENTITY(3)");
		execute ("PRETTYPRINT mxx");
		execute ("mxinv = INV mxx");
		execute ("PRETTYPRINT mxinv 8");
		execute ("mxprd = mxx * mxinv");
		execute ("PRETTYPRINT mxprd 15");
	}

	public static void main(String[] args)
	{
		//net.myorb.math.expressions.gui.DisplayIO.showConsole ("System Output", 700);
		TestComplexControlObject t = new TestComplexControlObject ();
		t.execute ("help");
		
		//t.exportTest ();
		//t.importTest ();

//		t.runQuad ();
//		t.runDump ();
//
//		t.runCubic ();
//		t.runDump ();
//
//		t.runQuart ();
//		t.runDump ();
//
//		t.runRoots ();
//		t.runDump ();
//
//		t.runApproxDer ();
//		t.runDump ();
//
//		t.runAppend ();
//		t.runDump ();
//
//		t.runImport ();
//
//		t.runPoly ();
//		t.runLS ();
//		t.runExpFit ();
		
//		t.runMatrix ();
//		t.runMatrixInv ();
//		t.solve ();
//		t.gaussian ();
//		t.runDump ();
		
//		t.rootTest ();
//		t.primesTest ();

//		t.derivTest ();

//		t.plotTest ();
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

	public void origPlotTest ()
	{
		
		//execute ("@DUMPING = 1");
		execute ("omega = pi / 1024");
		execute ("!! f(x) = cos (x*omega) + 4*cos (2*x*omega) + 8*cos (5*x*omega)");
		execute ("y = [0 <= i <= 511](cos (i*omega) + 4*cos (2*i*omega) + 8*cos (5*i*omega))");
		execute ("GRAPH [-128 <= x <= 128 <> 1](f(x)/20)");
		execute ("FFT y");

		execute ("y = [1 <= i <= 20](ln (i))");
		execute ("x = [1 <= i <= 20](i)");
		execute ("SCATTER x,y");

	}

	public void derivTest ()
	{

		//execute ("@DUMPING = 1");
		execute ("!! f(x) = 9*x^2");

		execute ("x = 0.5");
		execute ("fx = f(x)");
		execute ("dx = 1 / 10^5");
		execute ("d = f'(x <> dx)");
		execute ("d2 = f''(x <> dx)");

		execute ("SHOW Symbols");

	}

	public void primesTest ()
	{

		execute ("RUNSIEVE 75");

		execute ("prim = PRIMES (100)");
		execute ("fact = FACTORS (123456)");
		execute ("pifact = PI fact");

		execute ("gval = gcf (18, 27)");
		execute ("lval = lcm (18, 27)");

		execute ("SHOW Symbols");

	}

	public void primeTest ()
	{
		execute ("RUNSIEVE 75");
		execute ("prim = PRIMES (100)");
		execute ("fact = FACTORS (123456)");
		execute ("pifact = PI fact");
		execute ("SHOW Symbols");
	}


	public void rootTest ()
	{
		execute ("parm = 17");
		execute ("approx = 17/4");
		execute ("f = (-parm, 0, 1)");
		execute ("fPrime = POLYDER f");
		for (int i = 0; i < 20; i++) iterate (i);
	}

	public void iterate (int i)
	{
		execute ("iter = " + i);
		execute ("y = f +*^ approx");
		execute ("yPrime = fPrime +*^ approx");
		execute ("approx = approx - y/yPrime");
		execute ("error = f +*^ approx");
		execute ("SHOW Symbols");
	}

}
