
package net.myorb.math.polynomial.algebra;

import net.myorb.math.computational.ArithmeticFundamentals;
import net.myorb.math.computational.ArithmeticFundamentals.Scalar;
import net.myorb.math.computational.ArithmeticFundamentals.Conversions;

/**
 * implementation of Substitution Processing layer
 * - Substitution and Solution process separated into layers
 * @author Michael Druckman
 */
public class SubstitutionProcessing extends SolutionData
{


	/**
	 * initialize and execute Solution layer
	 * @param analysis the analysis results of the Manipulations module
	 * @param symbolTable the table of collected symbols
	 */
	public void process
	(Manipulations.Powers analysis, SymbolValues symbolTable)
	{
		this.converter = symbolTable.converter;
		this.symbolTable = symbolTable;
		this.analysis = analysis;
		this.doSubstitution ();
	}
	protected Conversions <?> converter;
	protected Manipulations.Powers analysis;
	protected SymbolValues symbolTable;


	// primary substitution driver


	/**
	 * nodes of element tree are updated for constant symbols
	 */
	public void doSubstitution ()
	{
		for (Integer power : analysis.getPowers ())
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
			Sum result = new Sum (converter);
			Scalar cons = converter.getZero ();
			for (Factor factor : (Sum) term)
			{
				if ( (subs = doSubstitutionForProduct (factor)) instanceof Constant )
				{ ArithmeticFundamentals.plusEquals (cons, valueOf ( subs ) ); }
				else { add (subs, result); }
			}
			if ( cons.isNot (0.0) )
			{ result.add ( new Constant (converter, cons) ); }
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
			Scalar scalar = converter.getOne ();
			Product result = new Product (converter);
			for (Factor factor : (Product) product)
			{
				if ( (subs = doSubstitutionForOperand (factor)) instanceof Constant )
				{ ArithmeticFundamentals.timesEquals ( scalar, valueOf ( subs ) ); }
				else { add (subs, result); }
			}
			if ( scalar.isNot (1.0) )
			{ result.add (0, new Constant (converter, scalar)); }
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
			return new Constant (converter, ( (Power) operand ).evaluate (symbolTable) );
		}
		return operand;
	}


}

