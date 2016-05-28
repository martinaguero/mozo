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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.FileTypeEnum;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.model.exception.NullDataException;
import org.trimatek.mozo.ui.Context;
import org.trimatek.mozo.ui.sockets.SocketClient;
import org.trimatek.mozo.ui.tools.EclipseTools;

public class GetProxiesHandler extends AbstractHandler {
	private static Logger logger = Logger.getLogger(GetProxiesHandler.class.getName());
	private Context ctx;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<String> targets = null;
		try {
			ctx = EclipseTools.setupContext(event);
			targets = readTargets(ctx.getDepsFile(), event);
			if (targets != null) {
				sendCommands(targets, ctx.getLibPath());
			}
		} catch (CoreException ce) {
			logger.log(Level.SEVERE,
					"MOZO -> Error while reading dependencies file content: " + ctx.getDepsFile().getName(), ce);
			return null;
		} catch (NullDataException nde) {
			logger.log(Level.SEVERE, "MOZO -> Could not find dependencies file", nde);
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
			MessageDialog.openWarning(HandlerUtil.getActiveShell(event), "MOZO", "Error: File extension is not POM or MOZO.");
			return null;
		}
		FileTypeEnum type = FileTypeEnum.valueOf(file.getFileExtension());
		return type.getReader().read(file.getContents());
	}

}
