package org.trimatek.mozo.dock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

public class Mozo {

	public Mozo() {
	}

	public Response resolve(Request request) {
		try {
			List<String> modules = split(request.parameter("modules"));
			for (String string : modules) {
				System.out.println(string);
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