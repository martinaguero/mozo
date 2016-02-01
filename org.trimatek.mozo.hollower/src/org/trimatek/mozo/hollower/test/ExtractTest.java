package org.trimatek.mozo.hollower.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.trimatek.mozo.hollower.tools.Hollower;
import org.trimatek.mozo.hollower.utils.JarUtils;

public class ExtractTest {

	public static void main(String[] args) throws IOException {

		FileInputStream inputStream = new FileInputStream(
				"F:\\Temp\\lib\\spring.jar");
		JarInputStream jarFile = new JarInputStream(inputStream);
		JarEntry jarEntry;
		while (true) {
			jarEntry = jarFile.getNextJarEntry();
			if (jarEntry == null) {
				break;
			}
			if (!jarEntry.isDirectory()) {
				File file = JarUtils.extractFile(jarFile, jarEntry.getName());
				if (jarEntry.getName().contains(".class")) {
					ClassParser cp = new ClassParser(new FileInputStream(file),
							jarEntry.getName());
					JavaClass javaClass = cp.parse();
					if (!javaClass.isInterface()) {
						Hollower hollower = new Hollower();						
						javaClass = hollower.hollow(javaClass);
						javaClass.dump(file);
					}
				}
			}

		}
		jarFile.close();

	}
}
