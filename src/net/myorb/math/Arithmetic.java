
package net.myorb.math;

import net.myorb.data.abstractions.Function;

/**
 * Implementation of arithmetic operations based on type primitives
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Arithmetic<T> extends ComputationConfiguration<T>
	implements SignManagementOperations
{


	/**
	 * construct arithmetic object based on type manager
	 * @param manager the manager for the type being manipulated
	 */
	public Arithmetic
		(SpaceManager<T> manager)
	{
		this.manager = manager;
		allocateIterationMap ();
	}
	protected SpaceManager<T> manager;

	public SpaceManager<T> getSpaceManager () { return manager; }
	public SpaceManager<T> getSpaceDescription () { return manager; }

	/**
	 * extended set of operations available based on type manager
	 * @param <T> type on which operations are to be executed
	 */
	public interface Value<T> extends SignManagementOperations
	{
		/**
		 * get the value in a representation used internally to the class.
		 *  the internal format keeps the value as positive so sign is separately managed
		 * @return the internal representation of the value
		 */
		T getInternal ();
		
		/**
		 * get the value in a representation used generally.
		 *  the underlying format allows for zero and negative values as this is the export from the Value wrapper
		 * @return the generalized representation of the value
		 */
		T getUnderlying ();

		/**
		 * change the internal value to specified value
		 * @param newValue the new value to assign to the object
		 */
		void update (Value<T> newValue);

		/**
		 * is the value zero.
		 *  this should be reduced to a boolean flag as should the "negative" aspect
		 * @return TRUE for zero value
		 */
		boolean isZero ();

		/**
		 * determine value of opposite sign.
		 *  this is the Value representation so this should be implemented as flag inversion
		 * @return negated value
		 */
		Value<T> negate ();
		
		/**
		 * compute difference of parameter subtracted from THIS
		 * @param subtrahend the value to be subtracted from THIS
		 * @return computed difference
		 */
		Value<T> minus (Value<T> subtrahend);
		
		/**
		 * compute sum of parameter added to THIS
		 * @param term the value to be added with THIS
		 * @return computed sum
		 */
		Value<T> plus (Value<T> term);
		
		/**
		 * compute sum of parameter terms added to THIS
		 * @param terms the values to be added with THIS
		 * @return computed sum
		 */
		@SuppressWarnings("unchecked")
		Value<T> accumulate (Value<T>... terms);
		
		/**
		 * compute product of parameter factor with THIS
		 * @param factor the value of factor to be multiplied with THIS
		 * @return computed product
		 */
		Value<T> times (Value<T> factor);
		
		/**
		 * compute product of parameter factors with THIS
		 * @param factors the values of factors to be multiplied with THIS
		 * @return computed product
		 */
		@SuppressWarnings("unchecked")
		Value<T> timesProductOf (Value<T>... factors);
		
		/**
		 * return computation of THIS value divided by parameter value
		 * @param x divisor to use in computation
		 * @return result computed of THIS/x
		 */
		Value<T> over (Value<T> x);
		
		/**
		 * return computation of value 1 divided by THIS value
		 * @return result computed of 1/THIS
		 */
		Value<T> inverted ();

		/**
		 * compute sqrt (THIS)
		 * @return computed square root
		 */
		Value<T> sqrt ();

		/**
		 * compute x^2
		 * @return computed result
		 */
		Value<T> squared ();

		/**
		 * raise THIS to power of exponent
		 * @param exponent the exponent for this computation
		 * @return computed result THIS ^ exponent
		 */
		Value<T> pow (int exponent);

		/**
		 * is THIS less than specified value
		 * @param value the value to be compared
		 * @return TRUE = THIS is less than value
		 */
		boolean isLessThan (Value<T> value);
	}

	/**
	 * construct representation for zero
	 * @return a value wrapper == 0
	 */
	public Value<T> zeroValue ()
	{
		ValueWrapper<T> w =
			new ValueWrapper<T> (null, false, true, manager);							// value object wrapped is null, flag indicates zero
		w.setLibrary (this.lib);
		return w;
	}
	public boolean isZro (T value) { return manager.isZero (value); }

	/**
	 * construct a value wrapper for an internal value
	 * @param internalValue a value in internal representation limited to GT 0
	 * @return new wrapped value object
	 */
	public Value<T> positiveValue (T internalValue)
	{
		ValueWrapper<T> w =
			new ValueWrapper<T> (internalValue, false, false, manager);					// non-zero and non-negative, represents all positive T
		w.setLibrary (this.lib);
		return w;
	}

	/**
	 * compute internal representation for scalar
	 * @param value simple integer value to convert
	 * @param negative TRUE for negative value, FALSE forpositive
	 * @return internal representation for value
	 */
	public Value<T> forValue (T value, boolean negative)
	{
		if (isZro (value)) return zeroValue ();
		T positive = negative? neg (value): value;										// if negative use negate to have positive T value
		Value<T> v = positiveValue (positive);											// wrapper takes positive value T
		v.setSign (negative);															// flag set as appropriate
		return v;
	}

	/**
	 * compute internal representation for scalar
	 * @param value simple integer value to convert
	 * @return internal representation for value
	 */
	public Value<T> forValue (int value)
	{
		return forValue (discrete (value), value < 0);									// use scalar convert and use int < for flag determination
	}
	public T discrete (int value) { return manager.newScalar (value); }

	/**
	 * convenience method for value=1
	 * @return wrapped value one (1)
	 */
	public Value<T> oneValue () { return forValue (1); }

	/**
	 * compute internal representation for generic value
	 * @param value simple generic value to convert
	 * @return internal representation for value
	 */
	public Value<T> forValue (T value)
	{
		return forValue (value, isNeg (value));											// use manager to determine if T value is negative
	}
	public boolean isNeg (T value) { return manager.isNegative (value); }

	/**
	 * determine value of opposite sign
	 * @param value starting value to be negated
	 * @return negated value
	 */
	public Value<T> negate (Value<T> value)
	{
		if (value.isZero ()) return zeroValue ();										// return new copy of zero object
		Value<T> newValue = positiveValue (value.getInternal ());						// internal is necessarily positive
		newValue.copyInvertedSign (value);												// mark with inverted sign flag 
		return newValue;
	}
	public Value<T> negative (Value<T> value) { return negate (value); }

	/**
	 * determine absolute value of parameter
	 * @param value starting value to be evaluated
	 * @return absolute value
	 */
	public Value<T> abs (Value<T> value)
	{
		if (value.isNegative ()) return value.negate ();								// use negate if flag shows negative value
		else return value;
	}
	public T abs (T value) { return isNeg (value)? neg (value): value; }

	/**
	 * comute sign function of value
	 * @param value the basis of the computation
	 * @return -1, 0, 1 depending on sign of value
	 */
	public T sgn (T value)
	{
		if (isZro (value)) return discrete (0);
		return discrete (isNeg (value)? -1: 1);
	}
	public Value<T> sgn (Value<T> value) { return value.isNegative ()? oneValue ().negate (): oneValue (); }

	/**
	 * hash left and right side value objects into bit mask of zero flags
	 * @param x left side value object
	 * @param y right side object
	 * @return zero flag mask
	 */
	public int zeroHash (Value<T> x, Value<T> y)
	{
		return bitHash (x.isZero (), y.isZero ());										// hash two zero flags together
	}

	/*
	 * Notes on implementation of addition and subtraction
	 * 
	 * The internal representation assumes values are positive so the sign management can be controlled.
	 * the "sum" and "difference" methods will show use of identity equations that reduce the actual operation
	 * to an execution on two positive values, either x + y or x + -y for sum and difference respectively where
	 * x and y have been manipulated to necessarily be positive values.  this is done so that component data types
	 * need not have sign management of their own, for example fractions and prime factors could become confused
	 * if the component values were allowed to have independent sign management.  by having the Arithmetic package
	 * control sign management and assume component data is necessarily positive the issues do not produce problems
	 * 
	 */

	/**
	 * compute sum of pair of terms
	 * @param left the value of the left side of computation
	 * @param right the value of the right side of computation
	 * @return result of computation
	 */
	public Value<T> sum (Value<T> left, Value<T> right)
	{
		switch (zeroHash (left, right))						// hash left.isZero | right.isZero
		{
			case NEITHER_BIT: break;
			case LEFT_BIT_ONLY: return right;											// left=0 so left + right = right		} zero
			case RIGHT_BIT_ONLY: case BOTH_BITS: return left;							// right=0 so left + right = left		} identity
			default: internalError ();
		}
		switch (signHash (left, right))						// hash left.isNegative | right.isNegative
		{
			case NEITHER_BIT: break;
			case LEFT_BIT_ONLY: return difference (right, negate (left));				// -left + right = right - left			} negative value
			case BOTH_BITS: return negate (sum (negate (left), negate (right)));		// -left + -right = -( left + right )	} changes operation to
			case RIGHT_BIT_ONLY: return difference (left, negate (right));				// left + -right = left - right			} subtraction
			default: internalError ();
		}
		return computeSum (left, right);					// simple addition with both values positive
	}

	/**
	 * use type manager to compute the sum and format a result object
	 * @param left the value of the left side of computation, non-zero AND non-neg
	 * @param right the value of the right side of computation
	 * @return result of computation
	 */
	@SuppressWarnings("unchecked")
	public Value<T> computeSum (Value<T> left, Value<T> right)
	{
		T l = left.getInternal (), r = right.getInternal ();							// convert to internal to use manager.add
		return positiveValue (sumOf (l, r));											// compute left + right, necessarily > 0
	}
	@SuppressWarnings("unchecked")
	public T sumOf (T... terms)
	{
		T sum = discrete (0);
		for (T term : terms) sum = manager.add (sum, term);
		return sum;
	}

	/**
	 * compute sum of series of terms
	 * @param terms the values to be added
	 * @return result of computation
	 */
	@SuppressWarnings("unchecked")
	public Value<T> summation (Value<T>... terms)
	{
		Value<T> accumulation = zeroValue ();
		for (Value<T> term : terms) accumulation = sum (accumulation, term);
		return accumulation;
	}
	@SuppressWarnings("unchecked")
	public T subtract (T left, T right) { return sumOf (left, neg (right)); }

	/**
	 * compute difference left-right
	 * @param left the value of the left side of computation
	 * @param right the value of the right side of computation
	 * @return result of computation
	 */
	public Value<T> difference (Value<T> left, Value<T> right)
	{
		switch (zeroHash (left, right))							// hash left.isZero | right.isZero
		{
			case NEITHER_BIT: break;
			case LEFT_BIT_ONLY: return negate (right);									// left=0 so 0-right = -right			} zero
			case RIGHT_BIT_ONLY: case BOTH_BITS: return left;							// right=0 so left-0 = left				} identity
			default: internalError ();
		}
		switch (signHash (left, right))							// hash left.isNegative | right.isNegative
		{
			case NEITHER_BIT: break;													// both non-zero and positive			} difference
			case LEFT_BIT_ONLY: return negate (sum (negate (left), right));				// -left - right = - (left + right)		} with negative changes
			case RIGHT_BIT_ONLY: case BOTH_BITS: return sum (left, negate (right));		// left - -right = left + right			} operation to sum
			default: internalError ();
		}
		return computeDifference (left, right);					// determine method of computation
	}

	/**
	 * compute difference left-right for non-zero AND non-neg
	 * @param left the value of the left side of computation
	 * @param right the value of the right side of computation
	 * @return result of computation
	 */
	public Value<T> computeDifference (Value<T> left, Value<T> right)
	{
		T l = left.getInternal (), r = right.getInternal ();							// convert to internal and compare,
		if (!isLessThan (l, r)) return computePositiveDifference (l, r);				// if left >= right then use simple subtraction
		else return negate (computePositiveDifference (r, l));							// otherwise compute - (right - left) allowing positive result
	}

	/**
	 * use type manager to compute a difference and format a result object
	 * @param left the internal value of the left side of computation, necessarily left GE right
	 * @param right the internal value of the right side of computation
	 * @return result of computation in Value wrapper
	 */
	@SuppressWarnings("unchecked")
	public Value<T> computePositiveDifference (T left, T right)
	{
		return forValue (sumOf (left, neg (right)), false);								// compute left + (-right), necessarily >= 0
	}
	public T neg (T value) { return manager.negate (value); }

	/**
	 * compute product left*right
	 * @param left the value of the left side of computation
	 * @param right the value of the right side of computation
	 * @return result of computation
	 */
	public Value<T> product (Value<T> left, Value<T> right)
	{
		if (left.isZero () || right.isZero ()) return zeroValue ();						// 0 * x = 0				} zero identity for multiply
		T l = left.getInternal (), r = right.getInternal ();							//							} when factors both positive
		Value<T> value = positiveValue (X (l, r));										// x * y is positive		} negative result is
		value.setSignXor (left, right);													// -x * y | x * -y			} logical xor
		return value;
	}
	public T X (T left, T right) { return manager.multiply (left, right); }
	public T squared (T x) { return X (x, x); }

	/**
	 * compute product of series of factors
	 * @param factors the values to be multiplied
	 * @return result of computation
	 */
	@SuppressWarnings("unchecked")
	public Value<T> productSeries (Value<T>... factors)
	{
		Value<T> accumulation = oneValue ();
		for (Value<T> term : factors) accumulation = product (accumulation, term);
		return accumulation;
	}

	/**
	 * compute division left/right
	 * @param left the value of the left side of computation
	 * @param right the value of the right side of computation
	 * @return result of computation
	 */
	public Value<T> quotient (Value<T> left, Value<T> right)
	{
		if (left.isZero ()) return zeroValue ();
		if (right.isZero ()) throw new RuntimeException ("Division by zero");
		T l = left.getInternal (), r = right.getInternal ();
		Value<T> value = positiveValue (divide (l, r));
		value.setSignXor (left, right);
		return value;
	}
	public T divide (T left, T right) { return X (left, inverted (right)); }			//  x / y = x * ( 1 / y )
	public T inverted (T value) { return manager.invert (value); }

	/**
	 * @param f function to call
	 * @param x parameter to function call
	 * @return value of function result
	 */
	public Value<T> call (Function<T> f, Value<T> x)
	{
		return forValue (f.eval (x.getUnderlying ()));
	}

	/**
	 * include new factors in product series of a factorial
	 * @param starting the highest factor already included into running product
	 * @param count the number of factors to be included in the running series
	 * @param factorial the object collecting the product of the factors
	 * @return the new highest factor now included
	 */
	public int factorialExtended (int starting, int count, Value<T> factorial)
	{
		int result = starting + count, factors = 1;
		for (int i=0; i<count; i++) { factors *= (result - i); }						// product of additional factors
		factorial.update (factorial.times (forValue (factors)));						// update within existing factorial object
		return result;
	}

	/**
	 * compute value raised to power
	 * @param base the base value for the computation
	 * @param exponent the exponent value for the computation
	 * @return result of computation
	 */
	public Value<T> raise (Value<T> base, int exponent)
	{
		switch (exponent)
		{
		case 1: return base;

		case 0: return oneValue ();

		default:
			if (exponent < 0)
			{
				return raise (base, -exponent).inverted ();
			}
			else
			{
				Value<T> result = base;
				for (int i=1; i<exponent; i++) result = product (result, base);					// iterative multiple of base
				return result;
			}
		}
	}

	/**
	 * format output for a series term dump
	 * @param termVal the value of the term being accumulated
	 * @param accumulation the sum upon adding this term
	 */
	public void seriesTermDump (Value<T> termVal, Value<T>accumulation)
	{
		System.out.println ("< term: " + termVal + " => sum: " + accumulation + " >");
		System.out.println ("< Internal SUM = " + accumulation.getInternal () + " >");
	}

	/**
	 * on option terms are reduced by reduction mechanism and accumulation is dumped
	 * @param termValue value of last term having been accumulated
	 * @param accumulation the sum of all prior terms
	 * @param reduce flag for execution of reduction
	 * @param dump flag for execution of dump
	 */
	public void reduceAndDump
	(Value<T> termValue, Value<T> accumulation, boolean reduce, boolean dump)
	{
		if (reduce) reductionMechanism.reduce (accumulation.getInternal ());
		if (dump) seriesTermDump (termValue, accumulation);		
	}


	// convenience wrapper for power library functions

	public T sroot (T x) { return lib.sqrt (x); }
	public T powerOf (T x, int to) { return lib.pow (x, to); }
	public Value<T> sqrt (Value<T> x) { return forValue (sroot (x.getUnderlying ())); }
	public Value<T> pow (Value<T> x, int exponent) { return forValue (lib.pow (x.getUnderlying (), exponent)); }
	public void setLibrary (PowerLibrary<T> lib) { this.lib = lib; }
	protected PowerLibrary<T> lib;


	// convenient access to comparison operator

	public boolean lessThan (Value<T> left, Value<T> right)
	{
		if (right == null) return left.isNegative ();
		if (left == null) return !right.isNegative ();
		return manager.lessThan (left.getUnderlying (), right.getUnderlying ());
	}
	public boolean isLessThan (T left, T right) { return manager.lessThan (left, right); }

	/**
	 * convenient access to value of PI
	 * @return PI from space manager
	 */
	public T PI () { return manager.getPi (); }
	public T piTimes (int multiplier) { return X (PI (), discrete (multiplier)); }
	public T piTimes (int multiplier, int divisor) { return X (piTimes (multiplier), inverted (discrete (divisor))); }
	public T piOver (int divisor) { return X (PI (), inverted (discrete (divisor))); }

	/**
	 * reduce angle to 0 LE X LE 2*PI
	 * @param angle starting value of angle GT 2*PI
	 * @return reduced angle LT 2*PI
	 */
	@SuppressWarnings("unchecked")
	public T reduceAngle (T angle)
	{
		T twoPi = piTimes (2), negTwoPi = neg (twoPi);
		while (isLessThan (twoPi, angle)) angle = sumOf (angle, negTwoPi);
		return angle;
	}

	/**
	 * reflect angle over in PI/4 (co-function identity)
	 * @param angle the original value of angle
	 * @return PI/2 - angle
	 */
	@SuppressWarnings("unchecked")
	public T piOver2minus (T angle)
	{
		return sumOf (piOver (2), neg (angle));
	}

}

/**
 * wrapper for internal representation of values
 * @param <T> type on which operations are to be executed
 */
class ValueWrapper<T> extends Arithmetic<T>
	implements Arithmetic.Value<T>, SignManagementOperations
{


	/**
	 * build a value wrapper
	 * @param value the internal representation of value
	 * @param negative flag which indicates value is negative
	 * @param zero flag which indicates value is zero
	 * @param manager the manager for the type
	 */
	public ValueWrapper (T value, boolean negative, boolean zero, SpaceManager<T> manager)
	{
		super (manager);
		this.setSign (negative);
		this.value = value;
		this.zero = zero;
	}
	private T value;

	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#isZero()
	 */
	public boolean isZero() { return zero; }
	private boolean zero;
	
	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#getUnderlying()
	 */
	public T getUnderlying ()
	{
		if (zero) return manager.getZero ();
		return negative? neg (value) : value;
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#getInternal()
	 */
	public T getInternal() { return value; }

	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#update(net.myorb.math.Arithmetic.Value)
	 */
	public void update (Value<T> newValue)
	{ value = newValue.getInternal(); }

	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#negate()
	 */
	public Value<T> negate () { return negate (this); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#inverted()
	 */
	public Value<T> inverted () { return oneValue ().over (this); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#plus(net.myorb.math.Arithmetic.Value)
	 */
	public Value<T> plus (Value<T> term) { return sum (term, this); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#minus(net.myorb.math.Arithmetic.Value)
	 */
	public Value<T> minus (Value<T> x) { return difference (this, x); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#times(net.myorb.math.Arithmetic.Value)
	 */
	public Value<T> times (Value<T> factor) { return product (factor, this); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#timesProductOf(net.myorb.math.Arithmetic.Value<T>[])
	 */
	@SuppressWarnings("unchecked")
	public Value<T> timesProductOf (Value<T>... factors) { return product (productSeries (factors), this); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#over(net.myorb.math.Arithmetic.Value)
	 */
	public Value<T> over (Value<T> x) { return quotient (this, x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#accumulate(net.myorb.math.Arithmetic.Value<T>[])
	 */
	@SuppressWarnings("unchecked")
	public Value<T> accumulate (Value<T>... terms)
	{
		Value<T> accumulation = this;
		for (Value<T> term : terms) accumulation = accumulation.plus (term);
		return accumulation;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#isLessThan(net.myorb.math.Arithmetic.Value)
	 */
	public boolean isLessThan (Value<T> value)
	{
		return lessThan (this, value);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#sqrt()
	 */
	public Value<T> sqrt ()
	{
		return sqrt (this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#pow(int)
	 */
	public Value<T> pow (int exponent)
	{
		return pow (this, exponent);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Arithmetic.Value#squared()
	 */
	public Value<T> squared ()
	{
		return this.times (this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		if (zero) return "0";
		if (negative) return "-" + manager.toDecimalString (value);
		return manager.toDecimalString (value);
	}

}
