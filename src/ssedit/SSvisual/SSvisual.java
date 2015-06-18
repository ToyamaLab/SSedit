package ssedit.SSvisual;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JFrame;

public class SSvisual extends JFrame{
	
	TextField field;
	WebEngine engine;
	final Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // 画面全体のサイズ
	int x = 900;
	int y = 700;	
	
	public SSvisual() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		JFrame frame = new JFrame("SSvisual");
		final JFXPanel fxPanel = new JFXPanel();
		frame.add(fxPanel);
		
		frame.setSize(x, y);
		frame.setVisible(true);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					initFX(fxPanel);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void initFX(JFXPanel fxPanel) throws IOException {
		Scene scene = createScene();
		fxPanel.setScene(scene);
	}
	
	private Scene createScene() throws IOException {
		// fxmlファイル読み込み
		Parent pane = FXMLLoader.load(getClass().getResource("SSvisualGUI.fxml"));
        Scene scene = new Scene(pane, Color.ALICEBLUE);

        return scene;
    }
}
