package org.trimatek.mozo.scanner.test;

import static org.trimatek.mozo.scanner.Config.PROXY_HOST;
import static org.trimatek.mozo.scanner.Config.PROXY_PORT;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class TestCopyFile {

	public static void main(String[] args) throws IOException {
		
		System.setProperty("https.proxyHost", PROXY_HOST);
		System.setProperty("https.proxyPort", PROXY_PORT);
		URL url = new URL("https://repo1.maven.org/maven2/HTTPClient/HTTPClient/0.3-3/maven-metadata.xml");
        File f = new File("maven-metadata.xml");
        FileUtils.copyURLToFile(url, f);
        System.out.println(f.toString());
	}

}
