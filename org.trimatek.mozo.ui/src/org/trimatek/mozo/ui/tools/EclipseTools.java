package org.trimatek.mozo.ui.tools;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.trimatek.mozo.model.exception.NullDataException;
import org.trimatek.mozo.ui.Config;
import org.trimatek.mozo.ui.Context;

public class EclipseTools {

	public static IResource getSelectedResource() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ssel = (IStructuredSelection) selection;
				Object obj = ssel.getFirstElement();
				if (IJavaProject.class.isInstance(obj)) {
					return (IProject) Platform.getAdapterManager().getAdapter(obj, IProject.class);
				}
				return (IFile) Platform.getAdapterManager().getAdapter(obj, IFile.class);
			}
		}
		return null;
	}
	
	public static Context setupContext(ExecutionEvent event) throws CoreException, NullDataException {
		Context ctx = new Context();
		IFile ifile = null;
		IProject project = (IProject) EclipseTools.getSelectedResource();
		ctx.setLibPath(project.getLocation() + Config.LIB_DIR);
		if (project.hasNature(JavaCore.NATURE_ID)) {
			ctx.setJavaProject(JavaCore.create(project));
		}
		for (Object o : ctx.getJavaProject().getNonJavaResources()) {
			if (IFile.class.isInstance(o)) {
				ifile = (IFile) o;
				if (ifile.getName().equalsIgnoreCase("project.pom")
						|| ifile.getFileExtension().equalsIgnoreCase("mozo")) {
					ctx.setDepsFile(ifile);
					return ctx;
				} 
			}
		}
		if (ctx.getDepsFile() == null) {
			String msg = "Error: Could not find project dependencies file.";
			MessageDialog.openError(HandlerUtil.getActiveShell(event), "MOZO", msg);
			throw new NullDataException(msg);
		}
		return null;
	}

}
