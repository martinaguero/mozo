package org.trimatek.mozo.catalog.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.trimatek.mozo.catalog.model.Version;

public class VersionRepository {

	EntityManagerFactory entityManagerFactory;

	public VersionRepository(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public Version findVersionByArtifactIdAndVersion(String artifactId,
			String version) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();
		List<Version> results = entityManager
				.createNamedQuery("findVersionByArtifactIdAndVersion",
						Version.class).setParameter("vaid", artifactId)
				.setParameter("vv", version).getResultList();
		Version result = null;
		if (!results.isEmpty()) {
			result = results.get(0);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		return result;
	}

}
