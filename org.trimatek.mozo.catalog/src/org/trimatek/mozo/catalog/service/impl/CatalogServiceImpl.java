package org.trimatek.mozo.catalog.service.impl;

import static org.trimatek.mozo.catalog.Config.PROXY_HOST;
import static org.trimatek.mozo.catalog.Config.PROXY_PORT;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.maven.model.io.ModelParseException;
import org.trimatek.mozo.catalog.Config;
import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Group;
import org.trimatek.mozo.catalog.model.Product;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.repositories.ClassRepository;
import org.trimatek.mozo.catalog.repositories.GroupRepository;
import org.trimatek.mozo.catalog.repositories.ProductRepository;
import org.trimatek.mozo.catalog.repositories.VersionRepository;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.catalog.utils.MavenUtils;

public class CatalogServiceImpl implements CatalogService {

	private EntityManagerFactory entityManagerFactory;
	private VersionRepository versionRepository;
	private GroupRepository groupRepository;
	private ProductRepository productRepository;
	private ClassRepository classRepository;

	public CatalogServiceImpl(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		if (PROXY_HOST != null) {
			System.setProperty("https.proxyHost", PROXY_HOST);
			System.setProperty("https.proxyPort", PROXY_PORT + "");
		}
	}

	@Override
	public void save(Repository repository) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(repository);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public Collection<Repository> listAllRepositories() {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();
		List<Repository> result = entityManager.createQuery("from Repository",
				Repository.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();
		return result;
	}

	public Repository loadRepository() {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		Repository result = null;
		entityManager.getTransaction().begin();
		List<Repository> results = entityManager.createNamedQuery(
				"loadRepository", Repository.class).getResultList();
		if (!results.isEmpty()) {
			result = results.get(0);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		return result;
	}

	@Override
	public void saveOrUpdate(Repository repository) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.merge(repository);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	public VersionRepository getVersionRepository() {
		if (versionRepository == null) {
			versionRepository = new VersionRepository(entityManagerFactory);
		}
		return versionRepository;
	}

	public GroupRepository getGroupRepository() {
		if (groupRepository == null) {
			groupRepository = new GroupRepository(entityManagerFactory);
		}
		return groupRepository;
	}

	public ProductRepository getProductRepository() {
		if (productRepository == null) {
			productRepository = new ProductRepository(entityManagerFactory);
		}
		return productRepository;
	}

	public ClassRepository getClassRepository() {
		if (classRepository == null) {
			classRepository = new ClassRepository(entityManagerFactory);
		}
		return classRepository;
	}

	public Version loadVersionWithClasses(String artifactId, String version)
			throws ModelParseException, IOException {
		return getVersionRepository().findVersionWithClasses(artifactId,
				version);
	}

	public Version loadVersion(String artifactId, String version)
			throws ModelParseException, IOException {
		return getVersionRepository().findVersion(artifactId, version);
	}

	@Override
	public Version buildVersionFromPom(String path, long snapshot)
			throws Exception {
		return (Version) MavenUtils.processPom(path, snapshot);
	}

	@Override
	public Group loadGroup(String groupId) {
		return getGroupRepository().findGroupByGroupId(groupId);
	}

	@Override
	public Product loadProduct(String artifactId) {
		return getProductRepository().findProductByArtifactId(artifactId);
	}

	@Override
	public long getCurrentSnapshot() {
		return Config.CURRENT_SNAPSHOT;
	}

	@Override
	public Class loadClass(String artifactId, String className) {
		return getClassRepository().findClassByArtifactIdAndClassName(
				artifactId, className);
	}

	@Override
	public List<Class> loadClasses(String artifactId, long snapshot) {
		return getClassRepository().findClassesByArtifactId(artifactId,
				snapshot);
	}

	@Override
	public Version loadVersionWithDependencies(String artifactId, String version) {
		return getVersionRepository().findVersionWithDependencies(artifactId,
				version);
	}
}
