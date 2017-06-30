package ssedit.Tree;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class TreeToQuery extends JFrame {
	static boolean bFlag = false;
	static int countB = 0;
	public static String q = "";
	ArrayList<DefaultMutableTreeNode> node;
	static String query = "";
	JTree tree;
//	static int counter = 0;

//	public static void main(String[] args) {
//		TreeToQuery frame = new TreeToQuery();
//
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setBounds(10, 10, 500, 400);
//		frame.setTitle("タイトル");
//		frame.setVisible(true);
//	}

	static String createQuery(DefaultMutableTreeNode leaf){
//		System.out.println("----------" + leaf + "----------");
		String leftElement = "", rightElement = "", element = "", connector = "";
//		counter++;
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) leaf.getParent();

		// 引数の葉ノードの親がルートノードで、その子が1つだったら→[e.name]!みたいな
		if(parent.isRoot()){
			if(parent.getChildCount() == 1){
				element = parent.getChildAt(0).toString();
				query = setQuery(element, "", parent.toString(), "");
				return query;
			}
//			else if(parent.getChildCount() == 2) {
//				leftElement = parent.getChildAt(1).toString();
//				rightElement = parent.getChildAt(0).toString();
//				System.out.println(leftElement);
//				System.out.println(rightElement);
//				query = setQuery(leftElement, rightElement, parent.toString(), parent.toString());
//			}

		}

		// 葉だったら2つを結合子を挟んで結合
		if(parent.getChildAt(0).isLeaf()){
			String str = "";
			DefaultMutableTreeNode grandparent = (DefaultMutableTreeNode)parent.getParent();

			// 引数の葉ノードの親ノードが　rootノードだったら
			if(parent.isRoot()){
				leftElement = parent.getChildAt(1).toString();
				rightElement = parent.getChildAt(0).toString();
				query = setQuery(leftElement, rightElement, parent.toString(), parent.toString());
				return query;
			// 親の親がrootノードだったら
			} else if(grandparent.isRoot()){
				// [e.id, e.name]!みたいな
				if(parent.getChildCount() == 2){
					leftElement = parent.getChildAt(1).toString();
					rightElement = parent.getChildAt(0).toString();
					query = setQuery(leftElement, rightElement, grandparent.toString(), parent.toString());
					return query;
				// {[e.name]!}とか
				} else {
					element = parent.getChildAt(0).toString();
					// [
					String left = parent.toString().substring(1, 2);
					// ]とその後の装飾子とか
					String right = parent.toString().substring(2);

					element = left + element + right;

					query = setQuery(element, "", grandparent.toString(), "");
					return query;
				}

			}
			// 親が2つの子を持っていたら
			if(parent.getChildCount()==2){
				leftElement = parent.getChildAt(1).toString();
				rightElement = parent.getChildAt(0).toString();
				connector = convertConnector(parent.toString());
				str = leftElement + connector + rightElement;
			} else {
				element = parent.getChildAt(0).toString();
				// [
				String left = parent.toString().substring(1, 2);
				// ]とその後の装飾子とか
				String right = parent.toString().substring(2);
				str = left + element + right;
			}

			if(grandparent.toString().startsWith("#[]") || grandparent.toString().startsWith("#{}")){
				DefaultMutableTreeNode grandgrandparent = (DefaultMutableTreeNode) grandparent.getParent();
				// [
				String left = grandparent.toString().substring(1, 2);
				// ]とその後の装飾子とか
				String right = grandparent.toString().substring(2);
				String s = "";
				String e = "";
				if(parent.getChildAt(0).toString().contains("#B#") || parent.getChildAt(1).toString().contains("#B#")){
//					System.out.println(parent.getChildAt(0));
//					System.out.println(parent.getChildAt(1));
					if (grandparent.toString().startsWith("#{}")) {
						countBrace(parent);
						for (int i = 0; i < countB; i++) {
							s += "\t";
						}
						for (int i = 1; i < countB; i++) {
							e += "\t";
						}
						countB = 0;
						left = left + "\n" + s;
						right = "\n" + e + right;
					}
				}
				str = left + str + right;
				if(grandparent.toString().startsWith("#{}")){
					str = "#B#" + str;
				}
				grandparent.removeAllChildren();
				grandparent.setUserObject(new DefaultMutableTreeNode(str));
//				System.out.println(str);
				createQuery((DefaultMutableTreeNode) grandgrandparent.getChildAt(0));
			} else {
				parent.removeAllChildren();
				parent.setUserObject(new DefaultMutableTreeNode(str));
				createQuery(grandparent.getLastLeaf());
			}

		} else {
			createQuery(((DefaultMutableTreeNode) parent.getChildAt(0)).getLastLeaf());
		}
		return query;
	}

	private static String setQuery(String left, String right, String key, String connector){
		connector = convertConnector(connector);
		String g1 = key.substring(1, 2);
		String g2 = key.substring(2);
//		System.out.println("l: " + left);
//		System.out.println("r: " + right);
//		System.out.println("k: " + key);
//		System.out.println("c: " + connector);
		if(key.startsWith("#[]")){
			query = g1 + left + connector + right + g2;
		} else if(key.equals("#{}")){
			if(right.equals("")){
				query = g1 + "\n\t" + left + "\n" + g2 + "\n";
			} else {
				query = g1 + "\n\t" + left + connector + right + "\n" + g2 + "\n";
			}
		} else {
//			System.out.println("in");
			query = left + connector + right;
		}
//		System.err.println("TFE = " + query);
		query = query.replaceAll("#B#", "");
		return 	query;
	}

	private static void countBrace(DefaultMutableTreeNode node) {
//		System.out.println("======" + node.getParent());
		if (node.getParent().toString().startsWith("#{}")) {
			countB++;
		}
		if (!((DefaultMutableTreeNode) node.getParent()).isRoot()) {
			countBrace((DefaultMutableTreeNode) node.getParent());
		} else {
			return;
		}
	}

	private static String convertConnector(String connector){

		switch (connector) {
		case "#,":
			connector = ", ";
			break;
		case "#!":
//			connector = "! \n";
			connector = "! ";
			break;
		case "#%":
			connector = "% ";
			break;
		}

		return connector;
	}


//	public void traverse(JTree tree) {
//		TreeModel model = tree.getModel();
//		Object root;
//		if (model != null) {
//			root = model.getRoot();
//			walk(model, root);
//		} else {
//			System.out.println("Tree is empty.");
//		}
//	}

//	// %をルートとした新規ノードをごとーさんに引数として渡す
//
//	protected void walk(TreeModel model, Object o) {
//		int cc = model.getChildCount(o);
//		for (int i = 0; i < cc; i++) {
//			DefaultMutableTreeNode child = (DefaultMutableTreeNode) model
//					.getChild(o, i);
//			if(child.toString().equals("#C3")){
//				// 深度演算子以下のノードをツリーから切り離す
//
////				DefaultMutableTreeNode partNode = (DefaultMutableTreeNode) child.clone();
////				DefaultMutableTreeNode x = new DefaultMutableTreeNode(child.getChildAt(0));
////				partNode.add(x);
////				partNode.add((DefaultMutableTreeNode) child);
//				partNode = (DefaultMutableTreeNode) child;
////				node2.add(partNode);
////				node2.get(0).removeFromParent();
//				node2 = node;
//				node2.get(0).removeAllChildren();
////				partNode.removeFromParent();
////				traverse(a);
//
////				createQuery(partNode.getLastLeaf());
//			}
//			if (model.isLeaf(child)) {
////				System.out.println(child);
//			} else {
////				System.out.println(child);
//				walk(model, child);
//			}
//		}
//		reload(node.get(0));
////		reload(partNode);
////		reload(node2.get(0));
////		System.out.println(partNode.getParent());
//
//	}
//
//	//copyOfTree
//	//return: Deep copy tree
//	private static ArrayList<DefaultMutableTreeNode> copyOfTree(ArrayList<DefaultMutableTreeNode> tree) {
//		ArrayList<DefaultMutableTreeNode> copy = new ArrayList<DefaultMutableTreeNode>();
//		for(DefaultMutableTreeNode x : tree)
//			copy.add(copyNode(x));
//		return copy;
//	}
//
//	//copyNode
//	private static DefaultMutableTreeNode copyNode(DefaultMutableTreeNode node){
//	    DefaultMutableTreeNode copy = new DefaultMutableTreeNode(node.toString());
//    	if(!node.isLeaf()){
//	        for(int i=0; i<node.getChildCount(); i++)
//	        	copy.add(copyNode((DefaultMutableTreeNode) node.getChildAt(i)));
//    	}
//	    return copy;
//	}


}