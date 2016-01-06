package org.trimatek.mozo.catalog.service;

import java.util.Collection;

import org.trimatek.mozo.catalog.model.Repository;

public interface CatalogService {

	public void save(Repository repository);
	
	public Collection<Repository> listAllRepositories();
	
}
