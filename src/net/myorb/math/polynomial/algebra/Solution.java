
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.evaluationstates.Environment;

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


	/**
	 * connect expanded series to the generated solutions
	 */
	public static class LinkedSolutions
		extends HashMap <String, Solution <?>>
	{ private static final long serialVersionUID = 6759323443298420151L; }


	public Solution (Environment <T> environment)
	{
		this.stream = environment.getOutStream ();
		this.manager = environment.getSpaceManager ();
		this.valueManager = environment.getValueManager ();
		this.reports = new SolutionReports <T> (environment);
		this.dataConversions = environment.getConversionManager ();
		SeriesExpansion.addInitialConditionsProcessors (manager);
	}
	protected DataConversions <T> dataConversions;
	protected ExpressionSpaceManager <T> manager;
	protected ValueManager <T> valueManager;
	protected SolutionReports <T> reports;
	protected java.io.PrintStream stream;


	// analysis and solution computation


	/**
	 * perform constant substitutions and solve
	 * @param series the expanded series being analyzed
	 * @param symbolTable the symbol values from the invoking command
	 */
	public void analyze
	(SeriesExpansion <T> series, SymbolValues symbolTable)
	{
		this.process
			( series.analysis, symbolTable );
		this.series = series;
		reports.establishTitle ( series, symbolTable );
		this.showAnalysis (); this.establishSolutionAlgorithm ();
		this.solve (equations);
	}
	protected SeriesExpansion <T> series;


	/**
	 * build matrix for linear algebra solution
	 * @param equations description of equations to solve
	 */
	public void solve (SystemOfEquations equations)
	{
		MatrixSolution <T> computer = getSolutionComputer ();
		this.solutionOfEquations = computer.solve (equations, symbolTable);
		reports.collectSolutionTableContent ( computer.getColumnList (), computer.getAugmentedMatrix () );
		this.symbolTable.showSymbols (stream);
	}
	protected Matrix <T> solutionOfEquations;


	/**
	 * display collected content
	 */
	public void showCollectedSolutionTableContent ()
	{
		reports.showCollectedSolutionTableContent ();
	}


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
		formatSectionBreak ("", stream);
		stream.print (series.expandedRoot);

		formatSectionBreak ("", stream);
		for (Double power : analysis.getPowers ())
		{
			refs = references (analysis.getTermFor (power));
			stream.print ( Constant.asInteger (power.toString ()) );
			stream.print ("\t"); stream.print (refs);
			stream.println ();
		}

		formatSectionBreak ("", stream);
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


}

