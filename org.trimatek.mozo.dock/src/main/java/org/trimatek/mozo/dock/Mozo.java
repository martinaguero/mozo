package org.trimatek.mozo.dock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.trimatek.mozo.model.Module;
import org.trimatek.mozo.model.RepositoryEnum;
import org.trimatek.mozo.tools.Utils;
import org.trimatek.remotezip.model.RemoteZipEntry;
import org.trimatek.remotezip.service.RemoteZipService;
import org.trimatek.remotezip.service.impl.RemoteZipServiceImpl;
import org.trimatek.remotezip.tools.RemoteZipFile;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

public class Mozo {

	private RemoteZipService remoteZip;

	public Mozo() {
		remoteZip = new RemoteZipServiceImpl();
	}

	public Response resolve(Request request) {
		try {
			List<String> targets = split(request.parameter("modules"));
			Module module = new Module("user-request");
			module = addRequires(module, targets);

			return Response.forPayload("OK");
		} catch (Exception e) {
			return Response.forStatus(Status.BAD_REQUEST);
		}
	}

	private List<String> split(Optional<String> modules) {
		return (List<String>) Arrays.asList(modules.get().split(","));
	}
	// Provisional
	private Module addRequires(Module module, List<String> targets) throws Exception {
		List<Module> requires = null;
		String path;
		if (targets == null) {
			if (module.getPath() == null) {
				for (RepositoryEnum repository : RepositoryEnum.values()) {
					path = repository.path + module.toString() + ".jar";
					if (Utils.exists(path)) {
						module.setPath(path);
						break;
					}
				}
			} else {
				RemoteZipFile zip = remoteZip.load(module.getPath(), null);
				for (RemoteZipEntry e : zip.getEntries()) {
					if (e.getName().equals("module-info.class")) {
						InputStream inputStream = remoteZip.getEntryStream(e, zip);
						File file = new File("d:\\Temp\\" + e.getName());
						OutputStream outputStream = new FileOutputStream(file);
						IOUtils.copy(inputStream, outputStream);
						outputStream.close();
						break;
					}
				}
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec("D:\\bin\\jdk-9\\bin\\javap d:\\Temp\\module-info.class");
				BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line;
				targets = new ArrayList<String>();
				while ((line = br.readLine()) != null) {
					if (line.contains("requires") && !line.contains("java.base")) {
						line = line.replace("requires", "");
						line = line.replace(";", "");
						targets.add(line.trim());
					}
				}
			}
		}

		if (targets != null&&!targets.isEmpty()) {
			requires = Utils.findPaths(targets);

			for (Module aModule : requires) {
				System.out.println(aModule.toString());
				aModule = addRequires(aModule, null);
			}
			module.setRequires(requires);
		}
		return module;
	}

}