package org.trimatek.mozo.navigator.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.navigator.service.NavigatorService;
import org.trimatek.mozo.navigator.tools.BytecodeTools;
import org.trimatek.mozo.navigator.tools.CatalogTools;
import org.trimatek.mozo.navigator.utils.NavigatorUtils;

public class NavigatorServiceImpl implements NavigatorService {

	CatalogService catalogService;
	BytecodeService bytecodeService;

	public NavigatorServiceImpl(CatalogService catalogService,
			BytecodeService bytecodeService) {
		this.catalogService = catalogService;
		this.bytecodeService = bytecodeService;
	}

	@Override
	public Version loadJarProxy(Version version) throws IOException,
			ClassNotFoundException {
		Version catalogVersion = catalogService.loadVersion(
				version.getArtifactId(), version.getVersion());
		if (catalogVersion == null || catalogVersion.getJar() == null) {
			version = bytecodeService.loadJar(version);
			version = bytecodeService.buildJarProxy(version);
			version = CatalogTools.save(version, catalogService);
		} else if (catalogVersion.getJarProxy() == null) {
			version = bytecodeService.buildJarProxy(catalogVersion);
			version = CatalogTools.save(version, catalogService);
		} else {
			version = catalogVersion;
		}
		return version;
	}

	@Override
	public Version fetchDependencies(List<String> references, Version version)
			throws Exception {
		Class catalogClass;
		int count = 0;
		for (String className : references) {
			catalogClass = catalogService.loadClass(version.getArtifactId(),
					className);
			if (catalogClass != null) {
				count++;
				if (!version.contains(catalogClass)) {
					version.addClass(catalogClass);
				}
			} else {
				throw new RuntimeException("MOZO: Could not load class "
						+ className + " from catalog.");
			}
		}
		if (references.size() != count) {
			throw new RuntimeException(
					"MOZO: Required and fetched number of classes are not equal.");
		}
		List<String> selfReferences = BytecodeTools.findReferences(
				version.getClasses(), version.getGroupId(), bytecodeService);
		selfReferences = NavigatorUtils.removeRepeated(selfReferences,
				version.getClasses());
		if (!selfReferences.isEmpty()) {
			version = fetchDependencies(selfReferences, version);
		}
		return version;
	}

}
