package org.trimatek.mozo.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;

public class Context {

	private String libPath;
	private IJavaProject javaProject;
	private IFile depsFile;

	public String getLibPath() {
		return libPath;
	}

	public void setLibPath(String libPath) {
		this.libPath = libPath;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public IFile getDepsFile() {
		return depsFile;
	}

	public void setDepsFile(IFile depsFile) {
		this.depsFile = depsFile;
	}

}
