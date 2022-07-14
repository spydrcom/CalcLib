
package net.myorb.math.expressions;

// keyword command support
import net.myorb.math.expressions.commands.*;

// charts and GUI display support
import net.myorb.math.expressions.gui.DisplayModeForm;
import net.myorb.math.expressions.charting.RegressionCharts;

import java.util.HashMap;

// IOlib GUI and Data support
import net.myorb.data.abstractions.SimplePropertiesManager;
import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.components.SideBySide;

/**
 * processing for keywords
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class KeywordMap<T> extends EnvironmentalUtilities<T>
	implements EnvironmentalUtilities.AccessToTopOfStack
{


	/**
	 * error check for command length
	 * @param count the expected count of parameters
	 * @param tokens the token sequence for the command
	 */
	public void expecting (int count, CommandSequence tokens)
	{
		if (tokens.size () <= count) throw new RuntimeException ("Command is short parameters, expecting " + count);
	}


	/**
	 * process a calculate command
	 * @return a keyword command for the calculate keyword
	 */
	public KeywordCommand constructCalculateKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Calculate and show value for an expression"; }

			public void execute (CommandSequence tokens)
			{
				getPrettyFormatter ().showValue (getValue (tokens));
			}
		};
	}


	/**
	 * format output with a specified radix
	 * @return a keyword command for the RADIX keyword
	 */
	public KeywordCommand constructRadixKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Calculate and show value for an expression in specified radix";
			}

			public void execute (CommandSequence tokens)
			{
				int radix = Integer.parseInt (getNextOperandImage (tokens));
				getPrettyFormatter ().showWithRadix (getValue (tokens), radix);
			}
		};
	}


	/**
	 * process a define command
	 * @return a keyword command for the define keyword
	 */
	public KeywordCommand constructDefineKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () 
			{ return "Define a user function"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().processFunctionDefinition (tokens); }
		};
	}


	/**
	 * process a HG Polynomial declaration command
	 * @return a keyword command for the define keyword
	 */
	public KeywordCommand constructHGPolynomialKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () 
			{ return "Declare a user function as a Hyper-Geometric polynomial"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().processFunctionDefinition (tokens); }
		};
	}


	/**
	 * process a transform command
	 * @return a keyword command for the define keyword
	 */
	public KeywordCommand constructTransformKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () 
			{ return "Define a function transform"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().processFunctionDefinition (tokens); }
		};
	}


	/**
	 * enable a function as an integration transform source
	 * @return a keyword command for the define keyword
	 */
	public KeywordCommand constructTransformEnableKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () 
			{ return "Define a function as an integration transform source"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().processFunctionDefinition (tokens); }
		};
	}


	/**
	 * process a lib import command
	 * @return a keyword command for the lib import prefix
	 */
	public KeywordCommand constructLibImportKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () 
			{ return "Define a user function as a library import"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().processFunctionDefinition (tokens); }
		};
	}


	/**
	 * process a segmented function definition
	 * @return a keyword command for the segmented function prefix
	 */
	public KeywordCommand constructSegmentedFunctionKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () 
			{ return "Define a segmented user function as a list of polynomials"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().processFunctionDefinition (tokens); }
		};
	}


	/**
	 * process a library command
	 * @return a keyword command for the LIBRARY keyword
	 */
	public KeywordCommand constructLibraryKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () 
			{ return "Construct a library of functions"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().getLibrarian ().processLibrary (tokens); }
		};
	}


	/**
	 * process a configure command
	 * @return a keyword command for the CONFIGURE keyword
	 */
	public KeywordCommand constructConfigureKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () 
			{ return "Configure a library of functions"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().getLibrarian ().configureLibrary (tokens); }
		};
	}


	/**
	 * process a family command
	 * @return a keyword command for the FAMILY keyword
	 */
	public KeywordCommand constructFamilyKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () 
			{ return "Import polynomial power functions for named family"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().getLibrarian ().importFamily (tokens); }
		};
	}


	/**
	 * process a GRAPH command
	 * @return a keyword command for the GRAPH keyword
	 */
	public KeywordCommand constructGraphKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Display a graph of an array of data points"; }

			public void execute (CommandSequence tokens)
			{ plotWithLimit (GraphManager.Types.ARRAY, tokens, getAccess ()); }
		};
	}


	/**
	 * process a CHART command
	 * @return a keyword command for the CHART keyword
	 */
	public KeywordCommand constructChartKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Display a chart for a set of symbols"; }

			public void execute (CommandSequence tokens)
			{
				CommandSequence
					oparands = withCommandRemoved (tokens);
				String kind = getCurrentOperandImage (oparands);
				getGraphManager ().chartFor (kind, oparands);
			}
		};
	}


	/**
	 * process a SPLOT command
	 * @return a keyword command for the SPLOT keyword
	 */
	public KeywordCommand constructSplotKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Display a graph of a complex number sequence"; }

			public void execute (CommandSequence tokens)
			{ plotWithLimit (GraphManager.Types.COMPLEX, tokens, getAccess ()); }
		};
	}


	/**
	 * process a DESCRIBE command
	 * @return a keyword command for the DESCRIBE keyword
	 */
	public KeywordCommand constructDescribeKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Add a description of a function to symbol table"; }

			public void execute (CommandSequence tokens)
			{ describeFunction (tokens); }
		};
	}


	/**
	 * process a ENTITLED command
	 * @return a keyword command for the ENTITLED keyword
	 */
	public KeywordCommand constructEntitledKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Change the title of the last frame displayed"; }

			public void execute (CommandSequence tokens)
			{ DisplayFrame.changeTitle (TokenParser.toString (withCommandRemoved (tokens))); }
		};
	}


	/**
	 * process a PLOTF command
	 * @return a keyword command for the PLOTF keyword
	 */
	public KeywordCommand constructPlotfKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Plot a specified range of a user defined function";
			}

			public void execute (CommandSequence tokens)
			{
				processLimit (tokens);
				getGraphManager ().singleFunctionPlot (tokens);
				resetLimit ();
			}
		};
	}


	/**
	 * process a PLOTT command
	 * @return a keyword command for the PLOTT keyword
	 */
	public KeywordCommand constructPlottKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Tabular plot with data taken from file";
			}

			public void execute (CommandSequence tokens)
			{
				CommandSequence rem = withCommandRemoved (tokens);
				String path = TokenParser.toFormatted (rem, false).replaceAll (" ", "");
				getGraphManager ().tabularPlotFromFile (path);
			}
		};
	}


	/**
	 * process a PLOTRI command
	 * @return a keyword command for the plot3d keyword
	 */
	public KeywordCommand constructPlotriKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Plot a specified range of a complex user defined function as separate plots of Real and Imaginary components";
			}

			public void execute (CommandSequence tokens)
			{
				processLimit (tokens);
				getGraphManager ().singleComplexFunctionPlot (tokens);
				resetLimit ();
			}
		};
	}


	/**
	 * process a PLOTC command
	 * @return a keyword command for the plotc keyword
	 */
	public KeywordCommand constructPlotcKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Plot a specified 2D domain with contours of a 3D user defined function";
			}

			public void execute (CommandSequence tokens)
			{
				CommandSequence 
					rem = withCommandRemoved (tokens);
				String function = getCurrentOperandImage (rem);
				getGraphManager ().singleContourFunctionPlot (function, rem);
			}
		};
	}


	/**
	 * process a PLOTM command
	 * @return a keyword command for the plotm keyword
	 */
	public KeywordCommand constructPlotmKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Display tabular plot with data taken from a matrix";
			}

			public void execute (CommandSequence tokens)
			{
				CommandSequence 
					rem = withCommandRemoved (tokens);
				String symbol = getCurrentOperandImage (rem);
				getGraphManager ().tabularFunctionPlot (symbol, rem);
			}
		};
	}


	/**
	 * process a PLOT3D command
	 * @return a keyword command for the plot3d keyword
	 */
	public KeywordCommand constructPlot3dKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Plot a specified 2D domain of a 3D user defined function";
			}

			public void execute (CommandSequence tokens)
			{
				processLimit (tokens);
				CommandSequence rem = withCommandRemoved (tokens);
				String function = getCurrentOperandImage (rem);
				getGraphManager ().single3DFunctionPlot (function, rem);
			}
		};
	}


	/**
	 * process a mandelbrot command
	 * @return a keyword command for the mandelbrot keyword
	 */
	public KeywordCommand constructMandelbrotKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Display a plot of the Mandelbrot set"; }

			public void execute (CommandSequence tokens)
			{ new MandelbrotGraphics<T>(environment).plot (tokens); }
		};
	}


	/**
	 * process a polynomial command
	 * @return a keyword command for the polynomial keyword
	 */
	public KeywordCommand constructPolynomialKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Analyze a polynomial and tabulate key points"; }

			public void execute (CommandSequence tokens)
			{ plot (GraphManager.Types.POLY_EVALUATION, tokens, getAccess ()); }
		};
	}


	/**
	 * process a derive command
	 * @return a keyword command for the derive keyword
	 */
	public KeywordCommand constructDeriveKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Plot derivatives of a polynomial"; }

			public void execute (CommandSequence tokens)
			{ plot (GraphManager.Types.POLY_DERIVATION, tokens, getAccess ()); }
		};
	}


	/**
	 * show the help table
	 * @return a keyword command for the help keyword
	 */
	public KeywordCommand constructHelpKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Show the HELP table"; }

			public void execute (CommandSequence tokens)
			{ help (); }
		};
	}


	/**
	 * show the JavaDocs in the browser
	 * @return a keyword command for the DOCS keyword
	 */
	public KeywordCommand constructDocsKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Show the JavaDocs for this release"; }

			public void execute (CommandSequence tokens)
			{ showJavaDocs (tokens); }
		};
	}


	/**
	 * verify symbol table entry
	 * @return a keyword command for the help keyword
	 */
	public KeywordCommand constructVerifyKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Verify symbol present in current symbol table"; }

			public void execute (CommandSequence tokens)
			{ engine.getSymbolMap ().verify (tokens.get (1).getTokenImage ()); }
		};
	}


	/**
	 * show a segment of the symbol table
	 * @return a keyword command for the show keyword
	 */
	public KeywordCommand constructShowKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Show symbol table contents Symbols|Functions|Parent|ALL parents";
			}

			public void execute (CommandSequence tokens)
			{
				SymbolMap s = engine.getSymbolMap ();
				String option = tokens.get (1).getTokenImage ();

				if (tokens.size() > 2)
				{
					int count = Integer.parseInt (tokens.get (2).getTokenImage ());
					for (int n = count; n > 0; n--)
					{
						if ((s = s.getParent ()) == null)
						{ throw new RuntimeException ("Too few parents"); }
					}
				}
				else if (option.toUpperCase ().startsWith ("P"))
				{ s = s.getParent (); }

				s.dump
				(
					option,
					environment.getSpaceManager (),
					environment.getConsoleWriter ()
				);
			}
		};
	}


	/**
	 * show the contents of a matrix
	 * @return a keyword command for the PRETTYPRINT keyword
	 */
	public KeywordCommand constructPrettyPrintKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Show the formatted value of a symbol"; }

			public void execute (CommandSequence tokens)
			{
				String precision = null;
				if (tokens.size() > 2) precision = tokens.get (2).getTokenImage ();
				new PrettyPrinter<T>(environment).formatSymbol (tokens.get (1).getTokenImage (), precision);
			}
		};
	}


	/**
	 * Format an array as a polynomial
	 * @return a keyword command for the POLYPRINT keyword
	 */
	public KeywordCommand constructPolyPrintKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Format an array as a polynomial";
			}

			public void execute (CommandSequence tokens)
			{
				new PrettyPrinter<T>(environment).formatPolynomial (tokens.get (1).getTokenImage ());
			}
		};
	}


	/**
	 * display an equation using MathML
	 * @return a keyword command for the RENDER keyword
	 */
	public KeywordCommand constructRenderKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Format and display (pretty print) an equation using MathML"; }

			public void execute (CommandSequence tokens)
			{ getCurrentRenderer ().RenderFrom (tokens); }
		};
	}


	/**
	 * select a render display to be shown and used for output
	 * @return a keyword command for the SELECT keyword
	 */
	public KeywordCommand constructSelectKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Select a render display to be shown and used for output of RENDER commands"; }

			public void execute (CommandSequence tokens) { setCurrentRenderer (withCommandRemoved (tokens)); }
		};
	}


	/**
	 * display a function using MathML
	 * @return a keyword command for the RENDERF keyword
	 */
	public KeywordCommand constructRenderFunctionKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Format and display (pretty print) a function using MathML"; }

			public void execute (CommandSequence tokens)
			{ getCurrentRenderer ().RenderFunction (tokens); }
		};
	}


	/**
	 * display a Differential Equation using MathML
	 * @return a keyword command for the RENDERF keyword
	 */
	public KeywordCommand constructRenderDiffEqKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Format and display (pretty print) a Differential Equation using MathML"; }

			public void execute (CommandSequence tokens)
			{ getCurrentRenderer ().RenderDifferentialEquation (tokens); }
		};
	}


	/**
	 * save symbols to a file
	 * @return a keyword command for the save keyword
	 */
	public KeywordCommand constructSaveKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Save a workspace storage file";
			}

			public void execute (CommandSequence tokens)
			{
				engine.getSymbolMap ().save
				(
					filename (tokens), environment.getSpaceManager (),
					engine.getDataIO (), environment.getOutStream ()
				);
			}
		};
	}


	/**
	 * read symbol definitions from a file
	 * @return a keyword command for the read keyword
	 */
	public KeywordCommand constructReadKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Read a workspace storage file"; }

			public void execute (CommandSequence tokens)
			{ engine.getScriptManager ().readAndExecute (filename (tokens)); }
		};
	}


	/**
	 * read symbol definitions from a file into parent table
	 * @return a keyword command for the read keyword
	 */
	public KeywordCommand constructRecognizeKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Read a symbol definition file"; }

			public void execute (CommandSequence tokens)
			{ engine.getScriptManager ().readSymbols (filename (tokens)); }
		};
	}


	/**
	 * read script from a file and iterate
	 * @return a keyword command for the ITERATE keyword
	 */
	public KeywordCommand constructIterateKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Read a script file and iterate";
			}

			public void execute (CommandSequence tokens)
			{
				int maxIterations = Integer.parseInt (getNextOperandImage (tokens));
				engine.getScriptManager ().readAndIterate (filename (tokens), maxIterations);
			}
		};
	}


	/**
	 * read script from a file and execute in background
	 * @return a keyword command for the BACKGROUND keyword
	 */
	public KeywordCommand constructBackgroundKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Read a script file and execute as a background task";
			}

			public void execute (CommandSequence tokens)
			{
				engine.getScriptManager ().readAndExecuteBG (filename (tokens));
			}
		};
	}


	/**
	 * make a conditional assertion
	 * @return a keyword command for the ASSERT keyword
	 */
	public KeywordCommand constructAssertKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () { return "Make a conditional assertion"; }

			public void execute (CommandSequence tokens)
			{
				String name = getNameTag (tokens); engine.process (tokens);
				engine.getScriptManager ().conditionallyAssert (name, tokens);
			}
		};
	}


	/**
	 * Display the contents of a script file
	 * @return a keyword command for the SCRIPTPRINT keyword
	 */
	public KeywordCommand constructScriptPrintKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Display the contents of a script file"; }

			public void execute (CommandSequence tokens)
			{ engine.getScriptManager ().print (filename (tokens)); }
		};
	}


	/**
	 * compute and display a fast Fourier transform
	 * @return a keyword command for the FFT keyword
	 */
	public KeywordCommand constructFftKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Compute and display a fast Fourier transform";
			}

			public void execute (CommandSequence tokens)
			{
				new RegressionCharts<T> (environment).transorm
				(
					tokens.get (1).getTokenImage ()
				);
			}
		};
	}


	/**
	 * produce an X/Y scatter plot
	 * @return a keyword command for the SCATTER keyword
	 */
	public KeywordCommand constructScatterKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Produce an X/Y scatter plot"; }

			public void execute (CommandSequence tokens)
			{
				new RegressionCharts<T> (environment).scatter
				(
					tokens.get (1).getTokenImage (), tokens.get (3).getTokenImage ()
				);
			}
		};
	}


	/**
	 * produce a side-by-side plot comparison
	 * @return a keyword command for the SIDEBYSIDE keyword
	 */
	public KeywordCommand constructComparisonKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Produce a side-by-side plot comparison"; }

			public void execute (CommandSequence tokens)
			{
				SideBySide.addToPanel (TokenParser.stripQuotes (tokens.get (1).getTokenImage ()));
			}
		};
	}


	/**
	 * import data from a file
	 * @return a keyword command for the IMPORT keyword
	 */
	public KeywordCommand constructImportKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{
				return "Import data from a file into specified matrix";
			}

			public void execute (CommandSequence tokens)
			{
				String matrixSymbol = getNextOperandImage (tokens);
				engine.getDataIO ().read (filename (tokens), matrixSymbol);
			}
		};
	}


	/**
	 * export data to a file
	 * @return a keyword command for the EXPORT keyword
	 */
	public KeywordCommand constructExportKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{
				engine.getDataIO ().write
				(
					filename (tokens),
					getMatrixFrom (getNextOperandImage (tokens))
				);
			}

			public String describe ()
			{
				return "Export data to a file from specified matrix";
			}
		};
	}


	/**
	 * generate Expression Tree for function
	 * @return a keyword command for the EXPRESS keyword
	 */
	public KeywordCommand constructExpressKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Enable Expression Tree generation for function"; }

			public void execute (CommandSequence tokens)
			{ allowExpressionTree (getFunctionName (tokens)); }
		};
	}


	/**
	 * save Expression Tree to JSON file
	 * @return a keyword command for the SAVEJSON keyword
	 */
	public KeywordCommand constructSaveJsonKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Save Expression Tree as JSON file"; }

			public void execute (CommandSequence tokens)
			{ new JsonExpressions<T>(environment).saveJson (getFunctionName (tokens)); }
		};
	}


	/**
	 * load Expression Tree(s) from JSON source(s)
	 * @return a keyword command for the LOADJSON keyword
	 */
	public KeywordCommand constructLoadJsonKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Load Expression Tree(s) from JSON source(s)"; }

			public void execute (CommandSequence tokens)
			{ new JsonExpressions<T>(environment).loadJson (tokens); }
		};
	}


	/**
	 * encode a segmented function
	 * @return a keyword command for the ENCODE keyword
	 */
	public KeywordCommand constructEncodeKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Encode a segmented function as a Java class"; }

			public void execute (CommandSequence tokens)
			{ encodeSpline (tokens); }
		};
	}


	/**
	 * open SPLINE tool
	 * @return a keyword command for the SPLINE keyword
	 */
	public KeywordCommand constructSplineKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Open anti-derivative spline tool for function"; }

			public void execute (CommandSequence tokens)
			{ splineToolInvocation (tokens); }
		};
	}


	/**
	 * optimize polynomial function use by embedding coefficients
	 * @return a keyword command for the OPTIMIZE keyword
	 */
	public KeywordCommand constructOptimizeKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Optimize polynomial function use by embedding constant coefficients"; }

			public void execute (CommandSequence tokens)
			{ engine.getFunctionManager ().optimizePolynomial (tokens); }
		};
	}


	/**
	 * prepare function as Diff EQ solution for test
	 * @return a keyword command for the PREPARE keyword
	 */
	public KeywordCommand constructPrepareKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Alias selected function and derivatives for Diff EQ solution test"; }

			public void execute (CommandSequence tokens)
			{ expecting (3, tokens); engine.getFunctionManager ().prepareDiffEqSolutionTest (tokens); }
		};
	}


	/**
	 * prepare polynomial as Diff EQ solution for test
	 * @return a keyword command for the PREPPOLY keyword
	 */
	public KeywordCommand constructPrepPolyKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Alias selected polynomial and derivatives for Diff EQ solution test"; }

			public void execute (CommandSequence tokens)
			{ expecting (2, tokens); engine.getFunctionManager ().preparePolynomialDiffEqSolutionTest (tokens); }
		};
	}


	/**
	 * Test Differential Equation Solution
	 * @return a keyword command for the TDES keyword
	 */
	public KeywordCommand constructTdesKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Run error test of differential equation solution"; }

			public void execute (CommandSequence tokens)
			{ environment.getDifferentialEquationsManager ().runTest (tokens, engine.getFunctionManager ()); }
		};
	}


	/**
	 * identify differential equation and describe
	 * @return a keyword command for the DIFEQ keyword
	 */
	public KeywordCommand constructDifeqKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Identify differential equation and describe"; }

			public void execute (CommandSequence tokens)
			{ expecting (2, tokens); environment.getDifferentialEquationsManager ().identifyDiffEq (tokens); }
		};
	}


	/**
	 * find root of function
	 * @return a keyword command for the ROOTOF keyword
	 */
	public KeywordCommand constructRootKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Find root of function near specified approximation"; }
			public void execute (CommandSequence tokens)
			{
				StringBuffer functionName = new StringBuffer ();
				int pos = getFunctionName (0, tokens, functionName);

				environment.findFunctionRoot
				(
					functionName.toString (),
					tokens.get (pos).getTokenImage ()
				);
			}
		};
	}


	/**
	 * find max/min of function
	 * @return a keyword command for the MAXMINOF keyword
	 */
	public KeywordCommand constructMaxMinKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Find Max/Min of function near specified approximation"; }
			public void execute (CommandSequence tokens)
			{
				StringBuffer functionName = new StringBuffer ();
				int pos = getFunctionName (0, tokens, functionName);

				environment.findFunctionMaxMin
				(
					functionName.toString (), tokens.get (pos).getTokenImage ()
				);
			}
		};
	}


	/**
	 * set domain constraints on a function
	 * @return a keyword command for the SETDOMAIN keyword
	 */
	public KeywordCommand constructSetDomainKeywordCommand ()
  	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{ engine.getDeclarationSupport ().setDomainConstraints (withCommandRemoved (tokens)); }

			public String describe () { return "Set domain constraints on a function"; }
		};
  	}


	/**
	 * standardize a function domain to [-1,1]
	 * @return a keyword command for the STDDOMAIN keyword
	 */
	public KeywordCommand constructStdDomainKeywordCommand ()
  	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{ engine.getDeclarationSupport ().standardizeDomainConstraints (withCommandRemoved (tokens)); }

			public String describe () { return "Standardize a function domain to [-1,1]"; }
		};
  	}


	/**
	 * apply Discrete Cosine Transform to function
	 * @return a keyword command for the DCT keyword
	 */
	public KeywordCommand constructDctKeywordCommand ()
  	{
		return new KeywordCommand ()
		{
			public String describe ()
			{ return "Apply Discrete Cosine Tranform to function"; }

			public void execute (CommandSequence tokens)
			{ new CosineTransform<T>(environment).analyze (tokens); }
		};
  	}


	/**
	 * Add a comment to the output stream
	 * @return a keyword command for the COMMENT prefix
	 */
	public KeywordCommand constructCommentCommand ()
	{
		return new KeywordCommand ()
		{
			public String describe () { return "Add a comment to the output stream"; }
			public void execute (CommandSequence tokens) {}
		};
	}


	/**
	 * start the RPN calculator
	 * @return a keyword command for the RPN prefix
	 */
	public KeywordCommand constructRpnCommand ()
	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{
				ScriptManager<T> scriptManager = engine.getScriptManager (); scriptManager.readSymbols ("NamedConstants.txt");
				scriptManager.readSymbols ("BuiltInFunctions.txt"); scriptManager.readSymbols ("TrigIdentities.txt");

				SimpleScreenIO.startBackgroundTask
				(
					() -> new net.myorb.math.expressions.gui.rpn.Calculator<T> (environment)
				);
			}
			public String describe () { return "Start the RPN calculator"; }
		};
	}


	/**
	 * set value display mode and precision
	 * @return a keyword command for the SETMODE prefix
	 */
	public KeywordCommand constructSetModeKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{ new DisplayModeForm (environment.getSpaceManager ().getformattingModes (), environment.getOutStream ()); }
			public String describe () { return "Set value display mode and precision"; }
		};
	}


	/**
	 * Set color scheme manager for contour plots
	 * @return a keyword command for the SETCONTOUR prefix
	 */
	public KeywordCommand constructSetContourKeywordCommand ()
	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{
				System.out.println (tokens);
				new net.myorb.math.expressions.charting.colormappings.ContourColorSchemeRequest ();
			}
			public String describe () { return "Set color scheme manager for contour plots"; }
		};
	}


	/**
	 * Set the value of a property
	 * @return a keyword command for the PSET command
	 */
	public KeywordCommand constructPsetCommand ()
	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{ SimplePropertiesManager.pset (withCommandRemoved (tokens)); }
			public String describe () { return "Set the value of a property"; }
		};
	}


	/**
	 * Delete the value of a property
	 * @return a keyword command for the PDEL command
	 */
	public KeywordCommand constructPdelCommand ()
	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{ SimplePropertiesManager.pdel (withCommandRemoved (tokens)); }
			public String describe () { return "Delete the value of a property"; }
		};
	}


	/**
	 * Clear all properties of a directory entry
	 * @return a keyword command for the PCLR command
	 */
	public KeywordCommand constructPclrCommand ()
	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{ SimplePropertiesManager.pclr (withCommandRemoved (tokens)); }
			public String describe () { return "Clear all properties of a directory entry"; }
		};
	}


	/**
	 * Load properties of a directory entry from jSON source
	 * @return a keyword command for the PLOAD command
	 */
	public KeywordCommand constructPloadCommand ()
	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{
				String entry = getNextOperandToken (tokens).getTokenImage ();
				SimplePropertiesManager.pload (entry, fileFor (filename (tokens)));
			}
			public String describe () { return "Load properties of a directory entry from jSON source"; }
		};
	}


	/**
	 * Save properties of a directory entry to jSON file
	 * @return a keyword command for the PSAVE command
	 */
	public KeywordCommand constructPsaveCommand ()
	{
		return new KeywordCommand ()
		{
			public void execute (CommandSequence tokens)
			{
				String entry = getNextOperandToken (tokens).getTokenImage ();
				SimplePropertiesManager.psave (entry, fileFor (filename (tokens)));
			}
			public String describe () { return "Save properties of a directory entry to jSON file"; }
		};
	}


	/**
	 * populate the keyword map with core definition commands
	 */
	public void constructCoreKeywordMap ()
	{
		add (OperatorNomenclature.TIP_PREFIX, constructCommentCommand ());
		add (OperatorNomenclature.COMMENT_PREFIX, constructCommentCommand ());
		add (OperatorNomenclature.DEFINITION_PREFIX, constructDefineKeywordCommand ());
		add (OperatorNomenclature.HG_POLYNOMIAL_PREFIX, constructHGPolynomialKeywordCommand ());
		add (OperatorNomenclature.TRANSFORM_ENABLE, constructTransformEnableKeywordCommand ());
		add (OperatorNomenclature.TRANSFORM_PREFIX, constructTransformKeywordCommand ());
		add (OperatorNomenclature.LIB_IMPORT_PREFIX, constructLibImportKeywordCommand ());
		add (OperatorNomenclature.SEGMENTED_PREFIX, constructSegmentedFunctionKeywordCommand ());
	}

	/**
	 * populate the keyword map
	 */
	public void constructKeywordMap ()
	{

		// non-alpha commands
		constructCoreKeywordMap ();

		// alpha commands allow for case independence
		addAsLowerCase (OperatorNomenclature.DEFINITION_KEYWORD, constructDefineKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.LIBRARY_KEYWORD, constructLibraryKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.CONFIGURE_KEYWORD, constructConfigureKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.FAMILY_KEYWORD, constructFamilyKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.CALCULATE_KEYWORD, constructCalculateKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.CALC_ABBREVIATION_KEYWORD, constructCalculateKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.RADIX_KEYWORD, constructRadixKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.POLY_ABBREVIATION_KEYWORD, constructPolynomialKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.POLYNOMIAL_KEYWORD, constructPolynomialKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.POLYPRINT_KEYWORD, constructPolyPrintKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SCRIPTPRINT_KEYWORD, constructScriptPrintKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PRETTYPRINT_KEYWORD, constructPrettyPrintKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.RENDER_FUNCTION_KEYWORD, constructRenderFunctionKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.RENDER_DIFFEQ_KEYWORD, constructRenderDiffEqKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.RENDER_KEYWORD, constructRenderKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SELECT_KEYWORD, constructSelectKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.DESCRIBE_KEYWORD, constructDescribeKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.DERIVE_KEYWORD, constructDeriveKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.GRAPH_KEYWORD, constructGraphKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.CHART_KEYWORD, constructChartKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SPLOT_KEYWORD, constructSplotKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.ENTITLED_KEYWORD, constructEntitledKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PLOTT_KEYWORD, constructPlottKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PLOTF_KEYWORD, constructPlotfKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PLOTRI_KEYWORD, constructPlotriKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PLOT3D_KEYWORD, constructPlot3dKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PLOTC_KEYWORD, constructPlotcKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PLOTM_KEYWORD, constructPlotmKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.MANDELBROT_KEYWORD, constructMandelbrotKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SHOW_SYMBOLS_KEYWORD, constructShowKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SAVE_INPUT_KEYWORD, constructSaveKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.ITERATE_KEYWORD, constructIterateKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.BACKGROUND_KEYWORD, constructBackgroundKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.ASSERT_KEYWORD, constructAssertKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.READ_INPUT_KEYWORD, constructReadKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.RECOGNIZE_KEYWORD, constructRecognizeKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.VERIFY_KEYWORD, constructVerifyKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.HELP_KEYWORD, constructHelpKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.FFT_KEYWORD, constructFftKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.DOCS_KEYWORD, constructDocsKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.ENCODE_KEYWORD, constructEncodeKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.EXPRESS_KEYWORD, constructExpressKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SAVEJSON_KEYWORD, constructSaveJsonKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.LOADJSON_KEYWORD, constructLoadJsonKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SPLINE_KEYWORD, constructSplineKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.OPTIMIZE_KEYWORD, constructOptimizeKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PREPARE_KEYWORD, constructPrepareKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PREPPOLY_KEYWORD, constructPrepPolyKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.TDES_KEYWORD, constructTdesKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.DIFEQ_KEYWORD, constructDifeqKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.ROOTOF_KEYWORD, constructRootKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.MAXMINOF_KEYWORD, constructMaxMinKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.IMPORT_KEYWORD, constructImportKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.EXPORT_KEYWORD, constructExportKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SCATTER_KEYWORD, constructScatterKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SIDEBYSIDE_KEYWORD, constructComparisonKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SETDOMAIN_KEYWORD, constructSetDomainKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.STDDOMAIN_KEYWORD, constructStdDomainKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SETCONTOUR_KEYWORD, constructSetContourKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.SETMODE_KEYWORD, constructSetModeKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.DCT_KEYWORD, constructDctKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PLOAD_KEYWORD, constructPloadCommand ());
		addAsLowerCase (OperatorNomenclature.PSAVE_KEYWORD, constructPsaveCommand ());
		addAsLowerCase (OperatorNomenclature.PSET_KEYWORD, constructPsetCommand ());
		addAsLowerCase (OperatorNomenclature.PCLR_KEYWORD, constructPclrCommand ());
		addAsLowerCase (OperatorNomenclature.PDEL_KEYWORD, constructPdelCommand ());
		addAsLowerCase (OperatorNomenclature.RPN_KEYWORD, constructRpnCommand ());

		addPrimeNumberCommands ();

	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.commands.EnvironmentalUtilities.AccessToTopOfStack#getValue(net.myorb.math.expressions.commands.CommandAdministration.CommandSequence)
	 */
	public ValueManager.GenericValue getValue (CommandSequence sequence)
	{
		engine.process (withCommandRemoved (sequence));
		return engine.popValueStack ();
	}


	/**
	 * @return access to top of stack
	 */
	public AccessToTopOfStack getAccess () { return this; }


	/**
	 * identify EvaluationEngine to be used
	 * @param engine the EvaluationEngine to use
	 */
	public void setEvaluationEngine
	(EvaluationEngine<T> engine) { this.engine = engine; }
	protected EvaluationEngine<T> engine;


	/**
	 * @return hash of command names
	 */
	public HashMap <String, KeywordCommand> getCommands () { return commands; }


	/**
	 * connect with the engine that can process tokens
	 * @param engine the engine that constructed this object
	 */
	public KeywordMap (EvaluationEngine<T> engine)
	{
		this (engine, true);
	}
	public KeywordMap (EvaluationEngine<T> engine, boolean doConstruct)
	{
		super (engine.getEnvironment ()); setEvaluationEngine (engine);
		if (doConstruct) constructKeywordMap ();
	}


}

