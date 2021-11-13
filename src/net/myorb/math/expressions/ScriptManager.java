
package net.myorb.math.expressions;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.DisplayConsole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class ScriptManager<T>
{


	public static class AssertionStatus extends Exception
	{
		public AssertionStatus (String message) { super (message); }
		public static final long serialVersionUID = 1233l;
	}


	public static class Script extends ArrayList<String>
	{ public static final long serialVersionUID = 1234l; }


	public ScriptManager (EvaluationControlI<T> control, Environment<T> environment)
	{
		this.scripts = new HashMap<String,Script>();
		this.environment = environment;
		this.control = control;
	}
	protected HashMap<String,Script> scripts;
	protected EvaluationControlI<T> control;
	protected Environment<T> environment;


	public void makeAssertion (String name, String expression)
	{
		throw new RuntimeException ("Assertion " + name + " has been validated, " + expression);
	}


	public void conditionallyAssert
		(String name, List<TokenParser.TokenDescriptor> tokens)
	{ if (isAssertionValidated ()) makeAssertion (name, TokenParser.toString (tokens)); }
	boolean isAssertionValidated () { return !environment.getSpaceManager ().isZero (getTop ()); }
	T getTop () { return environment.getValueManager ().toDiscrete (environment.getValueStack ().pop ()); }


	public Script read (String filename)
	{
		try
		{
			String line;

			Script script = new Script ();
			File f = new File ("scripts/" + filename);
			environment.getOutStream ().println ("Reading... " + f.getAbsolutePath ());

			BufferedReader reader = new BufferedReader (new FileReader (f));
			while ((line = reader.readLine ()) != null) script.add (line);

			reader.close ();
			return script;
		}
		catch (Exception e)
		{
			throw new RuntimeException ("File reader failed");
		}
	}


	public Script find (String filename)
	{
		if (scripts.containsKey (filename))
			return scripts.get (filename);
		else
		{
			Script script = read (filename);
			scripts.put (filename, script);
			return script;
		}
	}


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


	public void print (String filename)
	{
		for (String line : find (filename))
		{ environment.getOutStream ().println (line); }
	}


	/**
	 * read saved symbols and functions from a file
	 * @param filename the path of the file to be run
	 */
	public void readAndExecute (String filename)
	{
		execute (find (filename), true);
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


}

