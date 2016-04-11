package org.trimatek.mozo.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.trimatek.mozo.Context;
import org.trimatek.mozo.catalog.model.Version;

public interface ClasspathService {
	
	public List<File> buildJars(Version version) throws FileNotFoundException, IOException;

}
