package org.trimatek.mozo.service;

import java.util.Set;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.exception.BytecodeException;
import org.trimatek.mozo.exception.MozoException;

public interface MozoService {

	public Version loadJarProxy(Version target) throws MozoException;

	public Version fetchDependencies(Set<String> references, Version target)
			throws BytecodeException, MozoException;

}
