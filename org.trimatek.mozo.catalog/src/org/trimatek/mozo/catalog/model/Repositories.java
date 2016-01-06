package org.trimatek.mozo.catalog.model;

public enum Repositories {
	TheCentralRepository("https://repo1.maven.org/maven2/");

	public String path;

	Repositories(String path) {
		this.path = path;
	}

}
