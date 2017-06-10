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
import org.trimatek.mozo.model.Pool;
import org.trimatek.mozo.model.RepositoryEnum;
import org.trimatek.remotezip.model.RemoteZipEntry;
import org.trimatek.remotezip.service.RemoteZipService;
import org.trimatek.remotezip.tools.RemoteZipFile;

public class Utils {

	public static Module toModule(String target, Pool pool) throws Exception {
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
			if (exists(path, pool)) {
				module.setPath(path);
				break;
			}
		}
		return module;
	}

	public static boolean exists(String URLName, Pool pool) throws MalformedURLException, IOException {
		if (pool.containsPath(URLName)) {
			return true;
		}
		HttpURLConnection.setFollowRedirects(false);
		// note : you may also need
		// HttpURLConnection.setInstanceFollowRedirects(false)
		HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
		con.setRequestMethod("HEAD");
		return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	}

	public static List<Module> findPaths(List<String> targets, Pool pool) throws Exception {
		List<Module> modules = new ArrayList<Module>();
		for (String target : targets) {
			modules.add(Utils.toModule(target, pool));
		}
		return modules;
	}

	public static List<String> inspectModuleInfo(Module module, RemoteZipService remoteZip, Pool pool)
			throws IOException {
		if (pool.containsRefs(module.getModule())) {
			return pool.getReferences(module.getModule());
		}
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
			if (line.contains("requires")) {
				String words[] = line.trim().split(" ");
				if (!words[1].trim().startsWith("java.")) {
					line = line.replace("requires", "");
					line = line.replace(";", "");
					targets.add(line.trim());
				}
			}
		}
		return targets;
	}

}
