
package net.myorb.testing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

public class DNDtest implements DropTargetListener
{

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drop(DropTargetDropEvent evt)
	{
		// TODO Auto-generated method stub
		
		// Called when the user finishes or cancels the drag operation
		 
		try {

			Transferable transferable = evt.getTransferable();

			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			
				evt.acceptDrop(DnDConstants.ACTION_COPY);

				Object dragContents = transferable.getTransferData(DataFlavor.javaFileListFlavor);

				evt.getDropTargetContext().dropComplete(true);

				// We append the label text to the text area when dropped

				System.out.println (dragContents);

			}
			else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {

				evt.acceptDrop(DnDConstants.ACTION_COPY);

				String dragContents = (String) transferable.getTransferData(DataFlavor.stringFlavor);

				evt.getDropTargetContext().dropComplete(true);

				// We append the label text to the text area when dropped

				System.out.println (dragContents);
				// setText(getText() + " " + dragContents);

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
