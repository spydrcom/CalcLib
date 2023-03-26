
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.matrices.Matrix;

import java.util.*;

/**
 * command implementation for Series Expansion Solve algorithm
 * @author Michael Druckman
 */
public class Solution <T> extends Utilities
{


	/**
	 * text value name with Constant value
	 */
	public static class NameValuePair
	{
		public NameValuePair (String name, String value)
		{ this.name = name; this.value = new Constant (value); }
		public String toString () { return value.toString (); }
		String name; Constant value;
	}

	/**
	 * map name to pair
	 */
	public static class SymbolValues extends HashMap <String, NameValuePair>
	{
		public void add (String name, String value)
		{
			this.put (name, new NameValuePair (name, value));
		}
		private static final long serialVersionUID = 70879534035012284L;
	}


	public Solution
		(Environment <T> environment) { this.environment = environment; }
	protected Environment <T> environment;


	/**
	 * @param series the expanded series being analyzed
	 * @param profile the profile object of the function which was expanded
	 * @param symbolTable the symbol values from the invoking command
	 */
	public void analyze (SeriesExpansion <T> series, Subroutine <T> profile, SymbolValues symbolTable)
	{
		this.stream = environment.getOutStream ();
		this.series = series; this.profile = profile;
		this.symbolTable = symbolTable;

		this.analysis = series.analysis;

		this.doSubstitution ();
		this.showAnalysis ();

		this.solution = new MatrixSolution <T>
		(environment).solve (equations);
	}
	protected java.io.PrintStream stream;
	protected Manipulations.Powers analysis;
	protected SeriesExpansion <T> series;
	protected SymbolValues symbolTable;
	protected Subroutine <T> profile;
	protected Matrix <T> solution;


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
			Variable v = (Variable) operand;
			NameValuePair nvp = symbolTable.get ( v.toString () );
			if (nvp == null) return v;
			return nvp.value;
		}
		if (operand instanceof Power)
		{
			Power p = (Power) operand;
			Variable v = (Variable) p.base ();
			NameValuePair nvp = symbolTable.get ( v.toString () );
			if (nvp == null) throw new RuntimeException ("Non constant power base");
			return new Constant (Math.pow (nvp.value.getValue (), ((Constant) p.exponent ()).getValue ()));			
		}
		return operand;
	}


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

		stream.println ("===");
		stream.println (symbolTable);
		stream.println ("===");
		stream.println ();
	}


}

