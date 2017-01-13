package org.trimatek.mozo.dock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.trimatek.mozo.model.Module;
import org.trimatek.mozo.tools.Utils;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

public class Mozo {

	public Mozo() {
	}

	public Response resolve(Request request) {
		try {
			List<String> targets = split(request.parameter("modules"));			
			List<Module> modules = findPaths(targets);
			for (Module module : modules) {
				System.out.println(module.toString());
			}

			return Response.forPayload("OK");
		} catch (Exception e) {
			return Response.forStatus(Status.BAD_REQUEST);
		}
	}

	private List<String> split(Optional<String> modules) {
		return (List<String>) Arrays.asList(modules.get().split(","));
	}

	private List<Module> findPaths(List<String> targets) throws Exception {
		List<Module> modules = new ArrayList<Module>();
		for (String target : targets) {
			modules.add(Utils.toModule(target));
		}
		return modules;
	}

}