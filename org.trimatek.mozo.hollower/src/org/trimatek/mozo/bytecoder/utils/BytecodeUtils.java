package org.trimatek.mozo.bytecoder.utils;

import java.util.regex.Pattern;

import javax.lang.model.SourceVersion;

public class BytecodeUtils {

	private static Pattern p = Pattern
			.compile("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*");

	public static boolean isClassName(String className) {
		return p.matcher(className).matches();
	}

	public static boolean isValidSimpleClassName(String className) {
		return SourceVersion.isIdentifier(className)
				&& !SourceVersion.isKeyword(className);
	}

	public static String checkClassRef(String string) {
		if (string.startsWith("L") && string.endsWith(";")) {
			string = string.substring(1, string.length() - 1);
		} else if (string.startsWith("(L") && string.endsWith(";)V")) {
			string = string.substring(2, string.length() - 3);
		}
		return string.replace("/", ".");
	}
}
