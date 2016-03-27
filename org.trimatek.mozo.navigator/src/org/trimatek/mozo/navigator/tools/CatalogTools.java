package org.trimatek.mozo.navigator.tools;

import java.io.IOException;
import java.util.logging.Logger;

import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Group;
import org.trimatek.mozo.catalog.model.Product;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;

public class CatalogTools {

	private static Logger logger = Logger.getLogger(CatalogTools.class.getName());

	public static void save(Version version, CatalogService catalogService) throws IOException {
		catalogService.saveOrUpdate(version);
	}

	public static Version saveDependency(Version dependency, CatalogService catalogService) throws IOException {
		dependency = insertIntoHierarchy(dependency, catalogService);
		catalogService.saveOrUpdate(dependency);
		return dependency;
	}

	private static Version insertIntoHierarchy(Version version, CatalogService catalogService) {
		long snapshot = catalogService.getCurrentSnapshot();
		Repository repo = null;
		Product product = catalogService.loadProduct(version.getArtifactId());
		if (product == null) {
			Group group = catalogService.loadGroup(version.getGroupId());
			if (group == null) {
				repo = catalogService.loadRepository();
				if (repo == null) {
					repo = new Repository("The Central Repository", snapshot);
				}
				group = new Group(version.getGroupId(), snapshot);
				repo.addGroup(group);
				group.setRepository(repo);
			}
			product = new Product(version.getArtifactId(), snapshot);
			group.addProduct(product);
			product.setGroup(group);
		}
		product.addVersion(version);
		version.setProduct(product);
		return version;
	}

	public static void save(Class clazz, CatalogService catalogService) {
		catalogService.saveOrUpdate(clazz);
	}

}
