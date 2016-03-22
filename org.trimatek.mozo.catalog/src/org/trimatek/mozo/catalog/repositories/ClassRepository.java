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

	public Class findClassByArtifactIdAndClassName(String artifactId, String className) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<Class> results = entityManager.createNamedQuery("findClassByArtifactIdAndClassName", Class.class)
				.setParameter("caid", artifactId).setParameter("cn", className).getResultList();
		Class clazz = null;
		if (!results.isEmpty()) {
			clazz = results.get(0);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		return clazz;
	}

	public List<Class> findClassesByArtifactId(String artifactId, Long snapshot) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		List<Class> results = entityManager.createNamedQuery("findClassesByArtifactId", Class.class)
				.setParameter("caid", artifactId).setParameter("cs", snapshot).getResultList();
		entityManager.getTransaction().commit();
		entityManager.close();
		return results;
	}

	public void saveOrUpdate(Class clazz) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.merge(clazz);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

}
