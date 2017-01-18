package org.trimatek.mozo.tools;

import static org.trimatek.mozo.dock.Config.javapath;
import static org.trimatek.mozo.dock.Config.temppath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.trimatek.mozo.model.Module;
import org.trimatek.mozo.model.RepositoryEnum;
import org.trimatek.remotezip.model.RemoteZipEntry;
import org.trimatek.remotezip.service.RemoteZipService;
import org.trimatek.remotezip.tools.RemoteZipFile;

public class Utils {

	public static Module toModule(String target) throws Exception {
		Module module = null;
		String path = null;
		if (target.contains("@")) {
			String[] splitted = target.split("@");
			module = new Module(splitted[0], splitted[1]);
		} else {
			module = new Module(target);
		}
		for (RepositoryEnum repository : RepositoryEnum.values()) {
			path = repository.path + module.toString() + ".jar";
			if (exists(path)) {
				module.setPath(path);
				break;
			}
		}
		return module;
	}

	public static boolean exists(String URLName) throws MalformedURLException, IOException {
		HttpURLConnection.setFollowRedirects(false);
		// note : you may also need
		// HttpURLConnection.setInstanceFollowRedirects(false)
		HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
		con.setRequestMethod("HEAD");
		return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	}

	public static List<Module> findPaths(List<String> targets) throws Exception {
		List<Module> modules = new ArrayList<Module>();
		for (String target : targets) {
			modules.add(Utils.toModule(target));
		}
		return modules;
	}

	public static List<String> inspectModuleInfo(Module module, RemoteZipService remoteZip) throws IOException {
		List<String> targets = new ArrayList<String>();
		RemoteZipFile zip = remoteZip.load(module.getPath(), null);
		for (RemoteZipEntry e : zip.getEntries()) {
			if (e.getName().equals("module-info.class")) {
				InputStream inputStream = remoteZip.getEntryStream(e, zip);
				File file = new File(temppath + e.getName());
				OutputStream outputStream = new FileOutputStream(file);
				IOUtils.copy(inputStream, outputStream);
				outputStream.close();
				break;
			}
		}
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(javapath + "javap " + temppath + "module-info.class");
		BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("requires") && !line.contains("java.")) {
				line = line.replace("requires", "");
				line = line.replace(";", "");
				targets.add(line.trim());
			}
		}
		return targets;
	}

}
