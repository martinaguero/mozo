package org.trimatek.mozo.catalog.model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "VERSION")
// TODO cambiar el order by por algo que obtenga el max(snapshot)
// TODO agregar la condición de que traiga únicamente las clases públicas
@NamedQueries({
		@NamedQuery(name = "findVersionByArtifactIdAndVersion", query = "from Version v where v.artifactId = :vaid and v.version = :vv order by v.snapshot"),
		@NamedQuery(name = "findVersionByArtifactIdAndVersionWithClasses", query = "from Version v JOIN FETCH v.classes where v.artifactId = :vaid and v.version = :vv order by v.snapshot"),
		@NamedQuery(name = "findVersionByArtifactIdAndVersionWithDependencies", query = "from Version v JOIN FETCH v.dependencies where v.artifactId = :vaid and v.version = :vv order by v.snapshot") })
public class Version extends RepoEntity {

	private String version;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumns({
			@JoinColumn(name = "product_id", referencedColumnName = "id"),
			@JoinColumn(name = "product_snapshot", referencedColumnName = "snapshot") })
	private Product product;
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "VERSION_DEPENDENCY", joinColumns = {
			@JoinColumn(name = "version_id", referencedColumnName = "id"),
			@JoinColumn(name = "version_snapshot", referencedColumnName = "snapshot") }, inverseJoinColumns = {
			@JoinColumn(name = "dependency_id", referencedColumnName = "id"),
			@JoinColumn(name = "dependency_snapshot", referencedColumnName = "snapshot") })
	private Set<Version> dependencies;
	@OneToMany(mappedBy = "version", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Class> classes;
	@Lob
	@Column(name = "jar")
	private byte[] jar;
	@Lob
	@Column(name = "jarProxy")
	private byte[] jarProxy;
	private String namespace;

	public Version() {
	}

	public Version(String artifactId, long snapshot) {
		super(snapshot);
		setArtifactId(artifactId);
	}

	public Version(String artifactId, String groupId, long snapshot,
			String url, String version, File dataSource) {
		super(snapshot);
		setArtifactId(artifactId);
		setGroupId(groupId);
		setVersion(version);
		setUrl(url);
	}

	public byte[] getJar() {
		return jar;
	}

	public void setJar(byte[] jar) {
		this.jar = jar;
	}

	public byte[] getJarProxy() {
		return jarProxy;
	}

	public void setJarProxy(byte[] jarProxy) {
		this.jarProxy = jarProxy;
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

	public boolean updateDependecy(Version currentDep, Version newDep) {
		boolean result = dependencies.remove(currentDep);
		return dependencies.add(newDep);
	}

	public Set<Class> getClasses() {
		return classes;
	}

	public void setClasses(Set<Class> classes) {
		this.classes = classes;
	}

	public void addClass(Class clazz) {
		if (classes == null) {
			classes = new HashSet<Class>();
		}
		classes.add(clazz);
	}

	public boolean contains(Class clazz) {
		if (classes != null) {
			for (Class cl : classes) {
				if (cl.getId().equals(clazz.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

}
