
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.DisplayIO.CommandProcessor;

import javax.swing.RootPaneContainer;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.io.PrintWriter;

public class CoreProperties
{

	RootPaneContainer environmentFrame;

	JComponent envSplit;

	JComponent symbolTable;
	JComponent functionTable;
	JComponent symbolSplit;
	
	JComponent dataFiles;
	JComponent scriptFiles;
	JComponent fileSplit;

	PrintWriter mainConsole;
	Environment <?> coreExecutionEnvironment;
	CommandProcessor coreCommandProcessor;
	SymbolMap coreSymbolMap;

	JTextField coreCommandLine;
	JScrollPane coreDisplayArea;

	public RootPaneContainer getEnvironmentFrame() {
		return environmentFrame;
	}
	public void setEnvironmentFrame(RootPaneContainer environmentFrame) {
		this.environmentFrame = environmentFrame;
	}
	public JComponent getEnvSplit() {
		return envSplit;
	}
	public void setEnvSplit(JComponent envSplit) {
		this.envSplit = envSplit;
	}
	public JComponent getSymbolTable() {
		return symbolTable;
	}
	public void setSymbolTable(JComponent symbolTable) {
		this.symbolTable = symbolTable;
	}
	public JComponent getFunctionTable() {
		return functionTable;
	}
	public void setFunctionTable(JComponent functionTable) {
		this.functionTable = functionTable;
	}
	public JComponent getSymbolSplit() {
		return symbolSplit;
	}
	public void setSymbolSplit(JComponent symbolSplit) {
		this.symbolSplit = symbolSplit;
	}
	public JComponent getDataFiles() {
		return dataFiles;
	}
	public void setDataFiles(JComponent dataFiles) {
		this.dataFiles = dataFiles;
	}
	public JComponent getScriptFiles() {
		return scriptFiles;
	}
	public void setScriptFiles(JComponent scriptFiles) {
		this.scriptFiles = scriptFiles;
	}
	public JComponent getFileSplit() {
		return fileSplit;
	}
	public void setFileSplit(JComponent fileSplit) {
		this.fileSplit = fileSplit;
	}
	public PrintWriter getMainConsole() {
		return mainConsole;
	}
	public void setMainConsole(PrintWriter mainConsole) {
		this.mainConsole = mainConsole;
	}
	public Environment<?> getCoreExecutionEnvironment() {
		return coreExecutionEnvironment;
	}
	public void setCoreExecutionEnvironment(Environment<?> coreExecutionEnvironment) {
		this.coreExecutionEnvironment = coreExecutionEnvironment;
	}
	public CommandProcessor getCoreCommandProcessor() {
		return coreCommandProcessor;
	}
	public void setCoreCommandProcessor(CommandProcessor coreCommandProcessor) {
		this.coreCommandProcessor = coreCommandProcessor;
	}
	public SymbolMap getCoreSymbolMap() {
		return coreSymbolMap;
	}
	public void setCoreSymbolMap(SymbolMap coreSymbolMap) {
		this.coreSymbolMap = coreSymbolMap;
	}
	public JTextField getCoreCommandLine() {
		return coreCommandLine;
	}
	public void setCoreCommandLine(JTextField coreCommandLine) {
		this.coreCommandLine = coreCommandLine;
	}
	public JScrollPane getCoreDisplayArea() {
		return coreDisplayArea;
	}
	public void setCoreDisplayArea(JScrollPane coreDisplayArea) {
		this.coreDisplayArea = coreDisplayArea;
	}

}
