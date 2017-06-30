package ssedit.Tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class DisplayTree extends JFrame {

	public DisplayTree(ArrayList<DefaultMutableTreeNode> n) {
		showTree(n.get(0));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 10, 500, 400);
		setTitle( !n.isEmpty()? "From QueryToTree" : "null" );
		setVisible(true);
	}

	private void showTree(DefaultMutableTreeNode node0) {
		JTree tree = new JTree(node0);
		expandAll(tree);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(tree);
		scrollPane.setPreferredSize(new Dimension(300, 300));

		JPanel p = new JPanel();
		p.add(scrollPane);

		getContentPane().add(p, BorderLayout.CENTER);
	}

	private void expandAll(JTree tree) {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}
}