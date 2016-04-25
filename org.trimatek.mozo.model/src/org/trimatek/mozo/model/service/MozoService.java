package org.trimatek.mozo.model.service;

import org.trimatek.mozo.catalog.model.Version;

public interface MozoService {

	public Version loadJarProxy(Version version);
	
	public void setDispatcherService(DispatcherService dispatcherService);
	
	public void unsetDispatcherService(DispatcherService dispatcherService);
	
}
