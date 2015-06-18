package ssedit.GUI;

import javax.swing.text.DefaultStyledDocument;

public class Indent2 {

	public static void process (String query) {
		indent(query);

	}

	private static void indent (String query) {

		StringBuilder sb = new StringBuilder(query);
		int length = sb.length();

		for (int i = 0; i < length; i++) {

			//1文字ずつ取得
			char str = sb.charAt(i);

			if (str == '{') {

				char str2 = sb.charAt(i-1);
//				System.out.println(str2);
				
				if (str2 != '@') {				
					sb.insert(i, "\n");
//					sb.insert(i+2, "a");
					System.out.println(sb);
					break;
				}
			}

		}
	}
}

