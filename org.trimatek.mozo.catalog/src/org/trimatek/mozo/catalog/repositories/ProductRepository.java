package org.trimatek.mozo.catalog.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.trimatek.mozo.catalog.model.Product;

public class ProductRepository {

	EntityManagerFactory entityManagerFactory;

	public ProductRepository(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public Product findProductByArtifactId(String artifactId) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();
		List<Product> results = entityManager
				.createNamedQuery("findProductByArtifactId",
						Product.class).setParameter("paid", artifactId)
				.getResultList();
		Product result = null;
		if (!results.isEmpty()) {
			result = results.get(0);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		return result;
	}

}
