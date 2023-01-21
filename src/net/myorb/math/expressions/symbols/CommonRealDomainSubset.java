
package net.myorb.math.expressions.symbols;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.CommonComplexFunctionBase;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.realnumbers.CommonRealFunctionBase;
import net.myorb.data.abstractions.Function;

import java.util.Map;

/**
 * common function base extended to allow real domain access as sub-set of complex domain
 * - generic type breaks out to object specific to session data type
 * - real domain treated as wrapper of complex version
 * @param <T> data type for session operations
 * @author Michael Druckman
 */
public class CommonRealDomainSubset <T> extends CommonFunctionBase <T>
{


	/**
	 * base class for complex function that will allow real subsets
	 */
	public static class ComplexDefinition
			extends CommonComplexFunctionBase
	{

		public ComplexDefinition
			(
				String functionNamed
			)
		{ super (functionNamed); }

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
		 */
		public ComplexValue <Double> eval
			(ComplexValue <Double> z) { return implementation.eval (z); }
		protected Function < ComplexValue <Double> > implementation = null;

		/**
		 * specify the implementation of the function
		 * @param implementation an object that implements the algorithm
		 */
		public void setimplementation
			(Function < ComplexValue <Double> > implementation)
		{ this.implementation = implementation; }

		/* (non-Javadoc)
		 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
		 */
		public void addConfiguration (Map <String, Object> parameters)
		{
			this.boundaryEnforced =
				parameters.get ("enforced") != null;			
			super.addConfiguration (parameters);
		}
		protected boolean boundaryEnforced = false;

		/**
		 * @return TRUE if RE part of complex numbers allowed in real domain results
		 */
		public boolean isBoundaryEnforced ()
		{
			return boundaryEnforced;
		}

		/**
		 * change name of other function to match ths
		 * @param otherFunction function to be renamed
		 */
		public void adoptName (CommonFunctionBase <?> otherFunction)
		{
			otherFunction.parameterNameConvention = this.parameterNameConvention;
			otherFunction.functionNamed = this.functionNamed;
		}

	}


	public CommonRealDomainSubset () { super (null, null); }	// naming will be set by Defining Occurrence


	/**
	 * identify the core function to use for behavior of this object
	 * @param definition the complex function object that defines behavior
	 */
	public void setDefiningOccurrence
		(
			ComplexDefinition definition
		)
	{
		(this.complexImplementation = definition).adoptName (this);
	}


	/*
	 * implementation of function
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T z)
	{ return sessionSpecific.eval (z); }
	protected Function < T > sessionSpecific;


	/*
	 * accept configuration
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CommonFunctionBase#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		super.addConfiguration (parameters);
		this.complexImplementation.addConfiguration (parameters);
	}
	protected ComplexDefinition complexImplementation;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.CommonFunctionBase#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment (Environment <T> environment)
	{
		super.setEnvironment (environment);

		switch (manager.getDataType ())						// switch on session data type as identified by data type manager in environment object
		{
			case Real: identifyAsReal (); break;							// function wrapper allows complex function to process reals
			case Complex: identifyAsComplex (); break;
			default: throw new RuntimeException ("Unexpected data type");
		}
	}


	/**
	 * real domain version of function as sub-set of complex definition
	 */
	class SubsetDomain extends CommonRealFunctionBase
	{

		public SubsetDomain ()
		{
			super (null);
			complexImplementation.adoptName (this);
			this.parameterNameConvention = "x";
		}

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
		 */
		public Double eval (Double x)
		{
			ComplexValue <Double>
				result = complexImplementation.eval
					(ComplexSpaceCore.manager.C (x, 0.0));
			if (complexImplementation.isBoundaryEnforced () && result.Im () != 0.0)
			{ throw new RuntimeException ("function result is complex"); }
			else return result.Re ();
		}

	}


	/*
	 * acceptance of function object based on data type
	 */

	@SuppressWarnings("unchecked") void identifyAsReal ()
	{
		this.sessionSpecific = ( Function < T > )  new SubsetDomain ();					// real wrapper for complex function
	}
	@SuppressWarnings("unchecked") void identifyAsComplex ()
	{
		this.sessionSpecific = ( Function < T > ) this.complexImplementation;			//  complex function as configured
	}


}

