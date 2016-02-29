package org.trimatek.mozo.catalog.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Hibernate;
import org.trimatek.mozo.catalog.model.Version;

public class VersionRepository {

	EntityManagerFactory entityManagerFactory;

	public VersionRepository(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	private Version findVersion(String artifactId, String version,
			String namedQuery) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();
		List<Version> results = entityManager
				.createNamedQuery(namedQuery, Version.class)
				.setParameter("vaid", artifactId).setParameter("vv", version)
				.getResultList();
		Version result = null;
		if (!results.isEmpty()) {
			result = results.get(0);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		return result;
	}

	public Version findVersionWithDependencies(String artifactId, String version) {
		return findVersion(artifactId, version,
				"findVersionByArtifactIdAndVersionWithDependencies");
	}

	public Version findVersionWithClasses(String artifactId, String version) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();
		List<Version> results = entityManager
				.createNamedQuery("findVersionByArtifactIdAndVersion", Version.class)
				.setParameter("vaid", artifactId).setParameter("vv", version)
				.getResultList();
		Version result = null;
		if (!results.isEmpty()) {
			result = results.get(0);
			Hibernate.initialize(result.getClasses());
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		return result;
	}

	public Version findVersion(String artifactId, String version) {
		return findVersion(artifactId, version,
				"findVersionByArtifactIdAndVersion");
	}

}
