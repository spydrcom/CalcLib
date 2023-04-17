
package net.myorb.math.polynomial.algebra;

/**
 * management of parameter profiles and parameter substitution
 * @author Michael Druckman
 */
public class ParameterManagement extends Utilities
{


	/**
	 * get and set variable name
	 * @param polynomialVariable the name of the parameter in the profile
	 */
	public void setPolynomialVariable
		(String polynomialVariable) { this.polynomialVariable =  polynomialVariable; }
	public String getPolynomialVariable () { return polynomialVariable; }
	protected String polynomialVariable = null;


	/**
	 * verify polynomial variable description
	 * @return the parameter read from the function profile
	 */
	public TextItems parameterList ()
	{
		TextItems parameterNameList = new TextItems ();
		try { parameterNameList.add ( getPolynomialVariable () ); }
		catch (Exception e) { error ( "Error in function profile", e ); }
		return parameterNameList;
	}


	/**
	 * identify actual parameter for use in substitutions
	 * @param actualParameter the description of the actual parameter
	 */
	public void prepareParameterSubstitution
		(
			Elements.Factor actualParameter
		)
	{
		this.actualParameter = actualParameter;
	}


	/**
	 * check for formal parameter reference
	 * @param name the name of the identifier
	 * @return TRUE when identifier matches formal
	 */
	public boolean referencesFormalParameter (String name)
	{
		return actualParameter != null && name.equals ( getPolynomialVariable () );
	}


	/**
	 * @return the captured actual parameter factor
	 */
	protected Elements.Factor
		getActualParameter () { return actualParameter; }
	protected Elements.Factor actualParameter = null;


}

