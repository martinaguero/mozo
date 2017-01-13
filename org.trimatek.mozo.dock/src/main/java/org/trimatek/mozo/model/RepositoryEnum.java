package org.trimatek.mozo.model;

public enum RepositoryEnum {
	Trimatek("http://www.trimatek.org/repository/"),
	TheCentralRepository("https://repo1.maven.org/maven2/");

	public String path;

	RepositoryEnum(String path) {
		this.path = path;
	}
}