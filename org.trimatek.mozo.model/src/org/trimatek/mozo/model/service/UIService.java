package org.trimatek.mozo.model.service;

import java.util.Set;

public interface UIService extends Service {

	public void updateClasspath(String jarName);

	public void updateClasspath(Set<String> jars);

}
