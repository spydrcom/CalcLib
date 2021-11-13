
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.OperatorNomenclature;

import net.myorb.math.expressions.charting.fractals.Fractal;
import net.myorb.math.expressions.charting.fractals.Mandelbrot;
import net.myorb.math.expressions.charting.fractals.Newton;
import net.myorb.math.expressions.charting.fractals.Julia;

import net.myorb.math.SpaceManager;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * copy-paste version of menu items 
 *  taken from debug sand-box version of GUI
 * @author Michael Druckman
 */
public class ToolBarMenu
{


	/**
	 * @param menu the menu being constructed
	 * @param name the name of the item to be added
	 * @param actionListener the action to be connected
	 * @param tip the tool tip text for the item
	 */
	public static void add
	(JMenu menu, String name, ActionListener actionListener, String tip)
	{
		JMenuItem item = new JMenuItem (name);
		item.addActionListener (actionListener);
		item.setToolTipText (tip);
		menu.add (item);
	}


	/**
	 * menu for the HOME entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void home (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Home");
		
		ActionListener
		tab = new ShowCommand (processor),
		sym = new SimpleCommand ("SHOW Symbols", processor),
		func = new SimpleCommand ("SHOW Functions", processor),
		save = new DialogCommand ("SAVE ", "Filename for SAVE", "Specify File", processor, panel),
		all = new SimpleCommand ("SHOW ALL", processor),
		help = new SimpleCommand ("HELP", processor),
		rpn = new SimpleCommand ("RPN", processor);

		add (panel, "Tabulate", tab, "Tabulate the files and symbols in the environment");
		add (panel, "Symbols", sym, "Show the symbols currently defined in the environment");
		add (panel, "Functions", func, "Show the user defined functions defined in the environment");
		add (panel, "RPN", rpn, "Reverse Polish Notation calculator on STEROIDS with full stack display");
		add (panel, "Dump", all, "Dump all operators (user and built-in) defined in the environment");
		add (panel, "Save", save, "Save workspace script to a file and export matrices");
		add (panel, "HELP", help, "Display a table of command help information");

		bar.add (panel);
	}


	/**
	 * menu for the DATA entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void data (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Data");
		
		ActionListener
		pi = new SelectedAssignmentCommand (OperatorNomenclature.PI_OPERATOR, "SymbolTable", processor, true, panel),
		sigma = new SelectedAssignmentCommand (OperatorNomenclature.SIGMA_OPERATOR, "SymbolTable", processor, true, panel),
		dyadic = new OrderedDualSelectedAssignmentCommand (OperatorNomenclature.DYADIC_FUNCTION, "SymbolTable", processor, panel),
		augment = new CommutativeBinaryFunctionAssignmentCommand (OperatorNomenclature.AUGMENTED_FUNCTION, processor, panel),
		hypot = new SelectedAssignmentCommand (OperatorNomenclature.HYPOT_FUNCTION, "SymbolTable", processor, true, panel),
		dot = new DualSelectedAssignmentCommand (OperatorNomenclature.DOT_FUNCTION, "SymbolTable", processor, panel);

		add (panel, "PI", pi, "Compute product of the elements of an array");
		add (panel, "SIGMA", sigma, "Compute sum of the elements of an array");
		add (panel, "Hypot", hypot, "Compute root of sum of squares of the elements of an array");
		add (panel, "Augment", augment, "Augment a square matrix with a vector to prepare for Gaussian Elimination");
		add (panel, "Dyadic", dyadic, "Compute dyadic product of two vectors");
		add (panel, "Dot", dot, "Compute dot product of two vectors");

		bar.add (panel);
	}


	/**
	 * menu for the PRIMES entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void primes (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Primes");

		ActionListener
		gcf = new DualSelectedAssignmentCommand ("gcf", "SymbolTable", processor, panel),
		lcm = new DualSelectedAssignmentCommand ("lcm", "SymbolTable", processor, panel),
		gaps = new PrimesDialogCommand ("PRIMEGAPS ", "Starting at", "Prime Number Gaps", processor, panel),
		table = new PrimesDialogCommand ("PRIMETABLE ", "Starting at", "Prime Number Table", processor, panel),
		factors = new SelectedAssignmentCommand ("FACTORS ", "SymbolTable", processor, true, panel),
		primes = new DialogCommand ("CALC PRIMES ", "Primes Up To", "Specify Upper Limit of Primes", processor, panel),
		sieve = new SieveCommand ("RUNSIEVE ", "Number of Primes", "Specify Count of Primes", processor, panel);

		add (panel, "Sieve", sieve, "Construct a table of primes with the specified size");
		add (panel, "Tabulated", table, "Show a table of the prime factorizations starting as specified");
		add (panel, "Primes", primes, "Get a list of prime number less than the specified limit");
		add (panel, "Factors", factors, "Compute the prime factorization of the value selected");
		add (panel, "Gaps", gaps, "Show a table of the prime number gaps starting as selected");
		add (panel, "GCF", gcf, "Compute the greatest common factor");
		add (panel, "LCM", lcm, "Compute the least common multiple");

		bar.add (panel);
	}


	/**
	 * menu for the POLYNOMIAL entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void polynomials (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Polynomial");
		
		ActionListener
		format = new SelectedCommand ("POLYPRINT ", "SymbolTable", processor, true, panel),
		conv = new DualSelectedAssignmentCommand ("CONV", "SymbolTable", processor, panel),
		roots = new SelectedAssignmentCommand ("ROOTS", "SymbolTable", processor, true, panel),
		derivative = new SelectedAssignmentCommand ("POLYDER", "SymbolTable", processor, true, panel),
		integral = new SelectedAssignmentCommand ("POLYINT", "SymbolTable", processor, true, panel),
		poly = new SelectedCommand ("POLYNOMIAL ", "SymbolTable", processor, true, panel),
		derive = new SelectedCommand ("DERIVE ", "SymbolTable", processor, true, panel);

		add (panel, "Format", format, "Display the formatted polynomial");
		add (panel, "Roots", roots, "Compute the roots of the specified polynomial");
		add (panel, "Characterize", poly, "Plot a polynomial and tabulate characteristics");
		add (panel, "Derive", derive, "Plot a polynomial with derivatives and tabulated values");
		add (panel, "Derivative", derivative, "Compute derivative polynomial from coefficients");
		add (panel, "Integral", integral, "Compute integral polynomial from coefficients");
		add (panel, "Conv", conv, "Compute conv of two polynomials from coefficients");

		bar.add (panel);
	}


	/**
	 * menu for the MATRICES entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void matrices (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Matrices");
		
		ActionListener
		eig = new EigenvalueSpecialCaseCommand ("EIG", "SymbolTable", processor, panel),
		det = new SelectedAssignmentCommand ("DET", "SymbolTable", processor, true, panel),
		add = new DualSelectedAssignmentCommand ("MATADD", "SymbolTable", processor, panel),
		transpose = new SelectedAssignmentCommand ("TRANSPOSE", "SymbolTable", processor, true, panel),
		mul = new OrderedDualSelectedAssignmentCommand ("MATMUL", "SymbolTable", processor, panel),
		characteristic = new SelectedAssignmentCommand ("CHARACTERISTIC", "SymbolTable", processor, true, panel),
		comatrix = new SelectedAssignmentCommand ("COMATRIX", "SymbolTable", processor, true, panel),
		trace = new SelectedAssignmentCommand ("TR", "SymbolTable", processor, true, panel),
		adj = new SelectedAssignmentCommand ("ADJ", "SymbolTable", processor, true, panel),
		inv = new SelectedAssignmentCommand ("INV", "SymbolTable", processor, true, panel);

		add (panel, "Add", add, "Add two matrices");
		add (panel, "Mul", mul, "Multiply two matrices");
		add (panel, "Det", det, "Determinant of a matrix");
		add (panel, "Eig", eig, "Von Mises dominant Eigen-pair");
		add (panel, "Transpose", transpose, "Transpose of a matrix");
		add (panel, "Characteristic", characteristic, "Characteristic of a matrix");
		add (panel, "Comatrix", comatrix, "Comatrix of a matrix");
		add (panel, "Adj", adj, "Adjugate of a matrix");
		add (panel, "Inv", inv, "Inverse of a matrix");
		add (panel, "Tr", trace, "Trace of a matrix");

		bar.add (panel);
	}


	/**
	 * Simultaneous Equations entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void simulEq (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("SimulEq");
		
		ActionListener
		matRpt = new SelectedCommand ("calc MatRpt ", "SymbolTable", processor, true, panel),
		qr = new SelectedSimpleAssignmentCommand (QRDcommands, QRDsimplenames, "SymbolTable", processor, panel),
		svd = new SelectedSimpleAssignmentCommand (SVDcommands, SVDsimplenames, "SymbolTable", processor, panel),
		ev = new SelectedSimpleAssignmentCommand (EVDcommands, EVDsimplenames, "SymbolTable", processor, panel),
		solve = new CommutativeBinaryFunctionAssignmentCommand (OperatorNomenclature.SOLVE_FUNCTION, processor, panel),
		gaussian = new CommutativeBinaryFunctionAssignmentCommand (OperatorNomenclature.GAUSSIAN_FUNCTION, processor, panel),
		VC31 = new SelectedAssignmentCommand ("VC31 ", "SymbolTable", processor, true, panel);

		add (panel, "Report", matRpt, "Produce report of decomposition options and general properties");
		add (panel, "SVD", svd, "Produce Singular Value Decomposition of a matrix in form of S, U, and V matrices");
		add (panel, "QRD", qr, "Produce QR Decomposition of a matrix in form of Q (orthog), R (tri), and H (Householder) matrices");
		add (panel, "Eigen", ev, "Produce Eigenvalue Decomposition of a matrix in form of D (real and imaginary) and V matrices");
		add (panel, "Solve", solve, "Solve N equations in N unknowns using matrix column substitution and determinants");
		add (panel, "Gaussian", gaussian, "Solve N equations in N unknowns using Gaussian Elimination");
		add (panel, "VC31", VC31, "Produce a function interpolation spline using VC31");

		bar.add (panel);
	}
	static String[] QRDcommands = new String[]{"GetQrdH", "GetQrdQ", "GetQrdR"}, QRDsimplenames = new String[]{"H", "Q", "R"};
	static String[] SVDcommands = new String[]{"GetSvdS", "GetSvdV", "GetSvdU"}, SVDsimplenames = new String[]{"S", "V", "U"};
	static String[] EVDcommands = new String[]{"GetEvdD", "GetEvdDreal", "GetEvdDimag", "GetEvdV"};
	static String[] EVDsimplenames = new String[]{"D", "Dre", "Dim", "V"};


	/**
	 * menu for the STATISTICS  entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void statistics (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Statistics");
		
		ActionListener
		min = new SelectedAssignmentCommand ("MIN", "SymbolTable", processor, true, panel),
		max = new SelectedAssignmentCommand ("MAX", "SymbolTable", processor, true, panel),
		mean = new SelectedAssignmentCommand ("MEAN", "SymbolTable", processor, true, panel),
		median = new SelectedAssignmentCommand ("MEDIAN", "SymbolTable", processor, true, panel),
		stdev = new SelectedAssignmentCommand ("STDEV", "SymbolTable", processor, true, panel),
		var = new SelectedAssignmentCommand ("VAR", "SymbolTable", processor, true, panel),
		cov = new SelectedAssignmentCommand ("COV", "SymbolTable", processor, true, panel);

		add (panel, "Min", min, "Find the minimum value of a sample set");
		add (panel, "Max", max, "Find the maximum value of a sample set");
		add (panel, "Mean", mean, "Compute the mean of a sample set");
		add (panel, "Median", median, "Compute the median of a sample set");
		add (panel, "Stdev", stdev, "Compute the standard deviation of a sample set");
		add (panel, "Var", var, "Compute the variance of a sample set");
		add (panel, "Cov", cov, "Compute the covariance of a sample set");

		bar.add (panel);
	}


	/**
	 * menu for the REGRESSION  entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void regression (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Regression");
		
		ActionListener
		fft = new SelectedCommand ("FFT ", "SymbolTable", processor, true, panel),
		poly = new OrderedDualSelectedAssignmentCommand ("FITPOLY", "SymbolTable", processor, panel),
		linear = new OrderedDualSelectedAssignmentCommand ("FITLINE", "SymbolTable", processor, panel),
		nonlinear = new OrderedDualSelectedAssignmentCommand ("FITEXP", "SymbolTable", processor, panel),
		lagrange = new OrderedDualSelectedAssignmentCommand ("LAGRANGE", "SymbolTable", processor, panel),
		chebyshev = new OrderedDualSelectedAssignmentCommand ("CHEBYSHEV", "SymbolTable", processor, panel),
		gauss = new OrderedDualSelectedAssignmentCommand ("GAUSSQUAD", "SymbolTable", processor, panel),
		harmonic = new HarmonicRegression (processor, panel),
		series = new SeriesRegression (processor, panel);

		Map<String,ActionListener> map = new HashMap<String,ActionListener>();
		map.put ("Vandermonde", poly); map.put ("Lagrange", lagrange); map.put ("Chebyshev", chebyshev); map.put ("GaussQuad", gauss);

		add (panel, "FFT", fft, "Fast Fourier Transform");
		add (panel, "Linear", linear, "Linear (least squares) regression");
		add (panel, "Non-Linear", nonlinear, "Non-Linear (logarithmic) regression");
		add (panel, "Polynomial", new PolynomialRegression (map), "Polynomial interpolation using Linear Algebra solutions for coefficients");
		add (panel, "Harmonic", harmonic, "Harmonic (Fourier Series) regression");
		add (panel, "Time Series", series, "Cyclic time series analysis");

		bar.add (panel);
	}


	/**
	 * menu for the CHARTS  entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void charts (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Charts");
		ActionListener scatter, angular, radial;
		TrackingList list = new TrackingList (processor, SpaceManager.DataType.Complex);

		scatter = new OrderedDualSelectedCommand (OperatorNomenclature.SCATTER_KEYWORD, "SymbolTable", processor, panel);
		angular = new PolarCommand (OperatorNomenclature.POLAR_ANGULAR_KEYWORD, ANGULAR_PROMPTS, list, processor, panel);
		radial = new PolarCommand (OperatorNomenclature.POLAR_RADIAL_KEYWORD, RADIAL_PROMPTS, list, processor, panel);

		add (panel, "Scatter", scatter, "Scatter plot of X/Y data"); // not tied to complex engine

		add (panel, "Angular", angular, "Angular plot of complex plane transform");
		add (panel, "Radial", radial, "Radial plot of complex plane transform");
		add (panel, "List", list, "Show list of plots being tracked");

		bar.add (panel);
	}
	static final String[]
	ANGULAR_PROMPTS = new String[]{"Polynomial", "Radial Domain", "Angular Domain", "Color Domain"},
	RADIAL_PROMPTS = new String[]{"Polynomial", "Angular Domain", "Radial Domain", "Color Domain"};


	/**
	 * menu for the CHARTS entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void fractals (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu menu = new JMenu ("Fractals");
		ActionListener julia, mandelbrot, newton;
		TrackingList list = new TrackingList (processor, SpaceManager.DataType.Real);

		julia = new FractalDisplay ("JULIA ", juliaMap, list, processor, menu);
		mandelbrot = new FractalDisplay ("MANDELBROT ", mandelbrotMap, list, processor, menu);
		newton = new FractalDisplay ("NEWTON ", newtonMap, list, processor, menu);

		add (menu, "Julia", julia, "Display the Julia set for a constant");
		add (menu, "Mandelbrot", mandelbrot, "Display the Mandelbrot set from preset viewpoints");
		add (menu, "Newton", newton, "Display the available Newton fractal sets");
		add (menu, "List", list, "Show list of plots being tracked");

		bar.add (menu);
	}
	public static final Map<String,Fractal>
	mandelbrotMap = Mandelbrot.getFractalMap (),
	newtonMap = Newton.getFractalMap (),
	juliaMap = Julia.getFractalMap ();


	/**
	 * construction for application menu bar
	 * @param processor the command processor for the application
	 * @return a menu bar for the application
	 */
	public static JMenuBar getMasterMenuBar
			(DisplayIO.CommandProcessor processor)
	{
		JMenuBar menu = new JMenuBar ();

		home (menu, processor);
		data (menu, processor);
		primes (menu, processor);
		polynomials (menu, processor);
		matrices (menu, processor);
		simulEq (menu, processor);
		regression (menu, processor);
		statistics (menu, processor);
		fractals (menu, processor);
		charts (menu, processor);

		return menu;
	}


}
