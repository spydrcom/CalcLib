
package net.myorb.math.polynomial.algebra;

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
		this.symbolTable = symbolTable;
		this.analysis = analysis;
		this.doSubstitution ();
	}
	protected Manipulations.Powers analysis;
	protected SymbolValues symbolTable;


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


}

