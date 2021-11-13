
package net.myorb.math.computational;

import net.myorb.math.*;
import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * algorithms for Characterization of elements of a function
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class PolynomialFunctionCharacteristics<T> extends PolynomialRoots<T>
{

	/**
	 * the type of the Characterization being described
	 */
	public enum CharacteristicType
		{
			ZERO,			// the function is 0 at this X location
			DZERO,			// the function derivative is 0 at this X location
			MAX,			// the function has a local MAX value at this X location
			MIN,			// the function has a local MIN value at this X location
			INFLECTION,		// the function has a zero derivative but it is not MAX or MIN
			IMAGINARY		// the function has an imaginary root
		}

	/**
	 * access to the attributes that define a set of function characteristics
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface CharacteristicAttributes<T>
	{
		/**
		 * get the X axis value of this element
		 * @return the value of X for this element description
		 */
		T getX ();

		/**
		 * get the function value at this element X location
		 * @return function value at X [ f(X) ]
		 */
		T getFOfX ();

		/**
		 * get the function derivative value at this element X location
		 * @return function derivative value at X [ f'(X) ]
		 */
		T getFPrimeOfX ();

		/**
		 * get the second function derivative value at this element X location
		 * @return second function derivative value at X [ f''(X) ]
		 */
		T getFPrime2OfX ();

		/**
		 * get the type of characterization of this element
		 * @return the type of characterization
		 */
		CharacteristicType getCharacteristicType ();

		/**
		 * get a description of the function
		 * @return a PowerFunction object that describes the function
		 */
		Polynomial.PowerFunction<T> getFunction ();
	}

	/**
	 * build a library object based on type manager
	 * @param manager the manager for the type being manipulated
	 * @param lib an implementation of the power library
	 */
	public PolynomialFunctionCharacteristics
		(SpaceManager<T> manager, PowerLibrary<T> lib)
	{
		super (manager, lib);
	}

	/**
	 * evaluate function and built map of characteristics
	 * @param polynomial the polynomial that describes the function
	 * @return a map object that lists key points in the function
	 */
	public Map<T,CharacteristicAttributes<T>> evaluate
		(Polynomial.PowerFunction<T> polynomial)
	{
		List<T> derivativeZeros =
			computeDerivativeZeroes (polynomial.getCoefficients ());
		List<T> roots = locateFunctionRoots (derivativeZeros, polynomial, 0);
		Map<T,CharacteristicAttributes<T>> map = new HashMap<T,CharacteristicAttributes<T>> ();
		FunctionalSpecs<T> specs = new FunctionalSpecs<T> (polynomial);

		for (T x : roots)
		{
			FunctionalElement<T> element =
				new FunctionalElement<T> (x, specs, CharacteristicType.ZERO);
			map.put (x, element);
		}

		for (T x : derivativeZeros)
		{
			FunctionalElement<T> element =
				new FunctionalElement<T> (x, specs, CharacteristicType.DZERO);
			T d2 = element.getFPrime2OfX ();
			
			if (manager.isZero (d2)) element.type = CharacteristicType.INFLECTION;
			else if (manager.isNegative (d2)) element.type = CharacteristicType.MAX;
			else element.type = CharacteristicType.MIN;

			map.put (x, element);
		}

		return map;
	}

	/**
	 * build attributes for imaginary roots
	 * @param polynomial the function being evaluated
	 * @param roots the list of imaginary roots
	 * @return list of attributes
	 */
	public List<CharacteristicAttributes<T>> evaluateRoots
		(Polynomial.PowerFunction<T> polynomial, List<T> roots)
	{
		FunctionalSpecs<T> specs = new FunctionalSpecs<T> (polynomial);
		List<CharacteristicAttributes<T>> attributes = new ArrayList<CharacteristicAttributes<T>>();

		for (T x : roots)
		{
			FunctionalElement<T> element =
				new FunctionalElement<T> (x, specs, CharacteristicType.IMAGINARY);
			attributes.add (element);
		}

		return attributes;
	}

}


/**
 * connect the function with its first and second derivatives
 * @param <T> type of component values on which operations are to be executed
 */
class FunctionalSpecs<T>
{
	/**
	 * from the power function description the derivative are calculated
	 * @param powerFunction the power function being described
	 */
	FunctionalSpecs (Polynomial.PowerFunction<T> powerFunction)
	{
		this.polynomialManager =
			new OrdinaryPolynomialCalculus<T> (powerFunction.getSpaceManager());
		this.firstDerivative = polynomialManager.getFunctionDerivative (powerFunction);
		this.secondDerivative = polynomialManager.getFunctionDerivative (this.firstDerivative);
		this.polynomial = powerFunction;
	}
	Polynomial.PowerFunction<T> polynomial, firstDerivative, secondDerivative;
	OrdinaryPolynomialCalculus<T> polynomialManager;
}


/**
 * characterization of one element of the function space
 * @param <T> type of component values on which operations are to be executed
 */
class FunctionalElement<T> implements PolynomialFunctionCharacteristics.CharacteristicAttributes<T>
{

	FunctionalSpecs<T> functionalSpecs;
	PolynomialFunctionCharacteristics.CharacteristicType type;

	/**
	 * describe an element
	 * @param x location on the X axis of the element
	 * @param functionalSpecs the functions being characterized
	 * @param type the type of this characterization
	 */
	FunctionalElement
		(
			T x,
			FunctionalSpecs<T> functionalSpecs,
			PolynomialFunctionCharacteristics.CharacteristicType type
		)
	{
		this.x = x;
		this.functionalSpecs = functionalSpecs;
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.FunctionCharacteristics.Characteristics#getX()
	 */
	public T getX ()
	{ return x; }
	T x;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.FunctionCharacteristics.Characteristics#getFOfX()
	 */
	public T getFOfX () { return functionalSpecs.polynomial.eval (x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.FunctionCharacteristics.Characteristics#getFPrimeOfX()
	 */
	public T getFPrimeOfX () { return functionalSpecs.firstDerivative.eval (x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.FunctionCharacteristics.Characteristics#getFPrime2OfX()
	 */
	public T getFPrime2OfX () { return functionalSpecs.secondDerivative.eval (x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.FunctionCharacteristics.Characteristics#getCharacteristicType()
	 */
	public PolynomialFunctionCharacteristics.CharacteristicType getCharacteristicType ()
	{ return type; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.PolynomialFunctionCharacteristics.CharacteristicAttributes#getFunction()
	 */
	public Polynomial.PowerFunction<T> getFunction ()
	{
		return functionalSpecs.polynomial;
	}

}


