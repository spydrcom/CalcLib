
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.PrettyFormatter;
import net.myorb.math.expressions.commands.Tabulation;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.symbols.DefinedFunction;

import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.ValueManager;

import java.util.ArrayList;
import java.util.List;

/**
 * command implementation for solution display requests
 * @author Michael Druckman
 */
public class SolutionReports <T>
{


	public SolutionReports (Environment <T> environment)
	{
		this.symbols = environment.getSymbolMap ();
		this.manager = environment.getSpaceManager ();
		this.dataConversions = environment.getConversionManager ();
		this.stream = environment.getOutStream ();
		this.environment = environment;
	}
	protected SymbolMap symbols;
	protected Environment <T> environment;
	protected DataConversions <T> dataConversions;
	protected ExpressionSpaceManager <T> manager;
	protected java.io.PrintStream stream;


	/**
	 * get solutions linked to a differential equation
	 * @param equationName the name of the differential equation
	 * @return the linked solutions
	 */
	public Solution.LinkedSolutions getLinkedSolutions (String equationName)
	{
		Subroutine <?> profile;
		try { profile = DefinedFunction.asUDF ( symbols.get (equationName) ); }
		catch (Exception e) { throw new RuntimeException ( equationName + " not recognized", e ); }
		return profile.getSeries ().getGeneratedSolutions ();
	}


	/**
	 * identify linked solutions
	 * @param solutions the solution map from a differential equation
	 * @return the list of solution symbols
	 */
	public static Elements.SymbolList fromSolutionSet
		(Solution.LinkedSolutions solutions)
	{
		Elements.SymbolList list = new Elements.SymbolList ();
		list.addAll (solutions.keySet ()); list.sort (null);
		return list;
	}


	/**
	 * display solution information
	 * @param solutions the list of identifiers
	 */
	public void showSolutions (Elements.SymbolList solutions)
	{
		for (String name : solutions) showSolution (name);
	}


	/**
	 * display a solution polynomial
	 * @param solutionName the name of the solution
	 */
	public void showSolution (String solutionName)
	{
		stream.print (solutionName); stream.print ("\t");
		stream.print (new PrettyFormatter <T> (environment).getFunction (solutionName));
		stream.println ();
	}


	/**
	 * show the work-product of a solution
	 * @param solutionName the name of the solution to be displayed
	 * @param solutions solutions linked to equation
	 */
	public void showSolution (String solutionName, Solution.LinkedSolutions solutions)
	{
		Solution <?> solution;
		if ( ( solution = solutions.get (solutionName) ) == null )
		{ throw new RuntimeException ( "Solution not found: " + solutionName ); }
		showSolution (solution);
	}
	public void showSolution (Solution <?> solution)
	{
		solution.showCollectedSolutionTableContent ();
	}


	// display of work-product matrix of solution


	/**
	 * display collected content
	 */
	public void showCollectedSolutionTableContent ()
	{
		this.compileSolutionTable (coefficients, solutionValues);
	}


	/**
	 * collect data for report
	 * @param coefficients column headers with names of coefficients
	 * @param solutionValues the scalar for the coefficient in each equation
	 */
	public void collectSolutionTableContent
		(
			Elements.SymbolList coefficients, MatrixSolution.WorkProduct <T> solutionValues
		)
	{
		this.solutionValues  = solutionValues;
		this.coefficients = coefficients;
	}
	protected MatrixSolution.WorkProduct <T> solutionValues;
	protected Elements.SymbolList coefficients;


	/**
	 * compile the work-product matrix
	 * @param coefficients column headers with names of coefficients
	 * @param solutionValues the scalar for the coefficient in each equation
	 */
	public void compileSolutionTable
		(
			Elements.SymbolList coefficients, MatrixSolution.WorkProduct <T> solutionValues
		)
	{
		List <String> columnHeaders = new ArrayList <> ();
		columnHeaders.addAll (coefficients); columnHeaders.add ("=");
		showTable (columnHeaders, solutionValues);
	}


	/**
	 * format a title for this solution
	 * @param series the expanded series constructed to solve this series
	 * @param symbolTable the table of symbols provided for the solution request
	 */
	public void establishTitle (SeriesExpansion <T> series, SolutionData.SymbolValues symbolTable)
	{
		this.documentTitle.append (series.getFunctionName ()).append (" - ");
		this.documentTitle.append (series.getSolutionBeingBuilt ()).append (" Solution ");
		this.documentTitle.append (symbolTable);
	}
	protected StringBuffer documentTitle = new StringBuffer ();


	/**
	 * display tabulation of solution data
	 * @param columnHeaders the names of symbols in columns
	 * @param solutionValues the matrix of data points
	 */
	public void showTable
		(
			List <String> columnHeaders,
			List < ValueManager.DimensionedValue <T> > solutionValues
		)
	{
		new Tabulation <> (environment).format (documentTitle.toString (), columnHeaders, solutionValues);
	}


}

