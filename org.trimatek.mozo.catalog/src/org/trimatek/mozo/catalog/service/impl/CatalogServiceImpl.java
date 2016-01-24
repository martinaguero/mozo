package org.trimatek.mozo.catalog.service.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.service.CatalogService;

public class CatalogServiceImpl implements CatalogService {

	private EntityManagerFactory entityManagerFactory;

	public CatalogServiceImpl(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public void save(Repository repository) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(repository);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public Collection<Repository> listAllRepositories() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<Repository> result = entityManager.createQuery("from Repository", Repository.class).getResultList();

		entityManager.getTransaction().commit();
		entityManager.close();
		return result;
	}

	public Repository loadRepository(Long id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		Repository r = entityManager.createNamedQuery("findRepositoryById", Repository.class).setParameter("rid", id)
				.getSingleResult();
		entityManager.getTransaction().commit();
		entityManager.close();
		return r;
	}

	@Override
	public void saveOrUpdate(Repository repository) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.merge(repository);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

}
