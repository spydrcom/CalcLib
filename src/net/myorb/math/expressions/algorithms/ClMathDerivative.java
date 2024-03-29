
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.DerivativeApproximation;

import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ImplementedFeatures;
import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ParameterizationManager;
import net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Renderer;

import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.AssignedVariableStorage;
import net.myorb.math.expressions.symbols.GenericWrapper;
import net.myorb.math.expressions.symbols.LibraryObject;

import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.List;
import java.util.Map;

/**
 * manage parameterization of derivatives treated as functions
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathDerivative<T> extends AlgorithmImplementationAbstraction<T>
	implements SymbolMap.FactoryForImports
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.FactoryForImports#importSymbolFrom(java.lang.String, java.util.Map)
	 */
	public SymbolMap.Named importSymbolFrom
	(String named, Map<String, Object> configuration)
	{
		return new DerivativeAbstraction (named, configuration);
	}


	/**
	 * Derivative function object base class
	 */
	public class DerivativeAbstraction extends ImplementationAbstraction
		implements SymbolMap.ImportedFunction
	{

		public DerivativeAbstraction (String sym, LibraryObject<T> lib)
		{ this (sym, lib.getParameterization ()); }

		public DerivativeAbstraction
		(String sym, Map<String,Object> parameterMap)
		{
			super (sym, parameterMap);
			this.configuration = configurationDescriptionFor
					(ClMathDerivative.class.getCanonicalName (), parameterMap);
			this.runManager = getParameterCalled ("run");
		}
		protected ParameterManager<T> runManager;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.SymbolMap.ConfiguredImport#getConfiguration()
		 */
		public Map<String, Object> getConfiguration ()
		{
			return configuration;
		}
		Map<String, Object> configuration;

	}


	/**
	 * an object that manages configuration from start-up XML source
	 */
	class ConfigurableDerivativeImplementation extends Implementation
	{

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Implementation#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String markupForDisplay
		(String operator, String parameters, NodeFormatting using)
		{ return formatter.render (using); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Implementation#evaluate(java.util.List)
		 */
		public T evaluate (List<T> using) { return function.eval (null); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.CommonOperatorLibrary.CommonFunctionImplementation#configure(java.lang.String)
		 */
		public void configure (String parameters) { establishImplementedFeatures (configureDerivativeManager (parameters)); }

	}


	/**
	 * @return allocated object that manages configuration from start-up XML source
	 */
	public CommonFunctionImplementation getConfigurableDerivativeImplementation ()
	{
		return new ConfigurableDerivativeImplementation ();
	}


	/**
	 * allocate manager object for function
	 * @param parameters the parameter text supplied in configuration
	 * @return a configured function wrapper
	 */
	public DerivativeParameterManager<T>
		configureDerivativeManager (String parameters)
	{ return configureDerivativeManager (new DerivativeParameterManager<T> (parameters, environment)); }

	/**
	 * allocate manager object for function
	 * @param parameters the parameter map supplied in configuration
	 * @return a configured function wrapper
	 */
	public DerivativeParameterManager<T>
		configureDerivativeManager (Map<String,Object> parameters)
	{ return configureDerivativeManager (new DerivativeParameterManager<T> (parameters, environment)); }

	/**
	 * configure a parameter manager to represent a function
	 * @param derivative manager for parameters of derivative function
	 * @return the manager being configured
	 */
	public DerivativeParameterManager<T>
		configureDerivativeManager (DerivativeParameterManager<T> derivative)
	{ return derivative.buildFunction (manager, library); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#configureManager(java.util.Map)
	 */
	public AlgorithmImplementationAbstraction.Configuration<T>
		configureManager (Map<String, Object> parameterMap)
	{ return configureDerivativeManager (parameterMap); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#provideImplementation()
	 */
	public AlgorithmImplementationAbstraction<T>.Implementation
			provideImplementation ()
	{
		return new ConfigurableDerivativeImplementation ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		return new DerivativeAbstraction (sym, lib);
	}


}


/**
 * localize derivative processing as operator and as function syntax
 * @param <T> data type being processed
 */
class DerivativeParameterManager<T> implements
	AlgorithmImplementationAbstraction.ImplementedFeatures<T>,
	AlgorithmImplementationAbstraction.Configuration<T>,
	AlgorithmImplementationAbstraction.Renderer
{

	/*
	 * render syntax options
	 * * Euler		Dx f(x)
	 * * Lagrange	f'(x)
	 * * Leibniz	df/dx
	 * */
	public enum Syntax {Euler, Lagrange, Leibniz}

	/**
	 * @param parameters map of configured parameters
	 * @param environment description of the session
	 */
	DerivativeParameterManager (Map<String,Object> parameters, Environment<T> environment)
	{
		this
		(
			parameters.get ("symbol"), parameters.get ("order"), parameters.get ("function"),
			parameters.get ("variable"), parameters.get ("run"), parameters.get ("syntax"),
			environment
		);
	}
	DerivativeParameterManager (String parameters, Environment<T> environment)
	{
		this (parameters.split (";"), environment);
	}
	DerivativeParameterManager (String[] parameters, Environment<T> environment)
	{
		this (parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5], environment);
	}
	DerivativeParameterManager
		(
			Object symbol, Object order, Object function,
			Object variable, Object run, Object syntax,
			Environment<T> environment
		)
	{
		this.variableName = variable.toString ();
		this.symbols = environment.getSymbolMap ();
		this.parseRun (run.toString (), environment);
		this.identifyFunction (function.toString ());

		if (symbol != null)
		{
			this.symbolName = symbol.toString ();

			if (symbolName.charAt (0) == 'P')
			{
				this.symbolName = OperatorNomenclature.PARTIAL_RENDER;
			}
		}
		if (order != null) this.order = Integer.parseInt (order.toString ());

		if (syntax != null)
		{
			try { this.syntax = Syntax.valueOf (syntax.toString ()); }
			catch (Exception e) { throw new RuntimeException (BAD_REQUEST); }
		}

		switch (this.syntax)
		{
			case Euler:		if (this.symbolName == null) this.symbolName = "D"; break;
			case Leibniz:	if (this.symbolName == null) this.symbolName = "d"; break;
			default:
		}
	}
	static final String BAD_REQUEST =
		"Requested render syntax not recognized";
	protected String symbolName = null, variableName;
	protected Syntax syntax = Syntax.Leibniz;
	protected SymbolMap symbols;
	protected int order = 1;


	/**
	 * locate named function in symbol table
	 * @param functionName name of the function targeted for derivative
	 */
	@SuppressWarnings("unchecked")
	void identifyFunction (String functionName)
	{
		this.impl = symbols.get (this.functionName = functionName);
		if (impl == null) throw new RuntimeException ("Function not recognized: " + functionName);
		this.function = ((Subroutine<T>) impl).toSimpleFunction ();
	}
	protected Function<T> function;
	protected String functionName;
	protected Object impl;


	/**
	 * parse the configuration source for the run value
	 * @param configuration the text of the configuration parameters
	 * @param environment description of the session
	 */
	void parseRun (String configuration, Environment<T> environment)
	{
		runManager = new ParameterManager<T> (environment);
		runManager.setExpression (configuration);
	}
	public T getRun () { return runManager.eval (); }
	protected ParameterManager<T> runManager;


	/**
	 * construct DerivativeApproximation instance
	 * @param manager the description of the domain space
	 * @param library the library holding the model for the function
	 * @return THIS for chaining
	 */
	DerivativeParameterManager<T> buildFunction
	(SpaceManager<T> manager, ExtendedPowerLibrary<T> library)
	{
		this.mgr = manager;
		this.derivative = DerivativeApproximation.getDerivativesFor
				(function, getRun ()).forOrder (order);
		return this;
	}
	protected GenericWrapper.GenericFunction<T> derivative;
	protected SpaceManager<T> mgr;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ImplementedFeatures#getFunction()
	 */
	public Function<T> getFunction ()
	{
		return new Function<T> ()
		{
			public T eval (T x) { return derivative.eval (getVarValue ()); }
			public SpaceDescription<T> getSpaceDescription () { return mgr; }
			public SpaceManager<T> getSpaceManager () { return mgr; }
		};
	}
	@SuppressWarnings("unchecked") T getVarValue ()
	{
		AssignedVariableStorage value =
			(AssignedVariableStorage) symbols.get (variableName);
		ValueManager.DiscreteValue<T> dv = (ValueManager.DiscreteValue<T>)
				value.getValue ();
		return dv.getValue ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Renderer#render(net.myorb.math.expressions.gui.rendering.NodeFormatting)
	 */
	public String render (NodeFormatting using)
	{
		String symId, funId, varId, fn;

		switch (syntax)
		{
			case Euler: // Dx (n) f(x)

				symId = using.formatIdentifierReference (symbolName);
				funId = using.formatIdentifierReference (functionName);
				varId = using.formatIdentifierReference (variableName);
				fn = using.formatSubScript (symId, varId);

				if (order > 1)
				{
					String orderSup = using.formatParenthetical
						(using.formatNumericReference (Integer.toString (order)));
					fn = using.formatSuperScript (fn, orderSup);
				}

				return fn + funId + using.formatParenthetical (using.formatIdentifierReference (variableName));

			case Lagrange: // f'(x)

				if (order == 1)
				{
					fn = using.formatIdentifierReference (functionName + OperatorNomenclature.PRIME_RENDER);
				}
				else if (order == 2)
				{
					fn = using.formatIdentifierReference (functionName + DOUBLE_PRIME);
				}
				else
				{
					String orderSup = using.formatParenthetical (using.formatNumericReference (Integer.toString (order)));
					fn = using.formatSuperScript (using.formatIdentifierReference (functionName), orderSup);
				}

				return fn + using.formatParenthetical (using.formatIdentifierReference (variableName));

			case Leibniz: // df/dx

				symId = using.formatIdentifierReference (symbolName);
				funId = using.formatIdentifierReference (functionName);
				varId = using.formatIdentifierReference (variableName);
				String symIdN = symId;

				if (order > 1)
				{
					String orderSup = using.formatNumericReference (Integer.toString (order));
					symIdN = using.formatSuperScript (symIdN, orderSup);
					varId = using.formatSuperScript (varId, orderSup);
				}

				return using.formatOverUnderOperation
				(
					symIdN + funId,
					symId + varId
				);
		}

		throw new RuntimeException (BAD_REQUEST);
	}
	public static final String DOUBLE_PRIME = OperatorNomenclature.PRIME_RENDER+OperatorNomenclature.PRIME_RENDER;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.ImplementedFeatures#getFormatter()
	 */
	public Renderer getFormatter () { return this; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Configuration#getImplementedFeatures()
	 */
	public ImplementedFeatures<T> getImplementedFeatures () { return this; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Configuration#getParameterizationManager()
	 */
	public ParameterizationManager<T> getParameterizationManager ()
	{
		return new ParameterizationManager<T> ()
		{
			public ParameterManager<T> getManagerFor (String symbol)
			{
				return runManager;
			};
		};
	}


}

