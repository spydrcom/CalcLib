
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.commands.Tabulation;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.linalg.GaussSolution;
import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.matrices.Matrix;

import java.util.*;

/**
 * command implementation for Series Expansion Solve algorithm
 * @author Michael Druckman
 */
public class Solution <T> extends SolutionData
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
		this.series = series; this.profile = profile;
		this.symbolTable = symbolTable; this.analysis = series.analysis;
		this.doSubstitution (); this.showAnalysis ();
		this.establishSolutionAlgorithm ();
		this.solve (equations);
	}
	protected Manipulations.Powers analysis;
	protected SeriesExpansion <T> series;
	protected SymbolValues symbolTable;
	protected Subroutine <T> profile;


	/**
	 * build matrix for linear algebra solution
	 * @param equations description of equations to solve
	 */
	public void solve (SystemOfEquations equations)
	{
		MatrixSolution <T> computer = getSolutionComputer ();
		this.solutionOfEquations = computer.solve (equations, symbolTable);
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


	// primary substitution driver


	/**
	 * nodes of element tree are updated for constant symbols
	 */
	public void doSubstitution ()
	{
		for (Double power : analysis.getPowers ())
		{
			equations.add (doSubstitutionForTerm (analysis.getTermFor (power)));
		}
	}
	protected MatrixSolution.SystemOfEquations equations = new MatrixSolution.SystemOfEquations ();


	// tree traversal for substitution actions


	/**
	 * @param term a term to be updated
	 * @return the updated node
	 */
	public Factor doSubstitutionForTerm (Factor term)
	{
		if (term instanceof Sum)
		{
			Factor subs;
			Double cons = 0.0;
			Sum result = new Sum ();
			for (Factor factor : (Sum) term)
			{
				if ( (subs = doSubstitutionForProduct (factor)) instanceof Constant )
				{ cons += ( (Constant) subs ).getValue (); }
				else { add (subs, result); }
			}
			if (cons != 0.0) result.add (new Constant (cons));
			return result;
		}
		return doSubstitutionForProduct (term);
	}

	/**
	 * @param product a product to be updated
	 * @return the updated node
	 */
	public Factor doSubstitutionForProduct (Factor product)
	{
		if (product instanceof Product)
		{
			Factor subs;
			Double scalar = 1.0;
			Product result = new Product ();
			for (Factor factor : (Product) product)
			{
				if ( (subs = doSubstitutionForOperand (factor)) instanceof Constant )
				{ scalar *= ( (Constant) subs ).getValue (); }
				else { add (subs, result); }
			}
			if (scalar != 1.0) result.add (0, new Constant (scalar));
			return reduceSingle (result);
		}
		return doSubstitutionForOperand (product);
	}

	/**
	 * determine value of an operand
	 * @param operand an operand to be evaluated
	 * @return the node with updates where appropriate
	 */
	public Factor doSubstitutionForOperand (Factor operand)
	{
		if (operand instanceof Variable)
		{
			NameValuePair NVP;
			Variable variable = (Variable) operand;
			return ( NVP = symbolTable.get ( variable.toString () ) ) == null ?
					variable : NVP.getConstantValue ();
		}
		if (operand instanceof Power)
		{
			Power p = (Power) operand;
			Variable v = (Variable) p.base ();
			NameValuePair nvp = symbolTable.get ( v.toString () );
			if (nvp == null) throw new RuntimeException ("Non constant power base");
			return new Constant (Math.pow (nvp.getNamedValue (), ((Constant) p.exponent ()).getValue ()));			
		}
		return operand;
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
		for (String C : coefficients)
		{
			T environmentValue = dataConversions.fromDouble
				(symbolTable.get (C).getNamedValue ());
			solutionVector.add (environmentValue);
		}
	}


	/**
	 * convert coefficient vector into internal format
	 * @param coefficients the list of coefficient symbols
	 * @return the internal representation of the solution vector
	 */
	public ValueManager.DimensionedValue <T> getCoefficientsVector (SymbolList coefficients)
	{
		List <T> solutionVector = new ArrayList <> ();
		solutionVectorFor (coefficients, solutionVector);
		return valueManager.newDimensionedValue (solutionVector);
	}


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

