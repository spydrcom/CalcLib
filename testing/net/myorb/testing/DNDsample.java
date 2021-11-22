package net.myorb.testing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.io.File;
import java.util.List;

public class DNDsample {

	 
	  public static void main(String[] args) {
			 
		    // Create a frame
		    Frame frame = new Frame("Example Frame");
		 
		    /*
		 
		* Create a container with a flow layout, which arranges its children
		 
		* horizontally and center aligned. A container can also be created with
		 
		* a specific layout using Panel(LayoutManager) constructor, e.g.
		 
		* Panel(new FlowLayout(FlowLayout.RIGHT)) for right alignment
		 
		*/
		    Panel panel = new Panel();
		 
		    // Add a drop target text area in the center of the frame
		    Component textArea = new DropTargetTextArea();
		    frame.add(textArea, BorderLayout.CENTER);
		 
		    // Add several draggable labels to the container
		    Label helloLabel = new DraggableLabel("Hello");
		    Label worldLabel = new DraggableLabel("World");
		    panel.add(helloLabel);
		    panel.add(worldLabel);
		 
		    // Add the container to the bottom of the frame
		    frame.add(panel, BorderLayout.SOUTH);
		 
		    // Display the frame
		    int frameWidth = 300;
		    int frameHeight = 300;
		    frame.setSize(frameWidth, frameHeight);
		 
		    frame.setVisible(true);
		 
		  }
}

	 
	 
	  // Make a Label draggable; You can use the example to make any component draggable
	  @SuppressWarnings("serial")
	class DraggableLabel extends Label implements DragGestureListener, DragSourceListener {
	    DragSource dragSource;
	 
	    public DraggableLabel(String text) {
	 
	setText(text);
	 
	dragSource = new DragSource();
	 
	dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
	    }
	 
	    public void dragGestureRecognized(DragGestureEvent evt) {
	 
	Transferable transferable = new StringSelection(getText());
	 
	dragSource.startDrag(evt, DragSource.DefaultCopyDrop, transferable, this);
	    }
	 
	    public void dragEnter(DragSourceDragEvent evt) {
	 
	// Called when the user is dragging this drag source and enters the drop target
	 
	System.out.println("Drag enter");
	    }
	 
	    public void dragOver(DragSourceDragEvent evt) {
	 
	// Called when the user is dragging this drag source and moves over the drop target
	 
	System.out.println("Drag over");
	    }
	 
	    public void dragExit(DragSourceEvent evt) {
	 
	// Called when the user is dragging this drag source and leaves the drop target
	 
	System.out.println("Drag exit");
	    }
	 
	    public void dropActionChanged(DragSourceDragEvent evt) {
	 
	// Called when the user changes the drag action between copy or move
	 
	System.out.println("Drag action changed");
	    }
	 
	    public void dragDropEnd(DragSourceDropEvent evt) {
	 
	// Called when the user finishes or cancels the drag operation
	 
	System.out.println("Drag action End");
	    }
	 
	  }
	
	
	  // Make a TextArea a drop target; You can use the example to make any component a drop target
	  @SuppressWarnings("serial")
	class DropTargetTextArea extends TextArea implements DropTargetListener {
	 
	    public DropTargetTextArea() {
	 
	new DropTarget(this, this);
	    }
	 
	    public void dragEnter(DropTargetDragEvent evt) {
	 
	// Called when the user is dragging and enters this drop target
	 
	System.out.println("Drop enter");
	    }
	 
	    public void dragOver(DropTargetDragEvent evt) {
	 
	// Called when the user is dragging and moves over this drop target
	 
	System.out.println("Drop over");
	    }
	 
	    public void dragExit(DropTargetEvent evt) {
	 
	// Called when the user is dragging and leaves this drop target
	 
	System.out.println("Drop exit");
	    }
	 
	    public void dropActionChanged(DropTargetDragEvent evt) {
	 
	// Called when the user changes the drag action between copy or move
	 
	System.out.println("Drop action changed");
	    }
	 

	public void drop(DropTargetDropEvent evt) {

		// Called when the user finishes or cancels the drag operation

		try {

			Transferable transferable = evt.getTransferable();

			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

				evt.acceptDrop(DnDConstants.ACTION_COPY);

				Object dragContents = transferable.getTransferData(DataFlavor.javaFileListFlavor);

				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) dragContents;

				evt.getDropTargetContext().dropComplete(true);

				// We append the label text to the text area when dropped

				System.out.println(dragContents);
				System.out.println(files.get(0).getName());

			} else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {

				evt.acceptDrop(DnDConstants.ACTION_COPY);

				String dragContents = (String) transferable.getTransferData(DataFlavor.stringFlavor);

				evt.getDropTargetContext().dropComplete(true);

				// We append the label text to the text area when dropped

				setText(getText() + " " + dragContents);

			} else {

				evt.rejectDrop();

			}

		} catch (IOException e) {

			evt.rejectDrop();

		} catch (UnsupportedFlavorException e) {

			evt.rejectDrop();

		}
	}
	 
}
	 

