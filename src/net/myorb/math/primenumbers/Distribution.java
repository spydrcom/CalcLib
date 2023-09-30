
package net.myorb.math.primenumbers;

import net.myorb.math.SpaceManager;
import net.myorb.math.ReductionMechanism;
import net.myorb.math.fractions.*;

import java.math.BigInteger;

/**
 * 
 * an extended form of fraction used to represent a normalized form of Factorization
 * 
 * @author Michael Druckman
 *
 */
public class Distribution extends Fraction<Factorization>
{

	/**
	 * fraction values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public Distribution
	(SpaceManager<Factorization> manager)
	{
		super (manager);
	}

	/*
	 * Addition and Subtraction of Fractions
	 * 
	 * To perform Addition and Subtraction of Fractions the denominators of the 2 fractions must be made equivalent so the numerators are 
	 * properly proportioned and the operation result will have the same denominator.  The common denominator is most easily computed as the product 
	 * of the two denominator values of the parameter fractions. The next step is to use the LCM of the denominators to have unnecessary factors
	 * removed from the fractions before the operation is computed.  The series of "distribute" methods modifies the fractions to have a common
	 * denominator equal to the LCM of the denominators.  Additionally the GCF of the numerators is computed and removed from both numerators.
	 * This permits the ultimate operation to be computed with the smallest possible portions of the numerators remaining after distribution.
	 */

	/**
	 * add THIS fraction with specified addend fraction
	 * @param addend the value to be added to THIS
	 * @return the computed sum
	 */
	public Fraction<Factorization> add (Fraction<Factorization> addend)
	{
		Distribution gcfOverLcm = distribute (this, addend);
		Factorization n1 = this.getNumerator (), n2 = addend.getNumerator ();
		Factorization numeratorSum = FactorizationManager.forValue (n1.reduce ().add (n2.reduce ())); // refactor reduced sum
		if (DUMP_DISTRIBUTED_OPERATIONS) System.out.print (gcfOverLcm + " * (" + n1 + " + " + n2 + ") : sum = " + numeratorSum);
		return zeroCheck (numeratorSum, gcfOverLcm); // result may be zero
	}

	/**
	 * subtract specified value from THIS
	 * @param subtrahend representation of value to be subtracted from THIS
	 * @return computed difference
	 */
	public Fraction<Factorization> subtract (Fraction<Factorization> subtrahend)
	{
		Distribution gcfOverLcm = distribute (this, subtrahend);
		Factorization n1 = this.getNumerator (), n2 = subtrahend.getNumerator ();
		Factorization numeratorDif = FactorizationManager.forValue (n1.reduce ().subtract (n2.reduce ())); // refactor reduced difference
		if (DUMP_DISTRIBUTED_OPERATIONS) System.out.print (gcfOverLcm + " * (" + n1 + " - " + n2 + ") : dif = " + numeratorDif);
		return zeroCheck (numeratorDif, gcfOverLcm); // result may be zero
	}

	/**
	 * check for zero value
	 * @param value the value to be checked for zero
	 * @param multiplier the multiplier for non-zero values
	 * @return NULL for ZERO otherwise product with multiplier
	 */
	public Fraction<Factorization> zeroCheck (Factorization value, Distribution multiplier)
	{
		if (manager.isZero (value)) return null;
		else return multiplier.multiplyBy (value);
	}

	/*
	 * when a common factor set G can be found between two values X and Y
	 * 
	 * by definition:
	 * 					X0 = X / G and Y0 = Y / G where X0 and Y0 are remainders after removal of G factor from each
	 * 
	 * which implies:
	 * 					X = X0 * G and Y = Y0 * G
	 * 
	 * so:
	 * 					X * Y = X0 * Y0 * G^2
	 * 
	 * and the least common multiple L for the 2 values is the product removal of one copy of G
	 * 
	 * 					L = X * Y / G
	 * 
	 * and consequently:
	 * 					L = X * Y0 = Y * X0
	 * 
	 * this is useful in fraction addition:
	 * 					Xn/Xd + Yn/Yd
	 * 
	 * grade school math uses LCM:
	 * 					(Xn * Yd0) + (Yn * Xd0)
	 *                  -----------------------
	 *                         LCM (Xd, Yd)
	 * 
	 * an additional step provides additional simplification when using factorizations:
	 * 			(GCF (Xn, Yn) * Xn0 * Yd0) + (GCF (Xn, Yn) * Yn0 * Xd0)
	 * 			-------------------------------------------------------
	 *                                 LCM (Xd, Yd)
	 * 
	 * in simplest form is:
	 * 			(GCF (Xn, Yn) / LCM (Xd, Yd)) * ((Xn0 * Yd0) + (Yn0 * Xd0))
	 * 
	 * also interesting to note that using L = X * Y / G the form can also be expressed as:
	 * 			(GCF (Xn, Yn) * GCF (Xd, Yd)) * ((Xn0 * Yd0) + (Yn0 * Xd0)) / (Xd * Yd)
	 * 
	 * and this is useful because the sum uses only the smallest remaining parts after reduction, namely Xn0, Xd0, Yn0, and Yd0
	 * 
	 */

	/**
	 * normalize 2 fractions canceling common factors.
	 * apply distribution algorithms adjusting numerators relative to lcm of denominators
	 * @param term1 the left side fraction of the summing equation
	 * @param term2 the right side fraction of the equation
	 * @return computed gcf/lcm
	 */
	public Distribution distribute
		(
				Fraction<Factorization> term1, Fraction<Factorization> term2
		)
	{
		normalize (term1); // update numerators and denominators to eliminate negative exponents
		normalize (term2); // factors common to both numerators and denominators are canceled out

		return distribute
		(
				term1.getNumerator(), term1.getDenominator(),	// pass separated numerators and denominators as factorizations
				term2.getNumerator(), term2.getDenominator()	// distributed GCF/LCM factors are pulled out and returned as fraction multiplier
		);														// at return terms are both reduced to common denominator
	}

	/**
	 * compute gcf/lcm for a fraction pair
	 * @param n1 numerator of first fraction
	 * @param d1 denominator of first fraction
	 * @param n2 numerator of second fraction
	 * @param d2 denominator of second fraction
	 * @return gcf of numerators / lcm of denominators
	 */
	public Distribution distribute
		(
			Factorization n1, Factorization d1,
			Factorization n2, Factorization d2
		)
	{
		Factorization lcm = LCM (d1, d2);						// LCM of denominators
		Distribution distribution = new Distribution (manager); // distribution object will hold GCF/LCM fraction
		Factorization gcf = distribute (n1, d1, n2, d2, lcm);	// modify fractions to have common LCM denominator
		distribution.set (gcf, lcm);							// new multiplier is GCF of numerators over LCM of denominators
		return distribution;
	}
	
	/**
	 * adjust 2 fractions to be
	 *  represented with common denominator
	 * @param n1 numerator of first fraction
	 * @param d1 denominator of first fraction
	 * @param n2 numerator of second fraction
	 * @param d2 denominator of second fraction
	 * @param lcm least common multiple of denominators
	 * @return greatest common factors of numerators
	 */
	public Factorization distribute
		(
				Factorization n1, Factorization d1,
				Factorization n2, Factorization d2,
				Factorization lcm
		)
	{
		Factorization d1_0 = lcm.divideBy (d1),    d2_0 = lcm.divideBy (d2);	// compute denominators remainders
		Factorization n1_0 = n1.multiplyBy (d1_0), n2_0 = n2.multiplyBy (d2_0); // cross multiply numerators by denominator remainders
		
		Factorization gcf = GCF (n1_0, n2_0);			// compute numerator GCF allowing numerators to be reduced
		n1.set (n1_0.divideBy (gcf)); d1.set (lcm); 	// remove GCF from numerators leaving remainders to become terms of operation
		n2.set (n2_0.divideBy (gcf)); d2.set (lcm); 	// LCM is now common denominator
	
		return gcf;
	}

	/**
	 * compute least common multiple between two specified Factorizations
	 * @param x first of two factorizations which are to be compared for common factors
	 * @param y second of two factorizations which are to be compared for common factors
	 * @return the computed result (x*y/GCF(x,y))
	 */
	public static Factorization LCM
		(Factorization x, Factorization y)
	{ return x.multiplyBy (y).divideBy (GCF (x, y)); }							// computed using (x * y) / GCF (x, y)  as discussed above

	/**
	 * compute greatest common factor between two specified Factorizations
	 * @param x first of two factorizations which are to be compared for common factors
	 * @param y second of two factorizations which are to be compared for common factors
	 * @return the result Factorization containing just the common factors
	 */
	public static Factorization GCF (Factorization x, Factorization y)
	{
		FactorCollection
			xFactors = x.getFactors (),
			yFactors = y.getFactors ();
		Factorization result = new Factorization ();
		FactorCollection rFactors = result.getFactors ();
		for (BigInteger prime : x.join (y))										// iterate over set of primes taken from join
		{
			int min = Math.min
					(
						xFactors.checkExponent (prime),
						yFactors.checkExponent (prime)
					);															// get exponents associated with prime for each value
			if (min < 0)
			{ throw new RuntimeException ("GCF for unnormalized factor"); }		// parameters must have denominator of 1, no negative prime exponents
			if (min != 0) rFactors.addFactor (prime, min);						// minimum exponent is the common factor
		}
		return result;
	}

	/**
	 * build a fraction representation with copied component values
	 * @param source the fraction with the component values to be copied
	 * @param manager a type manager for the fraction components
	 * @return the normalized fraction copy
	 */
	public static Distribution normalizeCopy
	(Fraction<Factorization> source, SpaceManager<Factorization> manager)
	{
		Distribution r = new Distribution (manager);
		Factorization sourceNumerator = source.getNumerator ();
		Factorization n = new Factorization (); n.getFactors ().addFactors (sourceNumerator.getFactors ());
		Factorization d = new Factorization (); d.getFactors ().addFactors (source.getDenominator ().getFactors ());
		r.set (n, d); normalize (r); n.copySign (sourceNumerator);
		return r;
	}

	/**
	 * set flag allowing trace of Distributed Operations + and -
	 */
	public static void setDistributedOperationsDump () { DUMP_DISTRIBUTED_OPERATIONS = true; }
	public static boolean DUMP_DISTRIBUTED_OPERATIONS = false;

	/**
	 * compare THIS with specified fraction value
	 * @param value the value to be compared to THIS
	 * @return TRUE if THIS is less than specified value
	 */
	public boolean lessThan (Fraction<Factorization> value)
	{
		Factorization
			n1 = this.getNumerator (), d1 = this.getDenominator (),
			n2 = value.getNumerator (), d2 = value.getDenominator ();
		distribute
		(
				n1, d1, n2, d2, LCM (d1, d2)				// perform distribution to scale numerators relative to common denominator
		);
		return n1.reduce ().compareTo (n2.reduce ()) < 0;
	}

	/**
	 * normalize 2 fractions and compare numerators
	 * @param x left side value of comparison operator
	 * @param y right side value of comparison operator
	 * @return TRUE if x parameter is less than y value
	 */
	public static boolean lessThan (Factorization x, Factorization y)
	{
		Distribution
			xd = normalizeCopy (x, x.getFieldManager ()),
			yd = normalizeCopy (y, x.getFieldManager ());	// convert to normalized fractions to allow use of lessThan method
		return xd.lessThan (yd);
	}

	/**
	 * build a copy of a fraction and normalize components
	 * @param source the value to be copied and normalized as a fraction
	 * @param manager the type manager for the component type
	 * @return the new normalized copy
	 */
	public static Distribution normalizeCopy
	(Factorization source, SpaceManager<Factorization> manager)
	{
		Factorization f = new Factorization ();
		Distribution r = new Distribution (manager);
		f.getFactors ().addFactors (source.duplicate ().getFactors ());
		f.copySign (source); r.set (f); normalize (r);
		return r;
	}

	/**
	 * normalize the components of a fraction.
	 *  negative exponent factors are moved to denominator. common factors are canceled
	 * @param source the fraction value to be normalized, numerator and denominator objects are updated
	 */
	public static void normalize (Fraction<Factorization> source)
	{
		Factorization
			n = source.getNumerator (),
			d = source.getDenominator ();							// get separate access to numerator and denominator
		FactorCollection
			nf = n.getFactors (), df = d.getFactors ();				// also access to both factor collections individually
		nf.normalize (); df.normalize ();							// normalize both numerator and denominator components

		for (BigInteger prime : n.join (d))							// iterate over all primes in fraction
		{
			int numExp = nf.checkExponent (prime),					// numerator exponent for prime
				denomExp = df.checkExponent (prime);				// denominator exponent for prime
			int dif = numExp - denomExp;							// resulting exponent for this prime

			if (dif == 0)											// a zero difference means factors cancel
			{
				nf.removeFactor (prime);							// removed canceled components
				df.removeFactor (prime);
			}
			else if (dif < 0)										// negative difference puts factor in denominator
			{
				nf.removeFactor (prime);							// remove from numerator
				df.setFactor (prime, -dif);
			}
			else
			{
				df.removeFactor (prime);							// otherwise remove from denominator
				nf.setFactor (prime, dif);							// factor is part of numerator
			}
		}
	}

	/**
	 * reduce a fraction to a Factorization with negative exponents for denominator primes
	 * @param source the fraction to convert to a flattened Factorization
	 * @return a Factorization object with flattened result
	 */
	public static Factorization flattened (Fraction<Factorization> source)
	{
		if (source == null) return null;

		normalize (source);

		Factorization
			n = source.getNumerator (),									// normalized numerator and denominator of source
			d = source.getDenominator ();
		Factorization result = FactorizationManager.onePrime ();		// start to construct result Factorization
		FactorCollection factors = result.getFactors ();				// get access to the factor collection
		result.copySign (n);											// copy source sign to result

		FactorCollection
			nFactors = n.getFactors (),
			dFactors = d.getFactors ();
		factors.addFactors (nFactors);									// copy numerator factors as-is

		for (BigInteger prime : dFactors.getPrimes ())
		{																// iterate over factors found in denominator
			int dFactorExponent = dFactors.readExponentFor (prime);		// read denominator exponent value from map of prime
			factors.addFactor (prime, - dFactorExponent);				// factors from denominator have negative exponents
		}
		factors.normalize ();
		return result;
	}

	/**
	 * reduce fraction by factoring numerator and denominator and canceling terms
	 * @param fraction the fraction to evaluate for term reduction
	 * @param manager a Factorization field manager
	 * @return a normalized distribution object
	 */
	public static Distribution newReducedRatio
	(Fraction<BigInteger> fraction, SpaceManager<Factorization> manager)
	{
		Distribution ratio = new Distribution (manager);
		BigInteger n = fraction.getNumerator (), d = fraction.getDenominator ();
		ratio.set (FactorizationManager.forValue (n), FactorizationManager.forValue (d));			// factor numerator and denominator
		normalize (ratio);																			// normalize fraction to cancel common factors
		return ratio;
	}

	/**
	 * reduce the parameter fraction by factoring numerator and denominator and canceling terms
	 * @param fraction the fraction containing numerator and denominator to be reduced, object contains reduced components after return
	 * @param manager a Factorization field manager used to construct a Distribution object
	 */
	public static void reduceFraction
	(Fraction<BigInteger> fraction, SpaceManager<Factorization> manager)
	{
		Distribution reducedRatio =
			newReducedRatio (fraction, manager);		// use Distribution object to cancel common factors
		fraction.set
		(
			reducedRatio.getNumerator ().reduce (),		// copy numerator and denominator from Distribution object
			reducedRatio.getDenominator ().reduce ()	// back to Fraction object so Integer ratio is expressed as reduced fraction
		);
	}

	/**
	 * construct an object the implements ReductionMechanism for fractions of BigInteger
	 * @return newly constructed ReductionMechanism
	 */
	public ReductionMechanism<Fraction<BigInteger>> getReductionMechanism ()
	{
		return new ReductionMechanism<Fraction<BigInteger>>()
		{
			public void reduce (Fraction<BigInteger> value)
			{
				reduceFraction (value, manager);
			}
		};
	}

}
