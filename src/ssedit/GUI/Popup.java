package ssedit.GUI;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ssedit.FrontEnd;
import ssedit.Common.Functions;
import ssedit.Common.GlobalEnv;

public class Popup{
	public static JPopupMenu newFile_popup = new JPopupMenu();
	String mediaFile = "media_list.xml";

	public Popup(){
		JMenu createNewFile = new JMenu("新規作成");
		getTagTexts(Functions.getWorkingDir() + GlobalEnv.OS_FS + "XML" + GlobalEnv.OS_FS + mediaFile, "name", GlobalEnv.media_array);
		GlobalEnv.media_array.add("HTML");
		for(int i = 0; i < GlobalEnv.media_array.size(); i++){
			final JMenuItem media = new JMenuItem(GlobalEnv.media_array.get(i));
			media.addActionListener(new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO 自動生成されたメソッド・スタブ
					String source = e.getSource().toString();
					source = source.substring(source.indexOf("text=") + 5, source.length()-1);
					createQuery(source);
				}
			});
			createNewFile.add(media);
		}
		newFile_popup.add(createNewFile);

	}


	// xmlファイルのドキュメントから指定したタグの中身のデータを取得し、配列に格納
		public static void getTagTexts(String URI, String target,
//				Vector<String> str_list) {
				List<String> array) {
			try {
				// ドキュメントビルダーファクトリを生成
				DocumentBuilderFactory dbfactory = DocumentBuilderFactory
						.newInstance();
				// ドキュメントビルダーを生成
				DocumentBuilder builder = dbfactory.newDocumentBuilder();
				// パースを実行してDocumentオブジェクトを取得
				Document document = builder.parse(URI);// Document
				NodeList elements = document.getElementsByTagName(target);
				for (int i = 0; i < elements.getLength(); i++) {
					array.add(elements.item(i).getTextContent());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Collections.sort(array);
		}
		// xmlファイルのドキュメントから指定したタグの中身のデータを取得し、配列に格納
		public static void getTagTexts2(String URI, String target,
				Vector<String> str_list) {
//				List<String> array) {
			try {
				// ドキュメントビルダーファクトリを生成
				DocumentBuilderFactory dbfactory = DocumentBuilderFactory
						.newInstance();
				// ドキュメントビルダーを生成
				DocumentBuilder builder = dbfactory.newDocumentBuilder();
				// パースを実行してDocumentオブジェクトを取得
				Document document = builder.parse(URI);// Document
				NodeList elements = document.getElementsByTagName(target);
				for (int i = 0; i < elements.getLength(); i++) {
					str_list.add(elements.item(i).getTextContent());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
//			Collections.sort(str_list);
		}

		public void createQuery(String media){
			String query = GlobalEnv.textPane.getText();
			if(query.trim().isEmpty() || FrontEnd.filestateLabel2.getText().isEmpty()){
				GlobalEnv.textPane.setText("GENERATE " + media + " {\n\n}@{debug = 'on', pbgcolor = 'aliceblue'}\nFROM ;");
				FrontEnd.filenameLabel.setText("");
			} else {
				int option = JOptionPane.showConfirmDialog(null,
						"編集中のクエリを保存しますか？", "確認",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (option == JOptionPane.YES_OPTION) {
					FrontEnd.button4.doClick();
					if(FrontEnd.filestateLabel.getText().equals("ファイル名を入力してください")
							|| FrontEnd.filestateLabel.getText().equals("キャンセルしました")){
						return;
					}
					GlobalEnv.textPane.setText("GENERATE " + media + " {\n\n}@{debug = 'on', pbgcolor = 'aliceblue'}\nFROM ;");
					FrontEnd.filenameLabel.setText("");
				} else if (option == JOptionPane.NO_OPTION) {
					GlobalEnv.textPane.setText("GENERATE " + media + " {\n\n}@{debug = 'on', pbgcolor = 'aliceblue'}\nFROM ;");
					FrontEnd.filenameLabel.setText("");
				} else if(option == JOptionPane.CANCEL_OPTION){
					FrontEnd.stateTimer.start();
					FrontEnd.filestateLabel.setText("キャンセルしました");
				}
			}
		}
}
