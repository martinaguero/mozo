package org.trimatek.mozo.navigator;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.navigator.service.NavigatorService;
import org.trimatek.mozo.navigator.service.impl.NavigatorServiceImpl;
import org.trimatek.remotezip.service.RemoteZipService;

public class Activator implements BundleActivator {

	private CatalogService catalogService;
	private BytecodeService bytecodeService;
	private RemoteZipService remoteZipService;
	private ServiceTracker bytecodeServiceTracker;
	private ServiceTracker catalogServiceTracker;
	private ServiceTracker remoteZipServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		bytecodeServiceTracker = new ServiceTracker(context, BytecodeService.class.getName(), null);
		catalogServiceTracker = new ServiceTracker(context, CatalogService.class.getName(), null);
		remoteZipServiceTracker = new ServiceTracker(context, RemoteZipService.class.getName(), null);
		bytecodeServiceTracker.open();
		catalogServiceTracker.open();
		remoteZipServiceTracker.open();
		bytecodeService = (BytecodeService) bytecodeServiceTracker.getService();
		catalogService = (CatalogService) catalogServiceTracker.getService();
		remoteZipService = (RemoteZipService) remoteZipServiceTracker.getService();
		context.registerService(NavigatorService.class.getName(),
				new NavigatorServiceImpl(catalogService, bytecodeService, remoteZipService), new Hashtable());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye World!!");
	}

}
