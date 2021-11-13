
package net.myorb.math.specialfunctions;

import net.myorb.math.Function;
import net.myorb.math.SpaceManager;
import net.myorb.math.PowerLibrary;

import net.myorb.data.abstractions.SpaceConversion;
import net.myorb.data.abstractions.SpaceDescription;

import java.util.ArrayList;

/**
 * implementation of generic Riemann Zeta function
 * @param <T>  type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class Zeta<T> implements Function <T>
{


	/**
	 * enable Recognition of values in Continuation Domain
	 * @param <T> the data type
	 */
	public interface ContinuationDomainRecognition <T>
	{
		/**
		 * @param value the value to be checked
		 * @return TRUE = use of Continuation algorithm
		 */
		boolean inZetaContinuationDomain (T value);
	}

	/**
	 * functionality for extending the zeta function domain 
	 *  with a specified Continuation algorithm
	 * @param <T> the data type
	 */
	public interface AnalyticContinuation <T>
		extends Function <T>, ContinuationDomainRecognition <T>
	{}


	/**
	 * @param manager a type manager for T
	 * @param powerLibrary an implementation of exp for type T
	 * @param conversion a conversion object for double to T
	 */
	public Zeta
		(
			SpaceManager<T> manager,
			PowerLibrary<T> powerLibrary,
			SpaceConversion<T> conversion
		)
	{
		this (manager, powerLibrary);
		this.conversion = conversion;
	}
	public Zeta
		(
			SpaceManager<T> manager,
			PowerLibrary<T> powerLibrary
		)
	{
		this.manager = manager;
		this.powerLibrary = powerLibrary;
	}
	protected SpaceConversion<T> conversion;
	protected PowerLibrary<T> powerLibrary;
	protected SpaceManager<T> manager;


	/**
	 * @param continuation an implementation of the Continuation interface
	 */
	public void setAnalyticContinuation (AnalyticContinuation <T> continuation)
	{
		this.continuation = continuation;
	}
	protected AnalyticContinuation <T> continuation = null;


	/**
	 * @param s the parameter to zeta
	 * @return the computed result
	 */
	public T eval (T s)
	{
		if (continuation != null && continuation.inZetaContinuationDomain (s))
		{
			return continuation.eval (s);
		}
		return nonContinuationEval (s);
	}
	public T nonContinuationEval (T s)
	{
		T sum = manager.getZero ();
		for (T log : logs)
		{
			T term = powerLibrary.exp
					(manager.multiply (s, log));
			sum = manager.add (sum, term);
		}
		return sum;
	}


	/**
	 * cache list of natural logarithms of first N integers
	 * @param terms the count of terms to be computed
	 */
	public void configure (int terms)
	{
		logs.clear ();
		for (int i=1; i<=terms; i++)
		{ logs.add (conversion.convertFromDouble (- Math.log (i))); }
		System.out.println ("Zeta initialized, " + terms + " terms prepared");
	}
	protected ArrayList<T> logs = new ArrayList<T> ();


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return manager; }
	public SpaceManager<T> getSpaceManager () { return manager; }


}

