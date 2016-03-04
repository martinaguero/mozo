package org.trimatek.mozo.catalog.utils;

import static org.trimatek.mozo.catalog.Config.TCR_MAVEN2_URL;

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
import org.trimatek.mozo.catalog.service.CatalogService;

public class MavenUtils {

	private static ModelReader modelReader = new DefaultModelReader();
	private static MetadataReader metaReader = new DefaultMetadataReader();
	private static Map<String, ?> params = null;

	public static RepoEntity processPom(String path, long snapshot, CatalogService catalogService) throws ModelParseException,
			IOException {
		URL url = new URL(path);
		Model model = null;
		Version version = null;
		File file = new File(path.substring(path.lastIndexOf("/") + 1));
		FileUtils.copyURLToFile(url, file);
		model = modelReader.read(file, params);
		version = new Version(model.getArtifactId(), model.getGroupId(), snapshot, path, model.getVersion(), file);
		for (Dependency d : model.getDependencies()) {
			Version dep = catalogService.loadVersion(d.getArtifactId(), d.getVersion());
			if (dep == null) {
				dep = new Version(d.getArtifactId(), d.getGroupId(), snapshot, buildUrl(d), d.getVersion(), null);
			}
			version.addDependency(dep);
		}
		return version;
	}

	public static RepoEntity processMetadata(String path, long snapshot, List<Version> versions) throws MetadataParseException,
			IOException {
		URL url = new URL(path);
		File file = new File(path.substring(path.lastIndexOf("/") + 1));
		FileUtils.copyURLToFile(url, file);
		Metadata metadata = metaReader.read(file, params);
		System.out.println("XML: " + metadata.getArtifactId());
		return new Product(metadata.getArtifactId(), snapshot, path, versions);
	}

	public static String getGroupId(Product product) throws MetadataParseException, IOException {
		return metaReader.read(new File(product.getUrl()), params).getGroupId();
	}

	private static String buildId(String fileName) {
		return fileName.replace(".pom", "");
	}

	private static String buildUrl(Dependency dep) {
		return TCR_MAVEN2_URL + dep.getGroupId().replace(".", "/") + "/" + dep.getArtifactId() + "/" + dep.getVersion() + "/"
				+ dep.getArtifactId() + "-" + dep.getVersion() + ".pom";
	}

	private static String buildVersion(String fileName) {
		String id = fileName.replace(".pom", "");
		String[] tokens = id.split("-");
		for (String token : tokens) {
			if (!StringUtils.isAlphanumeric(token)) {
				return token;
			}
		}
		return "<null>";
	}

}
