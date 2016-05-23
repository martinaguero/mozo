package org.trimatek.mozo.model.service;

import java.util.Set;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.exception.BytecodeException;
import org.trimatek.mozo.model.exception.MozoException;

public interface DispatcherService extends Service {

	public Version loadJarProxy(Version target) throws MozoException;

	public Version fetchDependencies(Set<String> references, Version target)
			throws BytecodeException, MozoException;

}
