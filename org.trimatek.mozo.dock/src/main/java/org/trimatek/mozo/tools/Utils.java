package org.trimatek.mozo.tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.trimatek.mozo.model.Module;
import org.trimatek.mozo.model.RepositoryEnum;

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

}
