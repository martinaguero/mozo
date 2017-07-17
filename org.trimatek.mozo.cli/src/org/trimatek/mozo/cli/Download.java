package org.trimatek.mozo.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class Download implements Command, Callable {

	private static final int BUFFER_SIZE = 3072;
	private String path;
	private String result = "Ready";

	public Object setup(String path) throws IOException {
		this.path = path;
		return result;
	}

	@Override
	public Object call() {
		try {
			result = "The file could not be downloaded";
			URL url = new URL(path);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			int responseCode = httpConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String fileName = "";
				fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
				System.out.println("Downloading: " + fileName);
				InputStream inputStream = httpConn.getInputStream();
				String saveFilePath = System.getProperty("user.dir") + File.separator + fileName;
				FileOutputStream outputStream = new FileOutputStream(saveFilePath);
				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.close();
				inputStream.close();
			}
			httpConn.disconnect();
		} catch (IOException e) {
			System.out.println(e);
		}
		return result;
	}

}
