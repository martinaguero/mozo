package org.trimatek.mozo.dock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trimatek.mozo.model.Module;
import org.trimatek.mozo.model.Pool;
import org.trimatek.mozo.model.RepositoryEnum;
import org.trimatek.mozo.tools.Utils;
import org.trimatek.remotezip.service.RemoteZipService;
import org.trimatek.remotezip.service.impl.RemoteZipServiceImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

public class Mozo {

	private static Logger logger = Logger.getLogger(Mozo.class.getName());
	private RemoteZipService remoteZip;
	private Gson gson;
	private Pool pool;

	public Mozo() {
		remoteZip = new RemoteZipServiceImpl();
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		pool = new Pool();
	}

	public Response<String> findModules(Request request) {
		try {
			List<String> targets = split(request.parameter("modules"));
			Module module = new Module();
			module.setFrom("user-request: " + request.uri());
			module = addModules(module, targets);
			return Response.forPayload(gson.toJson(module));
		} catch (Exception e) {
			return Response.forStatus(Status.BAD_REQUEST);
		}
	}

	private List<String> split(Optional<String> modules) {
		return (List<String>) Arrays.asList(modules.get().split(","));
	}

	private Module addModules(Module module, List<String> targets) throws Exception {
		List<Module> requires = null;
		String path;
		if (targets == null) {
			if (module.getPath() == null) {
				for (RepositoryEnum repository : RepositoryEnum.values()) {
					path = repository.path + module.toString() + ".jar";
					if (Utils.exists(path, pool)) {
						module.setPath(path);
						break;
					}
				}
			}
			targets = Utils.inspectModuleInfo(module, remoteZip, pool);
		}
		if (module.getModule() != null) {
			pool.put(module.getModule(), targets);
		}
		if (targets != null && !targets.isEmpty()) {
			requires = Utils.findPaths(targets, pool);
			for (Module aModule : requires) {
				logger.log(Level.INFO, "\t\t >>>>> Module Added: " + aModule.toString());
				pool.put(aModule.getModule(), aModule.getPath());
				addModules(aModule, null);
			}
			module.setRequires(requires);
		}
		return module;
	}

}