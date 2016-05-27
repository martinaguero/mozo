package org.trimatek.mozo.ui.handlers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.trimatek.mozo.ui.Config;
import org.trimatek.mozo.ui.tools.EclipseTools;

public class SampleHandler extends AbstractHandler {

	private String libPath;
	private IJavaProject javaProject;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			setupContext();
			print(javaProject);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;
	}

	private void setupContext() throws CoreException {
		IProject project = (IProject) EclipseTools.getSelectedResource();
		libPath = project.getLocation() + Config.LIB_DIR;
		if (project.hasNature(JavaCore.NATURE_ID)) {
			javaProject = JavaCore.create(project);
		}
	}

	private void print(IJavaProject javaProject) {
		try {
			IPackageFragment[] packages = javaProject.getPackageFragments();
			for (IPackageFragment mypackage : packages) {
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {

					for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
						IImportDeclaration[] imports = unit.getImports();
						for (IImportDeclaration decla : imports) {

							URL[] urls = null;
							try {
								// Convert the file object to a URL

								URL url = new File(
										"D:\\Dropbox\\TESIS\\runtime-EclipseApplication\\Ejemplo\\lib\\commons-pool-1.5.4.jar")
												.toURI().toURL();
								URL url2 = new File(
										"D:\\Dropbox\\TESIS\\runtime-EclipseApplication\\Ejemplo\\lib\\commons-logging-1.1.1.jar")
												.toURI().toURL();

								urls = new URL[] { url, url2 };
							} catch (MalformedURLException e) {
							}

							ClassLoader cl = new URLClassLoader(urls);
							Class cls = cl.loadClass(decla.getElementName());

//							URL location = cls.getResource('/' + cls.getName().replace('.', '/') + ".class");
//
//							System.out.println(location);

							ProtectionDomain pd = cls.getProtectionDomain();

							if (pd.getCodeSource() != null) {

								System.out.println(pd.getCodeSource().getLocation().toURI().getPath());
							}

							// System.out.println(new File(
							// .getCodeSource().getLocation().toURI().getPath()));

							/*
							 * ProtectionDomain prDom =
							 * clazz.getProtectionDomain(); CodeSource
							 * codeSource = prDom.getCodeSource(); String path =
							 * codeSource.getLocation().getPath();
							 * System.out.println(URLDecoder.decode(path,
							 * "UTF-8"));
							 */

						}

					}

				}

			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}