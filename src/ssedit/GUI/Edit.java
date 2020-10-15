package ssedit.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import ssedit.Common.GlobalEnv;

public class Edit extends JPanel {
	// とりあえずの設定値
	public int size = 12;
	public Font defaultEditFont = new Font("MSゴシック", Font.PLAIN, size);
	public static JTextPane linePane;
	// 現在の行数
	private static int line;

	public Edit() {
		setLayout(new BorderLayout());

		/* 編集領域の追加 */
		add(setDefaultTextPane(), BorderLayout.CENTER);

		/* 行数表示の追加 */
		linePane = new JTextPane();
		add(setDefaultLinePane(), BorderLayout.WEST);
		line = getLineNumber();
		GlobalEnv.textPane.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				setLinePane();
			}
		});
	}

	private JComponent setDefaultTextPane() {
		GlobalEnv.textPane.setFont(defaultEditFont);

		return GlobalEnv.textPane;
	}

	private JComponent setDefaultLinePane() {
		linePane.setFont(defaultEditFont);
		linePane.setEditable(false);

		// ボーダの作成
		Insets in = linePane.getMargin();
		Border border = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK);
		Border margin = BorderFactory.createEmptyBorder(in.top, in.left, in.bottom, in.right);
		linePane.setBorder(new CompoundBorder(border, margin));

		linePane.setFocusable(false);
		linePane.setVisible(false);

		return linePane;
	}

	public static void setLinePane() {
		int maxLine = getLineNumber();
		// 行数に変化があった時のみ更新
		if(line == maxLine){
			return;
		}
		int tarSpace = (int) Math.log10(maxLine);
		Document doc = new DefaultStyledDocument();
		SimpleAttributeSet attr = new SimpleAttributeSet();

		linePane.setDocument(doc);

		try {
			for (int i = 0; i < maxLine; i++) {
				int curSpace = (int) Math.log10(i + 1);
				for (int j = curSpace; j < tarSpace; j++) {
					attr.addAttribute(StyleConstants.Foreground, Color.WHITE);
					doc.insertString(doc.getLength(), "0", attr);
				}
				attr.addAttribute(StyleConstants.Foreground, Color.BLACK);
				doc.insertString(doc.getLength(), (i + 1) + "  \n", attr);
				// 現在の行数の更新
				line = maxLine;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		linePane.setVisible(true);
	}

	private static int getLineNumber() {
		Document doc = GlobalEnv.textPane.getDocument();
		return doc.getDefaultRootElement().getElementIndex(doc.getLength()) + 1;
	}

	public void setText() {
		defaultEditFont = new Font("MSゴシック", Font.PLAIN, size);
		GlobalEnv.textPane.setFont(defaultEditFont);
		GlobalEnv.textPane.setText(GlobalEnv.textPane.getText());
	}
}