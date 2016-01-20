package org.trimatek.mozo.scanner.service;

import java.io.IOException;

import org.trimatek.mozo.catalog.model.RepoEntity;
import org.trimatek.mozo.catalog.model.RepositoryEnum;

public interface ScannerService {

	public RepoEntity scan(RepositoryEnum target, long snapshot) throws IOException;
	
	public RepoEntity scan(String path, long snapshot) throws IOException;
	
}
