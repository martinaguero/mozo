package org.trimatek.mozo.ui.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.FileTypeEnum;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.model.exception.NullDataException;
import org.trimatek.mozo.ui.Context;
import org.trimatek.mozo.ui.sockets.SocketClient;
import org.trimatek.mozo.ui.tools.EclipseTools;

public class GetBytecodeHandler extends AbstractHandler {

	private static Logger logger = Logger.getLogger(GetBytecodeHandler.class.getName());
	private Context ctx;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ctx = EclipseTools.setupContext(event);
			Map<String, Set<String>> targets = loadTargets(ctx.getJavaProject());
			if (targets != null) {
				sendCommands(translate(targets), ctx.getLibPath());
			}
		} catch (CoreException ce) {
			logger.log(Level.SEVERE, "MOZO -> Error while analyzing source code imports", ce);
		} catch (NullDataException nde) {
			logger.log(Level.SEVERE, "MOZO -> Could not find dependencies file", nde);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "MOZO -> Class loading error", e);
		}
		return null;
	}

	private void sendCommands(Map<Version, Set<String>> targets, String libPath) {
		Job job = new Job("Sending commands to Mozo") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				for (Map.Entry<Version, Set<String>> entry : targets.entrySet()) {
					UserCommand command = new UserCommand();
					command.setId("LoadBytecode");
					command.setVersion(addNamespace(entry.getKey()));
					command.setTargetDir(libPath);
					command.setReferences(entry.getValue());
					Runnable client = new Runnable() {
						@Override
						public void run() {
							try {
								new SocketClient(command).startClient();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					};
					new Thread(client, "UI-command").start();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private Map<String, Set<String>> loadTargets(IJavaProject javaProject)
			throws JavaModelException, ClassNotFoundException, URISyntaxException, IOException {
		Map<String, Set<String>> targets = new HashMap<String, Set<String>>();
		ClassLoader cl = new URLClassLoader(loadAvailableJars(ctx.getLibPath()));
		for (IPackageFragment mypackage : javaProject.getPackageFragments()) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					for (IImportDeclaration decla : unit.getImports()) {
						Class cls = cl.loadClass(decla.getElementName());
						ProtectionDomain pd = cls.getProtectionDomain();
						if (pd.getCodeSource() != null) {
							String source = pd.getCodeSource().getLocation().toURI().getPath();
							source = source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf("."));
							Set<String> references = targets.get(source);
							if (references != null) {
								references.add(decla.getElementName());
								targets.put(source, references);
							} else {
								Set<String> refs = new HashSet<String>();
								refs.add(decla.getElementName());
								targets.put(source, refs);
							}
						}
					}
				}
			}
		}
		((URLClassLoader)cl).close();
		return targets;
	}

	private URL[] loadAvailableJars(String libPath) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		for (String file : new File(libPath).list()) {
			if (file.endsWith(".jar")) {
				urls.add(new File(libPath + file).toURI().toURL());
			}
		}
		return urls.toArray(new URL[urls.size()]);
	}

	private Map<Version, Set<String>> translate(Map<String, Set<String>> targets) throws CoreException {
		Map<Version, Set<String>> translated = new HashMap<Version, Set<String>>();
		FileTypeEnum type = FileTypeEnum.valueOf(ctx.getDepsFile().getFileExtension());
		List<Version> deps = type.getReader().translate(ctx.getDepsFile().getContents());
		for (Map.Entry<String, Set<String>> entry : targets.entrySet()) {
			for (Version version : deps) {
				if (entry.getKey().equals(version.toString())) {
					translated.put(version, entry.getValue());
				}
			}
		}
		return translated;
	}

	private Version addNamespace(Version version) {
		try {
			JarInputStream jarStream = new JarInputStream(new FileInputStream(ctx.getLibPath() + version + ".jar"));
			version.setNamespace(jarStream.getManifest().getMainAttributes().getValue("Namespace"));
			jarStream.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "MOZO -> Could not open manifest file");
		}
		return version;
	}

}