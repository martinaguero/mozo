package org.trimatek.mozo.dock;

import com.spotify.apollo.Environment;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.httpservice.HttpService;
import com.spotify.apollo.httpservice.LoadingException;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Middleware;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.SyncHandler;

public final class Dock {

	private static Mozo MOZO;

	public static void main(String... args) throws LoadingException {
		HttpService.boot(Dock::init, "mozo-dock", args);
	}

	static void init(Environment environment) {
		SyncHandler<Response<String>> addHandler = context -> resolve(context.request());
		environment.routingEngine().registerAutoRoute(Route.with(exceptionHandler(), "GET", "/mozo/dock", addHandler));
		if (MOZO == null) {
			MOZO = new Mozo();
		}
	}

	static Response<String> resolve(Request request) {
		return MOZO.resolve(request);
	}

	/**
	 * A generic middleware that maps uncaught exceptions to error code 418
	 */
	static <T> Middleware<SyncHandler<Response<T>>, SyncHandler<Response<T>>> exceptionMiddleware() {
		return handler -> requestContext -> {
			try {
				return handler.invoke(requestContext);
			} catch (RuntimeException e) {
				return Response.forStatus(Status.IM_A_TEAPOT);
			}
		};
	}

	/**
	 * Async version of {@link #exceptionMiddleware()}
	 */
	static <T> Middleware<SyncHandler<Response<T>>, AsyncHandler<Response<T>>> exceptionHandler() {
		return Dock.<T> exceptionMiddleware().and(Middleware::syncToAsync);
	}

}
