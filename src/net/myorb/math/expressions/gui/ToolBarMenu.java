
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.OperatorNomenclature;

import net.myorb.math.expressions.charting.fractals.*;

import net.myorb.math.expressions.gui.Functionality;

import net.myorb.math.SpaceManager;

import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

import java.awt.event.ActionListener;
import java.awt.Component;

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
	 * collection of action listeners for HOME menu
	 */
	public static class HomeActions implements Functionality.Home
	{
		public ActionListener getTabAction () { return tab; }
		public ActionListener getSymAction () { return sym; }
		public ActionListener getFuncAction () { return func; }
		public ActionListener getSaveAction () { return save; }
		public ActionListener getHelpAction () { return help; }
		public ActionListener getAllAction () { return all; }
		public ActionListener getRpnAction () { return rpn; }

		public HomeActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			tab = new ShowCommand (processor);
			sym = new SimpleCommand ("SHOW Symbols", processor);
			func = new SimpleCommand ("SHOW Functions", processor);
			save = new DialogCommand ("SAVE ", "Filename for SAVE", "Specify File", processor, parent);
			all = new SimpleCommand ("SHOW ALL", processor);
			help = new SimpleCommand ("HELP", processor);
			rpn = new SimpleCommand ("RPN", processor);
		}
		ActionListener tab, sym, func, save, all, help, rpn;
	}

	/**
	 * menu for the HOME entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void home (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Home");
		HomeActions act = new HomeActions (processor, panel);
		
		add (panel, "Tabulate", act.getTabAction (), "Tabulate the files and symbols in the environment");
		add (panel, "Symbols", act.getSymAction (), "Show the symbols currently defined in the environment");
		add (panel, "Functions", act.getFuncAction (), "Show the user defined functions defined in the environment");
		add (panel, "RPN", act.getRpnAction (), "Reverse Polish Notation calculator on STEROIDS with full stack display");
		add (panel, "Dump", act.getAllAction (), "Dump all operators (user and built-in) defined in the environment");
		add (panel, "Save", act.getSaveAction (), "Save workspace script to a file and export matrices");
		add (panel, "HELP", act.getHelpAction (), "Display a table of command help information");

		bar.add (panel);
	}


	/**
	 * collection of action listeners for DATA menu
	 */
	public static class DataActions implements Functionality.Data
	{
		public ActionListener getPiAction () { return pi; }
		public ActionListener getSigmaAction () { return sigma; }
		public ActionListener getDyadicAction () { return dyadic; }
		public ActionListener getAugmentAction () { return augment; }
		public ActionListener getHypotAction () { return hypot; }
		public ActionListener getDotAction () { return dot; }

		public DataActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			pi = new SelectedAssignmentCommand (OperatorNomenclature.PI_OPERATOR, "SymbolTable", processor, true, parent);
			sigma = new SelectedAssignmentCommand (OperatorNomenclature.SIGMA_OPERATOR, "SymbolTable", processor, true, parent);
			dyadic = new OrderedDualSelectedAssignmentCommand (OperatorNomenclature.DYADIC_FUNCTION, "SymbolTable", processor, parent);
			augment = new CommutativeBinaryFunctionAssignmentCommand (OperatorNomenclature.AUGMENTED_FUNCTION, processor, parent);
			hypot = new SelectedAssignmentCommand (OperatorNomenclature.HYPOT_FUNCTION, "SymbolTable", processor, true, parent);
			dot = new DualSelectedAssignmentCommand (OperatorNomenclature.DOT_FUNCTION, "SymbolTable", processor, parent);
		}
		ActionListener pi, sigma, dyadic, augment, hypot, dot;
	}

	/**
	 * menu for the DATA entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void data (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Data");
		DataActions act = new DataActions (processor, panel);

		add (panel, "PI", act.getPiAction (), "Compute product of the elements of an array");
		add (panel, "SIGMA", act.getSigmaAction (), "Compute sum of the elements of an array");
		add (panel, "Hypot", act.getHypotAction (), "Compute root of sum of squares of the elements of an array");
		add (panel, "Augment", act.getAugmentAction (), "Augment a square matrix with a vector to prepare for Gaussian Elimination");
		add (panel, "Dyadic", act.getDyadicAction (), "Compute dyadic product of two vectors");
		add (panel, "Dot", act.getDotAction (), "Compute dot product of two vectors");

		bar.add (panel);
	}


	/**
	 * collection of action listeners for PRIMES menu
	 */
	public static class PrimesActions implements Functionality.Primes
	{
		public ActionListener getGcfAction () { return gcf; }
		public ActionListener getGapsAction () { return gaps; }
		public ActionListener getTableAction () { return table; }
		public ActionListener getPrimesAction () { return primes; }
		public ActionListener getFactorsAction () { return factors; }
		public ActionListener getSieveAction () { return sieve; }
		public ActionListener getLcmAction () { return lcm; }

		public PrimesActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			gcf = new DualSelectedAssignmentCommand ("gcf", "SymbolTable", processor, parent);
			gaps = new PrimesDialogCommand ("PRIMEGAPS ", "Starting at", "Prime Number Gaps", processor, parent);
			table = new PrimesDialogCommand ("PRIMETABLE ", "Starting at", "Prime Number Table", processor, parent);
			primes = new DialogCommand ("CALC PRIMES ", "Primes Up To", "Specify Upper Limit of Primes", processor, parent);
			sieve = new SieveCommand ("RUNSIEVE ", "Number of Primes", "Specify Count of Primes", processor, parent);
			factors = new SelectedAssignmentCommand ("FACTORS ", "SymbolTable", processor, true, parent);
			lcm = new DualSelectedAssignmentCommand ("lcm", "SymbolTable", processor, parent);
		}
		ActionListener gcf, lcm, gaps, table, factors, primes, sieve;
	}

	/**
	 * menu for the PRIMES entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void primes (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Primes");
		PrimesActions act = new PrimesActions (processor, panel);

		add (panel, "Sieve", act.getSieveAction (), "Construct a table of primes with the specified size");
		add (panel, "Primes", act.getPrimesAction (), "Get a list of prime number less than the specified limit");
		add (panel, "Tabulated", act.getTableAction (), "Show a table of the prime factorizations starting as specified");
		add (panel, "Factors", act.getFactorsAction (), "Compute the prime factorization of the value selected");
		add (panel, "Gaps", act.getGapsAction (), "Show a table of the prime number gaps starting as selected");
		add (panel, "GCF", act.getGcfAction (), "Compute the greatest common factor");
		add (panel, "LCM", act.getLcmAction (), "Compute the least common multiple");

		bar.add (panel);
	}


	/**
	 * collection of action listeners for POLYNOMIAL menu
	 */
	public static class PolynomialActions implements Functionality.Polynomials
	{
		public ActionListener getConvAction () { return conv; }
		public ActionListener getRootsAction () { return roots; }
		public ActionListener getFormatAction () { return format; }
		public ActionListener getDerivativeAction () { return derivative; }
		public ActionListener getIntegralAction () { return integral; }
		public ActionListener getDeriveAction () { return derive; }
		public ActionListener getPolyAction () { return poly; }

		public PolynomialActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			format = new SelectedCommand ("POLYPRINT ", "SymbolTable", processor, true, parent);
			conv = new DualSelectedAssignmentCommand ("CONV", "SymbolTable", processor, parent);
			roots = new SelectedAssignmentCommand ("ROOTS", "SymbolTable", processor, true, parent);
			derivative = new SelectedAssignmentCommand ("POLYDER", "SymbolTable", processor, true, parent);
			integral = new SelectedAssignmentCommand ("POLYINT", "SymbolTable", processor, true, parent);
			poly = new SelectedCommand ("POLYNOMIAL ", "SymbolTable", processor, true, parent);
			derive = new SelectedCommand ("DERIVE ", "SymbolTable", processor, true, parent);
		}
		ActionListener format, conv, roots, derivative, integral, derive, poly;
	}

	/**
	 * menu for the POLYNOMIAL entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void polynomials (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Polynomial");
		PolynomialActions act = new PolynomialActions (processor, panel);

		add (panel, "Format", act.getFormatAction (), "Display the formatted polynomial");
		add (panel, "Roots", act.getRootsAction (), "Compute the roots of the specified polynomial");
		add (panel, "Characterize", act.getPolyAction (), "Plot a polynomial and tabulate characteristics");
		add (panel, "Derive", act.getDeriveAction (), "Plot a polynomial with derivatives and tabulated values");
		add (panel, "Derivative", act.getDerivativeAction (), "Compute derivative polynomial from coefficients");
		add (panel, "Integral", act.getIntegralAction (), "Compute integral polynomial from coefficients");
		add (panel, "Conv", act.getConvAction (), "Compute conv of two polynomials from coefficients");

		bar.add (panel);
	}


	/**
	 * collection of action listeners for MATRICES menu
	 */
	public static class MatrixActions implements Functionality.Matrix
	{
		public ActionListener getEigAction () { return eig; }
		public ActionListener getTransposeAction () { return transpose; }
		public ActionListener getCharacteristicAction () { return characteristic; }
		public ActionListener getComatrixAction () { return comatrix; }
		public ActionListener getTraceAction () { return trace; }
		public ActionListener getDetAction () { return det; }
		public ActionListener getAddAction () { return add; }
		public ActionListener getMulAction () { return mul; }
		public ActionListener getInvAction () { return inv; }
		public ActionListener getAdjAction () { return adj; }

		public MatrixActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			eig = new EigenvalueSpecialCaseCommand ("EIG", "SymbolTable", processor, parent);
			det = new SelectedAssignmentCommand ("DET", "SymbolTable", processor, true, parent);
			add = new DualSelectedAssignmentCommand ("MATADD", "SymbolTable", processor, parent);
			mul = new OrderedDualSelectedAssignmentCommand ("MATMUL", "SymbolTable", processor, parent);
			transpose = new SelectedAssignmentCommand ("TRANSPOSE", "SymbolTable", processor, true, parent);
			characteristic = new SelectedAssignmentCommand ("CHARACTERISTIC", "SymbolTable", processor, true, parent);
			comatrix = new SelectedAssignmentCommand ("COMATRIX", "SymbolTable", processor, true, parent);
			trace = new SelectedAssignmentCommand ("TR", "SymbolTable", processor, true, parent);
			adj = new SelectedAssignmentCommand ("ADJ", "SymbolTable", processor, true, parent);
			inv = new SelectedAssignmentCommand ("INV", "SymbolTable", processor, true, parent);
		}
		ActionListener eig, det, add, comatrix, trace, transpose, characteristic, mul, inv, adj;
	}

	/**
	 * menu for the MATRICES entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void matrices (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Matrices");
		MatrixActions act = new MatrixActions (processor, panel);

		add (panel, "Add", act.getAddAction (), "Add two matrices");
		add (panel, "Mul", act.getMulAction (), "Multiply two matrices");
		add (panel, "Det", act.getDetAction (), "Determinant of a matrix");
		add (panel, "Eig", act.getEigAction (), "Von Mises dominant Eigen-pair");
		add (panel, "Transpose", act.getTransposeAction (), "Transpose of a matrix");
		add (panel, "Characteristic", act.getCharacteristicAction (), "Characteristic of a matrix");
		add (panel, "Comatrix", act.getComatrixAction (), "Comatrix of a matrix");
		add (panel, "Adj", act.getAdjAction (), "Adjugate of a matrix");
		add (panel, "Inv", act.getInvAction (), "Inverse of a matrix");
		add (panel, "Tr", act.getTraceAction (), "Trace of a matrix");

		bar.add (panel);
	}


	/**
	 * collection of action listeners for Simultaneous Equations menu
	 */
	public static class SimEqActions implements Functionality.SimEq
	{
		public ActionListener getEvAction () { return ev; }
		public ActionListener getSvdAction () { return svd; }
		public ActionListener getSolveAction () { return solve; }
		public ActionListener getGaussianAction () { return gaussian; }
		public ActionListener getMatRptAction () { return matRpt; }
		public ActionListener getVC31Action () { return VC31; }
		public ActionListener getQrAction () { return qr; }

		public SimEqActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			matRpt = new SelectedCommand ("calc MatRpt ", "SymbolTable", processor, true, parent);
			qr = new SelectedSimpleAssignmentCommand (QRDcommands, QRDsimplenames, "SymbolTable", processor, parent);
			svd = new SelectedSimpleAssignmentCommand (SVDcommands, SVDsimplenames, "SymbolTable", processor, parent);
			ev = new SelectedSimpleAssignmentCommand (EVDcommands, EVDsimplenames, "SymbolTable", processor, parent);
			solve = new CommutativeBinaryFunctionAssignmentCommand (OperatorNomenclature.SOLVE_FUNCTION, processor, parent);
			gaussian = new CommutativeBinaryFunctionAssignmentCommand (OperatorNomenclature.GAUSSIAN_FUNCTION, processor, parent);
			VC31 = new SelectedAssignmentCommand ("VC31 ", "SymbolTable", processor, true, parent);
		}
		ActionListener qr, svd, ev, solve, VC31, matRpt, gaussian;
	}
	static String[] QRDcommands = new String[]{"GetQrdH", "GetQrdQ", "GetQrdR"}, QRDsimplenames = new String[]{"H", "Q", "R"};
	static String[] SVDcommands = new String[]{"GetSvdS", "GetSvdV", "GetSvdU"}, SVDsimplenames = new String[]{"S", "V", "U"};
	static String[] EVDcommands = new String[]{"GetEvdD", "GetEvdDreal", "GetEvdDimag", "GetEvdV"};
	static String[] EVDsimplenames = new String[]{"D", "Dre", "Dim", "V"};

	/**
	 * Simultaneous Equations entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void simulEq (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("SimulEq");
		SimEqActions act = new SimEqActions (processor, panel);

		add (panel, "Report", act.getMatRptAction (), "Produce report of decomposition options and general properties");
		add (panel, "SVD", act.getSvdAction (), "Produce Singular Value Decomposition of a matrix in form of S, U, and V matrices");
		add (panel, "QRD", act.getQrAction (), "Produce QR Decomposition of a matrix in form of Q (orthog), R (tri), and H (Householder) matrices");
		add (panel, "Eigen", act.getEvAction (), "Produce Eigenvalue Decomposition of a matrix in form of D (real and imaginary) and V matrices");
		add (panel, "Solve", act.getSolveAction (), "Solve N equations in N unknowns using matrix column substitution and determinants");
		add (panel, "Gaussian", act.getGaussianAction (), "Solve N equations in N unknowns using Gaussian Elimination");
		add (panel, "VC31", act.getVC31Action (), "Produce a function interpolation spline using VC31");

		bar.add (panel);
	}


	/**
	 * collection of action listeners for STATISTICS menu
	 */
	public static class StatisticsActions implements Functionality.Statistics
	{
		public ActionListener getMinAction () { return min; }
		public ActionListener getMaxAction () { return max; }
		public ActionListener getMeanAction () { return mean; }
		public ActionListener getMedianAction () { return median; }
		public ActionListener getStDevAction () { return stdev; }
		public ActionListener getVarAction () { return var; }
		public ActionListener getCovAction () { return cov; }

		public StatisticsActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			min = new SelectedAssignmentCommand ("MIN", "SymbolTable", processor, true, parent);
			max = new SelectedAssignmentCommand ("MAX", "SymbolTable", processor, true, parent);
			mean = new SelectedAssignmentCommand ("MEAN", "SymbolTable", processor, true, parent);
			median = new SelectedAssignmentCommand ("MEDIAN", "SymbolTable", processor, true, parent);
			stdev = new SelectedAssignmentCommand ("STDEV", "SymbolTable", processor, true, parent);
			var = new SelectedAssignmentCommand ("VAR", "SymbolTable", processor, true, parent);
			cov = new SelectedAssignmentCommand ("COV", "SymbolTable", processor, true, parent);
		}
		ActionListener min, max, mean, median, stdev, var, cov;
	}

	/**
	 * menu for the STATISTICS  entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void statistics (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Statistics");
		StatisticsActions act = new StatisticsActions (processor, panel);

		add (panel, "Mean", act.getMeanAction (), "Compute the mean of a sample set");
		add (panel, "Min", act.getMinAction (), "Find the minimum value of a sample set");
		add (panel, "Max", act.getMaxAction (), "Find the maximum value of a sample set");
		add (panel, "Median", act.getMedianAction (), "Compute the median of a sample set");
		add (panel, "Stdev", act.getStDevAction (), "Compute the standard deviation of a sample set");
		add (panel, "Cov", act.getCovAction (), "Compute the covariance of a sample set");
		add (panel, "Var", act.getVarAction (), "Compute the variance of a sample set");

		bar.add (panel);
	}


	/**
	 * collection of action listeners for REGRESSION menu
	 */
	public static class RegressionActions implements Functionality.Regression
	{
		public ActionListener getVmAction () { return vm; }
		public ActionListener getFftAction () { return fft; }
		public ActionListener getLinearAction () { return linear; }
		public ActionListener getLagrangeAction () { return lagrange; }
		public ActionListener getNonlinearAction () { return nonlinear; }
		public ActionListener getChebyshevAction () { return chebyshev; }
		public ActionListener getHarmonicAction () { return harmonic; }
		public ActionListener getSeriesAction () { return series; }
		public ActionListener getGaussAction () { return gauss; }

		public ActionListener getPolyAction ()
		{
			Map<String,ActionListener> map =
					new HashMap<String,ActionListener>();
			map.put ("Vandermonde", vm); map.put ("Lagrange", lagrange);
			map.put ("Chebyshev", chebyshev); map.put ("GaussQuad", gauss);
			return new PolynomialRegression (map);
		}

		public RegressionActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			fft = new SelectedCommand ("FFT ", "SymbolTable", processor, true, parent);
			vm = new OrderedDualSelectedAssignmentCommand ("FITPOLY", "SymbolTable", processor, parent);
			linear = new OrderedDualSelectedAssignmentCommand ("FITLINE", "SymbolTable", processor, parent);
			nonlinear = new OrderedDualSelectedAssignmentCommand ("FITEXP", "SymbolTable", processor, parent);
			lagrange = new OrderedDualSelectedAssignmentCommand ("LAGRANGE", "SymbolTable", processor, parent);
			chebyshev = new OrderedDualSelectedAssignmentCommand ("CHEBYSHEV", "SymbolTable", processor, parent);
			gauss = new OrderedDualSelectedAssignmentCommand ("GAUSSQUAD", "SymbolTable", processor, parent);
			harmonic = new HarmonicRegression (processor, parent);
			series = new SeriesRegression (processor, parent);
		}
		ActionListener vm, gauss, harmonic, series, fft, linear, nonlinear, lagrange, chebyshev;
	}

	/**
	 * menu for the REGRESSION  entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void regression (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Regression");
		RegressionActions act = new RegressionActions (processor, panel);

		add (panel, "FFT", act.getFftAction (), "Fast Fourier Transform");
		add (panel, "Linear", act.getLinearAction (), "Linear (least squares) regression");
		add (panel, "Non-Linear", act.getNonlinearAction (), "Non-Linear (logarithmic) regression");
		add (panel, "Polynomial", act.getPolyAction (), "Polynomial interpolation using Linear Algebra solutions for coefficients");
		add (panel, "Harmonic", act.getHarmonicAction (), "Harmonic (Fourier Series) regression");
		add (panel, "Time Series", act.getSeriesAction (), "Cyclic time series analysis");

		bar.add (panel);
	}


	/**
	 * collection of action listeners for CHARTS menu
	 */
	public static class ChartsActions implements Functionality.Charts
	{
		public ActionListener getListAction () { return list; }
		public ActionListener getScatterAction () { return scatter; }
		public ActionListener getAngularAction () { return angular; }
		public ActionListener getRadialAction () { return radial; }

		public ChartsActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			list = new TrackingList (processor, SpaceManager.DataType.Real);
			scatter = new OrderedDualSelectedCommand (OperatorNomenclature.SCATTER_KEYWORD, "SymbolTable", processor, parent);
			angular = new PolarCommand (OperatorNomenclature.POLAR_ANGULAR_KEYWORD, ANGULAR_PROMPTS, list, processor, parent);
			radial = new PolarCommand (OperatorNomenclature.POLAR_RADIAL_KEYWORD, RADIAL_PROMPTS, list, processor, parent);
		}
		ActionListener scatter, angular, radial;
		TrackingList list;
	}
	static final String[]
	ANGULAR_PROMPTS = new String[]{"Polynomial", "Radial Domain", "Angular Domain", "Color Domain"},
	RADIAL_PROMPTS = new String[]{"Polynomial", "Angular Domain", "Radial Domain", "Color Domain"};

	/**
	 * menu for the CHARTS  entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void charts (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu panel = new JMenu ("Charts");
		ChartsActions act = new ChartsActions (processor, panel);

		add (panel, "Scatter", act.getScatterAction (), "Scatter plot of X/Y data"); // not tied to complex engine
		add (panel, "Angular", act.getAngularAction (), "Angular plot of complex plane transform");
		add (panel, "Radial", act.getRadialAction (), "Radial plot of complex plane transform");
		add (panel, "List", act.getListAction (), "Show list of plots being tracked");

		bar.add (panel);
	}


	/**
	 * collection of action listeners for FRACTALS menu
	 */
	public static class FractalsActions implements Functionality.Fractals
	{
		public ActionListener getJuliaAction () { return julia; }
		public ActionListener getMandelbrotAction () { return mandelbrot; }
		public ActionListener getNewtonAction () { return newton; }
		public ActionListener getListAction () { return list; }

		public FractalsActions (DisplayIO.CommandProcessor processor, Component parent)
		{
			list = new TrackingList (processor, SpaceManager.DataType.Real);
			julia = new FractalDisplay ("JULIA ", juliaMap, list, processor, parent);
			mandelbrot = new FractalDisplay ("MANDELBROT ", mandelbrotMap, list, processor, parent);
			newton = new FractalDisplay ("NEWTON ", newtonMap, list, processor, parent);
		}
		ActionListener julia, mandelbrot, newton;
		TrackingList list;
	}
	public static final Map<String,Fractal>
	mandelbrotMap = Mandelbrot.getFractalMap (),
	newtonMap = Newton.getFractalMap (),
	juliaMap = Julia.getFractalMap ();

	/**
	 * menu for the CHARTS entry
	 * @param bar the menu bar being built
	 * @param processor the master command processor object
	 */
	public static void fractals (JMenuBar bar, DisplayIO.CommandProcessor processor)
	{
		JMenu menu = new JMenu ("Fractals");
		FractalsActions act = new FractalsActions (processor, menu);

		add (menu, "Julia", act.getJuliaAction (), "Display the Julia set for a constant");
		add (menu, "Mandelbrot", act.getMandelbrotAction (), "Display the Mandelbrot set from preset viewpoints");
		add (menu, "Newton", act.getNewtonAction (), "Display the available Newton fractal sets");
		add (menu, "List", act.getListAction (), "Show list of plots being tracked");

		bar.add (menu);
	}


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


	/**
	 * control access to Action objects used in main menus
	 */
	public static class ActionManager implements Functionality.ActionManager
	{

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getHomeActions()
		 */
		@Override
		public Functionality.Home getHomeActions() {
			return new HomeActions (processor, appParent);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getSimEqActions()
		 */
		@Override
		public Functionality.SimEq getSimEqActions() {
			return new SimEqActions (processor, appParent);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getMatrixActions()
		 */
		@Override
		public Functionality.Matrix getMatrixActions() {
			return new MatrixActions (processor, appParent);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getStatisticsActions()
		 */
		@Override
		public Functionality.Statistics getStatisticsActions() {
			return new StatisticsActions (processor, appParent);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getPolynomialsActions()
		 */
		@Override
		public Functionality.Polynomials getPolynomialsActions() {
			return new PolynomialActions (processor, appParent);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getRegressionActions()
		 */
		@Override
		public Functionality.Regression getRegressionActions() {
			return new RegressionActions (processor, appParent);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getFractalActions()
		 */
		@Override
		public Functionality.Fractals getFractalActions() {
			return new FractalsActions (processor, appParent);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getPrimesActions()
		 */
		@Override
		public Functionality.Primes getPrimesActions() {
			return new PrimesActions (processor, appParent);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getChartActions()
		 */
		@Override
		public Functionality.Charts getChartActions() {
			return new ChartsActions (processor, appParent);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.gui.Functionality.ActionManager#getDataActions()
		 */
		@Override
		public Functionality.Data getDataActions() {
			return new DataActions (processor, appParent);
		}

		/**
		 * @param appParent the component used by menu action objects
		 */
		public void setAppParent (Component appParent)
		{
			this.appParent = appParent;
		}
		Component appParent;

		/**
		 * @param processor the command processor to be used by menu action objects
		 */
		public ActionManager (DisplayIO.CommandProcessor processor)
		{
			this.processor = processor;
		}
		DisplayIO.CommandProcessor processor;

	}

	/**
	 * allocate a management object for menu action objects
	 * @param processor the command processor to be used by menu action objects
	 * @return an action manager object
	 */
	public static Functionality.ActionManager getActionManager
		(DisplayIO.CommandProcessor processor)
	{
		return new ActionManager (processor);
	}


}
