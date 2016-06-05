package org.trimatek.mozo.model.command;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.trimatek.mozo.catalog.model.Version;

public class UserCommand implements Serializable {

	private String id;
	private Version version;
	protected Set<String> references = new HashSet<String>();
	private String targetDir;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public Set<String> getReferences() {
		return references;
	}

	public void setReferences(Set<String> references) {
		this.references = references;
	}

	public String getTargetDir() {
		return targetDir;
	}

	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}

}
