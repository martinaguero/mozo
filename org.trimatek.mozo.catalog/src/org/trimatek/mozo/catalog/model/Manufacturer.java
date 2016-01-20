package org.trimatek.mozo.catalog.model;

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
@Table(name = "MANUFACTURER")
public class Manufacturer extends RepoEntity {

	@OneToMany(mappedBy = "manufacturer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Product> products;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumns({ @JoinColumn(name = "repository_id", referencedColumnName = "id"),
			@JoinColumn(name = "repository_snapshot", referencedColumnName = "snapshot") })
	private Repository repository;

	public Manufacturer() {
	}

	public Manufacturer(String artifactId, long snapshot) {
		super(snapshot);
		setArtifactId(artifactId);
	}
	
	public Manufacturer(String artifactId, long snapshot, String path, List<Product> products){
		super(snapshot);
		setArtifactId(artifactId);
		setUrl(path);
		this.products = new HashSet<Product>(products);
		for(Product product : this.products){
			product.setManufacturer(this);
		}
	}

	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	public void addProduct(Product product) {
		if (products == null) {
			products = new HashSet<Product>();
		}
		products.add(product);
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

}
