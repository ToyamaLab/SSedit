package ssedit.Common;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;

import com.ibm.db2.jcc.am.v;

import ssedit.FrontEnd;
import ssedit.Caret.CaretState;
import ssedit.GUI.Edit;
import ssedit.GUI.History;

//import com.mysql.jdbc.jdbc2.optional.SuspendableXAConnection;

public class Functions {
	/**********************************************************************************************/
	/* 以下は、裏側の処理（SuperSQLの実行・ファイルの読み取り・ファイルの作成・ライブラリPathの取得など） */
	/**********************************************************************************************/
	// Read file
	public static boolean isWindows() {
		if (GlobalEnv.OS.indexOf("Windows") >= 0)
			return true;
		return false;
	}

	public static boolean isMac() {
		if (GlobalEnv.OS.indexOf("Mac") >= 0)
			return true;
		return false;
	}

	public static boolean isLinux() {
		if (GlobalEnv.OS.indexOf("Linux") >= 0)
			return true;
		return false;
	}


	public static String readFile(String filename) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename), GlobalEnv.getEncoding()));
			StringBuffer sb = new StringBuffer();
			int c;
			while ((c = br.read()) != -1) {
				sb.append((char) c);
			}
			br.close();
			String str = sb.toString();
//			return str;
			// TODO ここの原因解明
			if(isWindows()){
				return str;
			} else {
			return str.substring(0, str.lastIndexOf(GlobalEnv.OS_LS));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// Create file
	public static boolean createFile(String filename, String s) {
		try {
//			s = s.replaceAll(GlobalEnv.OS_LS + "$", ""); // 末尾の改行コードを削除
			PrintWriter pw;
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename), GlobalEnv.getEncoding())));
			pw.println(s);
			pw.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/******************************************************************************************************************/
	 /*
	 * .ssql 保存/反映
	 */
	//.ssql 保存
	public static boolean createConfig(){
		String config_fn = GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile;
		
		String driver = "";
		String db = "";
		String host = "";
		String port = "";
		String sqlserver_instance = "";
		String sqlserver_options = "";
		String user = "";
		String password = "";
		String outdir = "";
		
		String s = "";
		if(!GlobalEnv.driverModel.getSelectedItem().equals("")){
			driver = "driver=" + (String)GlobalEnv.driverModel.getSelectedItem();
			s += driver;
		} else {
			driver = "driver=postgresql";
			s += driver;
		}
		if(!GlobalEnv.config_hostField.getText().equals("")){
			host = "host=" + GlobalEnv.config_hostField.getText();
			s += GlobalEnv.OS_LS + host;
		}
		if(!GlobalEnv.config_dbField.getText().equals("")){
			db = "db=" + GlobalEnv.config_dbField.getText();
			s += GlobalEnv.OS_LS + db;
		}		
		if(!GlobalEnv.config_portField.getText().equals("")){
			port = "port=" + GlobalEnv.config_portField.getText();
			s += GlobalEnv.OS_LS + port;
		}
		if(!GlobalEnv.config_instanceField.getText().equals("")){		// goto 20200728  SQL Server 
			sqlserver_instance = "sqlserver_instance=" + GlobalEnv.config_instanceField.getText();
			s += GlobalEnv.OS_LS + sqlserver_instance;
		}
		if(!GlobalEnv.config_optionsField.getText().equals("")){		// goto 20200728  SQL Server 
			sqlserver_options = "sqlserver_options=" + GlobalEnv.config_optionsField.getText();
			s += GlobalEnv.OS_LS + sqlserver_options;
		}
		if(!GlobalEnv.config_userField.getText().equals("")){
			user = "user=" + GlobalEnv.config_userField.getText();
			s += GlobalEnv.OS_LS + user;
		}
		if(!GlobalEnv.config_passwordField.getText().equals("")){
			password = "password=" + GlobalEnv.config_passwordField.getText();
			s += GlobalEnv.OS_LS + password;
		}
		if(!GlobalEnv.outdirModel.getSelectedItem().equals("")){
			outdir = "outdir=" + GlobalEnv.outdirModel.getSelectedItem();
			GlobalEnv.outdirPath = (String) GlobalEnv.outdirModel.getSelectedItem();
			s += GlobalEnv.OS_LS + outdir;
		}
		
		History.addItem(GlobalEnv.urlCombo, (String)GlobalEnv.urlCombo.getEditor().getItem(), 5);
		try {
//			String s = driver + GlobalEnv.OS_LS + db + GlobalEnv.OS_LS + host + GlobalEnv.OS_LS + user + GlobalEnv.OS_LS + outdir;
			PrintWriter pw;
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(config_fn), GlobalEnv.getEncoding())));
			pw.println(s);
			pw.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	// .ssql 反映
	public static void reflectConfig(){
		String config_fn = GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile;
		
		GlobalEnv.driverModel.setSelectedItem(has(config_fn, "driver"));
		GlobalEnv.config_hostField.setText(has(config_fn, "host"));
		GlobalEnv.config_dbField.setText(has(config_fn, "db"));
		GlobalEnv.config_portField.setText(has(config_fn, "port"));
		GlobalEnv.config_instanceField.setText(has(config_fn, "sqlserver_instance"));	// goto 20200728  SQL Server
		GlobalEnv.config_optionsField.setText(has(config_fn, "sqlserver_options"));		// goto 20200728  SQL Server
		GlobalEnv.config_userField.setText(has(config_fn, "user"));
		GlobalEnv.config_passwordField.setText(has(config_fn, "password"));
		//GlobalEnv.outdirCombo.setSelectedItem(has(config_fn, "outdir"));		//TODO
	}
	
	/*
	 * .ssqltool 保存/反映
	 */
	static String config_fn_ssqltool = GlobalEnv.USER_HOME + GlobalEnv.OS_FS + ".ssqltool";
	// .ssqltool 保存
    // 閉じる前に、開いていたクエリ名・ディレクトリ等の情報を、ホームの「.ssqltool」へ保存
    public static void saveSSQLtoolInfo(String folderPath1, String fileName1) {
        // GlobalEnv.folderPath1: タブ1のフォルダ
        // fileName1: タブ1のファイル名
        // GlobalEnv.folderPath2: タブ2のフォルダ
        String s = "";
        s += "folderPath1=" + folderPath1 + "" + GlobalEnv.OS_LS + "";
        s += "fileName1=" + fileName1 + "" + GlobalEnv.OS_LS + "";
        s += "radio1Selected=" + GlobalEnv.radio1Selected + "" + GlobalEnv.OS_LS + "";
        s += "radio2Selected=" + GlobalEnv.radio2Selected + "" + GlobalEnv.OS_LS + "";
        s += "tabSize=" + FrontEnd.tabSize + "" + GlobalEnv.OS_LS + "";
        s += "url=" + GlobalEnv.urlCombo.getEditor().getItem() + "" + GlobalEnv.OS_LS + "";
        String folderHistory = History.saveHistory(GlobalEnv.folderPath, GlobalEnv.folderCombo);
        s += "folderHistory=" + folderHistory + "" + GlobalEnv.OS_LS + "";
        String outdirHistory = History.saveHistory(GlobalEnv.outdirPath, GlobalEnv.outdirCombo);
        s += "outdirHistory=" + outdirHistory + "" + GlobalEnv.OS_LS + "";
        String urlHistory = History.saveHistory((String)GlobalEnv.urlCombo.getEditor().getItem(), GlobalEnv.urlCombo);
        s += "urlHistory=" + urlHistory + "" + GlobalEnv.OS_LS + "";
		if(!GlobalEnv.encodingModel.getSelectedItem().equals("")){
			s += "encoding=" + (String)GlobalEnv.encodingModel.getSelectedItem() + "" + GlobalEnv.OS_LS + "";
		}
		if(Edit.size > 0 ){
			s += "text_size=" + Edit.size + "" + GlobalEnv.OS_LS + "";
		}

        Functions.deleteFile(GlobalEnv.outdirPath, ".htmlViewer.ssql");
        Functions.deleteFile(GlobalEnv.outdirPath, ".htmlViewer.html");
        Functions.deleteFile(GlobalEnv.outdirPath, ".errorlog.txt");
        Functions.createFile(config_fn_ssqltool, s);
    }
    // .ssqltool 反映
    // 前回の情報（開いていたフォルダ・ファイル）を、ホームの「.ssqltool」から読み込んで反映
    public static void reflectSSQLtoolInfo() {
        String fP1 = Functions.has(config_fn_ssqltool, "folderPath1");
        String fN1 = Functions.has(config_fn_ssqltool, "fileName1");

        if (!fP1.equals("") && new File(fP1).exists()){
            GlobalEnv.folderPath = fP1;
        }
        if (!fN1.equals("") && new File(fN1).exists()) {
            FrontEnd.filenameLabel.setText(new File(fN1).getName());
            GlobalEnv.textPane.setText(Functions.readFile(fN1));
            FrontEnd.currentfileName = new File(fN1).getName();
            FrontEnd.currentfileData = Functions.readFile(fN1);
        } else {
            if (GlobalEnv.radio2Selected == 0){
                GlobalEnv.textPane.setText("");
            }
            else{
                GlobalEnv.textPane.setText("");
            }

        }
        try {
            GlobalEnv.radio1Selected = Integer.parseInt(Functions.has(config_fn_ssqltool, "radio1Selected"));
            GlobalEnv.radio2Selected = Integer.parseInt(Functions.has(config_fn_ssqltool, "radio2Selected"));
        } catch (Exception e) {
            GlobalEnv.radio1Selected = 0;
            GlobalEnv.radio2Selected = 0;
        }
        try {
            FrontEnd.tabSize = Integer.parseInt(Functions.has(config_fn_ssqltool, "tabSize"));
            FrontEnd.tabCombo.setSelectedIndex(FrontEnd.tabSize - 1);
            CaretState.changeTabSize(FrontEnd.tabSize, GlobalEnv.textPane, GlobalEnv.doc);
        } catch (Exception e) {
        	FrontEnd.tabSize = 3;
        }
//		GlobalEnv.url_textField.setText(Common.has(config_fn_ssqltool, "url"));
        String[] urlStr = Functions.has(config_fn_ssqltool, "urlHistory").split(",");
        for(int i = 0; i < urlStr.length; i++){
            GlobalEnv.urlModel.addElement(urlStr[i]);
        }
        String[] folderStr = Functions.has(config_fn_ssqltool, "folderHistory").split(",");
        for(int i = 0; i < folderStr.length; i++){
            GlobalEnv.folderModel.addElement(folderStr[i]);
        }
        String[] outdirStr = Functions.has(config_fn_ssqltool, "outdirHistory").split(",");
        for(int i = 0; i < outdirStr.length; i++){
            GlobalEnv.outdirModel.addElement(outdirStr[i]);
        }
        if (!has(config_fn_ssqltool, "encoding").equals("")) {
    		GlobalEnv.encodingModel.setSelectedItem(has(config_fn_ssqltool, "encoding"));	
		} else {
			GlobalEnv.encodingModel.setSelectedItem(GlobalEnv.encoding_comboData[0]);
		}
		try {
			int text_size = Integer.parseInt(has(config_fn_ssqltool, "text_size"));
			Edit.size = text_size;
			Edit.setText();
      		FrontEnd.label_size.setFont(Edit.defaultEditFont);
      		FrontEnd.label_size.setText(Edit.size+"");
		} catch (Exception e) { }
        Edit.setLinePane();
    }
    /******************************************************************************************************************/


	public static String getWorkingDir(){
		String workingDir = new File(GlobalEnv.EXE_FILE_PATH).getAbsolutePath(); // 実行jarファイルの絶対パスを取得
//		if(isWindows()){
//			if (workingDir.contains(":") && workingDir.startsWith("C:")) {// ビルドバスの追加を行うと参照ライブラリ内のファイルのパスも付け加えてしまう仕様らしいので、:移行カット
//				// "C:"を取り出す
//				String tmp = workingDir.substring(0, 1);
//				workingDir = workingDir.substring(workingDir.indexOf(":"));
//				workingDir = tmp + workingDir;
//			}
//		} else {
			if (!isWindows() && workingDir.contains(":")) {// ビルドバスの追加を行うと参照ライブラリ内のファイルのパスも付け加えてしまう仕様らしいので、:移行カット
				workingDir = workingDir.substring(0, workingDir.indexOf(":"));
			}
//		}
		if (workingDir.endsWith(".jar")) { // jarファイルを実行した場合（Eclipseから起動した場合は入らない）
			workingDir = workingDir.substring(0, workingDir.lastIndexOf(GlobalEnv.OS_FS));
		}
		return workingDir;
	}

	// ライブラリの絶対パスを取得
	public static String getClassPath() {
		String classPath = ".";
		try {
			// String libs = new
			// File(System.getProperty("java.class.path")).getAbsolutePath();
			// //実行ファイルのパスを取得
			// String fs = System.getProperty("file.separator"); //ファイル区切り文字を取得
//			String libs = new File(GlobalEnv.EXE_FILE_PATH).getAbsolutePath(); // 実行jarファイルの絶対パスを取得
//			if(isWindows()){
//				if (libs.contains(":") && libs.startsWith("C:")) {// ビルドバスの追加を行うと参照ライブラリ内のファイルのパスも付け加えてしまう仕様らしいので、:移行カット
//					// "C:"を取り出す
//					String tmp = libs.substring(0, 1);
//					libs = libs.substring(libs.indexOf(":"));
//					libs = tmp + libs;
//				}
//			} else {
//				if (libs.contains(":")) {// ビルドバスの追加を行うと参照ライブラリ内のファイルのパスも付け加えてしまう仕様らしいので、:移行カット
//					libs = libs.substring(0, libs.indexOf(":"));
//				}
//			}
//			if (libs.endsWith(".jar")) { // jarファイルを実行した場合（Eclipseから起動した場合は入らない）
//				libs = libs.substring(0, libs.lastIndexOf(GlobalEnv.OS_FS));
//			}
			String workingDir =  getWorkingDir();
			File targetDir = new File(workingDir + GlobalEnv.OS_FS + "libs");
			GlobalEnv.resultPane2.setText(workingDir);

			if (targetDir.exists() && targetDir.isDirectory()) {
				File[] fileList = targetDir.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					if (fileList[i].getName().endsWith(".jar")) {
						classPath += GlobalEnv.OS_PS + fileList[i].getAbsolutePath();
					}
				}
			}
		} catch (Exception e) {
		}
		return classPath;
	}
	// 生成されたファイルの絶対パスファイル名を返す
	public static String getHTMLAbsolutePath(String folder, String filename,
			String extension) {
		try {
			String outdir = getOutdir(folder);

			if (filename.contains(GlobalEnv.OS_FS))
				filename = filename.substring(filename.lastIndexOf(GlobalEnv.OS_FS));
			if (filename.endsWith(".sql")) {
				filename = filename.substring(0, filename.indexOf(".sql"));
			} else if (filename.endsWith(".ssql")) {
				filename = filename.substring(0, filename.indexOf(".ssql"));
			}
			filename += "." + extension;
			return outdir + filename;
		} catch (Exception e) {
			return "";
		}
	}

	// 生成されたファイルの出力先を返す
	public static String getOutdir(String folder) {
		String config_fn = GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile;
		String outdir = change(has(config_fn, "outdir"));
		if (outdir.equals(""))
			outdir = folder;
		else
			outdir = outdir.replace("~" + GlobalEnv.OS_FS, GlobalEnv.USER_HOME + GlobalEnv.OS_FS); // "~(チルダ)"を含んでいた場合は、USER_HOMEへ変換
		return outdir;
	}

	// filenameが「target=」を持っている場合、その右辺の値を返す
	private static HashMap<String,HashMap<String,String>> file_targetMap = new HashMap<String,HashMap<String,String>>();
	public static String has(String filename, String target) {
		String ret = "";

		try {
			String fn = new File(filename).getName();
			HashMap<String,String> targetMap = new HashMap<String,String>();
			if(file_targetMap.containsKey(fn)){
				// 2回目以降 Mapから取得
				targetMap = file_targetMap.get(fn);
			}else{
				// 初回のみ ファイル読み込み
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), GlobalEnv.getEncoding()));
				String line;
				while ((line = br.readLine()) != null) {
					if(line.contains("=")){
						String k = line.substring(0, line.indexOf("=")).trim();
						String v = line.substring(line.indexOf("=") + 1).trim();
						targetMap.put(k, v);
						//System.out.println(k+" = "+v);
					}
				}
				br.close();
				file_targetMap.put(fn, targetMap);
			}
			
			if(targetMap.containsKey(target)) {
				ret = targetMap.get(target);
			}
		} catch (IOException e) {
		}
		return ret;
	}

	// 作業フォルダ内にある指定したファイルを読み込む
	public static String myfileReader(String filename){
		String str = "";
		if(!filename.equals("")){
		try {
			// ファイルから読み込む仕組みを作成
//			BufferedReader br = new BufferedReader(new FileReader(GlobalEnv.folderPath + GlobalEnv.OS_FS + filename));
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(GlobalEnv.folderPath + GlobalEnv.OS_FS + filename), GlobalEnv.getEncoding()));
			// 1行分の読み込んだデータを格納する変数
			String temp = "";
			// 行が存在する間ループする
			while ((temp = br.readLine()) != null) {
				// データを改行付きで追加
				str += temp + GlobalEnv.OS_LS;
			}
			// ファイルを閉じる
			br.close();
		} catch (Exception ex) {
			System.out.println("ファイルエラー");
		}
		// 最後に付けた1個無駄な改行を消す
		return str.substring(0, str.lastIndexOf(GlobalEnv.OS_LS));
		} else {
			return "";
		}
	}

	// 指定されたPathを開く（Pathによって起動するソフトウェアが変わる）
	// URL・File・フォルダ等を開くことが出来る
	public static void open(String Path) {
		if (!Desktop.isDesktopSupported()) {
			JOptionPane.showMessageDialog(null, "サポートされていません", "警 告",
					JOptionPane.WARNING_MESSAGE);
			if (GlobalEnv.radio2[0].isSelected())
				JOptionPane.showMessageDialog(null, "サポートされていません", "警 告",
						JOptionPane.WARNING_MESSAGE);// masato
			else
				JOptionPane.showMessageDialog(null, "It's not supported.",
						"WARNING！", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
//			Path = "https://www.google.co.jp/";
			if (Path.startsWith("http://") || Path.startsWith("https://") || Path.startsWith("mailto:")){
				try {
					Desktop.getDesktop().browse(new URI(Path));
				} catch (IOException e1) {
					// e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// e1.printStackTrace();
				}
			}
			else {
				if (isLinux()) { // Linuxだった場合
					if (new File(Path).isFile()){
						//実習環境用
						if (Path.contains("public_html")){
							String[] tmp = Path.split(GlobalEnv.OS_FS, 0);
							String tmp1 = Path.split("public_html", 0)[1];
							Path = "http://user.keio.ac.jp/~" + tmp[3] + tmp1;
						}
						// ファイルならそれをブラウザで開く
						// 現状では実習専用 コマンドを指定してLinuxのbashシェルスクリプトから実行してブラウザを起動
						try {
							Runtime.getRuntime().exec("firefox " + Path);
							// System.exit(0);
						} catch (Exception e) {
							// System.exit(1);
						}

					} else {
						try {
							Desktop.getDesktop().open(new File(Path));
						} catch (IOException e) {
							// e.printStackTrace();
						}
					}
				} else {
					if(GlobalEnv.urlCombo.getSelectedItem().equals("")){
						try {
							Desktop.getDesktop().open(new File(Path));
						} catch (IOException e) {
							 e.printStackTrace();
						}
					} else {
						try {
							String file = Path.substring(Path.lastIndexOf(GlobalEnv.OS_FS), Path.length());
							Path = (String) GlobalEnv.urlCombo.getSelectedItem();
//							System.out.println("http://localhost/Sites" + Path);
							Desktop.getDesktop().browse(new URI(Path + file));
						} catch (IOException e) {
							 e.printStackTrace();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (java.lang.IllegalArgumentException e) {
			// JOptionPane.showMessageDialog(null,"\""+Path+"\" does not exist.","Open error",JOptionPane.WARNING_MESSAGE);
		}
	}

	// 先頭に~がついていたらUSER_HOMEに変換して返す
	public static String change(String str) {
		int length = str.length();
		if (str.startsWith("~")) {
			str = str.substring(1, length);
			str = GlobalEnv.USER_HOME + str;
		}
		return str;
	}

	public static void deleteFile(String folderPath, String fileName){
		File file = new File(folderPath + GlobalEnv.OS_FS +fileName);
		if(file.exists()){
			file.delete();
		}
	}
	public static void addPanel(JPanel p, Component c2, int x, int y, int w, int h) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        GlobalEnv.gbl.setConstraints(c2, gbc);
        p.add(c2);
    }

	// FROM句の始まる場所を取得
	public static void searchFrom(){
		String data = GlobalEnv.textPane.getText();
		GlobalEnv.fromStart = GlobalEnv.textPane.getText().length();
		GlobalEnv.fromEnd = GlobalEnv.textPane.getText().length();
		// 大文字小文字の区別なし
		String regex = "\\s(?i)from\\s";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(data);
		while (m.find()) {
		// コメントアウトされていなかったら
			if (!CaretState.judgeComment(m.start())
					&& !CaretState.judgeComment2(m.start())) {
				GlobalEnv.fromStart = m.start();
				GlobalEnv.fromEnd = m.end();
			}
		}
	}

	// WHERE句の始まる場所を取得
	public static void searchWhere(){
		// from句までの文字数
		int index = GlobalEnv.textPane.getText().substring(0, GlobalEnv.fromEnd).length();
		// from句が終わる場所からの文字列
		String data = GlobalEnv.textPane.getText().substring(GlobalEnv.fromEnd);
		GlobalEnv.whereStart = GlobalEnv.textPane.getText().length();
		GlobalEnv.whereEnd = GlobalEnv.textPane.getText().length();
		String regex = "\\s(?i)where\\s";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(data);
		while (m.find()) {
			// コメントアウトされていなかったら
			if (!CaretState.judgeComment(m.start() + index)
					&& !CaretState.judgeComment2(m.start() + index)) {
				GlobalEnv.whereStart = m.start() + index;
				GlobalEnv.whereEnd = m.end() + index;
			}
		}
	}

	// 編集中クエリに存在するテーブルを取得
	public static void getTeble() {
//		// ハッシュセットを利用して重複テーブルを削除
//		HashSet<String> set = new HashSet<String>();

		GlobalEnv.currentTable_array.clear();
		GlobalEnv.alias_array.clear();

		Functions.searchFrom();
		Functions.searchWhere();

		String data = GlobalEnv.textPane.getText();
		data = data.substring(GlobalEnv.fromEnd, GlobalEnv.whereStart);
//		String regex = "\\s(?i)from\\s";
//		String regex2 = "\\s(?i)where\\s";
//		Pattern p = Pattern.compile(regex);
//		Pattern p2 = Pattern.compile(regex2);
//
//		Matcher m = p.matcher(data);
//
//		if (m.find()) {
//			int start = m.start();
//			int end = m.end();
//			// System.out.println("マッチしました");
//			// System.out.println("位置は " + start + " to " + end);
//			data = data.substring(end).trim();
//			// System.out.println(data);
//			Matcher m2 = p2.matcher(data);
//
//			if (m2.find()) {
//				int start2 = m2.start();
//				int end2 = m2.end();
//				// System.out.println("マッチしました");
//				// System.out.println("位置は " + start2 + " to " + end2);
//				data = data.substring(0, start2).trim();
//			}
//			 System.out.println("data= " + "\"" + data + "\"");
//		}
		String[] strAry = data.split(",");
		for (int i = 0; i < strAry.length; i++) {
			String alias = "";
			strAry[i] = strAry[i].trim();
//			System.out.println("strAry[i]=" + strAry[i]);

			// エイリアスが設定されていたら
			if(strAry[i].contains(" ")){
				alias = strAry[i].substring(strAry[i].lastIndexOf(" ")).trim();
				strAry[i] = strAry[i].substring(0, strAry[i].indexOf(" ")).trim();
			// エイリアスが設定されていなかったらテーブル名そのものをエイリアスとして処理
			} else {
				alias = strAry[i].trim();
			}
			if (strAry[i].endsWith(";")) {
				strAry[i] = strAry[i].substring(0, strAry[i].indexOf(";")).trim();
			}
			if (alias.endsWith(";")) {
				alias = alias.substring(0, alias.indexOf(";")).trim();
			}
			GlobalEnv.alias_array.add(alias);
			GlobalEnv.currentTable_array.add(strAry[i]);

			// エイリアスが設定されていたらテーブル名以降カット
//			if (strAry[i].lastIndexOf(" ") != -1) {
//				strAry[i] = strAry[i].substring(0, strAry[i].lastIndexOf(" ")).trim();
//			}
//			if (strAry[i].endsWith(";")) {
//				strAry[i] = strAry[i].substring(0, strAry[i].lastIndexOf(";")).trim();
//			}
//			set.add(strAry[i]);
		}
//		System.out.println("alias= " + GlobalEnv.alias_array);
//		System.out.println("list= " + list);
		//Setの値を取り出すためにsetをArrayListに変換
        //ArrayListのコンストラクタ引数にsetを渡すことで実現
//        ArrayList  <Object> tablenameList = new ArrayList <Object>(list);

        //Listの中身をfor文で取得
//        for(int i = 0; i < tablenameList.size(); i++){
//                String name = (String)tablenameList.get(i);
////                System.out.println(i + ":" + name);
////                System.out.println(GlobalEnv.tabledata.get(name));
//        }
	}

	// 属性を取り出して配列に入れなおし、ポップアップを生成
	public static void insertAttriburtes(List<String> list) {
		TreeSet<String> attribute = new TreeSet<String>();
		for (int i = 0; i < list.size(); i++) {
			String name = (String) list.get(i);
			try {
				// 指定したテーブル名の属性を全て取得
				String attributes = GlobalEnv.tabledata.get(name).toString();
				attributes = attributes.substring(1, attributes.length() - 1);

				// System.out.println(attributes);
				// あるテーブルの属性のリストを配列に格納
				String[] attributeAry = attributes.split(", ");
				for (int j = 0; j < attributeAry.length; j++) {
					attributeAry[j] = attributeAry[j].substring(0, attributeAry[j].indexOf(" "));
					// TreeSetに入れて重複するスキーマを削除
					attribute.add(attributeAry[j]);
				}
			} catch (NullPointerException e) {
//				FrontEnd.stateTimer.start();
//				FrontEnd.filestateLabel.setForeground(Color.RED);
//				FrontEnd.filestateLabel.setText("利用できるテーブルがありません");
			}
		}
		GlobalEnv.attribute_menuItem = new JMenuItem[attribute.size()];

		GlobalEnv.attribute_array.clear();
        Iterator<String> it = attribute.iterator();
        while (it.hasNext()) {
        	GlobalEnv.attribute_array.add(it.next());
//            System.out.println(it.next());
        }
        GlobalEnv.tableattribute_popup.removeAll();

        GlobalEnv.attribute_menuItem = FrontEnd.setMenuItem(GlobalEnv.tableattribute_popup, GlobalEnv.attribute_array, GlobalEnv.attribute_array.size());
	}

	// pの位置の文字が半角英数値(0～9、a～z、A～Z、_)かどうかを判定
	public static boolean checkChar(int pos, String regex){
		try {
			// キャレットの位置の1つ前の文字を取得
			String s = GlobalEnv.textPane.getText(pos, 1);

//			String regex = "\\w";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(s);

			if (m.find()) {
				 return true;
			} else {
				return false;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void getAliasTable(String alias){
		if(alias.isEmpty()){
			return;
		}
		TreeSet<String> attribute = new TreeSet<String>();
		// TODO　間違ったエイリアスだとエラー出るからあとで直す
		int index = GlobalEnv.alias_array.indexOf(alias);
		String table = GlobalEnv.currentTable_array.get(index);
		String attributes = GlobalEnv.tabledata.get(table).toString();
		attributes = attributes.substring(1, attributes.length() - 1);
//		System.out.println(attributes);
		String[] attributeAry = attributes.split(", ");
		for (int i = 0; i < attributeAry.length; i++) {
			attributeAry[i] = attributeAry[i].substring(0, attributeAry[i].indexOf(" "));
			// TreeSetに入れて重複するスキーマを削除
			attribute.add(attributeAry[i]);
		}
		GlobalEnv.attribute_menuItem = new JMenuItem[attribute.size()];

		GlobalEnv.attribute_array.clear();
        Iterator<String> it = attribute.iterator();
        while (it.hasNext()) {
        	GlobalEnv.attribute_array.add(it.next());
//            System.out.println(it.next());
        }
        GlobalEnv.tableattribute_popup.removeAll();

        GlobalEnv.attribute_menuItem = FrontEnd.setMenuItem(GlobalEnv.tableattribute_popup, GlobalEnv.attribute_array, GlobalEnv.attribute_array.size());
	}

	// キャレットの手前の文字列のエイリアスを解析しそのエイリアスを返す
	public static String searchAlias(){
		String alias = "";
		int count = 0;
		for(int i = GlobalEnv.p-1; i > 0; i--){
			try {
				if(GlobalEnv.textPane.getText(i, 1).equals(".")){
					for(int j = i-1; j > 0; j--){
						if(!Functions.checkChar(j, "\\w")){
							alias = GlobalEnv.textPane.getText(j + 1, count);
							return alias;
						}
						count++;
					}
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return alias;
	}


	// 文脈読んで挿入もしくは新しいポップアップ生成
	// 今後改良して引数追加していけばdecorationやfunctionにも応用できるかも
	public static JMenuItem[] matchCheck(String str, List<String> array, List<String> array2, List<String> partArray, JMenuItem[] menuItem, JPopupMenu popupMenu) {
		GlobalEnv.flag = 0;

		String attribute = "";
		String partattribute = "";
		int count = 0;
		array2.clear();
		partArray.clear();

		// もとの一覧（テーブル、属性など）の数だけ回して開始文字（str）が一致した単語の後半部分を取り出す + 一致した個数（補完候補）をカウント
		for (int i = 0; i < array.size(); i++) {
			attribute = array.get(i);
			if ((attribute.startsWith(str))
					&& str.length() != attribute.length()) {
				partattribute = attribute.substring(str.length());
				count++;
				// ここで新しいポップアップ作るためにAyyayかなんかにいれとく
				array2.add(attribute);
				partArray.add(partattribute);
			}
		}
		// 候補が1個しかなかったら挿入
		if (count == 1) {
			try {
				GlobalEnv.doc.insertString(GlobalEnv.p, partattribute, CaretState.plane);
				GlobalEnv.flag = 0;
				return null;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		// 複数候補があればポップアップを別に生成
		} else if(count > 1) {
//			System.out.println(array2);
			GlobalEnv.flag = 1;
			popupMenu.removeAll();
			menuItem = new JMenuItem[count];
			menuItem = FrontEnd.setMenuItem(popupMenu, array2, array2.size());
			return menuItem;
		} else {
			GlobalEnv.flag = 0;
		}
		return menuItem;
	}

	public static Rectangle getRect() {
        Rectangle rect = new Rectangle();
        try{
            rect = GlobalEnv.textPane.modelToView(GlobalEnv.textPane.getCaretPosition());
        }catch(BadLocationException ble) {
            ble.printStackTrace();
        }
        return rect;
    }

	static void checkAttribute(){
		String text = "";
		String text2 = "";
		// from句までの文字列を取得
		try {
			text = GlobalEnv.textPane.getText(0, GlobalEnv.fromStart);
			text += GlobalEnv.textPane.getText().substring(GlobalEnv.whereEnd);
//			System.out.println(text);
//			System.out.println(GlobalEnv.whereEnd);
//			text2 = GlobalEnv.textPane.getText().su
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		// 半角英数値(0～9、a～z、A～Z、_)が一文字以上の文字列にマッチ
//		String regex1 = "\\w+";
//		String regex1 = "[\\s,=]+?([0-9a-zA-Z_]+\\.[0-9a-zA-Z]+)[\\s,=$]+?";
		String regex1 = "[^0-9a-zA-Z_]??([0-9a-zA-Z_]+\\.[0-9a-zA-Z]+)[^0-9a-zA-Z_\\.]??";
//		String regex2 = "(\".*\")";
	    Pattern p1 = Pattern.compile(regex1);
//	    Pattern p2 = Pattern.compile(regex2);
	    Matcher m = p1.matcher(text);
//	    Matcher m2 = p2.matcher(text);
	    while(m.find()){
	        int start = m.start();
	        int end = m.end();
	        String matchedString = m.group(1);
//	        System.out.println(matchedString);
//	        System.out.println("start:" + start + "文字列：" + text.substring(start + 1, start + 4));
	     }
	}

	// キャレットが装飾演算子"@"の{}内かどうかを判定
	public static boolean decorationCheck(int p){
		// キャレットの1つ手前の文字から先頭までさかのぼってチェック
		for(int i = p-1; i > 0; i--){
			try {
				if(GlobalEnv.textPane.getText(i, 1).equals("}")){
					return false;
				}
				else if(GlobalEnv.textPane.getText(i, 1).equals("{")){
					for(int j = i-1; j >= 0; j--){
						// "{"の手前が改行、空白、タブならスルー
						if(checkChar(j, "\\s")){
							continue;
						} else {
							if(GlobalEnv.textPane.getText(j, 1).equals("@")){
								return true;
							} else {
								return false;
							}
						}
					}
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void searchWordRegex(String target, int start) {
//		System.out.println(target);
		try {
			String text = GlobalEnv.doc.getText(start, (GlobalEnv.doc.getLength() - start));
			if(!GlobalEnv.caseSensitivity[0].isSelected()){
				text = text.toLowerCase();
				target = target.toLowerCase();
			}
			Matcher m = Pattern.compile(target).matcher(text);

			if (m.find()) {
//				System.out.println("スタート位置:" + GlobalEnv.searchStart);
				GlobalEnv.textPane.setSelectionStart((m.start() + start));
				GlobalEnv.textPane.setSelectionEnd((m.end() + start));
//				System.out.println("start :" + (m.start() + GlobalEnv.searchStart));
//				System.out.println("end :" + (m.end() + GlobalEnv.searchStart));
//				System.out.println("group :" + m.group());
//				System.out.println("--------------------");
				GlobalEnv.searchStart += m.end();
			} else {
				if(GlobalEnv.searchStart == 0){
//					System.out.println("検索結果なし");
				} else {
					GlobalEnv.searchStart = 0;
					searchWordRegex(target, GlobalEnv.searchStart);
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	public static void searchWord(String target, int start) {
			// 検索対象の文字列
			String text = GlobalEnv.textPane.getText();
			text = text.substring(start);

			if(!GlobalEnv.caseSensitivity[0].isSelected()){
				text = text.toLowerCase();
				target = target.toLowerCase();
			}

			if(text.indexOf(target) != -1){
				GlobalEnv.textPane.setSelectionStart((text.indexOf(target) + start));
				GlobalEnv.textPane.setSelectionEnd((text.indexOf(target) + target.length() + start));
				GlobalEnv.searchStart = GlobalEnv.searchStart + text.indexOf(target) + target.length();
//				System.out.println(GlobalEnv.searchStart);
//				System.out.println("--------------------");

			} else {
				if(GlobalEnv.searchStart == 0){
//					System.out.println("検索結果なし");
				} else {
					GlobalEnv.searchStart = 0;
					searchWord(target, GlobalEnv.searchStart);
				}
			}
	}



}
