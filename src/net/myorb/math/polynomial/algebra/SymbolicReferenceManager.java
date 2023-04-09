
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ValueManager.DimensionedValue;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.symbols.AssignedVariableStorage;
import net.myorb.math.expressions.symbols.DefinedFunction;

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

	/**
	 * get profile for function
	 * @param functionName the name of the function
	 * @return the profile object or null for error
	 */
	public FunctionProfile <T> getProfile (String functionName)
	{
		Subroutine <T> S = null;
		try { S = DefinedFunction.asUDF ( symbols.get (functionName) ); }
		catch (Exception e) { Utilities.error ( functionName + " not recognized", e ); }
		return new FunctionProfileSpecifics <T> (S);
	}

	/**
	 * get solutions linked to a differential equation
	 * @param equationName the name of the differential equation
	 * @return the linked solutions
	 */
	public Solution.LinkedSolutions getLinkedSolutions (String equationName)
	{
		return getProfile (equationName).getSeries ().getGeneratedSolutions ();
	}

	/**
	 * post a solution vector to symbol table
	 * @param vector the DimensionedValue holding the solution
	 * @param as the name to give the vector
	 * @return access too the vector
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

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails.FunctionProfile#getParameterName()
	 */
	public String getParameterName ()
	{
		return S.getParameterNames ().get (0);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails.FunctionProfile#getExpressionTree()
	 */
	public JsonValue getExpressionTree () throws Exception
	{
		return S.getExpressionTree ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails.FunctionProfile#setSeries(net.myorb.math.polynomial.algebra.SeriesExpansion)
	 */
	public void setSeries (SeriesExpansion <T> series)
	{
		S.setSeries (series);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.algebra.SymbolicReferenceDetails.FunctionProfile#getSeries()
	 */
	public SeriesExpansion <T> getSeries ()
	{
		return S.getSeries ();
	}

}

