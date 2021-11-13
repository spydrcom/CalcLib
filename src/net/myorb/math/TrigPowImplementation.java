
package net.myorb.math;

/**
 * provide full complement of trigonometric function using identity equations.
 *  this layer allows an operator to be specified by enumeration
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class TrigPowImplementation<T> extends TrigAtomic<T>
{


	/**
	 * this is a local identifier 
	 *  for the Quarks interface at the Atomic layer
	 * @param <T> type on which operations are to be executed
	 */
	public interface SubAtomic<T> extends Quarks<T> {}


	/**
	 * this is the list of available operations
	 */
	public enum Operations
	{
		pow, sqrt, exp, ln,
		asin, atan, sin, cos,
		tan, cot, sec, csc,
		acos, asec, acsc, acot,
		sinh, cosh, tanh, coth, sech, csch,
		arsinh, arcosh, arsech, arcsch,
		arcoth, artanh
	}


	/**
	 * @param quarks the library of Quark level methods
	 * @param manager the type manager for required arithmetic
	 */
	public TrigPowImplementation
	(SubAtomic<T> quarks, SpaceManager<T> manager)
	{ super (quarks, manager); }


	/**
	 * @param named the text giving the name of an operation
	 * @return the enumeration constant for that operation
	 */
	public Operations getOperation (String named) 
	{
		return Operations.valueOf (named);
	}


	/**
	 * @param op the enumeration constant for the operation
	 * @param x the value on which to compute the operation
	 * @return the computed operation value
	 */
	public T evaluate (Operations op, T x)
	{
		switch (op)
		{
			case sqrt: return sqrt (x);
			case exp: return exp (x);
			case ln: return ln (x);

			case sin: return sin (x);
			case cos: return cos (x);
			case tan: return tan (x);
			case cot: return cot (x);
			case sec: return sec (x);
			case csc: return csc (x);
		
			case asin: return asin (x);
			case acos: return acos (x);
			case asec: return asec (x);
			case acsc: return acsc (x);
			case acot: return acot (x);
			case atan: return atan (x);
			
			case sinh: return sinh (x);
			case cosh: return cosh (x);
			case tanh: return tanh (x);
			case coth: return coth (x);
			case sech: return sech (x);
			case csch: return csch (x);
			
			case arsinh: return arsinh (x);
			case arcosh: return arcosh (x);
			case arsech: return arsech (x);
			case arcsch: return arcsch (x);
			case arcoth: return arcoth (x);
			case artanh: return artanh (x);

			default: throw new RuntimeException ("Operator Error");
		}
	}


	/**
	 * @param fromOp the name of the operation
	 * @param x the value on which to compute the operation
	 * @return the computed operation value
	 */
	public T compute (String fromOp, T x)
	{
		return evaluate (getOperation (fromOp), x);
	}


	/**
	 * @param op the enumeration constant for the operation
	 * @param x the value on which to compute the operation
	 * @param n the exponent for the final computation
	 * @return the computed operation value toThe nTH
	 */
	public T trigPow (Operations op, T x, int n)
	{
		T opVal = evaluate (op, x);
		if (n != 1) return quarks.pow (opVal, n);
		return opVal;
	}


}

