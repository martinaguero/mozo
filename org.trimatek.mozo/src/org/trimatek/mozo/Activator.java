package org.trimatek.mozo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.MozoService;
import org.trimatek.mozo.service.ClasspathService;
import org.trimatek.mozo.service.impl.ClasspathServiceImpl;
import org.trimatek.mozo.tools.JarUtils;

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

//		 testLoadJarProxy();
//		 testLoadBcelDeps();
//		testLoadZkclientDeps();
//		testLoadLog4JDeps();
//		 testLoadMirageDeps();
		 testLoadCommonsDBCP(); 
//		testLoadCommonsPool();
//		testLoadLog4j();

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

	private void testLoadBcelDeps() throws MozoException {

		Version target = new Version();
		target.setArtifactId("bcel");
		target.setVersion("5.2");
		target.setGroupId("org.apache.bcel");
		target.setNamespace("org.apache.bcel");

		Set<String> references = new HashSet<String>();
//		 references.add("org.apache.bcel.generic.JSR"); // 289
//		 references.add("org.apache.bcel.generic.NamedAndTyped"); //80
		 references.add("org.apache.bcel.generic.DRETURN"); //289
//		 references.add("org.apache.bcel.util.ClassPath"); //8
//		 references.add("org.apache.bcel.generic.Type"); //79
//		 references.add("org.apache.bcel.classfile.Visitor"); //79
//		 references.add("org.apache.bcel.verifier.Verifier"); // 343
//		 references.add("org.apache.bcel.classfile.Signature"); //79
//		 references.add("org.apache.bcel.util.ClassStack"); //80

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

	private void testLoadZkclientDeps() throws MozoException, FileNotFoundException, IOException {

		Version target = new Version();
		target.setArtifactId("zkclient");
		target.setVersion("0.7");
		target.setGroupId("com.101tec");
		target.setNamespace("org.I0Itec.zkclient");

		Set<String> references = new HashSet<String>();
		references.add("org.I0Itec.zkclient.ZkClient"); // 273
//		 references.add("org.I0Itec.zkclient.Gateway"); // 92

		target = mozoService.fetchDependencies(references, target);

		printResult(target);
		
		ClasspathService cpService = new ClasspathServiceImpl();
		List<File> files = cpService.buildJars(target);
		
		for (File file : files) {
			Context ctx = new Context(file.getName());
			JarUtils.buildJar(ctx);
		}
		
		
	}

	private void testLoadLog4JDeps() throws MozoException {

		Version target = new Version();
		target.setArtifactId("log4j");
		target.setVersion("1.2.15");
		target.setGroupId("org.apache.log4j");
		target.setNamespace("org.apache.log4j");

		Set<String> references = new HashSet<String>();
		references.add("org.apache.log4j.Logger"); //56

		target = mozoService.fetchDependencies(references, target);

		printResult(target);

	}

	private void testLoadMirageDeps() throws MozoException {

		Version target = new Version();
		target.setArtifactId("mirage");
		target.setVersion("1.2.3");
		target.setGroupId("jp.sf.amateras");
		target.setNamespace("jp.sf.amateras.mirage");

		Set<String> references = new HashSet<String>();
		references.add("jp.sf.amateras.mirage.parser.Node"); //2
		references.add("jp.sf.amateras.mirage.CallExecutor"); //38

		target = mozoService.fetchDependencies(references, target);

		printResult(target);

	}
	
	private void testLoadCommonsDBCP() throws MozoException, FileNotFoundException, IOException {

		Version target = new Version();
		target.setArtifactId("commons-dbcp");
		target.setVersion("1.4");
		target.setGroupId("commons-dbcp");
		target.setNamespace("org.apache.commons.dbcp");

		Set<String> references = new HashSet<String>();
//		references.add("org.apache.commons.dbcp.PoolingDriver"); //25
//		references.add("org.apache.commons.dbcp.ConnectionFactory"); //25
//		references.add("org.apache.commons.dbcp.DelegatingCallableStatement");
		references.add("org.apache.commons.dbcp.managed.ManagedConnection"); //29 dbcp,transaction,pool

		target = mozoService.fetchDependencies(references, target);

		printResult(target);
		
		ClasspathService cpService = new ClasspathServiceImpl();
		List<File> files = cpService.buildJars(target);
		
		for (File file : files) {
			Context ctx = new Context(file.getName());
			JarUtils.buildJar(ctx);
		}

	}
	
	private void testLoadCommonsPool() throws MozoException, FileNotFoundException, IOException {

		Version target = new Version();
		target.setArtifactId("commons-pool");
		target.setVersion("1.5.4");
		target.setGroupId("commons-pool");
		target.setNamespace("org.apache.commons.pool");

		Set<String> references = new HashSet<String>();
		references.add("org.apache.commons.pool.ObjectPool"); //29 dbcp,transaction,pool

		target = mozoService.fetchDependencies(references, target);

		printResult(target);
		
		ClasspathService cpService = new ClasspathServiceImpl();
		List<File> files = cpService.buildJars(target);
		
		for (File file : files) {
			Context ctx = new Context(file.getName());
			JarUtils.buildJar(ctx);
		}

	}
	
	
	
	private void testLoadLog4j() throws MozoException, FileNotFoundException, IOException {

		Version target = new Version();
		target.setArtifactId("log4j");
		target.setVersion("1.2.14");
		target.setGroupId("log4j");
		target.setNamespace("org.apache.log4j");

		Set<String> references = new HashSet<String>();
		references.add("org.apache.log4j.Logger"); //
		references.add("org.apache.log4j.RollingFileAppender");
		references.add("org.apache.log4j.PatternLayout");

		target = mozoService.fetchDependencies(references, target);

		printResult(target);
		
		ClasspathService cpService = new ClasspathServiceImpl();
		List<File> files = cpService.buildJars(target);
		
		for (File file : files) {
			Context ctx = new Context(file.getName());
			JarUtils.buildJar(ctx);
		}

	}
	

	private void testLoadJarProxy() throws Exception {

		 String path =
//		 "https://repo1.maven.org/maven2/org/apache/bcel/bcel/5.2/bcel-5.2.pom";
//		 "https://repo1.maven.org/maven2/com/101tec/zkclient/0.7/zkclient-0.7.pom";
//		 "https://repo1.maven.org/maven2/antlr/antlr/2.7.7/antlr-2.7.7.pom";
//		 "https://repo1.maven.org/maven2/jakarta-regexp/jakarta-regexp/1.4/jakarta-regexp-1.4.pom";
//		 "https://repo1.maven.org/maven2/log4j/log4j/1.2.15/log4j-1.2.15.pom";
//		 "https://repo1.maven.org/maven2/jp/sf/amateras/mirage/1.2.3/mirage-1.2.3.pom";
				 "https://repo1.maven.org/maven2/commons-dbcp/commons-dbcp/1.4/commons-dbcp-1.4.pom";
//		 "https://repo1.maven.org/maven2/commons-pool/commons-pool/1.5.4/commons-pool-1.5.4.pom";
//		 "https://repo1.maven.org/maven2/log4j/log4j/1.2.14/log4j-1.2.14.pom";
		Version version = new Version(path);
		
		version = mozoService.loadJarProxy(version);

		FileOutputStream fos = new FileOutputStream("D:\\Temp\\"
				+ version.getArtifactId() + "-" + version.getVersion() + ".jar");
		fos.write(version.getJarProxy());
		fos.close();

		System.out.println("PROXY: " + version.getArtifactId());
	}

}
