
package net.myorb.math.complexnumbers;

import net.myorb.math.specialfunctions.ZetaAnalyticContinuation;
import net.myorb.math.specialfunctions.Zeta;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.SpaceConversion;

/**
 * implementation of Analytic Continuation for Riemann Zeta function
 *  over complex numbers domain
 * @author Michael Druckman
 */
public class ZetaComplexAnalytic<T> extends Zeta <ComplexValue<T>>
{

	public ZetaComplexAnalytic
		(
			ComplexLibrary<T> complexLibrary
		)
	{
		super
		(
			complexLibrary.getComplexFieldManager (),
			complexLibrary, complexLibrary.getComplexSpaceConversion ()
		);
		this.setAnalyticContinuation (getAnalyticContinuation (complexLibrary));
	}

	/**
	 * @return the Continuation implementation
	 */
	AnalyticContinuation <ComplexValue<T>> getAnalyticContinuation (ComplexLibrary<T> complexLibrary)
	{
		ZetaAnalyticContinuation.RequiredFunctionality <ComplexValue<T>>
			operators = new ZetaContinuationRequiredFunctionality <T> (complexLibrary, conversion, manager, this);
		return new ZetaAnalyticContinuation <ComplexValue<T>> (operators, conversion, manager);
	}

}

/**
 * support operations provided by complex library,
 *  gamma uses Lanczos algorithm through class allocated in library,
 *  		exp uses cis logic separating Re and Im:
 *  cis(z) = exp (z.Re) * ( cos (z.Im) + i * sin (z.Im) )
 */
class ZetaContinuationRequiredFunctionality<T>
	implements ZetaAnalyticContinuation.RequiredFunctionality <ComplexValue<T>>
{

	public ZetaContinuationRequiredFunctionality
		(
			ComplexLibrary<T> complexLibrary,
			SpaceConversion<ComplexValue<T>> conversion,
			SpaceManager<ComplexValue<T>> manager,
			Zeta <ComplexValue<T>> zeta
		)
	{
		this.manager = manager;
		this.tmgr = complexLibrary.getComponentManager ();
		complexLibrary.initializeGamma ();
		this.lib = complexLibrary;
		this.zeta = zeta;
	}
	SpaceManager<T> tmgr;
	SpaceManager<ComplexValue<T>> manager;
	Zeta <ComplexValue<T>> zeta;
	ComplexLibrary<T> lib;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.Zeta.ContinuationDomainRecognition#inZetaContinuationDomain(java.lang.Object)
	 */
	public boolean inZetaContinuationDomain (ComplexValue<T> value)
	{
		return ! manager.lessThan (manager.getOne (), value);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.ZetaAnalyticContinuation.RequiredFunctionality#getExp()
	 */
	public Function<ComplexValue<T>> getExp ()
	{
		return new FunctionBoilerPlate ()
		{
			public ComplexValue<T> eval (ComplexValue<T> x) { return lib.nativeExp (x); }
		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.ZetaAnalyticContinuation.RequiredFunctionality#getGamma()
	 */
	public Function<ComplexValue<T>> getGamma ()
	{
		return new FunctionBoilerPlate ()
		{
			public ComplexValue<T> eval (ComplexValue<T> z) { return lib.gamma (z); }
		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.ZetaAnalyticContinuation.RequiredFunctionality#getZeta()
	 */
	public Function<ComplexValue<T>> getZeta ()
	{
		return new FunctionBoilerPlate ()
		{
			public ComplexValue<T> eval (ComplexValue<T> x) { return zeta.nonContinuationEval (x); }
		}; // alternate entry point provides for avoiding infinite recursion where ( 1 - z ) = 1 when z = 0
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.ZetaAnalyticContinuation.RequiredFunctionality#getSin()
	 */
	public Function<ComplexValue<T>> getSin ()
	{
		return new FunctionBoilerPlate ()
		{
			public ComplexValue<T> eval (ComplexValue<T> x) { return lib.sin (x); }
		};
	}

	/**
	 * reduce function to simple eval method coding
	 */
	class FunctionBoilerPlate implements Function <ComplexValue<T>>
	{
		public SpaceDescription<ComplexValue<T>> getSpaceDescription () { return manager; }
		public SpaceManager<ComplexValue<T>> getSpaceManager () { return manager; }
		public ComplexValue<T> eval (ComplexValue<T> x) { return null; }
	}

}


