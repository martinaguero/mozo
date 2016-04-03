package org.trimatek.mozo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Group;
import org.trimatek.mozo.catalog.model.Product;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.service.MozoService;
import org.trimatek.mozo.service.impl.MozoServiceImpl;

public class ActivatorOld implements BundleActivator, ServiceListener {

	private CatalogService catalogService;
	private BytecodeService bytecodeService;
	private ServiceTracker catalogServiceTracker;
	private ServiceTracker bytecodeServiceTracker;
	private static final String SERVICE_EXPORTED_CONFIGS = "service.exported.configs";
	private static final String DEFAULT_CONFIG = "ecf.generic.server";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> props = createRemoteServiceProperties();
		
		context.registerService(MozoService.class.getName(),
				new MozoServiceImpl(context), new Hashtable());

		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// close the service tracker
		catalogServiceTracker.close();
		catalogServiceTracker = null;
		catalogService = null;
		bytecodeServiceTracker.close();
		bytecodeServiceTracker = null;
		bytecodeService = null;
	}

	public void serviceChanged(ServiceEvent ev) {
		// ServiceReference sr = ev.getServiceReference();
		// switch (ev.getType()) {
		// case ServiceEvent.REGISTERED: {
		// Dictionary dictionary = (Dictionary) fContext.getService(sr);
		// service.registerDictionary(dictionary);
		// }
		// break;
		// case ServiceEvent.UNREGISTERING: {
		// Dictionary dictionary = (Dictionary) fContext.getService(sr);
		// service.unregisterDictionary(dictionary);
		// }
		// break;
		// }
	}

	private void print(Repository repo) {

		System.out.println("REPOSITORIO: " + "ID: " + repo.getId()
				+ " snapshot: " + repo.getSnapshot());
		for (Group m : repo.getGroups()) {
			System.out.println("\tGRUPO:" + "ID: " + m.getId() + " snapshot: "
					+ m.getSnapshot());
			if (m.getProducts() != null) {
				for (Product p : m.getProducts()) {
					System.out.println("\t\tPRODUCTO: " + "ID: " + p.getId()
							+ " snapshot: " + p.getSnapshot());
					if (p.getVersions() != null) {
						for (Version v : p.getVersions()) {
							System.out.println("\t\t\tVERSION: " + "ID: "
									+ v.getId() + " snapshot: "
									+ v.getSnapshot());
							if (v.getDependencies() != null) {
								for (Version d : v.getDependencies()) {
									System.out.println("\t\t\t\tDEPENDENCIA: "
											+ "ID:" + d.getId() + " snapshot: "
											+ d.getSnapshot());
								}
							}
						}
					}
				}
			}
		}

	}

	private void testHollower(BundleContext context)
			throws ClassNotFoundException, FileNotFoundException, IOException {
//		bytecodeServiceTracker = new ServiceTracker(context,
//				BytecodeService.class.getName(), null);
//		bytecodeServiceTracker.open();
//		bytecodeService = (BytecodeService) bytecodeServiceTracker.getService();
//
//		String target = "standard.jar";
//
//		InputStream input = bytecodeService.buildJarProxy(new FileInputStream(
//				"F:\\Temp\\mozo\\originales\\" + target), target);
//
//		byte[] buffer = new byte[input.available()];
//		input.read(buffer);
//
//		File targetFile = new File("f:\\Temp\\mozo\\ahuecados\\" + target);
//		OutputStream outStream = new FileOutputStream(targetFile);
//		outStream.write(buffer);
//		outStream.close();
		System.out.println("GENERADO");
	}

	private Repository testCatalog(BundleContext context) {
		catalogServiceTracker = new ServiceTracker(context,
				CatalogService.class.getName(), null);
		catalogServiceTracker.open();
		catalogService = (CatalogService) catalogServiceTracker.getService();

		long snapshot = 1;

		Product prod1 = new Product("p1", snapshot);

		Version ver1 = new Version("v1", snapshot);
		ver1.setProduct(prod1);
		prod1.addVersion(ver1);

		Class c1 = new Class("org/trimatek/deep/Deep", snapshot);
		c1.setVersion(ver1);
		ver1.addClass(c1);

		Version ver2 = new Version("v2", snapshot);
		ver2.setProduct(prod1);
		prod1.addVersion(ver2);

		ver1.addDependency(ver2);

		Repository repo = new Repository("TCR", snapshot);

		Group g1 = new Group("id2", snapshot);

		g1.addProduct(prod1);
		prod1.setGroup(g1);

		Group g2 = new Group("id3", snapshot);

		g1.setRepository(repo);
		g2.setRepository(repo);

		repo.addGroup(g1);
		repo.addGroup(g2);

		catalogService.save(repo);
		return repo;
	}
	
	private Dictionary<String, Object> createRemoteServiceProperties() {
		Hashtable<String, Object> result = new Hashtable<String, Object>();
		// This property is required by the Remote Services specification
		// (chapter 100 in enterprise specification), and when set results
		// in RSA impl exporting as a remote service
		result.put("service.exported.interfaces", "*");
		// async interfaces is an ECF Remote Services service property
		// that allows any declared asynchronous interfaces
		// to be used by consumers.
		// See https://wiki.eclipse.org/ECF/Asynchronous_Remote_Services
		result.put("ecf.exported.async.interfaces", "*");
		// get system properties
		Properties props = System.getProperties();
		// Get OSGi service.exported.configs property
		String config = props.getProperty(SERVICE_EXPORTED_CONFIGS);
		if (config == null) {
			config = DEFAULT_CONFIG;
			result.put(DEFAULT_CONFIG + ".port", "3288");
			result.put(DEFAULT_CONFIG + ".hostname", "93.188.166.20");
		}
		
		result.put(SERVICE_EXPORTED_CONFIGS, config);
		// add any config properties. config properties start with
		// the config name '.' property
		for (Object k : props.keySet()) {
			if (k instanceof String) {
				String key = (String) k;
				if (key.startsWith(config))
					result.put(key, props.get(key));
			}
		}
		return result;
	}

}
