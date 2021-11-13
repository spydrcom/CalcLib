
package net.myorb.math.expressions.gui;

import net.myorb.math.SpaceManager;
import net.myorb.math.expressions.charting.fractals.*;
import net.myorb.math.expressions.*;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * processing actions assigned to buttons of the tabbed panes of the toolbar
 * @author Michael Druckman
 */
public class ToolBarTabbedPanes
{

	
	/**
	 * add button with listener to panel
	 * @param panel the swing panel to be extendsd
	 * @param buttonName the name of the button to be added
	 * @param actionListener the action listener for that button
	 * @param tip the tool tip text to connect to this button
	 * @return button object added
	 */
	public static JButton add (JPanel panel, String buttonName, ActionListener actionListener, String tip)
	{ JButton b; panel.add (b = new JButton (buttonName)); b.addActionListener (actionListener); b.setToolTipText (tip); return b; }


	/*
	 * button processing for tabbed panes
	 */


	/**
	 * menu for the HOME tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel home (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();
		
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

		return panel;
	}


	/**
	 * menu for the DATA tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel data (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();
		
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

		return panel;
	}


	/**
	 * menu for the PRIMES tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel primes (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();

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

		return panel;
	}


	/**
	 * menu for the POLYNOMIAL tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel polynomials (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();
		
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

		return panel;
	}


	/**
	 * menu for the STATISTICS tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel statistics (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();
		
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

		return panel;
	}


	/**
	 * menu for the REGRESSION tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel regression (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();
		
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

		return panel;
	}


	/**
	 * menu for the CHARTS tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel charts (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();
		ActionListener scatter, angular, radial;
		TrackingList list = new TrackingList (processor, SpaceManager.DataType.Complex);

		scatter = new OrderedDualSelectedCommand (OperatorNomenclature.SCATTER_KEYWORD, "SymbolTable", processor, panel);
		angular = new PolarCommand (OperatorNomenclature.POLAR_ANGULAR_KEYWORD, ANGULAR_PROMPTS, list, processor, panel);
		radial = new PolarCommand (OperatorNomenclature.POLAR_RADIAL_KEYWORD, RADIAL_PROMPTS, list, processor, panel);

		add (panel, "Scatter", scatter, "Scatter plot of X/Y data"); // not tied to complex engine

		list.addItem (add (panel, "Angular", angular, "Angular plot of complex plane transform"));
		list.addItem (add (panel, "Radial", radial, "Radial plot of complex plane transform"));
		list.addItem (add (panel, "List", list, "Show list of plots being tracked"));

		return panel;
	}
	static final String[]
	ANGULAR_PROMPTS = new String[]{"Polynomial", "Radial Domain", "Angular Domain", "Color Domain"},
	RADIAL_PROMPTS = new String[]{"Polynomial", "Angular Domain", "Radial Domain", "Color Domain"};


	/**
	 * menu for the CHARTS tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel fractals (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();
		ActionListener julia, mandelbrot, newton;
		TrackingList list = new TrackingList (processor, SpaceManager.DataType.Real);

		julia = new FractalDisplay ("JULIA ", juliaMap, list, processor, panel);
		mandelbrot = new FractalDisplay ("MANDELBROT ", mandelbrotMap, list, processor, panel);
		newton = new FractalDisplay ("NEWTON ", newtonMap, list, processor, panel);

		list.addItem (add (panel, "Julia", julia, "Display the Julia set for a constant"));
		list.addItem (add (panel, "Mandelbrot", mandelbrot, "Display the Mandelbrot set from preset viewpoints"));
		list.addItem (add (panel, "Newton", newton, "Display the available Newton fractal sets"));
		list.addItem (add (panel, "List", list, "Show list of plots being tracked"));

		return panel;
	}
	public static final Map<String,Fractal>
	mandelbrotMap = Mandelbrot.getFractalMap (),
	newtonMap = Newton.getFractalMap (),
	juliaMap = Julia.getFractalMap ();


	/**
	 * menu for the MATRICES tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel matrices (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();
		
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

		return panel;
	}


	/**
	 * menu for Simultaneous Equations tab
	 * @param processor the master command processor object
	 * @return the panel for this tab
	 */
	public static JPanel simulEq (DisplayIO.CommandProcessor processor)
	{
		JPanel panel = new JPanel ();
		
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

		return panel;
	}
	static String[] QRDcommands = new String[]{"GetQrdH", "GetQrdQ", "GetQrdR"}, QRDsimplenames = new String[]{"H", "Q", "R"};
	static String[] SVDcommands = new String[]{"GetSvdS", "GetSvdV", "GetSvdU"}, SVDsimplenames = new String[]{"S", "V", "U"};
	static String[] EVDcommands = new String[]{"GetEvdD", "GetEvdDreal", "GetEvdDimag", "GetEvdV"};
	static String[] EVDsimplenames = new String[]{"D", "Dre", "Dim", "V"};


	/**
	 * @param handler the command processor for the display
	 * @return the tabbed pane with main menu items
	 */
	public static JTabbedPane buildTabbedPane (DisplayIO.CommandProcessor handler)
	{
		JTabbedPane tabs =
				new JTabbedPane ();
		tabs.add ("Home", home (handler));
		tabs.add ("Data", data (handler));
		tabs.add ("Primes", primes (handler));
		tabs.add ("Matrices", matrices (handler));
		tabs.add ("SimulEQ", simulEq (handler));
		tabs.add ("Polynomials", polynomials (handler));
		tabs.add ("Statistics", statistics (handler));
		tabs.add ("Regression", regression (handler));
		tabs.add ("Fractals", fractals (handler));
		tabs.add ("Charts", charts (handler));
		return tabs;
	}


}

