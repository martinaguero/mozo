package org.trimatek.mozo.ui;

import java.io.FileOutputStream;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.service.MozoService;

public class Activator implements BundleActivator {

	private MozoService mozoService;
	private ServiceTracker mozoServiceTracker;
	// provisorio
	private CatalogService catalogService;
	private ServiceTracker catalogServiceTracker;

	// hasta ac� provisiorio

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
		// provisorio
		catalogServiceTracker = new ServiceTracker(context,
				CatalogService.class.getName(), null);
		catalogServiceTracker.open();
		catalogService = (CatalogService) catalogServiceTracker.getService();
		// hasta ac� provisiorio

		testLoadJarProxy();
		//testLoadDependencies();


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
	
	private void testLoadDependencies(){
		
		Version target = new Version();
		Class c = new Class();
		c.setArtifactId("bcel-5.2");
		c.setClassName("org.apache.bcel.generic.JSR");
		target.addClass(c);
		mozoService.fetchDependencies(target);
		
	}

	private void testLoadJarProxy() throws Exception {

		String path = "https://repo1.maven.org/maven2/org/apache/bcel/bcel/5.2/bcel-5.2.pom";
		Version version = catalogService.buildVersionFromPom(path, 0);

		version = mozoService.loadJarProxy(version);

		FileOutputStream fos = new FileOutputStream("f:\\Temp\\"
				+ version.getArtifactId() + ".jar");
		fos.write(version.getJarProxy());
		fos.close();

		System.out.println("PROXY: " + version.getArtifactId());
	}

}
