package org.trimatek.mozo.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.exception.BytecodeException;
import org.trimatek.mozo.exception.ExternalResourceException;
import org.trimatek.mozo.exception.MozoException;
import org.trimatek.mozo.exception.NullDataException;
import org.trimatek.mozo.navigator.service.NavigatorService;
import org.trimatek.mozo.service.MozoService;

public class MozoServiceImpl implements MozoService {

	private NavigatorService navigatorService;
	private ServiceTracker navigatorServiceTracker;

	public MozoServiceImpl(BundleContext context) {
		navigatorServiceTracker = new ServiceTracker(context,
				NavigatorService.class.getName(), null);
		navigatorServiceTracker.open();
		navigatorService = (NavigatorService) navigatorServiceTracker
				.getService();
	}

	@Override
	public List<Version> fill(Map<Version, List<String>> target) {
		System.out.println("FILL");
		return null;
	}

	@Override
	public Version loadJarProxy(Version version) throws MozoException {
		if (version.getArtifactId() == null || version.getVersion() == null) {
			throw new NullDataException(
					"MOZO: ArtifactId or version is missing.");
		}
		try {
			version = navigatorService.loadJarProxy(version);
			version.setJar(null);
			return version;
		} catch (IOException e) {
			throw new ExternalResourceException(
					"MOZO: External resource exception", e);
		} catch (ClassNotFoundException e) {
			throw new BytecodeException(
					"MOZO: Error while processing bytecode", e);
		}
	}

	@Override
	public Version fetchDependencies(Set<String> references, Version target)
			throws MozoException {
		try {
			if (references != null && target != null && !references.isEmpty()) {
				target = navigatorService.fetchDependencies(references, target);
			} else {
				throw new NullDataException("MOZO: Required data is null.");
			}
		} catch (RuntimeException re) {
			throw new MozoException(re.getMessage(), re);
		} catch (Exception e) {
			throw new BytecodeException(
					"MOZO: Error while analyzing class bytecode.", e);
		}
		return target;
	}
}
