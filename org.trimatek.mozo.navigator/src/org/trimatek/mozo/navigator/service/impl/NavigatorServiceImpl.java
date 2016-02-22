package org.trimatek.mozo.navigator.service.impl;

import java.io.IOException;

import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.catalog.model.Repository;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.catalog.service.CatalogService;
import org.trimatek.mozo.navigator.service.NavigatorService;
import org.trimatek.mozo.navigator.tools.CatalogTools;

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
		} 
		return version;
	}

}
