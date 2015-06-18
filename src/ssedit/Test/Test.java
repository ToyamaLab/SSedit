package ssedit.Test;
//package ssqltool.Test;
//
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import javax.swing.JTextPane;
//import javax.swing.JViewport;
//import javax.swing.plaf.TextUI;
//import javax.swing.text.DefaultStyledDocument;
//import javax.swing.text.StyleContext;
//
//import java.awt.Button;
//import java.awt.Container;
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//class Test extends JFrame {
//
//	protected static JTextPane textPane = new JTextPane() {
//		// textPaneでも横スクロールが出現
//		@Override
//		public boolean getScrollableTracksViewportWidth() {
//			Object parent = getParent();
//			if (parent instanceof JViewport) {
//				JViewport port = (JViewport) parent;
//				int w = port.getWidth(); // 表示できる範囲(上限)
//
//				TextUI ui = getUI();
//				Dimension sz = ui.getPreferredSize(this); // 実際の文字列サイズ
//				if (sz.width < w) {
//					return true;
//				}
//			}
//			return false;
//		}
//	};
//
//	StyleContext sc = new StyleContext();
//	DefaultStyledDocument doc = new DefaultStyledDocument(sc);
//
//	JButton button;
//
//	public static void main(String args[]) {
//		Test frame = new Test("タイトル");
//		frame.setVisible(true);
//	}
//
//	Test(String title) {
//		setTitle(title);
//		setBounds(100, 100, 700, 350);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//		textPane.setDocument(doc);
//
//		JScrollPane scrollpane = new JScrollPane(textPane,
//				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		scrollpane.setPreferredSize(new Dimension(600, 200));
//
//		JPanel p = new JPanel();
//		button = new JButton("インデント");
//
//		textPane.setText("GENERATE HTML {[h.name@  {width=30}! h.area]!} FROM hotel h;");
//		p.add(scrollpane);
//		p.add(button);
//
//		Container contentPane = getContentPane();
//		contentPane.add(p, BorderLayout.CENTER);
//
//		button.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				StringBuilder sb = new StringBuilder(textPane.getText());
//				insertNewLine("!", sb);
////				insertNewLine("\\{", sb);
//				 System.out.println(sb);
//			}
//
//		});
//	}
//
//	public void insertNewLine(String regex, StringBuilder text) {
//		List<Integer> counter = new ArrayList<Integer>();
//		Matcher m;
//		m = Pattern.compile(regex).matcher(text);
//
//		// !が見つかったら
//		while (m.find()) {
//			String tmp = text.substring(0, m.start()).trim();
//			String ch = tmp.substring(tmp.length()-1, tmp.length());
//			System.out.println(ch);
//			if (ch.equals("@")) {
//				{
//
////					System.out.println(tmp);
//				}
//			}
//			counter.add(m.end());
//
//		}
//		for (int i = 0; i < counter.size(); i++) {
//			text.insert(counter.get(i) + i, "\n");
//		}
//	}
//
//}