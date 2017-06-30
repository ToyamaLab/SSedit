package ssedit.Tree;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import ssedit.Common.Log;

public class CreateTree {

	private static boolean dq = false;
	private static boolean sq = false;
	private static char preC = ' ';
	private static char nextC = ' ';

	static ArrayList<DefaultMutableTreeNode> node = new ArrayList<>();
	private static int nodeCount = 0;
	//private static ArrayList<Integer> parentNode = new ArrayList<>();
	//private static int parent = -1;
	private static int d = 0;	//depth

	public static void main(String[] args) {
		Log.debug = 3;	//デバッグモードレベル(0〜3)の指定

		String tfe = "[e.id % {e.id! {e.name, e.byear}}]!";
//		String tfe = "[e.id,e.id2,e.id3,e.id4]!";
		//String tfe = "[e.id,e.id2!{e.id3,e.id4}]!";
		process(tfe);
	}

	public CreateTree() {

	}

	public static void process(String t) {
		t = deleteAllCommentedOutCodes(t);
		preProcess();
		createTreeProcess(t);
	}

	//delete all commented out codes
	static String deleteAllCommentedOutCodes(String q) {
		String[] lines = q.split("\n");
		String line = "";
		String tmp = "";

		for (int j = 0; j < lines.length; j++) {
			line = lines[j];

			if (line.contains("/*")) {
				String line1 = line.substring(0, line.indexOf("/*"));
				while (!line.contains("*/"))
					line = lines[++j];
				line = line1 + line.substring(line.indexOf("*/") + 2);
			}

			if (line.contains("--") || line.contains("\\\"")
					|| line.contains("\"\"")) { // commentOutLetters = "--"
				boolean dqFlg = false;
				int i = 0;
				for (i = 0; i < line.length(); i++) {
					if (!dqFlg
							&& line.charAt(i) == '"'
							&& i > 0
							&& i < line.length() - 1
							&& (line.charAt(i - 1) != '\\'
									&& line.charAt(i - 1) != '"' && line
									.charAt(i + 1) != '"')) // omit \" and ""
						dqFlg = true;
					else if (dqFlg
							&& line.charAt(i) == '"'
							&& i > 0
							&& i < line.length() - 1
							&& (line.charAt(i - 1) != '\\'
									&& line.charAt(i - 1) != '"' && line
									.charAt(i + 1) != '"')) // omit \" and ""
						dqFlg = false;

					if (dqFlg
							&& i > 0
							&& (line.charAt(i - 1) == '\\' || line
									.charAt(i - 1) == '"')
							&& line.charAt(i) == '"') // if \" or ""
						line = line.substring(0, i - 1) + "&quot;"
								+ line.substring(i + 1, line.length());
					else if (!dqFlg && i > 0 && line.charAt(i - 1) == '<'
							&& line.charAt(i) == '$') { // if <$
						// line =
						// line.substring(0,i-1)+"&quot;"+line.substring(i+1,line.length());
					} else if (!dqFlg && i < line.length() - 1
							&& line.charAt(i) == '-'
							&& line.charAt(i + 1) == '-')
						break;
				}
				line = line.substring(0, i);
			}

			if (!line.trim().isEmpty())
				tmp += line + "\n";
		}
		return tmp;
	}

	private static void preProcess() {
		dq = false;
		sq = false;
		preC = ' ';
		nextC = ' ';

		node = new ArrayList<>();
	}

	private static void createTreeProcess(String t) {
		Log.i2(t);
//		[e.id,e.id2,e.id3,e.id4]!
//		node.add(new DefaultMutableTreeNode("#G2"));
//		node.add(new DefaultMutableTreeNode(","));	//C1
//		node.add(new DefaultMutableTreeNode("e.id4"));
//		node.add(new DefaultMutableTreeNode(","));	//C1
//		node.add(new DefaultMutableTreeNode("e.id3"));
//		node.add(new DefaultMutableTreeNode(","));	//C1
//		node.add(new DefaultMutableTreeNode("e.id2"));
//		node.add(new DefaultMutableTreeNode("e.id"));
//		node.get(0).add(node.get(1));
//		node.get(1).add(node.get(2));
//		node.get(1).add(node.get(3));
//		node.get(3).add(node.get(4));
//		node.get(3).add(node.get(5));
//		node.get(5).add(node.get(6));
//		node.get(5).add(node.get(7));
//
//		//[e.id,e.id2!{e.id3,e.id4}]!
//		node.add(new DefaultMutableTreeNode("#G2"));
//		node.add(new DefaultMutableTreeNode("#B"));
//		node.add(new DefaultMutableTreeNode("#C1"));
//		node.add(new DefaultMutableTreeNode("e.id4"));
//		node.add(new DefaultMutableTreeNode("e.id3"));
//		node.add(new DefaultMutableTreeNode("#C2"));
//		node.add(new DefaultMutableTreeNode("#C1"));
//		node.add(new DefaultMutableTreeNode("e.id2"));
//		node.add(new DefaultMutableTreeNode("e.id"));
//		node.get(5).add(node.get(1));	//{}
//		node.get(1).add(node.get(2));	//,
//		node.get(2).add(node.get(3));	//id4
//		node.get(2).add(node.get(4));	//id5
//		node.get(0).add(node.get(5));	//!
//		node.get(5).add(node.get(6));	//,
//		node.get(6).add(node.get(7));	//id2
//		node.get(6).add(node.get(8));	//id

		char c = ' ';
    	int f = 0;
    	dq = false;
    	sq = false;
    	String buf = "";
		for(int i=t.length()-1; i>=0; i--){
			c = t.charAt(i);
			//Log.i3(""+c);
			if(i>t.length())	nextC = t.charAt(i-1);

			check_dq_sq_re(c);
			if((c=='{' || c=='(') && !dq && !sq){
				f++;
			}
			else if((c=='}' || c==')') && !dq && !sq){
				f--;
			}

			if(c==']' && !dq && !sq && f==0){
				d++;
				String operator = "";
				if(preC==',')		operator = getOpName("[],");
				else if(preC=='!')	operator = getOpName("[]!");
				else if(preC=='%')	operator = getOpName("[]%");
				addNode(operator);

				buf = "";
				for(int j=i-1; j>=0; j--){
					//[e.id,e.id2,e.id3,e.id4]!
					//[e.id,e.id2,{e.id3,e.id4}]!

					c = t.charAt(j);
					if(j>t.length())	nextC = t.charAt(j-1);
					//Log.i3(""+c);
					check_dq_sq_re(c);
					if((c=='(') && !dq && !sq){
						f++;
					}
					else if((c==')') && !dq && !sq){
						f--;
					}

					if(!dq && !sq && f==0){
						if(c=='}'){
							//check {} or @{}
							if(!isDeco(t,j)){
								d++;
								addNode("#B");
								continue;
							}
						}
						else if(c=='{'){
							//check {} or @{}
							if(!isDeco(t,j)){
								addNode(reverseArray(buf));
								buf = "";
								d--;
								continue;
							}
						}
						else if(c==','){
							addNode("#C1");
							d++;
							addNode(reverseArray(buf));
							buf = "";
							d--;
							continue;
						}
						else if(c=='!'){
							addNode("#C2");
							d++;
							addNode(reverseArray(buf));
							buf = "";
							d--;
							continue;
						}
						else if(c=='%'){
							addNode("#C3");
							d++;
							addNode(reverseArray(buf));
							buf = "";
							d--;
							continue;
						}
						else if(c=='['){
							addNode(reverseArray(buf));
							buf = "";
							d--;
							break;
						}
					}
					buf += c;
					if(c!=' ' && c!='　')	preC = c;
				}
			}


			if(c!=' ' && c!='　')	preC = c;
		}

	}

	private static String getOpName(String s) {
//		,：#C1
//		!：#C2
//		%：#C3
//		[],：#G1
//		[]!：#G2
//		[]%：#G3
//		{}：#B
		if(s.equals(","))			return "#C1";
		else if(s.equals("!"))		return "#C2";
		else if(s.equals("%"))		return "#C3";
		else if(s.equals("[],"))	return "#G1";
		else if(s.equals("[]!"))	return "#G2";
		else if(s.equals("[]%"))	return "#G3";
		else if(s.equals("{}"))		return "#B";
		return "#No operator";
	}

	private static boolean isDeco(String t, int k) {
		//check {} or @{}
		//{e.id, e.name}
		//e,id@{color=red, bgcolor=blue}
		char c = ' ';
		int f = 0;
		if(t.charAt(k)=='{'){
			for(int j=k-1; j>=0; j--){
				c = t.charAt(j);
				if(c!=' ' && c!='　'){
					if(c=='@')	return true;
					else		return false;
				}
			}
		}
		else if(t.charAt(k)=='}'){
			for(int i=k-1; i>=0; i--){
				c = t.charAt(i);
				check_dq_sq_re(c);
				if((c=='(') && !dq && !sq){
					f++;
				}
				else if((c==')') && !dq && !sq){
					f--;
				}

				if(c=='{' && !dq && !sq && f==0){
					isDeco(t, i);
//					for(int j=k-1; j>=0; j--){
//						c = t.charAt(j);
//						if(c!=' ' && c!='　'){
//							if(c=='@')	return true;
//							else		return false;
//						}
//					}
				}
			}
		}
		return false;
	}

	static int preD = -1;
	private static void addNode(String s) {
		if(!s.trim().isEmpty()){
			String depth = "";
			if(nodeCount>0){
				depth = ""+d;
			}
			//Log.i3("addNode"+nodeCount+" = "+s+"		"+depth+" "+nodeCount);
			Log.i3("addNode"+nodeCount+" = "+s.trim());
			node.add(new DefaultMutableTreeNode(s));
			//parent = ;
			nodeCount++;
			if(nodeCount>0){
				preD = d;
			}
		}
	}

	private static String reverseArray(String s) {
		String r = "";
		for(int i=s.length()-1; i>=0; i--)
			r += s.charAt(i);
		return r;
	}

	//check_dq_sq
	private static void check_dq_sq(char c) {
		if(c=='"'){
			if(dq && preC!='\\')
				dq = false;
			else
				dq = true;
		}

		if(c=='\''){
			if(sq && preC!='\\')
				sq = false;
			else
				sq = true;
		}
		preC = c;
	}
	//check_dq_sq_re
	private static void check_dq_sq_re(char c) {
		if(c=='"'){
			if(nextC!='\\' && dq)
				dq = false;
			else
				dq = true;
		}

		if(c=='\''){
			if(nextC!='\\' && sq)
				sq = false;
			else
				sq = true;
		}
		//preC = c;
	}

}
