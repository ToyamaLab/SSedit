package ssedit.SSQL;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;

import ssedit.FrontEnd;
import ssedit.Caret.CaretState;
import ssedit.Common.Functions;
import ssedit.Common.GlobalEnv;

public class SSQL_exec extends FrontEnd implements Runnable {

//	SSQL_exec() {
//		super();
//	}
	
//	//環境変数の設定
//    public static void setenv(String name, String val){
//    	try {
//    		System.out.println(System.getenv("USER"));
//    		System.out.println("Before: "+System.getenv(name));
//    		
//    		
////			// 環境変数が設定されているマップをリフレクションを使用して持ってくる
////			Class<?> clazz = Class.forName("java.lang.ProcessEnvironment");
////			Field f = clazz.getDeclaredField("theCaseInsensitiveEnvironment");
////			f.setAccessible(true);
////			Map<String,String> fi = (Map<String,String>) f.get(null);
////			 
////			// そのマップにput
////			fi.put(name, val);
//    		
////    		Runtime rt = Runtime.getRuntime();
////    		String workingDir = Functions.getWorkingDir();
////    		Process proc = rt.exec(new String[]{
//////                    "export","R_HOME=/Library/Frameworks/R.framework/Resources"}, 
////                    "/bin/bash", "~/exec_export.sh"}, 
////                    null, null);
//			
//			System.out.println("After: "+System.getenv(name));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    }
	

	/**
	 * SuperSQLの実行
	 */
	private static final long serialVersionUID = 1L;
	static StringWriter sWriter; // 出力された文字列を受けとるためのオブジェクト
	static PrintWriter pWriter; // 出力された文字列を受けとるためのオブジェクト
	static BufferedReader buffReader; // 標準出力
	static BufferedReader errorBuffReader; // エラー出力

    //SuperSQLの実行
	private static final String workingDir = Functions.getWorkingDir();
	//NG: ssqltool5_2:	private static final String rjava_library_path = "."+GlobalEnv.OS_PS+"\""+new File(workingDir + GlobalEnv.OS_FS + "libs" + GlobalEnv.OS_FS + "jri").getAbsolutePath() + GlobalEnv.OS_FS+"\"";
	//OK: ssqltool5_3:	private static final String rjava_library_path = "."+GlobalEnv.OS_PS+new File(workingDir + GlobalEnv.OS_FS + "libs" + GlobalEnv.OS_FS + "jri").getAbsolutePath() + GlobalEnv.OS_FS;
	//OK: ssqltool5_4:
	public final static String java_library_path = "."+GlobalEnv.OS_PS
			                                         +new File(workingDir + GlobalEnv.OS_FS + "libs").getAbsolutePath() + GlobalEnv.OS_FS + GlobalEnv.OS_PS				// libs			mssql-jdbc_auth-8.2.2.x64.dll用
			                                         +new File(workingDir + GlobalEnv.OS_FS + "libs" + GlobalEnv.OS_FS + "jri").getAbsolutePath() + GlobalEnv.OS_FS;	// libs/jri		rJava用
    public static boolean execSuperSQL(String filename, String classPath, JTextPane resultPane, DefaultStyledDocument document) {
        try{
        	//setenv("R_HOME", "/Library/Frameworks/R.framework/Resources");	//TODO_old
        	//String java_library_path = "/Library/Frameworks/R.framework/Resources/library/rJava/jri/";
        	System.out.println("ssqltool5_6  -Djava.library.path="+java_library_path);
        	
        	String result = doExec(getCoomand(classPath, filename), resultPane, document);
//        	if(GlobalEnv.isLoggerOn()) {
//        		result = doExec(new String[]{
//                    "java",
//                    "-Dfile.encoding="+GlobalEnv.getEncoding(),
//                    "-Djava.library.path="+java_library_path,		//rJava用
//                    "-classpath", classPath,
//                    "supersql.FrontEnd",
//                    "-logger","on",									//20141210 masato -loggerは実習でのみ"-logger", "on"を配列の引数に追加
//                    "-f", filename}, resultPane, document);
//        	} else {
//        		System.out.println("logger off");
//        		result = doExec(new String[]{
//                     "java",
//                     "-Dfile.encoding="+GlobalEnv.getEncoding(),
//                     "-Djava.library.path="+java_library_path,		//rJava用
//                     "-classpath", classPath,
//                     "supersql.FrontEnd",
//                     "-f", filename}, resultPane, document);
//        	}
            if(result.equals("// completed normally //"))    return true;
            else                                             return false;
        }catch(Exception e){
            //e.printStackTrace();
            return false;
        }
    }
	public static boolean execSuperSQL2(String generateFileName, String query) {
		//System.out.println("実行中...　flag = " + GlobalEnv.runningFlag);
		ssqlExecLogs = "";
		Functions.createFile(generateFileName, query);
		try {
			String result = doExec(getCoomand(libsClassPath, generateFileName), null, null);
//			if(GlobalEnv.isLoggerOn()) {
//				 result = doExec(new String[] { "java",
//						"-Dfile.encoding="+GlobalEnv.getEncoding(), 
//	                    "-Djava.library.path="+java_library_path,		//rJava用
//						"-classpath", libsClassPath,
//						"supersql.FrontEnd", 
//						"-logger", "on", 								//20141210 masato -loggerは実習でのみ"-logger", "on"を配列の引数に追加
//						"-f", generateFileName}, null, null);
//			} else {
//				result = doExec(new String[] { "java",
//						"-Dfile.encoding="+GlobalEnv.getEncoding(), 
//	                    "-Djava.library.path="+java_library_path,		//rJava用
//						"-classpath", libsClassPath,
//						"supersql.FrontEnd", 
//						"-f", generateFileName}, null, null);
//			}
			if (result.equals("// completed normally //")) {
				errorStr[0] = "";
				errorStr[1] = "";
				//FrontEnd.tmp = GlobalEnv.textPane.getText();
				return true;
			} else {
				Functions.createFile(GlobalEnv.outdirPath + GlobalEnv.OS_FS + ".errorlog.txt", ssqlExecLogs);
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
    //実行コマンドの作成
    private static String[] getCoomand(String classpath, String filename) {
    	String[] command = new String[]{
                "java",
                "-Dfile.encoding="+GlobalEnv.getEncoding(),
                "-Djava.library.path="+java_library_path,		//rJava用
                "-classpath", classpath,
                "supersql.FrontEnd",
        };
    	//TODO SSedit直下のconfigを読む場合  "-c"追加
    	if (GlobalEnv.isLoggerOn()) {	//logger on
    		System.out.println("logger on");
    		command = Functions.arrayConcat(command, new String[]{"-logger","on"});
		}
    	command = Functions.arrayConcat(command, new String[]{"-f", filename});
    	System.out.println("command = "+Arrays.toString(command));
    	
    	return command;
	}


	/**
	 * 外部コマンドを実行する。 配列形式で渡すためにラップしてる
	 *
	 * @param command
	 *            実行する外部コマンド
	 * @return String 外部コマンドが標準出力に出力する実行結果
	 * @throws IOException
	 */
	public String doExec(String command, JTextPane resultPane, DefaultStyledDocument document)
			throws IOException {
		return doExec(new String[] { command }, resultPane, document);
	}

	/**
	 * 外部コマンドを実行する。
	 *
	 * @param commands
	 *            実行する外部コマンド（空白や引数を渡すための配列形式）
	 * @return String 外部コマンドが標準出力に出力する実行結果
	 * @throws IOException
	 */
	public static String doExec(String[] commands, JTextPane resultPane, DefaultStyledDocument document)
//	public static String doExec(String[] commands, JTextArea resultArea)
			throws IOException {
		// ランタイムオブジェクト取得
		//Runtime rt = Runtime.getRuntime();
		// 実行しているディレクトリを指定してコマンドを実行（用途に合わせてパスや環境変数を追加する必要あり）
		// Process proc = rt.exec(commands, null, new
		// File(Thread.currentThread().getContextClassLoader().getResource("").getPath()));
//		String workingDir = new File(GlobalEnv.EXE_FILE_PATH).getAbsolutePath(); // 実行jarファイルの絶対パスを取得
//		if (workingDir.contains(":")) {// ビルドバスの追加を行うと参照ライブラリ内のファイルのパスも付け加えてしまう仕様らしいので、:移行カット
//			workingDir = workingDir.substring(0, workingDir.indexOf(":"));
//		}
//		if (workingDir.endsWith(".jar")) { // jarファイルを実行した場合（Eclipseから起動した場合は入らない）
//			workingDir = workingDir.substring(0, workingDir.lastIndexOf(GlobalEnv.OS_FS));
//		}
		//String workingDir = Functions.getWorkingDir();
//		try {
//			document.insertString(resultPane.getCaretPosition(), "workingdir = " + workingDir + "\n", CaretState.attr3);
//		} catch (BadLocationException e1) {
//			e1.printStackTrace();
//		}
		
		
		

//		////////////////////////////////////////////////////////////////////////////////////////////////////////
//		
//		//TODO  [重要] R_HOME取得用shの実行
//		System.out.println("Get R_HOME: start");
//		BufferedReader buffReader0 = null;
//		BufferedReader errorBuffReader0 = null;
//		try {
//			Runtime rt0 = Runtime.getRuntime();
////			Runtime.getRuntime().exec("Rscript myScript.R"); 
//	//		String[] c = {"sh", "~/z.sh"};
//	//		ProcessBuilder pb0 = new ProcessBuilder(new String[] {"sh", "~/z.sh"});
//	//		ProcessBuilder pb0 = new ProcessBuilder("sh", "-c", "echo 'Hello!'");
////			ProcessBuilder pb0 = new ProcessBuilder("~/z.sh");
////			ProcessBuilder pb0 = new ProcessBuilder("Rscript", "~/z.R");
////			ProcessBuilder pb0 = new ProcessBuilder("/bin/sh", "R", "--quiet", "--vanilla", "--slave", "<<EOF", "R.home()", "EOF");
////			Process proc0 = pb0.start();
//			
//			
////			Process proc0 = rt0.exec(new String[]{"Rscript", "~/z.R"}, null, new File("/usr/local/bin"));
////			Process proc0 = rt0.exec(new String[]{"/usr/local/bin/Rscript", "~/z.R"}, null, new File("/usr/local/bin"));
////			Process proc0 = rt0.exec(new String[]{"which", "Rscript"}, null, null);
////			Process proc0 = rt0.exec(new String[]{"echo", "$PATH"}, null, null);
//			Process proc0 = rt0.exec(new String[]{"~/z.sh"}, null, null);
//			buffReader0 = new BufferedReader(new InputStreamReader(proc0.getInputStream(), GlobalEnv.getEncoding()));
//			errorBuffReader0 = new BufferedReader(new InputStreamReader(proc0.getErrorStream(), GlobalEnv.getEncoding()));
//
//			String line = "";
//			System.out.println("try1");
//			while ((line = buffReader0.readLine()) != null) {
//				System.out.println(line);
//			}
//			line = "";
//			while ((line = errorBuffReader0.readLine()) != null) {
//				System.out.println(line);
//			}
//			System.out.println("try2");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			buffReader0.close();
//			errorBuffReader0.close();
//		}
//		System.out.println("Get R_HOME: end");
//		
//		////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(workingDir));
        pb.redirectErrorStream(true);
        
        
        //[重要] rJava用 環境変数($R_HOME)の設定  							//TODO_old これで通るが他の位置で？ -> おそらくここでOK
        String rhome_path = GlobalEnv.rhome_pathField.getText();		// 設定タブから取得
        if(!rhome_path.equals("")){
            Map<String, String> env = pb.environment();
            //env.put("R_HOME", "/Library/Frameworks/R.framework/Resources");
            env.put("R_HOME", rhome_path);								//環境変数$R_HOMEを指定
		}
        
		
		
		
		
//		Process proc = rt.exec(commands, null, new File(workingDir));
		Process proc = pb.start();

		// 実行結果の取得用のオブジェクトの作成
		buffReader = new BufferedReader(new InputStreamReader(
				proc.getInputStream(), GlobalEnv.getEncoding()));
		errorBuffReader = new BufferedReader(new InputStreamReader(
				proc.getErrorStream(), GlobalEnv.getEncoding()));
		sWriter = new StringWriter();
		pWriter = new PrintWriter(sWriter);

		String line_end = ""; // 結果の最終行を格納
		try {
			String line = "";
			String str = "";
			// 正常ログ
			while ((line = buffReader.readLine()) != null) {
				ssqlExecLogs += line + "" + GlobalEnv.OS_LS + "";
				if (resultPane != null)
//					resultArea.append(line + "" + GlobalEnv.OS_LS + "");
					document.insertString(document.getLength(), line + "" + GlobalEnv.OS_LS + "", CaretState.plane);
					resultPane.setCaretPosition(document.getLength());
				line_end = line;
			}
			// エラーログ
			while ((line = errorBuffReader.readLine()) != null) {
				str += line + "" + GlobalEnv.OS_LS + "";
				ssqlExecLogs += line + "" + GlobalEnv.OS_LS + "";
				if (resultPane != null){
					document.insertString(document.getLength(), line + "" + GlobalEnv.OS_LS + "", CaretState.errAttr);
					resultPane.setCaretPosition(document.getLength());

				}

			}
//			Log.ggg(str);
//			System.out.println(str);
//			document.insertString(document.getLength(), "workingDir = " + workingDir + "" + GlobalEnv.OS_LS + "" +
//														"java_library_path = " + java_library_path + "" + GlobalEnv.OS_LS + "",
//														CaretState.plane);
//			resultPane.setCaretPosition(document.getLength());

		} catch (Exception e) {
			;
		} finally {
			buffReader.close();
			errorBuffReader.close();
			pWriter.close();
		}
		return line_end; // 結果の最終行をreturn
	}

	/**
	 * コマンドの実行結果を読み出す。
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			while (buffReader.ready()) {
				pWriter.println(buffReader.readLine());
			}
			while (errorBuffReader.ready()) {
				pWriter.println(errorBuffReader.readLine());
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
}
