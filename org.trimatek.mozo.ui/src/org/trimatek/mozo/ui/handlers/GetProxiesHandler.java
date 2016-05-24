package org.trimatek.mozo.ui.handlers;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.FileTypeEnum;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.ui.Config;
import org.trimatek.mozo.ui.sockets.SocketClient;

public class GetProxiesHandler extends AbstractHandler {
	private static Logger logger = Logger.getLogger(GetProxiesHandler.class.getName());

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile file = getSelectedFile();
		String libdir = file.getProject().getLocation().toString() + Config.LIB_DIR;
		List<String> targets = null;
		try {
			targets = readTargets(file, event);
			if (targets != null) {
				sendCommands(targets, libdir);
			}
		} catch (CoreException ce) {
			logger.log(Level.SEVERE, "MOZO -> Error while reading dependencies file content: " + file.getName(), ce);
			return null;
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
					new Thread(client, "UI-command").start();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private List<String> readTargets(IFile file, ExecutionEvent event) throws CoreException {
		if (!FileTypeEnum.contains(file.getFileExtension())) {
			MessageDialog.openWarning(HandlerUtil.getActiveShell(event), "MOZO", "File extension is not POM or MOZO");
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

}
