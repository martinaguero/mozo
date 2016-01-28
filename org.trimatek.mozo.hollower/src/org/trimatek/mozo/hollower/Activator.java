package org.trimatek.mozo.hollower;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.trimatek.mozo.hollower.service.HollowerService;
import org.trimatek.mozo.hollower.service.impl.HollowerServiceImpl;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		context.registerService(HollowerService.class.getName(), new HollowerServiceImpl(), new Hashtable());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
	}

}
