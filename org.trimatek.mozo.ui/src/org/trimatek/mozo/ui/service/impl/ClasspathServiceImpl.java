package org.trimatek.mozo.ui.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.ui.Context;
import org.trimatek.mozo.ui.service.ClasspathService;

public class ClasspathServiceImpl implements ClasspathService {

	@Override
	public List<File> buildJars(Version version) throws IOException {
		List<File> jars = new ArrayList<File>();
		Context ctx = new Context(version.getArtifactId() + "-" + version.getVersion() + ".jar");
		File jar = new File(ctx.OUTPUT_DIR);
		jar.mkdir();
		jars.add(jar);
		for (Class clazz : version.getClasses()) {
			String dirPath = "";
			if (clazz.getClassName().contains(".")) {
				dirPath = clazz.getClassName().substring(0, clazz.getClassName().lastIndexOf("."));
				dirPath = dirPath.replace(".", "\\");
				dirPath = ctx.OUTPUT_DIR + "\\" + dirPath;
				new File(dirPath).mkdirs();
			}
			FileOutputStream out = new FileOutputStream(dirPath + "\\"
					+ clazz.getClassName().substring(clazz.getClassName().lastIndexOf(".") + 1) + ".class");
			out.write(clazz.getBytecode());
			out.close();
		}
		for (Version dep : version.getDependencies()) {
			jars.addAll(buildJars(dep));
		}
		return jars;
	}

}
