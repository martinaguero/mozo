package org.trimatek.mozo.dock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavapTest {

	public static void main(String[] args) throws IOException {

		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec("D:\\bin\\jdk-9\\bin\\javap d:\\Temp\\module-info.class");
		BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
		  sb.append(line).append("\n");
		}
		String answer = sb.toString();
		System.out.println(answer);

	}

}
