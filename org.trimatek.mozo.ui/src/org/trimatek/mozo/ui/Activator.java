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

//		 testLoadJarProxy();
//		 testLoadBcelDeps();
//		testLoadZkclientDeps();
		testLoadLog4JDeps();

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

	private void testLoadBcelDeps() throws ParityCheckException,
			BytecodeException {

		Version target = new Version();
		target.setArtifactId("bcel");
		target.setVersion("5.2");
		target.setGroupId("org.apache.bcel");
		target.setNamespace("org.apache.bcel");

		List<String> references = new ArrayList<String>();
		// references.add("org.apache.bcel.generic.JSR"); // 289
		// references.add("org.apache.bcel.generic.NamedAndTyped"); //80
		// references.add("org.apache.bcel.generic.DRETURN"); //289
		// references.add("org.apache.bcel.util.ClassPath"); //8
//		 references.add("org.apache.bcel.generic.Type"); //79
		// references.add("org.apache.bcel.classfile.Visitor"); //79
//		 references.add("org.apache.bcel.verifier.Verifier"); // 343
		// references.add("org.apache.bcel.classfile.Signature"); //79
		 references.add("org.apache.bcel.util.ClassStack"); //80

		target = mozoService.fetchDependencies(references, target);

		printResult(target);

	}

	private void printResult(Version version) {
		int count = 0;

		for (Class clazz : version.getClasses()) {
			System.out.println(clazz.getClassName());
			count++;	
		}
		for (Version dep : version.getDependencies()) {
			for (Class dc : dep.getClasses()) {
				System.out.println("\t" + dc.getClassName());
				count++;
			}
		}
		System.out.println("TOTAL DE CLASES RECUPERADAS: " + count);
	}

	private void testLoadZkclientDeps() throws ParityCheckException,
			BytecodeException {

		Version target = new Version();
		target.setArtifactId("zkclient");
		target.setVersion("0.7");
		target.setGroupId("com.101tec");
		target.setNamespace("org.I0Itec.zkclient");

		List<String> references = new ArrayList<String>();
//		references.add("org.I0Itec.zkclient.ZkClient"); // 273
		 references.add("org.I0Itec.zkclient.Gateway"); // 92

		target = mozoService.fetchDependencies(references, target);

		printResult(target);

	}

	private void testLoadLog4JDeps() throws ParityCheckException,
			BytecodeException {

		Version target = new Version();
		target.setArtifactId("log4j");
		target.setVersion("1.2.15");
		target.setGroupId("org.apache.log4j");
		target.setNamespace("org.apache.log4j");

		List<String> references = new ArrayList<String>();
		references.add("org.apache.log4j.Logger"); //56

		target = mozoService.fetchDependencies(references, target);

		printResult(target);

	}

	private void testLoadJarProxy() throws Exception {

		 String path =
//		 "https://repo1.maven.org/maven2/org/apache/bcel/bcel/5.2/bcel-5.2.pom";
		 "https://repo1.maven.org/maven2/com/101tec/zkclient/0.7/zkclient-0.7.pom";
		// String path =
		// "https://repo1.maven.org/maven2/antlr/antlr/2.7.7/antlr-2.7.7.pom";
		Version version = catalogService.buildVersionFromPom(path, 0);

		version = mozoService.loadJarProxy(version);

		FileOutputStream fos = new FileOutputStream("F:\\Temp\\"
				+ version.getArtifactId() + "-" + version.getVersion() + ".jar");
		fos.write(version.getJarProxy());
		fos.close();

		System.out.println("PROXY: " + version.getArtifactId());
	}

}
