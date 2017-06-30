package ssedit.Common;

public class Log {

	//デバッグモードレベル(0〜3) (コンソールにどのレベルの内容を出力するか、0:未出力)
	public static int debug = 0;

	public Log() {
	}

	public static void i(String s) {
		System.out.println(s);
	}
	public static void i1(String s) {
		if(debug>=1) i(s);
	}
	public static void i2(String s) {
		if(debug>=2) i(s);
	}
	public static void i3(String s) {
		if(debug>=3) i(s);
	}

	public static void e(String s) {
		System.err.println(s);
	}
	public static void e1(String s) {
		if(debug>=1) e(s);
	}
	public static void e2(String s) {
		if(debug>=2) e(s);
	}
	public static void e3(String s) {
		if(debug>=3) e(s);
	}

}
