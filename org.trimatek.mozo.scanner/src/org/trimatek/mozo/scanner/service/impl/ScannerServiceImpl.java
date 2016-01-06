package org.trimatek.mozo.scanner.service.impl;

import static org.trimatek.mozo.scanner.Config.PROXY_HOST;
import static org.trimatek.mozo.scanner.Config.PROXY_PORT;

import java.io.IOException;

import org.trimatek.mozo.catalog.model.RepoEntity;
import org.trimatek.mozo.catalog.model.Repositories;
import org.trimatek.mozo.scanner.service.ScannerService;

public class ScannerServiceImpl implements ScannerService {

	private TCRScannerService scanner;

	public ScannerServiceImpl() {
		if (PROXY_HOST != null) {
			System.setProperty("https.proxyHost", PROXY_HOST);
			System.setProperty("https.proxyPort", PROXY_PORT);
		}
		scanner = new TCRScannerService();
	}

	@Override
	public RepoEntity scan(String path, long snapshot) throws IOException {
		return scanner.scan(path, snapshot);
	}

	@Override
	public RepoEntity scan(Repositories target, long snapshot) throws IOException {
		return scanner.scan(target.path, snapshot);
	}

}
