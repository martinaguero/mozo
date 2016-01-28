package org.trimatek.mozo.hollower.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarInputStream;

import org.trimatek.mozo.hollower.utils.JarUtils;

public class HollowerTest {

	public static void main(String[] args) throws IOException {

		List<String> classes = JarUtils.listClasses(new JarInputStream(
				new FileInputStream("F:\\Temp\\mozo\\deep.jar")));
		for (String string : classes) {
			System.out.println(string);
		}

		// InputStream is = new FileInputStream("F:\\Temp\\mozo\\Deep.class");
		// // ClassParser p = new ClassParser(is, "Deep");
		//
		// ClassParser p = new ClassParser(is, "org/trimatek/deep/Deep");
		//
		// Hollower h = new Hollower();
		// JavaClass jc = h.hollow(p.parse());
		//
		// jc.dump("F:\\Temp\\mozo\\DeepCascara.class");
		// is.close();

	}

}
