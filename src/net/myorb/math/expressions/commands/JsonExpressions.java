
package net.myorb.math.expressions.commands;

import net.myorb.math.expressions.tree.Gardener;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.expressions.symbols.FunctionWrapper;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.gui.components.SimpleFileDirectoryList;
import net.myorb.gui.components.MenuItem;

import java.io.File;

/**
 * support for commands handling JSON expressions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class JsonExpressions <T> extends Utilities <T>
{


	public JsonExpressions
	(Environment <T> environment)
	{ super (environment); }


	/**
	 * show ZIP utility for parameterless LOADJSON command
	 * @throws Exception for any errors
	 */
	public void showData () throws Exception
	{
		SimpleFileDirectoryList list;
		File directory = new File ("expressions");
		(list = new SimpleFileDirectoryList ()).show (directory.getAbsolutePath ());
		SimpleFileDirectoryList.DirectoryTable table = (SimpleFileDirectoryList.DirectoryTable) list.getTable ();
		table.addMenuItem (new XprLoad (table, environment.getSymbolMap (), environment.getSpaceManager ()));
		list.setEntriesFrom (directory, null);
		list.getFrame ().done ();
	}


	/**
	 * menu item for ZIP utility.
	 *  multiple expression files can be loaded with single selection
	 */
	public class XprLoad extends MenuItem
	{
		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimpleCallback.Adapter#executeAction()
		 */
		public void executeAction () throws Exception
		{
			for (File json : table.getSelectedSourceFiles ())
			{ Gardener.loadFromJson (json, environment); }
		}

		XprLoad
			(
				SimpleFileDirectoryList.DirectoryTable table, SymbolMap symbols,
				ExpressionSpaceManager<T> spaceManager
			)
		{
			super ("Load As Expression");
			this.table = table; this.symbols = symbols;
			this.spaceManager = spaceManager;
		}
		SymbolMap symbols; ExpressionSpaceManager<T> spaceManager;
		SimpleFileDirectoryList.DirectoryTable table;
	}


	/**
	 * @param tokens the command tokens used to identify source
	 */
	public void loadJson (TokenParser.TokenSequence tokens)
	{
		try
		{
			if (tokens.size () == 1)
			{
				showData ();
			}
			else if (tokens.size () > 2 && Gardener.isForest (tokens.get (tokens.size () - 1).getTokenImage ()))
			{
				Gardener.Associates<T> participants = Gardener.loadFromZip
					(
						tokens.get (1).getTokenImage (), environment
					);
				participants.displayTo (environment.getOutStream (), environment.getSymbolMap ());
			}
			else
			{
				Gardener.loadFromJson (getFunctionName (tokens), environment);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Error loading expression", e);
		}
	}


	/**
	 * @param tokens the command tokens used to identify source
	 */
	public void loadSpline (TokenParser.TokenSequence tokens)
	{
		try { Gardener.loadJsonSpline (getFunctionName (tokens), environment); }
		catch (Exception e) { throw new RuntimeException ("Error loading spline", e); }
	}


	/**
	 * @param functionSymbol the symbol to save as JSON tree
	 */
	public void saveJson (String functionSymbol)
	{
		Subroutine<T> s =
			Subroutine.cast (environment.getSymbolMap ().get (functionSymbol));
		if (s == null) throw new RuntimeException ("Symbol is not a user defined function");
		if (s instanceof FunctionWrapper) { ((FunctionWrapper <T>) s).saveAsJson (); return; }
		try { s.saveExpressionTree (functionSymbol); } catch (Exception e) { e.printStackTrace (); }
	}


}

