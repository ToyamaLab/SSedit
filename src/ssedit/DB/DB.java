package ssedit.DB;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import ssedit.FrontEnd;
import ssedit.Caret.CaretState;
import ssedit.Common.Functions;
import ssedit.Common.GlobalEnv;

public class DB {
	public static void db() {
		GlobalEnv.tabledata.clear();
		File file = new File(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile);
		// 使用しているドライバー
		String driver = "";
		// データベースのパス
		String db = "";
		// データベースのホスト名
		String host = "";
		// データベースのユーザー（posgresql）
		String user = "";

//		String outdir = "";

		if(file.exists()){
		driver = Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile, "driver");
		db = Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile, "db");
		host = Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile, "host");
		user = Functions.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile, "user");
//		outdir = Common.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile, "outdir");
//		if(Common.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile, "outdir").isEmpty() && !GlobalEnv.folderPath.isEmpty()){
//			GlobalEnv.outdirPath = GlobalEnv.folderPath;
//			GlobalEnv.config_outdirField.setText(GlobalEnv.outdirPath);
//		}

			try {
				// tree
				Connection con = null;
				if (driver.isEmpty()) {
					driver = "postgresql";
					FrontEnd.driverCombo.setSelectedIndex(1);
				}
				if (driver.equals("postgresql")) {
//					System.out.println("postgre");
					if(db.equals("") || host.equals("") || user.equals("")){
						return;
					// sqliteだったら（とりあえず…）
					} else {
						Class.forName("org." + driver + ".Driver");
						con = DriverManager.getConnection("jdbc:" + driver + "://" + host + "/" + db, user, "");
					}
				} else {
					// 相対パスだったら
//					System.out.println(db);
						Class.forName("org." + driver + ".JDBC");
					if (db.startsWith("./") || !db.startsWith("/")) {
						if (db.startsWith("./")) {
							db = db.substring(db.indexOf("/"));
						}
						db = GlobalEnv.folderPath + GlobalEnv.OS_FS + db;
					}
//					System.out.println(Common.has(GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile, "db"));
						con = DriverManager.getConnection("jdbc:" + driver
								+ ":" + db);
				}
				// ステートメントオブジェクトを生成
				Statement stmt = con.createStatement();
				//stmt.setQueryTimeout(30); // set timeout to 30 sec.
				DatabaseMetaData dmd = con.getMetaData();
				ResultSet result_table = null;
				String types[] = { "TABLE" , "VIEW"};
				result_table = dmd.getTables(null, null, "%", types);// テーブルを取得


				Hashtable<String, Object> treedata = new Hashtable<String, Object>();// テーブルの一覧を載せる

				try {
					GlobalEnv.table_array.clear();
					while (result_table.next()) {// 次のテーブルが存在しなくなるまで
						Hashtable<String, String> attributedata = new Hashtable<String, String>();// スキーマの情報を載せる、テーブルが次に進むたびに初期化
						// テーブル
						FrontEnd.str_attribute = result_table.getString("TABLE_NAME");
						if (!FrontEnd.str_attribute.startsWith("pg_")
								&& !FrontEnd.str_attribute.startsWith("sql_") && !FrontEnd.str_attribute.startsWith("SQLITE")) {
							GlobalEnv.table_array.add(FrontEnd.str_attribute);
							ResultSet result_attribute = dmd.getColumns(null,
									null, FrontEnd.str_attribute, "%");// テーブル上のスキーマを取得
							while (result_attribute.next()) {// スキーマがなくなるまで
								String name = result_attribute
										.getString("COLUMN_NAME");// 属性名を取得
								String type = result_attribute
										.getString("TYPE_NAME");// その属性のデータ型を取得
								attributedata.put(name + " (" + type + ")",
										"attribute");// 葉にスキーマを代入
							}
							GlobalEnv.tabledata.put(FrontEnd.str_attribute, attributedata);// スキーマの載った葉をGlobalEnv.tabledataに載せる
						}
					}
					treedata.put("テーブルリスト", GlobalEnv.tabledata);// GlobalEnv.tabledataをtreedataに載せる
					final JTree tree = new JTree(treedata);
					tree.setToggleClickCount(4);
					tree.addMouseListener(new MouseListener() {

						@Override
						public void mouseReleased(MouseEvent e) {
							if(tree.getRowForLocation(e.getX(), e.getY()) == -1) return;

							if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2){
								DefaultMutableTreeNode current = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
								if(current.getChildCount() == 0){
//									String attribute = current.toString().substring(0, current.toString().indexOf("(") - 1);
									Functions.getTeble();
									System.out.println(GlobalEnv.currentTable_array);
									current = null;
									return;
								}
								if(current.getParent().toString().equals("テーブルリスト")){

									Functions.searchFrom();
									try {
										GlobalEnv.doc.insertString(GlobalEnv.fromEnd , current.toString() + " , ", CaretState.plane);
										GlobalEnv.textPane.setCaretPosition(GlobalEnv.fromEnd + current.toString().length() + 1);
										GlobalEnv.textPane.requestFocusInWindow();
									} catch (BadLocationException e1) {
										e1.printStackTrace();
									}

								}
							}
						}

						@Override
						public void mousePressed(MouseEvent e) {

						}

						@Override
						public void mouseExited(MouseEvent e) {

						}

						@Override
						public void mouseEntered(MouseEvent e) {

						}

						@Override
						public void mouseClicked(MouseEvent e) {

						}
					});
					tree.expandRow(0);
					DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
					ImageIcon icon2 = new ImageIcon(FrontEnd.get_execDir()
							+ GlobalEnv.OS_FS + "image" + GlobalEnv.OS_FS
							+ "vector4.png");
					ImageIcon icon3 = new ImageIcon(FrontEnd.get_execDir()
							+ GlobalEnv.OS_FS + "image" + GlobalEnv.OS_FS
							+ "icon.png");
					renderer.setLeafIcon(icon2);
					renderer.setClosedIcon(icon3);
					renderer.setOpenIcon(icon3);

					tree.setCellRenderer(renderer);

					FrontEnd.table_scrollpane.getViewport().setView(tree);
					FrontEnd.table_scrollpane.setPreferredSize(new Dimension(550, 1200));
				} finally {
					result_table.close();
				}
				stmt.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
