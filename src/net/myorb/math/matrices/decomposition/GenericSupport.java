
package net.myorb.math.matrices.decomposition;

import net.myorb.math.matrices.*;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.ArithmeticOperations;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.notations.json.*;

import java.io.StringReader;
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
	{
		Vector <T> v;
		JsonSemantics.JsonArray a =
			JsonTools.getArrayFrom (parsedObject, called);
		copy (a, v = new Vector <T> (a.size (), mgr));
		return v;
	}


	/**
	 * @param a the JSON array
	 * @param v the vector to write to
	 */
	public void copy (JsonSemantics.JsonValue a, VectorAccess <T> v)
	{
		int i = 1;
		for (Number n : JsonTools.toNumberArray (a))
		{ v.set (i++, mgr.convertFromDouble (n.doubleValue ())); }
	}


	/**
	 * @param called name of the member
	 * @return the matrix read from JSON
	 */
	public Matrix <T> getMatrix (String called)
	{
		JsonSemantics.JsonArray rows =
			JsonTools.getArrayFrom (parsedObject, called);
		int cols = JsonTools.toArray (rows.get (0)).size ();
		Matrix <T> m = new Matrix <T> (rows.size (), cols, mgr);

		for (int r = 1; r <= rows.size (); r++)
		{ copy (rows.get (r-1), m.getRowAccess (r)); }
		return m;
	}


	/**
	 * @param called name of the member
	 * @return the numbers read from JSON
	 */
	public Number [] getIndex (String called)
	{
		return JsonTools.toNumberArray (parsedObject.getMemberCalled (called));
	}


	/**
	 * @param numbers an array of numbers
	 * @return a zero index based integer array
	 */
	public int [] toArray (Number [] numbers)
	{
		int ints [] = new int [numbers.length+1], i = 1;
		for (Number n : numbers) ints [i++] = n.intValue ();
		return ints;
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
		this.parsedSource = source;
		this.parsedObject = (JsonSemantics.JsonObject) parsedSource;
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
	 * @param text the text describing the decomposed matrix
	 * @param to the sink that will store the content
	 */
	public void storeDecomposition (String text, SimpleStreamIO.TextSink to)
	{
		SimpleStreamIO.TextSource source =
			new SimpleStreamIO.TextSource (new StringReader (text));
		try { SimpleStreamIO.processTextStream (source, to); }
		catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * identify solution being described
	 * @param buffer a string buffer being built
	 * @return the string buffer
	 */
	public StringBuffer addPathTo (StringBuffer buffer)
	{
		String path = getSolutionClassPath ();
		if (path != null)
		{
			buffer.append ("\n  \"Solution\" : \"")
			.append (path).append ("\",");
		}
		return buffer;
	}

	/**
	 * @param buffer a string buffer being built
	 * @param name the name for the member
	 * @param v vector  to be added
	 * @return the string buffer
	 */
	public StringBuffer addTo (StringBuffer buffer, String name, VectorAccess <T> v)
	{
		buffer.append ("\n  \"")
			.append (name).append ("\" : ")
			.append (toList (v));
		return buffer;
	}


	/**
	 * @param buffer a string buffer being built
	 * @param name the name for the member
	 * @param m matrix to be added
	 * @return the string buffer
	 */
	public StringBuffer addTo (StringBuffer buffer, String name, Matrix <T> m)
	{
		buffer.append ("\n  \"")
			.append (name).append ("\" : [");
		for (int i = 1; i < m.rowCount (); i++)
		{ buffer.append ("\n\t").append (toList (m.getRowAccess (i))).append (","); }
		buffer.append ("\n\t").append (toList (m.getRowAccess (m.rowCount ())))
			.append ("\n  ]");
		return buffer;
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
	{ this.mgr = mgr; this.arithmetic = new ArithmeticOperations <T> (mgr); }
	protected ArithmeticOperations <T> arithmetic;
	protected ExpressionSpaceManager <T> mgr;


}

