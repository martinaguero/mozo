package org.trimatek.mozo.model;

public enum RepositoryEnum {
	Trimatek("http://www.trimatek.org/repository/"),
	TheNewRepository("https://thenewrepository.000webhostapp.com/");
//	TheCentralRepository("https://repo1.maven.org/maven2/");

	public String path;

	RepositoryEnum(String path) {
		this.path = path;
	}
}