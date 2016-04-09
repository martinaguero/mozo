/*******************************************************************************
 * Copyright (c) 2013 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.trimatek.mozo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.exception.BytecodeException;
import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.MozoService;
import org.trimatek.mozo.ui.service.ClasspathService;
import org.trimatek.mozo.ui.service.impl.ClasspathServiceImpl;
import org.trimatek.mozo.ui.tools.JarUtils;

public class MozoServiceComponent {

	// Called by DS upon ITimeService discovery
	void loadJarProxy(MozoService mozoService) {
		System.out.println("Discovered ITimeService via DS.  Instance=" + mozoService);
		// Call the service and print out result!
		try {
			Version version = new Version(
					"https://repo1.maven.org/maven2/org/apache/bcel/bcel/5.2/bcel-5.2.pom");
			version = mozoService.loadJarProxy(version);
			FileOutputStream fos = new FileOutputStream(
					"D:\\Temp\\" + version.getArtifactId() + "-" + version.getVersion() + ".jar");
			fos.write(version.getJarProxy());
			fos.close();
			System.out.println("PROXY: " + version.getArtifactId());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MozoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	void fetchDependencies(MozoService mozoService) throws FileNotFoundException, IOException, BytecodeException, MozoException{
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
	

	// Called by DS upon ITimeService undiscovery
	void unbindTimeService(MozoService mozoService) {
		System.out.println("Undiscovered ITimeService via DS.  Instance=" + mozoService);
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
}
