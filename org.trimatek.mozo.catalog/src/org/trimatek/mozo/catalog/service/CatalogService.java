package org.trimatek.mozo.catalog.service;

import java.io.IOException;
import java.util.Collection;

import org.trimatek.mozo.catalog.model.Group;
import org.trimatek.mozo.catalog.model.Product;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.model.Class;

public interface CatalogService {

	public void save(Repository repository);
	
	public void saveOrUpdate(Repository repository);
	
	public Collection<Repository> listAllRepositories();
	
	public Repository loadRepository();
	
	public Version loadVersion(String artifactId, String version) throws IOException;
	
	public Group loadGroup(String groupId);
	
	public Product loadProduct(String artifactId);
	
	public Version buildVersionFromPom(String path, long snapshot) throws Exception;
	
	public Class loadClass(String artifactId, String className);
	
	public long getCurrentSnapshot();
	
}
