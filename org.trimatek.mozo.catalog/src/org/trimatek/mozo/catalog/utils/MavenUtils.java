package org.trimatek.mozo.catalog.utils;

import static org.trimatek.mozo.catalog.Config.TCR_MAVEN2_URL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.trimatek.mozo.catalog.tools.CatalogTools;

public class MavenUtils {

	private static ModelReader modelReader = new DefaultModelReader();
	private static MetadataReader metaReader = new DefaultMetadataReader();
	private static Map<String, ?> params = null;
	private static Logger logger = Logger.getLogger(MavenUtils.class.getName());

	public static RepoEntity processPom(String path, Long snapshot, CatalogService catalogService)
			throws ModelParseException, IOException {
		// TODO ver el asunto de la ausencia de la versión
		URL url = new URL(path);
		Model model = null;
		Version version = null;
		File file = new File(path.substring(path.lastIndexOf("/") + 1));
		try {
			// TODO ver de cambiar este FileUtils que es lentísimo
			FileUtils.copyURLToFile(url, file);
			model = modelReader.read(file, params);
			String v = model.getVersion() == null ? parseVersionFromPomPath(path) : model.getVersion();
			version = catalogService.loadVersion(model.getArtifactId(), v);
			if (version == null) {
				version = new Version(model.getArtifactId(), model.getGroupId(), snapshot, path, v, file);
			}
			if (version.getDependencies() == null || version.getDependencies().size() == 0) {
				for (Dependency d : model.getDependencies()) {
					Version dep = catalogService.loadVersion(d.getArtifactId(), d.getVersion());
					if (dep == null) {
						if (d.getVersion() != null) {
							dep = new Version(d.getArtifactId(), d.getGroupId(), snapshot, buildUrl(d), d.getVersion(),
									null);
							version.addDependency(dep);
						}
					} else {
						version.addDependency(dep);
					}
				}
				if (version.getId() != null) {
					catalogService.saveOrUpdate(version);
				} else {
					CatalogTools.save(version, catalogService);
				}
				version = catalogService.loadVersion(version.getArtifactId(), version.getVersion());
			}
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Could not load POM: " + file, ioe);
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
		return new Product(metadata.getArtifactId(), snapshot, path, versions);
	}

	public static String getGroupId(Product product) throws MetadataParseException, IOException {
		return metaReader.read(new File(product.getUrl()), params).getGroupId();
	}

	private static String buildId(String fileName) {
		return fileName.replace(".pom", "");
	}

	private static String buildUrl(Dependency dep) {
		return TCR_MAVEN2_URL + dep.getGroupId().replace(".", "/") + "/" + dep.getArtifactId() + "/" + dep.getVersion()
				+ "/" + dep.getArtifactId() + "-" + dep.getVersion() + ".pom";
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

	public static boolean isValidURL(String path) {
		return !path.contains("null");
	}

	private static String parseVersionFromPomPath(String path) {
		int ini = path.lastIndexOf("-");
		int end = path.lastIndexOf(".");
		return path.substring(ini + 1, end);
	}

	public static List<String> readPomDependencies(InputStream inputStream) {
		List<String> dependencies = new ArrayList<String>();
		try {
			Model model = modelReader.read(inputStream, params);
			for (Dependency d : model.getDependencies()) {
				dependencies.add(buildUrl(d));
			}
		} catch (ModelParseException e) {
			logger.log(Level.SEVERE, "MOZO: Error while reading POM file", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "MOZO: IO Exception while reading POM file", e);
		}
		return dependencies;
	}

}
