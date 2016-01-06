package org.trimatek.mozo.catalog.model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "VERSION")
public class Version extends RepoEntity {

	private String version;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "product_id", referencedColumnName = "id"),
			@JoinColumn(name = "product_snapshot", referencedColumnName = "snapshot") })
	private Product product;
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "VERSION_DEPENDENCY", joinColumns = {
			@JoinColumn(name = "version_id", referencedColumnName = "id"),
			@JoinColumn(name = "version_snapshot", referencedColumnName = "snapshot") }, inverseJoinColumns = {
					@JoinColumn(name = "dependency_id", referencedColumnName = "id"),
					@JoinColumn(name = "dependency_snapshot", referencedColumnName = "snapshot") })
	private Set<Version> dependencies;

	public Version() {
	}

	public Version(String artifactId, long snapshot) {
		super(snapshot);
		setArtifactId(artifactId);
	}

	public Version(String artifactId, long snapshot, String version, String url, File dataSource) {
		super(snapshot);
		setArtifactId(artifactId);
		setVersion(version);
		setUrl(url);
		setDataSource(dataSource);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Set<Version> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Set<Version> dependencies) {
		this.dependencies = dependencies;
	}

	public void addDependency(Version dependency) {
		if (dependencies == null) {
			dependencies = new HashSet<Version>();
		}
		dependencies.add(dependency);
	}

}
