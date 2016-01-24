package org.trimatek.mozo.scanner.service;

import java.io.IOException;

import org.trimatek.mozo.catalog.model.RepoEntity;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.RepositoryEnum;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.scanner.exception.ScannerException;

public interface ScannerService {

	public RepoEntity scan(RepositoryEnum target, long snapshot) throws IOException;
	
	public RepoEntity scan(String path, long snapshot) throws IOException;
	
	public RepoEntity scan(Repository repository, String path, CatalogService catalogService) throws IOException, ScannerException;
	
}
