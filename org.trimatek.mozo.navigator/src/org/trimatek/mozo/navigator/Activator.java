package org.trimatek.mozo.navigator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;

public class Activator implements BundleActivator {

	private CatalogService catalogService;
	private BytecodeService bytecodeService;
	private ServiceTracker bytecodeServiceTracker;
	private ServiceTracker catalogServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		bytecodeServiceTracker = new ServiceTracker(context,
				BytecodeService.class.getName(), null);
		catalogServiceTracker = new ServiceTracker(context,
				CatalogService.class.getName(), null);
		bytecodeServiceTracker.open();
		catalogServiceTracker.open();
		bytecodeService = (BytecodeService) bytecodeServiceTracker.getService();
		catalogService = (CatalogService) catalogServiceTracker.getService();

		Version version = catalogService
				.buildVersion(
						"https://repo1.maven.org/maven2/org/apache/bcel/bcel/5.2/bcel-5.2.pom",
						1);

		for (Version v : version.getDependencies()) {
			System.out.println(v.getArtifactId());
		}

		version = bytecodeService.fillClasses(version);

		for (org.trimatek.mozo.catalog.model.Class c : version.getClasses()) {
			System.out.println(c.getClassName());
		}

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
