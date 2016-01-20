package org.trimatek.mozo.catalog.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "REPOSITORY")
public class Repository extends RepoEntity {

	@OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Manufacturer> manufacturers;

	public Repository() {
	}

	public Repository(String artifactId, long snapshot) {
		super(snapshot);
		setArtifactId(artifactId);
	}

	public Repository(String artifactId, long snapshot, List<Manufacturer> manufacturers) {
		super(snapshot);
		setArtifactId(artifactId);
		this.manufacturers = new HashSet<Manufacturer>(manufacturers);
		for(Manufacturer manufacturer : this.manufacturers){
			manufacturer.setRepository(this);
		}
	}

	public Set<Manufacturer> getManufacturers() {
		return manufacturers;
	}

	public void setManufacturers(Set<Manufacturer> manufacturers) {
		this.manufacturers = manufacturers;
	}

	public void addManufacturer(Manufacturer manufacturer) {
		if (manufacturers == null) {
			manufacturers = new HashSet<Manufacturer>();
		}
		manufacturers.add(manufacturer);
	}

}
