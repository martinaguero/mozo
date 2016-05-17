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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.FileTypeEnum;
import org.trimatek.mozo.model.command.UserCommand;

public class ConvertHandler extends AbstractHandler {
	private QualifiedName path = new QualifiedName("html", "path");
	private static Logger logger = Logger.getLogger(ConvertHandler.class.getName());

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IFile file = getSelectedFile();
		getCurrentSelectedProject();
		List<String> targets = null;

		if (file == null) {
			MessageDialog.openError(HandlerUtil.getActiveShell(event), "Error", "Could not retrieve file information");
			return null;
		}
		try {
			targets = readTargets(file, event);
		} catch (CoreException ce) {
			logger.log(Level.SEVERE, "MOZO: Error while reading dependencies file content: " + file.getName(), ce);
			return null;
		}

		for (String t : targets) {
			UserCommand command = new UserCommand();
			Version version = new Version(t);
			command.setId("LoadProxy");
			command.setVersion(version);
			command.setTargetDir(file.getProject().getLocation().toString() + Config.LIB_DIR);

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

		// IWorkspaceRoot root = ResourcesPlugin.getWorkspace();

		// Shell shell = HandlerUtil.getActiveShell(event);
		// ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		// IStructuredSelection selection = (IStructuredSelection) sel;
		//
		// Object firstElement = selection.getFirstElement();
		// if (firstElement instanceof ICompilationUnit) {
		// createOutput(shell, firstElement);
		//
		// } else {
		// MessageDialog.openInformation(shell, "Info", "Please select a Java
		// source file");
		// }
		return null;
	}

	// private void createOutput(Shell shell, Object firstElement) {
	// String directory;
	// ICompilationUnit cu = (ICompilationUnit) firstElement;
	// IResource res = cu.getResource();
	// boolean newDirectory = true;
	// directory = getPersistentProperty(res, path);
	//
	// if (directory != null && directory.length() > 0) {
	// newDirectory = !(MessageDialog.openQuestion(shell, "Question", "Use the
	// previous output directory?"));
	// }
	// if (newDirectory) {
	// DirectoryDialog fileDialog = new DirectoryDialog(shell);
	// directory = fileDialog.open();
	//
	// }
	// if (directory != null && directory.length() > 0) {
	// setPersistentProperty(res, path, directory);
	// write(directory, cu);
	// }
	// }
	//
	// protected String getPersistentProperty(IResource res, QualifiedName qn) {
	// try {
	// return res.getPersistentProperty(qn);
	// } catch (CoreException e) {
	// return "";
	// }
	// }
	//
	// protected void setPersistentProperty(IResource res, QualifiedName qn,
	// String value) {
	// try {
	// res.setPersistentProperty(qn, value);
	// } catch (CoreException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private void write(String dir, ICompilationUnit cu) {
	// try {
	// cu.getCorrespondingResource().getName();
	// String test = cu.getCorrespondingResource().getName();
	// // Need
	// String[] name = test.split("\\.");
	// String htmlFile = dir + "\\" + name[0] + ".html";
	// FileWriter output = new FileWriter(htmlFile);
	// BufferedWriter writer = new BufferedWriter(output);
	// writer.write("<html>");
	// writer.write("<head>");
	// writer.write("</head>");
	// writer.write("<body>");
	// writer.write("<pre>");
	// writer.write(cu.getSource());
	// writer.write("</pre>");
	// writer.write("</body>");
	// writer.write("</html>");
	// writer.flush();
	// } catch (JavaModelException e) {
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }

	private List<String> readTargets(IFile file, ExecutionEvent event) throws CoreException {
		List<String> targets = new ArrayList<String>();
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
	    ISelectionService selectionService = 
	        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
	    ISelection selection = selectionService.getSelection();
	    if(selection instanceof IStructuredSelection) {
	        Object element = ((IStructuredSelection)selection).getFirstElement();
	        if (element instanceof IResource) {
	            project= ((IResource)element).getProject();
	        } else if (element instanceof PackageFragmentRoot) {
	            IJavaProject jProject = 
	                ((PackageFragmentRoot)element).getJavaProject();
	            project = jProject.getProject();
	        } else if (element instanceof IJavaElement) {
	            IJavaProject jProject= ((IJavaElement)element).getJavaProject();
	            project = jProject.getProject();
	        }
	    }
	    return project;
	}

}
