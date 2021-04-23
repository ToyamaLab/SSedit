package ssedit.DB;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Field;
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
import ssedit.SSQL.SSQL_exec;


public class DB {
	public static void db() {	//テーブルリストの取得・更新
		
		// [重要] java.library.pathの追加		sqlserver integreted_security=true  libs以下のdll読み込み用
		// SSeditを「-Djava.library.path=.;C:\XXX\SSedit\SSedit_20210405\libs」付きで起動させなくてOKにするため
		setJavaLibPath();			//TODO Azure Windows Java15だとうまくいかない	java.lang.NoSuchFieldException: sys_paths	2021/4/6
		
//		String config_fn = GlobalEnv.USER_HOME + GlobalEnv.OS_FS + GlobalEnv.configFile;
		String config_fn = Functions.getConfigSaveDir()+ GlobalEnv.configFile;
		
		GlobalEnv.tabledata.clear();
		File file = new File(config_fn);
		// 使用しているドライバー
		String driver = "";
		// データベースのパス
		String db = "";
		// データベースのホスト名
		String host = "";
		// データベースのユーザー
		String user = "";

//		String outdir = "";

		if(file.exists()){
			driver = Functions.has(config_fn, "driver");
			db = Functions.has(config_fn, "db");
			host = Functions.has(config_fn, "host");
			user = Functions.has(config_fn, "user");
			String password = Functions.has(config_fn, "password"); 
			String port = Functions.has(config_fn, "port"); 
	//		outdir = Common.has(config_fn, "outdir");
	//		if(Common.has(config_fn, "outdir").isEmpty() && !GlobalEnv.folderPath.isEmpty()){
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
				} else if (driver.equals("sqlserver")) {
					System.out.println("sqlserver");
					
					// 接続URLの一般的な書式:		https://docs.microsoft.com/ja-jp/sql/connect/jdbc/building-the-connection-url?view=sql-server-ver15
					// 	jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
					// 指定可能なオプション(プロパティ)一覧： https://docs.microsoft.com/ja-jp/sql/connect/jdbc/setting-the-connection-properties?view=sql-server-ver15
					// 	-> 50個以上あるため、optionsへ ; 区切りで記述できる形で実装
					String url = "jdbc:sqlserver://";
					if(host != null && !host.equals("")){
						String instanceName = Functions.has(config_fn, "sqlserver_instance").trim();
						String options = Functions.has(config_fn, "sqlserver_options").trim();

						url += host.trim();																			//serverName
						if(instanceName != null && !instanceName.equals(""))	url += "\\"+instanceName.trim();	//instanceName
						if(port != null && !port.equals(""))					url += ":"+port.trim();				//portNumber
						if(db != null && !db.equals(""))						url += ";databaseName="+db.trim();	//databaseName
						if(options != null && !options.equals(""))				url += ";"+options.trim();			//上記以外のオプション指定
																													// -> 下記に書かれているものを ; 区切りで書く  e.g. sqlserver_options=accessToken=a;applicationName=a
																													//    指定可能なオプション(プロパティ)一覧 https://docs.microsoft.com/ja-jp/sql/connect/jdbc/setting-the-connection-properties?view=sql-server-ver15
					}
					System.out.println("SQL Server URL: "+url);
					
					
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					con = DriverManager.getConnection(url, user, password);
					System.out.println("SQL Server接続完了");
				} else {
					// 相対パスだったら
					Class.forName("org." + driver + ".JDBC");
					if (db.startsWith("./") || !db.startsWith("/")) {
						if (db.startsWith("./")) {
							db = db.substring(db.indexOf("/"));
						}
						db = GlobalEnv.folderPath + GlobalEnv.OS_FS + db;
					}
					con = DriverManager.getConnection("jdbc:" + driver + ":" + db);
				}
				// ステートメントオブジェクトを生成
				Statement stmt = con.createStatement();
				//stmt.setQueryTimeout(30); // set timeout to 30 sec.
				DatabaseMetaData dmd = con.getMetaData();
//				dmd.nullsAreSortedAtEnd();
//				dmd.nullsAreSortedLow();
				ResultSet result_table = null;
				String types[] = {"TABLE" , "VIEW"};
				result_table = dmd.getTables(null, null, "%", types);// テーブルを取得
				

				
				/* メタデータ(テーブル名, 属性名)の取得 */
				Hashtable<String, Object> treedata = new Hashtable<String, Object>();	// テーブルの一覧を載せる

				try {
					//TODO sqlserver スキーマ名の取得
//					ResultSet sn = dmd.getSchemas();
//					while (sn.next()) {
//						System.out.println("sn = "+sn.getString(1));						
//					}
					
					
					
					GlobalEnv.table_array.clear();
					ResultSet result_attribute = null;
					// TODO テーブル名をソートして表示
					while (result_table.next()) {// 次のテーブルが存在しなくなるまで
						Hashtable<String, String> attributedata = new Hashtable<String, String>();	// スキーマの情報を載せる、テーブルが次に進むたびに初期化
						// テーブル
						FrontEnd.str_attribute = result_table.getString("TABLE_NAME");
						if (!FrontEnd.str_attribute.startsWith("pg_")
								&& !FrontEnd.str_attribute.startsWith("sql_") && !FrontEnd.str_attribute.startsWith("SQLITE")) {
//							System.out.println(" = "+result_table.getString(1)+" "+result_table.getString(2)
//									+" "+result_table.getString(3)+" "+result_table.getString(4));	//TODO スキーマ名表示
////							FrontEnd.str_attribute = result_table.getString(2)+"."+FrontEnd.str_attribute;//TODO スキーマ名を付与
							
							GlobalEnv.table_array.add(FrontEnd.str_attribute);
							result_attribute = dmd.getColumns(null, null, FrontEnd.str_attribute, "%");	// テーブル上のスキーマを取得
							while (result_attribute.next()) {// スキーマがなくなるまで
								String name = result_attribute.getString("COLUMN_NAME");	// 属性名を取得
								String type = result_attribute.getString("TYPE_NAME");		// その属性のデータ型を取得
								attributedata.put(name + " (" + type + ")",	"attribute");	// 葉にスキーマを代入
							}
							GlobalEnv.tabledata.put(FrontEnd.str_attribute, attributedata);	// スキーマの載った葉をGlobalEnv.tabledataに載せる
							result_attribute.close();
							result_attribute = null;
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
					result_table = null;
				}
				stmt.close();
				con.close();
				System.out.println("テーブルリスト更新完了");
			} catch (Exception e) {
				System.out.println("テーブルリスト更新失敗");
				e.printStackTrace();
			}
		}
	}

	// [重要] java.library.pathの追加		sqlserver integreted_security=true  libs以下のdll読み込み用
	// SSeditを「-Djava.library.path=.;C:\XXX\SSedit\SSedit_20210405\libs」付きで起動させなくてOKにするため
	private static boolean alreadySetJavaLibPath = false;
	private static void setJavaLibPath() {
		if (!alreadySetJavaLibPath) {	//初回1回のみ実行
			//参考: https://blogs.osdn.jp/2017/09/25/libpath.html
			
			//String newPath = SSQL_exec.java_library_path;
					//newPath = newPath.substring(2, newPath.length());
			String newPath = SSQL_exec.java_library_path + GlobalEnv.OS_PS +System.getProperty("java.library.path");	//TODO こちらで大丈夫？
			try {
				System.out.println("setJavaLibPath() start");
				System.out.println("currentPath = "+System.getProperty("java.library.path"));
				System.out.println("newPath = "+newPath);
				
				System.setProperty("java.library.path", newPath);					// java.library.path を変更(この時点では反映されない)
				Field sys_paths = ClassLoader.class.getDeclaredField("sys_paths");	// 以下3行: sys_paths フィールドに null を代入、これで次にライブラリーをロードするときに最新の java.library.path が参照される
				sys_paths.setAccessible(true);
				sys_paths.set(null, null);
				
				
				
//			    final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
//			    usrPathsField.setAccessible(true);
//			    //get array of paths
//			    final String[] paths = (String[])usrPathsField.get(null);
////			    //check if the path to add is already present
////			    for(String path : paths) {
////			        if(path.equals(newPath)) {
////			            return;
////			        }
////			    }
//			    //add the new path
//			    final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
//			    newPaths[newPaths.length-1] = newPath;
//			    usrPathsField.set(null, newPaths);
			} catch (Exception e) {
				System.out.println("setJavaLibPath() failed");
				e.printStackTrace();
				alreadySetJavaLibPath = true;
				return;
			}
			System.out.println("setJavaLibPath() end");
			System.out.println("currentPath = "+System.getProperty("java.library.path"));
			alreadySetJavaLibPath = true;
			return;
			
		} else {
			System.out.println("alreadySetJavaLibPath = "+alreadySetJavaLibPath);
			System.out.println("currentPath = "+System.getProperty("java.library.path"));
			return;
		}

		
//		String newPath = "C:/mylibs";
//
//		Field usr_paths = ClassLoader.class.getDeclaredField("usr_paths");
//		usr_paths.setAccessible(true);
//
//		// 現在の検索パスを取得します。
//		String[] paths = (String[])usr_paths.get(null);
//
//		// すでに検索パスに含まれていたら何もせずに復帰します。
//		for(String path : paths) {
//			if(path.equals(newPath)) {
//				return;
//			}
//		}
//
//		// 検索パスを追加した配列を新しく作成して usr_paths に代入します。
//		String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
//		newPaths[newPaths.length - 1] = newPath;
//		usr_paths.set(null, newPaths);
		
	}
	
}
