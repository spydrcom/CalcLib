
package net.myorb.math.fractions;

import net.myorb.math.SpaceManager;
import net.myorb.math.SignManager;

/**
 * 
 * manage fractions of components specified as generic type
 * 
 * @param <T> type of component values on which operations are to be executed
 * 
 * @author Michael Druckman
 *
 */
public class Fraction<T> extends SignManager
{

	/*
	 * fractions are composed of numerator and denominator values 
	 */
	protected T numerator;
	protected T denominator;

	/**
	 * fraction values are constructed based on type manager for components
	 * @param manager the manager for the component type
	 */
	public Fraction
	(SpaceManager<T> manager)
	{ this.manager = manager; }
	protected SpaceManager<T> manager;

	/**
	 * build a fraction object from generic numerator and denominator values
	 * @param numerator the value to use as numerator
	 * @param denominator the denominator value
	 * @param manager a manager for components
	 */
	public Fraction
		(			
			T numerator,
			T denominator,
			SpaceManager<T> manager
		)
	{
		this (manager);
		set (numerator, denominator);
	}

	/**
	 * build a fraction object from generic numerator value, denominator assumed to be 1
	 * @param numerator the value to use as numerator
	 * @param manager a manager for components
	 */
	public Fraction
		(			
			T numerator,
			SpaceManager<T> manager
		)
	{
		this (manager);
		set (numerator);
	}

	/**
	 * build a fraction object from an integer numerator value, denominator assumed to be 1
	 * @param scalar the integer to be used as the numerator
	 * @param manager a manager for components
	 */
	public Fraction
		(			
			int scalar,
			SpaceManager<T> manager
		)
	{
		this (manager);
		set (manager.newScalar (scalar));
	}

	/**
	 * change the component values in this fraction
	 * @param numerator the value to use as numerator
	 * @param denominator the denominator value
	 */
	public void set (T numerator, T denominator)
	{
		this.numerator = numerator;
		this.denominator = denominator;
	}

	/**
	 * change the numerator value in this fraction, denominator set to 1
	 * @param value the value to use as numerator
	 */
	public void set (T value)
	{
		set (value, manager.newScalar (1));
	}

	/**
	 * get the numerator value
	 * @return the numerator value
	 */
	public T getNumerator () { return numerator; }

	/**
	 * get the denominator value
	 * @return the denominator value
	 */
	public T getDenominator () { return denominator; }

	/**
	 * force internal representation of sign to match parameter
	 * @param negative TRUE to force value negative, FALSE for positive
	 */
	public void setSign (boolean negative)
	{
		boolean flip = negative;
		if (manager.isNegative (numerator)) flip = !negative;
		if (flip) numerator = manager.negate (numerator);
	}

	/**
	 * compute sum THIS with specified term
	 * @param term the addend to be summed in this computation
	 * @return the resulting sum fraction
	 */
	public Fraction<T> addWith (Fraction<T> term)
	{
		T numeratorx = manager.multiply (this.numerator, term.denominator);
		T numeratory = manager.multiply (term.numerator, this.denominator);
		T denominator = manager.multiply (this.denominator, term.denominator);
		return new Fraction<T> (manager.add (numeratorx, numeratory), denominator, manager);
	}

	/**
	 * compute fraction inversion (1/this)
	 * @return inverted value (denominator/numerator)
	 */
	public Fraction<T> inverted ()
	{
		return new Fraction<T>
		(
			this.denominator, this.numerator, manager
		);
	}

	/**
	 * multiply this fraction by a scalar factor
	 * @param factor the generic factor value to use in computation
	 * @return the computation result
	 */
	public Fraction<T> multiplyBy (T factor)
	{
		return new Fraction<T>
		(
			manager.multiply (this.numerator, factor),
			this.denominator, manager
		);
	}

	/**
	 * multiply this fraction by a parameter fraction factor
	 * @param factor the fraction to use as multiplier
	 * @return the computation result
	 */
	public Fraction<T> multiplyBy (Fraction<T> factor)
	{
		return new Fraction<T>
		(
			manager.multiply (this.numerator, factor.numerator),
			manager.multiply (this.denominator, factor.denominator),
			manager
		);
	}

	/**
	 * divide this fraction by a parameter fraction divisor
	 * @param divisor the fraction to use as divisor
	 * @return the computation result
	 */
	public Fraction<T> divideBy (Fraction<T> divisor)
	{
		return this.multiplyBy (divisor.inverted ());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "( " + numerator + " ) / ( " + denominator + " )";
	}

}
