package org.trimatek.mozo.catalog.model;

public enum RepositoryEnum {
	TheCentralRepository("https://repo1.maven.org/maven2/");

	public String path;

	RepositoryEnum(String path) {
		this.path = path;
	}

}
