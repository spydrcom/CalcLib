
package net.myorb.math.expressions.algorithms;

import net.myorb.math.linalg.SolutionPrimitives;
import net.myorb.math.linalg.SolutionPrimitives.Decomposition;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.computational.integration.Configuration;
import net.myorb.math.computational.Parameterization;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.reflection.ObjectManagement;

import java.util.Map;

/**
 * manage parameterization of linear algebra solutions
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathSysEQ  <T> extends InstanciableFunctionLibrary <T>
		implements SymbolMap.FactoryForImports
{


	/**
	 * describe objects that can solve systems of equations
	 * @param <S> data type for solutions
	 */
	public interface SolutionProvider <S>
	{
		/**
		 * @return a solution object provided by an instance
		 */
		SolutionPrimitives <S> provideSolution ();
	}
	public interface SolutionProduct <S> extends SolutionProvider <S>
	{
		/**
		 * @return a Decomposition produced by the associated solution
		 */
		SolutionPrimitives.Decomposition getProduct ();
	}
	public interface SolutionManager <S> extends SolutionProvider <S>
	{
		/**
		 * @param D a Decomposition to be associated
		 * @return a SolutionProduct wrapper object
		 */
		SolutionProduct <S> wrap (SolutionPrimitives.Decomposition D);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.FactoryForImports#importSymbolFrom(java.lang.String, java.util.Map)
	 */
	public SymbolMap.Named importSymbolFrom
	(String named, Map<String, Object> configuration)
	{
		this.sym = named;
		this.options = Parameterization.copy (configuration);
		return generateTool (sym);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		this.sym = sym;
		this.options = Parameterization.copy (lib.getParameterization ());
		return generateTool (sym);
	}
	protected String sym;


	/**
	 * @param sym the name of the symbol
	 * @return the created SysEQ tool
	 */
	public SysEQTool <T> generateTool (String sym)
	{
		SysEQTool <T> tool = new SysEQTool <T> (sym, options);
		tool.setEnvironment (environment);
		return tool;
	}
	protected Parameterization.Hash options;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getIterationConsumerDescription()
	 */
	public Map<String, Object> getIterationConsumerDescription ()
	{
		return new Parameterization.Hash (sym, "CLASSPATH", this.getClass (), options);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#buildIterationConsumer(java.util.Map)
	 */
	public IterationConsumer buildIterationConsumer (Map<String, Object> options)
	{
		return null;
	}


}


/**
 * description of the tool as placed in the symbol table
 * @param <T> data type being processed
 */
class SysEQTool <T> implements ClMathSysEQ.SolutionManager <T>,
		SymbolMap.Named, SymbolMap.VariableLookup
{

	public SysEQTool (String name, Parameterization.Hash options)
	{
		this.configuration = new Configuration (options);
		this.name = name;
	}

	/**
	 * provide the environment to the tool
	 * @param environment the session control structure
	 */
	public void setEnvironment (Environment<T> environment)
	{
		this.buildSolution
		(
			this.configuration.getParameter ("solution"),
			parameterFrom (this.environment = environment)
		);
		System.out.println ("Solution built: " + solution.getClass ().getCanonicalName ());
		this.vm = environment.getValueManager ();
	}
	protected Environment<T> environment;

	/**
	 * @param environment the session control structure
	 * @return the space manager taken from the environment wrapped as a reflection parameter
	 */
	public ObjectManagement.ObjectList parameterFrom (Environment<T> environment)
	{
		ObjectManagement.ObjectList
			constructorParameters = new ObjectManagement.ObjectList ();
		constructorParameters.add (mgr = environment.getSpaceManager ());
		return constructorParameters;
	}
	protected ExpressionSpaceManager <T> mgr;

	/**
	 * @param solutionPath class path to the solution object
	 * @param parameter the space manager taken from the environment
	 */
	@SuppressWarnings("unchecked")
	public void buildSolution
		(
			String solutionPath, ObjectManagement.ObjectList parameter
		)
	{
		try
		{
			Class<?> classDescriptor = Class.forName (this.solutionPath = solutionPath);
			Object o = ObjectManagement.doConstruct (classDescriptor, parameter);
			this.solution = (SolutionPrimitives <T>) o;
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Solution build failed", e);
		}
	}
	protected String solutionPath;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathSysEQ.SolutionProvider#provideSolution()
	 */
	public SolutionPrimitives <T>
		provideSolution () { return solution; }
	protected SolutionPrimitives <T> solution;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getName()
	 */
	public String
		getName () { return name; }
	protected String name;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolMap.SymbolType getSymbolType () { return SymbolMap.SymbolType.CONSTANT; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.VariableLookup#rename(java.lang.String)
	 */
	public void rename (String to) {}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.VariableLookup#getValue()
	 */
	public ValueManager.GenericValue
		getValue () { return vm.newStructure (this); }
	protected ValueManager <T> vm;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineTool.Algorithm#getConfiguration()
	 */
	public Configuration
		getConfiguration () { return configuration; }
	protected Configuration configuration;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathSysEQ.SolutionManager#wrap(net.myorb.math.linalg.SolutionPrimitives.Decomposition)
	 */
	public ClMathSysEQ.SolutionProduct <T>
		wrap (SolutionPrimitives.Decomposition D)
	{ return new SolutionProduct (D); }

	/**
	 * associate a Decomposition with the parent solution
	 */
	class SolutionProduct implements ClMathSysEQ.SolutionProduct <T>, ClMathBIF.FieldAccess, ValueManager.PortableValue <T>
	{

		SolutionProduct (SolutionPrimitives.Decomposition D) { this.D = D; }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathBIF.FieldAccess#getFieldNamed(java.lang.String)
		 */
		public GenericValue getFieldNamed (String name) { return ClMathBIF.getField (name, D); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathSysEQ.SolutionProvider#provideSolution()
		 */
		public SolutionPrimitives <T> provideSolution () { return solution; }

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString () { return D.toString (); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.ClMathSysEQ.SolutionProduct#getProduct()
		 */
		public Decomposition getProduct () { return D; }
		protected SolutionPrimitives.Decomposition D;

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.Portable.AsJson#toJson(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		public JsonValue toJson (ExpressionSpaceManager <T> manager)
		{
			return ( ( ValueManager.PortableValue <T> ) D ).toJson (mgr);
		}

	}

}

