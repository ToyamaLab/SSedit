package ssedit.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import ssedit.Common.Functions;
import ssedit.Common.GlobalEnv;

public class Search extends JFrame implements CaretListener {

	SimpleAttributeSet attr = new SimpleAttributeSet();
	static MutableAttributeSet attr2 = new SimpleAttributeSet();

	JTextField targetField = new JTextField();
	JTextField targetField2 = new JTextField();
	String currentTarget = "";

//	public static void main(String[] args) {
//		new test();
//	}

	public Search(String target) {
		// ツール名を記載
		setTitle("検索");
		setBounds(10, 10, 510, 100);
		// 起動時に画面中央に表示
		setLocationRelativeTo(null);

//		GlobalEnv.regex[0] = new JCheckBox("正規表現を用いる");


		StyleConstants.setItalic(attr2, true);
		StyleConstants.setBold(attr2, true);
		StyleConstants.setBackground(attr2, Color.YELLOW);

		GlobalEnv.currnetTarget = target;
		targetField.setText(GlobalEnv.currnetTarget);

		targetField.setPreferredSize(new Dimension(200, 30));
		targetField2.setPreferredSize(new Dimension(200, 30));

		targetField.addCaretListener(this);

		GlobalEnv.regex[0].addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalEnv.searchStart = 0;
			}
		});
		GlobalEnv.caseSensitivity[0].addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalEnv.searchStart = 0;
			}
		});


		JPanel mainPanel = new JPanel();
		JLabel searchLabel = new JLabel("検索：");
		JLabel replaceLabel = new JLabel("置換：");
//		JButton searchButton = new JButton("検索");
		JButton searchButton = new JButton("検索");
		JButton searchAllButton = new JButton("一括検索");
		JButton replaceButton = new JButton("置換");
		mainPanel.add(searchLabel);
		mainPanel.add(targetField);
		mainPanel.add(GlobalEnv.regex[0]);
		mainPanel.add(GlobalEnv.caseSensitivity[0]);
		mainPanel.add(replaceLabel);
		mainPanel.add(targetField2);
		mainPanel.add(searchButton);
		mainPanel.add(searchAllButton);
		mainPanel.add(replaceButton);

//		mainPanel.add(scroll);

		Container contentPane = getContentPane();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



		// ウィンドウの表示をオン
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // windowClosingが呼ばれた後になにもしない(終了しない)
				GlobalEnv.currnetTarget = "";
				setVisible(false);
			}
		});

//		searchButton.addActionListener(new AbstractAction() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				String target = targetField.getText();
//				if(!target.equals(currentTarget)){
//					currentTarget = target;
//					GlobalEnv.searchStart = 0;
//				}
//
//			System.out.println("検索文字：" + currentTarget);
//			System.out.println("検索開始位置：" + GlobalEnv.searchStart);
//			if(GlobalEnv.regex[0].isSelected()){
//				Common.searchWordRegex(currentTarget, GlobalEnv.searchStart);
//			} else {
//				Common.searchWord(currentTarget, GlobalEnv.searchStart);
//			}
//			}
//		});

		searchButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String target = targetField.getText();
				String target2 = targetField2.getText();

				if(!target.equals(currentTarget)){
					currentTarget = target;
					GlobalEnv.searchStart = 0;
				}
				// 正規表現検索なら
				if(GlobalEnv.regex[0].isSelected()){
					// 選択されていなかったら(最初の検索)
					if(GlobalEnv.textPane.getSelectedText() == null){
						Functions.searchWordRegex(currentTarget, 0);
						return;
					}
					// なんか選択されていたら
					else {
						String text = GlobalEnv.textPane.getSelectedText();
						if(!GlobalEnv.caseSensitivity[0].isSelected()){
							text = text.toLowerCase();
							target = target.toLowerCase();
						}
						Pattern p = Pattern.compile(target);
						Matcher m = p.matcher(text);
//						System.out.println(target);
//						System.out.println(text);
						if(!m.find()){
							Functions.searchWordRegex(currentTarget, 0);
							return;
						}
					}

					int size = targetField.getText().length() - targetField2.getText().length();
//					System.out.println(size);
//					try {
//						GlobalEnv.doc.replace(GlobalEnv.textPane.getSelectionStart(), targetField.getText().length(), targetField2.getText(), attr);
						GlobalEnv.searchStart = GlobalEnv.searchStart - size;
						GlobalEnv.searchStart++;
						Functions.searchWordRegex(target, GlobalEnv.searchStart);
//					} catch (BadLocationException e1) {
//						// TODO 自動生成された catch ブロック
//						e1.printStackTrace();
//					}
				}
				// 通常検索なら
				else {
					String text = GlobalEnv.textPane.getSelectedText();
					if(text == null){
						Functions.searchWord(currentTarget, 0);
						return;
					}
					if(!GlobalEnv.caseSensitivity[0].isSelected()){
						text = text.toLowerCase();
						target = target.toLowerCase();
					}
					if(!target.equals(text)){
//						System.out.println("sss");
						Functions.searchWord(currentTarget, GlobalEnv.searchStart);
						return;
					}
					int size = target.length() - targetField2.getText().length();
//					try{
//						GlobalEnv.doc.replace(GlobalEnv.textPane.getSelectionStart(), target.length(), target2, attr);
						GlobalEnv.searchStart = GlobalEnv.searchStart - size;
						GlobalEnv.searchStart++;
						Functions.searchWord(target, GlobalEnv.searchStart);
//					} catch (BadLocationException e1) {
//						e1.printStackTrace();
//					}
				}
			}
		});

		searchAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new FileSearch();
			}
		});

		replaceButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String target = targetField.getText();
				String target2 = targetField2.getText();

				if(!target.equals(currentTarget)){
					currentTarget = target;
					GlobalEnv.searchStart = 0;
				}
				// 正規表現検索なら
				if(GlobalEnv.regex[0].isSelected()){
					// 選択されていなかったら(最初の検索)
					if(GlobalEnv.textPane.getSelectedText() == null){
						Functions.searchWordRegex(currentTarget, 0);
						return;
					}
					// なんか選択されていたら
					else {
						String text = GlobalEnv.textPane.getSelectedText();
						if(!GlobalEnv.caseSensitivity[0].isSelected()){
							text = text.toLowerCase();
							target = target.toLowerCase();
						}
						Pattern p = Pattern.compile(target);
						Matcher m = p.matcher(text);
//						System.out.println(target);
//						System.out.println(text);
						if(!m.find()){
							Functions.searchWordRegex(currentTarget, 0);
							return;
						}
					}

					int size = targetField.getText().length() - targetField2.getText().length();
//					System.out.println(size);
					try {
						GlobalEnv.doc.replace(GlobalEnv.textPane.getSelectionStart(), targetField.getText().length(), targetField2.getText(), attr);
					GlobalEnv.searchStart = GlobalEnv.searchStart - size;
					GlobalEnv.searchStart++;
					Functions.searchWordRegex(target, GlobalEnv.searchStart);
					} catch (BadLocationException e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
					}
				}
				// 通常検索なら
				else {
					String text = GlobalEnv.textPane.getSelectedText();
					if(text == null){
						Functions.searchWord(currentTarget, 0);
						return;
					}
					if(!GlobalEnv.caseSensitivity[0].isSelected()){
						text = text.toLowerCase();
						target = target.toLowerCase();
					}
					if(!target.equals(text)){
//						System.out.println("sss");
						Functions.searchWord(currentTarget, GlobalEnv.searchStart);
						return;
					}
					int size = target.length() - targetField2.getText().length();
					try{
						GlobalEnv.doc.replace(GlobalEnv.textPane.getSelectionStart(), target.length(), target2, attr);
						GlobalEnv.searchStart = GlobalEnv.searchStart - size;
					GlobalEnv.searchStart++;
					Functions.searchWord(target, GlobalEnv.searchStart);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

	}

	@Override
	public void caretUpdate(CaretEvent e) {
		GlobalEnv.currnetTarget = targetField.getText();
//		 GlobalEnv.doc.setCharacterAttributes(0, GlobalEnv.doc.getLength(), attr, true);
//		String target = targetField.getText();
//		if(regex[0].isSelected()){
//			incrementalSearchRegex(target);
//		}
//		incrementalSearch(target, GlobalEnv.start);
	}

	static public void incrementalSearch(String target, int start) {
		String text = GlobalEnv.textPane.getText();
		if(!GlobalEnv.caseSensitivity[0].isSelected()){
			text = text.toLowerCase();
			target = target.toLowerCase();
		}

		text = text.substring(start);

		if(target.equals("")){
			return;
		}
			if(text.indexOf(target) != -1){
				GlobalEnv.doc.setCharacterAttributes(text.indexOf(target) + start, target.length(), attr2, false);
//				start += (text.indexOf(target) + target.length());
				start = start + (text.indexOf(target) + 1);
				incrementalSearch(target, start);
			} else {
				start = 0;
			}

	}

	public static void incrementalSearchRegex(String target) {
//		System.out.println(target);
		Matcher m;
		String text = GlobalEnv.textPane.getText();
		try {
			if(!GlobalEnv.caseSensitivity[0].isSelected()){
				text = text.toLowerCase();
				target = target.toLowerCase();
			}
			m = Pattern.compile(target)
					.matcher(text);
			while(m.find()) {
				GlobalEnv.doc.setCharacterAttributes(m.start(), m.end()-m.start(), attr2, false);
			}

		} catch (PatternSyntaxException e){
//			System.out.println("error");
		}
	}

	public static void replace(String target1, String target2){

	}


}