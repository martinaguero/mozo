package org.trimatek.mozo.model;

import java.io.InputStream;
import java.util.List;

public interface DependenciesReader {
	
	public List<String> read(InputStream inputStream);

}
