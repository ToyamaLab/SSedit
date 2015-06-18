package ssedit.LinkForeach;
//package ssqltool.LinkForeach;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//public class Start {
//
//	/**
//	 * @param args
//	 * @throws IOException
//	 */
//	public static void main(String[] args) throws IOException {
//		String f = "/Users/goto/Documents/workspace/LF_test/test05.ssql";
//		String s = readFile(f);
//		LinkForEach.process(s, f);
//	}
//
//	public static String readFile(String f) throws IOException {
//		File file = new File(f);
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//			StringBuffer sb = new StringBuffer();
//			int c;
//			while ((c = br.read()) != -1) {
//				sb.append((char) c);
//			}
//			return sb.toString();
//		} finally {
//			br.close();
//		}
//	}
//}
