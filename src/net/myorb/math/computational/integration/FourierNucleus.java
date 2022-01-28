
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.complexnumbers.ComplexLibrary;
import net.myorb.math.complexnumbers.ComplexValue;

/**
 * complex evaluation of the Fourier Transform nucleus function
 *  e ^ ( - 2 * PI * i * Xi * t )
 * @author Michael Druckman
 */
public class FourierNucleus extends ComplexIntegrandFunctionBase
{


	public FourierNucleus
		(
			Environment<ComplexValue<Double>> environment,
			String transformVariableName
		)
	{
		this.transformVariable = (SymbolMap.VariableLookup)
				environment.getSymbolMap ().get (transformVariableName);
		this.complexLibrary = new ComplexLibrary<Double> (components, manager);
		this.I2PI = complexLibrary.C (0.0, -2.0 * Math.PI);
		this.vm = environment.getValueManager ();
	}
	

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> t)
	{
		ComplexValue<Double> xit = manager.multiply (getXi (), t);
		return complexLibrary.exp (manager.multiply (I2PI, xit));
	}
	protected ComplexLibrary<Double> complexLibrary;
	protected ComplexValue<Double> I2PI;


	/**
	 * @return the value of the transform variable
	 */
	public ComplexValue<Double> getXi ()
	{
		return vm.toDiscrete (transformVariable.getValue ());
	}
	protected final ValueManager<ComplexValue<Double>> vm;
	protected SymbolMap.VariableLookup transformVariable;


}

