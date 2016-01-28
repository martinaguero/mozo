package org.trimatek.mozo.hollower.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarUtils {

	public static List<String> listClasses(JarInputStream jarFile) throws IOException {
		List<String> classes = new ArrayList<String>();
		JarEntry jarEntry;
		String className;
		while (true) {
			jarEntry = jarFile.getNextJarEntry();
			if (jarEntry == null) {
				break;
			}
			className = jarEntry.getName();
			if ((className.endsWith(".class"))) {
				classes.add(className.substring(0, className.lastIndexOf('.')));
			}
		}
		jarFile.close();
		return classes;
	}

	public static String getJarName(String jarPath) {
		return jarPath.substring(jarPath.lastIndexOf("\\") + 1,
				jarPath.lastIndexOf("."));
	}

}
