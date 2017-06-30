package ssedit.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.JTextPane;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndentTestGUI extends JFrame {

	static JTextPane textpane;
	JTextPane dispane;

	public static void main(String args[]){

		//オブジェクトが作成され、コンストラクタ内の処理が行われる
		IndentTestGUI frame = new IndentTestGUI("インデントテスト");
		frame.setVisible(true);

	}

	//コンストラクタ
	IndentTestGUI(String title){
		setTitle(title);
		setBounds(100, 100, 600, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainpanel = new JPanel();

		//クエリ画面
		textpane = new JTextPane();
		JScrollPane scrollpane1 = new JScrollPane(textpane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane1.setPreferredSize(new Dimension(400, 200));
		textpane.setText("GENERATE HTML\n"+
						"[e.name % e.salary@{color=red, width=100}]!\n"+
						"FROM employee e;");

		//取得したものを表示する画面
		dispane = new JTextPane();
		dispane.setEditable(false);
		dispane.setBackground(Color.LIGHT_GRAY);
		JScrollPane scrollpane2 = new JScrollPane(dispane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane2.setPreferredSize(new Dimension(400, 200));

		JButton indentbutton = new JButton("整形！");

		//取得ボタンを押したとき
		indentbutton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent event){
				//テキストの取得
				String target = textpane.getText();
//				Indent.process(target);
				Indent2.process(target);
//				Indent.process(GlobalEnv.doc);


				//TODO 整形後のクエリを表示
				dispane.setText(target);
//				System.out.println(target);

			}


		});



		mainpanel.add(scrollpane1);
		mainpanel.add(scrollpane2);
		mainpanel.add(indentbutton);

		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.PAGE_AXIS));

		Container contentPane = getContentPane();
		contentPane.add(mainpanel, BorderLayout.CENTER);

	}


}


