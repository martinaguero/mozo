package org.trimatek.mozo.navigator.service;

import java.io.IOException;
import java.util.Set;

import org.trimatek.mozo.catalog.model.Version;

public interface NavigatorService {

	public Version loadJarProxy(Version version) throws IOException, ClassNotFoundException;
	
	public Version fetchDependencies(Set<String> references, Version version) throws Exception;
	
}
