package org.trimatek.mozo.ui.service;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.trimatek.mozo.model.service.UIService;

public class UIServiceImpl implements UIService {

	private IProject iproject;

	public UIServiceImpl(IProject iproject) {
		this.iproject = iproject;
	}

	@Override
	public void updateClasspath(String jarName) {
		Job job = new Job("Updating the classpath") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IJavaProject javaProject = JavaCore.create((IProject) iproject);
				int idx = 0;
				try {
					IClasspathEntry[] entries = new IClasspathEntry[javaProject.getRawClasspath().length + 1];
					for (IClasspathEntry entry : javaProject.getRawClasspath()) {
						entries[idx++] = entry;
					}
					entries[idx] = JavaCore.newLibraryEntry(new Path(jarName), null, null);
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
