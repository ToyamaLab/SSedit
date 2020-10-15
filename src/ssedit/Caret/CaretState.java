package ssedit.Caret;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import ssedit.FrontEnd;
import ssedit.Common.Functions;
import ssedit.Common.GlobalEnv;
import ssedit.GUI.Search;

public class CaretState {

public class ExLog1 {

	}

protected static boolean flag = false;
	
	// カッコの対応付けのattribute
	public static SimpleAttributeSet attr = new SimpleAttributeSet();
	// コメントアウトしたときのattribute
	public static SimpleAttributeSet commentAttr = new SimpleAttributeSet();
	// エラー箇所のattribute
	public static SimpleAttributeSet errAttr = new SimpleAttributeSet();
	// 標準のattribute
	public static SimpleAttributeSet plane = new SimpleAttributeSet();
	
	// エラーログからエラーの箇所を探し、その文字を返す
	public String[] checkError() {
		String[] errorstr = { "", "" };
		String tmp = Functions.readFile(Functions.change(Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile, "outdir"))
				+ GlobalEnv.OS_FS + ".errorlog.txt");
		String regex = "\\>>>> (.*)\\ <<<<";
		String regex2 = "\\<<<< (.*)\\  ##";
		// String regex2 = "no such .*\\: (.*)";
		Pattern p = Pattern.compile(regex);
		Pattern p2 = Pattern.compile(regex2);
		errorstr[0] = check(p, tmp);
		errorstr[1] = check(p2, tmp);
		return errorstr;
	}

	// エラーの箇所をチェック
	private static String check(Pattern p, String target) {
		Matcher m = p.matcher(target);
		String errorstr = "";
		if (m.find()) {
			// System.out.println("マッチします");
			// System.out.println("マッチした部分は" + m.group(1) + "です");
			errorstr = m.group(1);
		} else {
			// System.out.println("マッチしません");
		}
		return errorstr;
	}
	
	// タブのサイズを変更
	public static void changeTabSize(int tabSize, JTextPane text,
			DefaultStyledDocument docu) {
		FontMetrics fm = text.getFontMetrics(text.getFont());
		int charWidth = fm.charWidth('m');
		int tabLength = charWidth * tabSize;
		TabStop[] tabs = new TabStop[10];
		for (int j = 0; j < tabs.length; j++) {
			tabs[j] = new TabStop((j + 1) * tabLength);
		}
		TabSet tabSet = new TabSet(tabs);
		StyleConstants.setTabSet(CaretState.plane, tabSet);
		text.getStyledDocument().setParagraphAttributes(0, docu.getLength(),
				CaretState.plane, false);
	}
	
	// 文脈読んでコメントアウト
	public static void caretComment(JTextPane textpane, DefaultStyledDocument doc) {
		boolean flag = false;
		Element root = textpane.getDocument().getDefaultRootElement();// 行数を取得したいJTextComponentからElementクラスを取得
		int line = root.getElementCount();
		int length = doc.getLength();
		// その行までの文字数
		int strsCount = 0;
		// その行の文字数
		int strCount;
		String str = textpane.getText();
		String[] strs = str.split(GlobalEnv.OS_LS, -1);
		for (int i = 0; i < line; i++) {
			if (i != 0) {
				strsCount += strs[i - 1].length();
			}
			strCount = strs[i].length();
			if (strs[i].contains("--")) {
				int index = strs[i].indexOf("--");
				try {
					doc.setCharacterAttributes(strsCount + i, strsCount + index + i, plane, true);
					doc.setCharacterAttributes(strsCount + index + i, strCount - index, commentAttr, true);
				} catch (IllegalStateException ex) {
				}
			} else {
				try {
					doc.setCharacterAttributes(strsCount + i, strCount + 1, plane, true);
				} catch (IllegalStateException ex) {
				}
			}
		}
		for (int i = 0; i < length; i++) {
			try {
				if (doc.getText(i, 2).equals("/*")) {
					flag = true;
					// System.out.println("i=" + i);
					for (int j = i + 2; j < length; j++) {
						if (doc.getText(j, 2).equals("*/") && flag == true) {
							try {
								// System.out.println("j=" + j);
								doc.setCharacterAttributes(i, j - i + 2, commentAttr, true);
								flag = false;
							} catch (IllegalStateException ex) {
							}
						}
					}
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		if(GlobalEnv.regex[0].isSelected()){
//			System.out.println("正規表現");
			Search.incrementalSearchRegex(GlobalEnv.currnetTarget);
		} else {
//			System.out.println("通常");
			Search.incrementalSearch(GlobalEnv.currnetTarget, GlobalEnv.start);
		}
	}
	
	// 指定の行をそのすぐ下にコピー
	public static void copyRow(JTextPane textpane, DefaultStyledDocument document) {// 行のコピー
		// int position = p;
		// 行数を取得したいJTextComponentからElementクラスを取得
		Element root = textpane.getDocument().getDefaultRootElement();
		// キャレットの位置を取得
		int offset = textpane.getCaretPosition();
		// キャレットの位置にある行番号を取得
		int currentLine = root.getElementIndex(offset);
		// テキストの最大の行数を取得
		// int maxLine = root.getElementCount();
		int startstrIndex = 0, strindexLength = 0;
		String partstr = "";
		String strCopy = "";

		String str = textpane.getText();
		String[] strs = str.split(GlobalEnv.OS_LS, -1);// テキスト内の文書を改行で区切って配列に格納（nullも）

		// テキストが選択されていなかったら
		if ((textpane.getSelectedText() == null)) {
			// キャレットのある行までのドキュメント
			for (int i = 0; i < currentLine; i++) {
				partstr += strs[i];
				// 現在のキャレットのある行の開始位置（キャレットのある行までのドキュメントの文字数+改行分+1文字）
				startstrIndex = partstr.length() + (i + 1);
			}
			// コピーする行の文字数
			strindexLength = strs[currentLine].length();

			try {
				document.insertString(startstrIndex, strs[currentLine] + GlobalEnv.OS_LS, plane);
				// コピーした行を選択状態に
				textpane.setSelectionStart(startstrIndex + strindexLength + 1);
				textpane.setSelectionEnd(startstrIndex + strindexLength
						+ strindexLength + 1);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			// テキストが選択されていたら
		} else {
			// 選択されたテキストの開始位置
			int start = textpane.getSelectionStart();
			// 選択されたテキストの終了位置
			int end = textpane.getSelectionEnd();
			// 開始位置にある行番号を取得
			int startLine = root.getElementIndex(start);
			// 終了位置にある行番号を取得
			int endLine = root.getElementIndex(end);
			for (int i = 0; i <= endLine; i++) {
				partstr += strs[i];
				// 選択された行の次の行の先頭位置（選択した行までのドキュメントの文字数+改行分+1文字）
				startstrIndex = partstr.length() + (i + 1);
			}
			for (int i = startLine; i <= endLine; i++) {
				strindexLength += strs[i].length() + 1;
				// コピーしたい行
				strCopy += strs[i] + GlobalEnv.OS_LS;
			}
			try {
				GlobalEnv.doc.insertString(startstrIndex, strCopy, plane);
				// コピーした行を選択状態に
				textpane.setSelectionStart(startstrIndex);
				textpane.setSelectionEnd(startstrIndex + strCopy.length() - 1);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void caretError(JTextPane textpane, DefaultStyledDocument doc,
			String[] errorstr) {
		if (!errorstr.equals("")) {
			StringBuffer buf = new StringBuffer();
			buf.append(errorstr[0]);
			buf.append(errorstr[1]);
			String str = buf.toString();
			int length = errorstr[0].length();
			// textpaneの中でerrorstrが見つかる位置
			int index = textpane.getText().indexOf(str);
			if (index >= 0) {
				doc.setCharacterAttributes(index, length, errAttr, true);
			}
		}
	}
	
	// ポジションpにある文字がコメント内(--)かどうか判別
	public static boolean judgeComment(int p) {
		Element root = GlobalEnv.textPane.getDocument().getDefaultRootElement();// 行数を取得したいJTextComponentからElementクラスを取得
		int currentLine = root.getElementIndex(p);// ポジションpの位置にある行番号を取得
		String text = "";
		String str = GlobalEnv.textPane.getText();
		String[] strs = str.split(GlobalEnv.OS_LS, -1);// テキスト内の文書を改行で区切って配列に格納（nullも）
		for(int i = 0; i < currentLine; i++){
			text += (strs[i] + GlobalEnv.OS_LS);
		}
		// ポジションpのある行の行内での位置
		int caretPos = p - text.length();
		String partStr = strs[currentLine].substring(0, caretPos);
		if(partStr.contains("--")){
			return true;
		} else {
			return false;
		}
	}
	// ポジションpにある文字がコメント内(/* */)かどうか判別
	public static boolean judgeComment2(int p) {
		int length = GlobalEnv.doc.getLength();
		String partStr = "";
		String partStr2 = "";
		
		// pの位置より手前を探索
		for (int i = p; i >= 0; i--) {
			try {
				partStr = GlobalEnv.textPane.getText(i - 1, 2);
				partStr2 = GlobalEnv.textPane.getText(i - 2, 2);
			} catch (BadLocationException e) {
				// e.printStackTrace();
			}
			// pの位置より手前に"/*"があるか
			if (partStr.equals("/*")) {
				for (int j = p; j < length; j++) {
					try {
						partStr = GlobalEnv.textPane.getText(j - 1, 2);
					} catch (BadLocationException e) {
						// e.printStackTrace();
					}
					if (partStr.equals("*/")) {
						return true;
					}
				}
			} else if (partStr2.equals("*/")) {
				return false;
			}
		}
		return false;
	}


	public static void commentout(JTextPane textpane, DefaultStyledDocument document) {
		// 行数を取得したいJTextComponentからElementクラスを取得
		Element root = document.getDefaultRootElement();
		// キャレットの位置を取得
		int offset = textpane.getCaretPosition();
		// キャレットの位置にある行番号を取得
		int currentLine = root.getElementIndex(offset);
		// int maxLine = root.getElementCount();// テキストの最大の行数を取得
		// int startLine = root.getElementIndex(start);//開始位置にある行番号を取得
		// int endLine = root.getElementIndex(end);//終了位置にある行番号を取得
		String str = textpane.getText();
		// テキスト内の文書を改行で区切って配列に格納（nullも）
		String[] strs = str.split(GlobalEnv.OS_LS, -1);

		String partstr = "";
		int startstrIndex = 0;

		// テキストが選択されていなかったら
		if ((textpane.getSelectedText() == null)) {
			for (int i = 0; i < currentLine; i++) {
				partstr += strs[i];
				startstrIndex = partstr.length() + (i + 1);
			}

			// 既にその行がコメントアウトされていたら
			if (strs[currentLine].replaceAll(" ", "").trim().startsWith("--")) {
				try {
					// "--"が最初に出てくる位置を取得
					int index = strs[currentLine].indexOf("--");
					// System.out.println(index);
					// "--"が出現する位置の2文字を削除
					document.remove(startstrIndex + index, 2);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else {
				try {
					document.insertString(startstrIndex, "--", plane);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			// 選択されていたら
		} else {
			// 選択されたテキストの開始位置
			int start = textpane.getSelectionStart();
			// 選択されたテキストの終了位置
			int end = textpane.getSelectionEnd();
			try {
				document.insertString(start, " /* ", plane);
				document.insertString(end + 4, " */ ", plane);
				textpane.setSelectionStart(start);
				textpane.setSelectionEnd(end + 8);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	// 行の削除
	public static void deleteRow(JTextPane textpane, DefaultStyledDocument document) {
		// 行数を取得したいJTextComponentからElementクラスを取得
		Element root = document.getDefaultRootElement();
		// キャレットの位置を取得
		int offset = textpane.getCaretPosition();
		// キャレットの位置にある行番号を取得
		int currentLine = root.getElementIndex(offset);
		// テキストの最大の行数を取得
		int maxLine = root.getElementCount();
		String str = textpane.getText();
		// テキスト内の文書を改行で区切って配列に格納（nullも）
		String[] strs = str.split(GlobalEnv.OS_LS, -1);

		String partstr = "";
		int startstrIndex = 0, strindexLength = 0;

		// 選択されていなかったら
		if ((textpane.getSelectedText() == null)) {
			for (int i = 0; i < currentLine; i++) {
				partstr += strs[i];
				// 削除する行の先頭のindex
				startstrIndex = partstr.length() + (i + 1);
			}
			// 削除する行の文字数
			strindexLength = strs[currentLine].length();
			// 最終行だったら
			if (currentLine == maxLine - 1) {
				try {
					GlobalEnv.doc.replace(startstrIndex, strindexLength, "", plane);
					GlobalEnv.textPane.setCaretPosition(startstrIndex);
					return;
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else {
				try {
					GlobalEnv.doc.replace(startstrIndex, strindexLength + 1, "", plane);
					GlobalEnv.textPane.setCaretPosition(startstrIndex);
					return;
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		// 選択されていたら
		} else {
			// 選択されたテキストの開始位置
			int start = textpane.getSelectionStart();
			// 選択されたテキストの終了位置
			int end = textpane.getSelectionEnd();
			// 開始位置にある行番号を取得
			int startLine = root.getElementIndex(start);
			// 終了位置にある行番号を取得
			int endLine = root.getElementIndex(end);
			String selectedpartstr = "";
			int count = 0;
			
			for(int i = 0; i < startLine; i++ ){
				partstr += strs[i];
				// 削除する行の先頭のindex
				startstrIndex = partstr.length() + (i + 1);
			}
			for(int i = startLine; i <= endLine; i++){
				selectedpartstr += strs[i];
				count++;
			}
			strindexLength = selectedpartstr.length() + count - 1;
			if (endLine == maxLine - 1) {
				try {
					document.replace(startstrIndex, strindexLength, "", plane);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else {
				try {
					document.replace(startstrIndex, strindexLength + 1, "", plane);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void caretChange(DefaultStyledDocument doc, int p) {
		// ドキュメントの長さを取得
		int length = doc.getLength();
		try {
			// キャレットの位置の文字を1文字取得
			String text = doc.getText(p, 1);
			if (text.equals("{") && !judgeComment(p) && !judgeComment2(p)) {
				flag = true;
				for (int x = p + 1; x < length; x++) {// "{"の次の文字からdocの長さまで探す
					int count1 = 0;
					// 進んで最初に見つけたのが"}"だったら
					if (doc.getText(x, 1).equals("}") && flag == true && !judgeComment(x) && !judgeComment2(x)) {
						// キャレットの1文字に属性セット
						doc.setCharacterAttributes(p, 1, attr, true);
						// 進んだ先の1文字に属性セット
						doc.setCharacterAttributes(x, 1, attr, true);
						return;
					} else if (doc.getText(x, 1).equals("{")) {// "進んで最初に見つけたのが"{"だったら
						for (int y = p + 1; y < length; y++) {
							if (doc.getText(y, 1).equals("{")) {
								count1++;
							}
							if (doc.getText(y, 1).equals("}")) {
								count1--;
							}
							if (count1 == -1 && flag == true && !judgeComment(x) && !judgeComment2(x)) {
								doc.setCharacterAttributes(p, 1, attr, true);
								doc.setCharacterAttributes(y, 1, attr, true);
								return;
							}
						}
					}
				}
			} else if (text.equals("[") && !judgeComment(p) && !judgeComment2(p)) {
				flag = true;
				for (int x = p + 1; x < length; x++) {
					int count1 = 0;
					if (doc.getText(x, 1).equals("]") && flag == true && !judgeComment(x) && !judgeComment2(x)) {
						doc.setCharacterAttributes(p, 1, attr, true); // ３文字目から５文字分を太字に
						doc.setCharacterAttributes(x, 1, attr, true); // ３文字目から５文字分を太字に
						return;
					} else if (doc.getText(x, 1).equals("[")) {
						for (int y = p + 1; y < length; y++) {
							if (doc.getText(y, 1).equals("[")) {
								count1++;
							}
							if (doc.getText(y, 1).equals("]")) {
								count1--;
							}
							if (count1 == -1 && flag == true && !judgeComment(x) && !judgeComment2(x)) {
								doc.setCharacterAttributes(p, 1, attr, true); // ３文字目から５文字分を太字に
								doc.setCharacterAttributes(y, 1, attr, true); // ３文字目から５文字分を太字に
								return;
							}
						}
					}
				}
			} else if (text.equals("(") && !judgeComment(p) && !judgeComment2(p)) {
				flag = true;
				for (int x = p + 1; x < length; x++) {
					int count1 = 0;
					if (doc.getText(x, 1).equals(")") && flag == true && !judgeComment(x) && !judgeComment2(x)) {
						doc.setCharacterAttributes(p, 1, attr, true); // ３文字目から５文字分を太字に
						doc.setCharacterAttributes(x, 1, attr, true); // ３文字目から５文字分を太字に
						return;
					} else if (doc.getText(x, 1).equals("(")) {
						for (int y = p + 1; y < length; y++) {
							if (doc.getText(y, 1).equals("(")) {
								count1++;
							}
							if (doc.getText(y, 1).equals(")")) {
								count1--;
							}
							if (count1 == -1 && flag == true && !judgeComment(x) && !judgeComment2(x)) {
								doc.setCharacterAttributes(p, 1, attr, true); // ３文字目から５文字分を太字に
								doc.setCharacterAttributes(y, 1, attr, true); // ３文字目から５文字分を太字に
								return;
							}
						}
					}
				}
			} else if (text.equals("\"") && !judgeComment(p) && !judgeComment2(p)) {
				flag = true;
				int count = 0;
				int counter = 0;
				for (int x = 0; x < p; x++) {
					if (doc.getText(x, 1).equals("\"")) {
						counter++;
						if (counter % 2 != 0) {
							count++;
						} else {
							count--;
						}
					}
				}
				if (count == 0) {
					for (int x = p + 1; x < length; x++) {
						if (doc.getText(x, 1).equals("\"") && flag == true && !judgeComment(x) && !judgeComment2(x)) {
							doc.setCharacterAttributes(p, 1, attr, true); // ３文字目から５文字分を太字に
							doc.setCharacterAttributes(x, 1, attr, true); // ３文字目から５文字分を太字に
							return;
						}
					}
				}
			}

			if (flag == true) {
				flag = false;
			}

		} catch (BadLocationException ble) {
			// System.err.println("文書の読み込みに失敗しました。");
		} catch (IllegalStateException ex) {
		}
	}
	
	public static void autocomplete(ActionEvent e){
		Functions.searchFrom();
		Functions.searchWhere();
		GlobalEnv.table_popup.removeAll();
		GlobalEnv.table_menuItem = FrontEnd.setMenuItem(GlobalEnv.table_popup, GlobalEnv.table_array, GlobalEnv.table_array.size());
		// キャレットの位置がfromよりあとでwhereより手前だったら 
		if(GlobalEnv.p >= GlobalEnv.fromEnd && GlobalEnv.p <= GlobalEnv.whereStart){
			// キャレットの1個前の文字が"[^0-9a-zA-Z_]"以外だったらテーブル一覧のポップアップ
			if (!Functions.checkChar(GlobalEnv.p - 1, "\\w")) {
				for (int i = 0; i < GlobalEnv.table_array.size(); i++) {
					GlobalEnv.table_menuItem[i].addActionListener(new autocompleteListener());
				}
				FrontEnd.show_tablePopup(e);
			} else {
				int count = 0;
				// キャレット位置から前にさかのぼり、半角英数値(0～9、a～z、A～Z、_)以外が出てくる場所を探索
				for (int i = GlobalEnv.p - 1; i > 0; i--) {
					if (!Functions.checkChar(i, "\\w")) {
						// System.out.println(i);
						try {
							// キャレット位置より手前の最小単語
							String partStr = GlobalEnv.textPane.getText(i + 1, count);
							// System.out.println(partStr);
							GlobalEnv.table_menuItem2 = Functions.matchCheck(partStr, GlobalEnv.table_array, GlobalEnv.table_array2, GlobalEnv.parttable_array, GlobalEnv.table_menuItem2, GlobalEnv.table_popup);
							if (GlobalEnv.flag == 1) {
								for (int j = 0; j < GlobalEnv.table_array2.size(); j++) {
									GlobalEnv.table_menuItem2[j].addActionListener(new autocompleteListener());
								}
								FrontEnd.show_tablePopup2(e);
							}
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
						return;
					}
					count++;
				}
			}
		// 属性が書ける位置であれば
		} else {
			// 定義されたテーブルとエイリアスを取ってくる
			Functions.getTeble();
				// キャレット位置の手前が"."だったら
				try {
					if(GlobalEnv.textPane.getText(GlobalEnv.p - 1, 1).equals(".")){
						int count2 = 0;
						String alias = "";
						// キャレット位置から前にさかのぼり、エイリアスを探す
						for (int i = GlobalEnv.p - 2; i > 0; i--) {
							if (!Functions.checkChar(i, "\\w")) {
								// System.out.println(i);
								try {
									// エイリアス取得
									alias = GlobalEnv.textPane.getText(i + 1, count2);
//									 System.out.println(alias);
									 // そのエイリアスのテーブルが持つ属性の取得
//									System.out.println(alias);
									 Functions.getAliasTable(alias);
									for (int j = 0; j < GlobalEnv.attribute_array.size(); j++) {
										GlobalEnv.attribute_menuItem[j].addActionListener(new autocompleteListener());
									}
									FrontEnd.show_attributePopup(e);
//									return;
								} catch (BadLocationException e1) {
									e1.printStackTrace();
								}
								return;
							}
							count2++;
						}
					}
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
						
				// キャレットの手前の文字が"[^0-9a-zA-Z_]"以外だったら属性一覧のポップアップ
				if (!Functions.checkChar(GlobalEnv.p - 1, "\\w")) {
				    Functions.insertAttriburtes(GlobalEnv.currentTable_array);
					// System.out.println(GlobalEnv.attribute_array);
					for (int i = 0; i < GlobalEnv.attribute_array.size(); i++) {
						GlobalEnv.attribute_menuItem[i].addActionListener(new autocompleteListener());
					}
					FrontEnd.show_attributePopup(e);
				} else {
				    Functions.insertAttriburtes(GlobalEnv.currentTable_array);

				    String alias = Functions.searchAlias();
//				    System.out.println(alias);
				   Functions.getAliasTable(alias);
				    
					int count = 0;
					// キャレット位置から前にさかのぼり、半角英数値(0～9、a～z、A～Z、_)以外が出てくる場所を探索
					for (int i = GlobalEnv.p - 1; i > 0; i--) {
						if (!Functions.checkChar(i, "\\w")) {
							// System.out.println(i);
							try {
								// キャレット位置より手前の最小単語
								String partStr = GlobalEnv.textPane.getText(i + 1, count);
								// System.out.println(partStr);
								GlobalEnv.attribute_menuItem2 = Functions.matchCheck(partStr, GlobalEnv.attribute_array, GlobalEnv.attribute_array2, GlobalEnv.partattribute_array, GlobalEnv.attribute_menuItem2, GlobalEnv.tableattribute_popup);
								if (GlobalEnv.flag == 1) {
									for (int j = 0; j < GlobalEnv.attribute_array2.size(); j++) {
										GlobalEnv.attribute_menuItem2[j].addActionListener(new autocompleteListener());
									}
									FrontEnd.show_attributePopup2(e);
								}
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}
							return;
						}
						count++;
					}
				}

		}
	}
}

class autocompleteListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		SimpleAttributeSet attr = new SimpleAttributeSet();

		/* 処理したい内容をここに記述する */
		for (int i = 0; i < GlobalEnv.attribute_array.size(); i++) {
			if (e.getSource() == GlobalEnv.attribute_menuItem[i]) {
				try {
					GlobalEnv.doc.insertString(GlobalEnv.p, GlobalEnv.attribute_array.get(i), attr);
					GlobalEnv.textPane.setCaretPosition(GlobalEnv.p);
					return;
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}
		for (int i = 0; i < GlobalEnv.attribute_array2.size(); i++) {
				if (e.getSource() == GlobalEnv.attribute_menuItem2[i]) {
					try {
						GlobalEnv.doc.insertString(GlobalEnv.p, GlobalEnv.partattribute_array.get(i), attr);
						GlobalEnv.textPane.setCaretPosition(GlobalEnv.p);	
						return;
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
		}
		for (int i = 0; i < GlobalEnv.table_array.size(); i++) {
			try {
				if (e.getSource() == GlobalEnv.table_menuItem[i]) {
					GlobalEnv.doc.insertString(GlobalEnv.p, GlobalEnv.table_array.get(i), attr);
					GlobalEnv.textPane.setCaretPosition(GlobalEnv.p);
					return;
				}
			} catch (BadLocationException e1) {
					e1.printStackTrace();	
			} catch (ArrayIndexOutOfBoundsException e1){
				
			}
				}
		for (int i = 0; i < GlobalEnv.table_array2.size(); i++) {
			if (e.getSource() == GlobalEnv.table_menuItem2[i]) {
				try {
					GlobalEnv.doc.insertString(GlobalEnv.p, GlobalEnv.parttable_array.get(i), attr);
					GlobalEnv.textPane.setCaretPosition(GlobalEnv.p);
					return;
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
