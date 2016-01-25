package org.trimatek.mozo.scanner.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.DefaultMetadataReader;
import org.apache.maven.artifact.repository.metadata.io.MetadataParseException;
import org.apache.maven.artifact.repository.metadata.io.MetadataReader;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.io.ModelParseException;
import org.apache.maven.model.io.ModelReader;
import org.trimatek.mozo.catalog.model.Product;
import org.trimatek.mozo.catalog.model.RepoEntity;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.scanner.Config;

public class MavenUtils {

	private static ModelReader modelReader = new DefaultModelReader();
	private static MetadataReader metaReader = new DefaultMetadataReader();
	private static Map<String, ?> params = null;

	public static RepoEntity processPom(String path, long snapshot) throws ModelParseException, IOException {
		URL url = new URL(path);
		Model model = null;
		Version version = null;
		File file = new File(path.substring(path.lastIndexOf("/") + 1));
		FileUtils.copyURLToFile(url, file);
		try {
			model = modelReader.read(file, params);
			System.out.println("POM: " + model.getArtifactId());
			version = new Version(buildId(model), snapshot, path, model.getVersion(), file);
			for (Dependency d : model.getDependencies()) {
				Version dep = new Version(buildId(d), snapshot, null, d.getVersion(), null);
				version.addDependency(dep);
			}
		} catch (Exception e) {
			return new Version(buildId(file.getName()), snapshot, path, buildVersion(file.getName()), null);
		}
		return version;
	}

	public static RepoEntity processMetadata(String path, long snapshot, List<Version> versions)
			throws MetadataParseException, IOException {
		URL url = new URL(path);
		File file = new File(path.substring(path.lastIndexOf("/") + 1));
		FileUtils.copyURLToFile(url, file);
		Metadata metadata = metaReader.read(file, params);
		System.out.println("XML: " + metadata.getArtifactId());
		return new Product(metadata.getArtifactId(), snapshot, path, versions, file);
	}

	public static String getManufacturerId(Product product) throws MetadataParseException, IOException {
		return metaReader.read(product.getDataSource(), params).getGroupId();
	}

	private static String buildId(Model model) {
		return model.getArtifactId() + "-" + model.getVersion();
	}

	private static String buildId(Dependency dependency) {
		return dependency.getArtifactId() + "-" + dependency.getVersion();
	}

	private static String buildId(String fileName) {
		return fileName.replace(Config.MVN_POM, "");
	}

	private static String buildVersion(String fileName) {
		String id = fileName.replace(Config.MVN_POM, "");
		String[] tokens = id.split("-");
		for (String token : tokens) {
			if (!StringUtils.isAlphanumeric(token)) {
				return token;
			}
		}
		return Config.PARSE_ERROR;
	}

}
