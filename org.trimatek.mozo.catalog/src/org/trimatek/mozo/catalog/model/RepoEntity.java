package org.trimatek.mozo.catalog.model;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
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
	protected Long id;
	@Id
	protected Long snapshot;
	protected String artifactId;
	protected String url;
	protected File data;

	protected RepoEntity() {
	}

	protected RepoEntity(Long snapshot) {
		setSnapshot(snapshot);
	}

	@Column(name="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="snapshot")
	public Long getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(Long snapshot) {
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

	@Basic(fetch=FetchType.LAZY)
	@Column(name = "data")
	public File getData() {
		return data;
	}

	public void setData(File data) {
		this.data = data;
	}

}
