package ssedit.Tree;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;

import ssedit.Common.Log;

public class QueryToTree {
	private static boolean dq = false;
	private static boolean sq = false;
	private static boolean deco = false;
	private static boolean aggregateFunction = false;
	private static char preC = ' ';
	private static ArrayList<String> preS = new ArrayList<>();
	private static char nextC = ' ';

	private static ArrayList<DefaultMutableTreeNode> nodeBuf = new ArrayList<>();
	private static ArrayList<Integer> nodeCount = new ArrayList<>();
	private static int nodeCountPlace = -1;
	private static int iPlace = -1;

	private static boolean simpleView = false;

	public QueryToTree() {

	}

//	//main
//	public static void main(String[] args) {
//		Log.debug = 2;	//デバッグモードレベル(0〜3)の指定
//
////		String tfe = "[ e.id1 , e.id2 ! e.id3 ] ,";	////TODO -> OK
////		String tfe = "[e.id1,e.id2!e.id3,e.id4]!";
////		String tfe = "{e.id1, e.id2}";
////		String tfe = "{[e.id1! e.id2]! , [e.id1! e.id2]!}";	//入れ子 順番 -> OK -> OK!
////		String tfe = "[e.id1! e.id2]! , [e.id1! e.id2]!";	//入れ子 順番 -> OK -> OK!
////		String tfe = "[[e.id1, e.id2]! , e.id3]!";	//TODO -> OK
////		String tfe = "[e.id1]!";	//TODO -> OK
////		String tfe = "[[e.id1]!]!";	//TODO -> TODO -> OK
////		String tfe = "{[[e.id1]! , e.id2]!}";	//TODO -> TODO -> OK
////		String tfe = "{[[e.id1, e.id2]! , e.id3]!}";	//TODO -> TODO -> OK
////		String tfe = "[[[e.id1, e.id2]! , e.id3]!]!";	//TODO -> TODO -> OK
////		String tfe = "[{e.id1, e.id2}! e.id3]!";	//TODO -> OK
////		String tfe = "{[{e.id1, e.id2}! e.id3]!}";	//TODO -> TODO -> OK
////		String tfe = "[{e.id1, e.id2}! e.id3]! , [e.id1, e.id2! e.id3]!";	//TODO -> TODO -> OK
////		String tfe = "[[e.id1, e.id2]! ! e.id3]!";	//TODO -> TODO -> TODO -> OK
////		String tfe = "[{e.id1-1}! e.id1-2]! , [/*{e.id2-1}! */e.id2-2]!";	//TODO -> TODO -> OK
////		String tfe = "[{e.id1-1}! e.id1-2]! , [{e.id2-1}! e.id2-2]!";	//TODO -> TODO -> TODO -> OK
////		String tfe = "[{e.id1, e.id2}! e.id3]! , [{e.id1, se.id2}! e.id3]!";	//TODO -> TODO -> TODO -> OK
////		String tfe = "{[{e.id1, e.id2}! e.id3]!} , {[{e.id1, se.id2}! e.id3]!}";	//TODO -> TODO -> TODO -> OK
////		String tfe = "{{[{e.id1, e.id2}! e.id3]!} , {[{e.id1, e.id2}! e.id3]!}}";	//TODO -> TODO -> TODO -> OK
////		String tfe = "[{e.id1, e.id2}! e.id3]! , [{e.id1, e.id2}! e.id3]!";	//TODO -> TODO -> TODO -> OK
////		String tfe = "{[{e.id1, e.id2}! e.id3]! , [{e.id1, e.id2}! e.id3]!}";	//TODO -> TODO -> TODO -> OK
//
////		String tfe = "e.id1";
////		String tfe = "e.id1,e.id2";
////		String tfe = "e.id1, {e.id2}";		//入れ子 順番 -> OK
////		String tfe = "{e.id1}, e.id2";
////		String tfe = "{e.id1}, {e.id2}";	//入れ子 順番 -> OK
//
////		String tfe = "{[e.id1]!, e.id2}";	////TODO -> OK -> OK
////		String tfe = "{e.id1, [e.id2]!}";
////		String tfe = "[{e.id1}, e.id2]!";
////		String tfe = "{e.id1, {e.id2}}";
////		String tfe = "{e.id1,[e.id2]!}";
////		String tfe = "{e.id1,{e.id2}}";
////		String tfe = "{{e.id1},e.id2}";
////		String tfe = "[{e.id1, e.id2}! e.id3]!";
////		String tfe = "[e.id1, {e.id2,e.id4}]!";
////		String tfe = "[e.id1,{e.id2,e.id4}]!";
////		String tfe = "[e.id,e.id2!{e.id3,e.id4}]!";
//
////		String tfe = "{[e.id1]!, {e.id2} }";	//深さ -> OK
////		String tfe = "{[e.id1]!, [e.id2]!}";	//深さ -> OK
////		String tfe = "[[e.id1]!, [e.id2]!]!";	//深さ -> OK
////		String tfe = "{{e.id1}, {e.id2}}";		//深さ -> OK
////		String tfe = "[{e.id1, {e.id2}}! e.id3]!";	//順番 -> OK
////		String tfe = "{{e.id1}, {e.id2}, {e.id3}}";
//
////		String tfe = "[e.id1 % e.id2! {e.name, e.byear}],";
////		String tfe = "[e.id1%e.id2!{e.name,e.byear}],";
////		String tfe = "[e.id0!e.id1,e.id2!{e.id3-1,e.id3-2,e.id4}]!";
////		String tfe = "[e.id1%{e.id2!{e.name,e.byear}}],";
////		String tfe = "[e,id!{e.id2, e.id3}!{e.id4, e.id5}![e.id6],]!";
////		String tfe = "[e.id%{e.id2!{e.name,e.byear}}],";
////		String tfe = "[e,id! {e.id2, e.id3}! {e.id4, e.id5}! [e.id6],]!";
//
//		/**************************************************************************************/
//		//<<文字列 "">>
////		String tfe = "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}],";
//
//		//<<装飾子 @{ }>>
////		String tfe = "{\n"
////				+ "	{\n"
////				+ "		[e.name@{class=name},e.salary@{class=salary},\n"
////				+ "		e.byear@{class=birth}]!\n"
////				+ "	}@{title=MEMBER,tablealign=center}\n"
////				+ "}@{cssfile=demo.css,charset=euc-jp}";
////		String tfe = "{{e.id1}, {e.id2-1, e.id2-2}@{width=200}, {e.id3}}";
////		String tfe = "{{e.id1@{color=red}}, {\"e.id2-1\", \"e.id2-2\"}, {e.id3}}";
////		String tfe = "{{e.id1@{ color=red }}@{ color=blue }, 	\n[\"e.id2-1\"@{ color=orange }, \"e.id2-2\"]!@{ color=yellow }, {e.id3}}";
////		String tfe = "[e.id1 % e.id2@{ color=red, width=100 }! {e.name, e.byear}],";
////		String tfe = "{{e.id1@{ color=red }}@{ color=blue, width=100 }, [\"e.id2-1\"@{ color=orange }, \"e.id2-2\"]!@{ color=yellow }, {e.id3}}";
//
//		//<<複合反復子(Compound Grouper)>>
////		String tfe = "[e.name],3!";
////		String tfe = "[e.name, e.salary],3!5%";
////		String tfe = "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}],5% !"
////				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}]!6,4%";
////		String tfe = "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}],15% !"
////				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}]!16,24%";
////		String tfe = "[["
////				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}],15!100% !"
////				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}]!16,24%"
////				+ "]!]!10%";	//このクエリは、実際に実行できるかは不明（あくまでテスト用です）
//
//		//<<Functions>>
//		//■ image 関数
////		String tfe = "[e.name, image(e.picture, path=\"./picture/\")@{width=200}]!";
//		//■ link 関数とFOREACH句
////		String tfe = "[e.name % e.salary, e.syear]!";
////		String tfe = "[link(e.name, file=\"Q3.ssql\", att=e.id)]!";
////		String tfe = "[e.salary, e.syear]!";
////		String tfe = "[link(e.name@{target=_new},file=\"Q3.ssql\",att=e.id)]!";
//		//■ 集約関数(Aggregate Functions)	avg[e.salary]等
////		String tfe = "[ e.name ]!, avg[e.salary]";
////		String tfe = "[["
////				+ "[e.id1 % avg[e.id2]@{ color=red, width=100 }! {\"e.name, e.byear\"}],15!100% !"
////				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}]!16,24%"
////				+ "]!]!10%";
//		//■ null 関数
////		String tfe = "[null((desc1)e.salary)@{color=red, width=200}, e.name]!10%";
//		//■ if then else 関数
////		String tfe = "[if (e.salary>10000) then (e.name,e.salary) else (e.salary)]!";
////		String tfe = "[e.name, (e.salary> 10000) ? e.name : e.salary]!";
//
//		//<<ソーティング(昇順・降順)>>
////		String tfe = "[(asc1)e.name, (desc2)e.salary]!";
////		String tfe = "[ (asc1)d.name, [ (desc2)e.name ]! ]!";
//
//		//<<コメント>>
////		String tfe = "{[e.name /*@{class=name}*/ , e.salary]!}/*@{cssfile=demo.css}*/";
//
//		//<<Syntax error queries>>
////		String tfe = "{[e.name /*@{class=name}*/ , e.salary]!}/*@{cssfile=demo.css}";
////		String tfe = "e.name , e.salary]!,[e.name , e.salary]!}";
//
//		//<<テスト用>>
//		//■ process()
////		String tfe = "[e.id, {e.id2! e.id3}! [e.id4, e.id5],]!";
////		String tfe = "[e.id0, e.id1, {e.id2! e.id3}! [e.id4, e.id5],]!";
//		//■ getSimpleView()
//		String tfe = "[e.id@{color1=red}, {e.id2! e.id3@{color2=blue}}@{test=t1}! [e.id4, e.id5@{color3=yellow}],@{test=t2}]!@{cssfile1=demo.css}";
//		//■ getTFE()
////		String query = ""
//		String query = "FOREACH e.id\nGENERATE HTML\n"
//				+ "\" From \", \" generate \", [  e.id @ { color = red } % {e.id! {e.name, e.byear}}]!![e.id @ {bgcolor=blue}, e.name]!\n"+
//				"From employee e;";
////			"";
//
//		String query = "GENERATE HTML "+tfe+" FROM employee e;";
////		String query = tfe;
////		getTree(query);
//		getSimpleView(query);
//	}


	//getTree
	//return: TFE tree
	public static ArrayList<DefaultMutableTreeNode> getTree(String query) {
		return process(query);
	}

	//getSimpleView
	//return: TFE tree with no deco(@{})
	public static ArrayList<DefaultMutableTreeNode> getSimpleViewTree(String query) {
		simpleView_preProcess();
		ArrayList<DefaultMutableTreeNode> tree = process(query);
		simpleView_postProcess();
		return tree;
	}

	//process
	private static ArrayList<DefaultMutableTreeNode> process(String query) {
		query = deleteAllCommentedOutCodes(query);
		String tfe = getTFE(query);
		Log.i1("TFE = "+tfe+"\n");
		if (tfe.trim().isEmpty()) return null;

		Log.i3(tfe);
		preProcess();
		ArrayList<DefaultMutableTreeNode> tree = getTree(tfe, tfe.length()-1);
		Log.i3("");

		/********************************************************************************************/
		if(tree != null && Log.debug>1)	new DisplayTree(tree);
		Log.i2(tfe);
		//new TreeToQuery().createQuery((DefaultMutableTreeNode) tree.get(0).getLastLeaf());
		/********************************************************************************************/
		return tree;
	}

	//delete all commented out codes
	private static String deleteAllCommentedOutCodes(String q) {
		String[] lines = q.split("\n");
		String line = "";
		String tmp = "";

		try{
			for (int j = 0; j < lines.length; j++) {
				line = lines[j];

				while (line.contains("/*")) {
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
					//tmp += line + "\n";
					tmp += line + " ";
			}
			return tmp;
		}catch(Exception e){
			Log.e1(""+e);
			return "";
		}
	}

	//getTFE
	private static String getTFE(String query){
		try{
			StringTokenizer st = new StringTokenizer(query);
			boolean tfe = false, from = false;
			String g = "", t = ""/*, f = ""*/;
			String s = "";

			char c = ' ';
	    	int m = 0;
	    	dq = false;
	    	sq = false;
	    	int i = 0;

			while(st.hasMoreTokens()){
				s = st.nextToken();
				preC = ' ';
				for(i=0; i<s.length(); i++){
					c = s.charAt(i);
					check_dq_sq(c);

					if((c=='{' || c=='[' || c=='(') && !dq && !sq){
						m++;
					}
					else if((c=='}' || c==']' || c==')') && !dq && !sq){
						m--;
					}

					if(!dq && !sq && m == 0){
						if(s.trim().toLowerCase().equals("generate")){
							g += s+" "+st.nextToken()+" ";
							s = "";
							tfe = true;
						}else if(s.trim().toLowerCase().equals("from")){
							tfe = false;
							from = true;
							break;
						}
					}
				}
				if(!tfe && !from) {
					g += s+" ";
				}else if(tfe) {
					t += s+" ";
				}else if(from){
					/*f += s+" ";*/
					break;
				}
			}
			return (!t.trim().isEmpty())? t.trim() : g.trim();
			//return g+"\n"+t+"\n"+f;
		}catch(Exception e){
			Log.e(""+e);
		}
		return "";
	}

	//preProcess
	private static void preProcess() {
		dq = false;
		sq = false;
		deco = false;
		aggregateFunction = false;
		preC = ' ';
		preS = new ArrayList<>();
		nextC = ' ';

		nodeBuf = new ArrayList<>();
		nodeCount = new ArrayList<>();
		nodeCountPlace = -1;
		iPlace = -1;
	}

	//getTree
	private static ArrayList<DefaultMutableTreeNode> getTree(String t, int p) {
		ArrayList<DefaultMutableTreeNode> n = new ArrayList<>();
		char c = ' ';
    	int f = 0;
    	dq = false;
    	sq = false;
    	deco = false;
    	aggregateFunction = false;
    	preC = ' ';
    	String buf = "";

    	try{
	    	nodeCount.add(++nodeCountPlace, 0);
			preS.add(nodeCountPlace, "");
	    	Log.i3("nodeCountPlace = "+ nodeCountPlace);

			for(int i=p; i>=0; i--){
				c = t.charAt(i);
				Log.i3(""+c);
				nextC = (i>0)? t.charAt(i-1) : ' ';

				check_dq_sq_re(c);
				if(c=='(' && !dq && !sq){
					f++;
				}
				else if(c==')' && !dq && !sq){
					f--;
				}

				if (!dq && !sq && f == 0) {
					if((c==']' && !(aggregateFunction = isAggregateFunction(t, i))) || (c == '}' && !(deco = isDeco(t, i)))){
						boolean g = (c==']')? true : false;
						String label = (g)? "Grouper" : "Connector";
						String operator = (g)? getGrouperName(t, i) : getOperatorName("{}");
						String deco = !isSimpleView()? getDeco(buf) : "";

						Log.e3("/////// "+label+" n = "+ (!n.isEmpty()? ""+n.get(nodeCount.get(nodeCountPlace)-1) : "null") +" ///////");
						if(n.isEmpty() && isSingleNode(t, i)){
							//左辺 or SingleNodeの場合
							Log.e3("/////// "+label+"左辺 or SingleNode n = "+ (!n.isEmpty()? ""+n.get(nodeCount.get(nodeCountPlace)-1) : "null") +" ///////");
							addNode(n, operator+deco);
							addNode(n, getTree(t, i-1));
						}else{
							//右辺の場合
							Log.e3("/////// "+label+"右辺 ///////");
							//(注) 下記の順番は重要(変更不可)
							DefaultMutableTreeNode x = getTree(t, i-1).get(0);
							nodeBuf.clear();
							nodeBuf.add(new DefaultMutableTreeNode(operator+deco));
							nodeBuf.get(0).add(x);
							//↓ java.lang.IllegalArgumentException: new child is an ancestor
							//n2.get(0).add(getTree(t, i-1).get(0));
						}

						buf = "";
						nodeCountPlace--;
						Log.i3("nodeCountPlace-- = "+ nodeCountPlace);
						Log.e3("i = "+i+"	|	iPlace = "+iPlace);
						if(i<=iPlace)	return null;
						i = iPlace;	//iを[ or {まで進める
					}
					else if ((c == '[' && !isAggregateFunction(t, i)) || (c == '{' && !isDeco(t, i))) {
						addNode(n, checkDeco(reverseArray(buf)));
						buf = "";
						iPlace = i;
						return n;
					}
					else if (!deco && !aggregateFunction && (c == ',' || c == '!' || c == '%')
							&& !isGrouper(t, i) && !isCompoundGrouper(t, i)) {
						addNode(n, getOperatorName(""+c));
						addNode(n, checkDeco(reverseArray(buf)));
						buf = "";
					}
				}
				if(!isOperator(c) || deco || aggregateFunction || dq || sq || f!=0)	buf += c;
				if(!Character.isWhitespace(c))	preC = c;
			}
			if(!nodeBuf.isEmpty())			//[e.id1]!! [e.id2]!
				addNode(n, "");
			if(n.isEmpty())					//指定が1つだけのとき(TFE内にconnectorもgrouperも無い場合)
				n.add(new DefaultMutableTreeNode( checkDeco(reverseArray(buf))) );
			else if (!buf.trim().isEmpty()){ //e.id1,e.id2
				addNode(n, checkDeco(reverseArray(buf)));
			}
			//Log.e3("last buf = "+buf);
			return n;
		}catch(Exception e){
			Log.e1(""+e);
			return null;
		}
	}



	//addNode
	private static ArrayList<DefaultMutableTreeNode> addNode(
			ArrayList<DefaultMutableTreeNode> n,
			String s) {
		try{
			if (!s.trim().equals("")) {
				Log.e3(s+"	"+n);
				n.add(new DefaultMutableTreeNode(s.trim()));
				int x = nodeCount.get(nodeCountPlace);
				if (x>0) {
					if(!preS.get(nodeCountPlace).startsWith("#")){
						Log.i3("n.get("+(x-2)+").add(n.get("+x+"));\n");
						n.get(x-2).add(n.get(x));
					} else {
						n.get(x-1).add(n.get(x));
					}
				}
				nodeCount.add(nodeCountPlace, x+1);
				preS.add(nodeCountPlace, s);
			}else{
				Log.e3("***** else *****");
				Log.e3("***** preC0 = "+preC+" *****");
				if(!nodeBuf.isEmpty()
						&& (preC == '}' || preC == ']')){
					//右辺の場合のadd処理
					Log.e3("***** 右辺   preC1 = "+preC+" *****");
					int x = nodeCount.get(nodeCountPlace);
					if(n.get(x-1).toString().trim().startsWith("#"))
						n.get(x-1).add(nodeBuf.get(0));
					else
						n.get(x-2).add(nodeBuf.get(0));
					nodeBuf.clear();
					Log.e3("***** "+n+" *****");
					Log.e3("***** "+nodeBuf+" *****");
					//nodeCount.add(nodeCountPlace, x+1);	//不要(あるとerror)
				}
			}
			return n;
		}catch(Exception e){
			Log.e1(""+e);
			return null;
		}
	}

	//add node2 to node1
	private static ArrayList<DefaultMutableTreeNode> addNode(
			ArrayList<DefaultMutableTreeNode> n1,
			ArrayList<DefaultMutableTreeNode> n2) {
		try{
			Log.i3(""+n1);
			Log.i3(""+n2);

			Log.i3(n1.get(nodeCount.get(nodeCountPlace-1)-1)+" .add "+n2.get(0));
			n1.get(nodeCount.get(nodeCountPlace-1)-1).add(n2.get(0));
			nodeCount.add(nodeCountPlace, nodeCount.get(nodeCountPlace)+1);
			return n1;
		}catch(Exception e){
			Log.e1(""+e);
			return null;
		}
	}


	//isOperator
	private static boolean isOperator(char c) {
		if(c == ',' || c == '!' || c == '%' || c == '}' || c == ']')
			return true;
		return false;
	}

	//getOperatorName
	private static String getOperatorName(String s) {
//		if(s.equals(","))			return "#C1";
//		else if(s.equals("!"))		return "#C2";
//		else if(s.equals("%"))		return "#C3";
//		else if(s.equals("[],"))	return "#G1";
//		else if(s.equals("[]!"))	return "#G2";
//		else if(s.equals("[]%"))	return "#G3";
//		else if(s.equals("{}"))		return "#B";
		if(s.equals(","))			return "#,";
		else if(s.equals("!"))		return "#!";
		else if(s.equals("%"))		return "#%";
		else if(s.equals("[],"))	return "#[],";
		else if(s.equals("[]!"))	return "#[]!";
		else if(s.equals("[]%"))	return "#[]%";
		else if(s.equals("{}"))		return "#{}";
		return "#No operator";
	}

	//isGrouper
	private static boolean isGrouper(String t, int k) {
		//check Connector(, / ! / %)  or  Grouper([], / []! / []%)
		char c = ' ';
		for(int j=k-1; j>=0; j--){
			c = t.charAt(j);
			if(!Character.isWhitespace(c)){
				if(c==']')	return true;
				else		return false;
			}
		}
		return false;
	}

	//isCompoundGrouper
	private static boolean isCompoundGrouper(String t, int p) {
		//複合反復子かどうかチェック
//		[ TFE ],number!
//		[ TFE ]!number,
//		[ TFE ],number%
//		[ TFE ]!number%
//		[ TFE ],number1!number2%
//		[ TFE ]!number1,number2%
		char c = ' ';
		boolean num = false;
		for(int i=p-1; i>=0; i--){
			c = t.charAt(i);
			if(!Character.isWhitespace(c)){
				if(!num && (num = isNumber(""+c)))	continue;
				if(num && isNumber(""+c))			continue;
				if(num && !isNumber(""+c) && isOperator(c))	return true;
				else return false;
			}
		}
		return false;
	}

	//getGrouperName
	private static String getGrouperName(String t, int p) {
		//[]!
		//[],number1!number2%
		char c = ' ';
		char g = ' ';
		boolean num = false;
		boolean op = false;
		String s = "";
		for(int i=p+1; i<t.length(); i++){
			c = t.charAt(i);

			if(Character.isWhitespace(c))
				continue;

			if(Character.isWhitespace(g)){
				g = c;
				op = true;
			}else
				if(op && !num && (num = isNumber(""+c))){
					s += c;
					op = false;
					continue;
				}else if(num && isNumber(""+c)){//数値が2桁以上
					s += c;
					continue;
				}else if(num && !op && (op = isOperator(c))){
					s += c;
					num = false;
					continue;
				}else
					break;
		}
		return "#[]"+g+s;
	}

	//isDeco
	private static boolean isDeco(String t, int k) {
		//check {} or @{}
		//{e.id, e.name}
		//e,id@{color=red, bgcolor=blue}
		char c = ' ';
		int f = 0;
		if(t.charAt(k)=='{'){
			for(int j=k-1; j>=0; j--){
				c = t.charAt(j);
				if(!Character.isWhitespace(c)){
					if(c=='@'){
						deco = false;
						return true;
					}
					else
						return false;
				}
			}
		}
		else if(t.charAt(k)=='}'){
			for(int i=k; i>=0; i--){
				c = t.charAt(i);
				nextC = (i>0)? t.charAt(i-1) : ' ';

				check_dq_sq_re(c);
				if((c=='{' || c=='[' || c=='(') && !dq && !sq){
					f++;
				}
				else if((c=='}' || c==']' || c==')') && !dq && !sq){
					f--;
				}

				if(f==0 && !dq && !sq && c=='{'){
					return isDeco(t, i);
				}
			}
		}
		return false;
	}

	//getDeco
	private static String getDeco(String buf) {
		String d = reverseArray(buf).trim();
		if(d.contains("@"))
			d = d.substring(d.indexOf("@"));
		else
			d = "";
		return d;
	}

	//checkDeco
	private static String checkDeco(String s) {
		return !isSimpleView()? s : removeDeco(s);
	}

	//removeDeco
	private static String removeDeco(String s) {
		if(!s.trim().isEmpty() && isDeco(s.trim(), s.trim().length()-1)){
			s = s.substring(0, s.lastIndexOf("@"));
		}
		if(s.contains("@")){
			s = removeDeco2(s);
		}
		return s;
	}

	// removeDeco in function
	private static String removeDeco2(String s){
		int flag = 0;
		int startPos = 0;
		int endPos = 0;
		for(int i=0; i < s.length(); i++){
			check_dq_sq(s.charAt(i));
			if(s.charAt(i) == '@' && !dq && !sq){
				flag++;
				startPos = i;
				for(int j = i + 2; j < s.length(); j++){
					if(s.charAt(j)=='{'){
						flag++;
					}
					if(s.charAt(j)=='}'){
						flag--;
					}
					if(flag==0){
						endPos = j;
						s = s.substring(0, startPos) + s.substring(endPos + 1);
						return s;
					}
				}
			}
		}
		return s;

	}

	//isAggregateFunction
	private static boolean isAggregateFunction(String t, int k) {
		//check [] or avg[]
		char c = ' ';
		int f = 0;
		if(t.charAt(k)=='['){
			for(int j=k-1; j>=0; j--){
				c = t.charAt(j);

				if(!Character.isWhitespace(c)){
					Log.i3("isAggregateFunction() nextC = "+c);
					if(!isOperator(c) && c!='[' && c!='{'){
						aggregateFunction = false;
						return true;
					}
					else
						return false;
				}
			}
		}
		else if(t.charAt(k)==']'){
			for(int i=k; i>=0; i--){
				c = t.charAt(i);
				nextC = (i>0)? t.charAt(i-1) : ' ';

				check_dq_sq_re(c);
				if((c=='{' || c=='[' || c=='(') && !dq && !sq){
					f++;
				}
				else if((c=='}' || c==']' || c==')') && !dq && !sq){
					f--;
				}

				if(f==0 && !dq && !sq && c=='['){
					return isAggregateFunction(t, i);
				}
			}
		}
		return false;
	}

	//isSingleNode
	private static boolean isSingleNode(String t, int p) {
		//check {e.id1} or {e.id1},{e.id2}
		char first = t.charAt(p);
		char c = ' ';
    	int f = 0;
    	dq = false;
    	sq = false;
    	boolean close = false;

		for(int i=p; i>=0; i--){
			c = t.charAt(i);
			nextC = (i>0)? t.charAt(i-1) : ' ';

			check_dq_sq_re(c);
			if((c=='{' || c=='[' || c=='(') && !dq && !sq){
				f++;
			}
			else if((c=='}' || c==']' || c==')') && !dq && !sq){
				f--;
			}

			if(!close && !dq && !sq && f == 0) {
				if((first==']' && c=='[') || (first=='}' && c=='{')){
					close = true;
					continue;
				}
			}
			if(close && !Character.isWhitespace(c)){
				Log.e3("\nclose c = "+c);
				if(isOperator(c))	return false;
				break;
			}
		}
		return true;
	}

	//check_dq_sq
	private static void check_dq_sq(char c) {
		if(c=='"'){
			if(dq && preC!='\\')
				dq = false;
			else
				dq = true;
		}
		else if(c=='\''){
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
		else if(c=='\''){
			if(nextC!='\\' && sq)
				sq = false;
			else
				sq = true;
		}
	}

	//reverseArray
	private static String reverseArray(String s) {
		String r = "";
		for(int i=s.length()-1; i>=0; i--)
			r += s.charAt(i);
		return r;
	}


	//simpleView_preProcess
	private static void simpleView_preProcess() {
		simpleView = true;
	}

	//simpleView_postProcess
	private static void simpleView_postProcess() {
		simpleView = false;
	}

	//isSimpleView
	private static boolean isSimpleView() {
		return simpleView;
	}


	//isNumber
	private static boolean isNumber(String s) {
		Pattern p = Pattern.compile("^[0-9]*$");
		Matcher m = p.matcher(s);
		return m.find();
	}
}
