
package net.myorb.math.expressions;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.matrices.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.FileReader;
import java.io.File;

import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * provide data import mechanisms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class DataIO<T>
{


	public DataIO (Environment<T> environment)
	{
		this.spaceManager =
			environment.getSpaceManager ();
		this.environment = environment;
	}
	protected ExpressionSpaceManager<T> spaceManager;
	protected Environment<T> environment;


	/**
	 * write matrix to file
	 * @param filepath the path to the file
	 * @param m the matrix to write
	 */
	public void write (String filepath, Matrix<T> m)
	{
		try
		{
			File destination = new File ("data/" + filepath);
			FileWriter writer = new FileWriter (destination);

			for (int r = 1; r <= m.rowCount(); r++)
			{
				StringBuffer buffer = new StringBuffer ();
				for (int c = 1; c <= m.columnCount(); c++)
				{
					buffer.append (m.get (r, c));
					buffer.append ("\t");
				}
				buffer.append ("\r\n");
				writer.write (buffer.toString ());
			}

			writer.close ();
		}
		catch (Exception e)
		{
			throw new RuntimeException ("File writer failed");
		}
	}


	/**
	 * read file to matrix
	 * @param filepath location of the file to read
	 * @param matrixName the name of the symbol that will hold the data
	 */
	public void read (String filepath, String matrixName)
	{
		File source = new File ("data/" + filepath);
		PrintStream out = environment.getOutStream ();
		out.println ("Import from file:  " + source.getAbsolutePath ());
		out.println ("Import to matrix:  " + matrixName);

		Matrix<T> data = read (source);
		out.println (" Columns per row:  " + data.columnCount ());
		out.println ("       Rows read:  " + data.rowCount ());

		environment.setSymbol (matrixName, new ValueManager<T>().newMatrix (data));

		out.println ();
		out.println ("data read:");
		new MatrixOperations<T> (spaceManager).show (out, data);
		out.println ("=EOD=");
		out.println ();
	}


	/**
	 * read data from tab delimited file
	 * @param f the file to be read
	 * @return the matrix read
	 */
	@SuppressWarnings("resource")
	public Matrix<T> read (File f)
	{
		String line; int rows = 1, cols;
		ArrayList<T> list = new ArrayList<T>();
		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader (new FileReader (f));
			if ((line = reader.readLine ()) == null)
			{ throw new RuntimeException ("File is empty"); }
			else cols = parse (line, list);

			int c;
			while ((line = reader.readLine ()) != null)
			{
				if ((c = parse (line, list)) == 0) continue;
				if (c != cols) throw new RuntimeException ("Inconsistant row size");
				rows++;
			}
			reader.close ();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try { reader.close (); } catch (IOException x) { }
			throw new RuntimeException ("File reader failed");
		}
		
		return new Matrix<T> (rows, cols, list, spaceManager);
	}


	/**
	 * parse column value from a text line
	 * @param line the line of text to be parsed
	 * @param list the list collecting the values
	 * @return the number of columns parsed
	 */
	public int parse (String line, ArrayList<T> list)
	{
		int n = 0;
		if (line.length () == 0) return 0;
		StringTokenizer t = new StringTokenizer (line, "\t");
		while (t.hasMoreTokens ())
		{
			list.add (parse (t.nextToken ()));
			n++;
		}
		return n;
	}
	public T parse (String text)
	{
		return spaceManager.evaluate (text);
	}


}

