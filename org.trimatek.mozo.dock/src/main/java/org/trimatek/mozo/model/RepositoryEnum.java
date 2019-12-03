package org.trimatek.mozo.model;

public enum RepositoryEnum {
	Trimatek("http://www.trimatek.org/repository/"),
	FirstRepository("http://firstrepository.000webhostapp.com/"),
	NextRepository("https://nextrepository.000webhostapp.com/");

	public String path;

	RepositoryEnum(String path) {
		this.path = path;
	}
}