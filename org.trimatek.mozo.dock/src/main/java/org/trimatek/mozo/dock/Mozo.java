package org.trimatek.mozo.dock;

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
			List<Module> modules = Utils.findPaths(targets);
			Module module = new Module("user-request");
			module.setRequires(modules);
			for (Module aModule : module.getRequires()) {
				System.out.println(aModule.toString());
			}

			return Response.forPayload("OK");
		} catch (Exception e) {
			return Response.forStatus(Status.BAD_REQUEST);
		}
	}

	private List<String> split(Optional<String> modules) {
		return (List<String>) Arrays.asList(modules.get().split(","));
	}

}