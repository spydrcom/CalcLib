
package net.myorb.math.polynomial.algebra;

/**
 * support and helper methods for algebra algorithms
 * @author Michael Druckman
 */
public class Utilities extends Elements
{


	// node linkage

	/**
	 * add a factor to a parent
	 * @param factor the factor to be added
	 * @param parent the parent summation or product node
	 */
	public static void add (Factor factor, Factor parent)
	{
		if (parent instanceof Factors && factor != null)
		{
			if ( factorMatchesParent (factor, parent) )
			{ ( (Factors) parent ).addAll ( (Factors) factor ); }
			else ( (Factors) parent ).add (factor);
		}
	}

	/**
	 * compare types of factors
	 * @param factor the factor being checked
	 * @param parent the object to compare with
	 * @return TRUE when types of factors match
	 */
	static boolean factorMatchesParent (Factor factor, Factor parent)
	{
		OpTypes factorType = factor.getType ();
		if (factorType == OpTypes.Operand) return false;
		return factorType == parent.getType ();
	}

	/**
	 * construct a reference to a variable
	 * @param variable the identifier referenced
	 * @param order the polynomial term order
	 * @return the appropriate reference to
	 */
	public static Factor powerFactor (String variable, Double order)
	{
		Variable symbol = new Variable (variable);
		if (order != 1) return Power.reference (symbol, order);
		return symbol;
	}


	// processing for constants

	/**
	 * check for Constant
	 * @param factor source to check
	 * @return TRUE when instance-of Constant
	 */
	public static boolean isConstant (Factor factor)
	{
		return factor instanceof Constant;
	}

	/**
	 * determine if factor is constant
	 * @param factor the factor to be evaluated
	 * @return the factor as Constant or null if fails
	 */
	public static Constant getConstant (Factor factor)
	{
		return isConstant (factor) ? (Constant) factor : null;
	}

	/*
	 * product factors will have a constant at the beginning of the child list
	 * this may not be present if the value is 1 so presence cannot be assumed
	 * when present this will be the scalar multiplier for the product
	 */

	/**
	 * get scalar if present
	 * @param factor a multi-child factor 
	 * @return leading constant from child list if present
	 */
	public static Constant getScalarFrom (Factor factor)
	{
		// this assumes the caller verified the factor as a product
		return getConstant ( Factors.firstOf (factor) );
	}


	// complexity management

	/**
	 * check for child nodes
	 * @param factor source to check
	 * @return TRUE when instance-of Factors
	 */
	public static boolean isMultiFactored (Factor factor)
	{
		return factor instanceof Factors;
	}

	/**
	 * get singleton child node
	 * @param factors source to check
	 * @return child node or NULL when not singleton
	 */
	public static Factor getSingleChild (Factors factors)
	{
		return ! factors.isSingleton () ? null :
			factors.getFirstChild ();
	}

	/**
	 * promote single factors
	 * @param factor a factor that may be a single factor set
	 * @return the single factor where appropriate otherwise parameter factor
	 */
	public static Factor reduceSingle (Factor factor)
	{
		if ( isMultiFactored (factor) )
		{
			Factor child = getSingleChild ( (Factors) factor );
			if ( child != null ) return child;
		}
		return factor;
	}

	/**
	 * check for single grand-child
	 * @param terms the source to check
	 * @return TRUE if contains single grand-child
	 */
	public static boolean simpleReference (Sum terms)
	{
		Factor child = getSingleChild (terms);
		if ( child == null || ! isMultiFactored (child) ) return false;
		return ( (Factors) child ).isSingleton ();
	}

	/**
	 * treat factor as factor list when appropriate
	 * @param factor the source node to check for children
	 * @return NULL if not appropriate otherwise child list
	 */
	public static Factors getChildList (Factor factor)
	{
		if ( ! isMultiFactored (factor) ) return null;
		else return (Factors) factor;
	}


	// process negative terms

	/**
	 * apply NEGATE structure to prepare display format
	 * @param description the description of a portion of an expression
	 * @return the altered structure
	 */
	public static Elements.Factor reducedForm (Elements.Factor description)
	{
		if (description instanceof Elements.Sum)
		{
			return reducedSum ( (Elements.Sum) description );
		}
		if (description instanceof Elements.Product)
		{
			if ( isNegative (description) )
			{
				return negate (description);
			}
		}
		return description;
	}

	/**
	 * insert Negate links in a series of terms
	 * @param sum a series of terms to be reduced
	 * @return the reduced sum
	 */
	public static Elements.Factor reducedSum (Elements.Sum sum)
	{
		Elements.Sum reduced = new Elements.Sum ();
		for (Elements.Factor term : sum)
		{
			if ( isNegative (term) )
			{
				term = negate (term);
			}
			add (term, reduced);
		}
		return reduced;
	}

	/**
	 * insert Negate links in factor connections
	 * @param factor the object to process
	 * @return the Negate link
	 */
	public static Elements.Factor negate (Elements.Factor factor)
	{
		return new Elements.Negated ( negated (factor, -1.0) );
	}

	/**
	 * build a negated form of a factor
	 * @param factor the sub-expression being negated
	 * @param ignoring the value that should be ignored if seen
	 * @return the negated form of the expression
	 */
	public static Elements.Factor negated (Elements.Factor factor, Double ignoring)
	{
		Constant C;
		if ( ( C = getConstant (factor) ) != null ) return C.negated ();
		if ( ( C = getExpectedScalar (factor) ) == null ) return null;
		else return qualified ( C, ignoring, (Factors) factor );
	}

	/**
	 * add a negated term to a series
	 * @param term the description of the term to be added
	 * @param series the series being modified
	 */
	public static void negate (Factor term, Sum series)
	{
		Factor factorToAdd = negated (term, null);
		if (factorToAdd == null) { factorToAdd = Operations.negative (term); }
		add (factorToAdd, series);
	}

	/**
	 * add a negated constant to a series
	 * @param constant the constant to apply to the series
	 * @param series the series being modified
	 */
	public static void negateConstant (Constant constant, Factors series)
	{
		add ( constant.negated (), series );
	}


	// process constants / scalars

	/**
	 * check the sign of a factor
	 * @param factor the object to check
	 * @return TRUE when factor is determined to be negative
	 */
	public static boolean isNegative (Elements.Factor factor)
	{
		Constant C = checkForScalar (factor);
		return C != null && C.isNegative ();
	}

	/**
	 * find scalar in a factor
	 * @param factor possible product holding a scalar
	 * @return the value of the scalar or null if not present
	 */
	public static Elements.Constant checkForScalar (Elements.Factor factor)
	{
		Constant C;
		if ( ( C = getConstant (factor) ) != null ) return C;
		else return getExpectedScalar ( factor );
	}

	/**
	 * get scalar from factor
	 * @param factor possible product holding a scalar
	 * @return the value of the scalar or null if not present
	 */
	public static Elements.Constant getExpectedScalar (Elements.Factor factor)
	{
		if ( ! isMultiFactored (factor) ) return null;
		else return getScalarFrom ( factor );
	}

	/**
	 * build a product starting with a given scalar
	 * @param C the constant to be used as scalar if qualified
	 * @param ignoring the value that should be ignored if seen
	 * @param originalProduct the product object being modified
	 * @return the modified product
	 */
	public static Elements.Factor qualified
		( Constant C, Double ignoring, Factors originalProduct )
	{
		Product product = new Product ();
		if ( C.otherThan ( ignoring ) ) { negateConstant ( C, product ); }
		duplicate ( 1, originalProduct, product );
		return product;
	}


	// list duplication

	/**
	 * factor list duplication
	 * @param starting the starting index for the copy
	 * @param from the source list to copy from
	 * @param to the destination list
	 */
	public static void duplicate (int starting, Factors from, Factors to)
	{
		for (int i = starting; i < from.size (); i++) { to.add (from.get (i)); }
	}


	// list symbolic references

	/**
	 * identify symbolic references from specified factor
	 * @param factor container to analyze for references
	 * @return the set of identifiers
	 */
	public static SymbolicReferences references (Factor factor)
	{
		SymbolicReferences symbols = new SymbolicReferences ();
		factor.identify (symbols);
		return symbols;
	}

	/**
	 * simple polynomial should be terms of coefficient-power pairs
	 * @param equation the Sum making the polynomial description
	 * @return the symbol list holding coefficients
	 */
	public static SymbolList getCoefficientsFrom (Factor equation)
	{
		SymbolList symbols = new SymbolList ();
		for (Factor term : (Sum) equation)
		{
			if (term instanceof Product)
			{ term = ( (Product) term ).getFirstChild (); }
			symbols.add ( references (term).getReferencedSymbol () );
		}
		return symbols;
	}


	// error processing

	/**
	 * produce Exception for error conditions
	 * @param message the text of a message for the condition
	 * @param source an initial Exception thrown causing the error
	 * @throws RuntimeException formatted with message and initial Exception
	 */
	public static void error
	(String message, Exception source) throws RuntimeException
	{ throw new RuntimeException (message, source); }


}

