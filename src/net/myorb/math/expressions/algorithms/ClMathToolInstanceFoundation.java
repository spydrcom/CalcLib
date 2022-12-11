
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.integration.Configuration;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap;

/**
 * manage parameterization of solution product representation.
 * - boiler-plate for data posted to symbol table supporting library instances
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathToolInstanceFoundation <T>
	implements SymbolMap.Named, SymbolMap.VariableLookup
{

	/**
	 * pull manager objects from session environment
	 * @param environment the session control structure
	 */
	public void extractEnvironment (Environment <T> environment)
	{
		this.vm = environment.getValueManager ();
		this.mgr = environment.getSpaceManager ();
		this.environment = environment;
	}
	protected ExpressionSpaceManager <T> mgr;
	protected Environment <T> environment;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getSymbolType()
	 */
	public SymbolMap.SymbolType getSymbolType () { return symbolType; }
	public void setSymbolType (SymbolMap.SymbolType symbolType) { this.symbolType = symbolType; }
	protected SymbolMap.SymbolType symbolType = SymbolMap.SymbolType.CONSTANT;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.VariableLookup#getValue()
	 */
	public ValueManager.GenericValue getValue () { return vm.newStructure (this); }
	protected ValueManager <T> vm;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineTool.Algorithm#getConfiguration()
	 */
	public Configuration getConfiguration () { return configuration; }
	protected Configuration configuration;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.VariableLookup#rename(java.lang.String)
	 */
	public void rename (String to) {}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.Named#getName()
	 */
	public String getName () { return name; }
	protected String name;

}
