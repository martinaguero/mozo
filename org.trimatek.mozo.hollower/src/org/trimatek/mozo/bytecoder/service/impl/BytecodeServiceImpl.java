package org.trimatek.mozo.bytecoder.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarInputStream;

import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.bytecoder.utils.JarUtils;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.model.Class;

public class BytecodeServiceImpl implements BytecodeService {

	@Override
	public Version fillClasses(Version version) throws IOException {
		List<String> classes;
		if (version.getData() == null) {
			if (version.getUrl() == null) {
				throw new NullPointerException("POM file URL can not be null.");
			}
			version = JarUtils.loadJar(version);
		}
		classes = JarUtils.listClasses(new JarInputStream(new FileInputStream(
				version.getData())));
		for (String className : classes) {
			version.addClass(new Class(className, version.getSnapshot()));
		}
		return version;
	}

}
