package org.trimatek.mozo.bytecoder.service;

import java.io.IOException;

import org.trimatek.mozo.catalog.model.Version;

public interface BytecodeService {

	public Version fillClasses(Version version) throws IOException;

}
