package org.trimatek.mozo.navigator.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.navigator.service.NavigatorService;
import org.trimatek.mozo.navigator.tools.BytecodeTools;
import org.trimatek.mozo.navigator.tools.CatalogTools;
import org.trimatek.mozo.navigator.tools.RemoteZipTools;
import org.trimatek.mozo.navigator.utils.NavigatorUtils;
import org.trimatek.remotezip.service.RemoteZipService;
import org.trimatek.remotezip.tools.RemoteZipFile;

public class NavigatorServiceImpl implements NavigatorService {

	CatalogService catalogService;
	BytecodeService bytecodeService;
	RemoteZipService remoteZipService;
	private static Logger logger = Logger.getLogger(NavigatorServiceImpl.class.getName());

	public NavigatorServiceImpl(CatalogService catalogService, BytecodeService bytecodeService,
			RemoteZipService remoteZipService) {
		this.catalogService = catalogService;
		this.bytecodeService = bytecodeService;
		this.remoteZipService = remoteZipService;
	}

	@Override
	public Version loadJarProxy(Version version) throws IOException, ClassNotFoundException {
		logger.info("Loading proxy for: " + version.getArtifactId());
		//TODO se pude evitar esta carga del catálogo
		Version catalogVersion = catalogService.loadVersionWithClasses(version.getArtifactId(), version.getVersion());
		if (catalogVersion == null || catalogVersion.getJar() == null) {
			logger.info("Building proxy for: " + version.getArtifactId());
			version = bytecodeService.loadJar(catalogVersion);
			version = bytecodeService.buildJarProxy(catalogVersion);
			// TODO estudiar si se puede dejar un thread persistiendo
			CatalogTools.save(version, catalogService);
			version = catalogService.loadVersion(version.getArtifactId(), version.getVersion());
		} else {
			version = catalogVersion;
		}
		return version;
	}

	@Override
	public Version fetchDependencies(Set<String> references, Version version) throws Exception {
		logger.info("Fetching classes for: " + version.getArtifactId());
		Version catalogDep;
		Set<Version> deps = new HashSet<Version>();
		version = doConjunction(references, version);
		Version catalogVersion = catalogService.loadVersionWithDependencies(version.getArtifactId(),
				version.getVersion());
		if (catalogVersion != null) {
			for (Version dependency : catalogVersion.getDependencies()) {
				logger.info("Loading dependency: " + dependency.getArtifactId());
				try {
					catalogDep = catalogService.loadVersionWithClasses(dependency.getArtifactId(),
							dependency.getVersion());
					if (catalogDep.getNamespace() == null) {
						catalogDep = RemoteZipTools.loadRemoteJarClasses(catalogDep, remoteZipService);
						catalogDep = CatalogTools.saveDependency(catalogDep, catalogService);
					}
					catalogDep.setJar(null);
					catalogDep.setJarProxy(null);
					Set<String> refs = BytecodeTools.findReferences(version.getClasses(), catalogDep.getNamespace(),
							bytecodeService);
					if (!refs.isEmpty()) {
						catalogDep.setClasses(new HashSet<Class>());
						deps.add(fetchDependencies(refs, catalogDep));
					}
				} catch (IOException ioe) {
					logger.log(Level.SEVERE, "Error while downloading Jar: " + ioe.getMessage(), ioe);
				} catch (ClassNotFoundException ce) {
					logger.log(Level.SEVERE, "Error while processing Jar: " + ce.getMessage(), ce);
				}
			}
		}
		version.setDependencies(deps);
		return version;
	}

	private Version doConjunction(Set<String> references, Version version) throws Exception {
		Class catalogClass;
		RemoteZipFile remoteJar = null;
		int count = 0;
		for (String className : references) {
			// TODO antes de cargar del catalogo, verificar si no es parte del
			// listado de clases de version
			catalogClass = catalogService.loadClass(version.getArtifactId(), className);
			if (catalogClass.getBytecode() == null) {
				if (remoteJar == null) {
					remoteJar = RemoteZipTools.loadRemoteJar(version, remoteZipService);
				}
				InputStream is = RemoteZipTools.getInputStream(catalogClass.getJarIndex(), remoteJar, remoteZipService);
				catalogClass.setBytecode(bytecodeService.toByteArray(is));
				logger.log(Level.INFO, "Saving bytecode of class: " + catalogClass.getClassName());
				CatalogTools.save(catalogClass, catalogService);
			}
			if (catalogClass != null) {
				count++;
				if (!version.contains(catalogClass)) {
					version.addClass(catalogClass);
				}
			} else {
				throw new RuntimeException("MOZO: Could not load class " + className + " from catalog.");
			}
		}
		if (references.size() != count) {
			throw new RuntimeException("MOZO: Required and fetched number of classes are not equal.");
		}
		Set<String> selfReferences = BytecodeTools.findReferences(version.getClasses(), version.getNamespace(),
				bytecodeService);
		selfReferences = NavigatorUtils.removeRepeated(selfReferences, version.getClasses());
		if (!selfReferences.isEmpty()) {
			version = doConjunction(selfReferences, version);
		}
		return version;
	}

}
