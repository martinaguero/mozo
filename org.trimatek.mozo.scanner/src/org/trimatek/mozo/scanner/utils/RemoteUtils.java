package org.trimatek.mozo.scanner.utils;

import static org.trimatek.mozo.scanner.Config.CONNECTION_TIMEOUT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.jsoup.Jsoup;

public class RemoteUtils {

	public static BufferedReader read(String path) throws IOException {
		return new BufferedReader(
				new StringReader(Jsoup.connect(path).timeout(CONNECTION_TIMEOUT).get().body().text()));
	}
}
