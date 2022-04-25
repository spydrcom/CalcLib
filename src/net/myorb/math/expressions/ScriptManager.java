
package net.myorb.math.expressions;

import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.gui.DisplayConsole;
import net.myorb.math.expressions.gui.DisplayFiles;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.abstractions.ZipUtilities;
import net.myorb.data.abstractions.ErrorHandling.Terminator;
import net.myorb.data.abstractions.ZipRecord;
import net.myorb.data.abstractions.ErrorHandling;
import net.myorb.data.abstractions.FileSource;
import net.myorb.data.abstractions.ZipSource;
import net.myorb.gui.components.FileDrop;

import java.awt.Component;
import java.io.PrintStream;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a manager for script content
 * @param <T> the type of environment
 * @author Michael Druckman
 */
public class ScriptManager<T> implements FileDrop.FileProcessor
{


	public static class AssertionStatus extends Exception
	{
		public AssertionStatus (String message) { super (message); }
		private static final long serialVersionUID = -8947372484209464364L;
	}


	/**
	 * a container for script content
	 */
	public static class Script extends ArrayList<String>
		implements SimpleStreamIO.TextLineSink
	{

		public Script (ZipSource zip) throws Exception
		{
			this ( (FileSource) zip );
			ZipRecord.Properties p = zip.getEntryProperties ();
			this.identity = p.getName ();
		}
		public Script (FileSource source) throws Exception { this (source.getTextSource ()); }
		public Script (SimpleStreamIO.TextSource source) throws Exception { read (source); }

		/**
		 * @param source a stream source for the script content
		 * @throws Exception for any errors
		 */
		public void read
		(SimpleStreamIO.TextSource source) throws Exception
		{ SimpleStreamIO.copyTo (this, source); }

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleStreamIO.TextLineSink#putLine(java.lang.String)
		 */
		public void putLine (String line) { if (identifiesTip (line)) { tip = line; } this.add (line); }
		public boolean identifiesTip (String line) { return line.startsWith (OperatorNomenclature.TIP_PREFIX); }
		protected String tip = null;

		/* (non-Javadoc)
		 * @see java.util.AbstractCollection#toString()
		 */
		public String toString () { return identity; }
		public Script identifyAs (String name) { identity = name; return this; }
		protected String identity = null;

		private static final long serialVersionUID = -6056647894328237933L;
	}


	public ScriptManager (EvaluationControlI<T> control, Environment<T> environment)
	{
		this.control = control;
		this.scripts = new HashMap<String,Script>();
		this.environment = environment;
	}
	protected EvaluationControlI<T> control;
	protected Environment<T> environment;


	/**
	 * @return a sorted list of the script names
	 */
	public List<String> getScriptNames ()
	{
		ArrayList<String> names = new ArrayList<String> ();
		names.addAll (scripts.keySet ());
		names.sort (null);
		return names;
	}


	/**
	 * @return a sorted list of the scripts cached
	 */
	public List<Script> getScriptCache ()
	{
		ArrayList<Script> cache = new ArrayList<Script> ();
		for (String name : getScriptNames ())
		{ cache.add (scripts.get (name)); }
		return cache;
	}
	protected HashMap<String,Script> scripts;


	/**
	 * show names of scripts in cache
	 */
	public void displayScriptCache ()
	{
		PrintStream out = environment.getOutStream ();

		out.println ();
		out.println ("===");

		for (Script s : getScriptCache ())
		{
			out.print ("'");
			out.print (s);
			out.print ("'");

			if (s.tip != null)
			{
				out.print (" - ");
				out.print (s.tip);
			}

			out.println ();
		}

		out.println ("===");
		out.println ();
	}


	public void makeAssertion (String name, String expression)
	{
		throw new RuntimeException ("Assertion " + name + " has been validated, " + expression);
	}


	public void conditionallyAssert
		(String name, List<TokenParser.TokenDescriptor> tokens)
	{ if (isAssertionValidated ()) makeAssertion (name, TokenParser.toString (tokens)); }
	boolean isAssertionValidated () { return !environment.getSpaceManager ().isZero (getTop ()); }
	T getTop () { return environment.getValueManager ().toDiscrete (environment.getValueStack ().pop ()); }


	/**
	 * @param zip a ZIP source object
	 * @throws Exception for any errors
	 */
	public void readSource (ZipSource zip) throws Exception
	{
		Script script; notify (script = new Script (zip));
		scripts.put (script.toString (), script);
	}


	/**
	 * @param f file identified as ZIP source
	 * @throws Exception for any errors
	 */
	public void readZip (File f) throws Exception
	{
		Map <String, Integer> index = ZipUtilities.index (f);
		for (String k : index.keySet ()) readSource (new ZipSource (f, index.get (k)));
	}


	/**
	 * @param f file containing content
	 * @return a script object holding the content
	 * @throws Exception for any errors
	 */
	public Script importContent (File f) throws Exception
	{
		String name = f.getName ();
		FileSource source = new FileSource (f);
		Script script = new Script (source).identifyAs (name);
		scripts.put (name, script); notify (script);
		return script;
	}


	/**
	 * @param f a file to read as a script source
	 * @return the script read or NULL if no script generated
	 */
	public Script read (File f)
	{
		try
		{
			if (f.isDirectory ())
			{ for (File item : f.listFiles ()) importContent (item); }
			else if ( ! f.getName ().toLowerCase ().endsWith (".zip") )
			{ return importContent (f); }
			else { readZip (f); }
			return null;
		}
		catch (Exception e)
		{
			throw new ErrorHandling.Terminator ("File reader failed", e);
		}
	}


	/**
	 * @param filename name of the file in the scripts list
	 * @return the script read or NULL if no script generated
	 */
	public Script read (String filename)
	{
		File f = new File ("scripts/" + filename);
		notify ("Reading... ", f.getAbsolutePath ());
		Script script = read (f);
		displayScriptCache ();
		return script;
	}


	/**
	 * @param filename source file name of the script
	 * @return the script found in cache
	 */
	public Script find (String filename)
	{
		if (scripts.containsKey (filename))
			return scripts.get (filename);
		else return read (filename);
	}


	/**
	 * @param script the script object to be executed
	 * @param processWithCheck TRUE implies errors should propagate when found
	 */
	public void execute (Script script, boolean processWithCheck)
	{
		for (String line : script)
		{
			if (line.length () > 0)
			{
				control.execute (line, processWithCheck);
			}
		}
	}


	public void readAndIterate (String filename, int maxIterations) // throws AssertionStatus
	{
		Script script = find (filename);

		if (script == null)
		{
			throw new RuntimeException ("Unable to process script");
		}

		
		for (int i = 1; i <= maxIterations; i++)
		{
			try { execute (script, false); }
			catch (Exception e)
			{
				notification (i, e.getMessage (), e);
				throw new RuntimeException ("Script has terminated");
			}
		}

		notification ("Iteration " + maxIterations + " has completed");
		throw new RuntimeException ("Maximum iteration count exceeded");
	}
	void notification (int i, String message, Exception e)
	{
		notification
		("Script interrupted in iteration " + i);
		if (message != null) environment.getOutStream ().println (message);
		else environment.getOutStream ().println ("Exception '" + e.getClass ().getName () + "' was caught");
	}
	void notification (String message)
	{
		environment.getOutStream ().println ();
		environment.getOutStream ().println (message);
	}


	/**
	 * @param filename source file name of the script
	 */
	public void print (String filename)
	{
		Script script; if ((script = find (filename)) == null) return;
		for (String line : script) { environment.getOutStream ().println (line); }
	}


	/**
	 * read saved symbols and functions from a file
	 * @param filename the path of the file to be run
	 */
	public void readAndExecute (String filename)
	{
		execute (find (filename), environment.getOutStream ());
	}
	public void execute (Script script, PrintStream out)
	{
		if (script != null)
		{
			ErrorHandling.process
			(
				new ErrorHandling.Executable ()
				{
					public void process () throws Terminator
					{
						execute (script, true);
					}
				},
				out
			);
		}
	}


	public ScriptManager<T> forkedScriptManager (String filename)
	{
		Environment<T> newEnvironment = new Environment<T> (environment);
		newEnvironment.setOutStream (DisplayConsole.showConsole (filename, control.getGuiMap (), 700));
		EvaluationControl<T> newControl = new EvaluationControl<T> (newEnvironment, control.getSymbolTableManager ());
		return new ScriptManager<T>(newControl, newEnvironment);
	}


	/**
	 * run in background thread
	 * @param filename the path of the file to be run
	 */
	public void readAndExecuteBG (final String filename)
	{
		new Thread
		(
			new Runnable ()
			{
				public void run ()
				{
					forkedScriptManager (filename).readAndExecute (filename);
					environment.getOutStream ().println ("Script execution of " + filename + " has completed");
				}
			}
		).start ();
	}


	/**
	 * add to symbol table parent chain
	 * @param filename the path of the file to be run
	 */
	public void readSymbols (String filename)
	{
		Environment<T> env;
		SymbolMap newParent = new SymbolMap (), currentMap;
		newParent.putAll (currentMap = environment.getSymbolMap ());
		EvaluationControl<T> ctl = new EvaluationControl<T>
			(
				env = new Environment<T> (newParent, environment.getSpaceManager ()),
				control.getSymbolTableManager ()
			);
		new ScriptManager<T>(ctl, env).readAndExecute (filename);
		newParent.setParent (currentMap.getParent ());
		currentMap.setParent (newParent);
	}


	/**
	 * @param file a dropped file to use as script source
	 */
	public void process (File file)
	{
		notify ("Reading... ", file.getAbsolutePath ());
		read (file);
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.components.FileDrop.FileProcessor#process(java.util.List)
	 */
	public void process (List<File> files)
	{
		for (File f : files) process (f);
		DisplayFiles.showScriptCache (control.getGuiMap ());
		displayScriptCache ();
	}


	/**
	 * @param component a component to watch for dropped files
	 */
	public void connectFileDrop (Component component)
	{ fileDrop = new FileDrop (component, this); }
	protected FileDrop fileDrop;


	public void notify (String action, String item)
	{ environment.getOutStream ().println (action + item); }
	public void notify (Script item) { notify ("Imported ", item.toString ()); }
	public void notify (File item) { notify ("Imported ", item.getAbsolutePath ()); }


}

