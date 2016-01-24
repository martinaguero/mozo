package org.trimatek.mozo.catalog.model;

import java.io.Serializable;

public class RepoEntityPK implements Serializable {

	private Long id;
	private Long snapshot;

	public RepoEntityPK() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(Long snapshot) {
		this.snapshot = snapshot;
	}

	@Override
	public int hashCode() {
		return id.hashCode() + snapshot.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RepoEntityPK) {
			RepoEntityPK rePk = (RepoEntityPK) obj;
			if (!rePk.getId().equals(getId())) {
				return false;
			}
			if (!rePk.getSnapshot().equals(getSnapshot())) {
				return false;
			}
			return true;
		}

		return false;
	}
}
