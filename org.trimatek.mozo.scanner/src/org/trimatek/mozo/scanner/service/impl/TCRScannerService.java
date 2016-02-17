package org.trimatek.mozo.scanner.service.impl;

import static org.trimatek.mozo.scanner.Config.MVN_POM;
import static org.trimatek.mozo.scanner.utils.MavenUtils.getGroupId;
import static org.trimatek.mozo.scanner.utils.StringUtils.getRepositoryId;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.repository.metadata.io.MetadataParseException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.io.ModelParseException;
import org.trimatek.mozo.catalog.model.Group;
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
		List<Group> manufacturers = new ArrayList<Group>();
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
				} else if (Group.class.isInstance(entity)) {
					manufacturers.add((Group) entity);
				}
			}
		}
		if (manufacturers != null && manufacturers.size() > 0) {
			return new Repository(getRepositoryId(path), snapshot, path, manufacturers);
		} else if (products != null && products.size() > 0) {
			return new Group(getGroupId(products.get(0)), snapshot, path, products);
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
						for (Group manufacturer : ((Repository) entity).getGroups()) {
							repository.addGroup(manufacturer);
							manufacturer.setRepository(repository);
						}
					} else if (Group.class.isInstance(entity)) {
						repository.addGroup((Group) entity);
						((Group) entity).setRepository(repository);
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
		Group m = new Group();
		m.setUrl(path);
		product.setGroup(m);
		m.addProduct(product);
		m.setArtifactId(getGroupId(product));
		m.setSnapshot(repository.getSnapshot());
		m.setRepository(repository);
		repository.addGroup(m);
		return repository;
	}

	private Repository setVersion(Repository repository, Version version, String path)
			throws ModelParseException, IOException {
		Model model = new DefaultModelReader().read(version.getData(), null);
		Product product = new Product();
		product.setArtifactId(model.getArtifactId());
		product.setSnapshot(repository.getSnapshot());
		version.setProduct(product);
		product.addVersion(version);
		Group group = new Group();
		group.setArtifactId(model.getArtifactId());
		group.setSnapshot(repository.getSnapshot());
		product.setGroup(group);
		group.addProduct(product);
		group.setRepository(repository);
		repository.addGroup(group);
		return repository;
	}

}
