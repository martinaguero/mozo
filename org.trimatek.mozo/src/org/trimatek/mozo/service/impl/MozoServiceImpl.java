package org.trimatek.mozo.service.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trimatek.mozo.Config;
import org.trimatek.mozo.model.service.DispatcherService;
import org.trimatek.mozo.model.service.MozoService;
import org.trimatek.mozo.tools.SocketServer;

public class MozoServiceImpl implements MozoService {

	private DispatcherService dispatcherService;
	private static Logger logger = Logger.getLogger(MozoServiceImpl.class.getName());

	public MozoServiceImpl() {
	}

	public void setDispatcherService(DispatcherService dispatcherService) {
		this.dispatcherService = dispatcherService;
		initServerSocket();
	}

	private void initServerSocket() {
		Runnable server = new Runnable() {
			@Override
			public void run() {
				try {
					new SocketServer("localhost", Config.SOCKET_PORT, dispatcherService).startServer();
				} catch (IOException ioe) {
					String msg = "MOZO: Error in Server Socket";
					logger.log(Level.SEVERE, msg + ioe.getMessage(), ioe);
				}
			}
		};
		new Thread(server).start();
	}

	@Override
	public void unsetDispatcherService(DispatcherService dispatcherService) {
		this.dispatcherService = null;
	}

}
