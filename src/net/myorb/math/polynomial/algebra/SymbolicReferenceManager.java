
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.expressions.symbols.AssignedVariableStorage;

import net.myorb.math.expressions.ValueManager.DimensionedValue;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;

/**
 * management of objects treated as differential Equation functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SymbolicReferenceManager <T> implements SymbolicReferenceDetails <T>
{

	public SymbolicReferenceManager (Environment <T> environment)
	{
		this.symbols = environment.getSymbolMap ();
	}
	protected SymbolMap symbols;

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails#getProfile(java.lang.String)
	 */
	public FunctionProfile <T> getProfile (String functionName)
	{
		Subroutine <T> S = null;
		try { S = DefinedFunction.asUDF ( symbols.get (functionName) ); }
		catch (Exception e) { Utilities.error ( functionName + " not recognized", e ); }
		return new FunctionProfileSpecifics <T> (S);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails#getLinkedSolutions(java.lang.String)
	 */
	public Solution.LinkedSolutions getLinkedSolutions (String equationName)
	{
		return getProfile (equationName).getSeries ().getGeneratedSolutions ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails#post(net.myorb.math.expressions.ValueManager.DimensionedValue, java.lang.String)
	 */
	public DimensionedValue <T> post (DimensionedValue <T> vector, String as)
	{
		this.symbols.add ( new AssignedVariableStorage (as, vector) );
		return vector;
	}

}


/**
 * wrapper for Subroutine objects treated as differential Equations
 * @param <T> data type used by symbols
 */
class FunctionProfileSpecifics <T>
	implements SymbolicReferenceDetails.FunctionProfile <T>
{
	
	FunctionProfileSpecifics
		(Subroutine <T> S) { this.S = S; }
	protected Subroutine <T> S;

	/**
	 * @return name associated with the series
	 */
	public String getFunctionName () { return getSeries ().functionName; }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails.FunctionProfile#getParameterName()
	 */
	public String getParameterName () { return S.getParameterNames ().get (0); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails.FunctionProfile#getExpressionTree()
	 */
	public JsonValue getExpressionTree () throws Exception { return S.getExpressionTree (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails.FunctionProfile#setSeries(net.myorb.math.polynomial.algebra.SeriesExpansion, net.myorb.math.polynomial.algebra.Elements.Factor)
	 */
	public void setSeries (SeriesExpansion <T> series, Elements.Factor tree)
	{ S.setSeries (series); series.setAssociatedElementTree (tree); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails.FunctionProfile#getSeriesRoot()
	 */
	public Elements.Factor getSeriesRoot () { return getSeries ().getAssociatedElementTree (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails.FunctionProfile#getSeries()
	 */
	public SeriesExpansion <T> getSeries () { return S.getSeries (); }

}

