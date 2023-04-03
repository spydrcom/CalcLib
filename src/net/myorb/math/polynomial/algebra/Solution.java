
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.commands.Tabulation;

import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.linalg.GaussSolution;

import net.myorb.math.matrices.Matrix;

import java.util.*;

/**
 * command implementation for Series Expansion Solve algorithm
 * @author Michael Druckman
 */
public class Solution <T> extends SubstitutionProcessing
{


	public Solution (Environment <T> environment)
	{
		this.manager = environment.getSpaceManager ();
		this.valueManager = environment.getValueManager ();
		this.dataConversions = environment.getConversionManager ();
		this.displayTable = new Tabulation <> (environment);
		this.stream = environment.getOutStream ();
	}
	protected DataConversions <T> dataConversions;
	protected ExpressionSpaceManager <T> manager;
	protected ValueManager <T> valueManager;
	protected java.io.PrintStream stream;


	// analysis and solution computation


	/**
	 * perform constant substitutions and solve
	 * @param series the expanded series being analyzed
	 * @param profile the profile object of the function which was expanded
	 * @param symbolTable the symbol values from the invoking command
	 */
	public void analyze
	(SeriesExpansion <T> series, Subroutine <T> profile, SymbolValues symbolTable)
	{
		this.process
			(series.analysis, symbolTable);
		this.establishTitle (series, symbolTable);
		this.series = series; this.profile = profile;
		this.showAnalysis (); this.establishSolutionAlgorithm ();
		this.solve (equations);
	}
	protected SeriesExpansion <T> series;
	protected Subroutine <T> profile;


	/**
	 * build matrix for linear algebra solution
	 * @param equations description of equations to solve
	 */
	public void solve (SystemOfEquations equations)
	{
		MatrixSolution <T> computer = getSolutionComputer ();
		this.solutionOfEquations = computer.solve (equations, symbolTable);
		this.compileSolutionTable (computer.getColumnList (), computer.getAugmentedMatrix ());
		this.symbolTable.showSymbols (stream);
	}
	protected Matrix <T> solutionOfEquations;


	/**
		Gauss Elimination chosen as default
		but all Solution Primitive implementations successfully tested
		Gauss Elimination tests showed significant improvement in time over others
		round off error did also appear to be reduced in Gauss tests
	 */
	public void establishSolutionAlgorithm ()
	{
		this.solutionAlgorithm = new GaussSolution <T> (manager);
	}
	protected SolutionPrimitives <T> solutionAlgorithm;


	/**
	 * configure a solution object
	 * @return configured Solution Computer
	 */
	public MatrixSolution <T> getSolutionComputer ()
	{
		MatrixSolution <T> solutionComputer = new MatrixSolution <> (manager, stream);
		solutionComputer.setPrimitives (solutionAlgorithm);
		return solutionComputer;
	}


	// analysis display formatter


	/**
	 * display analysis to GUI sysout stream
	 */
	public void showAnalysis ()
	{
		Set <String> refs;

		stream.println (); stream.println ("===");

		stream.print (series.expandedRoot);

		stream.println (); stream.println ("===");

		for (Double power : analysis.getPowers ())
		{
			refs = references (analysis.getTermFor (power));
			stream.print (Constant.asInteger (power.toString ()));
			stream.print ("\t"); stream.print (refs);
			stream.println ();
		}

		stream.println (); stream.println ("===");

		for (int n = 0; n < equations.size (); n++)
		{
			stream.print (n);
			stream.print ("\t");
			stream.print (equations.get (n));
			stream.println ();
		}

		symbolTable.showSymbols (stream);
	}


	// solution vector processing


	/**
	 * build ordered vector of solution values
	 * @param coefficients the values of the solution coefficients
	 * @param solutionVector list collecting vector elements
	 */
	public void solutionVectorFor (SymbolList coefficients, List <T> solutionVector)
	{
		for (String coefficientName : coefficients)
		{
			this.addCoefficientTo ( solutionVector, symbolTable.get ( coefficientName ) );
		}
	}
	void addCoefficientTo (List <T> solutionVector, NameValuePair from)
	{
		solutionVector.add ( dataConversions.fromDouble ( from.getNamedValue () ) );
	}


	/**
	 * convert coefficient vector into internal format
	 * @param coefficients the list of coefficient symbols
	 * @return the internal representation of the solution vector
	 */
	public ValueManager.DimensionedValue <T> getCoefficientsVector (SymbolList coefficients)
	{
		List <T> solutionVector = new ArrayList <> ();
		solutionVectorFor ( coefficients, solutionVector );

		return valueManager.newDimensionedValue
		(
			this.eliminateTrailingZeroes ( solutionVector )
		);
	}


	/**
	 * remove zero coefficients for highest powers
	 * @param solutionVector the computed solution polynomial coefficients
	 * @return the vector after trailing zero removal
	 */
	public List <T> eliminateTrailingZeroes (List <T> solutionVector)
	{
		for (int i = solutionVector.size () - 1; i > 0; i--)
		{
			T coefficient = solutionVector.get (i);
			if ( ! manager.isZero ( coefficient ) ) break;
			solutionVector.remove (i);
		}
		return solutionVector;
	}


	// display of work-product matrix of solution


	/**
	 * compile the work-product matrix
	 * @param coefficients column headers with names of coefficients
	 * @param solutionValues the scalar for the coefficient in each equation
	 */
	public void compileSolutionTable
		(
			SymbolList coefficients, MatrixSolution.WorkProduct <T> solutionValues
		)
	{
		List <String> columnHeaders = new ArrayList <> ();
		columnHeaders.addAll (coefficients); columnHeaders.add ("=");
		showTable (documentTitle.toString (), columnHeaders, solutionValues);
	}


	/**
	 * format a title for this solution
	 * @param series the expanded series constructed to solve this series
	 * @param symbolTable the table of symbols provided for the solution request
	 */
	public void establishTitle (SeriesExpansion <T> series, SymbolValues symbolTable)
	{
		this.documentTitle.append (series.getFunctionName ()).append (" - ");
		this.documentTitle.append (series.getSolutionBeingBuilt ()).append (" Solution ");
		this.documentTitle.append (symbolTable);
	}
	protected StringBuffer documentTitle = new StringBuffer ();


	/**
	 * display tabulation of solution data
	 * @param documentTitle the title for the display
	 * @param columnHeaders the names of symbols in columns
	 * @param solutionValues the matrix of data points
	 */
	public void showTable
		(
			String documentTitle, List <String> columnHeaders,
			List < ValueManager.DimensionedValue <T> > solutionValues
		)
	{
		displayTable.format (documentTitle, columnHeaders, solutionValues);
	}
	protected Tabulation <T> displayTable;


}

