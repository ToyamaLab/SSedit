package ssedit.Viewer;

import ssedit.Common.GlobalEnv;
import ssedit.SSQL.SSQL_exec;
import javafx.application.Platform;

public class ExecViewerThread extends Thread {
	public void run() {
		if (SSQL_exec.execSuperSQL2(GlobalEnv.outdirPath + GlobalEnv.OS_FS
				+ ".htmlViewer.ssql", GlobalEnv.textPane.getText())) {
			// V.setValue();
			GlobalEnv.runningFlag = true;
			System.out.println("実行完了：成功　flag = " + GlobalEnv.runningFlag
					+ "　次の実行どうぞ！");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// javaFX operations should go here
					HtmlViewer.setValue();
				}
			});
			// errorStr[0] = "";
			// errorStr[1] = "";
			return;
		} else {
			GlobalEnv.runningFlag = true;
			System.out.println("実行完了：失敗　flag= " + GlobalEnv.runningFlag
					+ "　次の実行どうぞ！");
			return;
			// errorStr = checkError();
		}
	}
}
