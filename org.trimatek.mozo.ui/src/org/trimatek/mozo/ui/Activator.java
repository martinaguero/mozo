package org.trimatek.mozo.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.service.MozoService;

public class Activator implements BundleActivator {

	private MozoService mozoService;
	private ServiceTracker mozoServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		mozoServiceTracker = new ServiceTracker(context,
				MozoService.class.getName(), null);
		mozoServiceTracker.open();
		mozoService = (MozoService) mozoServiceTracker.getService();
		
		Version proxy = new Version();
		proxy.setUrl("https://repo1.maven.org/maven2/org/apache/bcel/bcel/5.2/bcel-5.2.pom");
		proxy = mozoService.loadJarProxy(proxy);
		
		System.out.println(proxy.getArtifactId());
		
		//mozoService.fill(null);
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
