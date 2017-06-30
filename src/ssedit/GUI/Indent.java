package ssedit.GUI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class Indent {

	private static String generate = "";
	private static String from = "";

	public static void process (DefaultStyledDocument doc) {
//		getMedia_and_From(doc);
//		check_commnet(query);
//		indent(doc);
	}

/*
//	//getMedia_and_From
//	private static void getMedia_and_From(DefaultStyledDocument doc) {
//
//		if (!doc.isEmpty()) {
//
//			//GENERATE句をマッチング
//			String regex = "generate";
//			Pattern pattern = Pattern.compile(regex);
//			String lowerquery = doc.toLowerCase();
//			Matcher gmatcher = pattern.matcher(lowerquery);
//
//			//FROM句をマッチング
//			String regex2 = "from";
//			Pattern pattern2 = Pattern.compile(regex2);
//			String lowerquery2 = doc.toLowerCase();
//			Matcher fmatcher = pattern2.matcher(lowerquery2);
//
//			//GENERATE句、FROM句があるとき(普通のクエリ)
//			if (gmatcher.find() && fmatcher.find()) {
//
//				//Mediaを取得
//				String t = "";
//				String q1 = doc.replace("\n", " ");
//				String media = (t = q1.substring(q1.toLowerCase().indexOf("generate")+"generate".length()).trim())
//						.substring(0, t.indexOf(" ")).trim();
//
//				//System.out.println(t);
//
//				generate = "GENERATE "+media+" ";
//
////				System.out.println(generate);
//
//				//FROM句以下を取得
//				from = doc.substring(doc.toLowerCase().lastIndexOf("from"));
//
////				System.out.println(from);
//
//				indent(doc);
//
//			}
//
//			else if (!gmatcher.find() || !fmatcher.find()) {
//				System.err.println("GENERATE句またはFROM句が書かれていません");
//			}
//
////			else if (!fmatcher.find() && gmatcher.find()) {
////			else {
////				System.err.println("FROM句が書かれていません");
////			}
//
//		}
//
//		if (doc.isEmpty()) {
//			System.err.println("クエリが書かれていません");
//		}
//
//	}
//
//	//TODO コメントの読み飛ばし
//	private static void check_commnet(String query) {
//		String[] lines = query.split("\n");
//		String line = "";
////		String tmp = "";
//
//		for (int i = 0; i < lines.length; i++) {
//			line = lines[i];
//
////			System.out.println(line);
//
//			if(line.contains("/*")) {
//
//			}
//		}
//	}
//
//	//TODO dq,sqの読み飛ばし
//	private static void check_dq_sq(char q) {
//
//	}

	private static void indent(DefaultStyledDocument doc) {

		int p = 0;
		int length = doc.getLength();

		try {
			String text = doc.getText(p, 1);		
			for (p = 0; p < length; p++) {
				if (text.equals("{")) {
//					doc.insertString(p, "\n", );
				
					for(int i = p + 1; i < length; i++) {
//						int count = 0;

						if (IndentTestGUI.textpane.getText(i, 1).equals("}")) {

						}
					}
			//TODO GENERATE句で改行
			//TODO {で改行
			//TODO ],!などで改行
				}
			} catch (BadLocationException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

		//1文字ずつ取得
//		char str = query.charAt(i);
//
//		if (str == '{') {
//			for (int j = i + 1; j < length; j++) {
//				if () {
//
//				}
//			}
//		}

		}
	}
	*/
}


