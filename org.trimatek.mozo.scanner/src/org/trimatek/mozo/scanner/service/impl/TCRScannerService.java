package org.trimatek.mozo.scanner.service.impl;

import static org.trimatek.mozo.scanner.Config.MVN_POM;
import static org.trimatek.mozo.scanner.utils.MavenUtils.getManufacturerId;
import static org.trimatek.mozo.scanner.utils.StringUtils.getRepositoryId;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.io.ModelParseException;
import org.trimatek.mozo.catalog.model.Manufacturer;
import org.trimatek.mozo.catalog.model.Product;
import org.trimatek.mozo.catalog.model.RepoEntity;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.scanner.utils.MavenUtils;
import org.trimatek.mozo.scanner.utils.RemoteUtils;
import org.trimatek.mozo.scanner.utils.StringUtils;

class TCRScannerService {

	public RepoEntity scan(String path, long snapshot) throws IOException {
		String line;
		String token;
		RepoEntity entity = null;
		List<Version> versions = new ArrayList<Version>();
		List<Product> products = new ArrayList<Product>();
		List<Manufacturer> manufacturers = new ArrayList<Manufacturer>();
		BufferedReader br = RemoteUtils.read(path);
		while ((line = br.readLine()) != null) {
			token = StringUtils.getMVNEntity(line);
			System.out.println("LÍNEA:" + line);
			if (token != null) {
				System.out.println("ENTIDAD:" + token);
				br.close();
				return buildEntity(path + "/" + token, snapshot, versions);
			}
			token = StringUtils.getDirectory(line);
			if (token != null) {
				System.out.println("DIRECTORIO:" + token);
				entity = scan(path + "/" + token, snapshot);
				if (Version.class.isInstance(entity)) {
					versions.add((Version) entity);
				} else if (Product.class.isInstance(entity)) {
					products.add((Product) entity);
				} else if (Manufacturer.class.isInstance(entity)) {
					manufacturers.add((Manufacturer) entity);
				}
			}
		}
		if (products != null && products.size() > 0) {
			return new Manufacturer(getManufacturerId(products.get(0)), snapshot, path, products);
		} else if (manufacturers != null && manufacturers.size() > 0) {
			return new Repository(getRepositoryId(path), snapshot, path, manufacturers);
		}
		return entity;
	}

	private RepoEntity buildEntity(String path, long snapshot, List<Version> versions)
			throws ModelParseException, IOException {
		RepoEntity entity = null;
		if (path.endsWith(MVN_POM)) {
			entity = MavenUtils.processPom(path, snapshot);
		} else {
			entity = MavenUtils.processMetadata(path, snapshot, versions);
		}
		return entity;
	}

	RepoEntity scan(Repository repository, String path, CatalogService catalogService) throws IOException {
		RepoEntity entity;
		String line;
		String token;
		BufferedReader br = RemoteUtils.read(path);
		while ((line = br.readLine()) != null) {
			System.out.println("LÍNEA:" + line);
			token = StringUtils.getDirectory(line);
			if (StringUtils.getMVNEntity(line) == null && token != null) {
				entity = scan(path + "/" + token, repository.getSnapshot());
				if (entity != null && Manufacturer.class.isInstance(entity)) {
					repository.addManufacturer((Manufacturer) entity);
					((Manufacturer)entity).setRepository(repository);
					catalogService.saveOrUpdate(repository);
					repository = catalogService.loadRepository(repository.getId());
				}
			}
		}
		return repository;
	}

}
