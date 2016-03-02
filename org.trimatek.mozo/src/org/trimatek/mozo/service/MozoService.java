package org.trimatek.mozo.service;

import java.util.List;
import java.util.Map;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.exception.BytecodeException;
import org.trimatek.mozo.exception.MozoException;

public interface MozoService {

	public List<Version> fill(Map<Version, List<String>> target);

	public Version loadJarProxy(Version target) throws MozoException;

	public Version fetchDependencies(List<String> references, Version target)
			throws BytecodeException, MozoException;

}
