
package net.myorb.math.polynomial.algebra;

import net.myorb.math.computational.ArithmeticFundamentals;
import net.myorb.math.computational.ArithmeticFundamentals.Scalar;
import net.myorb.math.computational.ArithmeticFundamentals.Conversions;

/**
 * applications of algebraic rules to equations
 * @author Michael Druckman
 */
public class Manipulations extends Utilities
{


	/**
	 * a map for term order to specified object
	 * @param <TO> the target of the map
	 */
	public static class ExponentMap <TO> extends java.util.HashMap <Integer, TO>
	{ private static final long serialVersionUID = 8879561776409176307L; }


	/**
	 * collect symbols in a product
	 */
	public static class Symbols extends SymbolicMap <Integer>
	{

		Symbols
		(Conversions <?> converter) { this.converter = converter; }
		Conversions <?> converter;

		/**
		 * represent a variable
		 * @param identifier the text of the variable name
		 * @param exponent the exponent to apply to the variable
		 */
		void include (String identifier, int exponent)
		{
			Integer prior;
			if ( ( prior = get (identifier) ) != null )
			{ put ( identifier, prior + exponent ); }
			else put ( identifier, exponent );
		}

		/**
		 * reference as order 1
		 * @param variable the symbol to include
		 */
		void include (Variable variable)
		{
			include (variable.toString (), 1);
		}

		/**
		 * reference as order specified in Power object
		 * @param power description of a symbol with an exponent
		 */
		void include (Power power)
		{
			include
			(
				power.base ().toString (),
				Constant.getValueFrom ( power.exponent () ).intValue ()
			);
		}

		/**
		 * represent a term as a product of elements
		 * @param scalar the constant portion of the product
		 * @return a factor describing a term
		 */
		Factor getTerm (Scalar scalar)
		{
			if ( scalar.isEqualTo (0.0) ) return null;

			Product result = new Product (converter);

			if (scalar.isNot (1.0) || this.keySet ().size () == 0)
			{
				result.add (new Constant (converter, scalar));
			}

			for (String id : this.keySet ())
			{
				result.add (powerFactor (converter, id, exponentFor (id)));
			}

			return result;
		}
		Scalar exponentFor (String id) { return converter.fromInt (this.get (id)); }

		private static final long serialVersionUID = -9070913436157833971L;

	}


	/**
	 * examine child list with Symbols algorithm
	 * @param term a product or other factor making a term
	 * @return a factor describing a term
	 */
	public static Factor reduceChildFactors (Factor term)
	{
		Factors factors = getChildList (term);
		if (factors == null) return term;
		return reduce (factors);
	}


	/**
	 * reduce the factors of a product
	 * @param factors the factors of a product
	 * @return the product with the scalars folded
	 */
	public static Factor reduce (Factors factors)
	{
		trace (factors);
		if (factors instanceof Power) { return factors; }
		Symbols symbols = new Symbols (factors.converter);
		Scalar scalar = factors.converter.getOne ();

		for (Factor factor : factors)
		{
			if (factor instanceof Constant)
			{
				// multiplicative constant folding opportunity
				Operations.multiplicativeFolding ( scalar, factor );
			}
			else if (factor instanceof Variable)
			{
				symbols.include ( (Variable) factor );
			}
			else if (factor instanceof Power)
			{
				symbols.include ( (Power) factor );
			}
		}

		return trace (symbols.getTerm (scalar));
	}


	/**
	 * reduce the factor list of each term of a series
	 * @param series the series being manipulated
	 * @return the modified series
	 */
	public static Sum reduceTerms (Sum series)
	{
		Factor reduced;
		Sum result = new Sum (series.converter);
		for ( Factor term : series )
		{
			reduced = reduceChildFactors (term);
			if ( reduced != null ) add ( reduced, result );
		}
		return result;
	}


	/**
	 * analysis providing ability to combine like terms
	 */
	public static class FactorAnalysis
	{


		/**
		 * term described as separate scalar and product
		 */
		class ScaledFactor
		{
			ScaledFactor (Product factors, Scalar scalar)
			{ this.factors = factors; this.scalar = scalar; }
			Scalar scalar; Product factors;
		}

		/**
		 * map a factor image to the related scalar
		 */
		class TermFactors extends SymbolicMap <ScaledFactor>
		{

			/**
			 * increase existing scalar
			 * - or insert new record when none exists
			 * @param factors the product absent the scalar factor
			 * @param scalar the value of the scalar
			 */
			void addTerm (Product factors, Scalar scalar)
			{
				ScaledFactor factor;
				String image = factors.toString ();
				if ( ( factor = this.get (image) ) == null )
				{ this.put ( image, new ScaledFactor (factors, scalar) ); }
				// additive folding of exponents found to both refer to same factors
				else ArithmeticFundamentals.plusEquals (factor.scalar, scalar);
			}

			/**
			 * add a factor to a term product
			 * @param factor the factor to be added
			 */
			void addFactor (Factor factor)
			{
				if ( factor instanceof Product ) { addProduct ( (Product) factor ); }
				else if ( factor instanceof Constant ) { addTerm ( new Product (converter), valueOf (factor) ); }
				else { addTerm ( new Product (converter, factor), converter.getOne () ); }
			}

			/**
			 * describe term as separate scalar and product
			 * @param product the product including scalar
			 */
			void addProduct (Product product)
			{
				Scalar scalar = converter.getOne ();
				Product termFactors = new Product (converter);
				for ( Factor factor : product )
				{
					if (factor instanceof Constant)
					// multiplicative constant folding opportunity
					{ Operations.multiplicativeFolding ( scalar, factor ); }
					else { add ( factor, termFactors ); }
				}
				addTerm ( termFactors, scalar );
			}

			/**
			 * format optimized series
			 * @return the series in reduced form
			 */
			Factor getReducedSeries ()
			{
				Sum result;
				processAnalyzedProducts (result = new Sum (converter));
				if ( result.isEmpty () ) return null;
				return reduceSingle (result);
			}
			
			/**
			 * build new sum of analyzed terms
			 * @param series the new Sum of captured products
			 */
			void processAnalyzedProducts (Sum series)
			{
				Scalar scalar; ScaledFactor product;
				for (String factorImage : this.keySet () )
				{
					product = this.get (factorImage);
					if ( ! (scalar = product.scalar).isEqualTo (0.0) )
					{ add ( checkMultiplier ( product.factors, scalar ), series ); }
				}
			}

			private static final long serialVersionUID = 6359843120046263025L;
		}


		/**
		 * @param factor the factor to be analyzed
		 */
		FactorAnalysis (Factor factor)
		{ this.factor = factor; this.converter = factor.getConverter (); }
		Conversions <?> converter; Factor factor;


		/**
		 * @return optimal version of the analyzed factor
		 */
		Factor getReducedFactor ()
		{
			if ( factor instanceof Sum )
			{ return getReducedSeries ( (Sum) factor ); }
			return factor;
		}


		/**
		 * map the factors in terms of a series
		 * @param sum the series represented as a sum
		 * @return the optimized version of the series
		 */
		Factor getReducedSeries (Sum sum)
		{
			TermFactors map = new TermFactors ();
			for ( Factor factor : sum ) map.addFactor (factor);
			return map.getReducedSeries ();
		}


	}


	/**
	 * collect terms of common powers of a polynomial
	 */
	public static class Powers extends ExponentMap <Sum>
	{

		Powers (String variable, SeriesExpansion <?> root)
		{ this.variable = variable; this.C = root.converter; }
		Conversions <?> C; String variable;

		/**
		 * @param id symbol to check
		 * @return TRUE when id matched polynomial variable
		 */
		boolean matchesVariable (Factor factor)
		{
			if ( factor instanceof Reference )
			{ return ( (Reference) factor ).refersTo (variable); }
			return false;
		}

		/**
		 * include term in sum for power
		 * @param term the term being evaluated
		 * @param exponent the exponent of the term
		 */
		void includeInPowerSum (Factor term, Integer exponent)
		{
			Sum sum;
			if ( ( sum = get (exponent) ) == null )
			{ put ( exponent, sum = new Sum (C) ); }
			sum.add (term);
		}

		/**
		 * identify power for term
		 * @param term the term being evaluated
		 * @param base the factor to check for the variable
		 * @return TRUE when variable match found
		 */
		boolean includeForMatchWith (Factor term, Factor base)
		{
			Number exponent =
				base instanceof Variable ?
				includeAsVariable ( (Variable) base ) :
				base instanceof Power ? includeAsPower ( (Power) base ) : null;
			if ( exponent != null )
			{
				includeInPowerSum ( term, exponent.intValue () );
				return true;
			}
			return false;
		}

		/**
		 * check variable for match with parameter
		 * @param v the variable to be checked
		 * @return power of 1 for X term
		 */
		Number includeAsVariable ( Variable v ) { return matchesVariable (v) ? 1 : null; }

		/**
		 * @param p a power object
		 * @return the exponent of the power object as a Number
		 */
		Number exponentFor ( Power p ) { return valueOf ( p.exponent () ).toNumber (); }

		/**
		 * check power object for match with parameter
		 * @param p the power object to be evaluated for match with parameter
		 * @return NULL if not match otherwise exponent value
		 */
		Number includeAsPower ( Power p )
		{
			return matchesVariable ( p.base () ) ? exponentFor (p) : null;
		}

		/**
		 * identify variable power in factors
		 * @param term the term being evaluated
		 * @param product the list of factors in a term
		 */
		void includeInIdentifiedPower (Factor term, Factors product)
		{
			for ( Factor factor : product )
			{ if ( includeForMatchWith (term, factor) ) return; }
			includeInPowerSum ( term, 0 );
		}

		/**
		 * identify order of term and include with appropriate sum
		 * @param term the term being evaluated
		 */
		void include (Factor term)
		{
			if ( term instanceof Power )
			{ includePower ( (Power) term ); }
			else if ( isMultiFactored  (term) )
			{ includeInIdentifiedPower ( term, (Factors) term ); }
			else if ( term instanceof Constant ) { includeInPowerSum (term, 0); }
			else if ( ! includeForMatchWith (term, term) )
			{ includeInPowerSum (term, 0); }
		}

		/**
		 * treatment of power factor
		 * - if base is constant then treat as constant term
		 * @param P the power object to process
		 */
		void includePower (Power P)
		{
			Number E = 0; Factor factor;
			if ( matchesVariable (factor = P) )
			{  factor = P.base ();  E = exponentFor (P);  }
			includeInPowerSum ( factor, E.intValue () );
		}

		/**
		 * distribute terms with matching powers of variable
		 * @param e the value of the exponent for terms to distribute
		 * @return a factor describing the power term
		 */
		Factor distribute (Integer e)
		{
			Sum terms = get (e);
			if ( simpleReference (terms) ) return terms;
			return distribute ( e, terms, true );
		}

		/**
		 * do analysis of a term multiplier
		 * - eliminate terms where scalar has reduced to 0
		 * - reintroduce the power factor as a factor of the term
		 * @param E the value of the order (exponent) of this polynomial term
		 * @param terms the series of factors that comprise the term of the order
		 * @param includingPowerFactor TRUE to reintroduce the power factor
		 * @return the description of a term of the series
		 */
		Factor distribute
			(
				Integer E, Sum terms,
				boolean includingPowerFactor
			)
		{
			if ( E == 0 ) return reducedSumOf ( terms );
			Factor reduced = simpleReducedFactor ( terms );
			if ( ! includingPowerFactor ) return reduced;
			return appendPowerOfVariable ( reduced, E );
		}

		/**
		 * do factor analysis of a set of terms
		 * @param terms the terms to analyze
		 * @return reduction of factors
		 */
		Factor simpleReducedFactor (Sum terms)
		{
			return new FactorAnalysis ( multiplier (terms) ).getReducedFactor ();
		}

		/**
		 * build complete description of term
		 * @param termFactors the factors of the term
		 * @param E the value of the order (exponent) of this polynomial term
		 * @return the full term including the power factor
		 */
		Factor appendPowerOfVariable (Factor termFactors, Integer E)
		{
			if ( termFactors == null ) return null;
			Product term = checkMultiplier ( termFactors );
			add ( powerFactor ( C, variable, C.fromInt (E) ), term );
			return term;
		}

		/**
		 * examine products making up each term
		 * @param terms the terms of the series being examined
		 * @return the series making up the multiplier of a power
		 */
		Factor multiplier (Sum terms)
		{
			Sum factors = new Sum (C);
			for ( Factor term  :  terms )
			{ add ( termFor (term), factors ); }
			return reduceSingle (factors);
		}

		/**
		 * process multi-factored term
		 * - single factor indicates just power factor
		 * - 1 is entered into sum to hold place of power
		 * @param term the term being analyzed and manipulated
		 * @return factor resulting for analysis
		 */
		Factor termFor (Factor term)
		{
			if ( isMultiFactored (term) )
			{
				Factor product = new Product (C);
				for ( Factor factor : (Factors) term )
				{ if ( ! matchesVariable (factor) ) add (factor, product); }
				return reduceSingle (product);
			}
			return new Constant (C, C.getOne ());
		}

		/**
		 * reduce the rebuilt series
		 * - of all power terms analyzed
		 * @return the completed series
		 */
		public Sum getSeries ()
		{
			Sum result = new Sum (C);
			for ( Integer e : getPowers () )
			{ add ( distribute (e), result ); }
			return reducedSumOf (result);
		}

		/**
		 * a sum of terms is collected for each power
		 * - this will respond with the equation of terms
		 * @param E the exponent value to evaluate terms for
		 * @return the sum of terms found multiplied by this power
		 */
		public Sum getTermFor (Integer E)
		{
			Sum result = new Sum (C);
			add ( distribute (E, get (E), false), result );
			return reducedSumOf (result);
		}

		/**
		 * @return sorted list of compiled powers
		 */
		public Integer [] getPowers ()
		{
			Integer [] exponents =
				keySet ().toArray (new Integer[]{});
			java.util.Arrays.sort (exponents);
			return exponents;
		}

		private static final long serialVersionUID = 2421713129615367536L;

	}

	/**
	 * attempt to fold constants
	 * @param terms the terms of a sum
	 * @return the reduced sum
	 */
	public static Sum reducedSumOf (Sum terms)
	{
		Conversions <?> C = terms.converter;
		Scalar constant = C.getZero ();
		Sum reduced = new Sum ( C );

		for (Factor term : terms)
		{
			if (term instanceof Product)
			{
				Factor child = getSingleChild ( (Product) term );
				if (child != null) term = child;
			}

			if (term instanceof Constant)
			// additive constant folding opportunity
			{ Operations.additiveFolding ( constant, term ); }
			else { add (term, reduced); }
		}

		if ( constant.isNot (0.0) )
		{
			reduced.add (0, new Constant (C, constant));
		}

		return reduced;
	}


	/**
	 * collect terms around powers of a variable
	 * @param series the series being analyzed as a polynomial
	 * @param variable the variable name terms are to be collected across
	 * @param root the root node for the equation
	 * @return the modified series
	 */
	public static Sum collectTerms
		(Sum series, String variable, SeriesExpansion <?> root)
	{
		Powers powers = new Powers
			(variable, root);
		for ( Factor term : series )
		{  powers.include (term);  }
		root.linkAnalysis ( powers );
		return powers.getSeries ();
	}


	/**
	 * collect terms around powers of a variable
	 * - terms of the series will be reduced in complexity
	 * @param series the series being analyzed as a polynomial
	 * @param variable the variable name terms are to be collected across
	 * @param root the root node for the equation
	 * @return the modified series
	 */
	public static Sum reduceAndCollectTerms
		(Sum series, String variable, SeriesExpansion <?> root)
	{
		if (TRACE)
			System.out.println ("original: " + series);
		Sum reduced = (Sum) trace ( reduceTerms (series) );
		return (Sum) trace ( collectTerms ( reduced, variable, root ) );
	}


	/**
	 * print line debugging on option
	 * @param factor the expanded sequence being analyzed
	 * @return the parameter factor for chaining
	 */
	public static Factor trace (Factor factor)
	{
		if ( TRACE )
		{ System.out.println (factor); }
		return factor;
	}
	static boolean TRACE = false;


}

