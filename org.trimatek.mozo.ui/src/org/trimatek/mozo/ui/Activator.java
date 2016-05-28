package org.trimatek.mozo.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.trimatek.mozo.ui.sockets.SocketServer;
import org.trimatek.mozo.ui.tools.EclipseTools;

public class Activator extends AbstractUIPlugin {

	private static Logger logger = Logger.getLogger(Activator.class.getName());

	public Activator() {
		initServerSocket();
	}

	private void initServerSocket() {
		IProject project = (IProject)EclipseTools.getSelectedResource();
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
	
}
