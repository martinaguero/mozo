package org.trimatek.mozo.service.impl;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.DispatcherService;
import org.trimatek.mozo.model.service.MozoService;

public class MozoServiceImpl implements MozoService {

	private DispatcherService dispatcherService;

	public MozoServiceImpl() {
	}

	public void setDispatcherService(DispatcherService dispatcherService) {
		this.dispatcherService = dispatcherService;
	}

	@Override
	public void loadJarProxy(Version version) {
		System.out.println("pide proxy");

		try {
			dispatcherService.loadJarProxy(version);
		} catch (MozoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void unsetDispatcherService(DispatcherService dispatcherService) {
		this.dispatcherService = null;
	}

}
