package org.trimatek.mozo.ui.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.FileTypeEnum;
import org.trimatek.mozo.model.command.UserCommand;

public class ConvertHandler extends AbstractHandler {
	private static Logger logger = Logger.getLogger(ConvertHandler.class.getName());

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IFile file = getSelectedFile();
		String libdir = file.getProject().getLocation().toString() + Config.LIB_DIR;
		IProject project = getCurrentSelectedProject();
		List<String> targets = null;

		try {
			targets = readTargets(file, event);
		} catch (CoreException ce) {
			logger.log(Level.SEVERE, "MOZO: Error while reading dependencies file content: " + file.getName(), ce);
			return null;
		}

		sendCommands(targets, libdir);

		try {
			updateClasspath(project, targets, libdir);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private void sendCommands(List<String> targets, String dir) {
		Job job = new Job("Sending commands to Mozo") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				for (String t : targets) {
					UserCommand command = new UserCommand();
					Version version = new Version(t);
					command.setId("LoadProxy");
					command.setVersion(version);
					command.setTargetDir(dir);
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
					new Thread(client, "client-A").start();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private List<String> readTargets(IFile file, ExecutionEvent event) throws CoreException {
		if (!FileTypeEnum.contains(file.getFileExtension())) {
			MessageDialog.openError(HandlerUtil.getActiveShell(event), "Error", "File extension is not POM or MOZO");
			return null;
		}
		FileTypeEnum type = FileTypeEnum.valueOf(file.getFileExtension());
		return type.getReader().read(file.getContents());
	}

	private IFile getSelectedFile() {
		IFile file = null;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ssel = (IStructuredSelection) selection;
				Object obj = ssel.getFirstElement();
				file = (IFile) Platform.getAdapterManager().getAdapter(obj, IFile.class);
			}
		}
		return file;
	}

	public static IProject getCurrentSelectedProject() {
		IProject project = null;
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IResource) {
				project = ((IResource) element).getProject();
			} else if (element instanceof PackageFragmentRoot) {
				IJavaProject jProject = ((PackageFragmentRoot) element).getJavaProject();
				project = jProject.getProject();
			} else if (element instanceof IJavaElement) {
				IJavaProject jProject = ((IJavaElement) element).getJavaProject();
				project = jProject.getProject();
			}
		}
		return project;
	}

	private void updateClasspath(IProject project, List<String> targets, String libdir) throws JavaModelException {
		Job job = new Job("Updating the classpath") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IJavaProject javaProject = JavaCore.create(project);
				IClasspathEntry[] entries = new IClasspathEntry[targets.size()];
				String jarName;
				int idx = 0;
				for (String target : targets) {
					jarName = target.substring(target.lastIndexOf("/") + 1, target.lastIndexOf(".")) + ".jar";
					Path path = new Path(libdir + jarName);
					entries[idx++] = JavaCore.newLibraryEntry(path, null, null);
				}
				try {
					javaProject.setRawClasspath(entries, monitor);
				} catch (JavaModelException e) {
					Bundle bundle = FrameworkUtil.getBundle(getClass());
					return new Status(Status.ERROR, bundle.getSymbolicName(),
							"MOZO: Could not set classpath to Java project: " + javaProject.getElementName(), e);
				}
				return Status.OK_STATUS;
			}
		};

		job.schedule();
	}

}
