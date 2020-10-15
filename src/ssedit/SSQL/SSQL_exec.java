package ssedit.SSQL;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

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

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	static StringWriter sWriter; // 出力された文字列を受けとるためのオブジェクト
	static PrintWriter pWriter; // 出力された文字列を受けとるためのオブジェクト
	static BufferedReader buffReader; // 標準出力
	static BufferedReader errorBuffReader; // エラー出力

    //SuperSQLの実行
    public static boolean execSuperSQL(String filename, String classPath, JTextPane resultPane, DefaultStyledDocument document) {
        try{
        	String result = "";
        	if(GlobalEnv.loggerFlag) {
            result = doExec(new String[]{
                    "java",
                    "-Dfile.encoding=UTF-8",
                    "-classpath", classPath,
                    "supersql.FrontEnd",
                    //20141210 masato -loggerは実習でのみ"-logger", "on"を配列の引数に追加
                    "-logger",
                    "on",
                    "-f", filename}, resultPane, document);
        	} else {
        		result = doExec(new String[]{
                     "java",
                     "-Dfile.encoding=UTF-8",
                     "-classpath", classPath,
                     "supersql.FrontEnd",
                     //20141210 masato -loggerは実習でのみ"-logger", "on"を配列の引数に追加
                     "-f", filename}, resultPane, document);
        	}
            if(result.equals("// completed normally //"))    return true;
            else                                             return false;
        }catch(Exception e){
            //e.printStackTrace();
            return false;
        }
    }

	public static boolean execSuperSQL2(String generateFileName, String query) {
//		System.out.println("実行中...　flag = " + GlobalEnv.runningFlag);
		ssqlExecLogs = "";
		Functions.createFile(generateFileName, query);
		try {
			String result = "";
			if(GlobalEnv.loggerFlag) {
			 result = doExec(new String[] { "java",
					"-Dfile.encoding=UTF-8", "-classpath", libsClassPath,
                    //20141210 masato -loggerは実習でのみ"-logger", "on"を配列の引数に追加
					"supersql.FrontEnd", "-logger", "on", "-f", generateFileName}, null, null);
			} else {
			result = doExec(new String[] { "java",
					"-Dfile.encoding=UTF-8", "-classpath", libsClassPath,
	                 //20141210 masato -loggerは実習でのみ"-logger", "on"を配列の引数に追加
					"supersql.FrontEnd", "-f", generateFileName}, null, null);
			}
			if (result.equals("// completed normally //")) {
				errorStr[0] = "";
				errorStr[1] = "";
//				FrontEnd.tmp = GlobalEnv.textPane.getText();
				return true;
			} else {
				Functions.createFile(GlobalEnv.outdirPath + GlobalEnv.OS_FS + ".errorlog.txt", ssqlExecLogs);
				return false;
			}
		} catch (Exception e) {
			return false;
		}
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
		Runtime rt = Runtime.getRuntime();
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
		String workingDir = Functions.getWorkingDir();
//		try {
//			document.insertString(resultPane.getCaretPosition(), "workingdir = " + workingDir + "\n", CaretState.attr3);
//		} catch (BadLocationException e1) {
//			// TODO 自動生成された catch ブロック
//			e1.printStackTrace();
//		}
		Process proc = rt.exec(commands, null, new File(workingDir));

		// 実行結果の取得用のオブジェクトの作成
		buffReader = new BufferedReader(new InputStreamReader(
				proc.getInputStream()));
		errorBuffReader = new BufferedReader(new InputStreamReader(
				proc.getErrorStream()));
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
