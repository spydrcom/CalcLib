
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.gui.DisplayIO.CommandProcessor;

import net.myorb.gui.components.MenuManager.ActionList;

import java.awt.Component;

/**
 * button menus in a tabbed pane to provide one-click functionality
 * @author Michael Druckman
 */
public class ToolBar extends ToolBarTabbedPanes
{


	/**
	 * factory for menu item lists
	 */
	public interface MenuFactory
	{
		/**
		 * build menu item list
		 * @param processor the master command processor object
		 * @param component the display item acted upon by menu
		 * @return the action list for the menu items
		 */
		ActionList getMenuItems (CommandProcessor processor, Component component);

		/**
		 * @return the title for this menu in application menu bar
		 */
		String getMenuBarTitle ();
	}


	/**
	 * construct menu item list for the script display table
	 * @param processor the master command processor object
	 * @param c the parent component for this menu
	 * @return the list of action items
	 */
	public static ActionList
		getScriptTableMenuItems (CommandProcessor processor, Component c)
	{
		ActionList list = new ActionList ();

		list.add (new FastExecuteCommand ("READ", "ScriptFiles", processor, true, c));
		list.add (new ExecuteCommand ("READ", "Read", "ScriptFiles", processor, true, c));
		list.add (new BackgroundSymbolsCommand ("RECOGNIZE", "ScriptFiles", processor, true, c));
		list.add (new CreateCommand ("NEW ", "ScriptFiles", processor, false, c));
		list.add (new EditCommand ("EDIT ", "ScriptFiles", processor, true, c));

		list.add (new ScriptFilesCommand (processor, c));
		list.add (new ActiveFilesCommand (processor, c));
		list.add (new CachedFilesCommand (processor, c));

		list.add (new IterateCommand (processor, c));
		list.add (new ForkCommand (processor, c));
		list.add (new PrintCommand (processor, c));
		list.add (new ShowCommand (processor));

		return list;
	}


	/**
	 * construct menu item list for the data display table
	 * @param processor the master command processor object
	 * @param c the parent component for this menu
	 * @return the list of action items
	 */
	public static ActionList
		getDataTableMenuItems (CommandProcessor processor, Component c)
	{
		ActionList list = new ActionList ();
		list.add (new ImportCommand ("IMPORT  ", "DataFiles", processor, c));
		list.add (new ShowCommand (processor));
		return list;
	}



	/**
	 * construct menu item list for the symbols display table
	 * @param processor the master command processor object
	 * @param c the parent component for this menu
	 * @return the list of action items
	 */
	public static ActionList getSymbolTableMenuItems (CommandProcessor processor, Component c)
	{
		ActionList list = new ActionList ();
		list.add (new AddSymbol (processor, c));
		list.add (new RenameCommand (processor, c));
		list.add (new PrettyPrintSymbol (processor, c));
		list.add (new PublishAs (processor, c));
		list.add (new PrepPolynomialDEQTest (processor, c));
		list.add (new ArrayDecl (processor, c));
		list.add (new IntervalArray (processor, c));
		list.add (new SummationEquation (processor, c));
		list.add (new ExportCommand (processor, c));
		list.add (new DropSymbol (processor, c));
		list.add (new ShowCommand (processor));
		return list;
	}


	/**
	 * construct menu item list for the functions display table
	 * @param processor the master command processor object
	 * @param c the parent component for this menu
	 * @return the list of action items
	 * @param <T> data type
	 */
	public static <T> ActionList getFunctionTableMenuItems (CommandProcessor processor, Component c)
	{
		ActionList list = new ActionList ();
		list.add (new AddFunction (processor, c));
		list.add (new DropFunction (processor, c)); list.add (new PrettyPrintFunction (processor, c));
		list.add (new RenderFunction (processor, c)); list.add (new SeriesDecl (processor, c)); list.add (new SetConstraints (processor, c));
		list.add (new StandardizeConstraints (processor, c));  list.add (new DCT (processor, c)); list.add (new SummationFunction (processor, c));
		list.add (new ExpressCommand ("EXPRESS  ", processor, c)); list.add (new EncodeCommand ("ENCODE  ", "FunctionTable", processor, c));
		list.add (new PrepareDiffEQAnalysisCommand (PREP, processor, c)); list.add (new RunDiffEQSolutionTest (RUN, processor, c)); 
		list.add (new ExpressSaveCommand ("SAVEJSON  ", processor, c)); list.add (new ExpressLoadCommand ("LOADJSON  ", processor, c));
		list.add (new AppliedDifferentiationRules (processor, c)); list.add (new AppliedIntegrationTransform (processor, c));
		list.add (new ApproximateIntegral<T> (processor, c)); list.add (new ApproximateSeries<T> (processor, c));
		list.add (new ApproximateRoot<T> (processor, c)); list.add (new SymbolPromotion (processor, c));
		list.add (new FunctionPlot (processor, c)); list.add (new SplineToolCommand (processor, c));
		list.add (new ShowCommand (processor));
		return list;
	}
	static final String PREP = "Prep DiffEQ Solution Test", RUN = "Run DiffEQ Solution Test";


	/**
	 * @return menu factory for Script table
	 */
	public static MenuFactory getScriptTableMenuFactory ()
	{
		return new MenuFactory ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.gui.ToolBar.MenuFactory#getMenuItems(net.myorb.math.expressions.gui.DisplayIO.CommandProcessor, java.awt.Component)
			 */
			public ActionList getMenuItems (CommandProcessor processor, Component c)
			{
				return getScriptTableMenuItems (processor, c);
			};

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.gui.ToolBar.MenuFactory#getMenuBarTitle()
			 */
			public String getMenuBarTitle () { return "Scripts"; }
		};
	}


	/**
	 * @return menu factory for Symbol table
	 */
	public static MenuFactory getSymbolTableMenuFactory ()
	{
		return new MenuFactory ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.gui.ToolBar.MenuFactory#getMenuItems(net.myorb.math.expressions.gui.DisplayIO.CommandProcessor, java.awt.Component)
			 */
			public ActionList getMenuItems (CommandProcessor processor, Component c)
			{
				return getSymbolTableMenuItems (processor, c);
			};

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.gui.ToolBar.MenuFactory#getMenuBarTitle()
			 */
			public String getMenuBarTitle () { return "Symbols"; }
		};
	}


	/**
	 * @return menu factory for Function table
	 */
	public static MenuFactory getFunctionTableMenuFactory ()
	{
		return new MenuFactory ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.gui.ToolBar.MenuFactory#getMenuItems(net.myorb.math.expressions.gui.DisplayIO.CommandProcessor, java.awt.Component)
			 */
			public ActionList getMenuItems (CommandProcessor processor, Component c)
			{
				return getFunctionTableMenuItems (processor, c);
			};

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.gui.ToolBar.MenuFactory#getMenuBarTitle()
			 */
			public String getMenuBarTitle () { return "Functions"; }
		};
	}


}

