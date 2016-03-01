package org.trimatek.mozo.navigator.tools;

import java.io.IOException;

import org.trimatek.mozo.catalog.model.Group;
import org.trimatek.mozo.catalog.model.Product;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;

public class CatalogTools {

	public static Version save(Version version, CatalogService catalogService)
			throws IOException {
		Repository repo = insertIntoRepository(version, catalogService);
		catalogService.saveOrUpdate(repo);
		return version;
	}

	public static Version saveDependency(Version dependency,
			CatalogService catalogService) throws IOException {
		dependency = insertIntoHierarchy(dependency, catalogService);
		catalogService.saveOrUpdate(dependency);
		return dependency;
	}

	private static Version insertIntoHierarchy(Version version,
			CatalogService catalogService) {
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

	private static Repository insertIntoRepository(Version version,
			CatalogService catalogService) {
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
		return repo;
	}

	public static Version loadClasses(Version version,
			CatalogService catalogService) {

		return version;
	}

}
