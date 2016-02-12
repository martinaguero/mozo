package org.trimatek.mozo.hollower.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.trimatek.mozo.hollower.Context;
import org.trimatek.mozo.hollower.service.HollowerService;
import org.trimatek.mozo.hollower.tools.Litter;
import org.trimatek.mozo.hollower.utils.JarUtils;

public class HollowerServiceImpl implements HollowerService {
	
	Litter litter = new Litter();

	@Override
	public OutputStream hollow(InputStream inputStream, String jarName) throws IOException, ClassNotFoundException {
		Context ctx = new Context(jarName);
		JarInputStream jarFile = new JarInputStream(inputStream);
		JarEntry jarEntry;
		while (true) {
			jarEntry = jarFile.getNextJarEntry();
			if (jarEntry == null) {
				break;
			}
			if (!jarEntry.isDirectory()) {
				File file = JarUtils.extractFile(jarFile, jarEntry.getName(),ctx);
				if (jarEntry.getName().contains(".class")) {
					ClassParser cp = new ClassParser(new FileInputStream(file),
							jarEntry.getName());
					JavaClass javaClass = cp.parse();
					if (javaClass.isPublic()) {
						javaClass = litter.buildLiteVersion(javaClass,ctx); 
						javaClass.dump(ctx.OUTPUT_DIR + jarEntry.getName());
					} 
				}
			}
		}
		jarFile.close();
		return JarUtils.buildHollowedJar(ctx);
	}
}
