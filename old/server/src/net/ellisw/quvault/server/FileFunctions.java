package net.ellisw.quvault.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileFunctions {
	public static String readTextFile(String fullPathFilename) throws IOException {
		StringBuffer sb = new StringBuffer(1024);
		BufferedReader reader = new BufferedReader(new FileReader(fullPathFilename));

		char[] chars = new char[1024];
		int n = 0;
		while ((n = reader.read(chars)) > 0) {
			sb.append(chars, 0, n);
		}

		reader.close();

		return sb.toString();
	}
}
