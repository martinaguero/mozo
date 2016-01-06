package org.trimatek.mozo.catalog;

import java.util.Hashtable;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.catalog.service.impl.CatalogServiceImpl;

public class Activator implements BundleActivator {

	private EntityManagerFactory entityManagerFactory;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		// register the service
		entityManagerFactory = Persistence.createEntityManagerFactory("org.trimatek.mozo.catalog.jpa");
		
		context.registerService(CatalogService.class.getName(),
				new CatalogServiceImpl(entityManagerFactory),
				new Hashtable());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		entityManagerFactory.close();
	}

}
