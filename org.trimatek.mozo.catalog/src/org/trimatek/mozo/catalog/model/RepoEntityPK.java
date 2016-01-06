package org.trimatek.mozo.catalog.model;

import java.io.Serializable;

public class RepoEntityPK implements Serializable {

	private long id;
	private long snapshot;

	public RepoEntityPK() {
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(long snapshot) {
		this.snapshot = snapshot;
	}

	@Override
	public int hashCode() {
		return new Long(id).hashCode() + new Long(snapshot).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RepoEntityPK) {
			RepoEntityPK rePk = (RepoEntityPK) obj;
			if (!new Long(rePk.getId()).equals(getId())) {
				return false;
			}
			if (!new Long(rePk.getSnapshot()).equals(getSnapshot())) {
				return false;
			}
			return true;
		}

		return false;
	}
}
