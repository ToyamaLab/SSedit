package ssedit.LinkForeach;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import ssedit.FrontEnd;
import ssedit.Common.Functions;
import ssedit.Common.GlobalEnv;

public class LinkForEach {
	protected static SimpleAttributeSet linkAttr = new SimpleAttributeSet();

	private static boolean dq = false;
	private static boolean sq = false;

	private static int linkstart = 0;
	private static int foreachend = 0;
	private static String linkstr = "";
	private static String foreachstr = "";
	private static String querystr = "";
	private static String queryfilename = "";
	// private static String atts = "";
	private static int foreachfilecount;
	private static int sabun = 0;
	private static ArrayList<String> linkforeach_filename; // 0番目がlinkの内容、1番目以降がforeachの内容
	private static ArrayList<String> linkforeach;

	private static String foreach_generate = "";
	private static String foreach_from = "";
	private static char connector = ' ';

	// public static void process(String s, String filename, String a){
	public static void process(String s, String filename) {
		GlobalEnv.link_array.clear();
		GlobalEnv.foreach_array.clear();
		GlobalEnv.linkattpos_array.clear();
		GlobalEnv.linkattpos_array2.clear();
		GlobalEnv.foreachpos_array.clear();
		GlobalEnv.attInput.clear();
		GlobalEnv.fullLink = "";
		linkforeach_filename = new ArrayList<String>();
		linkforeach = new ArrayList<String>();
		foreachfilecount = 0;
		querystr = s;
		queryfilename = filename;
		// atts = a;
		sabun = 0;
		linkforeach.add(0, s);
		getMedia_and_From();
		checkp(s);
		if(GlobalEnv.foreach_array.isEmpty()){
			FrontEnd.stateTimer.start();
			FrontEnd.filestateLabel.setForeground(Color.RED);
			FrontEnd.filestateLabel.setText("深度結合子(%)が存在しません");
		} else {
			new GetAtt(GlobalEnv.fullLink, GlobalEnv.foreach_array.get(0), linkforeach_filename);
		}
//			return;
		// createFile();
		// System.out.println(GlobalEnv.link_array);
		// System.out.println(GlobalEnv.foreach_array);
	}

	private static void getMedia_and_From() {
		// generate MEDIA までの文字列
		foreach_generate = querystr.substring(0, querystr.toLowerCase()
				.indexOf("html") + 4);
		// TODO mobil_html5

		// foreach_generate =
		// querystr.substring(0,querystr.toLowerCase().indexOf("generate"));
		// String buf = "";
		// int x = 2;
		// for(int i=foreach_generate.length(); i<querystr.length(); i++){
		// char s = querystr.charAt(i);
		// buf += s;
		// if(s==' ' || s=='\n') x--;
		// if(x==0) foreach_generate += buf;
		// }

		// from -
		foreach_from = querystr.substring(querystr.toLowerCase().lastIndexOf(
				"from"));
	}

	private static void checkp(String target) {
		// 取得したテキスト内に%があるかどうか判定
		String regex = "%";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(target);

		if (match.find()) {
			// System.out.println("マッチ");

			char s = ' ';
			char preP = ' ';

			for (int i = 0; i < target.length(); i++) {
				s = target.charAt(i);
				// System.out.println(i+" : "+s);
				check_dq_sq(s);

				if (s == '%' && !checkStringNumber("" + preP) && !dq && !sq) {
					int f = 0;
					dq = false;
					sq = false;

					System.out.println("\n" + i + "番目の文字=" + s);
					for (int j = i; j >= 0; j--) {
						char s2 = target.charAt(j);

						check_dq_sq_link(s2, (j > 0) ? target.charAt(j - 1)
								: ' ');

						if ((s2 == '{' || s2 == '(') && !dq && !sq) {
							f++;
						} else if ((s2 == '}' || s2 == ')') && !dq && !sq) {
							f--;
						}

						if ((s2 == ',' || s2 == '!' || s2 == '[') && !dq && !sq
								&& f == 0) {
							String link = target.substring(j + 1, i).trim();
							System.out.println(linkstr = link);
							System.out.println("linkの始まる文字位置＝"
									+ (linkstart = (j + 1)));
							break;

						}
					}

					int l = i + 1;

					for (int k = i + 1; k < target.length(); k++) {
						char s3 = target.charAt(k);
						if (s3 != ' ' && s3 != '　') {
							if (s3 == '{') {
								f++;

								for (l = k + 1; l < target.length(); l++) {

									// {e.salary@{color=red}}
									char s4 = target.charAt(l);
									// System.out.println(s4);

									check_dq_sq(s4);

									if ((s4 == '{' || s4 == '(') && !dq && !sq) {
										f++;
									} else if ((s4 == '}' || s4 == ')') && !dq
											&& !sq) {
										f--;
									}
									if (!dq && !sq && f == 0) {
										String foreach = target.substring(
												i + 1, l + 1).trim();
										System.out
												.println(foreachstr = foreach);
										break;
									}

									// // if(s3==',' || s3=='!' || s3=='['){
									// if(s3==']'){
									// String foreach = target.substring(i+1,
									// k);
									// System.out.println(foreach);
									// }
								}
							} else {
								// //{なしの場合
								// % e.salary@{color=red}
								// if(s3==',' || s3=='!' || s3=='['){
								// // if(s3==']'){
								// // String foreach = target.substring(i+1, k);
								// // System.out.println(foreach);
								// }
								for (l = k; l < target.length(); l++) {

									char s4 = target.charAt(l);
									// System.out.println(s4);

									check_dq_sq(s4);

									if ((s4 == '{' || s4 == '(') && !dq && !sq) {
										f++;
									} else if ((s4 == '}' || s4 == ')') && !dq
											&& !sq) {
										f--;
									}

									if ((s4 == ',' || s4 == '!' || s4 == ']')
											&& !dq && !sq && f == 0) {
										String foreach = target.substring(
												i + 1, l).trim();
										System.out
												.println(foreachstr = foreach);
										break;
									}
								}
							}
							break;
						}
					}
					// System.out.println("foreachの終わる文字位置＝"+(foreachend=(l-1)));
					System.out.println("foreachの終わる文字位置＝" + (foreachend = l));

					// get connector
					for (int m = l; m < target.length(); m++) {
						char s5 = target.charAt(m);
						if (s5 == ']') {
							while ((target.charAt(m + 1) == ' ' || target
									.charAt(m + 1) == '\n')
									&& m < target.length())
								m++;
							connector = target.charAt(m + 1);
							System.out.println("]の後ろの結合子=" + connector);
							break;
						}
					}

					divide();
					i = l;
				}
				preP = s;
				
			}

		} else {
			System.out.println("深度結合子(%)が存在しません");
		}
//		return linkstart;
	}

	private static boolean checkStringNumber(String number) {
		Pattern p = Pattern.compile("^[0-9]*$");
		Matcher m = p.matcher(number);

		return m.find();
	}

	static char preS = ' ';

	private static void check_dq_sq_link(char s, char link_preS) {
		preS = link_preS;
		check_dq_sq(s);
	}

	private static void check_dq_sq(char s) {
		if (s == '"') {
			if (dq && preS != '\\')
				dq = false;
			else
				dq = true;
		}

		if (s == '\'') {
			if (sq && preS != '\\')
				sq = false;
			else
				sq = true;
		}
		preS = s;
	}

	private static void divide() {
		// System.out.println(queryfilename);
		// System.out.println(querystr);

		// String partlinkstr = linkforeach.get(0).substring(linkstart,
		// foreachend);
		// highlightLink(linkstart, foreachend);
		// new GetAtt();

		String beforelinkstr = linkforeach.get(0).substring(0,
				linkstart + sabun);
		String afterforeachstr = linkforeach.get(0).substring(
				foreachend + sabun);

		// String beforelinkstr = querystr.substring(0, linkstart + sabun);
		// String afterforeachstr = querystr.substring(foreachend + sabun);

		String fn = getForeachFilename(queryfilename);

		// TODO ここでポップアップを表示、attを入力させる

		String link = beforelinkstr + "link(" + linkstr + ", file=\""
				+ new File(fn).getName() + "\", " + ")" + afterforeachstr;
		// String link = beforelinkstr
		// + "link(" + linkstr + ", file=\"" + new File(fn).getName() +
		// "\", "+getAtts(atts)+")"
		// + afterforeachstr;

		int linkattPos = (beforelinkstr + "link(" + linkstr + ", file=\""
				+ new File(fn).getName() + "\", ").length();

		String foreach = "FOREACH " + "\n" + foreach_generate + " \n" + "["
				+ foreachstr + "]" + connector + " " + "\n" + foreach_from;
		// String foreach = "FOREACH " + atts + "\n"
		// + foreach_generate + " \n"
		// + "["+foreachstr+"]"+connector
		// + " " + "\n" + foreach_from;

		int foreachPos = "FOREACH ".length();

		GlobalEnv.link_array.add(link);
		GlobalEnv.linkattpos_array.add(linkattPos);
		GlobalEnv.linkattpos_array2.add(linkattPos);
		GlobalEnv.foreach_array.add(foreach);
		GlobalEnv.foreachpos_array.add(foreachPos);
		GlobalEnv.fullLink = link;

		sabun = link.length() - querystr.length();

		try {
			linkforeach_filename.remove(0);
			linkforeach.remove(0);
		} catch (Exception e) {
		}
		linkforeach_filename.add(0, getlinkFilename(queryfilename));
		linkforeach_filename.add(foreachfilecount, fn);
		linkforeach.add(0, link);
		linkforeach.add(foreachfilecount, foreach);
	}

	private static String getlinkFilename(String fn) {
		if (fn.contains(".")) {
			return fn.substring(0, fn.lastIndexOf(".")) + "_link"
					+ fn.substring(fn.lastIndexOf("."));
		}
		return "";
	}

	private static String getForeachFilename(String fn) {
		if (fn.contains(".")) {
			return fn.substring(0, fn.lastIndexOf(".")) + "_foreach"
					+ (++foreachfilecount) + fn.substring(fn.lastIndexOf("."));
		}
		return "";
	}

	public static void createFile(String linkQuery,
			List<String> foreachquery_array) {
		System.out.println("\n\n");
		// System.err.println(linkforeach);
		for (int i = 0; i < linkforeach.size() - 1; i++) {
			if (i == 0) {
				// System.out.println("<Link>");
				// System.err.println(linkQuery);
				Functions.createFile(GlobalEnv.folderPath + GlobalEnv.OS_FS
						+ linkforeach_filename.get(i), linkQuery);
			} else if (i > 0) {
				// System.out.println("<Foreach>");
				// System.err.println(foreachquery_array.get(i-1));

				Functions.createFile(GlobalEnv.folderPath + GlobalEnv.OS_FS
						+ linkforeach_filename.get(i),
						foreachquery_array.get(i - 1));

			}
			// System.out.println("■ " + linkforeach_filename.get(i)
			// + "\n" + linkforeach.get(i));
			// if(i==0) System.out.println();
		}

		// TODO ファイル作成

	}

}
