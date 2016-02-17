package org.trimatek.mozo.scanner.service;

import java.io.IOException;

import org.apache.maven.model.io.ModelParseException;
import org.trimatek.mozo.catalog.model.RepoEntity;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.RepositoryEnum;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.scanner.exception.ScannerException;

public interface ScannerService {

	public RepoEntity scan(RepositoryEnum target, long snapshot) throws IOException;
	
	public RepoEntity scan(String path, long snapshot) throws IOException;
	
	public RepoEntity scan(Repository repository, String path, CatalogService catalogService) throws IOException, ScannerException;
	
	public Version buildVersion(String pomPath, long snapshot) throws ModelParseException, IOException;
	
}
