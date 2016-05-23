package org.trimatek.mozo.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.trimatek.mozo.ui.Config;
import org.trimatek.mozo.ui.sockets.*;

public class Activator extends AbstractUIPlugin {

	private static Logger logger = Logger.getLogger(Activator.class.getName());

	public Activator() {
		initServerSocket();
	}

	private void initServerSocket() {
		IProject project = getCurrentSelectedProject();
		Runnable server = new Runnable() {
			@Override
			public void run() {
				try {
					new SocketServer("localhost", Config.SOCKET_UI_PORT, project).startServer(); 
				} catch (IOException ioe) {
					String msg = "MOZO: Error starting UI socket server";
					logger.log(Level.SEVERE, msg + ioe.getMessage(), ioe);
				}
			}
		};
		new Thread(server).start();
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

}
