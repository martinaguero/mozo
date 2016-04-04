package org.trimatek.mozo.service.impl;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.exception.BytecodeException;
import org.trimatek.mozo.exception.ExternalResourceException;
import org.trimatek.mozo.exception.MozoException;
import org.trimatek.mozo.exception.NullDataException;
import org.trimatek.mozo.navigator.service.NavigatorService;
import org.trimatek.mozo.service.MozoService;

public class MozoServiceImpl implements MozoService {

	private NavigatorService navigatorService;
	private ServiceTracker navigatorServiceTracker;
	private CatalogService catalogService;
	private ServiceTracker catalogServiceTracker;
	private static Logger logger = Logger.getLogger(MozoServiceImpl.class.getName());

	public MozoServiceImpl(BundleContext context) {
		navigatorServiceTracker = new ServiceTracker(context, NavigatorService.class.getName(), null);
		navigatorServiceTracker.open();
		navigatorService = (NavigatorService) navigatorServiceTracker.getService();
		catalogServiceTracker = new ServiceTracker(context, CatalogService.class.getName(), null);
		catalogServiceTracker.open();
		catalogService = (CatalogService) catalogServiceTracker.getService();
	}

	@Override
	public Version loadJarProxy(Version version) throws MozoException {
		String msg = null;
		if (version == null || version.getUrl() == null) {
			throw new NullDataException("MOZO: Version or URL is missing.");
		}
		try {
			logger.log(Level.INFO, "MOZO: Loading version for POM: " + version.getUrl());
			// TODO Evaluar si está en el catálogo antes de crear
			version = catalogService.buildVersion(version, catalogService.getDefaultLevel());
			logger.log(Level.INFO, "MOZO: Loading Jar proxy for : " + version.toString());
			version = navigatorService.loadJarProxy(version);
			version.setJar(null);
			version.setClasses(null);
			version.setDependencies(null);
			version.setProduct(null);
			logger.log(Level.INFO, "MOZO: Jar proxy " + version.toString() + " ready and prepared to be sent");
			return version;
		} catch (IOException ioe) {
			msg = "MOZO: Error while downloading Jar ";
			logger.log(Level.SEVERE, msg + ioe.getMessage(), ioe);
			throw new ExternalResourceException(msg, ioe);
		} catch (ClassNotFoundException ce) {
			msg = "MOZO: Error while processing bytecode";
			logger.log(Level.SEVERE, msg + ce.getMessage(), ce);
			throw new BytecodeException(msg, ce);
		} catch (Exception e) {
			msg = "MOZO: Undetermined error";
			logger.log(Level.SEVERE, msg + e.getMessage(), e);
			throw new MozoException();
		}
	}

	@Override
	public Version fetchDependencies(Set<String> references, Version target) throws MozoException {
		String msg = null;
		try {
			if (references != null && target != null && !references.isEmpty()) {
				logger.log(Level.INFO, "MOZO: Fetching bytecode for: " + target.toString());
				target = navigatorService.fetchDependencies(references, target);
			} else {
				msg = "MOZO: Required data is null";
				logger.log(Level.SEVERE, msg);
				throw new NullDataException();
			}
		} catch (RuntimeException re) {
			logger.log(Level.SEVERE, re.getMessage());
			throw new MozoException(re.getMessage(), re);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new BytecodeException("MOZO: Error while analyzing class bytecode.", e);
		}
		return target;
	}

}
