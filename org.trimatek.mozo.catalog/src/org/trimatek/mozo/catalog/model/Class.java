package org.trimatek.mozo.catalog.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CLASS")
public class Class extends RepoEntity {

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumns({
			@JoinColumn(name = "version_id", referencedColumnName = "id"),
			@JoinColumn(name = "version_snapshot", referencedColumnName = "snapshot") })
	private Version version;
	private String className;
	
	public Class(String className, long snapshot){
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

}
