package org.trimatek.mozo.ui.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.ui.Context;

public interface ClasspathService {
	
	public List<File> buildJars(Version version) throws FileNotFoundException, IOException;

}
