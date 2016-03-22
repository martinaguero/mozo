package org.trimatek.mozo.navigator.tools;

import java.io.IOException;
import java.io.InputStream;

import org.trimatek.mozo.bytecoder.utils.BytecodeUtils;
import org.trimatek.mozo.catalog.model.Class;
import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.remotezip.model.RemoteZipEntry;
import org.trimatek.remotezip.service.RemoteZipService;
import org.trimatek.remotezip.tools.RemoteZipFile;

public class RemoteZipTools {

	public static Version loadRemoteJarClasses(Version version, RemoteZipService remoteZipService) throws IOException {
		String entryName;
		int[] footprint = new int[20];
		int dotIdx = 0;
		int idx = 0;
		String jarPath = version.getUrl().replace(".pom", ".jar");
		RemoteZipFile remoteZip = remoteZipService.load(jarPath, null);
		for (RemoteZipEntry entry : remoteZip.getEntries()) {
			entryName = entry.getName();
			if (entryName.endsWith(".class")) {
				dotIdx = entryName.indexOf(".");
				entryName = entryName.replace("/", ".");
				entryName = entryName.substring(0, dotIdx);
				Class clazz = new Class(entryName, version.getSnapshot());
				clazz.setJarIndex(idx);
				clazz.setArtifactId(version.getArtifactId());
				clazz.setVersion(version);
				footprint = BytecodeUtils.split(entryName, footprint);
				version.addClass(clazz);
			}
			idx++;
		}
		version = BytecodeUtils.detachNamespace(footprint, version);
		return version;
	}

	public static RemoteZipFile loadRemoteJar(Version version, RemoteZipService remoteZipService) throws IOException {
		return remoteZipService.load(version.getUrl().replace(".pom", ".jar"), null);
	}

	public static InputStream getInputStream(long index, RemoteZipFile remoteZipFile,
			RemoteZipService remoteZipService) {
		return remoteZipService.getEntryStream(remoteZipFile.getEntries()[(int) index], remoteZipFile);
	}

}
