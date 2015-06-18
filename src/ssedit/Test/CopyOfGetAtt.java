package ssedit.Test;
//package ssqltool.Test;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Container;
//import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//
//import javax.swing.AbstractAction;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
//import javax.swing.WindowConstants;
//import javax.swing.text.MutableAttributeSet;
//import javax.swing.text.SimpleAttributeSet;
//import javax.swing.text.StyleConstants;
//
//public class CopyOfGetAtt extends JFrame {
//
//	JTextField attField = new JTextField();
//	String currentTarget = "";
//
//
//	//	getatt(String att) {
//
//	public static void main(String[] args) {
//		CopyOfGetAtt frame = new CopyOfGetAtt("attを入力してください");
//		frame.setVisible(true);
//	}
//
//	CopyOfGetAtt(String title){
//
//		setTitle(title);
//		setBounds(10, 10, 250, 100);
//		// 起動時に画面中央に表示
//		setLocationRelativeTo(null);
//
////		GlobalEnv.currnetTarget = target;
//		attField.setText(GlobalEnv.currnetTarget);
//
//		attField.setPreferredSize(new Dimension(100, 30));
////		targetField2.setPreferredSize(new Dimension(100, 30));
//
////		targetField.addCaretListener(this);
//
//
//		JPanel mainPanel = new JPanel();
//		JLabel attLabel = new JLabel("att=");
////		JButton searchButton = new JButton("検索");
//		JButton decisionButton = new JButton("決定");
//		mainPanel.add(attLabel);
//		mainPanel.add(attField);
//		mainPanel.add(decisionButton);
//
////		mainPanel.add(scroll);
//
//		Container contentPane = getContentPane();
//		contentPane.add(mainPanel, BorderLayout.CENTER);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//
//
//		// ウィンドウの表示をオン
////		setVisible(true);
//
//		addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // windowClosingが呼ばれた後になにもしない(終了しない)
//				GlobalEnv.currnetTarget = "";
//				setVisible(false);
//			}
//		});
//
//	}
//}
