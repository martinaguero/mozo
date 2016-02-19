package org.trimatek.mozo.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.exception.ExternalResourceException;
import org.trimatek.mozo.exception.MozoException;
import org.trimatek.mozo.exception.NullDataException;
import org.trimatek.mozo.service.MozoService;

public class MozoServiceImpl implements MozoService {

	private BundleContext context;
	private CatalogService catalogService;
	private ServiceTracker catalogServiceTracker;

	public MozoServiceImpl(BundleContext context) {
		this.context = context;
		catalogServiceTracker = new ServiceTracker(context,
				CatalogService.class.getName(), null);
		catalogServiceTracker.open();
		catalogService = (CatalogService) catalogServiceTracker.getService();
	}

	@Override
	public List<Version> fill(Map<Version, List<String>> target) {
		System.out.println("FILL");
		return null;
	}

	@Override
	public Version loadJarProxy(Version target) throws MozoException {
		if (target.getUrl() == null) {
			throw new NullDataException("MOZO: POM file URL address is null.");
		}
		try {
			target = catalogService.loadVersion(target.getUrl(), 1);
		} catch (IOException e) {
			throw new ExternalResourceException(
					"MOZO: External resource access error.", e);
		} catch (Exception e){
			throw new MozoException(
					"MOZO: " + e.getMessage(), e);
		}
		return target;
	}

}
