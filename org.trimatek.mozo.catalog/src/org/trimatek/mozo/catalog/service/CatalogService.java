package org.trimatek.mozo.catalog.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Group;
import org.trimatek.mozo.catalog.model.Product;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;

public interface CatalogService {

	public void save(Repository repository);

	public void saveOrUpdate(Repository repository);

	public void saveOrUpdate(Version version);

	public Collection<Repository> listAllRepositories();

	public Repository loadRepository();

	public Version loadVersionWithClasses(String artifactId, String version) throws IOException;

	public Version loadVersion(String artifactId, String version) throws IOException;

	public Group loadGroup(String groupId);

	public Product loadProduct(String artifactId);

	public Version buildVersion(Version version, int level) throws Exception;

	public Class loadClass(String artifactId, String className);

	public List<Class> loadClasses(String artifactId, long snapshot);

	public long getCurrentSnapshot();
	
	public int getDefaultLevel();

	public Version loadVersionWithDependencies(String artifactId, String version);

	public void saveOrUpdate(Class clazz);

}
