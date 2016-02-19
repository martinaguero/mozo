package org.trimatek.mozo.catalog.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.trimatek.mozo.catalog.model.Group;

public class GroupRepository {

	EntityManagerFactory entityManagerFactory;

	public GroupRepository(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public Group findGroupByGroupId(String groupId) {
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		entityManager.getTransaction().begin();
		List<Group> results = entityManager
				.createNamedQuery("findGroupByGroupId",
						Group.class).setParameter("gid", groupId)
				.getResultList();
		Group result = null;
		if (!results.isEmpty()) {
			result = results.get(0);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		return result;
	}

}
