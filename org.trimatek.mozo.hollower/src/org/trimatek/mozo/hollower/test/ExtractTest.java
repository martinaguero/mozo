package org.trimatek.mozo.hollower.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.trimatek.mozo.hollower.utils.JarUtils;

public class ExtractTest {

	public static void main(String[] args) throws IOException {

		FileInputStream inputStream = new FileInputStream(
				"F:\\Temp\\mozo\\deep.jar");
		JarInputStream jarFile = new JarInputStream(inputStream);
		JarEntry jarEntry;
		while (true) {
			jarEntry = jarFile.getNextJarEntry();
			if (jarEntry == null) {
				break;
			}
			if (!jarEntry.isDirectory()) {
				File file = JarUtils.extractFile(jarFile, jarEntry.getName());
			}

		}
		jarFile.close();

	}

}
