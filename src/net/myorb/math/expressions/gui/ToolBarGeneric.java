
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.OperatorNomenclature;

import net.myorb.math.expressions.charting.fractals.Fractal;
import net.myorb.math.expressions.charting.fractals.Mandelbrot;
import net.myorb.math.expressions.charting.fractals.Newton;
import net.myorb.math.expressions.charting.fractals.Julia;

import net.myorb.math.SpaceManager;

import java.awt.Component;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;


/**
 * abstract and generic version of application menu actions
 * @param <Item> an item of the menu structure being added to a menu
 * @param <Collector> the menu object collecting action items
 * @author Michael Druckman
 */
public abstract class ToolBarGeneric <Item, Collector>
{


	/**
	 * @param item object to be added
	 * @param to the collection added to
	 */
	abstract void add (Item item, Collector to);

	/**
	 * @param item object to be added
	 * @param listener the action to be connected
	 * @param tip the tool tip text for the item
	 */
	abstract void attribute (Item item, ActionListener listener, String tip);

	/**
	 * @param name the name for the item
	 * @return the new item object
	 */
	abstract Item construct (String name);


	/**
	 * @param list a tracking list to be updated
	 * @param from a component being added
	 */
	abstract void addTo (TrackingList list, Item from);


	/**
	 * @param parent the GUI object to treat as collection parent
	 */
	public ToolBarGeneric
		(
			Component parent
		)
	{
		this.parent = parent;
	}
	Component parent;


	/**
	 * generic construction of menu hierarchy
	 * @param collector the collection object for this node
	 * @param name the name of the item being added to the collection
	 * @param actionListener the action to associate with the item
	 * @param tip the tool tip text for the item
	 * @return the constructed Item
	 */
	public Item add
		(
			Collector collector, String name,
			ActionListener actionListener, String tip
		)
	{
		Item item = construct (name);
		attribute (item, actionListener, tip);
		add (item, collector);
		return item;
	}


	/**
	 * menu for the HOME entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void home (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener
		tab = new ShowCommand (processor),
		sym = new SimpleCommand ("SHOW Symbols", processor),
		func = new SimpleCommand ("SHOW Functions", processor),
		save = new DialogCommand ("SAVE ", "Filename for SAVE", "Specify File", processor, parent),
		all = new SimpleCommand ("SHOW ALL", processor),
		help = new SimpleCommand ("HELP", processor),
		rpn = new SimpleCommand ("RPN", processor);

		add (collector, "Tabulate", tab, "Tabulate the files and symbols in the environment");
		add (collector, "Symbols", sym, "Show the symbols currently defined in the environment");
		add (collector, "Functions", func, "Show the user defined functions defined in the environment");
		add (collector, "RPN", rpn, "Reverse Polish Notation calculator on STEROIDS with full stack display");
		add (collector, "Dump", all, "Dump all operators (user and built-in) defined in the environment");
		add (collector, "Save", save, "Save workspace script to a file and export matrices");
		add (collector, "HELP", help, "Display a table of command help information");
	}


	/**
	 * menu for the DATA entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void data (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener
		pi = new SelectedAssignmentCommand (OperatorNomenclature.PI_OPERATOR, "SymbolTable", processor, true, parent),
		sigma = new SelectedAssignmentCommand (OperatorNomenclature.SIGMA_OPERATOR, "SymbolTable", processor, true, parent),
		dyadic = new OrderedDualSelectedAssignmentCommand (OperatorNomenclature.DYADIC_FUNCTION, "SymbolTable", processor, parent),
		augment = new CommutativeBinaryFunctionAssignmentCommand (OperatorNomenclature.AUGMENTED_FUNCTION, processor, parent),
		hypot = new SelectedAssignmentCommand (OperatorNomenclature.HYPOT_FUNCTION, "SymbolTable", processor, true, parent),
		dot = new DualSelectedAssignmentCommand (OperatorNomenclature.DOT_FUNCTION, "SymbolTable", processor, parent);

		add (collector, "PI", pi, "Compute product of the elements of an array");
		add (collector, "SIGMA", sigma, "Compute sum of the elements of an array");
		add (collector, "Hypot", hypot, "Compute root of sum of squares of the elements of an array");
		add (collector, "Augment", augment, "Augment a square matrix with a vector to prepare for Gaussian Elimination");
		add (collector, "Dyadic", dyadic, "Compute dyadic product of two vectors");
		add (collector, "Dot", dot, "Compute dot product of two vectors");
	}


	/**
	 * menu for the PRIMES entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void primes (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener
		gcf = new DualSelectedAssignmentCommand ("gcf", "SymbolTable", processor, parent),
		lcm = new DualSelectedAssignmentCommand ("lcm", "SymbolTable", processor, parent),
		gaps = new PrimesDialogCommand ("PRIMEGAPS ", "Starting at", "Prime Number Gaps", processor, parent),
		table = new PrimesDialogCommand ("PRIMETABLE ", "Starting at", "Prime Number Table", processor, parent),
		factors = new SelectedAssignmentCommand ("FACTORS ", "SymbolTable", processor, true, parent),
		primes = new DialogCommand ("CALC PRIMES ", "Primes Up To", "Specify Upper Limit of Primes", processor, parent),
		sieve = new SieveCommand ("RUNSIEVE ", "Number of Primes", "Specify Count of Primes", processor, parent);

		add (collector, "Sieve", sieve, "Construct a table of primes with the specified size");
		add (collector, "Tabulated", table, "Show a table of the prime factorizations starting as specified");
		add (collector, "Primes", primes, "Get a list of prime number less than the specified limit");
		add (collector, "Factors", factors, "Compute the prime factorization of the value selected");
		add (collector, "Gaps", gaps, "Show a table of the prime number gaps starting as selected");
		add (collector, "GCF", gcf, "Compute the greatest common factor");
		add (collector, "LCM", lcm, "Compute the least common multiple");
	}


	/**
	 * menu for the POLYNOMIAL entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void polynomials (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener
		format = new SelectedCommand ("POLYPRINT ", "SymbolTable", processor, true, parent),
		conv = new DualSelectedAssignmentCommand ("CONV", "SymbolTable", processor, parent),
		roots = new SelectedAssignmentCommand ("ROOTS", "SymbolTable", processor, true, parent),
		derivative = new SelectedAssignmentCommand ("POLYDER", "SymbolTable", processor, true, parent),
		integral = new SelectedAssignmentCommand ("POLYINT", "SymbolTable", processor, true, parent),
		poly = new SelectedCommand ("POLYNOMIAL ", "SymbolTable", processor, true, parent),
		derive = new SelectedCommand ("DERIVE ", "SymbolTable", processor, true, parent);

		add (collector, "Format", format, "Display the formatted polynomial");
		add (collector, "Roots", roots, "Compute the roots of the specified polynomial");
		add (collector, "Characterize", poly, "Plot a polynomial and tabulate characteristics");
		add (collector, "Derive", derive, "Plot a polynomial with derivatives and tabulated values");
		add (collector, "Derivative", derivative, "Compute derivative polynomial from coefficients");
		add (collector, "Integral", integral, "Compute integral polynomial from coefficients");
		add (collector, "Conv", conv, "Compute conv of two polynomials from coefficients");
	}


	/**
	 * menu for the MATRICES entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void matrices (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener
		eig = new EigenvalueSpecialCaseCommand ("EIG", "SymbolTable", processor, parent),
		det = new SelectedAssignmentCommand ("DET", "SymbolTable", processor, true, parent),
		add = new DualSelectedAssignmentCommand ("MATADD", "SymbolTable", processor, parent),
		transpose = new SelectedAssignmentCommand ("TRANSPOSE", "SymbolTable", processor, true, parent),
		mul = new OrderedDualSelectedAssignmentCommand ("MATMUL", "SymbolTable", processor, parent),
		characteristic = new SelectedAssignmentCommand ("CHARACTERISTIC", "SymbolTable", processor, true, parent),
		comatrix = new SelectedAssignmentCommand ("COMATRIX", "SymbolTable", processor, true, parent),
		trace = new SelectedAssignmentCommand ("TR", "SymbolTable", processor, true, parent),
		adj = new SelectedAssignmentCommand ("ADJ", "SymbolTable", processor, true, parent),
		inv = new SelectedAssignmentCommand ("INV", "SymbolTable", processor, true, parent);

		add (collector, "Add", add, "Add two matrices");
		add (collector, "Mul", mul, "Multiply two matrices");
		add (collector, "Det", det, "Determinant of a matrix");
		add (collector, "Eig", eig, "Von Mises dominant Eigen-pair");
		add (collector, "Transpose", transpose, "Transpose of a matrix");
		add (collector, "Characteristic", characteristic, "Characteristic of a matrix");
		add (collector, "Comatrix", comatrix, "Comatrix of a matrix");
		add (collector, "Adj", adj, "Adjugate of a matrix");
		add (collector, "Inv", inv, "Inverse of a matrix");
		add (collector, "Tr", trace, "Trace of a matrix");
	}


	/**
	 * Simultaneous Equations entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void simulEq (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener
		matRpt = new SelectedCommand ("calc MatRpt ", "SymbolTable", processor, true, parent),
		qr = new SelectedSimpleAssignmentCommand (QRDcommands, QRDsimplenames, "SymbolTable", processor, parent),
		svd = new SelectedSimpleAssignmentCommand (SVDcommands, SVDsimplenames, "SymbolTable", processor, parent),
		ev = new SelectedSimpleAssignmentCommand (EVDcommands, EVDsimplenames, "SymbolTable", processor, parent),
		solve = new CommutativeBinaryFunctionAssignmentCommand (OperatorNomenclature.SOLVE_FUNCTION, processor, parent),
		gaussian = new CommutativeBinaryFunctionAssignmentCommand (OperatorNomenclature.GAUSSIAN_FUNCTION, processor, parent),
		VC31 = new SelectedAssignmentCommand ("VC31 ", "SymbolTable", processor, true, parent);

		add (collector, "Report", matRpt, "Produce report of decomposition options and general properties");
		add (collector, "SVD", svd, "Produce Singular Value Decomposition of a matrix in form of S, U, and V matrices");
		add (collector, "QRD", qr, "Produce QR Decomposition of a matrix in form of Q (orthog), R (tri), and H (Householder) matrices");
		add (collector, "Eigen", ev, "Produce Eigenvalue Decomposition of a matrix in form of D (real and imaginary) and V matrices");
		add (collector, "Solve", solve, "Solve N equations in N unknowns using matrix column substitution and determinants");
		add (collector, "Gaussian", gaussian, "Solve N equations in N unknowns using Gaussian Elimination");
		add (collector, "VC31", VC31, "Produce a function interpolation spline using VC31");
	}
	static String[] QRDcommands = new String[]{"GetQrdH", "GetQrdQ", "GetQrdR"}, QRDsimplenames = new String[]{"H", "Q", "R"};
	static String[] SVDcommands = new String[]{"GetSvdS", "GetSvdV", "GetSvdU"}, SVDsimplenames = new String[]{"S", "V", "U"};
	static String[] EVDcommands = new String[]{"GetEvdD", "GetEvdDreal", "GetEvdDimag", "GetEvdV"};
	static String[] EVDsimplenames = new String[]{"D", "Dre", "Dim", "V"};


	/**
	 * menu for the STATISTICS  entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void statistics (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener
		min = new SelectedAssignmentCommand ("MIN", "SymbolTable", processor, true, parent),
		max = new SelectedAssignmentCommand ("MAX", "SymbolTable", processor, true, parent),
		mean = new SelectedAssignmentCommand ("MEAN", "SymbolTable", processor, true, parent),
		median = new SelectedAssignmentCommand ("MEDIAN", "SymbolTable", processor, true, parent),
		stdev = new SelectedAssignmentCommand ("STDEV", "SymbolTable", processor, true, parent),
		var = new SelectedAssignmentCommand ("VAR", "SymbolTable", processor, true, parent),
		cov = new SelectedAssignmentCommand ("COV", "SymbolTable", processor, true, parent);

		add (collector, "Min", min, "Find the minimum value of a sample set");
		add (collector, "Max", max, "Find the maximum value of a sample set");
		add (collector, "Mean", mean, "Compute the mean of a sample set");
		add (collector, "Median", median, "Compute the median of a sample set");
		add (collector, "Stdev", stdev, "Compute the standard deviation of a sample set");
		add (collector, "Var", var, "Compute the variance of a sample set");
		add (collector, "Cov", cov, "Compute the covariance of a sample set");
	}


	/**
	 * menu for the REGRESSION  entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void regression (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener
		fft = new SelectedCommand ("FFT ", "SymbolTable", processor, true, parent),
		poly = new OrderedDualSelectedAssignmentCommand ("FITPOLY", "SymbolTable", processor, parent),
		linear = new OrderedDualSelectedAssignmentCommand ("FITLINE", "SymbolTable", processor, parent),
		nonlinear = new OrderedDualSelectedAssignmentCommand ("FITEXP", "SymbolTable", processor, parent),
		lagrange = new OrderedDualSelectedAssignmentCommand ("LAGRANGE", "SymbolTable", processor, parent),
		chebyshev = new OrderedDualSelectedAssignmentCommand ("CHEBYSHEV", "SymbolTable", processor, parent),
		gauss = new OrderedDualSelectedAssignmentCommand ("GAUSSQUAD", "SymbolTable", processor, parent),
		harmonic = new HarmonicRegression (processor, parent),
		series = new SeriesRegression (processor, parent);

		Map<String,ActionListener> map = new HashMap<String,ActionListener>();
		map.put ("Vandermonde", poly); map.put ("Lagrange", lagrange); map.put ("Chebyshev", chebyshev); map.put ("GaussQuad", gauss);

		add (collector, "FFT", fft, "Fast Fourier Transform");
		add (collector, "Linear", linear, "Linear (least squares) regression");
		add (collector, "Non-Linear", nonlinear, "Non-Linear (logarithmic) regression");
		add (collector, "Polynomial", new PolynomialRegression (map), "Polynomial interpolation using Linear Algebra solutions for coefficients");
		add (collector, "Harmonic", harmonic, "Harmonic (Fourier Series) regression");
		add (collector, "Time Series", series, "Cyclic time series analysis");
	}


	/**
	 * menu for the CHARTS  entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void charts (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener scatter, angular, radial;
		TrackingList list = new TrackingList (processor, SpaceManager.DataType.Complex);

		scatter = new OrderedDualSelectedCommand (OperatorNomenclature.SCATTER_KEYWORD, "SymbolTable", processor, parent);
		angular = new PolarCommand (OperatorNomenclature.POLAR_ANGULAR_KEYWORD, ANGULAR_PROMPTS, list, processor, parent);
		radial = new PolarCommand (OperatorNomenclature.POLAR_RADIAL_KEYWORD, RADIAL_PROMPTS, list, processor, parent);

		add (collector, "Scatter", scatter, "Scatter plot of X/Y data"); // not tied to complex engine

		addTo (list, add (collector, "Angular", angular, "Angular plot of complex plane transform"));
		addTo (list, add (collector, "Radial", radial, "Radial plot of complex plane transform"));
		addTo (list, add (collector, "List", list, "Show list of plots being tracked"));
	}
	static final String[]
	ANGULAR_PROMPTS = new String[]{"Polynomial", "Radial Domain", "Angular Domain", "Color Domain"},
	RADIAL_PROMPTS = new String[]{"Polynomial", "Angular Domain", "Radial Domain", "Color Domain"};


	/**
	 * menu for the CHARTS entry
	 * @param collector the object collecting the action items
	 * @param processor the command processor for the application
	 */
	public void fractals (Collector collector, DisplayIO.CommandProcessor processor)
	{
		ActionListener julia, mandelbrot, newton;
		TrackingList list = new TrackingList (processor, SpaceManager.DataType.Real);

		julia = new FractalDisplay ("JULIA ", juliaMap, list, processor, parent);
		mandelbrot = new FractalDisplay ("MANDELBROT ", mandelbrotMap, list, processor, parent);
		newton = new FractalDisplay ("NEWTON ", newtonMap, list, processor, parent);

		addTo (list, add (collector, "Julia", julia, "Display the Julia set for a constant"));
		addTo (list, add (collector, "Mandelbrot", mandelbrot, "Display the Mandelbrot set from preset viewpoints"));
		addTo (list, add (collector, "Newton", newton, "Display the available Newton fractal sets"));
		addTo (list, add (collector, "List", list, "Show list of plots being tracked"));
	}
	public static final Map<String,Fractal>
	mandelbrotMap = Mandelbrot.getFractalMap (),
	newtonMap = Newton.getFractalMap (),
	juliaMap = Julia.getFractalMap ();


}

