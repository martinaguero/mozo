package org.trimatek.mozo.navigator.service;

import java.io.IOException;

import org.trimatek.mozo.catalog.model.Version;

public interface NavigatorService {

	public Version loadJarProxy(Version version) throws IOException;
	
}
