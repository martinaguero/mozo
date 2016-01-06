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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "PRODUCT")
public class Product extends RepoEntity {

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "manufacturer_id", referencedColumnName = "id"),
			@JoinColumn(name = "manufacturer_snapshot", referencedColumnName = "snapshot") })
	private Manufacturer manufacturer;
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Version> versions;

	public Product() {
	}

	public Product(String artifactId, long snapshot) {
		super(snapshot);
		setArtifactId(artifactId);
	}

	public Product(String artifactId, long snapshot, List<Version> versions, File dataSource) {
		super(snapshot);
		setArtifactId(artifactId);
		this.versions = new HashSet<Version>(versions);
		setDataSource(dataSource);
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
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
