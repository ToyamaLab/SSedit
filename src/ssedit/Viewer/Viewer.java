package ssedit.Viewer;

import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.Document;

import ssedit.Common.GlobalEnv;

public class Viewer extends JFrame {
	static JEditorPane html = new JEditorPane();
	public static void main(String args[]) {
		(new Viewer()).setVisible(true);
	}
	public Viewer() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
//				System.exit(0);
				setVisible(false);
			}
		});

		html.setEditable(false);

		getContentPane().add(new JScrollPane(html));
		setSize(400 , 300);
		setVisible(true);
	}

	
	public void setValue(){
		 Document doc = html.getDocument();
		   doc.putProperty(Document.StreamDescriptionProperty, null);
		   html.setContentType("text/html");
		try {
			html.setPage("file://localhost" + GlobalEnv.outdirPath + GlobalEnv.OS_FS +  ".htmlViewer.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
