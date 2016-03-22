package org.trimatek.mozo.catalog.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "CLASS")
@NamedQueries({
		@NamedQuery(name = "findClassByArtifactIdAndClassName", query = "from Class c where c.artifactId = :caid and c.className = :cn order by c.snapshot"),
		@NamedQuery(name = "findClassesByArtifactId", query = "from Class c where c.artifactId = :caid and c.snapshot = :cs") })
public class Class extends RepoEntity {

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "version_id", referencedColumnName = "id"),
			@JoinColumn(name = "version_snapshot", referencedColumnName = "snapshot") })
	private Version version;
	private String className;
	@Column(name = "public")
	private Boolean publicClass;
	@Lob
	@Column(name = "bytecode")
	private byte[] bytecode;
	private long jarIndex;

	public Class() {
	}

	public Class(String className, long snapshot) {
		super(snapshot);
		setClassName(className);
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Boolean getPublicClass() {
		return publicClass;
	}

	public void setPublicClass(Boolean publicClass) {
		this.publicClass = publicClass;
	}

	public byte[] getBytecode() {
		return bytecode;
	}

	public void setBytecode(byte[] bytecode) {
		this.bytecode = bytecode;
	}

	public long getJarIndex() {
		return jarIndex;
	}

	public void setJarIndex(long jarIndex) {
		this.jarIndex = jarIndex;
	}

	public String toString() {
		return getClassName();
	}

}
