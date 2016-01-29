package org.trimatek.mozo.hollower.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipInputStream;

import org.trimatek.mozo.hollower.Config;

public class JarUtils {

	public static List<String> listClasses(JarInputStream jarFile)
			throws IOException {
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

	public static File extractFile(ZipInputStream zipIn, String filePath)
			throws IOException {
		String dirPath = "";
		if (filePath.contains("/")) {
			dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
		}
		new File(Config.TEMP_DIR + dirPath).mkdirs();
		File file = new File(Config.TEMP_DIR + filePath);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file));
		byte[] bytesIn = new byte[Config.BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
		return file;
	}

}
