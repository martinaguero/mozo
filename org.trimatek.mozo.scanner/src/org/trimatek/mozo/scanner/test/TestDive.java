package org.trimatek.mozo.scanner.test;

import java.io.IOException;

import org.trimatek.mozo.catalog.model.RepoEntity;
import org.trimatek.mozo.scanner.service.impl.ScannerServiceImpl;

public class TestDive {

	public static void main(String[] args) throws IOException {
		RepoEntity entity = new ScannerServiceImpl().scan("https://repo1.maven.org/maven2/abbot/", 10);
		System.out.println(entity);
		
	}

}
