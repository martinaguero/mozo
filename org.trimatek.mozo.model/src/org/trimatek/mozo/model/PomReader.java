package org.trimatek.mozo.model;

import java.io.InputStream;
import java.util.List;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.utils.MavenUtils;

public class PomReader implements DependenciesReader {

	@Override
	public List<String> read(InputStream inputStream) {
		return MavenUtils.readPomDependencies(inputStream);
	}

	@Override
	public List<Version> translate(InputStream inputStream) {
		return MavenUtils.translatePomDependencies(inputStream);
	}

}
