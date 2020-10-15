package ssedit.LinkForeach;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleContext;

import ssedit.FrontEnd;
import ssedit.Common.GlobalEnv;

public class GetAtt extends JFrame implements CaretListener {

	static JTextPane linkPane = new JTextPane() {
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
	JTextPane foreachPane = new JTextPane() {
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

	JTextField linkfilename_Field = new JTextField("");
	JTextField foreachfilename_Field = new JTextField("");

	JTextField attField = new JTextField("");

	StringBuffer linkBuffer = new StringBuffer();
	StringBuffer foreachBuffer = new StringBuffer();

	StringBuffer foreachfilename_Buffer = new StringBuffer();

	StyleContext sc, sc2;
	DefaultStyledDocument linkDoc, foreachDoc;
	SimpleAttributeSet attr = new SimpleAttributeSet();
	String linkQuery = "";
	int finalPos;

	static List<String> foreachquery_array;

	JComboBox<String> foreachCombo;
	int current = 0;

	private static String linkfilename = "";
	private static ArrayList<String> foreachfilename;

	// getatt(String att) {

	// public static void main(String[] args) {
	// GetAtt frame = new GetAtt();
	// frame.setVisible(true);
	// }

	public GetAtt(String linkQuery, String foreachQuery, ArrayList<String> linkforeach_filename) {

		setTitle("att指定画面");
		setBounds(10, 10, 650, 400);
		// 起動時に画面中央に表示
		setLocationRelativeTo(null);

		foreachCombo = new JComboBox<String>();
		foreachquery_array = new ArrayList<>();

		sc = new StyleContext();
		linkDoc = new DefaultStyledDocument(sc);
		linkPane.setDocument(linkDoc);
		sc2 = new StyleContext();
		foreachDoc = new DefaultStyledDocument(sc2);
		foreachPane.setDocument(foreachDoc);

		attField.setPreferredSize(new Dimension(200, 30));

		finalPos = GlobalEnv.linkattpos_array.get(0);
		finalPos = GlobalEnv.linkattpos_array2.get(0);

		linkPane.setText(linkQuery);
		foreachPane.setText(foreachQuery);
		linkPane.setEditable(false);
		foreachPane.setEditable(false);

		for (int i = 1; i <= GlobalEnv.foreach_array.size(); i++) {
			foreachCombo.addItem(i + "番目");
		}
		for (int i = 0; i < GlobalEnv.foreach_array.size(); i++) {
			GlobalEnv.attInput.add(i, "");
		}
		for (int i = 0; i < GlobalEnv.foreach_array.size(); i++) {
			foreachquery_array.add(i, GlobalEnv.foreach_array.get(i));
		}

		JScrollPane linkscrollpane = new JScrollPane(linkPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		linkscrollpane.setPreferredSize(new Dimension(500, 150));
		JScrollPane foreachscrollpane = new JScrollPane(foreachPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		foreachscrollpane.setPreferredSize(new Dimension(400, 150));

		JPanel mainPanel = new JPanel();
//		JPanel linkfilename_Panel = new JPanel();
//		JPanel foreachfilename_Panel = new JPanel();
		JPanel foreachPanel = new JPanel();
		JPanel attPanel = new JPanel();

		foreachfilename = new ArrayList<>();

		//TODO 分割生成されたファイルの名前について
		for (int i = 0; i < linkforeach_filename.size() ; i++) {
			if (i == 0) {
				linkfilename = linkforeach_filename.get(i);
			}else if(i > 0){
					foreachfilename.add(linkforeach_filename.get(i));
			}
		}
		System.err.println(foreachfilename);
		linkfilename_Field.setText(linkfilename);
		linkfilename_Field.setPreferredSize(new Dimension(150, 25));
		foreachfilename_Field.setText(foreachfilename.get(0));
		foreachfilename_Field.setPreferredSize(new Dimension(150, 25));


//		JLabel linkfilename_Label = new JLabel("ファイル名を入力してください");

		JLabel attLabel = new JLabel("attを指定してください");
		JButton decisionButton = new JButton("決定");

//		mainPanel.add(linkfilename_Panel);
//		linkPanel.add(linkfilename_Label);
//		linkfilename_Panel.add(linkfilename_Field);
		mainPanel.add(linkscrollpane);
//		mainPanel.add(foreachfilename_Panel);
//		foreachfilename_Panel.add(foreachfilename_Field);
		mainPanel.add(foreachPanel);
		foreachPanel.add(foreachCombo);
		foreachPanel.add(foreachscrollpane);
		mainPanel.add(attPanel);
		attPanel.add(attLabel);
		attPanel.add(attField);
		attPanel.add(decisionButton);

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		Container contentPane = getContentPane();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ウィンドウの表示をオン
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// windowClosingが呼ばれた後になにもしない(終了しない)
				setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				GlobalEnv.currnetTarget = "";
				setVisible(false);
			}
		});

		attField.addCaretListener(this);

		foreachCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalEnv.fullLink = linkPane.getText();
				int count=0;
				for(int i = 1; i < GlobalEnv.linkattpos_array.size(); i++){
					count+=getAtts(GlobalEnv.attInput.get(i-1)).length();
					GlobalEnv.linkattpos_array.set(i, GlobalEnv.linkattpos_array2.get(i) + count);
				}
				current = (int) foreachCombo.getSelectedIndex();
				foreachPane.setText(foreachquery_array.get(current));
				attField.setText(GlobalEnv.attInput.get(current));
//				System.err.println(linkPane.getText());
//				System.err.println(GlobalEnv.linkattpos_array.get(current) + ":" + getAtts(attField.getText()).length());
				try {
					linkDoc.replace(GlobalEnv.linkattpos_array.get(current), getAtts(attField.getText()).length() * 2, "", attr);
				} catch (BadLocationException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
//				System.out.println(linkPane.getText());
				GlobalEnv.fullLink = linkPane.getText();
				attField.requestFocus();

				foreachfilename_Field.setText(foreachfilename.get(current));
			}
		});

		decisionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String linkQuery = linkPane.getText();
				LinkForEach.createFile(linkQuery, foreachquery_array);
				FrontEnd.reloadFolderTree();

			}
		});

	}

	@Override
	public void caretUpdate(CaretEvent e) {
		String atts = getAtts(attField.getText());
		linkBuffer = new StringBuffer();
		linkBuffer.append(GlobalEnv.fullLink);
		linkBuffer.insert(GlobalEnv.linkattpos_array.get(current), atts);
		linkPane.setText(linkBuffer.toString());
		foreachBuffer = new StringBuffer();
		foreachBuffer.append(GlobalEnv.foreach_array.get(current));
		foreachBuffer.insert(GlobalEnv.foreachpos_array.get(current),
				attField.getText());
		foreachPane.setText(foreachBuffer.toString());
		GlobalEnv.attInput.set(current, attField.getText());
		foreachquery_array.set(current, foreachBuffer.toString());




	}

	private static String getAtts(String atts) {
		String att = "";
		if (atts.isEmpty()) {
			return att;
		} else {
			if (!atts.contains(",")) {
				// ex) att=e.id
				att = "att=" + atts;
			} else {
				// ex) att1=e.id, att2=e.name
				int i = 0;
				while (atts.contains(",")) {
					att += "att" + (++i) + "="
							+ atts.substring(0, atts.indexOf(",") + 1).trim()
							+ " ";
					atts = atts.substring(atts.indexOf(",") + 1);
				}
				att += "att" + (++i) + "=" + atts.trim();
			}
		}
		return att;
	}

}
