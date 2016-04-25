package org.trimatek.mozo.service.impl;

import java.io.IOException;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.DispatcherService;
import org.trimatek.mozo.model.service.MozoService;
import org.trimatek.mozo.tools.SocketServer;

public class MozoServiceImpl implements MozoService {

	private DispatcherService dispatcherService;

	public MozoServiceImpl() {
	}

	public void setDispatcherService(DispatcherService dispatcherService) {
		this.dispatcherService = dispatcherService;
		
		Runnable server = new Runnable() {
			@Override
			public void run() {
				 try {
					new SocketServer("localhost", 8090, dispatcherService).startServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		};
	
		new Thread(server).start();
	}

	@Override
	public Version loadJarProxy(Version version) {
		System.out.println("pide proxy");
		try {
			return dispatcherService.loadJarProxy(version);
		} catch (MozoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void unsetDispatcherService(DispatcherService dispatcherService) {
		this.dispatcherService = null;
	}

}
