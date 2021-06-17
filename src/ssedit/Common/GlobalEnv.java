package ssedit.Common;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.plaf.TextUI;
import javax.swing.text.DefaultStyledDocument;

import ssedit.GUI.History;


public class GlobalEnv {
	// システムのデフォルトのコマンド修飾キーを取得（WindowsならCTRL_MASK, MacOSならMETA_MASKになる）
	public static int osShortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	/* [重要] getPropertyメソッドによって、システムプロパティの値(OS、ファイル区切り文字、ホーム、言語など)を取得 */
	/* システムプロパティ値一覧の取得: System.getProperties().list(System.out); */
	public final static String USER_HOME = System.getProperty("user.home"); // ユーザのホームディレクトリ
	public final static String OS = System.getProperty("os.name"); // OSの名前("Mac OS X" 等)
	public final static String OS_LS = System.getProperty("line.separator"); // OSごとの改行コード(Windows:"\r\n",Mac:"\r",UNIX:"\n"
																		// 等)
	public final static String OS_FS = System.getProperty("file.separator"); // OSごとのファイル区切り文字(Windows:"\" , MacとLinux:"/"
																		// 等)
	public final static String OS_PS = System.getProperty("path.separator"); // OSごとのパス区切り文字(Windows";"
																		// ,
																		// MacとLinux":"
																		// 等)
	public final static String EXE_FILE_PATH = System.getProperty("java.class.path"); // 実行ファイルのパス(実行jarファイル等がどこにあるか)を取得
																				// (※注意:相対パスで返ってくる場合あり)
	// final String USER_LANGUAGE = System.getProperty("user.language");
	// //ユーザの言語(日本語:ja) 日本語・英語切り替え機能を付けるときに使用？
	// final String USER_COUNTRY = System.getProperty("user.country");
	// //ユーザの国名(日本:JP) 日本語・英語切り替え機能を付けるときに使用？

	public static String configFile = "";
	public static String folderPath = "";
//	static String folderPath2 = "";
	// 出力先
	public static String outdirPath = "";
	// linkforeachに分けるときのatt
	public static String linkAtt = "";


	public static JTextField config_driverField = new JTextField();
	public static JTextField config_dbField = new JTextField();
	public static JTextField config_hostField = new JTextField();
	public static JTextField config_portField = new JTextField();
	public static JTextField config_instanceField = new JTextField();	// goto 20200728  SQL Server 
	public static JTextField config_optionsField = new JTextField();	// goto 20200728  SQL Server 
	public static JTextField config_userField = new JTextField();
//	public static JTextField config_passwordField = new JTextField();
	public static JTextField config_passwordField = new JPasswordField();
	public static JTextField rhome_pathField = new JTextField();
//	static JTextField config_outdirField = new JTextField();
//	static JTextField url_textField = new JTextField();


	public static JTree dbTree = new JTree();



	public static GridBagLayout gbl = new GridBagLayout();

	public static Hashtable<String, Object> tabledata = new Hashtable<String, Object>();// スキーマの一覧を載せる


	public static List<String> decorations_array = new ArrayList<String>();
	public static List<String> functions_array = new ArrayList<String>();
	public static List<String> media_array = new ArrayList<String>();
//	static Vector<String> decorations_array = new Vector<String>();
//	static Vector<String> functions_array = new Vector<String>();
	public static List<String> file_array = new ArrayList<String>();
	public static List<String> newFile_array = new ArrayList<String>();
	public static List<String> attribute_array = new ArrayList<String>();
	// partstrに該当する属性（フル）の候補
	public static List<String> attribute_array2 = new ArrayList<String>();
	// partstrに該当する属性（partstr以降）の候補
	public static List<String> partattribute_array = new ArrayList<String>();
	public static List<String> parttable_array = new ArrayList<String>();
	// データベース内のすべてのテーブル一覧
	public static List<String> table_array = new ArrayList<String>();
	// From句以降で使われているテーブル一覧
	public static List<String> currentTable_array = new ArrayList<String>();
	public static List<String> table_array2 = new ArrayList<String>();
	public static List<String> alias_array = new ArrayList<String>();
//	static List<Integer> percent_array = new ArrayList<Integer>();

	//分割生成されたlinkファイルにあるattの場所を格納
	public static List<Integer> linkattpos_array = new ArrayList<Integer>();
	public static List<Integer> linkattpos_array2 = new ArrayList<Integer>();
	//分割生成されたforeachファイルにあるatt(FOREACHの後)の場所を格納
	public static List<Integer> foreachpos_array = new ArrayList<Integer>();
	//分割生成されたlinkファイルのクエリを格納
	public static List<String> link_array = new ArrayList<String>();
	//分割生成されたforeachファイルのクエリを格納
	public static List<String> foreach_array = new ArrayList<String>();
	public static List<String> attInput = new ArrayList<String>();
	public static String fullLink = "";




//	static BasicComboPopup decoration_popup = new BasicComboPopup();// 装飾子のポップアップ
	public static JPopupMenu decoration_popup = new JPopupMenu();// 装飾子のポップアップ
	public static JPopupMenu function_popup = new JPopupMenu();// 関数のポップアップ
	public static JPopupMenu tableattribute_popup = new JPopupMenu();// 装飾子のポップアップ
	public static JPopupMenu table_popup = new JPopupMenu();// テーブル一覧のポップアップ
	public static JPopupMenu file_popup = new JPopupMenu();// ツリーファイルの右クリック時のポップアップ
//	static JPopupMenu newFile_popup = new JPopupMenu();// ツリーの右クリック時のポップアップ(新規作成)
	public static JMenuItem[] attribute_menuItem = new JMenuItem[0];
	public static JMenuItem[] attribute_menuItem2 = new JMenuItem[0];
	public static JMenuItem[] table_menuItem = new JMenuItem[0];
	public static JMenuItem[] table_menuItem2 = new JMenuItem[0];
	public static JMenuItem[] file_menuItem = new JMenuItem[0];
	public static JMenuItem[] mediaList_menuItem = new JMenuItem[0];
	public static JMenuItem[] decoration_menuItem, function_menuItem;// 各ポップアップアイテムの配列
	public static JMenu newFile_menuItem = new JMenu("新規作成");


	public static int flag = 0;
	// クエリが裏側で実行可能な状態であるかどうか　true→裏側で実行終わってる　false→裏側で実行中
	public static boolean runningFlag = true;

	// ドライバ
	public static String[] driver_comboData = {"", "postgresql", "sqlite", "mysql", "db2", "sqlserver"};
	public static DefaultComboBoxModel driverModel = new DefaultComboBoxModel(driver_comboData);
	
	// 文字コード
	public static String[] encoding_comboData = {"UTF-8", "Shift-JIS", "EUC-JP"};
	public static DefaultComboBoxModel encodingModel = new DefaultComboBoxModel(encoding_comboData);
	public static String getEncoding() {
		return GlobalEnv.encodingModel.getSelectedItem().toString();
	}
	

	public static int searchStart = 0;


	public static JTextPane textPane = new JTextPane() {
		// textPaneでも横スクロールが出現
		@Override
		public boolean getScrollableTracksViewportWidth() {
			Object parent = getParent();
			if (parent instanceof JViewport) {
				JViewport port = (JViewport) parent;
				int w = port.getWidth(); // 表示できる範囲(上限)

				TextUI ui = getUI();
				Dimension sz = ui.getPreferredSize(this); // 実際の文字列サイズ
				if (sz.width < w) {
					return true;
				}

				}

			return false;
		}

	};



	public static JTextPane resultPane = new JTextPane() {
		// textPaneでも横スクロールが出現
		@Override
		public boolean getScrollableTracksViewportWidth() {
			// キャレットの位置を常に最後の位置に移動
//			resultPane.setCaretPosition(resultPane.getDocument().getLength());

			Object parent = getParent();
			if (parent instanceof JViewport) {
				JViewport port = (JViewport) parent;
				int w = port.getWidth(); // 表示できる範囲(上限)

				TextUI ui = getUI();
				Dimension sz = ui.getPreferredSize(this); // 実際の文字列サイズ
				if (sz.width < w) {
					return true;
				}
			}
			return false;
		}
	};

	public static JTextPane resultPane2 = new JTextPane() {
		// textPaneでも横スクロールが出現
		@Override
		public boolean getScrollableTracksViewportWidth() {
			// キャレットの位置を常に最後の位置に移動
			resultPane2.setCaretPosition(resultPane2.getDocument().getLength());

			Object parent = getParent();
			if (parent instanceof JViewport) {
				JViewport port = (JViewport) parent;
				int w = port.getWidth(); // 表示できる範囲(上限)

				TextUI ui = getUI();
				Dimension sz = ui.getPreferredSize(this); // 実際の文字列サイズ
				if (sz.width < w) {
					return true;
				}
			}
			return false;
		}
	};

	public static DefaultStyledDocument doc = null;

	public static DefaultStyledDocument resultDoc = null;

	public static DefaultStyledDocument resultDoc2 = null;

	public static JCheckBox[] regex = new JCheckBox[1];
	public static JCheckBox[] caseSensitivity = new JCheckBox[1];


	public static String currnetTarget = "";
	public static int start = 0;

	public static int p = 0;// キャレットの位置
	public static int fromStart = 0;
	public static int fromEnd = 0;
	public static int whereStart = 0;
	public static int whereEnd = 0;

	public static JRadioButton[] radio1 = new JRadioButton[2]; // goto4
	public static int radio1Selected = 0; // goto4 //どのラジオボタンが選択されていたか
	public static JRadioButton[] radio2 = new JRadioButton[2];
	public static int radio2Selected = 0;
	public static JRadioButton[] optimizerRadio = new JRadioButton[2];
	public static int optimizerRadioSelected = 0;
	public static JRadioButton[] configSaveDirRadio = new JRadioButton[2];
	public static int configSaveDirRadioSelected = 0;


//	public static final JComboBox folderCombo    = History.makeComboBox(Functions.has(USER_HOME + OS_FS + ".ssqltool", "folderPath1"));
	public static final JComboBox folderCombo    = History.makeComboBox(Functions.has(Functions.getConfigSaveDir() + ".ssqltool", "folderPath1"));
	public static DefaultComboBoxModel folderModel = (DefaultComboBoxModel) folderCombo.getModel();
//	public static final JComboBox outdirCombo    = History.makeComboBox(Functions.has(USER_HOME + OS_FS + ".ssql", "outdir"));
	public static final JComboBox outdirCombo    = History.makeComboBox(Functions.has(Functions.getConfigSaveDir() + ".ssql", "outdir"));
	public static DefaultComboBoxModel outdirModel = (DefaultComboBoxModel) outdirCombo.getModel();
//	public static final JComboBox urlCombo = History.makeComboBox(Functions.has(USER_HOME + OS_FS + ".ssqltool", "url"));
	public static final JComboBox urlCombo = History.makeComboBox(Functions.has(Functions.getConfigSaveDir() + ".ssqltool", "url"));
	public static DefaultComboBoxModel urlModel = (DefaultComboBoxModel) urlCombo.getModel();

	
	
	//ログ収集フラグ
	private static boolean loggerFlag = false;	//Default: false(off)
	public static void setLoggerFlag(String[] args) {
		//Default: off
		for (int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("-logger")) {
				loggerFlag = true;
			}
		}
	}
	public static boolean isLoggerOn() {
		return loggerFlag;
	}

}
