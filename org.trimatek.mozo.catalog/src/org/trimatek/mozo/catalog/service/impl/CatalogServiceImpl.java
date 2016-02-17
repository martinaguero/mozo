package org.trimatek.mozo.catalog.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;

public class CatalogServiceImpl implements CatalogService {

	private EntityManagerFactory entityManagerFactory;

	public CatalogServiceImpl(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
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



}
