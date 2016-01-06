package org.trimatek.mozo.scanner.test;

import static org.trimatek.mozo.scanner.utils.StringUtils.getRepositoryId;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.DefaultMetadataReader;
import org.apache.maven.artifact.repository.metadata.io.MetadataParseException;
import org.apache.maven.artifact.repository.metadata.io.MetadataReader;

public class TestMaven {

	public static void main(String[] args) throws MetadataParseException,
			IOException {

		MetadataReader reader = new DefaultMetadataReader();
		Metadata md = reader.read(new File("f:\\Temp\\maven-metadata.xml"),
				new HashMap());
		
		System.out.println(md.getArtifactId());
		Versioning ver = md.getVersioning();
		for (String v : ver.getVersions()){
			System.out.println(v.toString());
		}

	}

}
