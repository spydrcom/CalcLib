
package net.myorb.math.complexnumbers;

import net.myorb.math.Polynomial;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import net.myorb.math.expressions.charting.*;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.computational.*;

import net.myorb.gui.components.*;

import java.util.ArrayList;
import java.util.List;

/**
 * display a table of all derivatives of a function at all roots of all derivatives
 * @author Michael Druckman
 */
public class FunctionDerivativesTable extends PolynomialComplexFunctionCharacteristics
{

	/**
	 * unit test
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		Polynomial.Coefficients<ComplexValue<Double>> coefficients =
//			polyMgr.newCoefficients (ocl.C(4), ocl.C(-7), ocl.C(-9), ocl.C(-1), ocl.C(1)); // polynomial (4, -7, -9, -1, 1)
//			polyMgr.newCoefficients (ocl.C(4), ocl.C(-27), ocl.C(-9), ocl.C(-4), ocl.C(4), ocl.C(1));
			polyMgr.newCoefficients (ocl.C(4), ocl.C(-27), ocl.C(-9), ocl.C(40), ocl.C(4), ocl.C(1));
		showComplexTable (coefficients);
	}

	/**
	 * use MultiFunctionPlot
	 *  primitives to construct a plot
	 * @param functions the functions to be plotted
	 * @param roots the list of significant x-axis coordinates
	 * @param title a title for the frame
	 */
	public static void plot
		(
			List<PowerFunction<ComplexValue<Double>>> functions,
			List<ComplexValue<Double>> roots,
			String title
		)
	{
		int cNo = 0, cLast = color.length - 1;
		Double lo = roots.get (0).Re (), hi = roots.get (roots.size () - 1).Re ();
		DisplayGraph.RealSeries domain = MultiFunctionPlot.domain (lo, hi, (hi - lo) / 100);
		MultiFunctionPlot.PlotDescriptors descriptors = MultiFunctionPlot.newPlotDescriptor
			(DisplayGraph.DEFAULT_PLOT_SIZE, title, domain);
		for (PowerFunction<ComplexValue<Double>> poly : functions)
		{
			MultiFunctionPlot.addFunctionPlot
			(
				descriptors, color[cNo > cLast? cLast: cNo++],
				ComplexPrimitives.convertToReal (poly)
			);
		}
		MultiFunctionPlot.plot (descriptors);
	}
	static String[] color = new String[]{"WHITE", "GREEN", "RED", "BLUE"};
	
	/**
	 * get the function expression text to use for screen identity
	 * @param polynomial the power function being analyzed and charted
	 * @return the text of the function expression
	 */
	public static String nameFor (Polynomial.PowerFunction<ComplexValue<Double>> polynomial)
	{
		return "F(x) = " + new PolynomialSpaceManager<ComplexValue<Double>> (cfm).toString (polynomial);
	}

	/**
	 * show table for function described with real coefficients
	 * @param coefficients the real Coefficients list
	 */
	public static void showTable (Polynomial.Coefficients<Double> coefficients)
	{
		Polynomial<Double> polyMgr = new Polynomial<Double> (new DoubleFloatingFieldManager ());
		Polynomial.PowerFunction<Double> polynomial = polyMgr.getPolynomialFunction (coefficients);
		showTable (ComplexPrimitives.convertToComplex (polynomial));
	}

	/**
	 * show table for function described with complex coefficients
	 * @param coefficients the complex Coefficients list
	 */
	public static void showComplexTable (Polynomial.Coefficients<ComplexValue<Double>> coefficients)
	{
		showTable (polyMgr.getPolynomialFunction (coefficients));
	}

	/**
	 * show table for function described as Polynomial.PowerFunction
	 * @param polynomial a Polynomial.PowerFunction
	 */
	public static void showTable (Polynomial.PowerFunction<ComplexValue<Double>> polynomial)
	{
		String expr = nameFor (polynomial);
		String title = "Derivatives Table for " + expr;
		List<PowerFunction<ComplexValue<Double>>> functions = new ArrayList<PowerFunction<ComplexValue<Double>>> ();
		List<ComplexValue<Double>> roots = ComplexPrimitives.sort (getAllRoots (polynomial, functions));
		showTable (roots, functions, title, DisplayFrame.DEFAULT_DISPLAY_AREA_SIZE);
		plot (functions, roots, expr);
	}

	/**
	 * show a table of function values
	 * @param items the domain of values to be displayed
	 * @param functions the functions show show across items
	 * @param title the title for the display frame
	 * @param size the size of the frame
	 */
	public static void showTable
		(
			List<ComplexValue<Double>> items,
			List<PowerFunction<ComplexValue<Double>>> functions,
			String title, int size
		)
	{
		DisplayTable.showTable
		(
			getDerivativeTableBody (items, functions),
			getDerivativeTableHeaders (functions.size()),
			title, size
		);
	}

	/**
	 * populate the table for display
	 * @param items the domain of values to be displayed
	 * @param functions the functions show show across items
	 * @return a matrix of values
	 */
	public static Object[][] getDerivativeTableBody
		(
			List<ComplexValue<Double>> items,
			List<PowerFunction<ComplexValue<Double>>> functions
		)
	{
		int n = 0;
		Object[][] data = new Object[items.size()][];

		for (ComplexValue<Double> x : items)
		{
			int m = 1;
			data[n++] = new Object[functions.size()+1];
			data[n-1][0] = ComplexPrimitives.toDisplay (x);
			for (PowerFunction<ComplexValue<Double>> pf : functions)
			{ data[n-1][m++] = ComplexPrimitives.toDisplay (pf.eval (x)); }
		}

		return data;
	}

	/**
	 * construct a list of column headers
	 * @param functionCount the number of functions
	 * @return an array of column headers
	 */
	public static String[] getDerivativeTableHeaders (int functionCount)
	{
		String[] headers = new String[functionCount+1]; headers[0] = "x"; int n = 1;
		for (int i = 0; i < functionCount; i++) headers[n++] = "F" + (i>0? "'"+i: "");
		return headers;
	}

}
