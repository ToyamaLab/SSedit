package ssedit.SSvisual;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import ssedit.Common.GlobalEnv;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class SSvisualController implements Initializable {

	@FXML private WebView view;
	@FXML private TextArea query;
	@FXML private Label select_tfe;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// 初期化メソッド
		final WebEngine engine = view.getEngine();
		//engine.load("https://www.google.co.jp/");
		engine.load("file:///Users/halken/Documents/workspace/ssql-halken/test_queries/sample.html");
		
		// クエリ反映
		String str = GlobalEnv.textPane.getText();
		// query.setText(str); //TODO
		
		// HTMLが読み込まれたら実行 DOMにイベントを付与する
		Worker<Void> loadWorker = engine.getLoadWorker();
		loadWorker.stateProperty().addListener(new ChangeListener<State>() {
			@Override public void changed(ObservableValue ov, State oldStaate, State newState) {
				/*
				if (newState == Worker.State.SUCCEEDED) {
					EventListener mouseOverEvent;
					EventListener mouseOutEvent;
					EventListener clickEvent;

					Document doc = engine.getDocument();
					NodeList nodeList = doc.getElementsByTagName("table");
					for (int i = 0; i < nodeList.getLength(); i++) {
						final Element el = (Element)nodeList.item(i);
						String str = el.getAttribute("class");
						if (str != null && str.equals("att")) {
							mouseOverEvent = new EventListener() {
								@Override
								public void handleEvent(Event ev) {
									// TODO 元のbgcolorを取得出来るように
									System.out.println("Mouse Over");
									String class_tfe = el.getParentNode().getAttributes().getNamedItem("class").getNodeValue();
									String tfe = class_tfe.substring(0, 8);
									executejQuery(engine, "$(\"." + tfe + "\").css(\"background-color\", \"#BBCCDD\");");
								}
							};
							mouseOutEvent = new EventListener() {
								@Override
								public void handleEvent(Event ev) {
									// TODO 元のbgcolorに直せるように
									System.out.println("Mouse Out");
									String class_tfe = el.getParentNode().getAttributes().getNamedItem("class").getNodeValue();
									String tfe = class_tfe.substring(0, 8);
									executejQuery(engine, "$(\"." + tfe + "\").css(\"background-color\", \"#FFFFFF\");");
								}
							};
							clickEvent = new EventListener() {
								@Override
								public void handleEvent(Event ev) {
									//TODO
									String class_tfe = el.getParentNode().getAttributes().getNamedItem("class").getNodeValue();
									String tfe = class_tfe.substring(0, 8);
									executejQuery(engine, "$(\"TD.nest\").css({\"border\":\"1px solid black\"});");
									executejQuery(engine, "$(\"." + tfe + "\").css({\"border\":\"1px solid red\"});");
									select_tfe.setText(tfe);
								}
							};
							((EventTarget) el).addEventListener("mouseover", mouseOverEvent, false);
							((EventTarget) el).addEventListener("mouseout", mouseOutEvent, false);
							((EventTarget) el).addEventListener("click", clickEvent, false);
						}
					}
				}
			*/
			}
		});
		
		/*
		// SSvisual用JavaScriptの読み込み
		try {
			String s = fileToString(new File("/Users/halken/Documents/workspace/ssqltool/src/ssqltool/Test.java"));
			//engine.executeScript(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	/*
	// ファイルの中身をStringで返す 主にJavaScript読み込み用
	public static String fileToString(File file) throws IOException {
		StringBuffer sb = new StringBuffer();
		try {
			FileReader br = new FileReader(file);
			int c;
			while ((c = br.read()) != -1) {
				sb.append((char) c);
			}
			br.close();
		} catch(FileNotFoundException e) {
			System.out.println(e);
		}
		return sb.toString();
	}
	*/
	
	/**** jQueryを使えるようにするメソッド executejQuery(engine, query) ****/
	public static final String DEFAULT_JQUERY_MIN_VERSION = "1.7.2";
	public static final String JQUERY_LOCATION = "http://code.jquery.com/jquery-1.7.2.min.js";
	//public static final String JQUERY_LOCATION = "/Users/halken/Documents/workspace/ssqltool/jscss/jquery-1.9.0.min.js";
	private static void enableFirebug(final WebEngine engine) {
		engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
	}
	private static Object executejQuery(final WebEngine engine, String minVersion, String jQueryLocation, String script) {
		return engine.executeScript(
			"(function(window, document, version, callback) { "
			+ "var j, d;"
			+ "var loaded = false;"
			+ "if (!(j = window.jQuery) || version > j.fn.jquery || callback(j, loaded)) {"
			+ " var script = document.createElement(\"script\");"
			+ " script.type = \"text/javascript\";"
			+ " script.src = \"" + jQueryLocation + "\";"
			+ " script.onload = script.onreadystatechange = function() {"
			+ " if (!loaded && (!(d = this.readyState) || d == \"loaded\" || d == \"complete\")) {"
			+ " callback((j = window.jQuery).noConflict(1), loaded = true);"
			+ " j(script).remove();"
			+ " }"
			+ " };"
			+ " document.documentElement.childNodes[0].appendChild(script) "
			+ "} "
			+ "})(window, document, \"" + minVersion + "\", function($, jquery_loaded) {" + script + "});"
		);
	} 
	private static Object executejQuery(final WebEngine engine, String minVersion, String script) {
		return executejQuery(engine, DEFAULT_JQUERY_MIN_VERSION, JQUERY_LOCATION, script);
	}
	private Object executejQuery(final WebEngine engine, String script) {
		return executejQuery(engine, DEFAULT_JQUERY_MIN_VERSION, script);
	}
	/**** ****/
}
