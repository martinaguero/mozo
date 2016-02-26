package org.trimatek.mozo.ui;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.exception.BytecodeException;
import org.trimatek.mozo.exception.ParityCheckException;
import org.trimatek.mozo.service.MozoService;

public class Activator implements BundleActivator {

	private MozoService mozoService;
	private ServiceTracker mozoServiceTracker;
	// provisorio
	private CatalogService catalogService;
	private ServiceTracker catalogServiceTracker;

	// hasta acá provisiorio

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
		// hasta acá provisiorio

//		testLoadJarProxy();
		testLoadDependencies();

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

	private void testLoadDependencies() throws ParityCheckException,
			BytecodeException {

		Version target = new Version();
		 target.setArtifactId("bcel");
		 target.setVersion("5.2");
		 target.setGroupId("org.apache.bcel");
//		target.setArtifactId("antlr");
//		target.setGroupId("antlr");
//		target.setVersion("2.7.7");

		List<String> references = new ArrayList<String>();
//		references.add("org.apache.bcel.generic.JSR"); // 289
		// references.add("org.apache.bcel.generic.NamedAndTyped"); //80
		// references.add("org.apache.bcel.generic.DRETURN"); //289
		// references.add("org.apache.bcel.util.ClassPath"); //8
		// references.add("org.apache.bcel.generic.Type"); //79
		// references.add("org.apache.bcel.classfile.Visitor"); //79
		// references.add("org.apache.bcel.verifier.Verifier"); //343
		// references.add("org.apache.bcel.classfile.Signature"); //79

//		 references.add("antlr.ActionTransInfo"); //1s
		 references.add("antlr.actions.cpp.ActionLexer"); //falló porque no cargó
		 // en el catálogo la clase antlr.Tool (lo que está dentro del primer directorio)

		target = mozoService.fetchDependencies(references, target);

		for (Class clazz : target.getClasses()) {
			System.out.println(clazz.getClassName());
		}

		System.out.println("TOTAL DE CLASES RECUPERADAS: "
				+ target.getClasses().size());

	}

	private void testLoadJarProxy() throws Exception {

		//String path = "https://repo1.maven.org/maven2/org/apache/bcel/bcel/5.2/bcel-5.2.pom";
		String path = "https://repo1.maven.org/maven2/antlr/antlr/2.7.7/antlr-2.7.7.pom";
		Version version = catalogService.buildVersionFromPom(path, 0);

		version = mozoService.loadJarProxy(version);

		FileOutputStream fos = new FileOutputStream("f:\\Temp\\"
				+ version.getArtifactId() + "-" + version.getVersion() + ".jar");
		fos.write(version.getJarProxy());
		fos.close();

		System.out.println("PROXY: " + version.getArtifactId());
	}

}
