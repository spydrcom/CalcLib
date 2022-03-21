
package net.myorb.math.computational.splines;

import net.myorb.math.computational.Spline;
import net.myorb.math.computational.integration.Configuration;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.evaluationstates.*;
import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.tree.*;

import net.myorb.data.notations.json.JsonSemantics;

import net.myorb.math.Function;

/**
 * attribute a spline to the expression tree of a UDF
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SplineTool <T>
{


	/**
	 * construct the factory object to be used
	 * @param <T> type on which operations are to be executed
	 */
	public interface Algorithm <T>
	{
		/**
		 * @return the hash of parameters for the spline generation
		 */
		Configuration getConfiguration ();

		/**
		 * @return a factory object based on specified parameters
		 */
		Spline.Factory <T> buildFactory ();
	}


	/**
	 * @param environment the descriptor for the session
	 */
	public SplineTool
	(Environment <T> environment)
	{
		this.mgr = (ExpressionComponentSpaceManager <T>) environment.getSpaceManager ();
		this.symbols = environment.getSymbolMap ();
		this.environment = environment;
	}
	protected ExpressionComponentSpaceManager <T> mgr;
	protected Environment <T> environment;
	protected SymbolMap symbols;


	/**
	 * construct the factory object for this tool
	 * @param fromAlgorithm the algorithm container for the factory
	 */
	public void buildFactory (Algorithm <T> fromAlgorithm)
	{
		this.configuration = fromAlgorithm.getConfiguration ();
		this.showTree = configuration.getParameter ("show") != null;
		this.factory = fromAlgorithm.buildFactory ();
	}
	protected Configuration configuration;
	protected boolean showTree;


	/**
	 * process model with spline factory
	 * @param model the UDF to be fit with spline
	 * @param range the domain to be made available by the spline
	 * @return the spline object exporting identified operations
	 */
	public Spline.Operations <T> generateSpline
		(Function <T> model, ArrayDescriptor <T> range)
	{
		return factory.generateSpline
		(
			model,
			mgr.convertToDouble (range.getLo ()),
			mgr.convertToDouble (range.getHi ()),
			configuration
		);
	}
	protected Spline.Factory <T> factory;


	/**
	 * find user defined object in the session symbol table
	 * @param functionName the name of the object in the symbol table
	 * @return the object found in the symbol table cast to Function
	 */
	public Function <T> lookupFunction (String functionName)
	{
		this.functionName = functionName;
		this.udf = Subroutine.cast (symbols.get (functionName));
		return (Function <T>) udf.toSimpleFunction ();
	}
	protected String functionName;
	protected Subroutine <T> udf;


	/**
	 * find Algorithm object in the session symbol table
	 * @param algorithmName the name of the object in the symbol table
	 * @return the object found in the symbol table cast to Algorithm
	 */
	@SuppressWarnings("unchecked")
	public Algorithm <T> lookupAlgorithm (String algorithmName)
	{
		return (Algorithm <T>) symbols.get (algorithmName);
	}


	/**
	 * apply spline tool to a UDF using algorithm found in library
	 * @param tokens the text from the command issued invoking this tool
	 */
	public void applyTool (CommandSequence tokens)
	{
		String algorithmName = tokens.get (1).getTokenImage ();
		ExtendedArrayFeatures <T> parser = new ExtendedArrayFeatures <T> ();
		ArrayDescriptor <T> range = parser.getArrayDescriptor (tokens, 3, environment);
		Function <T> function = lookupFunction (tokens.get (2).getTokenImage ());
		buildFactory (lookupAlgorithm (algorithmName));
		process (generateSpline (function, range));
	}


	/**
	 * process the spline object generated
	 * @param ops the spline object created by the factory tool
	 */
	public void process (Spline.Operations <T> ops)
	{
		udf.attachSpline (transplant (ops));
	}


	/**
	 * prepare JSON tree representation
	 * @param spline result from spline factory processing
	 * @return JSON tree representation of spline
	 */
	public JsonSemantics.JsonObject toJson
		(
			Spline.Operations <T> spline
		)
	{
		return (JsonSemantics.JsonObject) Spline.toJson
		(
			functionName, udf.getParameterNameList ().get (0), udf.toString (), spline
		);
	}


	/**
	 * translate spline to local representation
	 * @param spline result from spline factory processing
	 * @return a fitted-function representation
	 */
	public Spline.Operations <T> transplant
		(
			Spline.Operations <T> spline
		)
	{
		FittedFunction <T> fitted;
		JsonSemantics.JsonObject json = toJson (spline);
		if (showTree) Util.dump (json);

		try
		{
			SplineMechanisms mechanisms = getSplineMechanisms (json);
			Environment.provideAccess (mechanisms, environment);
			fitted = new FittedFunction <T> (mgr, mechanisms);
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Spline transplant failed", e);
		}

		fitted.processSplineDescription (json);
		return fitted;
	}


	/**
	 * @param profile a JSON profile object describing a spline
	 * @return a SplineMechanisms object constructed from the path
	 */
	public static SplineMechanisms getSplineMechanisms (JsonSemantics.JsonObject profile)
	{
		try
		{
			String path = JsonSemantics.getStringOrNull
				(profile.getMemberCalled ("Interpreter"));
			if (path == null) { return new ChebyshevSpline (); }
			SplineMechanisms mechanisms = (SplineMechanisms)
				Class.forName (path).newInstance ();
			return mechanisms;
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Interpreter not found", e);
		}
	}


}

