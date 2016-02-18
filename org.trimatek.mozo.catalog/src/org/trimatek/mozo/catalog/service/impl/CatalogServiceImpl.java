package org.trimatek.mozo.catalog.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.maven.model.io.ModelParseException;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.catalog.utils.MavenUtils;
import static org.trimatek.mozo.catalog.Config.PROXY_HOST;
import static org.trimatek.mozo.catalog.Config.PROXY_PORT;

public class CatalogServiceImpl implements CatalogService {

	private EntityManagerFactory entityManagerFactory;

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

	public Version loadVersion(String pomPath, long snapshot)
			throws ModelParseException, IOException {
		//TODO implementar acceso a caché local
		
		return (Version) MavenUtils.processPom(pomPath, snapshot);
	}

}
