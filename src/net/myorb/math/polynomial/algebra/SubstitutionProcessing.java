
package net.myorb.math.polynomial.algebra;

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
		Factor equation;
		for (Integer power : analysis.getPowers ())
		{
			Sum term = analysis.getTermFor (power);
			if ( ( equation = doSubstitutionForTerm ( term ) ) != null )
			{ equations.add (equation); }
		}
	}
	protected MatrixSolution.SystemOfEquations equations = new MatrixSolution.SystemOfEquations ();


	// tree traversal for substitution actions


	/**
	 * process constants found as terms in a sum
	 * @param term a term to be updated
	 * @return the updated node
	 */
	public Factor doSubstitutionForTerm (Factor term)
	{
		if (term instanceof Sum)
		{
			Sum result = new Sum (converter);
			Scalar constantTerm = converter.getZero ();

			doSubstitutionsForTerms
			(
				(Sum) term, constantTerm, result
			);

			if ( ! nullTermCheck ( result, constantTerm ) )		// 0 is ignored as factor to avoid superfluous content
			{ insert ( constantTerm, result, 0.0 ); }			// constant should by convention be first term in series
			else { result = null; }								// flag condition which indicates constant zero term
			return result;
		}

		return doSubstitutionForProduct (term);
	}
	void doSubstitutionsForTerms
	(Sum terms, Scalar constantTerm, Sum result)
	{
		Factor reducedProduct;
		for (Factor factor : terms)
		{
			if ( ( reducedProduct = doSubstitutionForProduct (factor) ) != null )
			{
				if ( isConstant ( reducedProduct ) )
				{ Operations.additiveFolding ( constantTerm, reducedProduct ); }
				else { add ( reducedProduct, result ); }
			}
		}
	}
	boolean nullTermCheck (Sum evaluatedSeries, Scalar constantTerm)
	{
		// check for opportunity to completely eliminate degenerate equation
		return evaluatedSeries.isEmpty () && constantTerm.isEqualTo ( 0.0 );
		// constantTerm better be zero if series reduced to empty
		// may want to check for invalid degenerate case here
	}


	/**
	 * process constants found as factors in a product
	 * @param product a product to be updated
	 * @return the updated node
	 */
	public Factor doSubstitutionForProduct (Factor product)
	{
		if (product instanceof Product)
		{
			Scalar scalar = converter.getOne ();
			Product result = new Product (converter);

			doSubstitutionsForProducts
			(
				(Product) product, scalar, result
			);

			if ( scalar.isEqualTo ( 0.0 ) ) return null;		// multiplier is zero so raise NULL condition
			else insert ( scalar, result, 1.0 );				// constant should by convention be first factor
			return reduceSingle (result);						// 1 is ignored as factor to avoid superfluous content
		}

		return doSubstitutionForOperand (product);
	}
	void doSubstitutionsForProducts
	(Product factors, Scalar scalar, Product result)
	{
		Factor reducedOperand;
		for (Factor factor : factors)
		{
			if ( isConstant ( ( reducedOperand = doSubstitutionForOperand (factor) ) ) )
			{ Operations.multiplicativeFolding ( scalar, reducedOperand ); }
			else { add (reducedOperand, result); }
		}
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
			NamedValue < Constant > NVP;
			Variable variable = (Variable) operand;
			return ( NVP = symbolTable.get ( variable.toString () ) ) == null ?
					variable : NVP.getIdentifiedContent ();
		}
		if (operand instanceof Power)
		{
			return new Constant (converter, ( (Power) operand ).evaluate (symbolTable) );
		}
		return operand;
	}


}

