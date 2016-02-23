package org.trimatek.mozo.catalog.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.trimatek.mozo.catalog.model.Class;

public class ClassRepository {

	EntityManagerFactory entityManagerFactory;

	public ClassRepository(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public Class findClassByArtifactIdAndClassName(String artifactId,
			String className) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();
		List<Class> results = entityManager
				.createNamedQuery("findClassByArtifactIdAndClassName",
						Class.class).setParameter("caid", artifactId)
				.setParameter("cn", className).getResultList();
		Class clazz = null;
		if (!results.isEmpty()) {
			clazz = results.get(0);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		return clazz;
	}

}
