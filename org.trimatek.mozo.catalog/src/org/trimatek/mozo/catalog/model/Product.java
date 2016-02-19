package org.trimatek.mozo.catalog.model;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "PRODUCT")
//TODO mejorar la query para que no traiga todas las instancias
@NamedQuery(name = "findProductByArtifactId", query = "from Product p where p.artifactId = :paid order by p.snapshot")
public class Product extends RepoEntity {

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "group_id", referencedColumnName = "id"),
			@JoinColumn(name = "group_snapshot", referencedColumnName = "snapshot") })
	private Group group;
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Version> versions;

	public Product() {
	}

	public Product(String artifactId, long snapshot) {
		super(snapshot);
		setArtifactId(artifactId);
	}

	public Product(String artifactId, long snapshot, String path, List<Version> versions, File data) {
		super(snapshot);
		setArtifactId(artifactId);
		setData(data);
		setUrl(path);
		this.versions = new HashSet<Version>(versions);
		for(Version version : this.versions){
			version.setProduct(this);
		}
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Set<Version> getVersions() {
		return versions;
	}

	public void setVersions(Set<Version> versions) {
		this.versions = versions;
	}

	public void addVersion(Version version) {
		if (versions == null) {
			versions = new HashSet<Version>();
		}
		versions.add(version);
	}

}
