package org.trimatek.mozo.bytecoder.utils;

import java.util.regex.Pattern;

import javax.lang.model.SourceVersion;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.model.Class;

public class BytecodeUtils {

	private static Pattern p = Pattern.compile("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*");

	public static boolean isClassName(String className) {
		return p.matcher(className).matches();
	}

	public static boolean isValidSimpleClassName(String className) {
		return SourceVersion.isIdentifier(className) && !SourceVersion.isKeyword(className);
	}

	public static String checkClassRef(String string) {
		if (string.startsWith("L") && string.endsWith(";")) {
			string = string.substring(1, string.length() - 1);
		} else if (string.startsWith("(L") && string.endsWith(";)V")) {
			string = string.substring(2, string.length() - 3);
		}
		return string.replace("/", ".");
	}

	public static int[] split(String className, int[] namespace) {
		String[] splitted = className.split("\\.");
		for (int i = 0; i < splitted.length; i++) {
			namespace[i] = namespace[i] + 1;
		}
		return namespace;
	}

	public static Version detachNamespace(int[] footprint, Version version) {
		int cut = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < footprint.length; i++) {
			if (footprint[i] >= cut) {
				cut = footprint[i];
			} else {
				cut = i - 1;
				break;
			}
		}
		for (Class clazz : version.getClasses()) {
			String[] splitted = clazz.getClassName().split("\\.");
			for (int i = 0; i < cut; i++) {
				sb.append(splitted[i] + ".");
			}
			break;
		}
		version.setNamespace(sb.toString().substring(0, sb.toString().length() - 1));
		return version;
	}

}
