package org.trimatek.mozo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.hollower.service.HollowerService;

public class Activator implements BundleActivator, ServiceListener {

//	private CatalogService catalogService;
//	private ScannerService scannerService;
	private HollowerService hollowerService;
//	private ServiceTracker scannerServiceTracker;
//	private ServiceTracker catalogServiceTracker;
	private ServiceTracker hollowerServiceTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		// fContext = context;

		// // create a tracker and track the service
//		scannerServiceTracker = new ServiceTracker(context, ScannerService.class.getName(), null);
//		catalogServiceTracker = new ServiceTracker(context, CatalogService.class.getName(), null);
		hollowerServiceTracker = new ServiceTracker(context, HollowerService.class.getName(), null);
//		scannerServiceTracker.open();
//		catalogServiceTracker.open();
		hollowerServiceTracker.open();

		// have a service listener to implement the whiteboard pattern
		// fContext.addServiceListener(this, "(objectclass=" +
		// Dictionary.class.getName() + ")");

//		scannerService = (ScannerService) scannerServiceTracker.getService();
//		catalogService = (CatalogService) catalogServiceTracker.getService();
		hollowerService = (HollowerService) hollowerServiceTracker.getService();
		 
		InputStream input = hollowerService.hollow(new FileInputStream("F:\\Temp\\mozo\\deep.jar"),"deep.jar");
		
		byte[] buffer = new byte[input.available()];
	    input.read(buffer);
	 
	    File targetFile = new File("f:\\Temp\\mozo\\cliente\\martin.jar");
	    OutputStream outStream = new FileOutputStream(targetFile);
	    outStream.write(buffer);
		outStream.close();
		
//		Repository repo = new Repository();
//		 repo.setSnapshot(new Long(0));
//		 catalogService.save(repo);

//		Repository repo = catalogService.loadRepository(new Long(1), new Long(0));
//		System.out.println("ID " + repo.getId() + " SNAPSHOT: " + repo.getSnapshot());

//		RepoEntity ac = scannerService.scan(repo, "https://repo1.maven.org/maven2", catalogService);

		// RepoEntity entity =
		// scannerService.scan(repo,"https://repo1.maven.org/maven2",
		// catalogService);
		//
		// System.out.println(entity.getId());

		// if (Manufacturer.class.isInstance(entity)) {
		// repo.addManufacturer((Manufacturer) entity);
		// ((Manufacturer) entity).setRepository(repo);
		// print(repo);
		// catalogService.save(repo);
		// } else if (Repository.class.isInstance(entity)) {
		// print((Repository) entity);
		// catalogService.save((Repository) entity);
		// }

		// System.out.println("ID " + repo.getId());

		// Repository r = catalogService.loadRepository(new Long(16));
		// System.out.println("ID " + r.getArtifactId());
		// scannerService.scan(r,"xx",catalogService);

		// RepoEntity ac =
		// scannerService.scan("https://repo1.maven.org/maven2/activecluster",
		// snapshot);
		// if (Manufacturer.class.isInstance(ac)) {
		// repo.addManufacturer((Manufacturer) ac);
		// ((Manufacturer) ac).setRepository(repo);
		// print(repo);
		// catalogService.saveOrUpdate(repo);
		// }
		// save(catalogService);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// close the service tracker
//		scannerServiceTracker.close();
//		scannerServiceTracker = null;
//		scannerService = null;
//		catalogServiceTracker.close();
//		catalogServiceTracker = null;
//		catalogService = null;
		hollowerServiceTracker.close();
		hollowerServiceTracker = null;
		hollowerService = null;
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

//	private void print(Repository repo) {
//
//		System.out.println("REPOSITORIO: " + "ID: " + repo.getId() + " snapshot: " + repo.getSnapshot());
//		for (Manufacturer m : repo.getManufacturers()) {
//			System.out.println("\tMANUFACTURER:" + "ID: " + m.getId() + " snapshot: " + m.getSnapshot());
//			if (m.getProducts() != null) {
//				for (Product p : m.getProducts()) {
//					System.out.println("\t\tPRODUCTO: " + "ID: " + p.getId() + " snapshot: " + p.getSnapshot());
//					if (p.getVersions() != null) {
//						for (Version v : p.getVersions()) {
//							System.out
//									.println("\t\t\tVERSION: " + "ID: " + v.getId() + " snapshot: " + v.getSnapshot());
//							if (v.getDependencies() != null) {
//								for (Version d : v.getDependencies()) {
//									System.out.println("\t\t\t\tDEPENDENCIA: " + "ID:" + d.getId() + " snapshot: "
//											+ d.getSnapshot());
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//
//	}
//
//	public void save(CatalogService catalogService) {
//
//		long snapshot = 25;
//
//		Product prod1 = new Product("p1", snapshot);
//
//		Version ver1 = new Version("v1", snapshot);
//		ver1.setProduct(prod1);
//		prod1.addVersion(ver1);
//
//		Version ver2 = new Version("v2", snapshot);
//		ver2.setProduct(prod1);
//		prod1.addVersion(ver2);
//
//		ver1.addDependency(ver2);
//
//		Repository repo = new Repository("TCR", snapshot);
//
//		Manufacturer manu1 = new Manufacturer("id2", snapshot);
//
//		manu1.addProduct(prod1);
//		prod1.setManufacturer(manu1);
//
//		Manufacturer manu2 = new Manufacturer("id3", snapshot);
//
//		manu1.setRepository(repo);
//		manu2.setRepository(repo);
//
//		repo.addManufacturer(manu1);
//		repo.addManufacturer(manu2);
//
//		catalogService.save(repo);
//
//	}

}
