package org.trimatek.mozo.ui.handler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.trimatek.mozo.model.service.MozoService;

public class ConvertHandler extends AbstractHandler {
	private QualifiedName path = new QualifiedName("html", "path");
	private ServiceTracker mozoServiceTracker;
	private MozoService mozoService;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			IFile file  = getSelectedFile(event);
	        if (file != null) {
	            System.out.println(file.getLocation());
	            System.out.println(file.getProjectRelativePath());
	            System.out.println(file.getContents());
	        }
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Runnable client = new Runnable() {
			@Override
			public void run() {
				try {
					new SocketClient().startClient();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		};

		new Thread(client, "client-A").start();

		// BundleContext context =
		// InternalPlatform.getDefault().getBundleContext();
		//// BundleContext context =
		// FrameworkUtil.getBundle(ConvertHandler.class).getBundleContext();
		//
		// mozoServiceTracker = new ServiceTracker(context,
		// MozoService.class.getName(), null);
		// mozoServiceTracker.open();
		// mozoService = (MozoService) mozoServiceTracker.getService();
		// Version version = new
		// Version("https://repo1.maven.org/maven2/commons-dbcp/commons-dbcp/1.4/commons-dbcp-1.4.pom");
		// mozoService.loadJarProxy(version);
		

		Shell shell = HandlerUtil.getActiveShell(event);
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		IStructuredSelection selection = (IStructuredSelection) sel;

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof ICompilationUnit) {
			createOutput(shell, firstElement);

		} else {
			MessageDialog.openInformation(shell, "Info", "Please select a Java source file");
		}
		return null;
	}

	private void createOutput(Shell shell, Object firstElement) {
		String directory;
		ICompilationUnit cu = (ICompilationUnit) firstElement;
		IResource res = cu.getResource();
		boolean newDirectory = true;
		directory = getPersistentProperty(res, path);

		if (directory != null && directory.length() > 0) {
			newDirectory = !(MessageDialog.openQuestion(shell, "Question", "Use the previous output directory?"));
		}
		if (newDirectory) {
			DirectoryDialog fileDialog = new DirectoryDialog(shell);
			directory = fileDialog.open();

		}
		if (directory != null && directory.length() > 0) {
			setPersistentProperty(res, path, directory);
			write(directory, cu);
		}
	}

	protected String getPersistentProperty(IResource res, QualifiedName qn) {
		try {
			return res.getPersistentProperty(qn);
		} catch (CoreException e) {
			return "";
		}
	}

	protected void setPersistentProperty(IResource res, QualifiedName qn, String value) {
		try {
			res.setPersistentProperty(qn, value);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void write(String dir, ICompilationUnit cu) {
		try {
			cu.getCorrespondingResource().getName();
			String test = cu.getCorrespondingResource().getName();
			// Need
			String[] name = test.split("\\.");
			String htmlFile = dir + "\\" + name[0] + ".html";
			FileWriter output = new FileWriter(htmlFile);
			BufferedWriter writer = new BufferedWriter(output);
			writer.write("<html>");
			writer.write("<head>");
			writer.write("</head>");
			writer.write("<body>");
			writer.write("<pre>");
			writer.write(cu.getSource());
			writer.write("</pre>");
			writer.write("</body>");
			writer.write("</html>");
			writer.flush();
		} catch (JavaModelException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private IFile getSelectedFile(ExecutionEvent event) throws CoreException {
		IFile file = null;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		  if (selection instanceof IStructuredSelection) {
		        IStructuredSelection ssel = (IStructuredSelection) selection;
		        Object obj = ssel.getFirstElement();
		        file = (IFile) Platform.getAdapterManager().getAdapter(obj,
		                IFile.class);
//		        if (file == null) {
//		            if (obj instanceof IAdaptable) {
//		                file = (IFile) ((IAdaptable) obj).getAdapter(IFile.class);
//		                IAdaptable ia = (IAdaptable)obj;
//		                IFile ifile = ia.getAdapter(IFile.class);
//		                System.out.println(ifile.getFullPath());
//		            }
//		        }
		    } else {
		    	Shell shell = HandlerUtil.getActiveShell(event);
		    	MessageDialog.openInformation(shell, "Info", "Please select a Maven POM file");
		    }
		  
		}
		
		
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//		if (window != null) {
//			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
//			Object firstElement = selection.getFirstElement();
//			if (firstElement instanceof IAdaptable) {
//				CompilationUnit unit =(CompilationUnit)firstElement;
//				
//				IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
//				IPath path = project.getFullPath();
//				System.out.println(path);
//			}
//		}
	return file;
	}
}
