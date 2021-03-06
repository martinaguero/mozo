package org.trimatek.mozo.bytecoder.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.bcel.classfile.ClassFormatException;
import org.trimatek.mozo.catalog.model.Version;

public interface BytecodeService {

	public Version fillClasses(Version version) throws IOException;

	public Version loadJar(Version version) throws IOException;

	public Version buildJarProxy(Version version) throws IOException, ClassNotFoundException;

	public List<String> listReferences(String className, byte[] bytecode, String groupId)
			throws ClassFormatException, IOException;

	public byte[] toByteArray(InputStream inputStream) throws IOException;

}
