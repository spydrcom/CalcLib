
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.specialfunctions.bessel.OrdinaryFirstKind;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.complexnumbers.ComplexLibrary;
import net.myorb.math.complexnumbers.ComplexValue;

import java.util.Map;

/**
 * complex evaluation of the Fourier Transform nucleus function
 *  e ^ ( - 2 * PI * i * Xi * t ), or alternatively SIN and COS,
 *  also Hartley and Hankel are available in configuration
 * @author Michael Druckman
 */
public class FourierNucleus extends ComplexIntegrandFunctionBase
{


	enum NucleusType {KERNEL, INVERSE}
	enum TransformType {COMPLEX, SIN, COS, HARTLEY, BESSEL}

	protected TransformType transformType;
	protected NucleusType nucleusType;


	public FourierNucleus
		(
			Environment<ComplexValue<Double>> environment,
			Map<String, Object> configuration
		)
	{
		String variableName = configuration.get ("basis").toString ();
		this.nucleusType = NucleusType.valueOf (configuration.get ("nucleus").toString ().toUpperCase ());
		this.transformVariable = (SymbolMap.VariableLookup) environment.getSymbolMap ().get (variableName);
		this.transformType = TransformType.valueOf (configuration.get ("type").toString ().toUpperCase ());
		this.complexLibrary = new ComplexLibrary<Double> (components, manager);
		double  sign = nucleusType == NucleusType.INVERSE ? 1.0 : -1.0;
		this.S2PI = complexLibrary.C (Math.sqrt (2.0 / Math.PI), 0.0);
		this.I2PI = complexLibrary.C (0.0, sign * 2.0 * Math.PI);
		this.S2PI2 = complexLibrary.C (S2PI.Re () / 2, 0.0);
		this.vm = environment.getValueManager ();
		this.configuration = configuration;
		this.environment = environment;
		this.getJ (configuration);
	}
	protected Environment<ComplexValue<Double>> environment;
	protected Map<String, Object> configuration;
	

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> t)
	{
		ComplexValue<Double> ut = manager.multiply (getU (), t);

		switch (transformType)
		{
			case BESSEL:
				return manager.multiply (t, Jv.eval (ut));
			case HARTLEY:
				return manager.multiply (S2PI2, cas (ut));
			case COMPLEX:
				return complexLibrary.exp (manager.multiply (I2PI, ut));
			case SIN:
				return manager.multiply (S2PI, complexLibrary.sin (ut));
			case COS:
				return manager.multiply (S2PI, complexLibrary.cos (ut));
		}

		return null;
	}
	protected ComplexLibrary<Double> complexLibrary;
	protected ComplexValue<Double> I2PI, S2PI, S2PI2;


	public void getJ (Map<String, Object> configuration)
	{
		if (transformType != TransformType.BESSEL) return;

		int terms = Integer.parseInt (configuration.get ("terms").toString ());
		double v = Double.parseDouble (configuration.get ("order").toString ());
		ComplexValue<Double> p = complexLibrary.C (v, 0.0);

		PolynomialSpaceManager<ComplexValue<Double>> psm =
				new PolynomialSpaceManager<ComplexValue<Double>>(environment.getSpaceManager ());
		Jv = OrdinaryFirstKind.getJ (p, terms, complexLibrary, psm);
	}
	protected SpecialFunctionFamilyManager.FunctionDescription<ComplexValue<Double>> Jv;


	/**
	 * cosine and sine as specified in the Hartley definition
	 * @param ut the product of the transform pair of u and t
	 * @return the computed value of cas for the ut product
	 */
	public ComplexValue<Double> cas (ComplexValue<Double> ut)
	{
		return manager.add (complexLibrary.sin (ut), complexLibrary.cos (ut));
	}


	/**
	 * @return the value of the transform variable
	 */
	public ComplexValue<Double> getU ()
	{
		return vm.toDiscrete (transformVariable.getValue ());
	}
	protected final ValueManager<ComplexValue<Double>> vm;
	protected SymbolMap.VariableLookup transformVariable;


}

