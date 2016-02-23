package org.trimatek.mozo.bytecoder.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.io.IOUtils;
import org.trimatek.mozo.bytecoder.Context;
import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.bytecoder.tools.Litter;
import org.trimatek.mozo.bytecoder.utils.JarUtils;
import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Version;

public class BytecodeServiceImpl implements BytecodeService {

	Litter litter = new Litter();

	@Override
	public Version fillClasses(Version version) throws IOException {
		List<String> classes;
		if (version.getJar() == null) {
			if (version.getUrl() == null) {
				throw new NullPointerException("POM file URL can not be null.");
			}
			version = JarUtils.loadJar(version);
		}
		classes = JarUtils.listClasses(new JarInputStream(
				new ByteArrayInputStream(version.getJar())));
		for (String className : classes) {
			version.addClass(new Class(className, version.getSnapshot()));
		}
		return version;
	}

	@Override
	public Version loadJar(Version version) throws IOException {
		return JarUtils.loadJar(version);
	}

	@Override
	public Version buildJarProxy(Version version) throws IOException,
			ClassNotFoundException {
		Context ctx = new Context(version.getArtifactId() + ".jar");
		JarInputStream jarFile = new JarInputStream(new ByteArrayInputStream(
				version.getJar()));
		JarEntry jarEntry;
		while (true) {
			jarEntry = jarFile.getNextJarEntry();
			if (jarEntry == null) {
				break;
			}
			if (!jarEntry.isDirectory()) {
				File file = JarUtils.extractFile(jarFile, jarEntry.getName(),
						ctx);
				if (jarEntry.getName().contains(".class")) {
					ClassParser cp = new ClassParser(new FileInputStream(file),
							jarEntry.getName());
					JavaClass javaClass = cp.parse();
					Class clazz = new Class(javaClass.getClassName(),version.getSnapshot());
					clazz.setPublicClass(Boolean.FALSE);
					if (javaClass.isPublic()) {
						clazz.setPublicClass(Boolean.TRUE);
						javaClass = litter.buildLiteVersion(javaClass, ctx);
						javaClass.dump(ctx.OUTPUT_DIR + jarEntry.getName());
					}
					clazz.setVersion(version);
					version.addClass(clazz);
				}
			}
		}
		jarFile.close();
		File lite = JarUtils.buildHollowedJar(ctx);
		FileInputStream fis = new FileInputStream(lite);
		version.setJarProxy(IOUtils.toByteArray(fis));
		fis.close();
		lite.delete();
		return version;
	}
}
