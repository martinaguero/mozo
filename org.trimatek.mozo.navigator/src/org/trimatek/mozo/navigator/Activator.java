package org.trimatek.mozo.navigator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.scanner.service.ScannerService;

public class Activator implements BundleActivator {

	private CatalogService catalogService;
	private ScannerService scannerService;
	private ServiceTracker scannerServiceTracker;
	private ServiceTracker catalogServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		scannerServiceTracker = new ServiceTracker(context,
				ScannerService.class.getName(), null);
		scannerServiceTracker.open();
		scannerService = (ScannerService) scannerServiceTracker.getService();

		Version version = scannerService
				.buildVersion(
						"https://repo1.maven.org/maven2/org/apache/bcel/bcel/5.2/bcel-5.2.pom",
						1);

		for (Version v : version.getDependencies()) {
			System.out.println(v.getArtifactId());
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
