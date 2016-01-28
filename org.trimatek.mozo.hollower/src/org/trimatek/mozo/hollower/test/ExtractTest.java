package org.trimatek.mozo.hollower.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.bcel.classfile.ClassParser;
import org.trimatek.mozo.hollower.tools.Hollower;
import org.trimatek.mozo.hollower.utils.JarUtils;

public class ExtractTest {

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
				className = className.substring(className.lastIndexOf('/') + 1);
				System.out.println(className);
				
				OutputStream out = JarUtils.extractFile(jarFile, "F:\\Temp\\mozo\\prueba\\"
						+ className, 4096);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    baos = (ByteArrayOutputStream) out;
			    InputStream input = new ByteArrayInputStream(baos.toByteArray());
				Hollower h = new Hollower();
				ClassParser cp = new ClassParser(input, className);
				h.hollow(cp.parse());
				
			}
		}
		jarFile.close();

	}

}
