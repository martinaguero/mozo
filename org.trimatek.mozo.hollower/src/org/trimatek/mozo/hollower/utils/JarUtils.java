package org.trimatek.mozo.hollower.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarUtils {

	public static List<String> listClasses(String path) throws IOException {
		List<String> classes = new ArrayList<String>();
		JarInputStream jarFile = new JarInputStream(new FileInputStream(path));
		JarEntry jarEntry;
		while (true) {
			jarEntry = jarFile.getNextJarEntry();
			if (jarEntry == null) {
				break;
			}
			if ((jarEntry.getName().endsWith(".class"))) {
				String className = jarEntry.getName().replaceAll("/", "\\.");
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
