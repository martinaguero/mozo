package org.trimatek.mozo.ui.handlers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.trimatek.mozo.model.exception.NullDataException;
import org.trimatek.mozo.ui.Context;
import org.trimatek.mozo.ui.tools.EclipseTools;

public class SampleHandler extends AbstractHandler {

	private Context ctx;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ctx = EclipseTools.setupContext(event);
			print(ctx.getJavaProject());
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NullDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
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

							// URL location = cls.getResource('/' +
							// cls.getName().replace('.', '/') + ".class");
							//
							// System.out.println(location);

							ProtectionDomain pd = cls.getProtectionDomain();

							if (pd.getCodeSource() != null) {
								System.out.println("Clase: " + decla.getElementName());
								System.out.println("Origen: " + pd.getCodeSource().getLocation().toURI().getPath());
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