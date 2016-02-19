package org.trimatek.mozo.catalog.service.impl;

import static org.trimatek.mozo.catalog.Config.PROXY_HOST;
import static org.trimatek.mozo.catalog.Config.PROXY_PORT;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.maven.model.io.ModelParseException;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.repositories.VersionRepository;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.catalog.utils.MavenUtils;

public class CatalogServiceImpl implements CatalogService {

	private EntityManagerFactory entityManagerFactory;
	private VersionRepository versionRepository;

	public CatalogServiceImpl(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
		if (PROXY_HOST != null) {
			System.setProperty("https.proxyHost", PROXY_HOST);
			System.setProperty("https.proxyPort", PROXY_PORT);
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

	public Repository loadRepository(Long id, Long snapshot) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();
		Repository r = entityManager
				.createNamedQuery("findRepositoryByIdAndSnapshot",
						Repository.class).setParameter("rid", id)
				.setParameter("rsnapshot", snapshot).getSingleResult();
		entityManager.getTransaction().commit();
		entityManager.close();
		return r;
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

	public Version loadVersion(String artifactId, String version)
			throws ModelParseException, IOException {
		return getVersionRepository()
				.findVersionByArtifactIdAndVersion(artifactId, version);
	}

	@Override
	public Version buildVersionFromPom(String path, long snapshot) throws Exception {
		return (Version) MavenUtils.processPom(path, snapshot);
	}

}
