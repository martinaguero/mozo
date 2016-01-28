package org.trimatek.mozo.hollower.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class HollowerTest {

	public static void main(String[] args) throws IOException {

		FileInputStream inputStream = new FileInputStream(
				"F:\\Temp\\mozo\\deep.jar");
		JarInputStream jarFile = new JarInputStream(inputStream);
		JarEntry jarEntry;
		String className;
		while (true) {
			jarEntry = jarFile.getNextJarEntry();
			if (jarEntry == null) {
				break;
			}

			className = jarEntry.getName();

			if ((className.endsWith(".class"))) {
				//className = className.substring(className.lastIndexOf('/') + 1);
				System.out.println(className);
				new File("F:\\Temp\\mozo\\prueba\\" + className.replace("/", "\\"));
//				JarUtils.extractFile(jarFile, "F:\\Temp\\mozo\\prueba\\"
//						+ className.replace("/", "\\"), 4096);
			}
		}
		jarFile.close();

	}

}
