package ssedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComponentInputMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import ssedit.Caret.CaretState;
import ssedit.Common.Functions;
import ssedit.Common.GlobalEnv;
import ssedit.DB.DB;
import ssedit.GUI.Edit;
import ssedit.GUI.History;
import ssedit.GUI.Popup;
import ssedit.GUI.Search;
import ssedit.GUI.SimpleView;
import ssedit.GUI.UndoHelper;
import ssedit.LinkForeach.LinkForEach;
import ssedit.SSQL.SSQL_exec;
import ssedit.Tree.Exec;

@SuppressWarnings("serial")
public class Test extends JFrame implements ChangeListener, ItemListener, KeyListener, MouseListener,
        ActionListener, CaretListener, TreeSelectionListener {
    Edit editPanel;

    /**
     * @param args
     * @throws InterruptedException
     * @throws IOException
     */
    // main
    public static void main(String[] args) throws IOException,
            InterruptedException {
//    	System.out.println("XXX");
        new Test();
    }

    /**********************************************************************************************/
    /* GUI処理（レイアウト・アクション・アクセラレータの設定など） */
    /**********************************************************************************************/
//	static BasicComboPopup decoration_popup, function_popup;
//	static JComboBox decorationCombo, functionCombo;

    Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // 画面全体のサイズ
    int x = (int) (d.width * 0.12);
    int y = (int) (d.height * 0.45);

    static DefaultTreeModel treemodel = null;
    static DefaultMutableTreeNode root = null;
    static JTree tree = null;
    static File dir = null;

    protected static DefaultStyledDocument table_doc = null;

    protected StyleContext sc = null, resultSc = null, resultSc2 = null;

    protected static SimpleAttributeSet attr_table = new SimpleAttributeSet();
    protected static SimpleAttributeSet attr_attribute = new SimpleAttributeSet();

    JTabbedPane tabbedPane = new JTabbedPane();
    JTabbedPane tabbed_Table = new JTabbedPane();
    JTabbedPane tabbed_Preferences_Pane = new JTabbedPane();

    JTextArea textArea = new JTextArea("ツリーノードをシングルクリックでここにViewを表示" + GlobalEnv.OS_LS
            + "ダブルクリックで上のエディタで編集");

    static JComboBox tabCombo = null, queryCombo = null;
	public static JComboBox driverCombo;
    DefaultComboBoxModel tabsizeModel = new DefaultComboBoxModel();
    static DefaultComboBoxModel querycomboModel = new DefaultComboBoxModel();


    public static JScrollPane table_scrollpane = new JScrollPane();

    final JTextArea config = new JTextArea(); // configファイルをテキストボックスに表示

    JTextField textTabSize = new JTextField();

    // 最大化最小化アイコン
    ImageIcon maximizeIcon = new ImageIcon(get_execDir() + "/image/maximize.png");
    ImageIcon restoreIcon = new ImageIcon(get_execDir() + "/image/restore.png");
    ImageIcon searchIcon = new ImageIcon(get_execDir() + "/image/search.png");
    JButton maximizeButton = new JButton(maximizeIcon);
    JButton restoreButton = new JButton(restoreIcon);
    JButton searchButton = new JButton(searchIcon);
    boolean restore_flag = false;

    //シンプルビュー
//    JCheckBox simpleviewbox = new JCheckBox("Simpleview");
//    boolean simpleview_flag = false;
    JButton simpleviewButton = new JButton("シンプルビュー");

    //インデントボタン
    JButton indentButton = new JButton("整形");

    // masato　追加・変更　最初の2行
    JPanel allPanel1 = new JPanel();
    JPanel allPanel2 = new JPanel();
    JPanel topPanel1 = new JPanel();
    JPanel bottomPanel = new JPanel();
    JPanel bottomPanel1 = new JPanel();
    JPanel selectPanel = new JPanel();
    JPanel selectPanel1 = new JPanel();
    JPanel selectPanel2 = new JPanel();
    JPanel settingPanel = new JPanel();
    JPanel settingPanel2 = new JPanel();
    JPanel setting_Top_Panel = new JPanel();
    JPanel setting_Bottom_Panel = new JPanel();
    JPanel exitPanel1 = new JPanel();
    JPanel exitPanel2 = new JPanel();
    JPanel exitPanel3 = new JPanel();

    JLabel viewfilename_label = new JLabel("");
//	JLabel filename_label = new JLabel("ファイル名：");
    public static JLabel filenameLabel = new JLabel("");
    public static JLabel filestateLabel = new JLabel(" ");
    public static JLabel filestateLabel2 = new JLabel("");
    JLabel foldername_label = new JLabel("フォルダ名：");
    JLabel tabLabel = new JLabel("インデント幅：");
    JLabel langLabel = new JLabel("　　　　　　　　　Language：");
    JLabel concernLabel = new JLabel("成功時のファイル・フォルダ表示：");
    JLabel manualLabel = new JLabel("SSQLマニュアル：");
    JLabel config_driverLabel = new JLabel("ドライバー名：");
    JLabel config_dbLabel = new JLabel("データベース名：");
    JLabel config_hostLabel = new JLabel("ホスト名：");
    JLabel config_userLabel = new JLabel("ユーザ名：");
    JLabel config_outdirLabel = new JLabel("出力先：");
    JLabel config_pathLabel = new JLabel("URL指定：");
    JPanel configPanel = new JPanel();

//	JTextField folderPath_textField1 = new JTextField();
    JTextField folderPath_textField2 = new JTextField();

    JButton changeLang1 = new JButton();
    JButton button1 = new JButton("実行");
    JButton stopButton = new JButton("停止");
    JButton stopButton2 = new JButton("停止");

    //TODO
    JButton linkforeachButton = new JButton("％分割");

    // halken
    // JButton ssvisual = new JButton("SSvisual");

    JButton exit_button1 = new JButton("終了");
    JButton exit_button2 = new JButton("終了");
    JButton exit_button3 = new JButton("終了");
//	JButton folder_button = new JButton("フォルダ選択"); // フォルダ選択ボタン
    JButton selectoutdir_button = new JButton("フォルダ選択"); // フォルダ選択ボタン
    JButton folder_exe_button = new JButton("実行");
    JButton select_button = new JButton("Viewerを表示");
    JButton select_button2 = new JButton("作業フォルダ");

    JButton manual_button = new JButton("マニュアル");
    ImageIcon icon = new ImageIcon(get_execDir() + GlobalEnv.OS_FS + "image"+ GlobalEnv.OS_FS +"icon.jpg");
    JButton button3 = new JButton(); // 保存ボタンを押すとconfig.ssqlの内容を上書き保存
    public static JButton button4 = new JButton(); // 隠し保存ボタン

//	String[] combodata = { "クエリの新規作成", "HTML", "Mobile_HTML5", "PDF", "XML",
//			"CSV", "SWF", "X3D" };
    Vector<String> combodata = new Vector<>();
    public static String str_attribute = "";
    // static String[] function_strArray;
    static String decorationList;// 装飾子のxmlファイルの絶対パス
    static String functionList;// 関数のリストを配列で格納
    // 指定したターゲットのテキストを格納する配列
    // static String[] decorations = new String[100];



    int tabIndex = 0;

    // ファイル表示のクリック関係
    int delay = 250, delay2 = 200, readDelay = 1000, delay4 = 3000; // milli seconds ...
                                                    // 0.25秒（タイマ待機時間）
    boolean wasDouble = false;
    Timer timer = null, timer2 = null, timer3 = null;

    public static Timer stateTimer = null;
    static String tmp = "";
    protected static String[] errorStr = { "", "" };

    // クリックでツリーから読み込んだファイル名とその中身
    static String treefileData = "";
    static String treefileName = "";

    // クリックした際にエディタに残っているファイル名とその中身
    public static String currentfileName = "";
//	String currentData = "";

    int tabSize = 3;
//	int currentState = 0;
    static int i = 0;
    static Integer decoration_Count = new Integer(0);// 装飾子リストなどのファイル内の行数、つまり装飾子などの数
    Integer function_Count = new Integer(0);
    int file_Count = 0;
    // ツリー右クリックで出すポップアップのアイテム数（新規作成）
    int itemCount = 0;
    String test = "test";

    protected static String libsClassPath = "";
    protected static String ssqlExecLogs = "";

    // 現在編集してるクエリの中身
    public static String currentfileData = "";

    DefaultMutableTreeNode current = null;
    private final JButton button_1 = new JButton("拡大");
    private final JLabel label = new JLabel("文字サイズ ：");
    private final JTextField textField = new JTextField();
    private final JButton button_2 = new JButton("保存");


    public Test() {
    	textField.setBounds(360, 5, 66, 26);
    	textField.setColumns(10);
        combodata.add("クエリの新規作成");
        combodata.add("HTML");
        Popup.getTagTexts2(Functions.getWorkingDir() + GlobalEnv.OS_FS + "XML" + GlobalEnv.OS_FS + "media_list.xml", "name", combodata);
        libsClassPath = Functions.getClassPath();
        GlobalEnv.regex[0] = new JCheckBox("正規表現");
        GlobalEnv.caseSensitivity[0] = new JCheckBox("大/小文字の区別");

//		StyleConstants.setBold(CaretState.attr, true);
        StyleConstants.setBackground(CaretState.attr, Color.YELLOW); // 背景の色
        StyleConstants.setForeground(CaretState.commentAttr, Color.GRAY);
        StyleConstants.setForeground(CaretState.errAttr, new Color(248, 6, 6));

//		Common.Test();
        editPanel = new Edit();
        sc = new StyleContext();
        GlobalEnv.doc = new DefaultStyledDocument(sc);
        GlobalEnv.textPane.setDocument(GlobalEnv.doc);
        resultSc = new StyleContext();
        GlobalEnv.resultDoc = new DefaultStyledDocument(resultSc);
        GlobalEnv.resultPane.setDocument(GlobalEnv.resultDoc);
        resultSc2 = new StyleContext();
        GlobalEnv.resultDoc2 = new DefaultStyledDocument(resultSc2);
        GlobalEnv.resultPane2.setDocument(GlobalEnv.resultDoc2);

        driverCombo = new JComboBox(GlobalEnv.driverModel);


        // 前回の情報（開いていたフォルダ・ファイル）を読み込んで反映
        reflectSSQLtoolInfo();
        // クエリ新規作成のコンボボックス
        querycomboModel = new DefaultComboBoxModel((Vector)combodata);
        queryCombo = new JComboBox(querycomboModel);
        queryCombo.setPreferredSize(new Dimension(160, 40));
        queryCombo.addItemListener(this);
        queryCombo.setSelectedIndex(0);

        // タブサイズのコンボボックス
        String[] tabNumber = { "1", "2", "3", "4", "5", "6" };
        tabsizeModel = new DefaultComboBoxModel(tabNumber);
        tabCombo = new JComboBox(tabsizeModel);
        tabCombo.addItemListener(this);
        tabCombo.setSelectedIndex(2);
        tabSize = 3;

        CaretState.changeTabSize(tabSize, GlobalEnv.textPane, GlobalEnv.doc);

        /* CaretListenerをセット */
        GlobalEnv.textPane.addCaretListener(this);
        GlobalEnv.textPane.addKeyListener(this);

        decorationList = "decoration_list.xml";
        functionList = "functionlist.xml";
        // 指定したエレメントのテキストを配列に格納
        Popup.getTagTexts(Functions.getWorkingDir() + GlobalEnv.OS_FS + "XML" + GlobalEnv.OS_FS
                + decorationList, "key", GlobalEnv.decorations_array);
        decoration_Count = GlobalEnv.decorations_array.size();

        Popup.getTagTexts(Functions.getWorkingDir() + GlobalEnv.OS_FS + "XML" + GlobalEnv.OS_FS
                + functionList, "name", GlobalEnv.functions_array);
        function_Count = GlobalEnv.functions_array.size();

        GlobalEnv.file_array.add("削除");
        GlobalEnv.file_array.add("ファイル名変更");
        GlobalEnv.file_array.add("複製");
        file_Count = GlobalEnv.file_array.size();
        GlobalEnv.newFile_array.add("新規作成");
        itemCount = GlobalEnv.newFile_array.size();


        GlobalEnv.decoration_menuItem = setMenuItem(GlobalEnv.decoration_popup, GlobalEnv.decorations_array,
                decoration_Count);
        for (int i = 0; i < decoration_Count; i++) {
            GlobalEnv.decoration_menuItem[i].addActionListener(this);
        }
        GlobalEnv.function_menuItem = setMenuItem(GlobalEnv.function_popup, GlobalEnv.functions_array,
                function_Count);
        for (int i = 0; i < function_Count; i++) {
            GlobalEnv.function_menuItem[i].addActionListener(this);
        }
        GlobalEnv.file_menuItem = setMenuItem(GlobalEnv.file_popup, GlobalEnv.file_array, file_Count);
        for (int i = 0; i < file_Count; i++) {
            GlobalEnv.file_menuItem[i].addActionListener(this);
        }

        new Popup();

//		GlobalEnv.newFile_menuItem.add(comp);
//		GlobalEnv.newFile_menuItem.add(comp);
//		GlobalEnv.newFile_menuItem.add(comp);
//		GlobalEnv.newFile_menuItem.add(comp);
//		GlobalEnv.newFile_menuItem.add(comp);
//
//		HTML", "Mobile_HTML5", "PDF", "XML",
//		"CSV", "SWF", "X3D


        manual_button.setBorderPainted(true);
//		Image smallImg = icon.getImage().getScaledInstance(
//				(int) (icon.getIconWidth() * 0.5), -1,// アイコンの大きさの変更
//				Image.SCALE_SMOOTH);
//		ImageIcon smallIcon = new ImageIcon(smallImg);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(y);

        final JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane3.setDividerLocation(x);

        final JSplitPane splitPane4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane4.setDividerLocation(y);

        dir = new File(GlobalEnv.folderPath);

        // ノードの作成
        root = new DefaultMutableTreeNode(dir.getName());
        createNodes(root);
        // JTree オブジェクトの作成
        treemodel = new DefaultTreeModel(root);
        tree = new JTree(treemodel);
         tree.addTreeSelectionListener(this);
        tree.addMouseListener(this); // マウスイベント
        // 1つ目のタブ左上のツリー
        JPanel treePanel = new JPanel();
        treePanel.add(new JLabel("作業フォルダ"));
        final JScrollPane pane = new JScrollPane(tree);
        treePanel.add(pane);
        treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.PAGE_AXIS));// masato追加　縦にパネルを配置

        JScrollPane pane2 = new JScrollPane(textArea);
        JPanel viewPanel = new JPanel();
        textArea.setEditable(false);
        // setLayout(new GridLayout(1, 2));
        viewPanel.add(viewfilename_label);
        viewPanel.add(pane2);
        viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.PAGE_AXIS));// masato追加　縦にパネルを配置

        //add(viewPanel);

        JPanel dbPanel = new JPanel();
        JButton updatedbButton = new JButton("更新");
        dbPanel.add(table_scrollpane);
        dbPanel.add(updatedbButton);
        dbPanel.setLayout(new BoxLayout(dbPanel, BoxLayout.PAGE_AXIS));
        splitPane4.setTopComponent(treePanel);
        splitPane4.setBottomComponent(dbPanel);

//		folderPath_textField1.setEnabled(false);
        folderPath_textField2.setEnabled(false);

        // ファイル名のフォーム
//		folderPath_textField1.setPreferredSize(new Dimension(350, 30));
        folderPath_textField2.setPreferredSize(new Dimension(480, 30));


//		topPanel1.add(folderPath_textField1);

        GlobalEnv.folderCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        String data = (String) GlobalEnv.folderModel.getSelectedItem();
                        GlobalEnv.folderPath = data;
                        folderPath_textField2.setText(GlobalEnv.folderPath);
                        if(!GlobalEnv.folderModel.getSelectedItem().equals("")){
                            GlobalEnv.textPane.setText("");
                            filenameLabel.setText("");
                            currentfileData = "";
                            currentfileName = "";
                            }
                        }
                    dir = new File(GlobalEnv.folderPath);
                    // ノードの作成
                    root = new DefaultMutableTreeNode(dir.getName());
                    createNodes(root);
                    // JTree オブジェクトの作成
                    treemodel = new DefaultTreeModel(root);
                    tree.setModel(treemodel);
                    // History.addItem(GlobalEnv.folderCombo,
                    // GlobalEnv.folderPath, 5);
                }
        });

        GlobalEnv.folderCombo.setPreferredSize(new Dimension((int) (d.width*0.35), 25));
        GlobalEnv.folderCombo.setEditable(true);
        select_button2.setPreferredSize(new Dimension(110, 25));
        topPanel1.add(GlobalEnv.folderCombo);
        topPanel1.add(select_button2);
//		Common.addPanel(topPanel1, GlobalEnv.folderCombo, 0, 0, 4, 1);
//		Common.addPanel(topPanel1, select_button2, 4, 1, 1, 1);
        exitPanel1.add(exit_button1);
        exitPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));

        String result = ("実行結果");
        GlobalEnv.resultPane.setText(result);
        GlobalEnv.resultPane2.setText(result);

        // 編集不可
        GlobalEnv.resultPane.setEnabled(false);
        GlobalEnv.resultPane2.setEnabled(false);
        GlobalEnv.resultPane.setEditable(false);
        GlobalEnv.resultPane2.setEditable(false);
        // table_GlobalEnv.textPane.setEditable(false);//編集不可、コピペは出来る

        GlobalEnv.configFile = ".ssql";
        // .ssqlファイルが存在していればそのまま読み込む
        if(new File(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile).exists()){
            GlobalEnv.configFile = ".ssql";
        // 存在していなければconfig.ssqlを読み込む
        } else {
            GlobalEnv.configFile = "config.ssql";
            // それもなければ以降.ssqlで保存
            if(!new File(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile).exists()){
                GlobalEnv.configFile = ".ssql";
            }
        }
        Functions.reflectConfig();
//		String configFileContents = Common.readFile(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile);
//		if (configFileContents.trim().equals("")) {
//			GlobalEnv.configFile = "config.ssql";
//			configFileContents = Common.readFile(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile);
//			if (configFileContents.trim().equals(""))
//				GlobalEnv.configFile = ".ssql";
//		}

        // textAreaにスクロールボタンの追加
        JScrollPane scrollpane = new JScrollPane(editPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollpane.setPreferredSize(new Dimension(550, 1200));// masato　サイズ変更

        JScrollPane scrollpane2 = new JScrollPane(GlobalEnv.resultPane,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollpane2.setPreferredSize(new Dimension(550, 1200));// masato　サイズ変更
        button3.setBounds(501, 17, 1, 1);

        // 1つ目のタブ
        // masato　パネル名変更等　14行
        button3.setContentAreaFilled(false);
        button3.setBorderPainted(false);
        button3.setPreferredSize(new Dimension(1, 1));
        button4.setContentAreaFilled(false);
        button4.setBorderPainted(false);
        button4.setPreferredSize(new Dimension(1, 1));
        button_2.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        
        bottomPanel1.add(button_2);
        bottomPanel1.add(button1);
        bottomPanel1.add(stopButton);
        stopButton.setEnabled(false);

        /*
        bottomPanel1.add(linkforeachButton);
        bottomPanel1.add(simpleviewButton);
        bottomPanel1.add(indentButton);
        */

        // halken
        // bottomPanel1.add(ssvisual);

        bottomPanel1.add(button4);
        //bottomPanel.add(bottomPanel1);
        allPanel1.add(topPanel1);
        JPanel middlePanel = new JPanel();
        JPanel filenamePanel = new JPanel();
        JPanel filestatePanel = new JPanel();
        filenamePanel.add(filestateLabel2);
        filenamePanel.add(filenameLabel);
        filestatePanel.add(label);
        filestatePanel.add(filestateLabel);

        maximizeButton.setBorderPainted(false);
        searchButton.setBorderPainted(false);

//        Functions.addPanel(middlePanel, filenamePanel, 0, 0, 1, 1);
//        Functions.addPanel(middlePanel, queryCombo, 1, 0, 1, 1);
//        Functions.addPanel(middlePanel, searchButton, 2, 0, 1, 1);
//        Functions.addPanel(middlePanel, maximizeButton, 3, 0, 1, 1);
        middlePanel.add(filenamePanel);
        middlePanel.add(queryCombo);
        middlePanel.add(searchButton);
        middlePanel.add(maximizeButton);

//        Common.addPanel(middlePanel, simpleviewbox, 4, 0, 1, 1);

        allPanel1.add(middlePanel);
        allPanel1.add(filestatePanel);
        button_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });

        filestatePanel.add(button_1);
        allPanel1.add(scrollpane);
        allPanel1.add(bottomPanel1);
        tabbed_Table.add("クエリViewer", viewPanel);
        tabbed_Table.add("実行結果", scrollpane2);
        allPanel2.add(tabbed_Table); // 1つ目のタブの下半分の実行結果を表示する部分
        exitPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));
        allPanel2.add(exitPanel1);
        allPanel1.setLayout(new BoxLayout(allPanel1, BoxLayout.PAGE_AXIS));
        allPanel2.setLayout(new BoxLayout(allPanel2, BoxLayout.PAGE_AXIS));// masato追加　縦にパネルを配置

        folderPath_textField2.setText(GlobalEnv.folderPath);

        splitPane.setTopComponent(allPanel1);
        splitPane.setBottomComponent(allPanel2);
        splitPane3.setRightComponent(splitPane);

        splitPane3.setLeftComponent(splitPane4);

        //? getContentPane().add(splitPane3, BorderLayout.CENTER);

        // 2つ目のタブ関連
        JScrollPane scrollpane4 = new JScrollPane(GlobalEnv.resultPane2,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollpane4.setPreferredSize(new Dimension(0, 1200));

        exitPanel2.add(exit_button2);
        exitPanel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
        selectPanel1.add(foldername_label);
        selectPanel1.add(folderPath_textField2);
        selectPanel2.add(folder_exe_button);
        selectPanel2.add(stopButton2);
        stopButton2.setEnabled(false);
//		selectPanel2.add(folder_button);
        selectPanel.add(selectPanel1);
        selectPanel.add(selectPanel2);
        selectPanel.add(scrollpane4);
        selectPanel.add(exitPanel2);

        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.PAGE_AXIS));

        new UndoHelper(GlobalEnv.textPane);
        new UndoHelper(config);

        // 3つ目のタブ（設定）の内容
        // textAreaにスクロールボタンの追加
        JScrollPane config_scrollPane = new JScrollPane(config,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        config_scrollPane.setPreferredSize(new Dimension(0, 1200));

        //exitPanel3.add(manualLabel);
        //exitPanel3.add(manual_button);

        JLabel cofigFilePath_label = new JLabel(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile);

        if (GlobalEnv.radio1Selected == 1) { // なしがtrueのとき
            GlobalEnv.radio1[0] = new JRadioButton("あり");
            GlobalEnv.radio1[1] = new JRadioButton("なし", true);
        } else {
            GlobalEnv.radio1[0] = new JRadioButton("あり", true);
            GlobalEnv.radio1[1] = new JRadioButton("なし");
        }
        // radio2 = new JRadioButton[2];
        if (GlobalEnv.radio2Selected == 0) { // Japaneseがtrueのとき
            GlobalEnv.radio2[0] = new JRadioButton("Japanese", true);
            GlobalEnv.radio2[1] = new JRadioButton("English");
            repaint();
        } else {
            GlobalEnv.radio2[0] = new JRadioButton("Japanese");
            GlobalEnv.radio2[1] = new JRadioButton("English", true);
            repaint();
        }
        // ButtonGroup でグループ化することにより、グループ内のオン状態のボタンが常にひとつになるように制御することができる
        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(GlobalEnv.radio1[0]);
        bg1.add(GlobalEnv.radio1[1]);
        // １つのグループにまとめる
        JPanel group1 = new JPanel();
        group1.add(concernLabel);
        group1.add(GlobalEnv.radio1[0]);
        group1.add(GlobalEnv.radio1[1]);
        group1.setLayout(new FlowLayout(FlowLayout.LEFT));

        // ボタングループ
        ButtonGroup bglang = new ButtonGroup();
        bglang.add(GlobalEnv.radio2[0]);
        bglang.add(GlobalEnv.radio2[1]);
        JPanel lang = new JPanel();

        lang.setLayout(new FlowLayout());
        lang.add(langLabel);
        lang.add(GlobalEnv.radio2[0]);
        lang.add(GlobalEnv.radio2[1]);
        lang.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel tab = new JPanel();

        tabCombo.setPreferredSize(new Dimension(60, 30));
        // tabCombo.addActionListener(this);

        tab.add(tabLabel);
        tab.add(tabCombo);

        GlobalEnv.radio2[0].addChangeListener(this); // ラジオボタンにチェンジリスナーを登録
        GlobalEnv.radio2[1].addChangeListener(this);

        setting_Top_Panel.add(settingPanel2);
        // configファイルの編集パネル
//		setting_Top_Panel.add(config_scrollPane);
        GlobalEnv.config_driverField.setPreferredSize(new Dimension(150, 30));
        GlobalEnv.config_dbField.setPreferredSize(new Dimension(150, 30));
        GlobalEnv.config_hostField.setPreferredSize(new Dimension(150, 30));
        GlobalEnv.config_userField.setPreferredSize(new Dimension(150, 30));
//		GlobalEnv.config_outdirField.setPreferredSize(new Dimension(100, 30));
//		GlobalEnv.config_outdirField.setEditable(false);

        JPanel p = new JPanel();
        p.add(new JLabel("                    "));
        configPanel.setBounds(350, 18, 0, 0);
        configPanel.setLayout(GlobalEnv.gbl);
        settingPanel.setBounds(355, 18, 0, 0);
        settingPanel.setLayout(GlobalEnv.gbl);
        GlobalEnv.urlCombo.setEditable(true);

        Functions.addPanel(configPanel, cofigFilePath_label, 0, 0, 1, 1);
        Functions.addPanel(configPanel, config_driverLabel, 0, 1, 1, 1);
        Functions.addPanel(configPanel, driverCombo, 1, 1, 1, 1);
        Functions.addPanel(configPanel, p, 2, 1, 1, 1);
        Functions.addPanel(configPanel, config_dbLabel, 3, 1, 1, 1);
        Functions.addPanel(configPanel, GlobalEnv.config_dbField, 4, 1, 1, 1);
        Functions.addPanel(configPanel, config_hostLabel, 0, 2, 1, 1);
        Functions.addPanel(configPanel, GlobalEnv.config_hostField, 1, 2, 1, 1);
        Functions.addPanel(configPanel, config_userLabel, 3, 2, 1, 1);
        Functions.addPanel(configPanel, GlobalEnv.config_userField, 4, 2, 1, 1);
        Functions.addPanel(configPanel, config_outdirLabel, 0, 3, 1, 1);
//		Common.addPanel(configPanel, GlobalEnv.config_outdirField, 1, 3, 3, 1);
        Functions.addPanel(configPanel, GlobalEnv.outdirCombo, 1, 3, 3, 1);
        Functions.addPanel(configPanel, selectoutdir_button, 4, 3, 1, 1);
        Functions.addPanel(configPanel, config_pathLabel, 0, 4, 1, 1);
        Functions.addPanel(configPanel, GlobalEnv.urlCombo, 1, 4, 2, 1);


//		JPanel settingPanel = new JPanel();
        Functions.addPanel(settingPanel, tab, 0, 0, 1, 1);
        Functions.addPanel(settingPanel, lang, 0, 1, 1, 1);
        Functions.addPanel(settingPanel, group1, 0, 2, 1, 1);
        Functions.addPanel(settingPanel, exitPanel3, 0, 3, 1, 1);
        setting_Bottom_Panel.setLayout(null);
//		Common.addPanel(settingPanel, exit_button3, 0, 4, 1, 1);
//		Common.addPanel(settingPanel, button3, 0, 5, 1, 1);


        // masato　追加　5行
        setting_Bottom_Panel.add(configPanel);
        setting_Bottom_Panel.add(settingPanel);
        
        setting_Bottom_Panel.add(textField);
        exit_button3.setBounds(495, 17, 1, 1);
        exit_button3.setContentAreaFilled(false);
        exit_button3.setBorderPainted(false);
        exit_button3.setPreferredSize(new Dimension(1, 1));
        setting_Bottom_Panel.add(exit_button3);
        setting_Bottom_Panel.add(button3);


        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(tabIndex == 2){
                    button3.doClick();
                }
                tabIndex = tabbedPane.getSelectedIndex();
            }
        });

        tabbedPane.add("クエリを実行", splitPane3);// masato　変更
        tabbedPane.add("フォルダを指定して実行", selectPanel);
        tabbedPane.add("設定/Preferences", setting_Bottom_Panel);
        getContentPane().add(tabbedPane);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SuperSQL クエリ実行ツール");


        // データベースを読み込んでツリーを生成
        DB.db();

        // エディタにアクションを追加
        ActionMap am = GlobalEnv.textPane.getActionMap();


        Action popsearchAct = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//				new Search();
                if(GlobalEnv.textPane.getSelectedText() == null){
                    new Search("");
                } else {
                    new Search(GlobalEnv.textPane.getSelectedText());
                }
            }
        };
        Action popfuncAct = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                show_functionPopup(e);
            }
        };
        Action popdecoAct = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
            	System.out.println("jkedclhekcher");
                if(Functions.decorationCheck(GlobalEnv.p)){
                    try {
                        GlobalEnv.doc.insertString(GlobalEnv.p, "@", CaretState.plane);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    try {
                        GlobalEnv.doc.insertString(GlobalEnv.p, "@", CaretState.plane);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }

                    show_decorationPopup(e);

                }
            }
        };

        Action popdecoAct2 = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if(Functions.decorationCheck(GlobalEnv.p)){
                   try{
                	   GlobalEnv.doc.insertString(GlobalEnv.p, ",", CaretState.plane);
                   } catch (BadLocationException e1){
                	   e1.printStackTrace();
                   }
                   show_decorationPopup(e);

                } else {
                    try {
                        GlobalEnv.doc.insertString(GlobalEnv.p, ",", CaretState.plane);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                    show_decorationPopup(e);
                }
//                GlobalEnv.decoration_popup.setVisible(false);
//                GlobalEnv.textPane.requestFocus();
            }
        };

        Action caretcommentAct = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                CaretState.commentout(GlobalEnv.textPane, GlobalEnv.doc);
//        		System.out.println("テーブルリスト：" + GlobalEnv.table_array);
//        		System.out.println("クエリ上で使用されているテーブルリスト：" + GlobalEnv.currentTable_array);
//        		System.out.println("Fromのスタート位置：" + GlobalEnv.fromStart);
//        		Common.checkAttribute();
            }
        };
        Action caretdeleteAct = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                CaretState.deleteRow(GlobalEnv.textPane, GlobalEnv.doc);
            }
        };
        Action caretcopyAct = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                CaretState.copyRow(GlobalEnv.textPane, GlobalEnv.doc);
            }
        };
        Action autocompleteAct = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                CaretState.autocomplete(e);
            }
        };

        am.put("search", popsearchAct);
        am.put("functionPop", popfuncAct);
        am.put("decorationPop", popdecoAct);
        am.put("decorationPop2", popdecoAct2);
        am.put("commentCaret", caretcommentAct);
        am.put("deleteCaret", caretdeleteAct);
        am.put("copyCaret", caretcopyAct);
        am.put("autocompleteCaret", autocompleteAct);
        GlobalEnv.textPane.setActionMap(am);

        KeyStroke k_search = KeyStroke.getKeyStroke(KeyEvent.VK_F, GlobalEnv.osShortcutKey);
        KeyStroke k_function = KeyStroke.getKeyStroke(KeyEvent.VK_K, GlobalEnv.osShortcutKey);
        KeyStroke k_decoration = KeyStroke.getKeyStroke('@');
        KeyStroke k_decoration2 = KeyStroke.getKeyStroke(',');
        KeyStroke k_comment = KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, GlobalEnv.osShortcutKey);
        KeyStroke k_delete = KeyStroke.getKeyStroke(KeyEvent.VK_D, GlobalEnv.osShortcutKey);
        KeyStroke k_copy = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, GlobalEnv.osShortcutKey + InputEvent.ALT_DOWN_MASK);
        if(Functions.isLinux()){
               KeyStroke k_autocomplete = KeyStroke.getKeyStroke(KeyEvent.VK_H, GlobalEnv.osShortcutKey);
            GlobalEnv.textPane.getInputMap().put(k_autocomplete, "autocompleteCaret");
        } else {
            KeyStroke k_autocomplete = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK);
            GlobalEnv.textPane.getInputMap().put(k_autocomplete, "autocompleteCaret");
        }

        GlobalEnv.textPane.getInputMap().put(k_search, "search");
        GlobalEnv.textPane.getInputMap().put(k_function, "functionPop");
//        GlobalEnv.textPane.getInputMap().put(k_decoration, "decorationPop");
//        GlobalEnv.textPane.getInputMap().put(k_decoration2, "decorationPop2");
        GlobalEnv.textPane.getInputMap().put(k_comment, "commentCaret");
        GlobalEnv.textPane.getInputMap().put(k_delete, "deleteCaret");
        GlobalEnv.textPane.getInputMap().put(k_copy, "copyCaret");


        ChangeEvent e = new ChangeEvent(new Object());
        stateChanged(e);

        setSize((int) (d.width * 0.6), (int) (d.height * 0.85));

        setLocationRelativeTo(null); // 起動時に中央表示

        setVisible(true);
        repaint();
        if (Functions.isLinux())
            setResizable(false); // Linuxのときのみフレームを固定(可変不可にする) //goto5
        JPanel panel = new JPanel();

        Action action1, action2, action3, action4, execButton2;

//		timer = new Timer(delay, new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				if (!wasDouble) { // 0.25秒間のあいだにダブルクリックがなかった
//					textArea.setText(treefileData);
//					viewfilename_label.setText(treefileName);
//					tabbed_Table.setSelectedIndex(0);
//				}
//			}
//		});
//		timer.setRepeats(false);

        tmp = "";
        // 0.75秒毎に以下を実行し、繰り返す
        timer2 = new Timer(delay2, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                CaretState.caretComment(GlobalEnv.textPane, GlobalEnv.doc);
                CaretState.caretChange(GlobalEnv.doc, GlobalEnv.p);
//				 caretError(GlobalEnv.textPane, doc, errorStr);
                // クエリに変更がない、もしくは新規作成されていてクエリが存在しない場合
                if((currentfileData.equals(GlobalEnv.textPane.getText()) || (GlobalEnv.textPane.getText().trim().equals("") && filenameLabel.getText().equals("")))){
                    filestateLabel2.setText("");
                } else {
                    filestateLabel2.setText("*");
                }
            }
        });
        timer2.setRepeats(true);
        timer2.start();

//		final HtmlViewer viewer = new HtmlViewer();
//		final Viewer V = new Viewer();
        // 1.5秒毎に以下を実行し、繰り返す
        timer3 = new Timer(readDelay, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!tmp.equals(GlobalEnv.textPane.getText())) {
                    GlobalEnv.outdirPath = Functions.change(Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile, "outdir"));
                    if(GlobalEnv.outdirPath.equals("")){
                        GlobalEnv.outdirPath = GlobalEnv.folderPath;
                    }
					//ExecViewerThread ViewerThread = new ExecViewerThread();
                    //　実行可能であるか
                    if(GlobalEnv.runningFlag){
                        GlobalEnv.runningFlag = false;
						//System.out.println("実行開始：flag = " + GlobalEnv.runningFlag + "　この先進入禁止");
						//ViewerThread.start();
                        tmp = GlobalEnv.textPane.getText();
                    } else {
						//System.out.println("クエリ実行中につき、この先進むべからず！");
                    }
                }
            }
        });
        timer3.setRepeats(true);
        timer3.start();

        // 保存ボタン押したとき、3秒後にラベルを""に書き換える
        stateTimer = new Timer(delay4, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                filestateLabel.setForeground(Color.BLACK);
                filestateLabel.setText(" ");
            }
        });
        stateTimer.setRepeats(false);

        // 1つ目のタブ　[実行]
        button1.addActionListener(action1 = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                // 1.クエリを実行
                    button4.doClick();
                    if(filestateLabel.getText().equals("キャンセルしました") || filestateLabel.getText().equals("ファイル名を入力してください")){
                        return;
                    }
                    if(GlobalEnv.folderModel.getSelectedItem().equals("")){
                        return;
                    }
                    String data = GlobalEnv.textPane.getText(); // テキストエリアから値を得る（文書）
                    if(data.trim().equals("")){
                        stateTimer.start();
                        filestateLabel.setForeground(Color.RED);
                        filestateLabel.setText("実行するクエリがありません");
                        return;
                    }

                    String filename = filenameLabel.getText(); // テキストフィールドから値を得る（ファイル名）
                    filename = GlobalEnv.folderPath + GlobalEnv.OS_FS + filename;
                            // マルチスレッド(非同期)処理
                            // SwingWorkerを生成して実行
                            @SuppressWarnings("rawtypes")
                            final
                            SwingWorker worker1 = new execThread1(filename, button1, folder_exe_button);
                            worker1.execute();

                            // 実行ボタンが押されたら実行結果のタブに切り替える
                            tabbed_Table.setSelectedIndex(1);
                            stopButton.setEnabled(true);

                            stopButton.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    worker1.cancel(true);
                                    stopButton.setEnabled(false);
                                }
                            });

            }
        });

        //％分割ボタン
        linkforeachButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String target = GlobalEnv.textPane.getText();
//				Exec.process(target);
                LinkForEach.process(target, filenameLabel.getText());
            }
        });

        // halken
        /*
        ssvisual.addActionListener(new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		ssvisual.setEnabled(false);
        		SwingWorker worker = new SSvisualWorker(ssvisual);
        		worker.execute();
        	}
        });
        */

      //インデントボタン
        indentButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String indentquery = Exec.indentProcess(GlobalEnv.textPane.getText());
                GlobalEnv.textPane.setText(indentquery);
            }
        });

        //シンプルビューボタン
        simpleviewButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                SimpleView simpleview = new SimpleView("");
            }
        });

//        //シンプルビューチェックボックス
//        simpleviewbox.addActionListener(new ActionListener() {
//        	@Override
//        	public void actionPerformed(ActionEvent e) {
//        		if(!simpleview_flag){
//        			Search simpleview = new Search("");
//        			simpleview_flag = true;
//
//        			if(simpleviewbox.isSelected()){
////            			System.out.println(Exec.simpleViewProcess(GlobalEnv.textPane.getText()));
//            			simpleview.setVisible(true);
//            		} else{
//            			simpleview.setVisible(false);
//            		}
//        		}
//
//        	}
//        });

        maximizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!restore_flag){
                    splitPane3.setDividerLocation(0);
                    splitPane4.setVisible(false);
                    allPanel2.setVisible(false);
                    maximizeButton.setIcon(restoreIcon);
                    restore_flag = true;
                } else {
                    splitPane4.setVisible(true);
                    allPanel2.setVisible(true);
                    splitPane3.setDividerLocation(x);
                    splitPane.setDividerLocation(y);
                    maximizeButton.setIcon(maximizeIcon);
                    restore_flag = false;
                }

            }
        });

        restoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitPane4.setVisible(true);
                allPanel2.setVisible(true);
                splitPane3.setDividerLocation(x);
                splitPane.setDividerLocation(y);

            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(GlobalEnv.textPane.getSelectedText() == null){
                    new Search("");
                } else {
                    new Search(GlobalEnv.textPane.getSelectedText());
                }
            }

        });

        exit_button1.addActionListener(action2 = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                windowCloseCheckProcess();
            }
        });

        exit_button2.addActionListener(action2 = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                windowCloseCheckProcess();
            }
        });

        exit_button3.addActionListener(action2 = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                windowCloseCheckProcess();
            }
        });

        selectoutdir_button.addActionListener(new AbstractAction(){

                public void actionPerformed(ActionEvent arg0) {
                    JFileChooser filechooser = new JFileChooser(GlobalEnv.USER_HOME);

                    filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    filechooser.setMultiSelectionEnabled(true);

                    int selected = filechooser.showOpenDialog(null);
                    if (selected == JFileChooser.APPROVE_OPTION) {
                        File[] files = filechooser.getSelectedFiles();
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < files.length; i++) {
                            sb.append(files[i].getAbsolutePath());
                        }
                        String path = new String(sb);
//						GlobalEnv.config_outdirField.setText(path);
                        History.addItem(GlobalEnv.outdirCombo, path, 5);
                    }
                }

        });

        // 1つ目のタブ: [選択]
        select_button.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
//				V.setVisible(true);
//				final HtmlViewer viewer = new HtmlViewer();
            }
        });

        // 1つ目のタブ: [作業フォルダ選択]
        select_button2.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!GlobalEnv.folderModel.getSelectedItem().equals("")) {
                    if (!currentfileData.equals(GlobalEnv.textPane.getText())) {
                        ActionEvent ae = new ActionEvent(this,
                                ActionEvent.ACTION_PERFORMED, "保存チェック");
                        confirm_actionPerformed(ae);
                    }
                    if (filestateLabel.getText().equals("キャンセルしました")) {
                        return;
                    }
                }
                JFileChooser filechooser = new JFileChooser(GlobalEnv.folderPath);

                filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                filechooser.setMultiSelectionEnabled(true);

                int selected = filechooser.showOpenDialog(null);
                if (selected == JFileChooser.APPROVE_OPTION) {
                    File[] files = filechooser.getSelectedFiles();
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < files.length; i++) {
                        sb.append(files[i].getAbsolutePath());
                    }
                    String filename = new String(sb);
                    GlobalEnv.folderPath = new File(filename).getAbsolutePath();
                    if(!GlobalEnv.folderModel.getSelectedItem().equals("")){
                        GlobalEnv.textPane.setText("");
                        filenameLabel.setText("");
                        currentfileData = "";
                        currentfileName = "";
                    }
//					folderPath_textField1.setText(GlobalEnv.folderPath);
                    History.addItem(GlobalEnv.folderCombo, GlobalEnv.folderPath, 5);
                    folderPath_textField2.setText(GlobalEnv.folderPath);
                }
                dir = new File(GlobalEnv.folderPath);
                // ノードの作成
                root = new DefaultMutableTreeNode(dir.getName());
                createNodes(root);
                // JTree オブジェクトの作成
                treemodel = new DefaultTreeModel(root);
                tree.setModel(treemodel);
            }
        });

        // 1つ目のタブ: [更新]
        updatedbButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DB.db();
            }
        });

        // 2つ目のタブ: [実行]
        folder_exe_button.addActionListener(execButton2 = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // マルチスレッド(非同期)処理
                // SwingWorkerを生成して実行
                @SuppressWarnings("rawtypes")
                final
                SwingWorker worker2 = new execThread2(folder_exe_button,
                        button1);
                worker2.execute();
                stopButton2.setEnabled(true);

                stopButton2.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        worker2.cancel(true);
                        stopButton2.setEnabled(false);
                    }
                });
            }
        });

        // 3つ目のタブ: 保存ボタンを押した時の動作
        button3.addActionListener(action3 = new AbstractAction() {
            public void actionPerformed(ActionEvent arg0) {
                // どのラジオボタンが選択されているか
                if (GlobalEnv.radio1[0].isSelected())
                    GlobalEnv.radio1Selected = 0; // あり
                else
                    GlobalEnv.radio1Selected = 1; // なし
                if (GlobalEnv.radio2[0].isSelected())
                    GlobalEnv.radio2Selected = 0; // あり
                else
                    GlobalEnv.radio2Selected = 1; // なし

                String tabstr;
                tabstr = (String) tabCombo.getSelectedItem();
                tabSize = Integer.parseInt(tabstr);
                CaretState.changeTabSize(tabSize, GlobalEnv.textPane, GlobalEnv.doc);

                if (Functions.createConfig()) {
                    Functions.deleteFile(GlobalEnv.outdirPath, ".htmlViewer.ssql");
                    Functions.deleteFile(GlobalEnv.outdirPath, ".htmlViewer.html");
                    Functions.deleteFile(GlobalEnv.outdirPath, ".errorlog.txt");
                    DB.db();
                    if (GlobalEnv.outdirPath.equals("")) {
                        GlobalEnv.outdirPath = GlobalEnv.folderPath;
                    }
                    if (GlobalEnv.radio2[0].isSelected()) {
//						JOptionPane.showMessageDialog(null, "保存しました");// masato

                    } else {
//						JOptionPane.showMessageDialog(null, "Save Successfully");
                    }
                } else {
//					if (GlobalEnv.radio2[0].isSelected())
//						JOptionPane.showMessageDialog(null, "保存に失敗しました");// masato
//					else
//						JOptionPane.showMessageDialog(null, "faled to save");
                }
            }
        });

        // 1つ目のタブ：　途中ファイルの保存
        button4.addActionListener(action4 = new AbstractAction() {
            public void actionPerformed(ActionEvent arg0) {
                if(GlobalEnv.folderModel.getSelectedItem().equals("")){
                    stateTimer.start();
                    filestateLabel.setForeground(Color.RED);
                    filestateLabel.setText("作業フォルダを選択してください");
                    return;
                }
                // 編集中のクエリがあれば
                String query = GlobalEnv.textPane.getText();
                currentfileName = filenameLabel.getText();
                if(!currentfileName.equals("")){
                    if(Functions.createFile(GlobalEnv.folderPath + GlobalEnv.OS_FS + currentfileName, query)){
                        if (GlobalEnv.radio2[0].isSelected()) {
                            // 保存しましたダイヤログ
                            stateTimer.start();
                            filestateLabel.setText("保存しました");
                            currentfileData = GlobalEnv.textPane.getText();
                            filestateLabel2.setText("");
                            createNodes(root);
                            treemodel.reload();
                        }
                    }
                // ファイル名なし、つまり、クエリが新規作成されていたら
                } else {
                    // なにか書かれていたら
                    if (!query.equals("")) {
                        String data = "保存先：" + GlobalEnv.folderPath;
                        String value = JOptionPane.showInputDialog(data +  "\nファイル名を入力してください");
                        if (value == null) {
                            stateTimer.start();
                            filestateLabel.setText("キャンセルしました");
                        } else if(value.equals("")) {
                            stateTimer.start();
                            filestateLabel.setForeground(Color.RED);
                            filestateLabel.setText("ファイル名を入力してください");
                        } else {
                            if (!value.endsWith(".sql") && !value.endsWith(".ssql"))
                                value += ".ssql";
                            currentfileName = value;
                            currentfileData = query;
                            if(Functions.createFile(GlobalEnv.folderPath + GlobalEnv.OS_FS + currentfileName, query)){
                                if (GlobalEnv.radio2[0].isSelected()) {
                                    // 保存しましたダイヤログ
                                    stateTimer.start();
                                    filestateLabel.setText("保存しました");
                                    filenameLabel.setText(currentfileName);
                                    filestateLabel2.setText("");
                                    createNodes(root);
                                    treemodel.reload();
                                }
                            }
                        }
                    }
                }
            }
        });

        manual_button.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent arg0) {
                Functions.open("http://www.db.ics.keio.ac.jp/ssql/DL/SSQLman.pdf");
            }
        });

        // 左上(or右上)の[X]ボタンが押された時の処理
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // windowClosingが呼ばれた後になにもしない(終了しない)
                windowCloseCheckProcess();
            }
        });


        // アクセラレータの設定
        // setButtonAccelerator(changeLang1,
        // KeyStroke.getKeyStroke(KeyEvent.VK_L, GlobalEnv.osShortcutKey), langaction1);
        // //2つ目のタブ: [実行]ボタンのアクセラレータ'[Ctrl]+[Enter]'をセット
        setButtonAccelerator(button1,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, GlobalEnv.osShortcutKey),
                action1); // [実行]ボタンのアクセラレータ'[Ctrl]+[Enter]'をセット
        setButtonAccelerator(exit_button1,
                KeyStroke.getKeyStroke(KeyEvent.VK_W, GlobalEnv.osShortcutKey), action2); // [キャンセル]ボタンのアクセラレータ'[Ctrl]+[W]'をセット
        setButtonAccelerator(exit_button2,
                KeyStroke.getKeyStroke(KeyEvent.VK_W, GlobalEnv.osShortcutKey), action2); // [キャンセル]ボタンのアクセラレータ'[Ctrl]+[W]'をセット
        setButtonAccelerator(exit_button3,
                KeyStroke.getKeyStroke(KeyEvent.VK_W, GlobalEnv.osShortcutKey), action2); // [キャンセル]ボタンのアクセラレータ'[Ctrl]+[W]'をセット
        setButtonAccelerator(exit_button1,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), action2); // [キャンセル]ボタンのアクセラレータ'[Esc]'をセット
        setButtonAccelerator(exit_button2,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), action2); // [キャンセル]ボタンのアクセラレータ'[Esc]'をセット
        setButtonAccelerator(exit_button3,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), action2); // [キャンセル]ボタンのアクセラレータ'[Esc]'をセッ
        setButtonAccelerator(button3,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, GlobalEnv.osShortcutKey), action3); // [保存]ボタンのアクセラレータ'[Ctrl]+[S]'をセット
        setButtonAccelerator(button4,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, GlobalEnv.osShortcutKey), action4); // 1つ目のタブにおいて[保存]ボタンのアクセラレータ'[Ctrl]+[S]'をセット
        setButtonAccelerator(folder_exe_button,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, GlobalEnv.osShortcutKey),
                execButton2); // 2つ目のタブ: [実行]ボタンのアクセラレータ'[Ctrl]+[Enter]'をセット

        getContentPane().add(panel, BorderLayout.SOUTH);

    }

    // ボタンのアクセラレータをセットする
    public void setButtonAccelerator(JButton button, KeyStroke keys, Action a) {
        // setting the button to receive action when 'keys' is pressed
        try {
            InputMap keyMap = new ComponentInputMap(button);
            keyMap.put(keys, "action");

            ActionMap actionMap = new ActionMapUIResource();
            actionMap.put("action", a);

            SwingUtilities.replaceUIActionMap(button, actionMap);
            SwingUtilities.replaceUIInputMap(button,
                    JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
        } catch (Exception e) {
        }
    }

    public String osName() {
        if (GlobalEnv.OS.indexOf("Windows") >= 0) {
            return "Windows";
        } else if (GlobalEnv.OS.indexOf("Mac") >= 0) {
            return "Mac";
        } else if (GlobalEnv.OS.indexOf("Linux") >= 0) {
            return "Linux";
        } else {
            return "Others";// その他
        }
    }

    // 終了（[キャンセル]・[X]ボタン押下）時に呼び出す
    public void windowCloseCheckProcess() {
        // [キャンセル]が押されたとき、Save＆終了確認画面を表示（※dataが存在している場合のみ）
        if (currentfileData.equals(GlobalEnv.textPane.getText())) {
            button3.doClick();
            String filePath = GlobalEnv.folderPath + GlobalEnv.OS_FS + currentfileName;
            // 閉じたときcurrentfileNameがファイルではなかったら保存しない
            if(!new File(GlobalEnv.folderPath + GlobalEnv.OS_FS + currentfileName).isFile()){
                filePath = "";
            }
            saveSSQLtoolInfo(GlobalEnv.folderPath, filePath);
            System.exit(0);
        } else {
            int option = JOptionPane.showConfirmDialog(this,
                    "編集中のクエリを保存して終了しますか？", "確認",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(option == 0){
                button4.doClick();
                button3.doClick();
                saveSSQLtoolInfo(GlobalEnv.folderPath, GlobalEnv.folderPath + GlobalEnv.OS_FS + currentfileName);
                System.exit(0);
            } else if(option == 1) {
                button3.doClick();
                saveSSQLtoolInfo(GlobalEnv.folderPath, GlobalEnv.folderPath + GlobalEnv.OS_FS + currentfileName);
                System.exit(0);
            } else if(option == 2) {
                return;
            }
        }
    }

    /**********************************************************************************************/
    /* マルチスレッド(非同期)処理（クエリの実行・選択されたフォルダ内の全クエリの実行など） */
    /**********************************************************************************************/
    /* 非同期に行う処理を記述するためのクラス */
    // 1つ目のタブ　クエリの実行
    class execThread1 extends SwingWorker<Object, Object> {
        private String filename1;
        private JButton button1, button2;

        public execThread1(String filename, JButton button1, JButton button2) {
            this.button1 = button1;
            this.button2 = button2;
            this.filename1 = filename;
        }

        // 非同期処理
        @Override
        public Object doInBackground() {
            // if(GlobalEnv.radio2[0].isSelected()){
            // 前処理
            button1.setEnabled(false);
            button1.setText("実行中...");
            button2.setEnabled(false);
            GlobalEnv.resultPane.setEnabled(true);

            // クエリの実行
            String filename = new File(filename1).getAbsolutePath();
            String logFile_outdir = new File(filename).getParent();

            GlobalEnv.resultPane.setText("実行結果" + GlobalEnv.OS_LS + "");
            ssqlExecLogs = "[クエリを実行]" + GlobalEnv.OS_LS + "";
            ssqlExecLogs += "■実行ファイル: " + filename + "" + GlobalEnv.OS_LS + "";
            try {
                GlobalEnv.resultDoc.insertString(GlobalEnv.resultDoc.getLength(), "■実行ファイル: " + filename + "" + GlobalEnv.OS_LS + "", CaretState.plane);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            ssqlExecLogs += "" + GlobalEnv.OS_LS + "＜詳細＞" + GlobalEnv.OS_LS
                    + "";
//			try {
//				GlobalEnv.resultDoc.insertString(GlobalEnv.resultDoc.getLength(), "EXE_FILE_PATH = " + GlobalEnv.EXE_FILE_PATH + GlobalEnv.OS_LS + "", CaretState.errAttr);
//			} catch (BadLocationException e1) {
//				e1.printStackTrace();
//			}
//			try {
//				GlobalEnv.resultDoc.insertString(GlobalEnv.resultDoc.getLength(), "getClassPath() = " + Common.getClassPath() + GlobalEnv.OS_LS + "", CaretState.errAttr);
//			} catch (BadLocationException e1) {
//				e1.printStackTrace();
//			}
//			try {
//				GlobalEnv.resultDoc.insertString(GlobalEnv.resultDoc.getLength(), "libsclassPath = " + libsClassPath + GlobalEnv.OS_LS + "", CaretState.errAttr);
//			} catch (BadLocationException e1) {
//				e1.printStackTrace();
//			}
            if (SSQL_exec.execSuperSQL(filename, libsClassPath, GlobalEnv.resultPane, GlobalEnv.resultDoc)) {
                ssqlExecLogs += "結果: 成功" + GlobalEnv.OS_LS + "";
                try {
                    GlobalEnv.resultDoc.insertString(GlobalEnv.resultDoc.getLength(), "結果: 成功" + GlobalEnv.OS_LS + "", CaretState.plane);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }

                if (GlobalEnv.radio1Selected == 0) {
                     Functions.open(Functions.getHTMLAbsolutePath(GlobalEnv.folderPath, filename, "html")); // 成功時、生成されたファイルを開く
                     //TODO:HTML以外のファイル(XML等)が生成された場合の判定処理
                }
            } else {
                ssqlExecLogs += "結果: 失敗" + GlobalEnv.OS_LS + "";
                try {
                    GlobalEnv.resultDoc.insertString(GlobalEnv.resultDoc.getLength(), "結果: 失敗" + GlobalEnv.OS_LS + "", CaretState.errAttr);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            Functions.createFile(logFile_outdir + GlobalEnv.OS_FS + "log.txt",
                    ssqlExecLogs);
            createNodes(root);
            treemodel.reload();
            return null;
        }

        // 非同期処理後に実行
        @Override
        protected void done() {
            // ボタンを使用可能にする
            if (GlobalEnv.radio2[0].isSelected())
                button1.setText("実行");
            else
                button1.setText("Run");
            button1.setEnabled(true);
            button2.setEnabled(true);
        }
    }

    /* 非同期に行う処理を記述するためのクラス */
    // 2つ目のタブ　選択されたフォルダ内の全クエリの実行
    class execThread2 extends SwingWorker<Object, Object> {
        private JButton button1, button2;

        public execThread2(JButton button1, JButton button2) {
            this.button1 = button1;
            this.button2 = button2;
        }

        // 非同期処理
        @Override
        public Object doInBackground() {
            if (GlobalEnv.radio2[0].isSelected()) {
                // 前処理
                button1.setEnabled(false);
                button1.setText("実行中...");
                button2.setEnabled(false);
                GlobalEnv.resultPane2.setEnabled(true);

                // 選択されたフォルダ内の全クエリの実行
                String execResultLogs = "", execSuccededResultLogs = "", execFailedResultLogs = "";
                execResultLogs += "選択フォルダ '" + GlobalEnv.folderPath + "'"
                        + GlobalEnv.OS_LS + "";
                ssqlExecLogs = "";
                GlobalEnv.resultPane2.setText(execResultLogs + "" + GlobalEnv.OS_LS + "");
                String buf = "";
                File targetDir = new File(GlobalEnv.folderPath);
                String filename = "";
                int execCount = 0, execSucceded = 0, execFailed = 0;

                if (targetDir.exists() && targetDir.isDirectory()) {
                    File[] fileList = targetDir.listFiles();
                    for (int i = 0; i < fileList.length; i++) {
                        if (fileList[i].getName().endsWith(".sql")
                                || fileList[i].getName().endsWith(".ssql")) {
                            filename = fileList[i].getAbsolutePath();
                            buf = "■実行ファイル" + (++execCount) + ": " + filename
                                    + "" + GlobalEnv.OS_LS + "";
                            ssqlExecLogs += buf;
                            try {
                                GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), buf, CaretState.plane);
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                            if (SSQL_exec.execSuperSQL(filename, libsClassPath,
                                    GlobalEnv.resultPane2, GlobalEnv.resultDoc2)) {
                                execSuccededResultLogs += "成功: " + filename
                                        + "" + GlobalEnv.OS_LS + "";
                                execSucceded++;
                                ssqlExecLogs += "結果: 成功" + GlobalEnv.OS_LS + "";
                                try {
                                    GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), "結果: 成功" + GlobalEnv.OS_LS + "", CaretState.plane);
                                } catch (BadLocationException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                execFailedResultLogs += "失敗: " + filename + "" + GlobalEnv.OS_LS + "";
                                execFailed++;
                                ssqlExecLogs += "結果: 失敗" + GlobalEnv.OS_LS + "";
                                try {
                                    GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), "結果: 失敗" + GlobalEnv.OS_LS + "", CaretState.errAttr);
                                } catch (BadLocationException e) {
                                    e.printStackTrace();
                                }
                            }
                            ssqlExecLogs += "" + GlobalEnv.OS_LS + "";
                            try {
                                GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), "" + GlobalEnv.OS_LS + "", CaretState.plane);
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (execCount != 0) {
                    buf = execSuccededResultLogs + execFailedResultLogs;
                    buf += "" + GlobalEnv.OS_LS + "" + execCount + "ファイル実行（成功:"
                            + execSucceded + "ファイル、失敗:" + execFailed + "ファイル） "
                            + GlobalEnv.OS_LS + "";
                    execResultLogs += buf;
                    Functions.createFile(targetDir + GlobalEnv.OS_FS + "logs.txt",
                            "[フォルダを指定して実行]" + GlobalEnv.OS_LS + ""
                                    + execResultLogs + "" + GlobalEnv.OS_LS
                                    + "＜詳細＞" + GlobalEnv.OS_LS + ""
                                    + ssqlExecLogs); // ログファイルの作成
                    try {
                        GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), "" + GlobalEnv.OS_LS + "[結果]" + GlobalEnv.OS_LS + "" + buf, CaretState.plane);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    if (GlobalEnv.radio1Selected == 0)
                        if (execSucceded > 0)
                            Functions.open(Functions.getOutdir(GlobalEnv.folderPath)); // 成功したファイルが1つ以上あった場合、出力先のフォルダを開く
                } else {
                    GlobalEnv.resultPane2.setEnabled(false);
                    GlobalEnv.resultPane2.setText("実行結果" + GlobalEnv.OS_LS + "");
                    JOptionPane.showMessageDialog(null,
                            "指定されたフォルダには、実行可能なファイル(拡張子.sql or .ssql)が存在しません");
                }
            } else {
                // 前処理
                button1.setEnabled(false);
                button1.setText("Executing...");
                button2.setEnabled(false);
                GlobalEnv.resultPane2.setEnabled(true);

                // 選択されたフォルダ内の全クエリの実行
                String execResultLogs = "", execSuccededResultLogs = "", execFailedResultLogs = "";
                execResultLogs += "Selected Folder '" + GlobalEnv.folderPath
                        + "'" + GlobalEnv.OS_LS + "";
                ssqlExecLogs = "";
                GlobalEnv.resultPane2.setText(execResultLogs + "" + GlobalEnv.OS_LS + "");
                String buf = "";
                File targetDir = new File(GlobalEnv.folderPath);
                String filename = "";
                int execCount = 0, execSucceded = 0, execFailed = 0;

                if (targetDir.exists() && targetDir.isDirectory()) {
                    File[] fileList = targetDir.listFiles();
                    for (int i = 0; i < fileList.length; i++) {
                        if (fileList[i].getName().endsWith(".sql")
                                || fileList[i].getName().endsWith(".ssql")) {
                            filename = fileList[i].getAbsolutePath();
                            buf = "■Exec files" + (++execCount) + ": "
                                    + filename + "" + GlobalEnv.OS_LS + "";
                            ssqlExecLogs += buf;
                            try {
                                GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), buf, CaretState.plane);
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                            if (SSQL_exec.execSuperSQL(filename, libsClassPath,
                                    GlobalEnv.resultPane2, GlobalEnv.resultDoc2)) {
                                execSuccededResultLogs += "Success: " + filename + "" + GlobalEnv.OS_LS + "";
                                execSucceded++;
                                ssqlExecLogs += "Result: Success"
                                        + GlobalEnv.OS_LS + "";
                                try {
                                    GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), "Result: Success" + GlobalEnv.OS_LS + "", CaretState.plane);
                                } catch (BadLocationException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                execFailedResultLogs += "Failure: " + filename + "" + GlobalEnv.OS_LS + "";
                                execFailed++;
                                ssqlExecLogs += "Success: Failure" + GlobalEnv.OS_LS + "";
                                try {
                                    GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), "Result: Failure" + GlobalEnv.OS_LS + "", CaretState.errAttr);
                                } catch (BadLocationException e) {
                                    e.printStackTrace();
                                }
                            }
                            ssqlExecLogs += "" + GlobalEnv.OS_LS + "";
                            try {
                                GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), "" + GlobalEnv.OS_LS + "", CaretState.plane);
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (execCount != 0) {
                    buf = execSuccededResultLogs + execFailedResultLogs;
                    buf += "" + GlobalEnv.OS_LS + "" + execCount
                            + "Exec Files（Success:" + execSucceded
                            + "Files、Failure:" + execFailed + "Files） "
                            + GlobalEnv.OS_LS + "";
                    execResultLogs += buf;
                    Functions.createFile(targetDir + GlobalEnv.OS_FS + "logs.txt",
                            "[Execute Selected Folder]" + GlobalEnv.OS_LS + ""
                                    + execResultLogs + "" + GlobalEnv.OS_LS
                                    + "＜Details＞" + GlobalEnv.OS_LS + ""
                                    + ssqlExecLogs); // ログファイルの作成
                    try {
                        GlobalEnv.resultDoc2.insertString(GlobalEnv.resultDoc2.getLength(), "" + GlobalEnv.OS_LS + "[Result]" + GlobalEnv.OS_LS + "" + buf, CaretState.plane);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    if (GlobalEnv.radio1Selected == 0)
                        if (execSucceded > 0)
                            Functions.open(Functions.getOutdir(GlobalEnv.folderPath)); // 成功したファイルが1つ以上あった場合、出力先のフォルダを開く
                } else {
                    GlobalEnv.resultPane2.setEnabled(false);
                    GlobalEnv.resultPane2.setText("Exec Result" + GlobalEnv.OS_LS + "");
                    JOptionPane
                            .showMessageDialog(null,
                                    "There are not files whose extension is \".sql\" in this folder.");
                }
            }
            return null;
        }

        // 非同期処理後に実行
        @Override
        protected void done() {
            // ボタンを使用可能にする
            if (GlobalEnv.radio2[0].isSelected())
                button1.setText("実行");
            else
                button1.setText("Run");
            button1.setEnabled(true);
            button2.setEnabled(true);
        }
    }


    public static void reloadFolderTree(){
        dir = new File(GlobalEnv.folderPath);
        // ノードの作成
        root = new DefaultMutableTreeNode(dir.getName());
        createNodes(root);
        // JTree オブジェクトの作成
        treemodel = new DefaultTreeModel(root);
        tree.setModel(treemodel);
    }



    // 閉じる前に、開いていたクエリ名・ディレクトリ等の情報を、ホームの「.ssqltool」へ保存
    public void saveSSQLtoolInfo(String folderPath1, String fileName1) {
        // GlobalEnv.folderPath1: タブ1のフォルダ
        // fileName1: タブ1のファイル名
        // GlobalEnv.folderPath2: タブ2のフォルダ
        String s = "";
        s += "folderPath1=" + folderPath1 + "" + GlobalEnv.OS_LS + "";
        s += "fileName1=" + fileName1 + "" + GlobalEnv.OS_LS + "";
        s += "radio1Selected=" + GlobalEnv.radio1Selected + "" + GlobalEnv.OS_LS + "";
        s += "radio2Selected=" + GlobalEnv.radio2Selected + "" + GlobalEnv.OS_LS + "";
        s += "tabSize=" + tabSize + "" + GlobalEnv.OS_LS + "";
        s += "url=" + GlobalEnv.urlCombo.getEditor().getItem() + "" + GlobalEnv.OS_LS + "";
        String folderHistory = History.saveHistory(GlobalEnv.folderPath, GlobalEnv.folderCombo);
        s += "folderHistory=" + folderHistory + "" + GlobalEnv.OS_LS + "";
        String outdirHistory = History.saveHistory(GlobalEnv.outdirPath, GlobalEnv.outdirCombo);
        s += "outdirHistory=" + outdirHistory + "" + GlobalEnv.OS_LS + "";
        String urlHistory = History.saveHistory((String)GlobalEnv.urlCombo.getEditor().getItem(), GlobalEnv.urlCombo);
        s += "urlHistory=" + urlHistory + "" + GlobalEnv.OS_LS + "";

        Functions.deleteFile(GlobalEnv.outdirPath, ".htmlViewer.ssql");
        Functions.deleteFile(GlobalEnv.outdirPath, ".htmlViewer.html");
        Functions.deleteFile(GlobalEnv.outdirPath, ".errorlog.txt");

        Functions.createFile(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + ".ssqltool", s);
    }

    // 前回の情報（開いていたフォルダ・ファイル）を、ホームの「.ssqltool」から読み込んで反映
    public void reflectSSQLtoolInfo() {
        String fP1 = Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + ".ssqltool", "folderPath1");
        String fN1 = Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + ".ssqltool", "fileName1");

        if (!fP1.equals("") && new File(fP1).exists()){
            GlobalEnv.folderPath = fP1;
        }
        if (!fN1.equals("") && new File(fN1).exists()) {
            filenameLabel.setText(new File(fN1).getName());
            GlobalEnv.textPane.setText(Functions.readFile(fN1));
            currentfileName = new File(fN1).getName();
            currentfileData = Functions.readFile(fN1);
        } else {
            if (GlobalEnv.radio2Selected == 0){
                GlobalEnv.textPane.setText("");
            }
            else{
                GlobalEnv.textPane.setText("");
            }

        }
        try {
            GlobalEnv.radio1Selected = Integer.parseInt(Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS
                    + ".ssqltool", "radio1Selected"));
            GlobalEnv.radio2Selected = Integer.parseInt(Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS
                    + ".ssqltool", "radio2Selected"));
        } catch (Exception e) {
            GlobalEnv.radio1Selected = 0;
            GlobalEnv.radio2Selected = 0;
        }
        try {
            tabSize = Integer.parseInt(Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + ".ssqltool",
                    "tabSize"));
            tabCombo.setSelectedIndex(tabSize - 1);
            CaretState.changeTabSize(tabSize, GlobalEnv.textPane, GlobalEnv.doc);
        } catch (Exception e) {
            tabSize = 3;
        }
//		GlobalEnv.url_textField.setText(Common.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS
//					+ ".ssqltool", "url"));
        String[] urlStr = Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + ".ssqltool", "urlHistory").split(",");
        for(int i = 0; i < urlStr.length; i++){
            GlobalEnv.urlModel.addElement(urlStr[i]);
        }
        String[] folderStr = Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + ".ssqltool", "folderHistory").split(",");
        for(int i = 0; i < folderStr.length; i++){
            GlobalEnv.folderModel.addElement(folderStr[i]);
        }
        String[] outdirStr = Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + ".ssqltool", "outdirHistory").split(",");
        for(int i = 0; i < outdirStr.length; i++){
            GlobalEnv.outdirModel.addElement(outdirStr[i]);
        }
        Edit.setLinePane();
    }




    public static String get_execDir(){
        String libs = new File(GlobalEnv.EXE_FILE_PATH).getAbsolutePath();// 実行jarファイルの絶対パスを取得

        if (libs.contains(":")) {// ビルドバスの追加を行うと参照ライブラリ内のファイルのパスも付け加えてしまう仕様らしいので、:移行カット
            libs = libs.substring(0, libs.indexOf(":"));
        }
        if (libs.endsWith(".jar")) { // jarファイルを実行した場合（Eclipseから起動した場合は入らない）
            libs = libs.substring(0, libs.lastIndexOf(GlobalEnv.OS_FS));
        }
        return libs;
    }



    // itemを個数分セットし、ポップアップに載せ、アクションをセット
    public static JMenuItem[] setMenuItem(JPopupMenu popup, List<String> itemN, final int count) {
        final JMenuItem[] item = new JMenuItem[count];
        for (int j = 0; j < count; j++) {
            item[j] = new JMenuItem(itemN.get(j));
            popup.add(item[j]);
        }
        return item;
    }



    public void actionPerformed(ActionEvent e) {
        SimpleAttributeSet attr = new SimpleAttributeSet();

//		if(e.getSource() == Popup.){
//			System.out.println("ok");
//		}
        // 削除だったら
        if (e.getSource() == GlobalEnv.file_menuItem[0]) {
            // 削除確認ダイヤログ
            int option = JOptionPane.showConfirmDialog(this, treefileName
                    + "\nこのファイルを削除しますか？", "確認", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                File treeFile = new File(GlobalEnv.folderPath + GlobalEnv.OS_FS
                        + treefileName);
                if (treeFile.exists()) {
                    // 削除選択したファイルが現在編集中のファイルなら
                    if (currentfileName.equals(treefileName)) {
                        currentfileData = "";
                        currentfileName = "";
                        filenameLabel.setText("");
                        GlobalEnv.textPane.setText("");
                    }
                    treeFile.delete();
                    // ツリーを更新
                    createNodes(root);
                    treemodel.reload();
                    stateTimer.start();
                    filestateLabel.setForeground(Color.RED);
                    filestateLabel.setText("削除しました");
                    return;
                }
            } else {
                stateTimer.start();
                filestateLabel.setText("キャンセルしました");
                return;
            }
        // ファイル名変更だったら
        } else if(e.getSource() == GlobalEnv.file_menuItem[1]){
            File treeFile = new File(GlobalEnv.folderPath + GlobalEnv.OS_FS + treefileName);
            if(treeFile.exists()){
                String value = JOptionPane.showInputDialog("ファイル名を入力してください", treefileName);
                if (value == null) {
                    stateTimer.start();
                    filestateLabel.setText("キャンセルしました");
                    return;
                } else if(value.equals("")) {
                    stateTimer.start();
                    filestateLabel.setForeground(Color.RED);
                    filestateLabel.setText("ファイル名を入力してください");
                    return;
                } else {
                    if (!value.endsWith(".sql") && !value.endsWith(".ssql"))
                        value += ".ssql";
                    if(treefileName.equals(value)){
                        return;
                    }
                    if(Functions.createFile(GlobalEnv.folderPath + GlobalEnv.OS_FS + value, treefileData)){
                        // 削除選択したファイルが現在編集中のファイルなら
                        if(currentfileName.equals(treefileName)){
                            currentfileData = "";
                            currentfileName = "";
                            filenameLabel.setText("");
                            GlobalEnv.textPane.setText("");
                        }
                        treeFile.delete();
                        // 保存しましたダイヤログ
                        stateTimer.start();
                        filestateLabel.setText("変更しました");
                        filestateLabel2.setText("");
                        // ツリーを更新
                        createNodes(root);
                        treemodel.reload();
                        return;
                    }
                }
            }
        // 複製だったら
        } else if (e.getSource() == GlobalEnv.file_menuItem[2]) {
            File treeFile = new File(GlobalEnv.folderPath + GlobalEnv.OS_FS + treefileName);
            int i = 1;
            String tmp = treefileName.substring(0, treefileName.lastIndexOf("."));
            // 複製したいファイルが存在していたら
            if (treeFile.exists()) {
                for (;;) {
                    treefileName = tmp + "(" + i + ")" + ".ssql";
                    // そのファイル名が存在していたら（複製済みだったら）i++
                    if (new File(GlobalEnv.folderPath + GlobalEnv.OS_FS + treefileName).exists()) {
                        i++;
                    } else {
                        if (Functions.createFile(GlobalEnv.folderPath
                                + GlobalEnv.OS_FS + treefileName, treefileData)) {
                            stateTimer.start();
                            filestateLabel.setText("複製しました");
                            // filestateLabel2.setText("");
                            // ツリーを更新
                            createNodes(root);
                            treemodel.reload();
                            return;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < decoration_Count; i++) {
            if (e.getSource() == GlobalEnv.decoration_menuItem[i]) {
                // 装飾子の{}内であれば
                if(Functions.decorationCheck(GlobalEnv.p)){
                    try {
                        GlobalEnv.doc.insertString(GlobalEnv.p, ", " + GlobalEnv.decorations_array.get(i) + "=", attr);
                        GlobalEnv.textPane.setCaretPosition(GlobalEnv.p);// 挿入後1文字キャレットを後退
                        return;
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                // 装飾子内でなければ
                } else {
                    try {
                        GlobalEnv.doc.insertString(GlobalEnv.p, "{" + GlobalEnv.decorations_array.get(i) + "=}", attr);
                        GlobalEnv.textPane.setCaretPosition(GlobalEnv.p - 1);// 挿入後1文字キャレットを後退
                        return;
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        for (int i = 0; i < function_Count; i++) {
            if (e.getSource() == GlobalEnv.function_menuItem[i]) {
                try {
                    GlobalEnv.doc.insertString(GlobalEnv.p, GlobalEnv.functions_array.get(i), attr);
                    // 挿入後1文字キャレットを後退
                    GlobalEnv.textPane.setCaretPosition(GlobalEnv.p - 1);
                    if(GlobalEnv.functions_array.get(i).equals("(asc1)") || GlobalEnv.functions_array.get(i).equals("(desc1)")){
                        GlobalEnv.textPane.setSelectionStart(GlobalEnv.p - 1);
                        GlobalEnv.textPane.setSelectionEnd(GlobalEnv.p);
                    }
                    return;
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void tree_actionPerformed(ActionEvent e) {
        String data = "保存先：" + GlobalEnv.folderPath;
        int option = JOptionPane.showConfirmDialog(null,
                data + "\n編集中のクエリを保存しますか？", "確認",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == 0) {
            button4.doClick();
            if(filestateLabel.getText().equals("ファイル名を入力してください")
                    || filestateLabel.getText().equals("キャンセルしました")){
                return;
            }
            GlobalEnv.textPane.setText(treefileData);
            filenameLabel.setText(treefileName);
            currentfileData = treefileData;
            filestateLabel2.setText("");

        } else if (option == 1) {
            GlobalEnv.textPane.setText(treefileData);
            filenameLabel.setText(treefileName);
            currentfileData = treefileData;
            filestateLabel2.setText("");
        } else if(option == 2){
            stateTimer.start();
            filestateLabel.setText("キャンセルしました");
            return;
        }
    }
    public void confirm_actionPerformed(ActionEvent e) {
        String data = "保存先：" + GlobalEnv.folderPath;

        int option = JOptionPane.showConfirmDialog(this,
                data + "\n編集中のクエリを保存しますか？", "確認",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == 0) {
            button4.doClick();
        } else if (option == 1) {
        } else if(option == 2){
            stateTimer.start();
            filestateLabel.setText("キャンセルしました");
            return;
        }
    }

    static public void newquery_actionPerformed(ActionEvent e) {
        int option = JOptionPane.showConfirmDialog(null,
                "編集中のクエリを保存しますか？", "確認",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        String mediaData = (String) querycomboModel.getSelectedItem();

        if (option == JOptionPane.YES_OPTION) {
            button4.doClick();
            if(filestateLabel.getText().equals("ファイル名を入力してください")
                    || filestateLabel.getText().equals("キャンセルしました")){
                queryCombo.setSelectedIndex(0);
                return;
            }
            GlobalEnv.textPane.setText("GENERATE " + mediaData + " {\n\n}@{debug = 'on', pbgcolor = 'aliceblue'}\nFROM ;");
            filenameLabel.setText("");
            queryCombo.setSelectedIndex(0);
        } else if (option == JOptionPane.NO_OPTION) {
            GlobalEnv.textPane.setText("GENERATE " + mediaData + " {\n\n}@{debug = 'on', pbgcolor = 'aliceblue'}\nFROM ;");
            filenameLabel.setText("");
            queryCombo.setSelectedIndex(0);
        } else if(option == JOptionPane.CANCEL_OPTION){
            queryCombo.setSelectedIndex(0);
            stateTimer.start();
            filestateLabel.setText("キャンセルしました");
        }
    }

    // コンボボックスの処理
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String data = (String) tabsizeModel.getSelectedItem();

            int tabSize = Integer.parseInt(data);
            CaretState.changeTabSize(tabSize, GlobalEnv.textPane, GlobalEnv.doc);
        }
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String mediaData = (String) querycomboModel.getSelectedItem();
            String query = GlobalEnv.textPane.getText();
            if(mediaData.equals("クエリの新規作成")){
                return;
            } else {
                if (query.equals(GlobalEnv.OS_LS + "")) {
                    GlobalEnv.textPane.setText("GENERATE " + mediaData + " {\n\n}@{debug = 'on', pbgcolor = 'aliceblue'}\nFROM ;");
                    filenameLabel.setText("");
                    queryCombo.setSelectedIndex(0);
//					currentState = queryCombo.getSelectedIndex();
                } else {
                    // クエリが編集中だったとき（保存済み）
                    if(filestateLabel2.getText().equals("")){
                        GlobalEnv.textPane.setText("GENERATE " + mediaData + " {\n\n}@{debug = 'on', pbgcolor = 'aliceblue'}\nFROM ;");
                        filenameLabel.setText("");
                        queryCombo.setSelectedIndex(0);
                    // 未保存だったら
                    } else {
                        ActionEvent ae = new ActionEvent(this,
                                ActionEvent.ACTION_PERFORMED, "Media");
                        newquery_actionPerformed(ae);
                    }
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
//    	System.out.print("Pressed Key.");

//    	    	System.out.println("hide");
//    	    	GlobalEnv.decoration_popup.hide();

    }


    // ノード作成メソッド
    private static void createNodes(DefaultMutableTreeNode root) {
        root.removeAllChildren();
        DefaultMutableTreeNode file;

        try{
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if ((files[i].getName().endsWith(".sql")
                    || files[i].getName().endsWith(".ssql")) && !files[i].getName().startsWith(".")) {
                String sqlFile = files[i].getName();
                file = new DefaultMutableTreeNode(sqlFile);
                root.add(file);
            }
        }
        } catch (NullPointerException e) {
        }
    }

    public void keyTyped(KeyEvent e) {
        // NOP
    }

    // stateChangedメソッド
    public void stateChanged(ChangeEvent e) {
        if (GlobalEnv.radio2[0].isSelected()) {
//			filename_label.setText("ファイル名：");
            foldername_label.setText("フォルダ名：");
            concernLabel.setText("成功時のファイル・フォルダ表示：");
            GlobalEnv.resultPane.setText("実行結果");
            GlobalEnv.resultPane2.setText("実行結果");
            select_button.setText("Viewerを表示");
            select_button2.setText("作業フォルダ");
            button1.setText("実行");
            folder_exe_button.setText("実行");
            tabbedPane.setTitleAt(0, "クエリを実行");
            tabbedPane.setTitleAt(1, "フォルダを指定して実行");
            tabbedPane.setTitleAt(2, "設定/Preferences");
            setTitle("SuperSQL クエリ実行ツール");
            exit_button1.setText("終了");
            exit_button2.setText("終了");
            exit_button3.setText("終了");
            manualLabel.setText("SSQLマニュアル：");
            tabLabel.setText("インデント幅：");
            GlobalEnv.radio1[0].setText("あり");
            GlobalEnv.radio1[1].setText("なし");
        }
        if (GlobalEnv.radio2[1].isSelected()) {
//			filename_label.setText("File Name：");
            foldername_label.setText("Folder Name：");
            concernLabel.setText("Display File or Folder：");
            GlobalEnv.resultPane.setText("Exec Result");
            GlobalEnv.resultPane2.setText("Exec Result");
            select_button.setText("Viewer");
            select_button2.setText("Select Folder");
            button1.setText("Run");
            folder_exe_button.setText("Run");
            tabbedPane.setTitleAt(0, "Execute Query");
            tabbedPane.setTitleAt(1, "Execute Selected Folder");
            tabbedPane.setTitleAt(2, "Preferences/設定");
            setTitle("SuperSQL：Query Execution Tool");
            exit_button1.setText("Exit");
            exit_button2.setText("Exit");
            exit_button3.setText("Exit");
            manualLabel.setText("SSQL Manual：");
            tabLabel.setText("Indent：");
            GlobalEnv.radio1[0].setText("Yes");
            GlobalEnv.radio1[1].setText("No");
        }
    }




    @Override
    public void mouseClicked(MouseEvent e) {

    }


    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    @Override
    public void mousePressed(MouseEvent arg0) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        JTree tree = (JTree) e.getSource();
        if(tree.getRowForLocation(e.getX(), e.getY()) == -1){
            if(SwingUtilities.isRightMouseButton(e)){
//				tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));
                show_newFilePopup(e, e.getX(), e.getY());
                return;
            }
        }
//		System.out.println(tree.getRowForLocation(e.getX(), e.getY()));
        current = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if(current == null || current.isRoot()){
            return;
        }
        try {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            Object sqlfilePath = path.getLastPathComponent();
            treefileData = "";
            treefileName = sqlfilePath.toString();
        } catch (NullPointerException ex) {
            return;
        } catch (StringIndexOutOfBoundsException ex) {
        }
        // 選択されたノードがRootノードでなければ

        if (!current.isRoot()) {

            currentfileName = filenameLabel.getText();
            currentfileData = Functions.myfileReader(currentfileName);
            treefileData = Functions.myfileReader(treefileName);

            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                wasDouble = true;

                // 読み込まれていたクエリもしくはファイル名が変更されていなかった場合
                if (currentfileData.equals(GlobalEnv.textPane.getText())
                        || (GlobalEnv.textPane.getText().trim().equals(""))
                        && filenameLabel.getText().equals("")) {
                    GlobalEnv.textPane.setText(treefileData);
                    filenameLabel.setText(treefileName);
                    // 現在のデータにツリーから選択したファイルの中身を入れる
                    currentfileData = treefileData;
                    currentfileName = treefileName;
                    // 読み込まれていたクエリもしくはファイル名が変更されていた場合
                } else {
                    ActionEvent ae = new ActionEvent(this,
                            ActionEvent.ACTION_PERFORMED, "保存チェック");
                    tree_actionPerformed(ae);
                }
            } else if (SwingUtilities.isLeftMouseButton(e)
                    && e.getClickCount() == 1) {
                wasDouble = false;
                // timer.start(); // しばらく待ってみる
            } else if(SwingUtilities.isRightMouseButton(e)){
                tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));
                show_filePopup(e, e.getX(), e.getY());
            }
        }


    }


    private void show_decorationPopup(ActionEvent e) {
    	System.out.println("Pressed " + e.getActionCommand());
        Rectangle rect = Functions.getRect();
        GlobalEnv.decoration_popup.show(GlobalEnv.textPane, rect.x, rect.y + rect.height);
//		Rectangle r;
//		try {
//			r = GlobalEnv.textPane.modelToView(GlobalEnv.p);// GlobalEnv.textPane上のキャレットの位置情報？
//			GlobalEnv.decoration_popup.show(e.getComponent(), r.x, r.y);// その位置情報を元にx,y座標を出す
//		} catch (BadLocationException e1) {
//			e1.printStackTrace();
//		}
    }





 /*   private void show_decorationPopup2(ActionEvent e) {
        Rectangle rect = Functions.getRect();
        GlobalEnv.decoration_popup.show(GlobalEnv.textPane, rect.x, rect.y + rect.height);
    }*/

    private void show_functionPopup(ActionEvent e) {
        Rectangle rect = Functions.getRect();
        GlobalEnv.function_popup.show(GlobalEnv.textPane, rect.x, rect.y + rect.height);
//        EventQueue.invokeLater(new Runnable() {
//            @Override public void run() {
//                SwingUtilities.getWindowAncestor(GlobalEnv.function_popup).toFront();
//                GlobalEnv.function_popup.requestFocusInWindow();
//            }
//        });
    }

    public static void show_tablePopup(ActionEvent e) {
        Rectangle rect = Functions.getRect();
        GlobalEnv.table_popup.show(GlobalEnv.textPane, rect.x, rect.y + rect.height);
//        EventQueue.invokeLater(new Runnable() {
//            @Override public void run() {
//                SwingUtilities.getWindowAncestor(GlobalEnv.table_popup).toFront();
//                GlobalEnv.table_popup.requestFocusInWindow();
//            }
//        });
    }
    public static void show_tablePopup2(ActionEvent e) {
        Rectangle rect = Functions.getRect();
        GlobalEnv.table_popup.show(GlobalEnv.textPane, rect.x, rect.y + rect.height);
//        EventQueue.invokeLater(new Runnable() {
//            @Override public void run() {
//                SwingUtilities.getWindowAncestor(GlobalEnv.table_popup).toFront();
//                GlobalEnv.table_popup.requestFocusInWindow();
//            }
//        });
    }
    public static void show_attributePopup(ActionEvent e) {
        Rectangle rect = Functions.getRect();
        GlobalEnv.tableattribute_popup.show(GlobalEnv.textPane, rect.x, rect.y + rect.height);
//        EventQueue.invokeLater(new Runnable() {
//            @Override public void run() {
//                SwingUtilities.getWindowAncestor(GlobalEnv.tableattribute_popup).toFront();
//                GlobalEnv.tableattribute_popup.requestFocusInWindow();
//            }
//        });
    }
    public static void show_attributePopup2(ActionEvent e) {
        Rectangle rect = Functions.getRect();
        GlobalEnv.tableattribute_popup.show(GlobalEnv.textPane, rect.x, rect.y + rect.height);
//        EventQueue.invokeLater(new Runnable() {
//            @Override public void run() {
//                SwingUtilities.getWindowAncestor(GlobalEnv.tableattribute_popup).toFront();
//                GlobalEnv.tableattribute_popup.requestFocusInWindow();
//            }
//        });
    }
    private void show_filePopup(MouseEvent e, int x, int y) {
        GlobalEnv.file_popup.show(e.getComponent(), x, y);// その位置情報を元にx,y座標を出す
    }
    private void show_newFilePopup(MouseEvent e, int x, int y) {
        Popup.newFile_popup.show(e.getComponent(), x, y);// その位置情報を元にx,y座標を出す
    }


    public void caretUpdate(CaretEvent e) {
        GlobalEnv.p = e.getDot();// キャレットの位置の取得
    }



    @Override
    public void valueChanged(TreeSelectionEvent e) {
        // JTreeクラスのgetLastSelectedPathComponent()メソッドで、
        // 選択されている要素を取得します。
        DefaultMutableTreeNode current = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        if (tree.getSelectionPath() != null) {
            if (!current.isRoot()) {
                try {
                    textArea.setText(Functions.myfileReader(current.toString()));
                    viewfilename_label.setText(current.toString());
                } catch (StringIndexOutOfBoundsException ex) {

                }
            }
            tabbed_Table.setSelectedIndex(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
