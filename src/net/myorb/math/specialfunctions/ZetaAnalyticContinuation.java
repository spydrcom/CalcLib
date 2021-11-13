
package net.myorb.math.specialfunctions;

import net.myorb.data.abstractions.SpaceConversion;
import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * implementation of Analytic Continuation for Riemann Zeta function
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class ZetaAnalyticContinuation<T> implements Zeta.AnalyticContinuation <T>
{


	/**
	 * access to library of operators for data type T
	 * @param <T> type of component values on which operations are to be executed
	 */
	public interface RequiredFunctionality<T>
		extends Zeta.ContinuationDomainRecognition<T>
	{
		Function <T> getExp ();
		Function <T> getGamma ();
		Function <T> getZeta ();
		Function <T> getSin ();
	}


	/**
	 * @param operators reference to library of operators on space T
	 * @param conversion implementation of data type conversions for type T
	 * @param manager a type manager for T
	 */
	public ZetaAnalyticContinuation
		(
			RequiredFunctionality<T> operators,
			SpaceConversion<T> conversion,
			SpaceManager<T> manager
		)
	{
		this.exp = operators.getExp ();
		this.gamma = operators.getGamma ();
		this.zeta = operators.getZeta ();
		this.sin = operators.getSin ();

		this.ONE = manager.getOne ();
		this.negOne = manager.newScalar (-1);
		this.logPi = conversion.convertFromDouble (Math.log (Math.PI));
		this.piOver2 = conversion.convertFromDouble (Math.PI / 2);
		this.log2 = conversion.convertFromDouble (Math.log (2));

		this.domainRecognition = operators;
		this.manager = manager;
	}
	protected T log2, logPi, piOver2, negOne, ONE;
	protected Zeta.ContinuationDomainRecognition<T> domainRecognition;
	protected Function <T> exp, sin, gamma, zeta;
	protected SpaceManager<T> manager;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T s)
	{
		// 2^s * pi^(s-1) * sin (s * pi/2) * GAMMA (1-s) * zeta (1-s)
		T oneMinusS = manager.add (manager.negate (s), manager.getOne ());
		T sLog2 = manager.multiply (s, log2), sPiOver2 = manager.multiply (s, piOver2);
		T sMinus1logPi = manager.multiply (manager.add (s, negOne), logPi);

		T product = manager.multiply
			(
				exp.eval (manager.add (sLog2, sMinus1logPi)),	// 2^s * pi^(s-1)
				sin.eval (sPiOver2)								// sin (s * pi/2)
			);
		product = manager.multiply (product, gamma.eval (oneMinusS));
		product = manager.multiply (product, zeta.eval (oneMinusS));

		return product;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.Zeta.AnalyticContinuation#inZetaContinuationDomain(java.lang.Object)
	 */
	public boolean inZetaContinuationDomain (T value)
	{
		return domainRecognition.inZetaContinuationDomain (value);
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription ()
	{ return manager; }


	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceManager ()
	{ return manager; }


}
