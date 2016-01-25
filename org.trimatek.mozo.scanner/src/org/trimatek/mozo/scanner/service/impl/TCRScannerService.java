package org.trimatek.mozo.scanner.service.impl;

import static org.trimatek.mozo.scanner.Config.MVN_POM;
import static org.trimatek.mozo.scanner.utils.MavenUtils.getManufacturerId;
import static org.trimatek.mozo.scanner.utils.StringUtils.getRepositoryId;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.repository.metadata.io.MetadataParseException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelReader;
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
		if (manufacturers != null && manufacturers.size() > 0) {
			return new Repository(getRepositoryId(path), snapshot, path, manufacturers);
		} else if (products != null && products.size() > 0) {
			return new Manufacturer(getManufacturerId(products.get(0)), snapshot, path, products);
		} else if (versions != null && versions.size() > 0) {
 
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
				if (entity != null) {
					if (Repository.class.isInstance(entity)) {
						for (Manufacturer manufacturer : ((Repository) entity).getManufacturers()) {
							repository.addManufacturer(manufacturer);
							manufacturer.setRepository(repository);
						}
					} else if (Manufacturer.class.isInstance(entity)) {
						repository.addManufacturer((Manufacturer) entity);
						((Manufacturer) entity).setRepository(repository);
					} else if (Product.class.isInstance(entity)) {
						repository = setProduct(repository, (Product) entity, path);
					} else if (Version.class.isInstance(entity)) {
						repository = setVersion(repository, (Version) entity, path);
					}
				}
				catalogService.saveOrUpdate(repository);
				repository = catalogService.loadRepository(repository.getId(), repository.getSnapshot());
			}
		}
		return repository;
	}

	private Repository setProduct(Repository repository, Product product, String path)
			throws MetadataParseException, IOException {
		Manufacturer m = new Manufacturer();
		m.setUrl(path);
		product.setManufacturer(m);
		m.addProduct(product);
		m.setArtifactId(getManufacturerId(product));
		m.setSnapshot(repository.getSnapshot());
		m.setRepository(repository);
		repository.addManufacturer(m);
		return repository;
	}

	private Repository setVersion(Repository repository, Version version, String path)
			throws ModelParseException, IOException {
		Model model = new DefaultModelReader().read(version.getDataSource(), null);
		Product product = new Product();
		product.setArtifactId(model.getArtifactId());
		product.setSnapshot(repository.getSnapshot());
		version.setProduct(product);
		product.addVersion(version);
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setArtifactId(model.getArtifactId());
		manufacturer.setSnapshot(repository.getSnapshot());
		product.setManufacturer(manufacturer);
		manufacturer.addProduct(product);
		manufacturer.setRepository(repository);
		repository.addManufacturer(manufacturer);
		return repository;
	}

}
