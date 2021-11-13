
package net.myorb.math.computational;

import net.myorb.math.Polynomial;
import net.myorb.math.expressions.charting.DisplayGraph;
import net.myorb.math.expressions.charting.MultiFunctionPlot;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.gui.components.DisplayTable;
import net.myorb.gui.components.DisplayFrame;

import java.util.List;

/**
 * methods of examination of polynomial characteristics
 * @author Michael Druckman
 */
public class PolynomialEvaluation
{


	/**
	 * build a graph of a polynomial function.
	 *  using default display area size defined as constant
	 * @param coefficients the coefficients that define the polynomial
	 * @param domain the list of values on the x-axis
	 */
	public static void chart
	(Polynomial.Coefficients<Double> coefficients, DisplayGraph.RealSeries domain)
	{
		chart (coefficients, domain, DisplayFrame.DEFAULT_DISPLAY_AREA_SIZE);
	}


	static ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();
	static OrdinaryPolynomialCalculus<Double> poly = new OrdinaryPolynomialCalculus<Double>(mgr);
	static PolynomialSpaceManager<Double> psm = new PolynomialSpaceManager<Double> (mgr);


	/**
	 * build a graph of a polynomial function
	 * @param coefficients the coefficients that define the polynomial
	 * @param domain the list of values on the x-axis
	 * @param size the size of the display area
	 */
	public static void chart
	(Polynomial.Coefficients<Double> coefficients, DisplayGraph.RealSeries domain, int size)
	{
		Polynomial.PowerFunction<Double> function = poly.getPolynomialFunction (coefficients);
		Polynomial.PowerFunction<Double> derivative = poly.getFunctionDerivative (function);
		Polynomial.PowerFunction<Double> derivative2 = poly.getFunctionDerivative (derivative);

		MultiFunctionPlot.PlotDescriptors master;
		String title = "  y = " + psm.toString (function);
		master = MultiFunctionPlot.newPlotDescriptor (size, title, domain);
		MultiFunctionPlot.addFunctionPlot (master, "WHITE", function);
		MultiFunctionPlot.addFunctionPlot (master, "GREEN", derivative);
		MultiFunctionPlot.addFunctionPlot (master, "RED", derivative2);
		MultiFunctionPlot.plot (master);
	}


	public static void chart
	(Polynomial.PowerFunction<Double> function, DisplayGraph.RealSeries domain, int size)
	{ chart (function.getCoefficients (), domain, size); }


	public static void chart
	(Polynomial.PowerFunction<Double> function, DisplayGraph.RealSeries domain)
	{ chart (function.getCoefficients (), domain); }


	/**
	 * show a table of the characteristics of a function
	 * @param function the function being described
	 */
	@SuppressWarnings("rawtypes") public static void describe
	(Polynomial.PowerFunction<Double> function)
	{
		List<PolynomialFloatingFunctionCharacteristics.CharacteristicAttributes>
			floatAttributes = PolynomialFloatingFunctionCharacteristics.characterize (function);
		List<PolynomialComplexFunctionCharacteristics.CharacteristicAttributes>
			complexAttributes = PolynomialComplexFunctionCharacteristics.characterize (function);
		Object[][] table = new Object[floatAttributes.size() + complexAttributes.size()][];
		int n = addAll (floatAttributes, table, 0); addAll (complexAttributes, table, n);
		show (table, "Evaluation for F(x) = " + psm.toString (function));
	}


	/**
	 * add attributes to a table
	 * @param a the attributes to add
	 * @param table the table being built
	 * @param n the current row
	 */
	@SuppressWarnings("rawtypes")
	static void add (PolynomialFunctionCharacteristics.CharacteristicAttributes a, Object[][] table, int n)
	{ table[n] = new Object[]{a.getX (), a.getCharacteristicType (), a.getFOfX (), a.getFPrimeOfX (), a.getFPrime2OfX ()}; }


	/**
	 * add all attributes from a list
	 * @param attributes the list of attributes
	 * @param table the table being built
	 * @param n the current row
	 * @return the next row
	 */
	@SuppressWarnings("rawtypes")
	static int addAll (List<PolynomialFunctionCharacteristics.CharacteristicAttributes> attributes, Object[][] table, int n)
	{ for (PolynomialFunctionCharacteristics.CharacteristicAttributes a : attributes) add (a, table, n++); return n; }


	/**
	 * display a table
	 * @param x the contents as a matrix of objects
	 * @param title a title for the frame
	 */
	public static void show (Object[][] x, String title)
	{ DisplayTable.showTable (x, HEADERS, title, DisplayFrame.DEFAULT_DISPLAY_AREA_SIZE); }
	static final String[] HEADERS = new String[]{"X", "Type", "F(x)", "F'(x)", "F''(x)"};


	/**
	 * compute domain for function as outer-most zeroes of function
	 * @param function the power function description of the function
	 * @return the domain list of x-axis values for the function
	 */
	@SuppressWarnings("rawtypes")
	public static DisplayGraph.RealSeries domainFor
	(Polynomial.PowerFunction<Double> function)
	{
		Double lo = -1.0, hi = 1.0;
		List<PolynomialFloatingFunctionCharacteristics.CharacteristicAttributes>
			attributes = PolynomialFloatingFunctionCharacteristics.characterize (function);
		//int n = attributes.size(); if (n < 1) throw new RuntimeException ("No data found during function evaluation");
		//int n = attributes.size(); if (n < 2) throw new RuntimeException ("Minimum of 2 function zeroes required, " + n + " found");

		if (attributes.size() > 0)
		{
			lo = (Double)attributes.get (0).getX ();
			hi = (Double)attributes.get (attributes.size() - 1).getX ();
			if (lo == hi) if (lo < 0) hi = -lo; else lo = -hi;
		}
		return MultiFunctionPlot.domain (lo, hi, (hi - lo) / 1000);
	}


	/**
	 * produce reports and charts for a polynomial
	 * @param coefficients the coefficients array describing the polynomial
	 */
	public static void process (Polynomial.Coefficients<Double> coefficients)
	{
		Polynomial.PowerFunction<Double> pf =
			poly.getPolynomialFunction (coefficients);
		chart (coefficients, domainFor (pf));
		describe (pf);
	}


	/**
	 * unit test
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		Polynomial.Coefficients<Double> c = psm.newCoefficients
		//(-5040.0, 29952.0, -24553.0, 3821.0, 759.0, -197.0, 10.0);
		//(-5040.0, 29952.0, -24553.0, 3821.0, 759.0, -197.0, 10.0);
		(1.0, 1.0, -1.0, 1.0, -2.0, 1.0);
		//(1.0, 1.0, -1.0);
		process (c);

		//chart (c, MultiFunctionPlot.domain (-5.1, 12.1, 0.1));
		//chart (c, MultiFunctionPlot.domain (-3.9, 7.1, 0.1));
		//chart (c, MultiFunctionPlot.domain (-0.5, 4.5, 0.1));

//		Polynomial.PowerFunction<Double> pf =
//			poly.getPolynomialFunction (c);
//		chart (c, domainFor (pf));
//		describe (pf);
	}


}

