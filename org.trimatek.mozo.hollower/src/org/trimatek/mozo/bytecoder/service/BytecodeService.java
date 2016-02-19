package org.trimatek.mozo.bytecoder.service;

import java.io.IOException;
import java.io.InputStream;

import org.trimatek.mozo.catalog.model.Version;

public interface BytecodeService {
	
	public Version fillClasses(Version version) throws IOException;
	
	public Version loadJar(Version version) throws IOException;

	public Version buildJarProxy(Version version) throws IOException,
			ClassNotFoundException;

}
