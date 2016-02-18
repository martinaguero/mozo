package org.trimatek.mozo.catalog.service;

import java.io.IOException;
import java.util.Collection;

import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;

public interface CatalogService {

	public void save(Repository repository);
	
	public void saveOrUpdate(Repository repository);
	
	public Collection<Repository> listAllRepositories();
	
	public Repository loadRepository(Long id, Long snahpshot);
	
	public Version loadVersion(String pomPath, long snapshot) throws IOException;
	
}
