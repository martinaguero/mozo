package org.trimatek.mozo;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;
import org.trimatek.mozo.service.MozoService;
import org.trimatek.mozo.service.impl.MozoServiceImpl;

public class Activator implements BundleActivator, ServiceListener {

	private static final String SERVICE_EXPORTED_CONFIGS = "service.exported.configs";
	private static final String DEFAULT_CONFIG = "ecf.generic.server";
	private ServiceRegistration<MozoService> mozoServiceRegistration;

	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> props = createRemoteServiceProperties();
		context.registerService(MozoService.class.getName(), new MozoServiceImpl(context), props);
	}

	public void stop(BundleContext context) throws Exception {
		if (mozoServiceRegistration != null) {
			mozoServiceRegistration.unregister();
			mozoServiceRegistration = null;
		}
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
