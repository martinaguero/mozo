package org.trimatek.mozo.ui.tools;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

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

}
