package org.trimatek.mozo.catalog.model;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@IdClass(RepoEntityPK.class)
public class RepoEntity implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	@Id
	protected long snapshot;
	protected String artifactId;
	protected String url;
	protected File dataSource;

	protected RepoEntity() {
	}

	protected RepoEntity(long snapshot) {
		setSnapshot(snapshot);
	}

	@Column(name="id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name="snapshot")
	public long getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(long snapshot) {
		this.snapshot = snapshot;
	}

	public String toString() {
		return getId() + "";
	}

	@Column(name = "artifactid")
	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "datasource")
	public File getDataSource() {
		return dataSource;
	}

	public void setDataSource(File dataSource) {
		this.dataSource = dataSource;
	}

}
