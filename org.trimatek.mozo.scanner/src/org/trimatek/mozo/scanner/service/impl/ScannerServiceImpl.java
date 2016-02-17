package org.trimatek.mozo.scanner.service.impl;

import static org.trimatek.mozo.scanner.Config.PROXY_HOST;
import static org.trimatek.mozo.scanner.Config.PROXY_PORT;

import java.io.IOException;

import org.apache.maven.model.io.ModelParseException;
import org.trimatek.mozo.catalog.model.RepoEntity;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.RepositoryEnum;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.scanner.exception.ScannerException;
import org.trimatek.mozo.scanner.service.ScannerService;
import org.trimatek.mozo.scanner.utils.MavenUtils;

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
	public RepoEntity scan(RepositoryEnum target, long snapshot) throws IOException {
		return scanner.scan(target.path, snapshot);
	}

	@Override
	public RepoEntity scan(Repository repository, String path, CatalogService catalogService) throws IOException, ScannerException {
		if (repository.getId() == null) {
			throw new ScannerException("The repository object is not associated with a persistence context.");
		}
		return scanner.scan(repository, path, catalogService);
	}

	@Override
	public Version buildVersion(String pomPath, long snapshot) throws ModelParseException, IOException {
		return (Version) MavenUtils.processPom(pomPath, snapshot);
	}

}
