
package net.myorb.math.specialfunctions;

import net.myorb.math.Function;
import net.myorb.math.PowerLibrary;
import net.myorb.math.SpaceManager;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.SpaceConversion;

/**
 * implementation of Zeta with Analytic Continuation extension
 * @author Michael Druckman
 */
public class ZetaRealAnalytic extends Zeta <Double>
{

	public ZetaRealAnalytic
		(
			SpaceManager<Double> manager,
			PowerLibrary<Double> powerLibrary,
			SpaceConversion<Double> conversion
		)
	{
		super (manager, powerLibrary, conversion);
		this.setAnalyticContinuation (getAnalyticContinuation ());
	}

	/**
	 * @return the Continuation implementation
	 */
	AnalyticContinuation <Double> getAnalyticContinuation ()
	{
		ZetaAnalyticContinuation.RequiredFunctionality <Double>
			operators = new ZetaContinuationRequiredFunctionality (manager, this);
		return new ZetaAnalyticContinuation <Double> (operators, conversion, manager);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.Zeta#eval(java.lang.Object)
	 */
	public Double eval (Double s)
	{
		if (s == 0) return 0.0;
		else return super.eval (s);
	}

}

/**
 * support operations provided by JRE Math class
 *  except gamma and zeta functions provided by special_functions package,
 *  Gamma class and Zeta class
 */
class ZetaContinuationRequiredFunctionality
	implements ZetaAnalyticContinuation.RequiredFunctionality <Double>
{

	public ZetaContinuationRequiredFunctionality
		(
			SpaceManager<Double> manager,
			Zeta <Double> zeta
		)
	{
		this.manager = manager;
		this.gamma = new Gamma ();
		this.zeta = zeta;
	}
	SpaceManager<Double> manager;
	Zeta <Double> zeta;
	Gamma gamma;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.Zeta.ContinuationDomainRecognition#inZetaContinuationDomain(java.lang.Object)
	 */
	public boolean inZetaContinuationDomain (Double value)
	{
		return value <= 1;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.ZetaAnalyticContinuation.RequiredFunctionality#getExp()
	 */
	public Function<Double> getExp ()
	{
		return new FunctionBoilerPlate ()
		{
			public Double eval (Double x) { return Math.exp (x); }
		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.ZetaAnalyticContinuation.RequiredFunctionality#getGamma()
	 */
	public Function<Double> getGamma ()
	{
		return new FunctionBoilerPlate ()
		{
			public Double eval (Double x) { return gamma.eval (x); }
		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.ZetaAnalyticContinuation.RequiredFunctionality#getZeta()
	 */
	public Function<Double> getZeta ()
	{
		return new FunctionBoilerPlate ()
		{
			public Double eval (Double x) { return zeta.eval (x); }
		};
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.ZetaAnalyticContinuation.RequiredFunctionality#getSin()
	 */
	public Function<Double> getSin ()
	{
		return new FunctionBoilerPlate ()
		{
			public Double eval (Double x) { return Math.sin (x); }
		};
	}

	/**
	 * reduce function to simple eval method coding
	 */
	class FunctionBoilerPlate implements Function <Double>
	{
		public SpaceDescription<Double> getSpaceDescription () { return manager; }
		public SpaceManager<Double> getSpaceManager () { return manager; }
		public Double eval (Double x) { return null; }
	}

}


