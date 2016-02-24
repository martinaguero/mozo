package org.trimatek.mozo.navigator.tools;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Group;
import org.trimatek.mozo.catalog.model.Product;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;

public class CatalogTools {

	public static Version save(Version version, CatalogService catalogService)
			throws IOException {
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
		catalogService.saveOrUpdate(repo);
		return catalogService.loadVersion(version.getArtifactId(),
				version.getVersion());
	}

	public static Version loadClasses(Version version,
			CatalogService catalogService) {
		
		return version;
	}

}
