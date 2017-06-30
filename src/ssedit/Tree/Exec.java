package ssedit.Tree;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import ssedit.Common.Log;

public class Exec {

	public Exec() {

	}

//	public static void main(String[] args) {
	public static void process(String target) {
		String query = target;
		Log.debug = 1;

		//String tfe = "[e,id! {e.id2, e.id3}! {e.id4,s e.id5}! [e.id6],]!";	//Query(TFE)の指定
		//String tfe = "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}],";
		//String tfe = "{{e.id1@{ color=red }}@{ color=blue, width=100 }, [\"e.id2-1\"@{ color=orange }, \"e.id2-2\"]!@{ color=yellow }, {e.id3}}";

		//<<文字列 "">>
//		String tfe = "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}],";

		//<<装飾子 @{ }>>
//		String tfe = "{\n"
//				+ "	{\n"
//				+ "		[e.name@{class=name},e.salary@{class=salary},\n"
//				+ "		e.byear@{class=birth}]!\n"
//				+ "	}@{title=MEMBER,tablealign=center}\n"
//				+ "}@{cssfile=demo.css,charset=euc-jp}";
//		String tfe = "{{e.id1}, {e.id2-1, e.id2-2}@{width=200}, {e.id3}}";
//		String tfe = "{{e.id1@{color=red}}, {\"e.id2-1\", \"e.id2-2\"}, {e.id3}}";
//		String tfe = "{{e.id1@{ color=red }}@{ color=blue }, 	\n[\"e.id2-1\"@{ color=orange }, \"e.id2-2\"]!@{ color=yellow }, {e.id3}}";
//		String tfe = "[e.id1 % e.id2@{ color=red, width=100 }! {e.name, e.byear}],";
//		String tfe = "{{e.id1@{ color=red }}@{ color=blue, width=100 }, [\"e.id2-1\"@{ color=orange }, \"e.id2-2\"]!@{ color=yellow }, {e.id3}}";

		//<<複合反復子(Compound Grouper)>>
//		String tfe = "[e.name],3!";
//		String tfe = "[e.name, e.salary],3!5%";
//		String tfe = "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}],5% !"
//				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}]!6,4%";
//		String tfe = "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}],15% !"
//				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}]!16,24%";
//		String tfe = "[["
//				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}],15!100% !"
//				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}]!16,24%"
//				+ "]!]!10%";	//このクエリは、実際に実行できるかは不明（あくまでテスト用です）

		//<<Functions>>
		//■ image 関数
//		String tfe = "[e.name, image(e.picture, path=\"./picture/\")@{width=200}]!";
		//■ link 関数とFOREACH句
//		String tfe = "[e.name % e.salary, e.syear]!";
//		String tfe = "[link(e.name, file=\"Q3.ssql\", att=e.id)]!";
//		String tfe = "[e.salary, e.syear]!";
//		String tfe = "[link(e.name@{target=_new},file=\"Q3.ssql\",att=e.id)]!";
		//■ 集約関数(Aggregate Functions)	avg[e.salary]等
//		String tfe = "[ e.name ]!, avg[e.salary]";
//		String tfe = "[["
//				+ "[e.id1 % avg[e.id2]@{ color=red, width=100 }! {\"e.name, e.byear\"}],15!100% !"
//				+ "[e.id1 % e.id2@{ color=red, width=100 }! {\"e.name, e.byear\"}]!16,24%"
//				+ "]!]!10%";
		//■ null 関数
//		String tfe = "[null((desc1)e.salary)@{color=red, width=200}, e.name]!10%";
		//■ if then else 関数
//		String tfe = "[if (e.salary>10000) then (e.name,e.salary) else (e.salary)]!";
//		String tfe = "[e.name, (e.salary> 10000) ? e.name : e.salary]!";

		//<<ソーティング(昇順・降順)>>
//		String tfe = "[(asc1)e.name, (desc2)e.salary]!";
//		String tfe = "[ (asc1)d.name, [ (desc2)e.name ]! ]!";

		//<<コメント>>
//		String tfe = "{[e.name /*@{class=name}*/ , e.salary]!}/*@{cssfile=demo.css}*/";

		//<<テスト用>>
		//■ process()
//		String tfe = "[e.id, {e.id2! e.id3}! [e.id4, e.id5],]!";
//		String tfe = "[e.id0, e.id1, {e.id2! e.id3}! [e.id4, e.id5],]!";
		//■ getSimpleView()
//		String tfe = "[e.id@{color1=red}, {e.id2! e.id3@{color2=blue}}@{test=t1}! [e.id4, e.id5@{color3=yellow}],@{test=t2}]!@{cssfile1=demo.css}";
//		String tfe = "[\"from\", {e.id, e.name}]!";
//		String tfe = "[e.id@{ color = red, width = 100 }, e.name@{ bgcolor=yellow }]!";

		//<<論文用>>
//		String tfe = "[e.id@{color=red} % {(asc1)e.name! {e.syear@{width=100}, e.byear}}]!!"
//				+ "[e.id@{bgcolor=blue}, e.name],3!";

//		String tfe = "[e.id, e.name]!";



//		String query = "GENERATE HTML "+tfe+" FROM employee e;";

//		String query = "FOREACH e.id\nGENERATE HTML\n"
//		+ "\" From \", \" generate \", [  e.id @ { color = red } % {e.id! {e.name, e.byear}}]!![e.id @ {bgcolor=blue}, e.name]!\n"+
//		"From employee e;";
//	"";


//		ArrayList<DefaultMutableTreeNode> tree = QueryToTree.getTree(query);			//Query -> Tree
		ArrayList<DefaultMutableTreeNode> tree = QueryToTree.getSimpleViewTree(query);	//Query -> SimpleView Tree

		if(tree != null){
			new DisplayTree(tree);	//Treeを表示

			new TreeToQuery().createQuery((DefaultMutableTreeNode) tree.get(0).getLastLeaf());	//Tree -> Query
			//Log.i1(tfe = tfe.replace(" ", "").replace("　", "").replace("	", "").trim());
			//if(tfe.equals(TreeToQuery.q.replace(" ", "").replace("　", "").replace("	", "").trim()))
			//	Log.i1("結果： 一致 !");
			//else
			//	Log.e1("結果： 不一致");
		}else{
			Log.e1("<<Error>> TFE syntax error, etc.");
		}
	}

	public static String simpleViewProcess(String query){
//		ArrayList<DefaultMutableTreeNode> tree = QueryToTree.getTree(query);			//Query -> Tree

		ArrayList<DefaultMutableTreeNode> tree = QueryToTree.getSimpleViewTree(query);	//Query -> SimpleView Tree
//		new DisplayTree(tree);	//Treeを表示

		query = TreeToQuery.createQuery((DefaultMutableTreeNode) tree.get(0).getLastLeaf());	//Tree -> Query
		return query;
	}

	public static String indentProcess(String query){
		ArrayList<DefaultMutableTreeNode> tree = QueryToTree.getTree(query);			//Query -> Tree
		query = TreeToQuery.createQuery((DefaultMutableTreeNode) tree.get(0).getLastLeaf());	//Tree -> Query
		return query;
	}
}
