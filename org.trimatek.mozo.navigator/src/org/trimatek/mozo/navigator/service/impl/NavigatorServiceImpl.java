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
		if (catalogVersion == null) {
			version = bytecodeService.loadJar(version);
			version = CatalogTools.save(version, catalogService);

			version = bytecodeService.buildJarProxy(version);
		} else if (catalogVersion.getData() == null) {
			version = bytecodeService.loadJar(catalogVersion);
			// persistir
			version = bytecodeService.buildJarProxy(catalogVersion);
		} else if (catalogVersion.getDataProxy() == null) {
			version = bytecodeService.buildJarProxy(catalogVersion);
		}

		return version;
	}

}
