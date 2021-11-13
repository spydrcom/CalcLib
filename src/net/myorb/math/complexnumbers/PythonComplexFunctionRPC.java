
package net.myorb.math.complexnumbers;

import net.myorb.data.abstractions.ServerAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * helper methods for forming Python specific complex number RPCs
 * @author Michael Druckman
 */
public class PythonComplexFunctionRPC extends VectorEnabledComplexFunctionRPC
{


	/**
	 * special case for complex value exchanges
	 */
	static PythonComplexTuplesParser complexTuplesParser = new PythonComplexTuplesParser ();


	/*
	 * server access and function reference are abstract methods
	 *  with default behavior of throwing RuntimeException in absence of override
	 */

	/**
	 * @return access object enabling RPC services
	 */
	public ServerAccess getServerAccess ()
	{
		throw new ServerAccess.ServerError ("Python RPC services have not been configured");
	}

	/**
	 * @return the text of the function description used in the call
	 */
	public String functionReference ()
	{
		throw new ServerAccess.ServerError ("Function description for RPC not found");
	}


	/*
	 * the evaluation request is implemented referring to the above abstraction
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.VectorEnabledComplexFunctionRPC#evaluateRequestUsing(net.myorb.math.complexnumbers.ComplexValue, net.myorb.math.complexnumbers.ComplexValue, net.myorb.math.complexnumbers.ComplexValue)
	 */
	public List<ComplexValue<Double>> evaluateRequestUsing
		(
			ComplexValue<Double> lo,
			ComplexValue<Double> hi,
			ComplexValue<Double> inc
		)
	{
		return evaluateRequest
		(
			functionReference (),
			lo, hi, inc, getServerAccess ()
		);
	}


	/*
	 * evaluation request implementation
	 */

	/**
	 * build RPC call based on formatted function text
	 *  and the domain information broken into lo, hi, inc
	 * @param functionReference the text of the function reference
	 * @param lo the lo value of the vector, the starting point
	 * @param hi the hi value of the vector, the ending point
	 * @param inc the increment to use for the domain
	 * @param access the communication object
	 * @return the list of complex numbers
	 */
	public static List<ComplexValue<Double>> evaluateRequest
		(
			String functionReference,
			ComplexValue<Double> lo,
			ComplexValue<Double> hi,
			ComplexValue<Double> inc,
			ServerAccess access
		)
	{
		String request = buildCall (functionReference, lo, hi, inc);
		return getValues (access, request);
	}


	/**
	 * build call to getFtuples Python method
	 * @param functionReference the name of the function being called
	 * @param lo the lo value of the vector, the starting point
	 * @param hi the hi value of the vector, the ending point
	 * @param inc the increment to use for the domain
	 * @return the text of the request
	 */
	public static String buildCall
		(
			String functionReference,
			ComplexValue<Double> lo,
			ComplexValue<Double> hi,
			ComplexValue<Double> inc
		)
	{
		StringBuffer buf = new StringBuffer ().append ("getFtuples ( ")
				.append (functionReference).append (", ");
		add (lo, buf).append (", "); add (inc, buf).append (", ");
		buf.append (count (lo, hi, inc)).append (" )");
		return buf.toString ();
	}


	/**
	 * parse Python response expecting tuples.
	 *  each pair is interpreted as a R/I complex.
	 * @param access the RPC access object for the call
	 * @param request the text of the request for the call
	 * @return the list of values returned
	 */
	public static List<ComplexValue<Double>>
		getValues (ServerAccess access, String request)
	{
		List<ComplexValue<Double>> values = new ArrayList<ComplexValue<Double>> ();
		try { complexTuplesParser.convert (access.getSocketSource (request), values); }
		catch (Exception e) { throw new ServerAccess.ServerError ("Python server error", e); }
		return values;
	}


	/**
	 * simple RPC call requesting single parameter evaluation
	 * @param request the text of the request being made using RPC
	 * @param access the communication object for RPC calls
	 * @return only interested is first value from list
	 */
	public static ComplexMarker requestEval (String request, ServerAccess access)
	{ return getValues (access, request).get (0); }


	/**
	 * request access to the server,
	 * 	pass in the function text, process the returned content
	 * @param access the communication object for RPC calls
	 * @param functionText the text of the function call
	 * @param z the parameter to the function
	 * @return the computed result
	 */
	public static ComplexMarker requestEvalFor
	(ServerAccess access, String functionText, ComplexValue<Double> z)
	{
		String request =
			buildCall (functionText, z, null, null);
		return requestEval (request, access);
	}


}

