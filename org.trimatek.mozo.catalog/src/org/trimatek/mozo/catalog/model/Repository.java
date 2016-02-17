package org.trimatek.mozo.catalog.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "REPOSITORY")
@NamedQuery(name = "findRepositoryByIdAndSnapshot", query = "from Repository r where r.id = :rid and r.snapshot = :rsnapshot")
public class Repository extends RepoEntity {

	@OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Group> groups;

	public Repository() {
	}

	public Repository(String artifactId, long snapshot) {
		super(snapshot);
		setArtifactId(artifactId);
	}

	public Repository(String artifactId, long snapshot, String path, List<Group> manufacturers) {
		super(snapshot);
		setArtifactId(artifactId);
		setUrl(path);
		if (manufacturers != null) {
			this.groups = new HashSet<Group>(groups);
			for (Group manufacturer : this.groups) {
				manufacturer.setRepository(this);
			}
		}
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public void addGroup(Group group) {
		if (groups == null) {
			groups = new HashSet<Group>();
		}
		groups.add(group);
	}

}
