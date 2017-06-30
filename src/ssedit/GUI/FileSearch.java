package ssedit.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import ssedit.FrontEnd;
import ssedit.Common.Functions;
import ssedit.Common.GlobalEnv;

public class FileSearch extends JFrame implements TreeCellRenderer, MouseListener {

    int lineCount;
    DefaultStyledDocument style;
	MutableAttributeSet attr = new SimpleAttributeSet();

	JTextField targetField = new JTextField();
	JLabel stateLabel;
	JTree searchTree;
	DefaultTreeModel treemodel;
	DefaultMutableTreeNode root = new DefaultMutableTreeNode(GlobalEnv.folderPath);

//	List<String> fileNameList = new ArrayList<String>();
	List<String> nodeList = new ArrayList<String>();

	FileSearch() {
		// ツール名を記載
		setTitle("検索");
		setBounds(10, 10, 510, 350);
		// 起動時に画面中央に表示
		setLocationRelativeTo(null);

		targetField.setPreferredSize(new Dimension(200, 30));
		treemodel = new DefaultTreeModel(root);

		searchTree = new JTree(treemodel);
		searchTree.setCellRenderer(this);
		searchTree.addMouseListener(this);

		JPanel mainPanel = new JPanel();
		JLabel searchLabel = new JLabel("検索：");
		stateLabel = new JLabel("検索結果：");
		JButton searchButton = new JButton("検索");
		JPanel searchPanel = new JPanel();
		searchPanel.add(searchLabel);
		searchPanel.add(targetField);
		searchPanel.add(searchButton);
		JPanel statePanel = new JPanel();
		statePanel.add(stateLabel);
		mainPanel.add(searchPanel);
		mainPanel.add(statePanel);

		JScrollPane scrollpane = new JScrollPane(searchTree,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane.setPreferredSize(new Dimension(400, 250));

		mainPanel.add(scrollpane);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));// masato追加　縦にパネルを配置



		Container contentPane = getContentPane();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



		// ウィンドウの表示をオン
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // windowClosingが呼ばれた後になにもしない(終了しない)
				setVisible(false);
			}
		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				root.removeAllChildren();
				String target = targetField.getText();
		    	File dir = new File(GlobalEnv.folderPath);
				File[] files = dir.listFiles();
				for(int i = 0; i < files.length; i++){
					if((files[i].toString().endsWith(".ssql") || files[i].toString().endsWith(".sql"))){
						String query = "";
						try {
							BufferedReader br = new BufferedReader(new InputStreamReader(
									new FileInputStream(files[i]), "UTF-8"));
							String line;
							while ((line = br.readLine()) != null) {
								query += line + "\n";
							}
							searchKeyword(files[i].toString(), target, query);
							br.close();
						} catch (IOException e1) {
						}
					}
				}
					stateLabel.setText("検索結果: " + root.getChildCount() + "件の一致");
			}
		});

	}

	public void searchKeyword(String file, String regex, String query){
		nodeList.clear();
		String filename = "";
		//検索
        Pattern pattern = Pattern.compile(regex);

        Matcher match = pattern.matcher(query);
        while(match.find()){
//            System.out.println("ファイル名：" + filename);
//        	  System.out.println("位置：" + match.start());

        	  // クエリの検索文字の出現位置までの文字列
        	  String target2 = query.substring(0, match.start());
        	  // クエリの検索文字の出現位置から最後までの文字列
        	  String target3 = query.substring(match.start(), query.length());
              String regex2 = "\n";
              Pattern pattern2 = Pattern.compile(regex2);
              Matcher match2 = pattern2.matcher(target2);
              lineCount = 1;
              // クエリの検索文字の出現位置までの文字列内で最後に改行の出現する場所
              int retPosition =0;
              while(match2.find()){
            	  lineCount++;
            	  retPosition = match2.end();
              }
//              System.out.println("行数：" + lineCount);
//              System.out.println("行：" + query.substring(retPosition, target2.length() + target3.indexOf("\n")));
//              String line = query.substring(retPosition, target2.length() + target3.indexOf("\n"));
//              System.out.println("指定行内の位置：" + line.indexOf(regex) + "〜" + (line.indexOf(regex) + regex.length()));
              filename = file.substring(file.lastIndexOf("/") + 1);
//              System.out.println(filename);
              String nodeInfo = lineCount + ": " + query.substring(retPosition, target2.length() + target3.indexOf("\n"));
//              System.out.println(nodeInfo);
              nodeList.add(nodeInfo);

        }
        if(!nodeList.isEmpty()){
        	createTree(filename, nodeList);
        }

	}


	public void createTree(String filename, List<String> nodeList) {
//        System.out.println(filename);
//        System.out.println(nodeList);

        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(nodeList);

		DefaultMutableTreeNode file =  new DefaultMutableTreeNode(filename);

		Iterator<String> it = hashSet.iterator();
        while (it.hasNext()) {
			DefaultMutableTreeNode node =  new DefaultMutableTreeNode(it.next());
			file.add(node);
        	}
//		for(int i = 0; i < nodeList.size(); i++){
//			DefaultMutableTreeNode node =  new DefaultMutableTreeNode(nodeList.get(i));
//			file.add(node);
//		}
		root.add(file);
		treemodel = new DefaultTreeModel(root);
		searchTree.setModel(treemodel);
		expandAll(searchTree);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		JTextPane textPane = new JTextPane();
		style = new DefaultStyledDocument();
		textPane.setDocument(style);
		textPane.setText(value.toString());

		if(leaf){
			StyleConstants.setForeground(attr, Color.BLACK);
			StyleConstants.setBackground(attr, Color.getHSBColor((float) 0.58, (float) 0.32, 1));
			String target = targetField.getText();
//			System.out.println(value);
//			if(value.toString().contains(target)){
//				style.setCharacterAttributes(value.toString().indexOf(target), target.length(), attr, true);
//
//			}
			Matcher m;
			m = Pattern.compile(target).matcher(value.toString());
			while(m.find()) {
				style.setCharacterAttributes(m.start(), m.end()-m.start(), attr, true);
			}

		}
		if(selected){
			StyleConstants.setBackground(attr, Color.getHSBColor((float) 0.58, 1, 1));
			StyleConstants.setForeground(attr, Color.WHITE);
			style.setCharacterAttributes(0, value.toString().length(), attr, true);
		}

		return textPane;
	}

	static void expandAll(JTree tree) {
		  int row = 0;
		  while (row < tree.getRowCount()) {
		    tree.expandRow(row);
		    row++;
		  }
		}

	@Override
	public void mouseClicked(MouseEvent e) {
		JTree tree = (JTree) e.getSource();
		if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2){
			DefaultMutableTreeNode current = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//			String file = GlobalEnv.folderPath + GlobalEnv.OS_FS + current.getParent();
//			System.out.println(current);
//			System.out.println(Common.readFile(GlobalEnv.folderPath + GlobalEnv.OS_FS + current.getParent().toString()));
			// ファイルが変更されていたら
			if(!FrontEnd.filestateLabel2.getText().equals("")){
				ActionEvent ae = new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED, "保存チェック");
				FrontEnd.tree_actionPerformed(ae);
			}
			String tmpText = Functions.myfileReader(current.getParent().toString());
			// 選択したファイルが表示してるファイルと同じだったら
			if(GlobalEnv.textPane.getText().trim().equals(tmpText.trim())){

			// 違ったら
			} else {
				GlobalEnv.textPane.setText(tmpText);
				FrontEnd.filenameLabel.setText(current.getParent().toString());
				// 現在のデータにツリーから選択したファイルの中身を入れる
				FrontEnd.currentfileData = tmpText;
				FrontEnd.currentfileName = current.getParent().toString();
			}


		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}


}