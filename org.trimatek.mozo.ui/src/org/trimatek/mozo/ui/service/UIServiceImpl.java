package org.trimatek.mozo.ui.service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
				try {
					IClasspathEntry newEntry = JavaCore.newLibraryEntry(new Path(jarName), null, null);
					List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
					for (IClasspathEntry entry : javaProject.getRawClasspath()) {
						if (newEntry != null && entry.equals(newEntry)) {
							newEntry = null;
						}
						entries.add(entry);
					}
					if (newEntry != null) {
						entries.add(newEntry);
					}
					javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), monitor);
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
