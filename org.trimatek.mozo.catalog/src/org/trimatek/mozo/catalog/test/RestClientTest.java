package org.trimatek.mozo.catalog.test;

import static org.trimatek.mozo.catalog.Config.PROXY_HOST;
import static org.trimatek.mozo.catalog.Config.PROXY_PORT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

public class RestClientTest {

	// http://localhost:8080/RESTfulExample/json/product/get
	public static void main(String[] args) {

		try {
			URL url = new URL(
					"http://search.maven.org/solrsearch/select?q=g:\"jakarta-regexp\"+AND+a:\"jakarta-regexp\"&core=gav&rows=20&wt=json");
			//g:"jakarta-regexp" AND a:"jakarta-regexp" AND v:"1.4"
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
			HttpURLConnection conn = (HttpURLConnection) url
					.openConnection(proxy);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
