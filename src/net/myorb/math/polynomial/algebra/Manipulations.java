
package net.myorb.math.polynomial.algebra;

import java.util.HashMap;
import java.util.Arrays;

/**
 * applications of algebraic rules to equations
 * @author Michael Druckman
 */
public class Manipulations extends Utilities
{


	/**
	 * collect symbols in a product
	 */
	static class Symbols extends HashMap <String, Double>
	{

		/**
		 * represent a variable
		 * @param identifier the text of the variable name
		 * @param exponent the exponent to apply to the variable
		 */
		void include (String identifier, double exponent)
		{
			Double prior;
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
				Constant.getValueFrom ( power.exponent () )
			);
		}

		/**
		 * represent a term as a product of elements
		 * @param scalar the constant portion of the product
		 * @return a factor describing a term
		 */
		Factor getTerm (double scalar)
		{
			if (scalar == 0) return null;

			Product result = new Product ();
			
			if (scalar != 1 || this.keySet ().size () == 0)
			{
				result.add (new Constant (scalar));
			}

			for (String id : this.keySet ())
			{
				result.add (powerFactor (id, this.get (id)));
			}

			return result;
		}

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
	public static Factor reduce (Factors factors)
	{
		double scalar = 1.0;
		Symbols Symbols = new Symbols ();
		for (Factor factor : factors)
		{
			if (factor instanceof Constant)
			{
				scalar *= Constant.getValueFrom (factor);
			}
			else if (factor instanceof Variable)
			{
				Symbols.include ( (Variable) factor );
			}
			else if (factor instanceof Power)
			{
				Symbols.include ( (Power) factor );
			}
		}
		return Symbols.getTerm (scalar);
	}


	/**
	 * reduce the factor list of each term of a series
	 * @param series the series being manipulated
	 * @return the modified series
	 */
	public static Sum reduceTerms (Sum series)
	{
		Factor reduced;
		Sum result = new Sum ();
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
	static class FactorAnalysis
	{


		/**
		 * term described as separate scalar and product
		 */
		class ScaledFactor
		{
			ScaledFactor (Product factors, Double scalar)
			{ this.factors = factors; this.scalar = scalar; }
			Product factors; Double scalar;
		}

		/**
		 * map a factor image to the related scalar
		 */
		class TermFactors extends HashMap <String, ScaledFactor>
		{

			/**
			 * increase existing scalar
			 * - or insert new record when none exists
			 * @param factors the product absent the scalar factor
			 * @param scalar the value of the scalar
			 */
			void addTerm (Product factors, Double scalar)
			{
				ScaledFactor factor;
				String image = factors.toString ();
				if ( ( factor = this.get (image) ) == null )
				{ this.put ( image, new ScaledFactor (factors, scalar) ); }
				else factor.scalar += scalar;
			}

			/**
			 * add a factor to a term product
			 * @param factor the factor to be added
			 */
			void addFactor (Factor factor)
			{
				if ( factor instanceof Product ) addProduct ( (Product) factor );
				else if ( factor instanceof Constant ) addTerm ( new Product (), Constant.getValueFrom (factor) );
				else addTerm ( new Product (factor), 1.0 );
			}

			/**
			 * describe term as separate scalar and product
			 * @param product the product including scalar
			 */
			void addProduct (Product product)
			{
				Double scalar = 1.0;
				Product termFactors = new Product ();
				for ( Factor factor : product )
				{
					if (factor instanceof Constant)
					{ scalar = Constant.getValueFrom (factor); }
					else add ( factor, termFactors );
				}
				addTerm ( termFactors, scalar );
			}

			/**
			 * format optimized series
			 * @return the series in reduced form
			 */
			Factor getReducedSeries ()
			{
				Sum result = new Sum (); Double scalar;
				for (String factorImage : this.keySet () )
				{
					ScaledFactor factor = this.get (factorImage);
					if ( (scalar = factor.scalar) == 0 ) continue;
					add ( termFor ( factor, scalar ), result );
				}
				if ( result.isEmpty () ) return null;
				return reduceSingle (result);
			}

			/**
			 * format multiplier for term
			 * - optimize removal of unit scalar as appropriate
			 * @param factor the factors without the scalar factor
			 * @param scalar the scalar multiple for this product
			 * @return the full product description
			 */
			Product termFor (ScaledFactor factor, Double scalar)
			{
				Product term = new Product (), factors = factor.factors;
				if ( scalar != 1 ) add ( new Constant (scalar), term );
				add ( factors, term );
				return term;
			}

			private static final long serialVersionUID = 6359843120046263025L;
		}


		/**
		 * @param factor the factor to be analyzed
		 */
		FactorAnalysis (Factor factor)
		{ this.factor = factor; }
		Factor factor;


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
	static class Powers extends HashMap <Double, Sum>
	{

		Powers (String variable)
		{ this.variable = variable; }
		String variable;

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
		void includeInPowerSum (Factor term, Double exponent)
		{
			Sum sum;
			if ( ( sum = get (exponent) ) == null )
			{ put ( exponent, sum = new Sum () ); }
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
			if (base instanceof Variable)
			{
				if (matchesVariable (base))
				{
					includeInPowerSum (term, 1.0); return true;
				}
			}
			else if (base instanceof Power)
			{
				Power p = (Power) base;
				if (matchesVariable (p.base ()))
				{
					includeInPowerSum
					( term, Constant.getValueFrom (p.exponent ()) );
					return true;
				}
			}
			return false;
		}

		/**
		 * identify variable power in factors
		 * @param term the term being evaluated
		 * @param product the list of factors in a term
		 */
		void includeInIdentifiedPower (Factor term, Factors product)
		{
			for (Factor factor : product)
			{ if (includeForMatchWith (term, factor)) return; }
			includeInPowerSum (term, 0.0);
		}

		/**
		 * identify order of term and include with appropriate sum
		 * @param term the term being evaluated
		 */
		void include (Factor term)
		{
			if (isMultiFactored (term))
			{ includeInIdentifiedPower (term, (Factors) term); }
			else if (term instanceof Constant) { includeInPowerSum (term, 0.0); }
			else if ( ! includeForMatchWith (term, term) )
			{ includeInPowerSum (term, 0.0); }
		}

		/**
		 * distribute terms with matching powers of variable
		 * @param e the value of the exponent for terms to distribute
		 * @return a factor describing the power term
		 */
		Factor distribute (Double e)
		{
			Sum terms = get (e);
			if ( simpleReference (terms) ) return terms;
			if ( e == 0.0 ) return reducedSumOf ( terms );
			return distributedProduct (terms, e);
		}

		/**
		 * do analysis of a term multiplier
		 * - eliminate terms where scalar has reduced to 0
		 * - reintroduce the power factor as a factor of the term
		 * @param terms the series of factors that comprise the term of the order
		 * @param e the value of the order (exponent) of this polynomial term
		 * @return the full polynomial term of the series
		 */
		Factor distributedProduct (Sum terms, Double e)
		{
			return reducedTerm ( new FactorAnalysis ( multiplier (terms) ).getReducedFactor (), e );
		}
		Factor reducedTerm (Factor termFactors, Double e)
		{
			if ( termFactors == null ) return null;
			Product term = new Product (termFactors);
			add ( powerFactor (variable, e), term );
			return term;
		}

		/**
		 * examine products making up each term
		 * @param terms the terms of the series being examined
		 * @return the series making up the multiplier of a power
		 */
		Factor multiplier (Sum terms)
		{
			Sum factors = new Sum ();
			for ( Factor term  :  terms )
			{ add ( termFor (term), factors ); }
			return reduceSingle (factors);
		}
		Factor termFor (Factor term)
		{
			if ( isMultiFactored (term) )
			{
				Factor product = new Product ();
				for ( Factor factor : (Factors) term )
				{ if ( ! matchesVariable (factor) ) add (factor, product); }
				return reduceSingle (product);
			}
			return Constant.ONE;
		}

		/**
		 * @return the completed series
		 */
		Sum getSeries ()
		{
			Sum result = new Sum ();
			for ( Double e : getPowers () )
			{ add ( distribute (e), result ); }
			return reducedSumOf (result);
		}
		Double [] getPowers ()
		{
			Double [] exponents =
				keySet ().toArray (new Double[]{});
			Arrays.sort (exponents);
			return exponents;
		}

		private static final long serialVersionUID = 2421713129615367536L;
		
	}

	/**
	 * attempt to fold constants
	 * @param terms the terms of a sum
	 * @return the reduced sum
	 */
	static Sum reducedSumOf (Sum terms)
	{
		double constant = 0.0;
		Sum reduced = new Sum ();

		for (Factor term : terms)
		{
			if (term instanceof Product)
			{
				Factor child = getSingleChild ( (Product) term );
				if (child != null) term = child;
			}

			if (term instanceof Constant)
			{
				constant += Constant.getValueFrom (term);
			}
			else
			{
				add (term, reduced);
			}
		}

		if (constant != 0)
		{
			add ( new Constant (constant), reduced );
		}

		return reduced;
	}

	/**
	 * collect terms around powers of a variable
	 * @param series the series being analyzed as a polynomial
	 * @param variable the variable name terms are to be collected across
	 * @return the modified series
	 */
	public static Sum collectTerms (Sum series, String variable)
	{
		Powers powers = new Powers (variable);
		for ( Factor term : series ) powers.include (term);
		return powers.getSeries ();
	}


	/**
	 * collect terms around powers of a variable
	 * - terms of the series will be reduced in complexity
	 * @param series the series being analyzed as a polynomial
	 * @param variable the variable name terms are to be collected across
	 * @return the modified series
	 */
	public static Sum reduceAndCollectTerms (Sum series, String variable)
	{
		return collectTerms ( reduceTerms (series), variable );
	}


}

