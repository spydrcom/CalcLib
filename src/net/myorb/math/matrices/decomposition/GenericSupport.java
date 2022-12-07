
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.*;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.ArithmeticOperations;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.*;

import java.util.ArrayList;
import java.util.List;

/**
 * support for matrix operations implementing linear algebra algorithms
 * - specific to data represented in CalcLib generic structures
 * - includes JSON representation of decomposed matrix data
 * @param <T> data type used in Matrix objects
 * @author Michael Druckman
 */
public class GenericSupport <T> extends CellSequencePrimitives
{


	/*
	 * JSON parser bindings
	 */


	/**
	 * @param called name of the member
	 * @return the vector read from JSON
	 */
	public Vector <T> getVector (String called)
	{ return vecOps.fromJson (JsonTools.getArrayFrom (parsedObject, called)); }
	protected VectorOperations <T> vecOps;


	/**
	 * @param called name of the member
	 * @return the matrix read from JSON
	 */
	public Matrix <T> getMatrix (String called)
	{ return matOps.fromJson (JsonTools.getArrayFrom (parsedObject, called)); }
	protected MatrixOperations <T> matOps;


	/**
	 * @param called name of the member
	 * @return the numbers read from JSON
	 */
	public Number [] getIndex (String called)
	{
		return JsonTools.toNumberArray (parsedObject.getMemberCalled (called));
	}


	/**
	 * @return the class path to the controlling solution
	 */
	public String getSolutionClassPath ()
	{
		return solutionClassPath;
	}


	/**
	 * @param called name of the member
	 * @return the number read from JSON
	 */
	public Number getValue (String called)
	{
		return JsonTools.getNumberFrom (parsedObject, called).getNumber ();
	}


	/**
	 * @param source a text source to read in as a decomposed matrix
	 */
	public void parseDecomposedMatrix (SimpleStreamIO.TextSource source)
	{
		try { this.identifySource (JsonReader.readFrom (source)); }
		catch (Exception e) { throw new RuntimeException ("Error reading source", e); }
	}
	public void identifySource (JsonSemantics.JsonValue source)
	{
		this.parsedObject = (JsonSemantics.JsonObject) (this.parsedSource = source);
	}
	public void dump () 
	{
		try { JsonPrettyPrinter.sendTo (parsedSource, System.out); }
		catch (Exception e) { throw new RuntimeException ("Error formatting source", e); }
	}
	protected JsonSemantics.JsonObject parsedObject;
	protected JsonSemantics.JsonValue parsedSource;


	/*
	 * JSON representation bindings for decomposed matrix structures
	 */


	/**
	 * @param JSON tree of JSON nodes representing decomposed matrix
	 * @param to the sink that will store the content
	 */
	public void storeDecomposition (JsonValue JSON, SimpleStreamIO.TextSink to)
	{
		try { JsonPrettyPrinter.sinkTo (JSON, to); }
		catch (Exception e) { throw new RuntimeException ("JSON transport error"); }
	}


	/**
	 * add matrix JSON representation to master object
	 * @param object the JSON representation being built
	 * @param name the name for the member
	 * @param m matrix to be added
	 */
	public void addTo (JsonSemantics.JsonObject object, String name, Matrix <T> m)
	{
		object.addMemberNamed (name, matOps.toJson (m));
	}


	/**
	 * add vector JSON representation to master object
	 * @param object the JSON representation being built
	 * @param name the name for the member
	 * @param v vector to be added
	 */
	public void addTo (JsonSemantics.JsonObject object, String name, VectorAccess <T> v)
	{
		object.addMemberNamed (name, vecOps.toJson (v));
	}


	/**
	 * add vector JSON representation to master object
	 * @param object the JSON representation being built
	 * @param name the name for the member
	 * @param v vector to be added
	 */
	public void addTo (JsonSemantics.JsonObject object, String name, int [] v)
	{
		JsonSemantics.JsonArray array = new JsonSemantics.JsonArray ();
		for (int i = 0; i < v.length; i++) array.add (new JsonSemantics.JsonNumber (v[i]));
		object.addMemberNamed (name, array);
	}


	/**
	 * add numeric value JSON representation to master object
	 * @param object the JSON representation being built
	 * @param name the name for the member
	 * @param value number to be added
	 */
	public void addTo (JsonSemantics.JsonObject object, String name, Number value)
	{
		object.addMemberNamed (name, new JsonSemantics.JsonNumber (value));
	}


	/**
	 * describe arrays of values for JSON
	 * @param items an array of integer values
	 * @return list of text of values
	 */
	public static List <Integer> toList (int [] items)
	{
		return toList (items, 1);
	}
	public static List <Integer> toList (int [] items, int starting)
	{
		List <Integer> list = new ArrayList <Integer> ();
		for (int item = starting; item < items.length; item++)
		{ list.add (items[item]); }
		return list;
	}


	/*
	 * common methods for linear algebra algorithms
	 */


	/**
	 * @param a the matrix being evaluated
	 * @return vector of values giving scale (1/max) of each row
	 */
	public Vector <T> scaling (Matrix <T> a)
	{
		Vector <T> v;

		int n = a.rowCount ();
		v = new Vector <> (n, mgr);

		for (int i = 1; i <= n; i++)
		{
			v.set (i, mgr.invert (biggestOf (i, a)));
		}

		return v;
	}


	/**
	 * find largest value in row
	 * @param row the number of the row
	 * @param in the matrix being evaluated
	 * @return largest value in row
	 */
	public T biggestOf (int row, Matrix <T> in)
	{
		int n = in.columnCount ();
		T biggest = mgr.getZero (), temp;

		for (int col = 1; col <= n; col++)
		{
			temp = arithmetic.abs (in.get (row, col));
			if ( mgr.lessThan (biggest, temp) ) biggest = temp;
		}

		return check (biggest);
	}


	/**
	 * @param value the value to check
	 * @return the value that was checked
	 * @throws RuntimeException for zero value indicating det==0
	 */
	public T check (T value) throws RuntimeException
	{
		if (mgr.isZero (value))
			throw new RuntimeException ("Singular matrix");
		else return value;
	}


	/**
	 * dot product of vector segment
	 * @param A first vector for product
	 * @param B second vector for product
	 * @param start starting index for segment
	 * @param end ending index for segment
	 * @return the computed product
	 */
	public T dot
		(
			VectorAccess <T> A,
			VectorAccess <T> B,
			int start, int end
		)
	{
		T sum = mgr.getZero ();
		for (int k = start; k <= end; k++)
		{ sum = mgr.add (sum, mgr.multiply (A.get (k), B.get (k))); }
		return sum;
	}


	/**
	 * @param row the row of focus cell
	 * @param col the column of focus cell
	 * @param upTo the highest index in product
	 * @param A the matrix being evaluated
	 * @return cell minus rowXcol product
	 */
	public T reduceByProduct
		(
			int row, int col, int upTo,
			Matrix <T> A
		)
	{
		T product = dot
			(A.getRowAccess (row), A.getColAccess (col), 1, upTo-1);
		return reduceBy (A, row, col, product, mgr);
	}


	/**
	 * identify the biggest remaining row
	 * @param starting the starting row and column
	 * @param in matrix being searched
	 * @return biggest row found
	 */
	public int maxRow
		(
			int starting, Matrix <T> in
		)
	{
		T maxA = mgr.getZero (), absA;
		int ending = in.getEdgeCount (), imax = starting;
		
	    for (int k = starting; k <= ending; k++)
	    {
	    	absA = arithmetic.abs (in.get (k, starting));
	    	if (mgr.lessThan (maxA, absA))
	    	{ maxA = absA; imax = k; }
	    }
	
	    check (maxA);
	    return imax;
	}


	/**
	 * identify controlling solution class
	 * @param path the class path to the controlling solution
	 */
	public void setSolutionClassPath (String path)
	{
		solutionClassPath = path;
	}
	public String solutionClassPath = null;

	public void setSolutionClassPath ()
	{
		setSolutionClassPath (this.getClass ().getCanonicalName ());
	}

	public GenericSupport (ExpressionSpaceManager <T> mgr)
	{
		this.arithmetic = new ArithmeticOperations <T> (mgr);
		this.matOps = new MatrixOperations <T> (mgr);
		this.vecOps = matOps.getVectorOperations ();
		this.mgr = mgr;
	}
	protected ArithmeticOperations <T> arithmetic;
	protected ExpressionSpaceManager <T> mgr;


}

