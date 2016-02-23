package org.trimatek.mozo.navigator.service;

import java.io.IOException;
import java.util.List;

import org.trimatek.mozo.catalog.model.Version;

public interface NavigatorService {

	public Version loadJarProxy(Version version) throws IOException, ClassNotFoundException;
	
	public List<Version> fetchDependencies(Version version);
	
}
