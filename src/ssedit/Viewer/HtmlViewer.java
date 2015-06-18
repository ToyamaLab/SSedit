package ssedit.Viewer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import javax.swing.JFrame;

import ssedit.GUI.Browser;

public class HtmlViewer extends JFrame {
	private static final long serialVersionUID = 1L;
	private static Browser browser;
	int delay2 = 1000;
	final Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // 画面全体のサイズ
	int x = (int) (d.width * 0.4);
	int y = (int) (d.height * 0.5);	
	
//	public static void main(String... args){
//		HtmlViewer viewer = new HtmlViewer();
//	}

	public HtmlViewer() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		JFrame frame = new JFrame("Viewer");
		final JFXPanel fxPanel = new JFXPanel();
		frame.add(fxPanel);

		frame.setSize(x, y);
		frame.setVisible(true);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initFX(fxPanel);
			}
		});

	}

	public void initFX(JFXPanel fxPanel) {
		Scene scene = createScene();
		fxPanel.setScene(scene);
	}

	public Scene createScene() {
		browser = new Browser();
		Scene scene = new Scene(browser,d.width * 0.3 ,d.height * 0.5, Color.web("#666970"));
        return scene;
	}
	public static void setValue(){
		browser.reload();
	}	
}