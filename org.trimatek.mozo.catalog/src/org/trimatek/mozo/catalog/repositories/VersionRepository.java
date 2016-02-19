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

	public Version findVersionByArtifactIdAndVersion(Version version) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();
		List<Version> results = entityManager
				.createNamedQuery("findVersionByArtifactIdAndVersion",
						Version.class)
				.setParameter("vaid", version.getArtifactId())
				.setParameter("vv", version.getVersion()).getResultList();
		if (!results.isEmpty()) {
			version = results.get(0);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		return version;
	}

}
