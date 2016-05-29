package org.trimatek.mozo.model;

import java.io.InputStream;
import java.util.List;

import org.trimatek.mozo.catalog.model.Version;

public interface DependenciesReader {
	
	public List<String> read(InputStream inputStream);
	
	public List<Version> translate(InputStream inputStream);

}
